package am.ik.home.client.member;

import org.springframework.data.domain.Pageable;
import org.springframework.web.util.UriComponentsBuilder;

class PageableUtils {

	static UriComponentsBuilder withPageable(UriComponentsBuilder builder,
			Pageable pageable) {
		builder.queryParam("page", pageable.getPageNumber()).queryParam("size",
				pageable.getPageSize());
		if (pageable.getSort() != null) {
			StringBuilder sb = new StringBuilder();
			pageable.getSort().forEach(order -> {
				sb.append(order.getProperty()).append(",").append(order.getDirection());
			});
			builder.queryParam("sort", sb.toString());
		}
		return builder;
	}
}
