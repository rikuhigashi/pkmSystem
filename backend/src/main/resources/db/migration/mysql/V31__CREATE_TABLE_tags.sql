CREATE TABLE tags
(
    id         INT AUTO_INCREMENT NOT NULL,
    name       VARCHAR(255)       NOT NULL,
    user_id    INT                NOT NULL,
    created_at datetime           NULL,
    CONSTRAINT pk_tags PRIMARY KEY (id)
);

ALTER TABLE sidedata
    ADD tag_id INT NULL;

ALTER TABLE sidedata
    ADD CONSTRAINT FK_SIDEDATA_ON_TAG FOREIGN KEY (tag_id) REFERENCES tags (id);

ALTER TABLE tags
    ADD CONSTRAINT FK_TAGS_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);