CREATE TABLE knowledge
(
    id BIGSERIAL PRIMARY KEY,
    title          VARCHAR(255)     NOT NULL,
    content        TEXT             NULL,
    user_id        INTEGER          NULL,
    price          DOUBLE PRECISION NOT NULL,
    is_encrypted   BOOLEAN          NOT NULL,
    purchase_count INTEGER          NOT NULL,
    created_at TIMESTAMPTZ NULL,
    updated_at TIMESTAMPTZ NULL,
    status         VARCHAR(255)     NULL
);

CREATE TABLE knowledge_purchase
(
    id BIGSERIAL PRIMARY KEY,
    knowledge_id BIGINT  NULL,
    user_id      INTEGER NULL,
    purchased_at TIMESTAMPTZ NULL
);

CREATE TABLE knowledge_tags
(
    knowledge_id BIGINT       NOT NULL,
    tag          VARCHAR(255) NULL
);

ALTER TABLE knowledge
    ADD CONSTRAINT FK_KNOWLEDGE_ON_USER FOREIGN KEY (user_id) REFERENCES "user" (id);

ALTER TABLE knowledge_purchase
    ADD CONSTRAINT FK_KNOWLEDGE_PURCHASE_ON_KNOWLEDGE FOREIGN KEY (knowledge_id) REFERENCES knowledge (id);

ALTER TABLE knowledge_purchase
    ADD CONSTRAINT FK_KNOWLEDGE_PURCHASE_ON_USER FOREIGN KEY (user_id) REFERENCES "user" (id);

ALTER TABLE knowledge_tags
    ADD CONSTRAINT fk_knowledge_tags_on_knowledge FOREIGN KEY (knowledge_id) REFERENCES knowledge (id);
