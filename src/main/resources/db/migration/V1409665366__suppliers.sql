-- CREATE TYPE enum_supplier_type AS ENUM ('COMPANY', 'PRIVATE_PERSON');
-- CREATE TYPE enum_supplier_organization_type AS ENUM ('LEGAL_PERSON', 'LEGAL_PERSON_NOT_RU', 'INDIVIDUAL_ENTREPRENEUR');


CREATE TABLE suppliers (
  id               SERIAL PRIMARY KEY,
  clientType       CHARACTER VARYING,
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
  createDate       TIMESTAMP         NOT NULL
);
