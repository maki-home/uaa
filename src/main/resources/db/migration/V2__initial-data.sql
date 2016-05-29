-- maki
INSERT INTO member (email, family_name, given_name, password, member_id) VALUES ('maki@example.com', 'Maki', 'Toshiaki',
                                                                                 '35dcdb23e786100735dcdb23e78610072e219bef26d4d503156c29471c2a11ce330ceb5dc620cc044b87695b3c5fae9a',
                                                                                 '00000000-0000-0000-0000-000000000000');
INSERT INTO member_roles (member_member_id, roles) VALUES ('00000000-0000-0000-0000-000000000000', 'USER');
INSERT INTO member_roles (member_member_id, roles) VALUES ('00000000-0000-0000-0000-000000000000', 'ADMIN');

-- demo
INSERT INTO member (email, family_name, given_name, password, member_id) VALUES ('demo@example.com', 'Demo', 'Taro',
                                                                                 '35dcdb23e786100735dcdb23e78610072e219bef26d4d503156c29471c2a11ce330ceb5dc620cc044b87695b3c5fae9a',
                                                                                 '00000000-0000-0000-0000-000000000001');
INSERT INTO member_roles (member_member_id, roles) VALUES ('00000000-0000-0000-0000-000000000001', 'USER');

-- moneyger
INSERT INTO app (access_token_validity_seconds, app_name, app_secret, app_url, refresh_token_validity_seconds, app_id)
VALUES (1800, 'Moneygr', '00000000-0000-0000-0000-000000000000', 'http://localhost:18080', 259200,
        '00000000-0000-0000-0000-000000000000');
INSERT INTO app_auto_approve_scopes (app_app_id, auto_approve_scopes)
VALUES ('00000000-0000-0000-0000-000000000000', 'READ');
INSERT INTO app_auto_approve_scopes (app_app_id, auto_approve_scopes)
VALUES ('00000000-0000-0000-0000-000000000000', 'WRITE');
INSERT INTO app_grant_types (app_app_id, grant_types)
VALUES ('00000000-0000-0000-0000-000000000000', 'AUTHORIZATION_CODE');
INSERT INTO app_grant_types (app_app_id, grant_types) VALUES ('00000000-0000-0000-0000-000000000000', 'IMPLICIT');
INSERT INTO app_grant_types (app_app_id, grant_types) VALUES ('00000000-0000-0000-0000-000000000000', 'PASSWORD');
INSERT INTO app_grant_types (app_app_id, grant_types) VALUES ('00000000-0000-0000-0000-000000000000', 'REFRESH_TOKEN');
INSERT INTO app_redirect_urls (app_app_id, redirect_urls)
VALUES ('00000000-0000-0000-0000-000000000000', 'http://localhost:18080/login');
INSERT INTO app_roles (app_app_id, roles) VALUES ('00000000-0000-0000-0000-000000000000', 'CLIENT');
INSERT INTO app_roles (app_app_id, roles) VALUES ('00000000-0000-0000-0000-000000000000', 'TRUSTED_CLIENT');
INSERT INTO app_scopes (app_app_id, scopes) VALUES ('00000000-0000-0000-0000-000000000000', 'READ');
INSERT INTO app_scopes (app_app_id, scopes) VALUES ('00000000-0000-0000-0000-000000000000', 'WRITE');

-- guest app
INSERT INTO app (access_token_validity_seconds, app_name, app_secret, app_url, refresh_token_validity_seconds, app_id)
VALUES (180, 'Guest App', '00000000-0000-0000-0000-000000000001', 'https://guest.example.com', 10800,
        '00000000-0000-0000-0000-000000000001');
INSERT INTO app_auto_approve_scopes (app_app_id, auto_approve_scopes)
VALUES ('00000000-0000-0000-0000-000000000001', 'READ');
INSERT INTO app_auto_approve_scopes (app_app_id, auto_approve_scopes)
VALUES ('00000000-0000-0000-0000-000000000001', 'WRITE');
INSERT INTO app_grant_types (app_app_id, grant_types)
VALUES ('00000000-0000-0000-0000-000000000001', 'AUTHORIZATION_CODE');
INSERT INTO app_grant_types (app_app_id, grant_types) VALUES ('00000000-0000-0000-0000-000000000001', 'IMPLICIT');
INSERT INTO app_grant_types (app_app_id, grant_types) VALUES ('00000000-0000-0000-0000-000000000001', 'PASSWORD');
INSERT INTO app_grant_types (app_app_id, grant_types) VALUES ('00000000-0000-0000-0000-000000000001', 'REFRESH_TOKEN');
INSERT INTO app_redirect_urls (app_app_id, redirect_urls)
VALUES ('00000000-0000-0000-0000-000000000001', 'https://guest.example.com/login');
INSERT INTO app_roles (app_app_id, roles) VALUES ('00000000-0000-0000-0000-000000000001', 'CLIENT');
INSERT INTO app_scopes (app_app_id, scopes) VALUES ('00000000-0000-0000-0000-000000000001', 'READ');

-- 3rd
INSERT INTO app (access_token_validity_seconds, app_name, app_secret, app_url, refresh_token_validity_seconds, app_id)
VALUES (180, '3rd App', '00000000-0000-0000-0000-000000000002', 'https://3rd.example.com', 10800,
        '00000000-0000-0000-0000-000000000002');
INSERT INTO app_grant_types (app_app_id, grant_types)
VALUES ('00000000-0000-0000-0000-000000000002', 'AUTHORIZATION_CODE');
INSERT INTO app_grant_types (app_app_id, grant_types) VALUES ('00000000-0000-0000-0000-000000000002', 'IMPLICIT');
INSERT INTO app_grant_types (app_app_id, grant_types) VALUES ('00000000-0000-0000-0000-000000000002', 'REFRESH_TOKEN');
INSERT INTO app_redirect_urls (app_app_id, redirect_urls)
VALUES ('00000000-0000-0000-0000-000000000002', 'https://3rd.example.com/login');
INSERT INTO app_roles (app_app_id, roles) VALUES ('00000000-0000-0000-0000-000000000002', 'CLIENT');
INSERT INTO app_scopes (app_app_id, scopes) VALUES ('00000000-0000-0000-0000-000000000002', 'READ');
INSERT INTO app_scopes (app_app_id, scopes) VALUES ('00000000-0000-0000-0000-000000000002', 'WRITE');