package am.ik.home.cloudfoundry.broker;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class UaaCatalogFactoryBean implements FactoryBean<Catalog> {
	@Value("${servicebroker.catalog-json:classpath:catalog.json}")
	Resource catalog;
	@Autowired
	ObjectMapper objectMapper;

	@Override
	public Catalog getObject() throws Exception {
		Catalog catalog = objectMapper.readValue(this.catalog.getInputStream(),
				Catalog.class);
		return catalog;
	}

	@Override
	public Class<?> getObjectType() {
		return Catalog.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
