CREATE SCHEMA IF NOT EXISTS recycle;

SET search_path TO recycle;

CREATE TABLE IF NOT EXISTS "user" (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_login    VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    user_name     VARCHAR(255) NOT NULL,
    user_role     VARCHAR(50)  NOT NULL,
    created_at    TIMESTAMP,
    updated_at    TIMESTAMP
);

CREATE TABLE IF NOT EXISTS category (
    id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS recycling_point_type (
    id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS recycling_point (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(255),
    type_id      UUID REFERENCES recycling_point_type(id),
    address      VARCHAR(255),
    latitude     DOUBLE PRECISION,
    longitude    DOUBLE PRECISION,
    phone_number VARCHAR(255),
    email        VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS recycling_point_category (
    recycling_point_id UUID REFERENCES recycling_point(id),
    category_id        UUID REFERENCES category(id),
    PRIMARY KEY (recycling_point_id, category_id)
);

CREATE TABLE IF NOT EXISTS media (
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    file_path VARCHAR(255),
    mime_type VARCHAR(255),
    name      VARCHAR(255),
    size      INTEGER
);

CREATE TABLE IF NOT EXISTS advertisement (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title       VARCHAR(255)   NOT NULL,
    description TEXT           NOT NULL,
    price       NUMERIC(19, 2) NOT NULL,
    address     VARCHAR(255),
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    user_id     UUID NOT NULL REFERENCES "user"(id)
);

CREATE TABLE IF NOT EXISTS advertisement_category (
    advertisement_id UUID REFERENCES advertisement(id),
    category_id      UUID REFERENCES category(id),
    PRIMARY KEY (advertisement_id, category_id)
);

CREATE TABLE IF NOT EXISTS advertisement_media (
    advertisement_id UUID REFERENCES advertisement(id),
    media_id         UUID REFERENCES media(id),
    PRIMARY KEY (advertisement_id, media_id)
);
