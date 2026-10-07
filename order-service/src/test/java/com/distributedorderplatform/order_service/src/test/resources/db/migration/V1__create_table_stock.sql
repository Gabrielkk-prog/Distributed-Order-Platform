CREATE TABLE IF NOT EXISTS tb_stock (
    product_id UUID PRIMARY KEY,
    quantity INT NOT NULL,
    reserved_quantity INT NOT NULL
);
