CREATE DATABASE orders;

\connect orders;

CREATE TABLE order_table (
                                    orderId SERIAL  PRIMARY KEY,
                                    customerId BIGINT NOT NULL,
                                    status VARCHAR(255) DEFAULT 'UNKNOWN' NOT NULL
);

CREATE TABLE order_product_table (
                                    id SERIAL PRIMARY KEY,
                                    orderId BIGINT,
                                    productId BIGINT NOT NULL,
                                    quantity INT NOT NULL,
                                    status VARCHAR(255) DEFAULT 'UNKNOWN' NOT NULL

);

