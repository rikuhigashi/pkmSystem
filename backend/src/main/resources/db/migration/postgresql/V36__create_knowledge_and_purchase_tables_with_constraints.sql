CREATE TABLE knowledge
(
    id BIGSERIAL PRIMARY KEY,
    title          VARCHAR(255)     NOT NULL,
    content        TEXT,
    user_id        INT REFERENCES "user" (id),
    price          DOUBLE PRECISION NOT NULL DEFAULT 0,
    is_encrypted   BOOLEAN          NOT NULL DEFAULT false,
    purchase_count INT              NOT NULL DEFAULT 0,
    created_at     TIMESTAMP                 DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP,
    status         VARCHAR(20)      NOT NULL DEFAULT 'PUBLISHED'
);

CREATE TABLE knowledge_tags
(
    knowledge_id BIGINT REFERENCES knowledge (id) ON DELETE CASCADE,
    tag          VARCHAR(255)
);

CREATE TABLE knowledge_purchase
(
    id BIGSERIAL PRIMARY KEY,
    knowledge_id     BIGINT NOT NULL REFERENCES knowledge (id),
    user_id          INT    NOT NULL REFERENCES "user" (id),
    payment_order_id BIGINT NOT NULL REFERENCES payment_order (id),
    purchased_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uc_knowledge_purchase_payment_order UNIQUE (payment_order_id)
);

ALTER TABLE payment_order
    ADD COLUMN knowledge_id BIGINT REFERENCES knowledge (id);