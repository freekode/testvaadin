ALTER TABLE orders ADD COLUMN addressId INTEGER;
ALTER TABLE orders ADD CONSTRAINT orders_address FOREIGN KEY (addressId) REFERENCES addresses(id);
