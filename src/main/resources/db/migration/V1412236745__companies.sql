CREATE TABLE companies (
  id               SERIAL PRIMARY KEY,
  managerId        INTEGER,
  shortcutLegalName CHARACTER VARYING,
  orgEmail         CHARACTER VARYING,
  orgPhoneNumber   CHARACTER VARYING,
  inn              CHARACTER VARYING,
  kpp              CHARACTER VARYING,
  okpo             CHARACTER VARYING,
  surname          CHARACTER VARYING,
  name             CHARACTER VARYING,
  email            CHARACTER VARYING,
  createDate       TIMESTAMP         NOT NULL,
  CONSTRAINT companies_manager_fk FOREIGN KEY (managerId) REFERENCES users(id)
);
