ALTER TABLE public.devices ADD dev_name varchar;
UPDATE public.devices SET dev_name = dev_mac WHERE dev_name is NULL;
ALTER TABLE public.devices ALTER COLUMN dev_name SET NOT NULL;