package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

public class V8__add_credhub_app implements SpringJdbcMigration {
	@Override
	public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
		Long count = jdbcTemplate.queryForObject(
				"SELECT count(*) FROM app WHERE app_name='CredHub' or app_url='http://localhost:9000'",
				Long.class);
		if (count == 0) {
			jdbcTemplate.update(
					"INSERT INTO app (access_token_validity_seconds, app_name, app_secret, app_url, refresh_token_validity_seconds, app_id) VALUES (1800, 'CredHub', '00000000-0000-0000-0000-00000000000a', 'http://localhost:9000', 259200, '00000000-0000-0000-0000-00000000000a')");
			jdbcTemplate.update(
					"INSERT INTO app_grant_types (app_app_id, grant_types) VALUES ('00000000-0000-0000-0000-00000000000a', 'PASSWORD')");
			jdbcTemplate.update(
					"INSERT INTO app_grant_types (app_app_id, grant_types) VALUES ('00000000-0000-0000-0000-00000000000a', 'AUTHORIZATION_CODE')");
			jdbcTemplate.update(
					"INSERT INTO app_grant_types (app_app_id, grant_types) VALUES ('00000000-0000-0000-0000-00000000000a', 'REFRESH_TOKEN')");
			jdbcTemplate.update(
					"INSERT INTO app_redirect_urls (app_app_id, redirect_urls) VALUES ('00000000-0000-0000-0000-00000000000a', 'http://localhost:9000/login')");
			jdbcTemplate.update(
					"INSERT INTO app_roles (app_app_id, roles) VALUES ('00000000-0000-0000-0000-00000000000a', 'CLIENT')");
			jdbcTemplate.update(
					"INSERT INTO app_scopes (app_app_id, scopes) VALUES ('00000000-0000-0000-0000-00000000000a', 'credhub.write');");
			jdbcTemplate.update(
					"INSERT INTO app_scopes (app_app_id, scopes) VALUES ('00000000-0000-0000-0000-00000000000a', 'credhub.read');");
			jdbcTemplate.update(
					"INSERT INTO app_grant_types (app_app_id, grant_types) VALUES ('00000000-0000-0000-0000-00000000000a', 'REFRESH_TOKEN')");
			jdbcTemplate.update(
					"INSERT INTO app_resource_ids (app_app_id, resource_ids) VALUES ('00000000-0000-0000-0000-00000000000a', 'credhub');");
		}
	}
}