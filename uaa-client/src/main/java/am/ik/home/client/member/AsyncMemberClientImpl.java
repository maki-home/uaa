package am.ik.home.client.member;

import static am.ik.home.client.member.PageableUtils.withPageable;
import static am.ik.home.client.member.TypeReferences.memberResourceType;
import static am.ik.home.client.member.TypeReferences.memberResourcesType;
import static org.springframework.http.RequestEntity.get;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

import java.util.concurrent.ExecutionException;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureAdapter;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class AsyncMemberClientImpl implements MemberClient.Async {
	private final String apiBase;
	private final AsyncRestTemplate asyncRestTemplate;

	public AsyncMemberClientImpl(String apiBase, AsyncRestTemplate asyncRestTemplate) {
		this.apiBase = apiBase;
		this.asyncRestTemplate = asyncRestTemplate;
	}

	@Override
	public ListenableFuture<PagedResources<Member>> findAll(Pageable pageable) {
		UriComponentsBuilder builder = fromHttpUrl(apiBase).pathSegment("api", "members");
		RequestEntity<Void> requestEntity = get(
				withPageable(builder, pageable).build().encode().toUri()).build();
		return new ListenableFutureAdapter<PagedResources<Member>, ResponseEntity<PagedResources<Member>>>(
				asyncRestTemplate.exchange(requestEntity.getUrl(),
						requestEntity.getMethod(), requestEntity, memberResourcesType)) {
			@Override
			protected PagedResources<Member> adapt(
					ResponseEntity<PagedResources<Member>> adapteeResult)
					throws ExecutionException {
				return adapteeResult.getBody();
			}
		};
	}

	@Override
	public ListenableFuture<Resource<Member>> findOne(String memberId) {
		RequestEntity<Void> requestEntity = get(fromHttpUrl(apiBase)
				.pathSegment("api", "members", memberId).build().encode().toUri())
						.build();
		return new ListenableFutureAdapter<Resource<Member>, ResponseEntity<Resource<Member>>>(
				asyncRestTemplate.exchange(requestEntity.getUrl(),
						requestEntity.getMethod(), requestEntity, memberResourceType)) {
			@Override
			protected Resource<Member> adapt(
					ResponseEntity<Resource<Member>> adapteeResult)
					throws ExecutionException {
				return adapteeResult.getBody();
			}
		};
	}

	@Override
	public ListenableFuture<PagedResources<Member>> findByIds(String... ids) {
		RequestEntity<Void> requestEntity = get(
				fromHttpUrl(apiBase).pathSegment("api", "members", "search", "findByIds")
						.queryParam("ids", (Object[]) ids).build().encode().toUri())
								.build();
		return new ListenableFutureAdapter<PagedResources<Member>, ResponseEntity<PagedResources<Member>>>(
				asyncRestTemplate.exchange(requestEntity.getUrl(),
						requestEntity.getMethod(), requestEntity, memberResourcesType)) {
			@Override
			protected PagedResources<Member> adapt(
					ResponseEntity<PagedResources<Member>> adapteeResult)
					throws ExecutionException {
				return adapteeResult.getBody();
			}
		};
	}

	@Override
	public ListenableFuture<Resource<Member>> findByEmail(String email) {
		RequestEntity<Void> requestEntity = get(fromHttpUrl(apiBase)
				.pathSegment("api", "members", "search", "findByEmail")
				.queryParam("email", email).build().encode().toUri()).build();
		return new ListenableFutureAdapter<Resource<Member>, ResponseEntity<Resource<Member>>>(
				asyncRestTemplate.exchange(requestEntity.getUrl(),
						requestEntity.getMethod(), requestEntity, memberResourceType)) {
			@Override
			protected Resource<Member> adapt(
					ResponseEntity<Resource<Member>> adapteeResult)
					throws ExecutionException {
				return adapteeResult.getBody();
			}
		};
	}
}
