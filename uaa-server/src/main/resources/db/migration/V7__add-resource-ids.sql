CREATE TABLE IF NOT EXISTS app_resource_ids (
  app_app_id   VARCHAR(36)  NOT NULL,
  resource_ids VARCHAR(255) NOT NULL,
  FOREIGN KEY (app_app_id) REFERENCES app (app_id)
);
