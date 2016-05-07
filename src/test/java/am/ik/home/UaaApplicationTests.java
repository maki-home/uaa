package am.ik.home;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UaaApplicationTests {

    @Value("${SERVER_URI:http://localhost:${local.server.port}}/uaa")
    String uri;
    RestTemplate restTemplate = new RestTemplate();

    @Test
    public void testTrustedClient() throws IOException {
        RequestEntity<?> req1 = RequestEntity.post(UriComponentsBuilder.fromUriString(uri)
                .pathSegment("oauth", "token")
                .queryParam("grant_type", "password")
                .queryParam("username", "maki@example.com")
                .queryParam("password", "demo")
                .build().toUri())
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("acme:acmesecret".getBytes()))
                .build();

        // issue token
        JsonNode res1 = restTemplate.exchange(req1, JsonNode.class).getBody();

        assertThat(res1.get("access_token").asText()).matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
        assertThat(res1.get("refresh_token").asText()).matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
        assertThat(res1.get("scope").asText()).isEqualTo("read write trust");
        assertThat(res1.get("expires_in").asLong()).isLessThan(TimeUnit.DAYS.toSeconds(1));

        // check token
        RequestEntity<?> req2 = RequestEntity.get(UriComponentsBuilder.fromUriString(uri)
                .pathSegment("oauth", "check_token")
                .queryParam("token", res1.get("access_token").asText())
                .build().toUri())
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("checker:checker".getBytes()))
                .build();
        JsonNode res2 = restTemplate.exchange(req2, JsonNode.class).getBody();
        assertThat(res2.get("user_name").asText()).isEqualTo("maki@example.com");
        assertThat(res2.get("scope").size()).isEqualTo(3);
        assertThat(res2.get("scope").get(0).asText()).isEqualTo("read");
        assertThat(res2.get("scope").get(1).asText()).isEqualTo("write");
        assertThat(res2.get("scope").get(2).asText()).isEqualTo("trust");
        assertThat(res2.get("authorities").get(0).asText()).isEqualTo("ROLE_USER");
    }

    @Test
    public void testGuestClient() throws IOException {
        RequestEntity<?> req1 = RequestEntity.post(UriComponentsBuilder.fromUriString(uri)
                .pathSegment("oauth", "token")
                .queryParam("grant_type", "password")
                .queryParam("username", "maki@example.com")
                .queryParam("password", "demo")
                .build().toUri())
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("guest:guest".getBytes()))
                .build();

        // issue token
        JsonNode res1 = restTemplate.exchange(req1, JsonNode.class).getBody();

        assertThat(res1.get("access_token").asText()).matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
        assertThat(res1.get("refresh_token").asText()).matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
        assertThat(res1.get("scope").asText()).isEqualTo("read");
        assertThat(res1.get("expires_in").asLong()).isLessThan(TimeUnit.HOURS.toSeconds(1));

        // check token
        RequestEntity<?> req2 = RequestEntity.get(UriComponentsBuilder.fromUriString(uri)
                .pathSegment("oauth", "check_token")
                .queryParam("token", res1.get("access_token").asText())
                .build().toUri())
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("checker:checker".getBytes()))
                .build();
        JsonNode res2 = restTemplate.exchange(req2, JsonNode.class).getBody();
        assertThat(res2.get("user_name").asText()).isEqualTo("maki@example.com");
        assertThat(res2.get("scope").size()).isEqualTo(1);
        assertThat(res2.get("scope").get(0).asText()).isEqualTo("read");
        assertThat(res2.get("authorities").get(0).asText()).isEqualTo("ROLE_USER");
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void test3rdClient() throws IOException {
        RequestEntity<?> req1 = RequestEntity.post(UriComponentsBuilder.fromUriString(uri)
                .pathSegment("oauth", "token")
                .queryParam("grant_type", "password")
                .queryParam("username", "maki@example.com")
                .queryParam("password", "demo")
                .build().toUri())
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("3rd:3rd".getBytes()))
                .build();

        thrown.expect(HttpClientErrorException.class);
        thrown.expectMessage("401 Unauthorized");
        // issue token
        restTemplate.exchange(req1, JsonNode.class).getBody();
    }


    @Test
    public void testGetMemberByTrustedClient() throws Exception {
        RequestEntity<?> req1 = RequestEntity.post(UriComponentsBuilder.fromUriString(uri)
                .pathSegment("oauth", "token")
                .queryParam("grant_type", "password")
                .queryParam("username", "maki@example.com")
                .queryParam("password", "demo")
                .build().toUri())
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("acme:acmesecret".getBytes()))
                .build();

        // issue token
        JsonNode res1 = restTemplate.exchange(req1, JsonNode.class).getBody();
        String accessToken = res1.get("access_token").asText();

        // get member
        RequestEntity<?> req2 = RequestEntity.get(UriComponentsBuilder.fromUriString(uri)
                .pathSegment("api", "members", "search", "findByEmail")
                .queryParam("email", "maki@example.com")
                .build().toUri())
                .header("Authorization", "Bearer " + accessToken)
                .build();
        JsonNode res2 = restTemplate.exchange(req2, JsonNode.class).getBody();
        assertThat(res2.get("givenName").asText()).isEqualTo("Toshiaki");
        assertThat(res2.get("familyName").asText()).isEqualTo("Maki");
        assertThat(res2.get("email").asText()).isEqualTo("maki@example.com");
    }

    @Test
    public void testGetMemberByGuest() throws Exception {
        RequestEntity<?> req1 = RequestEntity.post(UriComponentsBuilder.fromUriString(uri)
                .pathSegment("oauth", "token")
                .queryParam("grant_type", "password")
                .queryParam("username", "maki@example.com")
                .queryParam("password", "demo")
                .build().toUri())
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("guest:guest".getBytes()))
                .build();

        // issue token
        JsonNode res1 = restTemplate.exchange(req1, JsonNode.class).getBody();
        String accessToken = res1.get("access_token").asText();

        // get member
        RequestEntity<?> req2 = RequestEntity.get(UriComponentsBuilder.fromUriString(uri)
                .pathSegment("api", "members", "search", "findByEmail")
                .queryParam("email", "maki@example.com")
                .build().toUri())
                .header("Authorization", "Bearer " + accessToken)
                .build();

        thrown.expect(HttpClientErrorException.class);
        thrown.expectMessage("403 Forbidden");
        // issue token
        restTemplate.exchange(req2, JsonNode.class).getBody();
    }

}
