CREATE TABLE categories (
  id         SERIAL PRIMARY KEY,
  title      CHARACTER VARYING NOT NULL,
  description CHARACTER VARYING,
  parentId   INTEGER,
  createDate TIMESTAMP         NOT NULL,
  CONSTRAINT categories_fk1 FOREIGN KEY (parentId) REFERENCES categories (id)
);
