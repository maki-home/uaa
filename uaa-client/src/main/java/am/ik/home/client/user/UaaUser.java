package am.ik.home.client.user;

import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.util.Base64Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UaaUser implements Serializable {
	private final String userId;
	private final String userName;
	private final String displayName;
	private final Set<String> scope;
	private final Set<String> authorities;

	public static final String ACCESS_TOKEN_MESSAGE_HEADER = "accessToken";

	public UaaUser(ObjectMapper objectMapper) {
		this(objectMapper, getTokenValueFromSecurityContext());
	}

	public UaaUser(ObjectMapper objectMapper, String jwtAccessToken) {
		String payload = jwtAccessToken.split("\\.")[1];
		try {
			JsonNode json = objectMapper.readValue(
					Base64Utils.decodeFromUrlSafeString(payload), JsonNode.class);
			this.userId = json.get("user_id").asText();
			this.userName = json.get("user_name").asText();
			this.displayName = json.get("display_name").asText();
			Set<String> scope = new LinkedHashSet<>();
			for (JsonNode node : json.get("scope")) {
				scope.add(node.asText());
			}
			this.scope = Collections.unmodifiableSet(scope);
			Set<String> authorities = new LinkedHashSet<>();
			for (JsonNode node : json.get("authorities")) {
				authorities.add(node.asText());
			}
			this.authorities = Collections.unmodifiableSet(authorities);
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public String getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Set<String> getScope() {
		return scope;
	}

	public Set<String> getAuthorities() {
		return authorities;
	}

	private static String getTokenValueFromSecurityContext() {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		OAuth2Authentication auth = OAuth2Authentication.class.cast(authentication);
		OAuth2AuthenticationDetails details = OAuth2AuthenticationDetails.class
				.cast(auth.getDetails());
		return details.getTokenValue();
	}

	@Override
	public String toString() {
		return "UaaUser{" + "userId='" + userId + '\'' + ", userName='" + userName + '\''
				+ ", displayName='" + displayName + '\'' + ", scope=" + scope
				+ ", authorities=" + authorities + '}';
	}
}
