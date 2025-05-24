CREATE TABLE password_reset_tokens
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    token           VARCHAR(255)          NOT NULL,
    user_id         INT                   NULL,
    expiration_time datetime              NOT NULL,
    CONSTRAINT pk_password_reset_tokens PRIMARY KEY (id)
);

ALTER TABLE password_reset_tokens
    ADD CONSTRAINT uc_password_reset_tokens_token UNIQUE (token);

ALTER TABLE password_reset_tokens
    ADD CONSTRAINT uc_password_reset_tokens_user UNIQUE (user_id);

ALTER TABLE password_reset_tokens
    ADD CONSTRAINT FK_PASSWORD_RESET_TOKENS_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);