package am.ik.home.cloudfoundry.broker;

import am.ik.home.app.App;
import am.ik.home.app.AppGrantType;
import am.ik.home.app.AppRepository;
import am.ik.home.app.AppRole;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.servicebroker.model.*;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UaaServiceInstanceService implements ServiceInstanceService {
	private final AppRepository appRepository;

	@Override
	public CreateServiceInstanceResponse createServiceInstance(
			CreateServiceInstanceRequest request) {
		ArbitraryParameters parameters = new ArbitraryParameters(request.getParameters());

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

		app.setAppUrl(parameters.appUrl()
				.orElseGet(() -> "http://" + app.getAppId() + ".example.com"));
		app.setAppName(parameters.appName().orElseGet(() -> app.getAppId()));
		app.setRedirectUrls(parameters.redirectUrls()
				.map(urls -> urls.stream().collect(Collectors.toSet()))
				.orElseGet(() -> Collections.emptySet()));

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
	@Transactional
	public UpdateServiceInstanceResponse updateServiceInstance(
			UpdateServiceInstanceRequest request) {
		appRepository.findByAppId(request.getServiceInstanceId()).ifPresent(app -> {
			ArbitraryParameters parameters = new ArbitraryParameters(
					request.getParameters());
			parameters.appUrl().ifPresent(url -> app.setAppUrl(url));
			parameters.appName().ifPresent(name -> app.setAppName(name));
			parameters.redirectUrls().ifPresent(urls -> app
					.setRedirectUrls(urls.stream().collect(Collectors.toSet())));
		});
		return new UpdateServiceInstanceResponse();
	}

	static class ArbitraryParameters {
		final Map<String, Object> parameters;

		public ArbitraryParameters(Map<String, Object> parameters) {
			this.parameters = parameters == null ? Collections.emptyMap() : parameters;
		}

		Optional<String> appUrl() {
			return Optional.ofNullable(parameters.get("appUrl")).map(Object::toString);
		}

		Optional<String> appName() {
			return Optional.ofNullable(parameters.get("appName")).map(Object::toString);
		}

		@SuppressWarnings("unchecked")
		Optional<Collection<String>> redirectUrls() {
			return Optional.ofNullable(parameters.get("redirectUrls"))
					.map(Collection.class::cast);
		}
	}
}
