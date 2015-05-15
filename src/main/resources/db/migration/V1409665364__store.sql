CREATE TABLE stores (
  id         SERIAL PRIMARY KEY,
  title      CHARACTER VARYING NOT NULL,
  createDate TIMESTAMP         NOT NULL
);

CREATE TABLE stores_rows (
  id      SERIAL PRIMARY KEY,
  storeId INTEGER NOT NULL,
  barcode CHARACTER VARYING,
  createDate TIMESTAMP NOT NULL,
  CONSTRAINT stores_rows_fk1 FOREIGN KEY (storeId) REFERENCES stores (id)
);

CREATE TABLE stores_rows_cells (
  id      SERIAL PRIMARY KEY,
  rowId   INTEGER NOT NULL,
  barcode CHARACTER VARYING,
  createDate TIMESTAMP NOT NULL,
  CONSTRAINT stores_rows_cells_fk1 FOREIGN KEY (rowId) REFERENCES stores_rows (id)
);
