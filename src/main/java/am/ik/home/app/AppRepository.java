package am.ik.home.app;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface AppRepository extends CrudRepository<App, String> {
    Optional<App> findByAppId(String appId);

    Optional<App> findByAppName(String appName);

    Optional<App> findByAppUrl(String appUrl);
}
