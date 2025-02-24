INSERT INTO usr_role (rol_id, rol_name, rol_xml_client_permission, rol_json_client_permission, rol_notes) VALUES(3, 'transporter', '<?xml version="1.0" encoding="UTF-8"?><security><MENU><ELEMENT attr="admin"><Enabled restricted="yes"/><Visible restricted="yes"/></ELEMENT></MENU></security>', '{"menu": [{ "attr": "contenedores", "visible": false, "enabled": false },{ "attr": "devices", "visible": false, "enabled": false },{ "attr": "lots", "visible": false, "enabled": false },{ "attr": "masters", "visible": false, "enabled": false },{ "attr": "transports", "visible": false, "enabled": false },{ "attr": "alerts", "visible": false, "enabled": false },{ "attr": "usage", "visible": false, "enabled": false },{ "attr": "users", "visible": false, "enabled": false },{ "attr": "companies", "visible": false, "enabled": false },{ "attr": "devices-without-users", "visible": false, "enabled": false },{ "attr": "medidas", "visible": false, "enabled": false },{ "attr": "consumptions", "visible": false, "enabled": false }]}', 'This is the Transporter role');

UPDATE public.usr_role
SET rol_json_client_permission='{"menu": [{ "attr": "contenedores", "visible": false, "enabled": false },{ "attr": "devices", "visible": false, "enabled": false },{ "attr": "lots", "visible": false, "enabled": false },{ "attr": "masters", "visible": false, "enabled": false },{ "attr": "transports", "visible": false, "enabled": false },{ "attr": "alerts", "visible": false, "enabled": false },{ "attr": "usage", "visible": false, "enabled": false }, { "attr": "transporters", "visible": false, "enabled": false }]}'
WHERE rol_id=1;
UPDATE public.usr_role
SET rol_json_client_permission='{"menu": [{ "attr": "users", "visible": false, "enabled": false },{ "attr": "companies", "visible": false, "enabled": false },{ "attr": "devices-without-users", "visible": false, "enabled": false },{ "attr": "medidas", "visible": false, "enabled": false },{ "attr": "consumptions", "visible": false, "enabled": false }, { "attr": "transporters", "visible": false, "enabled": false }]}'
WHERE rol_id=2;
