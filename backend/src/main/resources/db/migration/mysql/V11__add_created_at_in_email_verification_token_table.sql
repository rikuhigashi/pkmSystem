ALTER TABLE email_verification_token
    ADD created_at datetime NULL;

ALTER TABLE email_verification_token
    MODIFY created_at datetime NOT NULL;