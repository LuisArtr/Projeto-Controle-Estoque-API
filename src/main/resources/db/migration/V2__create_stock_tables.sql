CREATE TABLE products (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(120) NOT NULL UNIQUE,
    sale_price NUMERIC(12,2) NOT NULL,
    unit VARCHAR(10) NOT NULL
);

CREATE TABLE recipe_items (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    product_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    quantity NUMERIC(12,3) NOT NULL,
    CONSTRAINT fk_recipe_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_recipe_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredients(id),
    CONSTRAINT uq_recipe UNIQUE (product_id, ingredient_id)
);

CREATE TABLE stock_batches (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ingredient_id BIGINT NOT NULL,
    supplier_id BIGINT NULL,
    current_quantity NUMERIC(12,3) NOT NULL,
    unit_cost NUMERIC(12,4) NOT NULL,
    expiration_date DATE NOT NULL,
    entered_at DATETIMEOFFSET NOT NULL DEFAULT SYSDATETIMEOFFSET(),
    CONSTRAINT fk_batch_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredients(id),
    CONSTRAINT fk_batch_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

CREATE INDEX ix_batch_fefo ON stock_batches (ingredient_id, expiration_date);

CREATE TABLE production_orders (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    product_id BIGINT NOT NULL,
    quantity_produced NUMERIC(12,3) NOT NULL,
    created_at DATETIMEOFFSET NOT NULL DEFAULT SYSDATETIMEOFFSET(),
    user_id BIGINT NULL,
    CONSTRAINT fk_prod_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_prod_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE stock_movements (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ingredient_id BIGINT NOT NULL,
    batch_id BIGINT NULL,
    type VARCHAR(20) NOT NULL,
    quantity NUMERIC(12,3) NOT NULL,
    reason NVARCHAR(255) NULL,
    created_at DATETIMEOFFSET NOT NULL DEFAULT SYSDATETIMEOFFSET(),
    user_id BIGINT NULL,
    CONSTRAINT fk_mov_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredients(id),
    CONSTRAINT fk_mov_batch FOREIGN KEY (batch_id) REFERENCES stock_batches(id),
    CONSTRAINT fk_mov_user FOREIGN KEY (user_id) REFERENCES users(id)
);