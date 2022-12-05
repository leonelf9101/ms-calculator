DROP TABLE IF EXISTS percentage_auditory;
DROP TABLE IF EXISTS authorities;
DROP TABLE IF EXISTS users;

CREATE TABLE percentage_auditory (id SERIAL PRIMARY KEY, response_body varchar NOT NULL, response_status integer NOT NULL, elapsed_time integer NOT NULL, created_at timestamp NOT NULL, uri varchar NOT NULL);
create table users (id SERIAL PRIMARY KEY, enabled boolean not null, password varchar NOT NULL, username varchar NOT NULL);
create table authorities (id SERIAL PRIMARY KEY, authority varchar, user_id INTEGER NOT NULL);

alter table users add constraint users_username_unique unique (username);
alter table authorities add constraint authorities_user_id_authority_unique unique (user_id, authority);
ALTER TABLE percentage_auditory ALTER COLUMN created_at SET DEFAULT now();