ALTER TABLE user
    ADD email_verified BIT(1) DEFAULT 0 NULL;

ALTER TABLE user
    MODIFY email_verified BIT(1) NOT NULL;