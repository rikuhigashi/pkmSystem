DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_name = 'knowledge_purchase'
    ) THEN
        RAISE EXCEPTION '缺失表 knowledge_purchase';
END IF;

IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'knowledge_purchase'
        AND column_name = 'payment_order_id'
    ) THEN
        RAISE EXCEPTION '缺失字段 payment_order_id';
END IF;
END $$;