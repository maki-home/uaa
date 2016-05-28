package am.ik.home.app;

import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import java.util.stream.Collectors;

public class AppClientDetails extends BaseClientDetails {

    private final App app;

    public AppClientDetails(App app) {
        super(app.getAppId(), "oauth2-resource",
                app.getScopes().stream()
                        .map(Enum::name)
                        .distinct()
                        .map(String::toLowerCase).collect(Collectors.joining(",")),
                app.getGrantTypes().stream()
                        .map(Enum::name)
                        .distinct()
                        .map(String::toLowerCase).collect(Collectors.joining(",")),
                app.getRoles().stream()
                        .map(Enum::name)
                        .map(String::toUpperCase)
                        .map(s -> "ROLE_" + s)
                        .distinct()
                        .collect(Collectors.joining(",")),
                app.getRedirectUrls().stream()
                        .distinct()
                        .map(String::toLowerCase).collect(Collectors.joining(",")));
        setClientSecret(app.getAppSecret());
        setAccessTokenValiditySeconds(app.getAccessTokenValiditySeconds());
        setRefreshTokenValiditySeconds(app.getRefreshTokenValiditySeconds());
        setAutoApproveScopes(app.getAutoApproveScopes().stream()
                .map(Enum::name)
                .map(String::toLowerCase).collect(Collectors.toSet()));
        this.app = app;
    }

    public String getAppName() {
        return app.getAppName();
    }

    public String getAppUrl() {
        return app.getAppUrl();
    }
}
