UPDATE public.usr_role
SET rol_json_client_permission='{ "menu": [{ "attr": "contenedores", "visible": false, "enabled": false }, { "attr": "devices", "visible": false, "enabled": false }] }'
WHERE rol_id=1;