package am.ik.home.it;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Base64;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import am.ik.home.client.member.*;
import am.ik.home.client.user.UaaUser;

public class UaaIT {
	private String apiBase;
	private RestTemplate restTemplate;
	private AsyncRestTemplate asyncRestTemplate;

	@Before
	public void setUp() {
		apiBase = System.getenv("API_BASE");
		if (apiBase == null) {
			apiBase = "https://home-uaa-dev.cfapps.pez.pivotal.io/uaa";
		}
	}

	private String retrieveAccessToken(String clientId, String clientSecret,
			String username, String password) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setInterceptors(singletonList((request, body, execution) -> {
			request.getHeaders().add(HttpHeaders.AUTHORIZATION,
					"Basic " + Base64.getEncoder()
							.encodeToString((clientId + ":" + clientSecret).getBytes()));
			return execution.execute(request, body);
		}));
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiBase)
				.pathSegment("oauth", "token");
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("username", username);
		body.add("password", password);
		body.add("grant_type", "password");
		JsonNode response = restTemplate.postForObject(builder.build().toUri(), body,
				JsonNode.class);
		return response.get("access_token").asText();
	}

	private RestTemplate createRestTemplate(String accessToken) {
		if (this.restTemplate != null) {
			return this.restTemplate;
		}
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setInterceptors(singletonList((request, body, execution) -> {
			request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
			return execution.execute(request, body);
		}));
		restTemplate.getMessageConverters().stream()
				.filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
				.map(MappingJackson2HttpMessageConverter.class::cast).findAny()
				.ifPresent(converter -> {
					converter.getObjectMapper().registerModule(new Jackson2HalModule());
				});
		this.restTemplate = restTemplate;
		return restTemplate;
	}

	private AsyncRestTemplate createAsyncRestTemplate(String accessToken) {
		if (this.asyncRestTemplate != null) {
			return this.asyncRestTemplate;
		}
		SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
		clientHttpRequestFactory.setTaskExecutor(new SimpleAsyncTaskExecutor());
		AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate(
				clientHttpRequestFactory, createRestTemplate(accessToken));
		asyncRestTemplate.setInterceptors(singletonList((request, body, execution) -> {
			request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
			return execution.executeAsync(request, body);
		}));
		this.asyncRestTemplate = asyncRestTemplate;
		return asyncRestTemplate;
	}

	private void assertThatMemberIsMaki(Member member) {
		assertThat(member.getMemberId())
				.isEqualTo("00000000-0000-0000-0000-000000000000");
		assertThat(member.getEmail()).isEqualTo("maki@example.com");
		assertThat(member.getGivenName()).isEqualTo("Toshiaki");
		assertThat(member.getFamilyName()).isEqualTo("Maki");
		assertThat(member.getRoles()).containsExactly(MemberRole.USER, MemberRole.ADMIN);
	}

	private void assertThatMemberIsDemo(Member member) {
		assertThat(member.getMemberId())
				.isEqualTo("00000000-0000-0000-0000-000000000001");
		assertThat(member.getEmail()).isEqualTo("demo@example.com");
		assertThat(member.getGivenName()).isEqualTo("Taro");
		assertThat(member.getFamilyName()).isEqualTo("Demo");
		assertThat(member.getRoles()).containsExactly(MemberRole.USER);
	}

	@Test
	public void testMemberClientImpl_findAll() {
		String accessToken = retrieveAccessToken("00000000-0000-0000-0000-000000000000",
				"00000000-0000-0000-0000-000000000000", "maki@example.com", "demo");
		RestTemplate restTemplate = createRestTemplate(accessToken);
		MemberClient memberClient = new MemberClientImpl(apiBase, restTemplate);

		PagedResources<Member> members = memberClient.findAll();
		assertThat(members).hasSize(2);
		Iterator<Member> iterator = members.iterator();
		assertThatMemberIsMaki(iterator.next());
		assertThatMemberIsDemo(iterator.next());
	}

	@Test
	public void testMemberClientImpl_findOne() {
		String accessToken = retrieveAccessToken("00000000-0000-0000-0000-000000000000",
				"00000000-0000-0000-0000-000000000000", "maki@example.com", "demo");
		RestTemplate restTemplate = createRestTemplate(accessToken);
		MemberClient memberClient = new MemberClientImpl(apiBase, restTemplate);

		assertThatMemberIsMaki(memberClient
				.findOne("00000000-0000-0000-0000-000000000000").getContent());
		assertThatMemberIsDemo(memberClient
				.findOne("00000000-0000-0000-0000-000000000001").getContent());
	}

	@Test
	public void testMemberClientImpl_findByEmail() {
		String accessToken = retrieveAccessToken("00000000-0000-0000-0000-000000000000",
				"00000000-0000-0000-0000-000000000000", "maki@example.com", "demo");
		RestTemplate restTemplate = createRestTemplate(accessToken);
		MemberClient memberClient = new MemberClientImpl(apiBase, restTemplate);

		assertThatMemberIsMaki(memberClient.findByEmail("maki@example.com").getContent());
		assertThatMemberIsDemo(memberClient.findByEmail("demo@example.com").getContent());
	}

	@Test
	public void testMemberClientImpl_findByIds() {
		String accessToken = retrieveAccessToken("00000000-0000-0000-0000-000000000000",
				"00000000-0000-0000-0000-000000000000", "maki@example.com", "demo");
		RestTemplate restTemplate = createRestTemplate(accessToken);
		MemberClient memberClient = new MemberClientImpl(apiBase, restTemplate);

		{
			PagedResources<Member> members = memberClient
					.findByIds("00000000-0000-0000-0000-000000000000");
			assertThat(members).hasSize(1);
			Iterator<Member> iterator = members.iterator();
			assertThatMemberIsMaki(iterator.next());
		}
		{
			PagedResources<Member> members = memberClient.findByIds(
					"00000000-0000-0000-0000-000000000000",
					"00000000-0000-0000-0000-000000000001");
			assertThat(members).hasSize(2);
			Iterator<Member> iterator = members.iterator();
			// order by familyName, givenName
			assertThatMemberIsDemo(iterator.next());
			assertThatMemberIsMaki(iterator.next());
		}
	}

	@Test
	public void testAsyncMemberClientImpl_findAll() throws Exception {
		String accessToken = retrieveAccessToken("00000000-0000-0000-0000-000000000000",
				"00000000-0000-0000-0000-000000000000", "maki@example.com", "demo");
		AsyncRestTemplate asyncRestTemplate = createAsyncRestTemplate(accessToken);

		MemberClient.Async memberClient = new AsyncMemberClientImpl(apiBase,
				asyncRestTemplate);

		memberClient.findAll().thenAccept(members -> {
			assertThat(members).hasSize(2);
			Iterator<Member> iterator = members.iterator();
			assertThatMemberIsMaki(iterator.next());
			assertThatMemberIsDemo(iterator.next());
		}).get();
	}

	@Test
	public void testAsyncMemberClientImpl_findOne() throws Exception {
		String accessToken = retrieveAccessToken("00000000-0000-0000-0000-000000000000",
				"00000000-0000-0000-0000-000000000000", "maki@example.com", "demo");
		AsyncRestTemplate asyncRestTemplate = createAsyncRestTemplate(accessToken);

		MemberClient.Async memberClient = new AsyncMemberClientImpl(apiBase,
				asyncRestTemplate);

		CompletableFuture.allOf(memberClient
				.findOne("00000000-0000-0000-0000-000000000000").thenAccept(member -> {
					assertThatMemberIsMaki(member.getContent());
				}), memberClient.findOne("00000000-0000-0000-0000-000000000001")
						.thenAccept(member -> {
							assertThatMemberIsDemo(member.getContent());
						}))
				.get();
	}

	@Test
	public void testAsyncMemberClientImpl_findByEmail() throws Exception {
		String accessToken = retrieveAccessToken("00000000-0000-0000-0000-000000000000",
				"00000000-0000-0000-0000-000000000000", "maki@example.com", "demo");
		AsyncRestTemplate asyncRestTemplate = createAsyncRestTemplate(accessToken);

		MemberClient.Async memberClient = new AsyncMemberClientImpl(apiBase,
				asyncRestTemplate);

		CompletableFuture
				.allOf(memberClient.findByEmail("maki@example.com").thenAccept(member -> {
					assertThatMemberIsMaki(member.getContent());
				}), memberClient.findByEmail("demo@example.com").thenAccept(member -> {
					assertThatMemberIsDemo(member.getContent());
				})).get();
	}

	@Test
	public void testAsyncMemberClientImpl_findByIds() throws Exception {
		String accessToken = retrieveAccessToken("00000000-0000-0000-0000-000000000000",
				"00000000-0000-0000-0000-000000000000", "maki@example.com", "demo");
		AsyncRestTemplate asyncRestTemplate = createAsyncRestTemplate(accessToken);

		MemberClient.Async memberClient = new AsyncMemberClientImpl(apiBase,
				asyncRestTemplate);

		CompletableFuture
				.allOf(memberClient
						.findByIds("00000000-0000-0000-0000-000000000000",
								"00000000-0000-0000-0000-000000000001")
						.thenAccept(members -> {
							assertThat(members).hasSize(2);
							Iterator<Member> iterator = members.iterator();
							assertThatMemberIsDemo(iterator.next());
							assertThatMemberIsMaki(iterator.next());
						}), memberClient.findByIds("00000000-0000-0000-0000-000000000000")
								.thenAccept(members -> {
									assertThat(members).hasSize(1);
									Iterator<Member> iterator = members.iterator();
									assertThatMemberIsMaki(iterator.next());
								}))
				.get();
	}

	@Test
	public void testUaaUser() {
		String accessToken = retrieveAccessToken("00000000-0000-0000-0000-000000000000",
				"00000000-0000-0000-0000-000000000000", "maki@example.com", "demo");
		ObjectMapper objectMapper = new ObjectMapper();
		UaaUser user = new UaaUser(objectMapper, accessToken);

		assertThat(user.getUserId()).isEqualTo("00000000-0000-0000-0000-000000000000");
		assertThat(user.getUserName()).isEqualTo("maki@example.com");
		assertThat(user.getDisplayName()).isEqualTo("Maki Toshiaki");
		assertThat(user.getAuthorities()).containsExactly("ROLE_ADMIN", "ROLE_USER");
		assertThat(user.getScope()).containsExactly("read", "write");
	}
}
