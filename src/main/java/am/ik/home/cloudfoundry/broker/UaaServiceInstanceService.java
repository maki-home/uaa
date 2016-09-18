package am.ik.home.cloudfoundry.broker;

import am.ik.home.app.App;
import am.ik.home.app.AppGrantType;
import am.ik.home.app.AppRepository;
import am.ik.home.app.AppRole;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.servicebroker.model.*;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class UaaServiceInstanceService implements ServiceInstanceService {
	private final AppRepository appRepository;

	@Override
	public CreateServiceInstanceResponse createServiceInstance(
			CreateServiceInstanceRequest request) {
		Map<String, Object> parameters = request.getParameters();

		String serviceInstanceId = request.getServiceInstanceId();
		App app = App.builder().appId(serviceInstanceId)
				.appSecret(UUID.randomUUID().toString())
				.roles(Collections.singleton(AppRole.CLIENT))
				.scopes(Collections.singleton("openid"))
				.autoApproveScopes(Collections.singleton("openid"))
				.grantTypes(new HashSet<AppGrantType>() {
					{
						add(AppGrantType.AUTHORIZATION_CODE);
						add(AppGrantType.PASSWORD);
					}
				}).accessTokenValiditySeconds((int) TimeUnit.HOURS.toSeconds(3))
				.refreshTokenValiditySeconds(0).build();

		if (parameters != null && !StringUtils.isEmpty(parameters.get("appUrl"))) {
			app.setAppUrl((String) parameters.get("appUrl"));
		}
		else {
			app.setAppUrl("http://" + app.getAppId() + ".example.com");
		}
		if (parameters != null && !StringUtils.isEmpty(parameters.get("appName"))) {
			app.setAppName((String) parameters.get("appName"));
		}
		else {
			app.setAppName(app.getAppId());
		}
		if (parameters != null && parameters.get("redirectUrls") instanceof Collection
				&& !CollectionUtils
						.isEmpty((Collection) parameters.get("redirectUrls"))) {
			Set<String> urls = new HashSet<>();
			for (Object url : Collection.class.cast(parameters.get("redirectUrls"))) {
				urls.add((String) url);
			}
			app.setRedirectUrls(urls);
		}
		else {
			app.setRedirectUrls(Collections.emptySet());
		}
		appRepository.save(app);
		return new CreateServiceInstanceResponse();
	}

	@Override
	public GetLastServiceOperationResponse getLastOperation(
			GetLastServiceOperationRequest request) {
		return new GetLastServiceOperationResponse();
	}

	@Override
	public DeleteServiceInstanceResponse deleteServiceInstance(
			DeleteServiceInstanceRequest request) {
		String serviceInstanceId = request.getServiceInstanceId();
		appRepository.delete(serviceInstanceId);
		return new DeleteServiceInstanceResponse();
	}

	@Override

	public UpdateServiceInstanceResponse updateServiceInstance(
			UpdateServiceInstanceRequest request) {
		return new UpdateServiceInstanceResponse();
	}
}
