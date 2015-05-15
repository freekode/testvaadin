ALTER TABLE categories DROP CONSTRAINT categories_fk1;
ALTER TABLE categories ADD CONSTRAINT categories_fk1 FOREIGN KEY (parentId) REFERENCES categories (id) ON DELETE CASCADE;

ALTER TABLE items DROP CONSTRAINT items_fk1;
ALTER TABLE items ADD CONSTRAINT items_fk1 FOREIGN KEY (categoryId) REFERENCES categories(id) ON DELETE CASCADE;

ALTER TABLE procurements DROP CONSTRAINT procurements_fk2;
ALTER TABLE procurements ADD CONSTRAINT procurements_fk2 FOREIGN KEY (categoryId) REFERENCES categories(id) ON DELETE CASCADE;
