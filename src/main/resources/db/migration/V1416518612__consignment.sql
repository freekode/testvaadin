CREATE TABLE consignments (
  id         SERIAL PRIMARY KEY,
  shipmentDate TIMESTAMP NOT NULL,
  createDate TIMESTAMP NOT NULL
);

ALTER TABLE items ADD COLUMN consignmentId INTEGER;
ALTER TABLE items ADD CONSTRAINT items_fk_consignment FOREIGN KEY (consignmentId) REFERENCES consignments (id) ON DELETE CASCADE;
