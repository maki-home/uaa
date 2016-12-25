package am.ik.home.client.member;

import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;

public interface MemberClient {
	PagedResources<Member> findAll(Pageable pageable);

	default PagedResources<Member> findAll() {
		return findAll(new PageRequest(0, 20));
	}

	Resource<Member> findOne(String memberId);

	PagedResources<Member> findByIds(String... ids);

	Resource<Member> findByEmail(String email);

	interface Async {
		CompletableFuture<PagedResources<Member>> findAll(Pageable pageable);

		default CompletableFuture<PagedResources<Member>> findAll() {
			return findAll(new PageRequest(0, 20));
		}

		CompletableFuture<Resource<Member>> findOne(String memberId);

		CompletableFuture<PagedResources<Member>> findByIds(String... ids);

		CompletableFuture<Resource<Member>> findByEmail(String email);
	}
}
