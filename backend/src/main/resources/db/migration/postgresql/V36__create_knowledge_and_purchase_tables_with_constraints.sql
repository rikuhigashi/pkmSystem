CREATE TABLE knowledge
(
    id             BIGSERIAL PRIMARY KEY,
    title          VARCHAR(255)          NOT NULL,
    content        TEXT                  NULL,
    user_id        INT                   NULL,
    price          DOUBLE PRECISION      NOT NULL,
    is_encrypted   BOOLEAN               NOT NULL,
    purchase_count INT                   NOT NULL,
    created_at     TIMESTAMP             NULL,
    updated_at     TIMESTAMP             NULL,
    status         VARCHAR(255)          NULL,
    CONSTRAINT fk_knowledge_user FOREIGN KEY (user_id) REFERENCES "user" (id)
);

CREATE TABLE knowledge_purchase
(
    id               BIGSERIAL PRIMARY KEY,
    knowledge_id     BIGINT                NOT NULL,
    user_id          INT                   NOT NULL,
    payment_order_id BIGINT                NOT NULL,
    purchased_at     TIMESTAMP             NULL,
    CONSTRAINT fk_knowledge_purchase_knowledge FOREIGN KEY (knowledge_id) REFERENCES knowledge (id),
    CONSTRAINT fk_knowledge_purchase_user FOREIGN KEY (user_id) REFERENCES "user" (id),
    CONSTRAINT fk_knowledge_purchase_payment_order FOREIGN KEY (payment_order_id) REFERENCES payment_order (id),
    CONSTRAINT uc_knowledge_purchase_payment_order UNIQUE (payment_order_id)
);

CREATE TABLE knowledge_tags
(
    knowledge_id BIGINT       NOT NULL,
    tag          VARCHAR(255) NULL,
    CONSTRAINT fk_knowledge_tags_on_knowledge FOREIGN KEY (knowledge_id) REFERENCES knowledge (id)
);

-- Add foreign key constraints for other referenced tables
ALTER TABLE knowledge_purchase
    ADD CONSTRAINT fk_knowledge_purchase_on_knowledge FOREIGN KEY (knowledge_id) REFERENCES knowledge (id);

ALTER TABLE knowledge_purchase
    ADD CONSTRAINT fk_knowledge_purchase_on_payment_order FOREIGN KEY (payment_order_id) REFERENCES payment_order (id);

ALTER TABLE knowledge_purchase
    ADD CONSTRAINT fk_knowledge_purchase_on_user FOREIGN KEY (user_id) REFERENCES "user" (id);

ALTER TABLE payment_order
    ADD CONSTRAINT fk_payment_order_on_knowledge FOREIGN KEY (knowledge_id) REFERENCES knowledge (id);
