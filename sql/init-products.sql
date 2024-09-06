CREATE DATABASE products;

\connect products;

CREATE TABLE product_table (
                            productId BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            name VARCHAR(255),
                            reserved BIGINT,
                            available BIGINT
);

INSERT INTO product_table (name, reserved, available) VALUES ('milk', 0, 130);
INSERT INTO product_table (name, reserved, available) VALUES ('eggs', 0, 130);
INSERT INTO product_table (name, reserved, available) VALUES ('bread', 0, 30);