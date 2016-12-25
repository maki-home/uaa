package am.ik.home.client.member;

import static am.ik.home.client.member.TypeReferences.memberResourceType;
import static am.ik.home.client.member.TypeReferences.memberResourcesType;

import java.util.concurrent.ExecutionException;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureAdapter;
import org.springframework.web.client.AsyncRestTemplate;

public class AsyncMemberClientImpl implements MemberClient.Async {
	private final String apiBase;
	private final AsyncRestTemplate asyncRestTemplate;

	public AsyncMemberClientImpl(String apiBase, AsyncRestTemplate asyncRestTemplate) {
		this.apiBase = apiBase;
		this.asyncRestTemplate = asyncRestTemplate;
	}

	@Override
	public ListenableFuture<PagedResources<Member>> findAll(Pageable pageable) {
		RequestEntity<Void> requestEntity = RequestEntities.findAll(apiBase, pageable);
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
		RequestEntity<Void> requestEntity = RequestEntities.findOne(apiBase, memberId);
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
		RequestEntity<Void> requestEntity = RequestEntities.findByIds(apiBase, ids);
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
		RequestEntity<Void> requestEntity = RequestEntities.findByEmail(apiBase, email);
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
