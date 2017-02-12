package am.ik.home;

import static am.ik.home.app.AppGrantType.AUTHORIZATION_CODE;
import static am.ik.home.app.AppGrantType.PASSWORD;
import static am.ik.home.app.AppRole.CLIENT;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.http.RequestEntity.get;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.JsonNode;

import am.ik.home.app.App;
import am.ik.home.app.AppRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
		"spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:h2:mem:test;DB_CLOSE_ON_EXIT=FALSE}" })
public class LoginTests {
	@Value("${SERVER_URI:http://localhost:${local.server.port}}/uaa")
	String uri;
	TestRestTemplate restTemplate = new TestRestTemplate();
	@Autowired
	AppRepository appRepository;

	@Test
	public void healthCheckWithActuatorRole() {
		Login login = login("maki@example.com", "demo");
		RequestEntity<?> req3 = get(
				fromUriString(uri).pathSegment("/admin/health").build().toUri())
						.header("Cookie", format("JSESSIONID=%s;", login.jsessionId))
						.build();
		ResponseEntity<JsonNode> res3 = restTemplate.exchange(req3, JsonNode.class);
		assertThat(res3.getBody().has("db")).isTrue();
		logout(login);
	}

	@Test
	public void healthCheckWithoutActuatorRole() {
		Login login = login("demo@example.com", "demo");
		RequestEntity<?> req3 = get(
				fromUriString(uri).pathSegment("/admin/health").build().toUri())
						.header("Cookie", format("JSESSIONID=%s;", login.jsessionId))
						.build();
		ResponseEntity<JsonNode> res3 = restTemplate.exchange(req3, JsonNode.class);
		assertThat(res3.getBody().has("db")).isFalse();
		logout(login);
	}

	@Test
	public void createNewApp() {
		Login login = login("maki@example.com", "demo");
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("_csrf", login.csrf);
		params.add("appName", "Message");
		params.add("appUrl", "https://msg.example.com");
		params.add("roles", "CLIENT");
		params.add("grantTypes", "PASSWORD");
		params.add("grantTypes", "AUTHORIZATION_CODE");
		params.add("resourceIds", "oauth2-resource");
		params.add("scopes", "msg.read,msg.write");
		params.add("redirectUrls", "https://msg.example.com/login");
		params.add("accessTokenValiditySeconds", "10800");
		params.add("refreshTokenValiditySeconds", "43200");
		params.add("autoApproveScopes", "");

		RequestEntity<MultiValueMap<String, String>> req2 = post(fromUriString(uri)
				.pathSegment("apps").queryParam("new", "").build().toUri())
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.accept(MediaType.TEXT_HTML)
						.header("Cookie", format("JSESSIONID=%s;", login.jsessionId))
						.body(params);
		ResponseEntity<String> res2 = restTemplate.exchange(req2, String.class);
		assertThat(res2.getStatusCode()).isEqualTo(HttpStatus.FOUND);

		Optional<App> app = appRepository.findByAppName("Message");
		assertThat(app.isPresent()).isEqualTo(true);

		App a = app.get();
		assertThat(a.getAppName()).isEqualTo("Message");
		assertThat(a.getAppUrl()).isEqualTo("https://msg.example.com");
		assertThat(a.getRoles()).containsAll(asList(CLIENT));
		assertThat(a.getGrantTypes()).containsAll(asList(PASSWORD, AUTHORIZATION_CODE));
		assertThat(a.getResourceIds()).containsAll(asList("oauth2-resource"));
		assertThat(a.getScopes()).containsAll(asList("msg.read", "msg.write"));
		assertThat(a.getRedirectUrls())
				.containsAll(asList("https://msg.example.com/login"));
		assertThat(a.getAccessTokenValiditySeconds()).isEqualTo(10800);
		assertThat(a.getRefreshTokenValiditySeconds()).isEqualTo(43200);
		assertThat(a.getAutoApproveScopes()).isEmpty();

		appRepository.delete(a);
		logout(login);
	}

