package am.ik.home.client.member;

import static am.ik.home.client.member.PageableUtils.withPageable;
import static am.ik.home.client.member.TypeReferences.memberResourceType;
import static am.ik.home.client.member.TypeReferences.memberResourcesType;
import static org.springframework.http.RequestEntity.get;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class MemberClientImpl implements MemberClient {
	private final String apiBase;
	private final RestTemplate restTemplate;

	public MemberClientImpl(String apiBase, RestTemplate restTemplate) {
		this.apiBase = apiBase;
		this.restTemplate = restTemplate;
	}

	@Override
	public PagedResources<Member> findAll(Pageable pageable) {
		UriComponentsBuilder builder = fromHttpUrl(apiBase).pathSegment("api", "members");
		return restTemplate.exchange(
				get(withPageable(builder, pageable).build().encode().toUri()).build(),
				memberResourcesType).getBody();
	}

	@Override
	public Resource<Member> findOne(String memberId) {
		return restTemplate
				.exchange(get(fromHttpUrl(apiBase).pathSegment("api", "members", memberId)
						.build().encode().toUri()).build(), memberResourceType)
				.getBody();
	}

	@Override
	public PagedResources<Member> findByIds(String... ids) {
		return restTemplate.exchange(get(
				fromHttpUrl(apiBase).pathSegment("api", "members", "search", "findByIds")
						.queryParam("ids", (Object[]) ids).build().encode().toUri())
								.build(),
				memberResourcesType).getBody();
	}

	@Override
	public Resource<Member> findByEmail(String email) {
		return restTemplate.exchange(
				get(fromHttpUrl(apiBase)
						.pathSegment("api", "members", "search", "findByEmail")
						.queryParam("email", email).build().encode().toUri()).build(),
				memberResourceType).getBody();
	}
}
