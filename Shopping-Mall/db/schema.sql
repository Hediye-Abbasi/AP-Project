-- PostgreSQL schema for Shopping Mall Desktop Application

CREATE TABLE IF NOT EXISTS users (
    id              SERIAL PRIMARY KEY,
    username        VARCHAR(50) UNIQUE NOT NULL,
    password_hash   VARCHAR(255)       NOT NULL,
    full_name       VARCHAR(100)       NOT NULL,
    email           VARCHAR(100) UNIQUE NOT NULL,
    role            VARCHAR(20)        NOT NULL, -- 'CUSTOMER' or 'ADMIN'
    balance         NUMERIC(12,2)      NOT NULL DEFAULT 0,
    created_at      TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS products (
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(100)       NOT NULL,
    description     TEXT,
    category        VARCHAR(50),
    price           NUMERIC(12,2)      NOT NULL,
    stock_quantity  INTEGER            NOT NULL DEFAULT 0,
    image_url       VARCHAR(255),
    active          BOOLEAN            NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS orders (
    id              SERIAL PRIMARY KEY,
    user_id         INTEGER            NOT NULL REFERENCES users(id),
    total_amount    NUMERIC(12,2)      NOT NULL,
    created_at      TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
    id              SERIAL PRIMARY KEY,
    order_id        INTEGER            NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id      INTEGER            NOT NULL REFERENCES products(id),
    quantity        INTEGER            NOT NULL,
    unit_price      NUMERIC(12,2)      NOT NULL
);

-- Simple cart implementation: items persist per user until checkout/cleared
CREATE TABLE IF NOT EXISTS carts (
    id              SERIAL PRIMARY KEY,
    user_id         INTEGER            NOT NULL REFERENCES users(id),
    created_at      TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cart_items (
    id              SERIAL PRIMARY KEY,
    cart_id         INTEGER            NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id      INTEGER            NOT NULL REFERENCES products(id),
    quantity        INTEGER            NOT NULL,
    UNIQUE (cart_id, product_id)
);

CREATE INDEX IF NOT EXISTS idx_products_name ON products (name);
CREATE INDEX IF NOT EXISTS idx_products_category ON products (category);



