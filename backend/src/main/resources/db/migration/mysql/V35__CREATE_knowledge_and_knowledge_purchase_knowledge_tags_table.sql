CREATE TABLE knowledge
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    title          VARCHAR(255)          NOT NULL,
    content        TEXT                  NULL,
    user_id        INT                   NULL,
    price          DOUBLE                NOT NULL,
    is_encrypted   BIT(1)                NOT NULL,
    purchase_count INT                   NOT NULL,
    created_at     datetime              NULL,
    updated_at     datetime              NULL,
    status         VARCHAR(255)          NULL,
    CONSTRAINT pk_knowledge PRIMARY KEY (id)
);

CREATE TABLE knowledge_purchase
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    knowledge_id BIGINT                NULL,
    user_id      INT                   NULL,
    purchased_at datetime              NULL,
    CONSTRAINT pk_knowledge_purchase PRIMARY KEY (id)
);

CREATE TABLE knowledge_tags
(
    knowledge_id BIGINT       NOT NULL,
    tag          VARCHAR(255) NULL
);

ALTER TABLE knowledge
    ADD CONSTRAINT FK_KNOWLEDGE_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE knowledge_purchase
    ADD CONSTRAINT FK_KNOWLEDGE_PURCHASE_ON_KNOWLEDGE FOREIGN KEY (knowledge_id) REFERENCES knowledge (id);

ALTER TABLE knowledge_purchase
    ADD CONSTRAINT FK_KNOWLEDGE_PURCHASE_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE knowledge_tags
    ADD CONSTRAINT fk_knowledge_tags_on_knowledge FOREIGN KEY (knowledge_id) REFERENCES knowledge (id);