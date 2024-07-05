CREATE DATABASE orders;

\connect orders;

CREATE TABLE order_table (
                             orderId BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             customerId BIGINT NOT NULL
);

CREATE TABLE order_product_table (
                             orderId BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             productId BIGINT NOT NULL,
                            quantity INT NOT NULL,
                            status VARCHAR(255)
);

INSERT INTO order_table VALUES (1, 21);
INSERT INTO order_table VALUES (2, 21);

INSERT INTO order_product_table VALUES (1, 1, 5, "uncompleted");
INSERT INTO order_product_table VALUES (1, 2, 2, "uncompleted");