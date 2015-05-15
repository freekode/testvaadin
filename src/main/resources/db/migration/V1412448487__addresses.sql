CREATE TABLE addresses (
  id         SERIAL PRIMARY KEY,
  title      CHARACTER VARYING,
  address    TEXT,
  clientId   INTEGER,
  createDate TIMESTAMP NOT NULL,
  CONSTRAINT addresses_fk FOREIGN KEY (clientId) REFERENCES clients (id) ON DELETE CASCADE
);
