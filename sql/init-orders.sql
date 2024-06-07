CREATE DATABASE orders;

\connect orders;

CREATE TABLE order_table (
                             id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             name VARCHAR(255) NOT NULL
);
