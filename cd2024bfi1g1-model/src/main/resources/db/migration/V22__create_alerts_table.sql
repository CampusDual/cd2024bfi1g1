CREATE TABLE public.alerts (
	alt_id serial NOT NULL,
	alt_date_init timestamp DEFAULT now() NULL,
	alt_date_end timestamp NULL,
	CONSTRAINT alerts_pk PRIMARY KEY (alt_id)
);

ALTER TABLE public.alerts ADD alt_min_temp float8 NULL;
ALTER TABLE public.alerts ADD alt_max_temp float8 NULL;

ALTER TABLE public.alerts ADD cl_id int4 NULL;
ALTER TABLE public.alerts ADD CONSTRAINT alerts_containers_lots_fk FOREIGN KEY (cl_id) REFERENCES public.containers_lots(cl_id);

ALTER TABLE public.measurements ADD alt_id int4 NULL;
ALTER TABLE public.measurements ADD CONSTRAINT measurements_alerts_fk FOREIGN KEY (alt_id) REFERENCES public.alerts(alt_id);

ALTER TABLE public.alerts ADD dev_id int4 NULL;
ALTER TABLE public.alerts ADD CONSTRAINT alerts_devices_fk FOREIGN KEY (dev_id) REFERENCES public.devices(dev_id);