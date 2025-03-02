CREATE TABLE public.lots (
        lot_id serial NOT NULL,
        lot_name varchar NOT NULL,
        cmp_id int4 NOT NULL,
        CONSTRAINT lot_pk PRIMARY KEY (lot_id),
        CONSTRAINT lot_company_fk FOREIGN KEY (cmp_id) REFERENCES public.companies(cmp_id)
);

Alter TABLE public.containers ADD lot_id int4;
ALTER TABLE public.containers ADD CONSTRAINT containers_lots_fk FOREIGN KEY (lot_id) REFERENCES public.lots(lot_id);

ALTER TABLE public.measurements ADD lot_id int4;
ALTER TABLE public.measurements ADD CONSTRAINT measurements_lots_fk FOREIGN KEY (lot_id) REFERENCES public.lots(lot_id);

ALTER TABLE public.measurements ADD cnt_id int4;
ALTER TABLE public.measurements ADD CONSTRAINT measurements_containers_fk FOREIGN KEY (cnt_id) REFERENCES public.containers(cnt_id);

ALTER TABLE public.containers DROP CONSTRAINT containers_usr_user_fk;
ALTER TABLE public.containers DROP COLUMN usr_id;

Alter TABLE public.containers ADD cmp_id int4;
ALTER TABLE public.containers ADD CONSTRAINT containers_companies_fk FOREIGN KEY (cmp_id) REFERENCES public.companies(cmp_id);