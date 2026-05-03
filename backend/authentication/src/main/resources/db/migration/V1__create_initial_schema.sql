CREATE TABLE users
(
    user_id                     BIGSERIAL PRIMARY KEY,
    email                       VARCHAR(100) UNIQUE NOT NULL,
    password                    VARCHAR(255)        NOT NULL,
    birthday                    DATE                NOT NULL,
    full_name                   VARCHAR(150)        NOT NULL,
    unique_local_identification VARCHAR(100) UNIQUE NOT NULL,
    phone_number                VARCHAR(20),
    role                        VARCHAR(20),
    created_at                  TIMESTAMP WITHOUT TIME ZONE,
    updated_at                  TIMESTAMP WITHOUT TIME ZONE,
    active                      BOOLEAN DEFAULT TRUE,
    account_non_locked          BOOLEAN DEFAULT FALSE
);

CREATE TABLE address
(
    address_id   BIGSERIAL PRIMARY KEY,
    street       VARCHAR(255),
    number       VARCHAR(20),
    zip_code     VARCHAR(20),
    city         VARCHAR(100),
    state        VARCHAR(50),
    main_address BOOLEAN DEFAULT FALSE,
    user_id      BIGINT NOT NULL,
    active       BOOLEAN DEFAULT TRUE
);

CREATE TABLE user_tokens
(
    token_id   BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    token_type VARCHAR(40),
    token      VARCHAR(255),
    active     BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    expires_at TIMESTAMP WITHOUT TIME ZONE,
    used_at    TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL
);