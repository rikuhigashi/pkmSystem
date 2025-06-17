-- 确保所有表名使用小写和下划线
CREATE TABLE IF NOT EXISTS knowledge_purchase (
                                                  id               BIGSERIAL PRIMARY KEY,
                                                  knowledge_id     BIGINT                NOT NULL,
                                                  user_id          INTEGER               NOT NULL,
                                                  payment_order_id BIGINT                NOT NULL,
                                                  purchased_at     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 添加显式外键约束（使用带下划线的表名）
ALTER TABLE knowledge_purchase
    ADD CONSTRAINT fk_knowledge_purchase_knowledge
        FOREIGN KEY (knowledge_id) REFERENCES knowledge (id);

ALTER TABLE knowledge_purchase
    ADD CONSTRAINT fk_knowledge_purchase_user
        FOREIGN KEY (user_id) REFERENCES "user" (id);

ALTER TABLE knowledge_purchase
    ADD CONSTRAINT fk_knowledge_purchase_payment_order
        FOREIGN KEY (payment_order_id) REFERENCES payment_order (id);

-- 添加唯一约束
ALTER TABLE knowledge_purchase
    ADD CONSTRAINT uc_knowledge_purchase_payment_order
        UNIQUE (payment_order_id);