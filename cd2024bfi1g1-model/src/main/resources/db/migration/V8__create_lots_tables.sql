CREATE TABLE public.lots (
       lot_id serial NOT NULL,
       lot_name varchar NOT NULL,
       usr_id int4 NOT NULL,
       CONSTRAINT lot_pk PRIMARY KEY (lot_id),
       CONSTRAINT lot_usr_user_fk FOREIGN KEY (usr_id) REFERENCES public.usr_user(usr_id)
);

ALTER TABLE public.containers ADD lot_id int4;
ALTER TABLE public.containers ADD CONSTRAINT containers_lots_fk FOREIGN KEY (lot_id) REFERENCES public.lots(lot_id);

ALTER TABLE public.measurements ADD lot_id int4;
ALTER TABLE public.measurements ADD CONSTRAINT measurements_lots_fk FOREIGN KEY (lot_id) REFERENCES public.lots(lot_id);

ALTER TABLE public.measurements ADD cnt_id int4;
ALTER TABLE public.measurements ADD CONSTRAINT measurements_containers_fk FOREIGN KEY (cnt_id) REFERENCES public.containers(cnt_id);