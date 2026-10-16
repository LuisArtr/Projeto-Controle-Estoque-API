ALTER TABLE products
    ADD yield_quantity NUMERIC(12,3) NOT NULL
        CONSTRAINT df_products_yield DEFAULT 1;