CREATE TABLE email_verification_token
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    token           VARCHAR(255)          NOT NULL,
    user_id         INT                   NULL,
    expiration_time datetime              NOT NULL,
    CONSTRAINT pk_email_verification_token PRIMARY KEY (id)
);

ALTER TABLE email_verification_token
    ADD CONSTRAINT uc_email_verification_token_token UNIQUE (token);

ALTER TABLE email_verification_token
    ADD CONSTRAINT uc_email_verification_token_user UNIQUE (user_id);

ALTER TABLE email_verification_token
    ADD CONSTRAINT FK_EMAIL_VERIFICATION_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);