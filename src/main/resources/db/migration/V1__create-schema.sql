CREATE TABLE IF NOT EXISTS app (
  app_id                         VARCHAR(36)  NOT NULL,
  access_token_validity_seconds  INTEGER      NOT NULL CHECK (access_token_validity_seconds >= 0 AND
                                                              access_token_validity_seconds <= 600000),
  app_name                       VARCHAR(255) NOT NULL UNIQUE,
  app_secret                     VARCHAR(255) NOT NULL,
  app_url                        VARCHAR(255) NOT NULL UNIQUE,
  refresh_token_validity_seconds INTEGER      NOT NULL CHECK (refresh_token_validity_seconds >= 0 AND
                                                              refresh_token_validity_seconds <= 600000),
  PRIMARY KEY (app_id)
);
CREATE TABLE IF NOT EXISTS app_auto_approve_scopes (
  app_app_id          VARCHAR(36)  NOT NULL,
  auto_approve_scopes VARCHAR(255) NOT NULL,
  FOREIGN KEY (app_app_id) REFERENCES app (app_id)
);
CREATE TABLE IF NOT EXISTS app_grant_types (
  app_app_id  VARCHAR(36)  NOT NULL,
  grant_types VARCHAR(255) NOT NULL,
  FOREIGN KEY (app_app_id) REFERENCES app (app_id)
);
CREATE TABLE IF NOT EXISTS app_redirect_urls (
  app_app_id    VARCHAR(36)  NOT NULL,
  redirect_urls VARCHAR(255) NOT NULL,
  FOREIGN KEY (app_app_id) REFERENCES app (app_id)
);
CREATE TABLE IF NOT EXISTS app_roles (
  app_app_id VARCHAR(36)  NOT NULL,
  roles      VARCHAR(255) NOT NULL,
  FOREIGN KEY (app_app_id) REFERENCES app (app_id)
);
CREATE TABLE IF NOT EXISTS app_scopes (
  app_app_id VARCHAR(36)  NOT NULL,
  scopes     VARCHAR(255) NOT NULL,
  FOREIGN KEY (app_app_id) REFERENCES app (app_id)
);
CREATE TABLE IF NOT EXISTS member (
  member_id   VARCHAR(36)  NOT NULL,
  email       VARCHAR(255) NOT NULL UNIQUE,
  family_name VARCHAR(255) NOT NULL,
  given_name  VARCHAR(255) NOT NULL,
  password    VARCHAR(255) NOT NULL,
  PRIMARY KEY (member_id)
);
CREATE TABLE IF NOT EXISTS member_roles (
  member_member_id VARCHAR(36)  NOT NULL,
  roles            VARCHAR(255) NOT NULL,
  FOREIGN KEY (member_member_id) REFERENCES member (member_id)
);