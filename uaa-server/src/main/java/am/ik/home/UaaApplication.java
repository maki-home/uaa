package am.ik.home;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.AuthorizationServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import am.ik.home.app.AppClientDetails;
import am.ik.home.app.AppRepository;
import am.ik.home.member.Member;
import am.ik.home.member.MemberRepository;
import am.ik.home.member.MemberUserDetails;

@SpringBootApplication
public class UaaApplication {

	@Autowired
	MemberRepository memberRepository;

	public static void main(String[] args) {
		SpringApplication.run(UaaApplication.class, args);
	}

	@Bean
	UserDetailsService userDetailsService(MemberRepository memberRepository) {
		return s -> memberRepository.findByEmail(s).map(MemberUserDetails::new)
				.orElseThrow(() -> new UsernameNotFoundException("not found"));
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new Pbkdf2PasswordEncoder();
	}

	@Profile("!cloud")
	@Bean
	RequestDumperFilter requestDumperFilter() {
		return new RequestDumperFilter();
	}

	@Configuration
	static class RestMvcConfig extends RepositoryRestConfigurerAdapter {
		@Override
		public void configureRepositoryRestConfiguration(
				RepositoryRestConfiguration config) {
			config.exposeIdsFor(Member.class);
		}
	}

	@Configuration
	@EnableGlobalMethodSecurity(prePostEnabled = true)
	@Order(-20)
	static class LoginConfig extends WebSecurityConfigurerAdapter {

		@Autowired
		DataSource dataSource;
		@Autowired
		UserDetailsService userDetailsService;

		@Bean
		PersistentTokenRepository persistentTokenRepository() {
			JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
			tokenRepository.setDataSource(dataSource);
			return tokenRepository;
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.formLogin().loginPage("/login").permitAll().and().requestMatchers()
					.antMatchers("/", "/apps", "/login", "/logout", "/oauth/authorize",
							"/oauth/confirm_access")
					.and().authorizeRequests().antMatchers("/login**").permitAll()
					.antMatchers("/apps**").access("hasRole('ADMIN')").anyRequest()
					.authenticated().and().rememberMe()
					.tokenRepository(persistentTokenRepository())
					.userDetailsService(userDetailsService)
					.tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(7)).and().logout()
					.deleteCookies("JSESSIONID", "remember-me").permitAll().and().csrf()
					.ignoringAntMatchers("/oauth/**");
		}
	}

	@Configuration
	@EnableAuthorizationServer
	@EnableConfigurationProperties(AuthorizationServerProperties.class)
	static class OAuth2AuthorizationConfig extends AuthorizationServerConfigurerAdapter {
		@Autowired
		AuthenticationManager authenticationManager;
		@Autowired
		AppRepository appRepository;
		@Autowired
		TokenEnhancer tokenEnhancer;
		@Autowired
		AuthorizationServerProperties props;

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.withClientDetails(clientId -> appRepository.findByAppId(clientId)
					.map(AppClientDetails::new)
					.orElseThrow(() -> new ClientRegistrationException(
							"The given client is invalid")));
		}

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints)
				throws Exception {
			TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
			tokenEnhancerChain.setTokenEnhancers(
					Arrays.asList(tokenEnhancer, jwtAccessTokenConverter()));
			endpoints.authenticationManager(authenticationManager)
					.tokenEnhancer(tokenEnhancerChain)
					.pathMapping("/oauth/token_key", "/token_key")
					.pathMapping("/oauth/check_token", "/check_token");
		}

		@Override
		public void configure(AuthorizationServerSecurityConfigurer security)
				throws Exception {
			security.tokenKeyAccess(props.getTokenKeyAccess());
			security.checkTokenAccess(props.getCheckTokenAccess());
		}

		@Bean
		@ConfigurationProperties("jwt")
		JwtAccessTokenConverter jwtAccessTokenConverter() {
			return new JwtAccessTokenConverter();
		}
	}

	@Configuration
	@EnableResourceServer
	static class OAuth2ResourceConfig extends ResourceServerConfigurerAdapter {
		@Override
		public void configure(HttpSecurity http) throws Exception {
			http.sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
					.authorizeRequests().mvcMatchers("/userinfo")
					.access("#oauth2.hasScope('openid')")
					.antMatchers(HttpMethod.GET, "/v1/**")
					.access("#oauth2.clientHasRole('ROLE_TRUSTED_CLIENT') and #oauth2.hasScope('read')")
					.antMatchers(HttpMethod.POST, "/v1/**")
					.access("#oauth2.clientHasRole('ROLE_TRUSTED_CLIENT') and #oauth2.hasScope('write')");
		}
	}
}
