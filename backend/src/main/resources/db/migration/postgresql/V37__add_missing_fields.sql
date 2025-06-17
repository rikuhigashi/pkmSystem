-- 为user表添加balance字段
ALTER TABLE "user"
    ADD COLUMN balance DECIMAL(10, 2) NOT NULL DEFAULT 0;

-- 为payment_order表添加payload字段
ALTER TABLE payment_order
    ADD COLUMN payload TEXT;

-- 为payment_order表添加knowledge_id字段
ALTER TABLE payment_order
    ADD COLUMN knowledge_id BIGINT;

-- 为knowledge表添加purchase_count默认值
ALTER TABLE knowledge
    ALTER COLUMN purchase_count SET DEFAULT 0;