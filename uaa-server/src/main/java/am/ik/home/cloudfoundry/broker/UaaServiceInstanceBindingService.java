package am.ik.home.cloudfoundry.broker;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.stereotype.Component;

import am.ik.home.app.App;
import am.ik.home.app.AppRepository;

@Component
public class UaaServiceInstanceBindingService implements ServiceInstanceBindingService {
	private final AppRepository appRepository;
	private final String appDomain;

	public UaaServiceInstanceBindingService(AppRepository appRepository,
			@Value("${vcap.application.uris[0]:example.com}") String appUrl,
			@Value("${server.context-path:}") String contextPath) {
		this.appRepository = appRepository;
		this.appDomain = appUrl + contextPath;
	}

	@Override
	public CreateServiceInstanceBindingResponse createServiceInstanceBinding(
			CreateServiceInstanceBindingRequest request) {
		String serviceInstanceId = request.getServiceInstanceId();
		App app = appRepository.findByAppId(serviceInstanceId)
				.orElseThrow(() -> new IllegalStateException(
						"the requested app is not found! (appId=" + serviceInstanceId
								+ ")"));
		Map<String, Object> credentials = new HashMap<>();
		credentials.put("client_id", app.getAppId());
		credentials.put("client_secret", app.getAppSecret());
		credentials.put("auth_domain", "https://" + appDomain);
		return new CreateServiceInstanceAppBindingResponse().withCredentials(credentials);
	}

	@Override
	public void deleteServiceInstanceBinding(
			DeleteServiceInstanceBindingRequest request) {
	}
}
