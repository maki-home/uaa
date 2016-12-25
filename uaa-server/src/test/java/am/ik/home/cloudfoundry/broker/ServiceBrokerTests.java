package am.ik.home.cloudfoundry.broker;

import am.ik.home.app.App;
import am.ik.home.app.AppRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
		"spring.datasource.url=jdbc:h2:mem:test;DB_CLOSE_ON_EXIT=FALSE",
		"security.user.name=foo", "security.user.password=bar",
		"vcap.application.uris[0]=maki-uaa.example.com" })
public class ServiceBrokerTests {
	@Value("${SERVER_URI:http://localhost:${local.server.port}}/uaa")
	String uri;
	TestRestTemplate restTemplate = new TestRestTemplate("foo", "bar");
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	AppRepository appRepository;

	@Test
	public void catalogUnauthorized() {
		ResponseEntity<Void> entity = new TestRestTemplate()
				.getForEntity(uri + "/v2/catalog", Void.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void catalogAuthorized() throws Exception {
		ResponseEntity<Catalog> entity = restTemplate.getForEntity(uri + "/v2/catalog",
				Catalog.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		try (InputStream stream = ServiceBrokerSecurityConfig.class
				.getResourceAsStream("/catalog.json")) {
			Catalog expected = objectMapper.readValue(stream, Catalog.class);
			assertThat(entity.getBody()).isEqualTo(expected);
		}
	}

	@Test
	public void createServiceUnauthorized() throws Exception {
		String serviceInstanceId = UUID.randomUUID().toString();
		RequestEntity<CreateServiceInstanceRequest> req = RequestEntity
				.put(UriComponentsBuilder.fromHttpUrl(uri)
						.pathSegment("v2", "service_instances", serviceInstanceId).build()
						.toUri())
				.body(new CreateServiceInstanceRequest(
						"7c1d35ca-b696-4e74-a9b9-4a45aab66e6d",
						"e4518390-ab55-411c-b11c-55c31f25db90",
						"00000000-0000-0000-0000-000000000000",
						"00000000-0000-0000-0000-000000000000", Collections.emptyMap()));
		ResponseEntity<Void> entity = new TestRestTemplate().exchange(req, Void.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void createServiceAndDeleteServiceAuthorized() throws Exception {
		String serviceInstanceId = UUID.randomUUID().toString();
		RequestEntity<CreateServiceInstanceRequest> req = RequestEntity
				.put(UriComponentsBuilder.fromHttpUrl(uri)
						.pathSegment("v2", "service_instances", serviceInstanceId).build()
						.toUri())
				.body(new CreateServiceInstanceRequest(
						"7c1d35ca-b696-4e74-a9b9-4a45aab66e6d",
						"e4518390-ab55-411c-b11c-55c31f25db90",
						"00000000-0000-0000-0000-000000000000",
						"00000000-0000-0000-0000-000000000000", Collections.emptyMap()));
		ResponseEntity<JsonNode> entity = restTemplate.exchange(req, JsonNode.class);
		assertThat(entity.getBody().get("async").asBoolean()).isEqualTo(false);
		assertThat(entity.getBody().get("dashboard_url").isNull()).isEqualTo(true);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		App app = appRepository.findOne(serviceInstanceId);
		assertThat(app).isNotNull();
		assertThat(app.getAppName()).isEqualTo(serviceInstanceId);
		assertThat(app.getRedirectUrls()).isEmpty();

		RequestEntity<Void> req2 = RequestEntity
				.delete(UriComponentsBuilder.fromHttpUrl(uri)
						.pathSegment("v2", "service_instances", serviceInstanceId)
						.queryParam("service_id", "7c1d35ca-b696-4e74-a9b9-4a45aab66e6d")
						.queryParam("plan_id", "e4518390-ab55-411c-b11c-55c31f25db90")
						.build().toUri())
				.build();
		ResponseEntity<JsonNode> entity2 = restTemplate.exchange(req2, JsonNode.class);
		assertThat(entity2.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(appRepository.findOne(serviceInstanceId)).isNull();
	}

	@Test
	public void createServiceAuthorizedWithParameters() throws Exception {
		String serviceInstanceId = UUID.randomUUID().toString();
		RequestEntity<CreateServiceInstanceRequest> req = RequestEntity
				.put(UriComponentsBuilder.fromHttpUrl(uri)
						.pathSegment("v2", "service_instances", serviceInstanceId).build()
						.toUri())
				.body(new CreateServiceInstanceRequest(
						"7c1d35ca-b696-4e74-a9b9-4a45aab66e6d",
						"e4518390-ab55-411c-b11c-55c31f25db90",
						"00000000-0000-0000-0000-000000000000",
						"00000000-0000-0000-0000-000000000000",
						new HashMap<String, Object>() {
							{
								put("appName", "My App");
								put("appUrl", "https://myapp.example.com");
								put("redirectUrls", asList("https://myapp.example.com",
										"https://myapp.example.com/login"));
							}
						}));
		ResponseEntity<JsonNode> entity = restTemplate.exchange(req, JsonNode.class);
		assertThat(entity.getBody().get("async").asBoolean()).isEqualTo(false);
		assertThat(entity.getBody().get("dashboard_url").isNull()).isEqualTo(true);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		App app = appRepository.findOne(serviceInstanceId);
		assertThat(app).isNotNull();
		assertThat(app.getAppName()).isEqualTo("My App");
		assertThat(app.getAppUrl()).isEqualTo("https://myapp.example.com");
		assertThat(app.getRedirectUrls()).contains("https://myapp.example.com",
				"https://myapp.example.com/login");
	}

	@Test
	public void createServiceAndUpdateServiceAuthorized() throws Exception {
		String serviceInstanceId = UUID.randomUUID().toString();
		RequestEntity<CreateServiceInstanceRequest> req = RequestEntity
				.put(UriComponentsBuilder.fromHttpUrl(uri)
						.pathSegment("v2", "service_instances", serviceInstanceId).build()
						.toUri())
				.body(new CreateServiceInstanceRequest(
						"7c1d35ca-b696-4e74-a9b9-4a45aab66e6d",
						"e4518390-ab55-411c-b11c-55c31f25db90",
						"00000000-0000-0000-0000-000000000000",
						"00000000-0000-0000-0000-000000000000", Collections.emptyMap()));
		ResponseEntity<JsonNode> entity = restTemplate.exchange(req, JsonNode.class);
		assertThat(entity.getBody().get("async").asBoolean()).isEqualTo(false);
		assertThat(entity.getBody().get("dashboard_url").isNull()).isEqualTo(true);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		App app = appRepository.findOne(serviceInstanceId);
		assertThat(app).isNotNull();
		assertThat(app.getAppName()).isEqualTo(serviceInstanceId);
		assertThat(app.getRedirectUrls()).isEmpty();

		RequestEntity<UpdateServiceInstanceRequest> req2 = RequestEntity
				.post(UriComponentsBuilder.fromHttpUrl(uri)
						.queryParam("_method", "PATCH")
						.pathSegment("v2", "service_instances", serviceInstanceId).build()
						.toUri())
				.body(new UpdateServiceInstanceRequest(
						"7c1d35ca-b696-4e74-a9b9-4a45aab66e6d",
						"e4518390-ab55-411c-b11c-55c31f25db90",
						new HashMap<String, Object>() {
							{
								put("appName", "My App 2");
								put("appUrl", "https://myapp2.example.com");
								put("redirectUrls", asList("https://myapp2.example.com",
										"https://myapp2.example.com/login"));
							}
						}));
		ResponseEntity<JsonNode> entity2 = restTemplate.exchange(req2, JsonNode.class);
		assertThat(entity2.getStatusCode()).isEqualTo(HttpStatus.OK);
		app = appRepository.findOne(serviceInstanceId);
		assertThat(app).isNotNull();
		assertThat(app.getAppName()).isEqualTo("My App 2");
		assertThat(app.getAppUrl()).isEqualTo("https://myapp2.example.com");
		assertThat(app.getRedirectUrls()).contains("https://myapp2.example.com",
				"https://myapp2.example.com/login");
	}

	@Test
	public void bindServiceUnauthorized() throws Exception {
		String serviceInstanceId = UUID.randomUUID().toString();
		String bindingId = UUID.randomUUID().toString();
		RequestEntity<CreateServiceInstanceBindingRequest> req2 = RequestEntity
				.put(UriComponentsBuilder.fromHttpUrl(uri)
						.pathSegment("v2", "service_instances", serviceInstanceId,
								"service_bindings", bindingId)
						.build().toUri())
				.body(new CreateServiceInstanceBindingRequest(
						"7c1d35ca-b696-4e74-a9b9-4a45aab66e6d",
						"e4518390-ab55-411c-b11c-55c31f25db90",
						"00000000-0000-0000-0000-000000000000",
						Collections.singletonMap("app_guid",
								"00000000-0000-0000-0000-000000000000"),
						Collections.emptyMap()));
		ResponseEntity<Void> entity2 = new TestRestTemplate().exchange(req2, Void.class);
		assertThat(entity2.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void bindServiceAuthorized() throws Exception {
		String serviceInstanceId = UUID.randomUUID().toString();
		RequestEntity<CreateServiceInstanceRequest> req = RequestEntity
				.put(UriComponentsBuilder.fromHttpUrl(uri)
						.pathSegment("v2", "service_instances", serviceInstanceId).build()
						.toUri())
				.body(new CreateServiceInstanceRequest(
						"7c1d35ca-b696-4e74-a9b9-4a45aab66e6d",
						"e4518390-ab55-411c-b11c-55c31f25db90",
						"00000000-0000-0000-0000-000000000000",
						"00000000-0000-0000-0000-000000000000", Collections.emptyMap()));
		ResponseEntity<JsonNode> entity = restTemplate.exchange(req, JsonNode.class);
		assertThat(entity.getBody().get("async").asBoolean()).isEqualTo(false);
		assertThat(entity.getBody().get("dashboard_url").isNull()).isEqualTo(true);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		String bindingId = UUID.randomUUID().toString();
		RequestEntity<CreateServiceInstanceBindingRequest> req2 = RequestEntity
				.put(UriComponentsBuilder.fromHttpUrl(uri)
						.pathSegment("v2", "service_instances", serviceInstanceId,
								"service_bindings", bindingId)
						.build().toUri())
				.body(new CreateServiceInstanceBindingRequest(
						"7c1d35ca-b696-4e74-a9b9-4a45aab66e6d",
						"e4518390-ab55-411c-b11c-55c31f25db90",
						"00000000-0000-0000-0000-000000000000",
						Collections.singletonMap("app_guid",
								"00000000-0000-0000-0000-000000000000"),
						Collections.emptyMap()));

		ResponseEntity<JsonNode> entity2 = restTemplate.exchange(req2, JsonNode.class);
		JsonNode credentials = entity2.getBody().get("credentials");
		App app = appRepository.findOne(serviceInstanceId);
		assertThat(credentials.get("auth_domain").asText())
				.isEqualTo("https://maki-uaa.example.com/uaa");
		assertThat(credentials.get("client_id").asText()).isEqualTo(app.getAppId());
		assertThat(credentials.get("client_secret").asText())
				.isEqualTo(app.getAppSecret());
		assertThat(entity2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}
}
