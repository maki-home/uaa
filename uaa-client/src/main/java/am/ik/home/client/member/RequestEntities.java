package am.ik.home.client.member;

import static am.ik.home.client.member.PageableUtils.withPageable;
import static org.springframework.http.RequestEntity.get;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

import org.springframework.data.domain.Pageable;
import org.springframework.http.RequestEntity;
import org.springframework.web.util.UriComponentsBuilder;

class RequestEntities {
	static RequestEntity<Void> findAll(String apiBase, Pageable pageable) {
		UriComponentsBuilder builder = fromHttpUrl(apiBase).pathSegment("v1", "members");
		return get(withPageable(builder, pageable).build().encode().toUri()).build();
	}

	static RequestEntity<Void> findOne(String apiBase, String memberId) {
		return get(fromHttpUrl(apiBase).pathSegment("v1", "members", memberId).build()
				.encode().toUri()).build();
	}

	static RequestEntity<Void> findByIds(String apiBase, String... ids) {
		return get(
				fromHttpUrl(apiBase).pathSegment("v1", "members", "search", "findByIds")
						.queryParam("ids", (Object[]) ids).build().encode().toUri())
								.build();
	}

	static RequestEntity<Void> findByEmail(String apiBase, String email) {
		return get(
				fromHttpUrl(apiBase).pathSegment("v1", "members", "search", "findByEmail")
						.queryParam("email", email).build().encode().toUri()).build();
	}
}
