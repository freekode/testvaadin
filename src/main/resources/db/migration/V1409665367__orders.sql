CREATE TABLE orders (
  id         SERIAL PRIMARY KEY,
  number     CHARACTER VARYING,
  fromDate   TIMESTAMP,
  clientId   INTEGER,
  contract   CHARACTER VARYING,
  status     CHARACTER VARYING,
  priority   CHARACTER VARYING,
  delivery   CHARACTER VARYING,
  shipmentDate TIMESTAMP,
  commentary TEXT,
  createDate TIMESTAMP NOT NULL,
  CONSTRAINT orders_fk1 FOREIGN KEY (clientId) REFERENCES clients (id)
);

CREATE TABLE orders_items (
  id     SERIAL PRIMARY KEY,
  orderId INTEGER NOT NULL,
  itemId INTEGER NOT NULL,
  CONSTRAINT orders_items_fk1 FOREIGN KEY (orderId) REFERENCES orders (id),
  CONSTRAINT orders_items_fk2 FOREIGN KEY (itemId) REFERENCES items (id)
);
