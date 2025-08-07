SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE users;
TRUNCATE points;
TRUNCATE product;
TRUNCATE product_line;
TRUNCATE orders;
TRUNCATE order_lines;

-- 1) 단일 유저 & 충분한 포인트
INSERT INTO users (user_id, username) VALUES
    (1, 'user1');
INSERT INTO points (point_id, user_id, balance) VALUES
    (1, 1, 10000.00);

-- 2) 단일 상품 & 상품라인(재고 1)
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

-- 3) 단일 주문(수량 1) & 주문라인
INSERT INTO orders (
    order_id,
    order_code,
    user_id,
    total_price,
    order_dt,
    status,
    version
) VALUES
    (1001, 'ORD-1', 1, 100.00, '2025-08-06 00:00:00', 'O_MAKE', 0);
INSERT INTO order_lines (
    order_line_id,
    order_id,
    user_id,
    product_line_id,
    order_line_price,
    quantity,
    status,
    order_dt,
    update_dt
) VALUES
    (2001, 1001, 1, 1, 100.00, 1, 'O_MAKE', '2025-08-06 00:00:00', '2025-08-06 00:00:00');

SET FOREIGN_KEY_CHECKS = 1;
