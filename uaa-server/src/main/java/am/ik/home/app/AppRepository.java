package am.ik.home.app;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface AppRepository extends JpaRepository<App, String> {
	Optional<App> findByAppId(String appId);

	Optional<App> findByAppName(String appName);

	Optional<App> findByAppUrl(String appUrl);
}
