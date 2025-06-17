-- V40__fix_knowledge_tags_table.sql
BEGIN;

-- 删除外键约束（如果存在）
ALTER TABLE knowledge_tags DROP CONSTRAINT IF EXISTS knowledge_tags_tag_id_fkey;
ALTER TABLE knowledge_tags DROP CONSTRAINT IF EXISTS knowledge_tags_knowledge_id_fkey;

-- 删除现有表
DROP TABLE IF EXISTS knowledge_tags;

-- 创建符合实体类要求的新表
CREATE TABLE knowledge_tags (
                                knowledge_id BIGINT NOT NULL REFERENCES knowledge(id) ON DELETE CASCADE,
                                tag          VARCHAR(255) NOT NULL,
                                PRIMARY KEY (knowledge_id, tag)
);

-- 添加索引
CREATE INDEX idx_knowledge_tags_tag ON knowledge_tags (tag);

COMMIT;