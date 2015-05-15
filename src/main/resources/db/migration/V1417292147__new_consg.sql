ALTER TABLE procurements RENAME TO purchases;


ALTER TABLE items DROP CONSTRAINT items_fk_consignment;
ALTER TABLE items DROP COLUMN consignmentId;


DROP TABLE consignments;
CREATE TABLE shipments (
  id         SERIAL PRIMARY KEY,
  purchaseId INTEGER UNIQUE,
  shipmentDate TIMESTAMP,
  createDate TIMESTAMP NOT NULL,
  CONSTRAINT shipments_fk_purchases FOREIGN KEY (purchaseId) REFERENCES purchases (id)
);


ALTER TABLE items ADD COLUMN shipmentId INTEGER;
ALTER TABLE items ADD CONSTRAINT items_fk_shipment FOREIGN KEY (shipmentId) REFERENCES shipments (id) ON DELETE SET NULL;
