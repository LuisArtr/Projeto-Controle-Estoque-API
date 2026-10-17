ALTER TABLE stock_batches
    ADD expired_processed BIT NOT NULL
        CONSTRAINT df_batch_expired DEFAULT 0;