	@Test
	public void createNewAppWithAnotherResourceId() {
		Login login = login("maki@example.com", "demo");
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("_csrf", login.csrf);
		params.add("appName", "Message");
		params.add("appUrl", "https://msg.example.com");
		params.add("roles", "CLIENT");
		params.add("grantTypes", "PASSWORD");
		params.add("grantTypes", "AUTHORIZATION_CODE");
		params.add("resourceIds", "msg");
		params.add("scopes", "msg.read,msg.write");
		params.add("redirectUrls", "https://msg.example.com/login");
		params.add("accessTokenValiditySeconds", "10800");
		params.add("refreshTokenValiditySeconds", "43200");
		params.add("autoApproveScopes", "");

		RequestEntity<MultiValueMap<String, String>> req2 = post(fromUriString(uri)
				.pathSegment("apps").queryParam("new", "").build().toUri())
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.accept(MediaType.TEXT_HTML)
						.header("Cookie", format("JSESSIONID=%s;", login.jsessionId))
						.body(params);
		ResponseEntity<String> res2 = restTemplate.exchange(req2, String.class);
		assertThat(res2.getStatusCode()).isEqualTo(HttpStatus.FOUND);

		Optional<App> app = appRepository.findByAppName("Message");
		assertThat(app.isPresent()).isEqualTo(true);

		App a = app.get();
		assertThat(a.getAppName()).isEqualTo("Message");
		assertThat(a.getAppUrl()).isEqualTo("https://msg.example.com");
		assertThat(a.getRoles()).containsAll(asList(CLIENT));
		assertThat(a.getGrantTypes()).containsAll(asList(PASSWORD, AUTHORIZATION_CODE));
		assertThat(a.getResourceIds()).containsAll(asList("msg"));
		assertThat(a.getScopes()).containsAll(asList("msg.read", "msg.write"));
		assertThat(a.getRedirectUrls())
				.containsAll(asList("https://msg.example.com/login"));
		assertThat(a.getAccessTokenValiditySeconds()).isEqualTo(10800);
		assertThat(a.getRefreshTokenValiditySeconds()).isEqualTo(43200);
		assertThat(a.getAutoApproveScopes()).isEmpty();

		appRepository.delete(a);
		logout(login);
	}

	@Test
	public void createNewAppWithTwoResourceIds() {
		Login login = login("maki@example.com", "demo");
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("_csrf", login.csrf);
		params.add("appName", "Message");
		params.add("appUrl", "https://msg.example.com");
		params.add("roles", "CLIENT");
		params.add("grantTypes", "PASSWORD");
		params.add("grantTypes", "AUTHORIZATION_CODE");
		params.add("resourceIds", "oauth2-resource,msg");
		params.add("scopes", "msg.read,msg.write");
		params.add("redirectUrls", "https://msg.example.com/login");
		params.add("accessTokenValiditySeconds", "10800");
		params.add("refreshTokenValiditySeconds", "43200");
		params.add("autoApproveScopes", "");

		RequestEntity<MultiValueMap<String, String>> req2 = post(fromUriString(uri)
				.pathSegment("apps").queryParam("new", "").build().toUri())
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.accept(MediaType.TEXT_HTML)
						.header("Cookie", format("JSESSIONID=%s;", login.jsessionId))
						.body(params);
		ResponseEntity<String> res2 = restTemplate.exchange(req2, String.class);
		assertThat(res2.getStatusCode()).isEqualTo(HttpStatus.FOUND);

		Optional<App> app = appRepository.findByAppName("Message");
		assertThat(app.isPresent()).isEqualTo(true);

		App a = app.get();
		assertThat(a.getAppName()).isEqualTo("Message");
		assertThat(a.getAppUrl()).isEqualTo("https://msg.example.com");
		assertThat(a.getRoles()).containsAll(asList(CLIENT));
		assertThat(a.getGrantTypes()).containsAll(asList(PASSWORD, AUTHORIZATION_CODE));
		assertThat(a.getResourceIds()).containsAll(asList("oauth2-resource", "msg"));
		assertThat(a.getScopes()).containsAll(asList("msg.read", "msg.write"));
		assertThat(a.getRedirectUrls())
				.containsAll(asList("https://msg.example.com/login"));
		assertThat(a.getAccessTokenValiditySeconds()).isEqualTo(10800);
		assertThat(a.getRefreshTokenValiditySeconds()).isEqualTo(43200);
		assertThat(a.getAutoApproveScopes()).isEmpty();

		appRepository.delete(a);
		logout(login);
	}

