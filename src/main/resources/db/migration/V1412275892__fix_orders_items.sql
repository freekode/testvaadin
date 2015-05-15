ALTER TABLE orders_items DROP CONSTRAINT orders_items_fk1;
ALTER TABLE orders_items ADD CONSTRAINT orders_items_fk1 FOREIGN KEY (orderId) REFERENCES orders (id) ON DELETE CASCADE;

ALTER TABLE orders_items DROP CONSTRAINT orders_items_fk2;
ALTER TABLE orders_items ADD CONSTRAINT orders_items_fk2 FOREIGN KEY (itemId) REFERENCES items (id) ON DELETE CASCADE;
