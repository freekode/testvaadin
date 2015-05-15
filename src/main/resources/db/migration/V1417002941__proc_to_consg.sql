ALTER TABLE items DROP CONSTRAINT items_fk_consignment;

DROP TABLE consignments;

CREATE TABLE consignments (
  id         INTEGER PRIMARY KEY,
  shipmentDate TIMESTAMP NOT NULL,
  createDate TIMESTAMP NOT NULL,
  CONSTRAINT consignments_fk_procurement FOREIGN KEY (id) REFERENCES procurements (id)
);

ALTER TABLE items ADD CONSTRAINT items_fk_consignment FOREIGN KEY (consignmentId) REFERENCES consignments (id) ON DELETE SET NULL;
