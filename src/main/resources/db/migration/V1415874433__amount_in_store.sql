ALTER TABLE items DROP COLUMN unit;
ALTER TABLE cells_items ADD COLUMN amount INTEGER;

DROP TABLE cells_items;

CREATE TABLE cells_items (
  id     SERIAL PRIMARY KEY,
  cellId INTEGER,
  itemId INTEGER,
  amount CHARACTER VARYING DEFAULT 0,
  CONSTRAINT cells_items_fk_cell FOREIGN KEY (cellId) REFERENCES stores_rows_cells (id) ON DELETE CASCADE,
  CONSTRAINT cells_items_fk_item FOREIGN KEY (itemId) REFERENCES items (id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX cells_items_unique ON cells_items (cellId, itemId);
