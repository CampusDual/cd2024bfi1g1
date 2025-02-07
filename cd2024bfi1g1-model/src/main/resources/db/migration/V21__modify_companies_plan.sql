INSERT INTO plans (pln_id, pln_name)
VALUES(1,'BASIC');

INSERT INTO public.pricing (prc_id,prc_fprc, prc_dprc, prc_bprc, prc_breq, prc_start, pln_id)
VALUES (1,4,5,20,1000,now(), 1);

ALTER TABLE public.companies ADD pln_id INTEGER;
ALTER table public.companies ADD CONSTRAINT companies_plans_fk FOREIGN KEY (pln_id) REFERENCES public.plans(pln_id);
ALTER TABLE public.companies ALTER COLUMN pln_id SET DEFAULT 1;
UPDATE companies
SET pln_id = 1
WHERE pln_id IS NULL;
