CREATE DATABASE orders;

\connect orders;

CREATE TABLE order_table (
                             orderId BIGINT AS IDENTITY PRIMARY KEY,
                             customerId BIGINT NOT NULL,
                            status VARCHAR(255)
);

CREATE TABLE order_product_table (
                            id BIGINT AS IDENTITY PRIMARY KEY,
                             orderId BIGINT,
                             productId BIGINT NOT NULL,
                            quantity INT NOT NULL

);

INSERT INTO order_table (customerId, status) VALUES (21, 'UNCOMPLETED');
INSERT INTO order_table (customerId, status) VALUES (22, 'UNCOMPLETED');

INSERT INTO order_product_table(orderId, productId, quantity) VALUES (1, 1, 5);
INSERT INTO order_product_table(orderId, productId, quantity) VALUES (1, 2, 2);
INSERT INTO order_product_table(orderId, productId, quantity) VALUES (2, 1, 2);