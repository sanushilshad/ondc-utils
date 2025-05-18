
CREATE TABLE IF NOT EXISTS domain (
    id SERIAL NOT NULL PRIMARY KEY,
    code TEXT NOT NULL,
    label TEXT NOT NULL,
    image TEXT NOT NULL,
    created_on TIMESTAMPTZ NOT NULL 
);

ALTER TABLE domain ADD CONSTRAINT uq_domain UNIQUE (code);


CREATE TABLE IF NOT EXISTS category (
    id SERIAL NOT NULL PRIMARY KEY,
    domain_id INTEGER NOT NULL,
    code TEXT NOT NULL,
    label TEXT NOT NULL,
    image TEXT NOT NULL,
    created_on TIMESTAMPTZ NOT NULL 
);


ALTER TABLE category ADD CONSTRAINT uq_category UNIQUE (domain_id, code);
ALTER TABLE category ADD CONSTRAINT fk_category_domain FOREIGN KEY (domain_id) REFERENCES domain(id) ON DELETE CASCADE;


CREATE TABLE IF NOT EXISTS state (
    id SERIAL NOT NULL PRIMARY KEY,
    label TEXT NOT NULL,
    code TEXT NOT NULL,
    country_code TEXT NOT NULL,
    created_on TIMESTAMPTZ NOT NULL 
);
ALTER TABLE state ADD CONSTRAINT uq_state UNIQUE (country_code, code);