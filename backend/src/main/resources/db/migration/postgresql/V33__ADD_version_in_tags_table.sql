CREATE TABLE tags
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    user_id    INT NOT NULL,
    created_at TIMESTAMP NULL,
    CONSTRAINT fk_tags_on_user FOREIGN KEY (user_id) REFERENCES "user" (id)
);

ALTER TABLE sidedata
    ADD COLUMN tag_id INT NULL;

ALTER TABLE sidedata
    ADD CONSTRAINT fk_sidedata_on_tag FOREIGN KEY (tag_id) REFERENCES tags (id);

ALTER TABLE tags
    ADD CONSTRAINT fk_tags_on_user FOREIGN KEY (user_id) REFERENCES "user" (id);
