CREATE TABLE IF NOT EXISTS category (
    id SERIAL NOT NULL PRIMARY KEY,
    domain_code TEXT NOT NULL,
    code TEXT NOT NULL,
    label TEXT NOT NULL,
    image TEXT NOT NULL,
    created_on TIMESTAMPTZ NOT NULL 
);


ALTER TABLE category ADD CONSTRAINT unique_domain_code UNIQUE (domain_code, code);