	@Test
	public void createNewAppAndEditResourceId() {
		Login login = login("maki@example.com", "demo");
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("_csrf", login.csrf);
		params.add("appName", "Message");
		params.add("appUrl", "https://msg.example.com");
		params.add("roles", "CLIENT");
		params.add("grantTypes", "PASSWORD");
		params.add("grantTypes", "AUTHORIZATION_CODE");
		params.add("resourceIds", "oauth2-resource");
		params.add("scopes", "msg.read,msg.write");
		params.add("redirectUrls", "https://msg.example.com/login");
		params.add("accessTokenValiditySeconds", "10800");
		params.add("refreshTokenValiditySeconds", "43200");
		params.add("autoApproveScopes", "");

		RequestEntity<MultiValueMap<String, String>> req2 = post(fromUriString(uri)
				.pathSegment("apps").queryParam("new", "").build().toUri())
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.accept(MediaType.TEXT_HTML)
						.header("Cookie", format("JSESSIONID=%s;", login.jsessionId))
						.body(params);
		ResponseEntity<String> res2 = restTemplate.exchange(req2, String.class);
		assertThat(res2.getStatusCode()).isEqualTo(HttpStatus.FOUND);

		Optional<App> created = appRepository.findByAppName("Message");
		assertThat(created.isPresent()).isEqualTo(true);

		params.add("resourceIds", "msg");
		params.add("appSecret", created.get().getAppSecret());
		RequestEntity<MultiValueMap<String, String>> req3 = post(
				fromUriString(uri).pathSegment("apps").queryParam("edit", "")
						.queryParam("appId", created.get().getAppId()).build().toUri())
								.contentType(MediaType.APPLICATION_FORM_URLENCODED)
								.accept(MediaType.TEXT_HTML)
								.header("Cookie",
										format("JSESSIONID=%s;", login.jsessionId))
								.body(params);
		ResponseEntity<String> res3 = restTemplate.exchange(req3, String.class);
		assertThat(res3.getStatusCode()).isEqualTo(HttpStatus.FOUND);

		Optional<App> app = appRepository.findByAppName("Message");
		assertThat(app.isPresent()).isEqualTo(true);

		App a = app.get();
		assertThat(a.getAppName()).isEqualTo("Message");
		assertThat(a.getAppUrl()).isEqualTo("https://msg.example.com");
		assertThat(a.getRoles()).containsAll(asList(CLIENT));
		assertThat(a.getGrantTypes()).containsAll(asList(PASSWORD, AUTHORIZATION_CODE));
		assertThat(a.getResourceIds()).containsAll(asList("oauth2-resource", "msg"));
		assertThat(a.getScopes()).containsAll(asList("msg.read", "msg.write"));
		assertThat(a.getRedirectUrls())
				.containsAll(asList("https://msg.example.com/login"));
		assertThat(a.getAccessTokenValiditySeconds()).isEqualTo(10800);
		assertThat(a.getRefreshTokenValiditySeconds()).isEqualTo(43200);
		assertThat(a.getAutoApproveScopes()).isEmpty();

		appRepository.delete(a);
		logout(login);
	}

	Login login(String username, String password) {
		RequestEntity<?> req1 = get(
				fromUriString(uri).pathSegment("login").build().toUri()).build();
		ResponseEntity<String> res1 = restTemplate.exchange(req1, String.class);
		String csrf0 = csrf(res1);
		String jsessionid0 = jsessionid(res1);
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("_csrf", csrf0);
		params.add("username", username);
		params.add("password", password);
		RequestEntity<MultiValueMap<String, Object>> req2 = post(
				fromUriString(uri).pathSegment("login").build().toUri())
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.accept(MediaType.TEXT_HTML)
						.header("Cookie", format("JSESSIONID=%s;", jsessionid0))
						.body(params);
		ResponseEntity<String> res2 = restTemplate.exchange(req2, String.class);
		String jsessionid = jsessionid(res2);
		RequestEntity<?> req3 = get(
				fromUriString(uri).pathSegment("login").build().toUri())
						.header("Cookie", format("JSESSIONID=%s;", jsessionid)).build();
		ResponseEntity<String> res3 = restTemplate.exchange(req3, String.class);
		String csrf = csrf(res3);
		return new Login(jsessionid, csrf);
	}

	void logout(Login login) {
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("_csrf", login.csrf);
		RequestEntity<MultiValueMap<String, Object>> req2 = post(
				fromUriString(uri).pathSegment("logout").build().toUri())
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.accept(MediaType.TEXT_HTML)
						.header("Cookie", format("JSESSIONID=%s;", login.jsessionId))
						.body(params);
		restTemplate.exchange(req2, Void.class);
	}

	class Login {
		String jsessionId;
		String csrf;

		Login(String jsessionId, String csrf) {
			this.jsessionId = jsessionId;
			this.csrf = csrf;
		}
	}

	String csrf(ResponseEntity<String> res) {
		Matcher m = Pattern.compile("name=\"_csrf\" value=\"(.+)\"")
				.matcher(res.getBody());
		String csrf = null;
		if (m.find()) {
			csrf = m.group(1);
		}
		else {
			fail("CSRF token is missing.");
		}
		return csrf;
	}

	String jsessionid(ResponseEntity<?> res) {
		String cookie = res.getHeaders().get("Set-Cookie").toString();
		Matcher m = Pattern.compile("JSESSIONID=([0-9A-Fa-f]+);").matcher(cookie);

		String jsessionid = null;
		if (m.find()) {
			jsessionid = m.group(1);
		}
		else {
			fail("JSESSIONID is missing.");
		}
		return jsessionid;
	}
}