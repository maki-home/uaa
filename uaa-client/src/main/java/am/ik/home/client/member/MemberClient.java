package am.ik.home.client.member;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.util.concurrent.ListenableFuture;

public interface MemberClient {
	PagedResources<Member> findAll(Pageable pageable);

	default PagedResources<Member> findAll() {
		return findAll(new PageRequest(0, 20));
	}

	Resource<Member> findOne(String memberId);

	PagedResources<Member> findByIds(String... ids);

	Resource<Member> findByEmail(String email);

	interface Async {
		ListenableFuture<PagedResources<Member>> findAll(Pageable pageable);

		default ListenableFuture<PagedResources<Member>> findAll() {
			return findAll(new PageRequest(0, 20));
		}

		ListenableFuture<Resource<Member>> findOne(String memberId);

		ListenableFuture<PagedResources<Member>> findByIds(String... ids);

		ListenableFuture<Resource<Member>> findByEmail(String email);
	}
}
