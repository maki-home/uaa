package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

public class V6__add_actuator_role implements SpringJdbcMigration {
	@Override
	public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
		Long count = jdbcTemplate.queryForObject(
				"SELECT count(*) FROM member WHERE member_id='00000000-0000-0000-0000-000000000000'",
				Long.class);
		if (count == 1) {
			jdbcTemplate.update(
					"INSERT INTO member_roles (member_member_id, roles) VALUES ('00000000-0000-0000-0000-000000000000', 'ACTUATOR');");
		}
	}
}