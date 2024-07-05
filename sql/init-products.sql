CREATE DATABASE products;

\connect products;

CREATE TABLE product_table (
                            productId BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            name VARCHAR(255),
                            reserved BIGINT,
                            available BIGINT
);

INSERT INTO product_table VALUES (1, "milk", 0, 30);
INSERT INTO product_table VALUES (2, "eggs", 0, 20);
INSERT INTO product_table VALUES (3, "bread", 5, 15);