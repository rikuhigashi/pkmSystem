-- 创建 'knowledge' 表
CREATE TABLE knowledge
(
    id             BIGINT AUTO_INCREMENT NOT NULL,   -- 主键
    title          VARCHAR(255)          NOT NULL,   -- 知识标题
    content        TEXT                  NULL,       -- 存储 Tiptap JSON 内容
    user_id        BIGINT                NULL,       -- 作者的外键，调整为 BIGINT，因为 user_id 是 Long 类型
    price          DOUBLE                NOT NULL,   -- 价格
    is_encrypted   BIT(1)                NOT NULL,   -- 是否加密
    purchase_count INT                   NOT NULL,   -- 购买次数
    created_at     DATETIME              NULL,       -- 创建时间
    updated_at     DATETIME              NULL,       -- 更新时间
    status         VARCHAR(255)          NULL,       -- 状态：草稿、已发布、已删除
    CONSTRAINT pk_knowledge PRIMARY KEY (id),      -- 主键约束
    CONSTRAINT FK_KNOWLEDGE_ON_USER FOREIGN KEY (user_id) REFERENCES user (id) -- 外键约束
);

-- 创建 'knowledge_purchase' 表
CREATE TABLE knowledge_purchase
(
    id               BIGINT AUTO_INCREMENT NOT NULL,    -- 主键
    knowledge_id     BIGINT                NOT NULL,    -- 知识 ID
    user_id          BIGINT                NOT NULL,    -- 用户 ID
    payment_order_id BIGINT                NOT NULL,    -- 支付订单 ID
    purchased_at     DATETIME              NULL,       -- 购买时间
    CONSTRAINT pk_knowledge_purchase PRIMARY KEY (id), -- 主键约束
    CONSTRAINT FK_KNOWLEDGE_PURCHASE_ON_KNOWLEDGE FOREIGN KEY (knowledge_id) REFERENCES knowledge (id), -- 外键约束到 'knowledge' 表
    CONSTRAINT FK_KNOWLEDGE_PURCHASE_ON_USER FOREIGN KEY (user_id) REFERENCES user (id), -- 外键约束到 'user' 表
    CONSTRAINT FK_KNOWLEDGE_PURCHASE_ON_PAYMENT_ORDER FOREIGN KEY (payment_order_id) REFERENCES payment_order (id), -- 外键约束到 'payment_order' 表
    CONSTRAINT uc_knowledge_purchase_payment_order UNIQUE (payment_order_id) -- 唯一约束
);

-- 创建 'knowledge_tags' 表
CREATE TABLE knowledge_tags
(
    knowledge_id BIGINT       NOT NULL,  -- 知识 ID
    tag          VARCHAR(255) NULL,       -- 标签
    CONSTRAINT fk_knowledge_tags_on_knowledge FOREIGN KEY (knowledge_id) REFERENCES knowledge (id) -- 外键约束到 'knowledge' 表
);

-- 修改 'knowledge_purchase' 表：添加唯一约束
ALTER TABLE knowledge_purchase
    ADD CONSTRAINT uc_knowledge_purchase_payment_order UNIQUE (payment_order_id);

-- 修改 'knowledge' 表：添加外键约束到 'user' 表
ALTER TABLE knowledge
    ADD CONSTRAINT FK_KNOWLEDGE_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

-- 修改 'knowledge_purchase' 表：添加外键约束到 'knowledge' 表
ALTER TABLE knowledge_purchase
    ADD CONSTRAINT FK_KNOWLEDGE_PURCHASE_ON_KNOWLEDGE FOREIGN KEY (knowledge_id) REFERENCES knowledge (id);

-- 修改 'knowledge_purchase' 表：添加外键约束到 'payment_order' 表
ALTER TABLE knowledge_purchase
    ADD CONSTRAINT FK_KNOWLEDGE_PURCHASE_ON_PAYMENT_ORDER FOREIGN KEY (payment_order_id) REFERENCES payment_order (id);

-- 修改 'knowledge_purchase' 表：添加外键约束到 'user' 表
ALTER TABLE knowledge_purchase
    ADD CONSTRAINT FK_KNOWLEDGE_PURCHASE_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

-- 修改 'payment_order' 表：添加外键约束到 'knowledge' 表
ALTER TABLE payment_order
    ADD CONSTRAINT FK_PAYMENT_ORDER_ON_KNOWLEDGE FOREIGN KEY (knowledge_id) REFERENCES knowledge (id);

-- 修改 'knowledge_tags' 表：添加外键约束到 'knowledge' 表
ALTER TABLE knowledge_tags
    ADD CONSTRAINT fk_knowledge_tags_on_knowledge FOREIGN KEY (knowledge_id) REFERENCES knowledge (id);

-- 修改 'user' 表中的 'id' 字段类型为 BIGINT
ALTER TABLE user
    MODIFY COLUMN id BIGINT AUTO_INCREMENT NOT NULL;

-- 修改 'notification' 表中的 'id' 字段类型为 BIGINT
ALTER TABLE notification
    MODIFY COLUMN id BIGINT AUTO_INCREMENT NOT NULL;

-- 修改 'sidedata' 表中的 'id' 字段类型为 BIGINT
ALTER TABLE sidedata
    MODIFY COLUMN id BIGINT AUTO_INCREMENT NOT NULL;

-- 修改 'tags' 表中的 'id' 字段类型为 BIGINT
ALTER TABLE tags
    MODIFY COLUMN id BIGINT AUTO_INCREMENT NOT NULL;

-- 修改 'email_verification_token' 表中的 'user_id' 字段类型为 BIGINT
ALTER TABLE email_verification_token
    MODIFY COLUMN user_id BIGINT NULL;

-- 修改 'password_reset_tokens' 表中的 'user_id' 字段类型为 BIGINT
ALTER TABLE password_reset_tokens
    MODIFY COLUMN user_id BIGINT NULL;

-- 修改 'payment_order' 表中的 'user_id' 字段类型为 BIGINT
ALTER TABLE payment_order
    MODIFY COLUMN user_id BIGINT NULL;

-- 修改 'sidedata' 表中的 'user_id' 字段类型为 BIGINT
ALTER TABLE sidedata
    MODIFY COLUMN user_id BIGINT NULL;

-- 修改 'tags' 表中的 'user_id' 字段类型为 BIGINT
ALTER TABLE tags
    MODIFY COLUMN user_id BIGINT NOT NULL;
