package am.ik.home;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConfigurationProperties(prefix = "whitelabel")
@Data
@Component
public class WhitelabelConfigProps {
	private String applicationName = "Maki UAA";
}
