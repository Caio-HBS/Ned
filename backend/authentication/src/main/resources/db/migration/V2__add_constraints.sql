ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_uniquelocalidentification UNIQUE (unique_local_identification);

ALTER TABLE address
    ADD CONSTRAINT fk_address_on_user FOREIGN KEY (user_id) REFERENCES users (user_id);

ALTER TABLE user_tokens
    ADD CONSTRAINT fk_user_on_user_tokens FOREIGN KEY (user_id) REFERENCES users (user_id)