package am.ik.home.cloudfoundry;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("cloud")
@Configuration
public class CloudConfig extends AbstractCloudConfig {

	@ConfigurationProperties(prefix = "spring.datasource.tomcat")
	@Bean
	DataSource dataSource() {
		return connectionFactory().dataSource();
	}
}
