ALTER TABLE production_orders
    ADD batches NUMERIC(12,3) NOT NULL
            CONSTRAINT df_po_batches DEFAULT 1,
        total_cost NUMERIC(14,4) NOT NULL
            CONSTRAINT df_po_cost DEFAULT 0;