package am.ik.home;

import com.fasterxml.jackson.datatype.jsr353.JSR353Module;
import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.AuthorizationServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class UaaApplication {

    public static void main(String[] args) {
        SpringApplication.run(UaaApplication.class, args);
    }

    @Autowired
    MemberRepository memberRepository;

    @Bean
    UserDetailsService userDetailsService(MemberRepository memberRepository) {
        return s -> memberRepository.findByEmail(s)
                .map(MemberUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("not found"));
    }

    @Profile("!cloud")
    @Bean
    RequestDumperFilter requestDumperFilter() {
        return new RequestDumperFilter();
    }

    @Bean
    InitializingBean init(MemberRepository memberRepository) {
        return () -> {
            Member member = new Member();
            member.setEmail("maki@example.com");
            member.setFamilyName("Maki");
            member.setGivenName("Toshiaki");
            member.setPassword("demo");
            memberRepository.saveAndFlush(member);
        };
    }

    @Bean
    JSR353Module jsr353Module() {
        return new JSR353Module();
    }

    @Configuration
    @Order(-20)
    static class LoginConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .formLogin().loginPage("/login").permitAll()
                    .and()
                    .requestMatchers()
                    .antMatchers("/", "/login", "/oauth/authorize", "/oauth/confirm_access")
                    .and()
                    .authorizeRequests()
                    .antMatchers("/login**").permitAll()
                    .anyRequest().authenticated()
                    .and().csrf().ignoringAntMatchers("/oauth/**");
        }
    }

    @Configuration
    @EnableAuthorizationServer
    @EnableConfigurationProperties(AuthorizationServerProperties.class)
    static class OAuth2AuthorizationConfig extends AuthorizationServerConfigurerAdapter {
        @Autowired
        AuthenticationManager authenticationManager;
        @Autowired
        AuthorizationServerProperties props;

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.inMemory()
                    .withClient("acme")
                    .secret("acmesecret")
                    .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
                    .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
                    .scopes("read", "write", "trust")
                    .resourceIds("oauth2-resource")
                    .accessTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(1))
                    .autoApprove(true)
                    .and()
                    .withClient("guest")
                    .secret("guest")
                    .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
                    .authorities("ROLE_CLIENT")
                    .scopes("read")
                    .resourceIds("oauth2-resource")
                    .accessTokenValiditySeconds((int) TimeUnit.HOURS.toSeconds(1))
                    .autoApprove(true)
                    .and()
                    .withClient("3rd")
                    .secret("3rd")
                    .authorizedGrantTypes("authorization_code", "refresh_token", "implicit")
                    .authorities("ROLE_CLIENT")
                    .scopes("read")
                    .resourceIds("oauth2-resource")
                    .accessTokenValiditySeconds((int) TimeUnit.HOURS.toSeconds(1))
                    .autoApprove(true)
                    .and()
                    .withClient("checker")
                    .secret("checker")
                    .authorities("ROLE_TRUSTED_CLIENT")
                    .autoApprove(true);
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.authenticationManager(authenticationManager);
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
            security.checkTokenAccess(props.getCheckTokenAccess());
        }
    }

    @Configuration
    @EnableResourceServer
    static class OAuth2ResourceConfig extends ResourceServerConfigurerAdapter {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/api/**").access("#oauth2.clientHasRole('ROLE_TRUSTED_CLIENT') and #oauth2.hasScope('read')")
                    .antMatchers(HttpMethod.POST, "/api/**").access("#oauth2.clientHasRole('ROLE_TRUSTED_CLIENT') and #oauth2.hasScope('write')");
        }
    }
}
