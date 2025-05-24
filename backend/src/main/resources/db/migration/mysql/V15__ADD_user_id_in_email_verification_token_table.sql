ALTER TABLE email_verification_token
    ADD user_id INT NULL;

ALTER TABLE email_verification_token
    ADD CONSTRAINT uc_email_verification_token_user UNIQUE (user_id);

ALTER TABLE email_verification_token
    ADD CONSTRAINT FK_EMAIL_VERIFICATION_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);