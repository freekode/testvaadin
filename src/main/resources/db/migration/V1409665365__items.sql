-- CREATE TYPE enum_item_vat AS ENUM ('TEN', 'EIGHTEEN', 'TWENTY_TWO');

CREATE TABLE items (
  id           SERIAL PRIMARY KEY,
  mainName     CHARACTER VARYING,
  printName    CHARACTER VARYING,
  numenclType  CHARACTER VARYING,
  unit         CHARACTER VARYING,
  manufactureCode CHARACTER VARYING,
  otherCode    CHARACTER VARYING,
  applicable   CHARACTER VARYING,
  originalCode CHARACTER VARYING,
  price        CHARACTER VARYING,
  barcode      CHARACTER VARYING,
  tax          CHARACTER VARYING,
  categoryId   INTEGER,
  cellId       INTEGER,
  createDate   TIMESTAMP         NOT NULL,
  CONSTRAINT items_fk1 FOREIGN KEY (categoryId) REFERENCES categories(id),
  CONSTRAINT items_fk2 FOREIGN KEY (cellId) REFERENCES stores_rows_cells(id)
);
