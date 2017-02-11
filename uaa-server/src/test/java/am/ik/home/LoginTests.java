package am.ik.home;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.http.RequestEntity.get;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.JsonNode;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
		"spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:h2:mem:test;DB_CLOSE_ON_EXIT=FALSE}" })
public class LoginTests {
	@Value("${SERVER_URI:http://localhost:${local.server.port}}/uaa")
	String uri;
	TestRestTemplate restTemplate = new TestRestTemplate();

	@Test
	public void healthCheckWithActuatorRole() {
		RequestEntity<?> req1 = get(
				fromUriString(uri).pathSegment("login").build().toUri()).build();
		ResponseEntity<String> res1 = restTemplate.exchange(req1, String.class);
		String csrf = csrf(res1);
		String jsessionid0 = jsessionid(res1);
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("_csrf", csrf);
		params.add("username", "maki@example.com");
		params.add("password", "demo");
		RequestEntity<MultiValueMap<String, Object>> req2 = post(
				fromUriString(uri).pathSegment("login").build().toUri())
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.accept(MediaType.TEXT_HTML)
						.header("Cookie", String.format("JSESSIONID=%s;", jsessionid0))
						.body(params);
		ResponseEntity<String> res2 = restTemplate.exchange(req2, String.class);
		String jsessionid = jsessionid(res2);
		RequestEntity<?> req3 = get(
				fromUriString(uri).pathSegment("/admin/health").build().toUri())
						.header("Cookie", String.format("JSESSIONID=%s;", jsessionid))
						.build();
		ResponseEntity<JsonNode> res3 = restTemplate.exchange(req3, JsonNode.class);
		assertThat(res3.getBody().has("db")).isTrue();
	}

	@Test
	public void healthCheckWithoutActuatorRole() {
		RequestEntity<?> req1 = get(
				fromUriString(uri).pathSegment("login").build().toUri()).build();
		ResponseEntity<String> res1 = restTemplate.exchange(req1, String.class);
		String csrf = csrf(res1);
		String jsessionid0 = jsessionid(res1);
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("_csrf", csrf);
		params.add("username", "demo@example.com");
		params.add("password", "demo");
		RequestEntity<MultiValueMap<String, Object>> req2 = post(
				fromUriString(uri).pathSegment("login").build().toUri())
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.accept(MediaType.TEXT_HTML)
						.header("Cookie", String.format("JSESSIONID=%s;", jsessionid0))
						.body(params);
		ResponseEntity<String> res2 = restTemplate.exchange(req2, String.class);
		String jsessionid = jsessionid(res2);
		RequestEntity<?> req3 = get(
				fromUriString(uri).pathSegment("/admin/health").build().toUri())
						.header("Cookie", String.format("JSESSIONID=%s;", jsessionid))
						.build();
		ResponseEntity<JsonNode> res3 = restTemplate.exchange(req3, JsonNode.class);
		assertThat(res3.getBody().has("db")).isFalse();
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