ALTER TABLE purchases DROP COLUMN file2;

INSERT INTO stores (title, createdate) VALUES ('default', now());
INSERT INTO stores_rows (storeid, barcode, createdate) SELECT stores.id, 'default', now() FROM stores WHERE title = 'default';
INSERT INTO stores_rows_cells (rowid, barcode, createdate) SELECT stores_rows.id, 'default', now() FROM stores_rows WHERE barcode = 'default';