CREATE TABLE users (
  id         SERIAL PRIMARY KEY,
  login      CHARACTER VARYING UNIQUE NOT NULL,
  password   CHARACTER VARYING NOT NULL,
  fname      CHARACTER VARYING,
  lname      CHARACTER VARYING,
  createDate TIMESTAMP         NOT NULL
);

INSERT INTO users(id, login, password, createDate) VALUES (1, 'admin', (SELECT substring(md5(random()::text) from 1 for 10)), now());
INSERT INTO users(id, login, password, createDate) VALUES (2, 'admin2', (SELECT substring(md5(random()::text) from 1 for 10)), now());
INSERT INTO users(id, login, password, createDate) VALUES (3, 'admin3', (SELECT substring(md5(random()::text) from 1 for 10)), now());
INSERT INTO users(id, login, password, createDate) VALUES (4, 'admin4', (SELECT substring(md5(random()::text) from 1 for 10)), now());
