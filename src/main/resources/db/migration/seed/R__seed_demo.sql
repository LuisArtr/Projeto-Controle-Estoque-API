-- Dados de exemplo (só rodam com o perfil "dev")

IF NOT EXISTS (SELECT 1 FROM suppliers WHERE name = N'Moinho Boa Safra')
    INSERT INTO suppliers (name, contact) VALUES (N'Moinho Boa Safra', N'(11) 4000-0001');

IF NOT EXISTS (SELECT 1 FROM suppliers WHERE name = N'Distribuidora Doce Vida')
    INSERT INTO suppliers (name, contact) VALUES (N'Distribuidora Doce Vida', N'(11) 4000-0002');

IF NOT EXISTS (SELECT 1 FROM ingredients WHERE name = N'Farinha de trigo')
    INSERT INTO ingredients (name, base_unit, minimum_stock) VALUES (N'Farinha de trigo', 'kg', 50);

IF NOT EXISTS (SELECT 1 FROM ingredients WHERE name = N'Sal')
    INSERT INTO ingredients (name, base_unit, minimum_stock) VALUES (N'Sal', 'kg', 5);

IF NOT EXISTS (SELECT 1 FROM ingredients WHERE name = N'Fermento biológico')
    INSERT INTO ingredients (name, base_unit, minimum_stock) VALUES (N'Fermento biológico', 'kg', 2);

IF NOT EXISTS (SELECT 1 FROM ingredients WHERE name = N'Açúcar')
    INSERT INTO ingredients (name, base_unit, minimum_stock) VALUES (N'Açúcar', 'kg', 10);

IF NOT EXISTS (SELECT 1 FROM products WHERE name = N'Pão francês')
BEGIN
    INSERT INTO products (name, sale_price, unit, yield_quantity)
    VALUES (N'Pão francês', 0.80, 'un', 700);

    INSERT INTO recipe_items (product_id, ingredient_id, quantity)
    SELECT p.id, i.id, r.qty
    FROM products p
    JOIN (VALUES (N'Farinha de trigo', 30.0),
                 (N'Sal', 0.6),
                 (N'Fermento biológico', 0.3),
                 (N'Açúcar', 0.5)) AS r(ingredient_name, qty) ON 1 = 1
    JOIN ingredients i ON i.name = r.ingredient_name
    WHERE p.name = N'Pão francês';
END;

-- Lotes: só se ainda não houver nenhum
IF NOT EXISTS (SELECT 1 FROM stock_batches)
BEGIN
    INSERT INTO stock_batches (ingredient_id, supplier_id, current_quantity, unit_cost, expiration_date)
    SELECT i.id, s.id, b.qty, b.cost, DATEADD(DAY, b.days, CAST(SYSDATETIMEOFFSET() AS date))
    FROM (VALUES (N'Farinha de trigo', N'Moinho Boa Safra',        25.0, 4.00,  30),
                 (N'Farinha de trigo', N'Moinho Boa Safra',       200.0, 4.20, 120),
                 (N'Sal',              N'Moinho Boa Safra',        20.0, 2.50, 300),
                 (N'Fermento biológico', N'Distribuidora Doce Vida', 5.0, 40.00, 45),
                 (N'Açúcar',           N'Distribuidora Doce Vida', 30.0, 3.80, 200))
         AS b(ingredient_name, supplier_name, qty, cost, days)
    JOIN ingredients i ON i.name = b.ingredient_name
    JOIN suppliers s ON s.name = b.supplier_name;

    INSERT INTO stock_movements (ingredient_id, batch_id, type, quantity, reason)
    SELECT ingredient_id, id, 'ENTRADA', current_quantity, N'Carga de exemplo'
    FROM stock_batches;
END;