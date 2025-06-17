DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'knowledge_tags'
        AND column_name = 'tag'
    ) THEN
        RAISE EXCEPTION '缺失字段: knowledge_tags.tag';
END IF;
END $$;