CREATE TABLE IF NOT EXISTS items (
    item_id uuid NOT NULL,
    item_name varchar(50) NOT NULL,
    item_price numeric NOT NULL,
    item_quantity int NOT NULL,
    item_description text NULL,
    item_created_at timestamp with time zone NOT NULL,
    order_id uuid NOT NULL,
    item_code int NULL,
    product_id uuid NULL,
    item_discount numeric NULL,
    CONSTRAINT PK_item PRIMARY KEY (item_id),
    CONSTRAINT ind_53 UNIQUE (item_code),
    CONSTRAINT fk_order_item FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_product_item FOREIGN KEY (product_id) REFERENCES products (product_id) ON DELETE
    SET NULL ON UPDATE CASCADE
);
CREATE INDEX IF NOT EXISTS fk_order_item ON items (order_id);
CREATE INDEX IF NOT EXISTS fk_product_item ON items (product_id);