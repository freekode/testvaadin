ALTER TABLE items DROP CONSTRAINT items_fk2;
ALTER TABLE items DROP COLUMN cellid;


CREATE TABLE cells_items (
  cellId INTEGER,
  itemId INTEGER,
  PRIMARY KEY (cellId, itemId),
  CONSTRAINT cells_items_fk_cell FOREIGN KEY (cellId) REFERENCES stores_rows_cells (id),
  CONSTRAINT cells_items_fk_item FOREIGN KEY (itemId) REFERENCES items (id)
);
