DROP SCHEMA IF EXISTS "planner" CASCADE;

CREATE SCHEMA "planner";
SET search_path TO "planner";

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- WORKERS

DROP TABLE IF EXISTS workers CASCADE;

CREATE TABLE workers
(
    id         uuid    NOT NULL,
    first_name varchar NOT NULL,
    last_name  varchar NOT NULL,
    email      varchar,
    version    bigint,
    CONSTRAINT workers_pkey PRIMARY KEY (id)
);

-- SHIFTS

DROP TYPE IF EXISTS shift_type;
CREATE TYPE shift_type AS ENUM ('DAY', 'MID', 'NIGHT');

DROP TABLE IF EXISTS shifts CASCADE;

CREATE TABLE shifts
(
    id        uuid       NOT NULL,
    worker_id uuid       NOT NULL,
    type      shift_type NOT NULL,
    day       date       NOT NULL,
    CONSTRAINT shifts_pkey PRIMARY KEY (id)
);

ALTER TABLE shifts
    ADD CONSTRAINT "FK_WORKER_ID" FOREIGN KEY (worker_id)
        REFERENCES workers (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE;
