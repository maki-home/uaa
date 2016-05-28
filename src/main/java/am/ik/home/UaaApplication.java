package am.ik.home;

import am.ik.home.member.Member;
import am.ik.home.member.MemberRepository;
import am.ik.home.member.MemberRole;
import am.ik.home.member.MemberUserDetails;
import com.fasterxml.jackson.datatype.jsr353.JSR353Module;
import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.beans.factory.InitializingBean;
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
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.Arrays;
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

    @Bean
    PasswordEncoder passwordEncoder() {
        return new Pbkdf2PasswordEncoder();
    }

    @Profile("!cloud")
    @Bean
    RequestDumperFilter requestDumperFilter() {
        return new RequestDumperFilter();
    }

    @Profile("!cloud")
    @Bean
    InitializingBean init(MemberRepository memberRepository) {
        return () -> {
            if (!memberRepository.findByEmail("maki@example.com").isPresent()) {
                Member member = new Member();
                member.setEmail("maki@example.com");
                member.setFamilyName("Maki");
                member.setGivenName("Toshiaki");
                member.setPassword(passwordEncoder().encode("demo"));
                member.setRoles(Arrays.asList(MemberRole.USER, MemberRole.ADMIN));
                memberRepository.save(member);
            }

            System.out.println(memberRepository.countByRoles(MemberRole.ADMIN));
        };
    }

    @Bean
    JSR353Module jsr353Module() {
        return new JSR353Module();
    }


    @Configuration
    static class RestMvcConfig extends RepositoryRestConfigurerAdapter {
        @Override
        public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
            config.exposeIdsFor(Member.class);
        }
    }

    @Configuration
    @EnableGlobalMethodSecurity(prePostEnabled = true)
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
        TokenEnhancer tokenEnhancer;
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
            TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
            tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer, jwtAccessTokenConverter()));
            endpoints.authenticationManager(authenticationManager)
                    .tokenEnhancer(tokenEnhancerChain);
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
            security.tokenKeyAccess(props.getTokenKeyAccess());
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
            http
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/api/**").access("#oauth2.clientHasRole('ROLE_TRUSTED_CLIENT') and #oauth2.hasScope('read')")
                    .antMatchers(HttpMethod.POST, "/api/**").access("#oauth2.clientHasRole('ROLE_TRUSTED_CLIENT') and #oauth2.hasScope('write')");
        }
    }
}
