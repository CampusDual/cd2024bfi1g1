CREATE TABLE public.transfers (
	tra_id serial NOT NULL,
	cl_id_origin int4 NOT NULL,
    cl_id_destiny int4 NOT NULL,
	CONSTRAINT transfers_pk PRIMARY KEY (tra_id),
    CONSTRAINT tranfers_origin_containers_lots_fk FOREIGN KEY (cl_id_origin) REFERENCES public.containers_lots(cl_id),
    CONSTRAINT tranfers_destiny_containers_lots_fk FOREIGN KEY (cl_id_destiny) REFERENCES public.containers_lots(cl_id)
);