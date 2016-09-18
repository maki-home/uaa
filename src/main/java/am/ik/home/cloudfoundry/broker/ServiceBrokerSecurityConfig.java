package am.ik.home.cloudfoundry.broker;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@EnableWebSecurity
@RequiredArgsConstructor
@Order(-15)
public class ServiceBrokerSecurityConfig extends WebSecurityConfigurerAdapter {
	private final SecurityProperties security;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.antMatcher("/v2/**").authorizeRequests()
				.antMatchers("/v2/catalog", "/v2/service_instances/**").authenticated()
				.and().httpBasic().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf()
				.disable();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser(security.getUser().getName())
				.password(security.getUser().getPassword())
				.roles(security.getUser().getRole().toArray(new String[0]));
	}
}
