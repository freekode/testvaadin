CREATE TABLE procurements (
  id         SERIAL PRIMARY KEY,
  supplierId INTEGER,
  selectDate TIMESTAMP,
  file0      CHARACTER VARYING,
  file1      CHARACTER VARYING,
  file2      CHARACTER VARYING,
  tax        BOOLEAN,
  categoryId INTEGER,
  createDate TIMESTAMP NOT NULL,
  CONSTRAINT procurements_fk1 FOREIGN KEY (supplierId) REFERENCES suppliers (id),
  CONSTRAINT procurements_fk2 FOREIGN KEY (categoryId) REFERENCES categories (id)
);
