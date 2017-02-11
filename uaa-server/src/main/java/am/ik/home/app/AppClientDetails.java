package am.ik.home.app;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.util.CollectionUtils;

public class AppClientDetails extends BaseClientDetails {

	private final App app;

	public AppClientDetails(App app) {
		super(app.getAppId(),
				CollectionUtils.isEmpty(app.getResourceIds()) ? App.DEFAULT_RESOURCE_ID
						: app.getResourceIds().stream().collect(joining(",")),
				app.getScopes().stream().distinct().map(String::toLowerCase)
						.collect(joining(",")),
				app.getGrantTypes().stream().map(Enum::name).distinct()
						.map(String::toLowerCase).collect(joining(",")),
				app.getRoles().stream().map(Enum::name).map(String::toUpperCase)
						.map(s -> "ROLE_" + s).distinct().collect(joining(",")),
				app.getRedirectUrls().stream().distinct().map(String::toLowerCase)
						.collect(joining(",")));
		setClientSecret(app.getAppSecret());
		setAccessTokenValiditySeconds(app.getAccessTokenValiditySeconds());
		setRefreshTokenValiditySeconds(app.getRefreshTokenValiditySeconds());
		setAutoApproveScopes(app.getAutoApproveScopes().stream().map(String::toLowerCase)
				.collect(toSet()));
		this.app = app;
	}

	public String getAppName() {
		return app.getAppName();
	}

	public String getAppUrl() {
		return app.getAppUrl();
	}
}
