ALTER TABLE shipments DROP CONSTRAINT shipments_fk_purchases;
ALTER TABLE shipments ADD CONSTRAINT shipments_fk_purchases FOREIGN KEY (purchaseId) REFERENCES purchases (id) ON DELETE CASCADE;

ALTER TABLE orders_items DROP CONSTRAINT orders_items_fk2;
ALTER TABLE orders_items ADD CONSTRAINT orders_items_items FOREIGN KEY (itemId) REFERENCES items (id);


ALTER TABLE items DROP CONSTRAINT items_fk_shipment;
ALTER TABLE items ADD CONSTRAINT items_fk_shipment FOREIGN KEY (shipmentId) REFERENCES shipments (id) ON DELETE CASCADE;
