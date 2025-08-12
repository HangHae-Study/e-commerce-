-- src/test/resources/initial-data-order-optimistic-fail.sql

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE order_lines;
TRUNCATE TABLE orders;
TRUNCATE TABLE product_line;
TRUNCATE TABLE product;
TRUNCATE TABLE points;
TRUNCATE TABLE users;

-- 1) 단일 유저 & 충분한 포인트
INSERT INTO users (user_id, username) VALUES
    (1, 'user1');
INSERT INTO points (point_id, user_id, balance) VALUES
    (1, 1, 20.00); -- 돈 없음

-- 2) 단일 상품 & 상품라인(재고 3)
INSERT INTO product (product_id, product_name, product_price) VALUES
    (1, 'SampleProd', 100.00);
INSERT INTO product_line (
    product_line_id,
    product_id,
    product_line_name,
    product_line_price,
    product_line_type,
    remaining
) VALUES
    (1, 1, 'Line-1', 100.00, 'TYPE', 3);

INSERT INTO orders (
    order_id,
    order_code,
    user_id,
    total_price,
    order_dt,
    status,
    version
) VALUES
    (1001, 'ORD-1', 1, 100.00, '2025-08-06 00:00:00', 'O_MAKE', 1);

INSERT INTO order_lines (
    order_line_id,
    order_id,
    user_id,
    product_line_id,
    order_line_price,
    quantity,
    status,
    order_dt,
    update_dt,
    version
) VALUES
    (2001, 1001, 1, 1, 100.00, 1, 'O_MAKE', '2025-08-06 00:00:00', '2025-08-06 00:00:00', 1);

SET FOREIGN_KEY_CHECKS = 1;
