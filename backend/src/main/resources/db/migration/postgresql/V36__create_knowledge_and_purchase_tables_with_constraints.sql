-- 修正knowledge表定义
CREATE TABLE knowledge
(
    id             BIGSERIAL PRIMARY KEY,
    title          VARCHAR(255)          NOT NULL,
    content        TEXT,
    user_id        INTEGER REFERENCES "user"(id),
    price          DOUBLE PRECISION      NOT NULL,
    is_encrypted   BOOLEAN               NOT NULL DEFAULT false,
    purchase_count INTEGER               NOT NULL DEFAULT 0,
    created_at     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status         VARCHAR(255)
);

-- 修正knowledge_tags表定义（改为使用tag_id）
CREATE TABLE knowledge_tags
(
    knowledge_id BIGINT REFERENCES knowledge(id),
    tag_id       INTEGER REFERENCES tags(id),
    PRIMARY KEY (knowledge_id, tag_id)
);