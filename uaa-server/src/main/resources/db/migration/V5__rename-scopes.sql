UPDATE app_scopes SET scopes='member.read' WHERE scopes='READ';
UPDATE app_scopes SET scopes='member.write' WHERE scopes='WRITE';
UPDATE app_scopes SET scopes='openid' WHERE scopes='OPENID';
UPDATE app_auto_approve_scopes SET auto_approve_scopes='member.read' WHERE auto_approve_scopes='READ';
UPDATE app_auto_approve_scopes SET auto_approve_scopes='member.write' WHERE auto_approve_scopes='WRITE';
UPDATE app_auto_approve_scopes SET auto_approve_scopes='openid' WHERE auto_approve_scopes='OPENID';