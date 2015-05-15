-- CREATE TYPE enum_client_type AS ENUM ('COMPANY', 'PRIVATE_PERSON');
-- CREATE TYPE enum_client_organization_type AS ENUM ('LEGAL_PERSON', 'LEGAL_PERSON_NOT_RU', 'INDIVIDUAL_ENTREPRENEUR');


CREATE TABLE clients (
  id               SERIAL PRIMARY KEY,
  managerId        INTEGER,
  login            CHARACTER VARYING UNIQUE NOT NULL,
  password         CHARACTER VARYING NOT NULL,
  clientType       CHARACTER VARYING ,
  shortcutLegalName CHARACTER VARYING,
  orgEmail         CHARACTER VARYING,
  orgPhoneNumber   CHARACTER VARYING,
  organizationType CHARACTER VARYING,
  inn              CHARACTER VARYING,
  kpp              CHARACTER VARYING,
  okpo             CHARACTER VARYING,
  surname          CHARACTER VARYING,
  name             CHARACTER VARYING,
  patronymic       CHARACTER VARYING,
  phoneNumber      CHARACTER VARYING,
  email            CHARACTER VARYING,
  mobileNumber     CHARACTER VARYING,
  contract         CHARACTER VARYING,
  createDate       TIMESTAMP         NOT NULL,
  CONSTRAINT clients_manager_fk FOREIGN KEY (managerId) REFERENCES users(id)
);
