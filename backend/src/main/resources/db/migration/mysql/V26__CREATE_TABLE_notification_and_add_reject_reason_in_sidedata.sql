CREATE TABLE notification
(
    id              INT AUTO_INCREMENT NOT NULL,
    user_id         INT                NULL,
    content         VARCHAR(255)       NULL,
    `read`          BIT(1)             NOT NULL,
    created_at      datetime           NULL,
    expire_at       datetime           NULL,
    related_data_id INT                NULL,
    CONSTRAINT pk_notification PRIMARY KEY (id)
);

ALTER TABLE sidedata
    ADD reject_reason TEXT NULL;

ALTER TABLE notification
    ADD CONSTRAINT FK_NOTIFICATION_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);