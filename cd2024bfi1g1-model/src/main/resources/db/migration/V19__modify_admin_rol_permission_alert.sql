UPDATE public.usr_role
SET rol_json_client_permission='{ "menu": [{ "attr": "contenedores", "visible": false, "enabled": false }, { "attr": "devices", "visible": false, "enabled": false }, { "attr": "lots", "visible": false, "enabled": false }, { "attr": "locations", "visible": false, "enabled": false }, { "attr": "vehicles", "visible": false, "enabled": false }, { "attr": "transports", "visible": false, "enabled": false }, { "attr": "alerts", "visible": false, "enabled": false } ] }'
WHERE rol_id=1;