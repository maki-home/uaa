package am.ik.home.client.member;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;

class TypeReferences {

	final static ParameterizedTypeReference<Resource<Member>> memberResourceType = new ParameterizedTypeReference<Resource<Member>>() {
	};
	final static ParameterizedTypeReference<PagedResources<Member>> memberResourcesType = new ParameterizedTypeReference<PagedResources<Member>>() {
	};
}
