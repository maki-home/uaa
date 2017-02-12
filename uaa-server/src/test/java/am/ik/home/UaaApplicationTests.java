package am.ik.home;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.RequestEntity.get;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.assertj.core.util.Sets;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.RequestEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import am.ik.home.app.App;
import am.ik.home.app.AppGrantType;
import am.ik.home.app.AppRepository;
import am.ik.home.app.AppRole;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
		"spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:h2:mem:test;DB_CLOSE_ON_EXIT=FALSE}",
		"spring.application.name=uaa-test" })
public class UaaApplicationTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Value("${SERVER_URI:http://localhost:${local.server.port}}/uaa")
	String uri;
	RestTemplate restTemplate = new RestTemplate();
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	AppRepository appRepository;

	String getBasic(String appName) {
		String ret = jdbcTemplate.queryForObject(
				"SELECT concat(app_id, ':', app_secret) FROM app WHERE app_name = ?",
				String.class, appName);
		return Base64.getEncoder().encodeToString(ret.getBytes());
	}

	@Test
	public void testTrustedClient() throws IOException {
		RequestEntity<?> req1 = post(fromUriString(uri).pathSegment("oauth", "token")
				.queryParam("grant_type", "password")
				.queryParam("username", "maki@example.com").queryParam("password", "demo")
				.build().toUri()).header("Authorization", "Basic " + getBasic("Moneygr"))
						.build();

		// issue token
		JsonNode res1 = restTemplate.exchange(req1, JsonNode.class).getBody();
		assertThat(res1.get("access_token").asText()).isNotEmpty();
		assertThat(res1.get("refresh_token").asText()).isNotEmpty();
		assertThat(res1.get("scope").asText().split(" ")).hasSize(2);
		assertThat(res1.get("scope").asText().split(" ")).contains("member.read");
		assertThat(res1.get("scope").asText().split(" ")).contains("member.write");
		assertThat(res1.get("expires_in").asLong())
				.isLessThan(TimeUnit.DAYS.toSeconds(1));
		assertThat(res1.get("family_name").asText()).isEqualTo("Maki");
		assertThat(res1.get("given_name").asText()).isEqualTo("Toshiaki");
		assertThat(res1.get("display_name").asText()).isEqualTo("Maki Toshiaki");
		assertThat(res1.get("user_id").asText())
				.matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
		assertThat(res1.get("email").asText()).isEqualTo("maki@example.com");
		assertThat(res1.get("iss").asText()).isEqualTo("uaa-test");
		assertThat(res1.get("iat").asLong())
				.isLessThanOrEqualTo(Instant.now().getEpochSecond());
		assertThat(res1.get("exp").asLong())
				.isGreaterThanOrEqualTo(Instant.now().getEpochSecond());
	}

	@Test
	public void testTrustedClient_loginByMemberId() throws IOException {
		RequestEntity<?> req1 = post(fromUriString(uri).pathSegment("oauth", "token")
				.queryParam("grant_type", "password")
				.queryParam("username", "00000000-0000-0000-0000-000000000000")
				.queryParam("password", "demo").build().toUri())
						.header("Authorization", "Basic " + getBasic("Moneygr")).build();

		// issue token
		JsonNode res1 = restTemplate.exchange(req1, JsonNode.class).getBody();

		assertThat(res1.get("access_token").asText()).isNotEmpty();
		assertThat(res1.get("refresh_token").asText()).isNotEmpty();
		assertThat(res1.get("scope").asText().split(" ")).hasSize(2);
		assertThat(res1.get("scope").asText().split(" ")).contains("member.read");
		assertThat(res1.get("scope").asText().split(" ")).contains("member.write");
		assertThat(res1.get("expires_in").asLong())
				.isLessThan(TimeUnit.DAYS.toSeconds(1));
		assertThat(res1.get("family_name").asText()).isEqualTo("Maki");
		assertThat(res1.get("given_name").asText()).isEqualTo("Toshiaki");
		assertThat(res1.get("display_name").asText()).isEqualTo("Maki Toshiaki");
		assertThat(res1.get("user_id").asText())
				.matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
		assertThat(res1.get("email").asText()).isEqualTo("maki@example.com");
		assertThat(res1.get("iss").asText()).isEqualTo("uaa-test");
		assertThat(res1.get("iat").asLong())
				.isLessThanOrEqualTo(Instant.now().getEpochSecond());
		assertThat(res1.get("exp").asLong())
				.isGreaterThanOrEqualTo(Instant.now().getEpochSecond());
	}

	@Test
	public void testGuestClient() throws IOException {
		RequestEntity<?> req1 = post(fromUriString(uri).pathSegment("oauth", "token")
				.queryParam("grant_type", "password")
				.queryParam("username", "maki@example.com").queryParam("password", "demo")
				.build().toUri())
						.header("Authorization", "Basic " + getBasic("Guest App"))
						.build();

		// issue token
		JsonNode res1 = restTemplate.exchange(req1, JsonNode.class).getBody();

		assertThat(res1.get("access_token").asText()).isNotEmpty();
		assertThat(res1.get("refresh_token").asText()).isNotEmpty();
		assertThat(res1.get("scope").asText()).isEqualTo("member.read");
		assertThat(res1.get("expires_in").asLong())
				.isLessThan(TimeUnit.HOURS.toSeconds(1));
		assertThat(res1.get("family_name").asText()).isEqualTo("Maki");
		assertThat(res1.get("given_name").asText()).isEqualTo("Toshiaki");
		assertThat(res1.get("display_name").asText()).isEqualTo("Maki Toshiaki");
		assertThat(res1.get("user_id").asText())
				.matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
		assertThat(res1.get("email").asText()).isEqualTo("maki@example.com");
		assertThat(res1.get("iss").asText()).isEqualTo("uaa-test");
		assertThat(res1.get("iat").asLong())
				.isLessThanOrEqualTo(Instant.now().getEpochSecond());
		assertThat(res1.get("exp").asLong())
				.isGreaterThanOrEqualTo(Instant.now().getEpochSecond());
	}

	@Test
	public void test3rdClient() throws IOException {
		RequestEntity<?> req1 = post(fromUriString(uri).pathSegment("oauth", "token")
				.queryParam("grant_type", "password")
				.queryParam("username", "maki@example.com").queryParam("password", "demo")
				.build().toUri()).header("Authorization", "Basic " + getBasic("3rd App"))
						.build();

		thrown.expect(HttpClientErrorException.class);
		// TODO thrown.expectMessage("401 Unauthorized");
		// issue token
		restTemplate.exchange(req1, JsonNode.class).getBody();
	}

	@Test
	public void testGetMemberByTrustedClient() throws Exception {
		RequestEntity<?> req1 = post(fromUriString(uri).pathSegment("oauth", "token")
				.queryParam("grant_type", "password")
				.queryParam("username", "maki@example.com").queryParam("password", "demo")
				.build().toUri()).header("Authorization", "Basic " + getBasic("Moneygr"))
						.build();

		// issue token
		JsonNode res1 = restTemplate.exchange(req1, JsonNode.class).getBody();
		String accessToken = res1.get("access_token").asText();

		// get member
		RequestEntity<?> req2 = get(
				fromUriString(uri).pathSegment("v1", "members", "search", "findByEmail")
						.queryParam("email", "maki@example.com").build().toUri())
								.header("Authorization", "Bearer " + accessToken).build();
		JsonNode res2 = restTemplate.exchange(req2, JsonNode.class).getBody();
		assertThat(res2.get("memberId").asText())
				.matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
		assertThat(res2.get("givenName").asText()).isEqualTo("Toshiaki");
		assertThat(res2.get("familyName").asText()).isEqualTo("Maki");
		assertThat(res2.get("email").asText()).isEqualTo("maki@example.com");
	}

	@Test
	public void testGetMemberByGuest() throws Exception {
		RequestEntity<?> req1 = post(fromUriString(uri).pathSegment("oauth", "token")
				.queryParam("grant_type", "password")
				.queryParam("username", "maki@example.com").queryParam("password", "demo")
				.build().toUri())
						.header("Authorization", "Basic " + getBasic("Guest App"))
						.build();

		// issue token
		JsonNode res1 = restTemplate.exchange(req1, JsonNode.class).getBody();
		String accessToken = res1.get("access_token").asText();

		// get member
		RequestEntity<?> req2 = get(
				fromUriString(uri).pathSegment("v1", "members", "search", "findByEmail")
						.queryParam("email", "maki@example.com").build().toUri())
								.header("Authorization", "Bearer " + accessToken).build();

		thrown.expect(HttpClientErrorException.class);
		// TODO thrown.expectMessage("403 Forbidden");
		// issue token
		restTemplate.exchange(req2, JsonNode.class).getBody();
	}

	@Test
	public void testFindByIdsByTrustedClient() throws Exception {
		RequestEntity<?> req1 = post(fromUriString(uri).pathSegment("oauth", "token")
				.queryParam("grant_type", "password")
				.queryParam("username", "maki@example.com").queryParam("password", "demo")
				.build().toUri()).header("Authorization", "Basic " + getBasic("Moneygr"))
						.build();

		// issue token
		JsonNode res1 = restTemplate.exchange(req1, JsonNode.class).getBody();
		String accessToken = res1.get("access_token").asText();

		// get member
		RequestEntity<?> req3 = get(
				fromUriString(uri).pathSegment("v1", "members", "search", "findByIds")
						.queryParam("ids", res1.get("user_id").asText())
						.queryParam("ids", "00000000-0000-0000-0000-000000000000").build()
						.toUri()).header("Authorization", "Bearer " + accessToken)
								.build();
		JsonNode res3 = restTemplate.exchange(req3, JsonNode.class).getBody();
		assertThat(res3.get("_embedded").get("members")).hasSize(1);
		assertThat(res3.get("_embedded").get("members").get(0).get("memberId").asText())
				.matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
		assertThat(res3.get("_embedded").get("members").get(0).get("givenName").asText())
				.isEqualTo("Toshiaki");
		assertThat(res3.get("_embedded").get("members").get(0).get("familyName").asText())
				.isEqualTo("Maki");
		assertThat(res3.get("_embedded").get("members").get(0).get("email").asText())
				.isEqualTo("maki@example.com");
	}

	@Test
	public void testUserWithOpenIdScope() throws Exception {
		String testId = UUID.randomUUID().toString();
		int insertedApp = jdbcTemplate
				.update("INSERT INTO app (access_token_validity_seconds, app_name, app_secret, app_url, refresh_token_validity_seconds, app_id)\n"
						+ "VALUES (180, 'OpenIdApp', '" + testId
						+ "', 'https://openid.example.com', 10800, '" + testId + "');");
		assertThat(insertedApp).isEqualTo(1);
		int insertedGrantType = jdbcTemplate
				.update("INSERT INTO app_grant_types (app_app_id, grant_types) VALUES ('"
						+ testId + "', 'PASSWORD');");
		assertThat(insertedGrantType).isEqualTo(1);
		int insertedScope = jdbcTemplate
				.update("INSERT INTO app_scopes (app_app_id, scopes) VALUES ('" + testId
						+ "', 'OPENID');");
		assertThat(insertedScope).isEqualTo(1);

		RequestEntity<?> req1 = post(fromUriString(uri).pathSegment("oauth", "token")
				.queryParam("grant_type", "password")
				.queryParam("username", "maki@example.com").queryParam("password", "demo")
				.build().toUri())
						.header("Authorization", "Basic " + getBasic("OpenIdApp"))
						.build();

		// issue token
		JsonNode res1 = restTemplate.exchange(req1, JsonNode.class).getBody();

		String accessToken = res1.get("access_token").asText();

		// get user
		RequestEntity<?> req2 = get(
				fromUriString(uri).pathSegment("userinfo").build().toUri())
						.header("Authorization", "Bearer " + accessToken).build();
		JsonNode res2 = restTemplate.exchange(req2, JsonNode.class).getBody();
		assertThat(res2.get("id").asText())
				.isEqualTo("00000000-0000-0000-0000-000000000000");
		assertThat(res2.get("email").asText()).isEqualTo("maki@example.com");
		assertThat(res2.get("name").get("givenName").asText()).isEqualTo("Toshiaki");
		assertThat(res2.get("name").get("familyName").asText()).isEqualTo("Maki");

		int deletedScope = jdbcTemplate
				.update("DELETE FROM app_scopes WHERE app_app_id = ?", testId);
		assertThat(deletedScope).isEqualTo(1);
		int deletedGrantType = jdbcTemplate
				.update("DELETE FROM app_grant_types WHERE app_app_id = ?", testId);
		assertThat(deletedGrantType).isEqualTo(1);
		int deletedApp = jdbcTemplate.update("DELETE FROM app WHERE app_id = ?", testId);
		assertThat(deletedApp).isEqualTo(1);
	}

	@Test
	public void testUserWithoutOpenIdScope() throws Exception {
		RequestEntity<?> req1 = post(fromUriString(uri).pathSegment("oauth", "token")
				.queryParam("grant_type", "password")
				.queryParam("username", "maki@example.com").queryParam("password", "demo")
				.build().toUri()).header("Authorization", "Basic " + getBasic("Moneygr"))
						.build();

		// issue token
		JsonNode res1 = restTemplate.exchange(req1, JsonNode.class).getBody();

		String accessToken = res1.get("access_token").asText();

		// get user
		thrown.expect(HttpClientErrorException.class);

		RequestEntity<?> req2 = get(
				fromUriString(uri).pathSegment("user").build().toUri())
						.header("Authorization", "Bearer " + accessToken).build();
		restTemplate.exchange(req2, JsonNode.class).getBody();
	}

	@Test
	public void testGetMemberByAdminClient_ResourceOwnerPasswordCredentials() {
		App adminClient = App.builder().appName("AdminClient").appSecret("admin")
				.appId(UUID.randomUUID().toString()).appUrl("http://admin.example.com")
				.grantTypes(Sets.newLinkedHashSet(AppGrantType.PASSWORD))
				.accessTokenValiditySeconds(100).refreshTokenValiditySeconds(100)
				.redirectUrls(Sets.newLinkedHashSet("http://admin.example.com/login"))
				.roles(Sets.newLinkedHashSet(AppRole.TRUSTED_CLIENT))
				.scopes(Sets.newLinkedHashSet("admin.read", "admin.write")).build();
		adminClient = appRepository.saveAndFlush(adminClient);

		// begin test

		RequestEntity<?> req1 = post(fromUriString(uri).pathSegment("oauth", "token")
				.queryParam("grant_type", "password")
				.queryParam("username", "maki@example.com").queryParam("password", "demo")
				.build().toUri())
						.header("Authorization", "Basic " + getBasic("AdminClient"))
						.build();

		// issue token
		JsonNode res1 = restTemplate.exchange(req1, JsonNode.class).getBody();
		String accessToken = res1.get("access_token").asText();

		// get member
		RequestEntity<?> req3 = get(
				fromUriString(uri).pathSegment("v1", "members", "search", "findByIds")
						.queryParam("ids", res1.get("user_id").asText())
						.queryParam("ids", "00000000-0000-0000-0000-000000000000").build()
						.toUri()).header("Authorization", "Bearer " + accessToken)
								.build();
		JsonNode res3 = restTemplate.exchange(req3, JsonNode.class).getBody();
		assertThat(res3.get("_embedded").get("members")).hasSize(1);
		assertThat(res3.get("_embedded").get("members").get(0).get("memberId").asText())
				.matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
		assertThat(res3.get("_embedded").get("members").get(0).get("givenName").asText())
				.isEqualTo("Toshiaki");
		assertThat(res3.get("_embedded").get("members").get(0).get("familyName").asText())
				.isEqualTo("Maki");
		assertThat(res3.get("_embedded").get("members").get(0).get("email").asText())
				.isEqualTo("maki@example.com");

		// end test

		appRepository.delete(adminClient);
	}

	@Test
	public void testGetMemberByAdminClient_ClientCredentials() {
		App adminClient = App.builder().appName("AdminClient").appSecret("admin")
				.appId(UUID.randomUUID().toString()).appUrl("http://admin.example.com")
				.grantTypes(Sets.newLinkedHashSet(AppGrantType.CLIENT_CREDENTIALS))
				.accessTokenValiditySeconds(100).refreshTokenValiditySeconds(100)
				.redirectUrls(Sets.newLinkedHashSet("http://admin.example.com/login"))
				.roles(Sets.newLinkedHashSet(AppRole.TRUSTED_CLIENT))
				.scopes(Sets.newLinkedHashSet("admin.read", "admin.write")).build();
		adminClient = appRepository.saveAndFlush(adminClient);

		// begin test

		RequestEntity<?> req1 = post(fromUriString(uri).pathSegment("oauth", "token")
				.queryParam("grant_type", "client_credentials").build().toUri())
						.header("Authorization", "Basic " + getBasic("AdminClient"))
						.build();

		// issue token
		JsonNode res1 = restTemplate.exchange(req1, JsonNode.class).getBody();
		String accessToken = res1.get("access_token").asText();

		// get member
		RequestEntity<?> req3 = get(
				fromUriString(uri).pathSegment("v1", "members", "search", "findByIds")
						.queryParam("ids", "00000000-0000-0000-0000-000000000000")
						.queryParam("ids", "00000000-0000-0000-0000-000000000000").build()
						.toUri()).header("Authorization", "Bearer " + accessToken)
								.build();
		JsonNode res3 = restTemplate.exchange(req3, JsonNode.class).getBody();
		assertThat(res3.get("_embedded").get("members")).hasSize(1);
		assertThat(res3.get("_embedded").get("members").get(0).get("memberId").asText())
				.matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
		assertThat(res3.get("_embedded").get("members").get(0).get("givenName").asText())
				.isEqualTo("Toshiaki");
		assertThat(res3.get("_embedded").get("members").get(0).get("familyName").asText())
				.isEqualTo("Maki");
		assertThat(res3.get("_embedded").get("members").get(0).get("email").asText())
				.isEqualTo("maki@example.com");

		// end test

		appRepository.delete(adminClient);
	}
}
