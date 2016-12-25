package am.ik.home.client.member;

import static am.ik.home.client.member.TypeReferences.memberResourceType;
import static am.ik.home.client.member.TypeReferences.memberResourcesType;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

public class MemberClientImpl implements MemberClient {
	private final String apiBase;
	private final RestTemplate restTemplate;

	public MemberClientImpl(String apiBase, RestTemplate restTemplate) {
		this.apiBase = apiBase;
		this.restTemplate = restTemplate;
	}

	@Override
	public PagedResources<Member> findAll(Pageable pageable) {
		RequestEntity<Void> requestEntity = RequestEntities.findAll(apiBase, pageable);
		return restTemplate.exchange(requestEntity, memberResourcesType).getBody();
	}

	@Override
	public Resource<Member> findOne(String memberId) {
		RequestEntity<Void> requestEntity = RequestEntities.findOne(apiBase, memberId);
		return restTemplate.exchange(requestEntity, memberResourceType).getBody();
	}

	@Override
	public PagedResources<Member> findByIds(String... ids) {
		RequestEntity<Void> requestEntity = RequestEntities.findByIds(apiBase, ids);
		return restTemplate.exchange(requestEntity, memberResourcesType).getBody();
	}

	@Override
	public Resource<Member> findByEmail(String email) {
		RequestEntity<Void> requestEntity = RequestEntities.findByEmail(apiBase, email);
		return restTemplate.exchange(requestEntity, memberResourceType).getBody();
	}
}
