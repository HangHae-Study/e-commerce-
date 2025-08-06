SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO users (user_id, username) VALUES
                                          (1, 'user1'),
                                          (2, 'user2');

INSERT INTO points (point_id, user_id, balance) VALUES
                                                              (1, 1, 1000.00),
                                                              (2, 2, 1000.00);

-- 2) 샘플 Product & ProductLine (라인 1~3 재고 1,1,2)
INSERT INTO product (product_id, product_name, product_price) VALUES
    (1, 'SampleProd', 100.00);

INSERT INTO product_line (
    product_line_id, product_id, product_line_name, product_line_price, product_line_type, remaining
) VALUES
      (1, 1, 'Line-1', 10.00, 'TYPE', 1),
      (2, 1, 'Line-2', 20.00, 'TYPE', 1),
      (3, 1, 'Line-3', 30.00, 'TYPE', 2);

-- 3) User1 주문 시도 (각 2개) — 재고 부족으로 비즈니스 로직에서 실패
INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status) VALUES
    (1001, 'ORD-1-FAIL', 1, (2*10.00 + 2*20.00 + 2*30.00), '2025-08-01 09:00:00', 'O_MAKE');

INSERT INTO order_lines (
    order_line_id, order_id, user_id, product_line_id,
    order_line_price, quantity, status, order_dt
) VALUES
      (2001, 1001, 1, 1, 10.00, 2, 'O_MAKE', '2025-08-01 09:00:00'),
      (2002, 1001, 1, 2, 20.00, 2, 'O_MAKE', '2025-08-01 09:00:00'),
      (2003, 1001, 1, 3, 30.00, 2, 'O_MAKE', '2025-08-01 09:00:00');

-- 4) User2 주문 (각 1개) — 재고 충분으로 성공
INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status) VALUES
    (1002, 'ORD-2-SUCC', 2, (1*10.00 + 1*20.00 + 1*30.00), '2025-08-01 10:00:00', 'O_CMPL');

INSERT INTO order_lines (
    order_line_id, order_id, user_id, product_line_id,
    order_line_price, quantity, status, order_dt
) VALUES
      (2004, 1002, 2, 1, 10.00, 1, 'O_MAKE', '2025-08-01 10:00:00'),
      (2005, 1002, 2, 2, 20.00, 1, 'O_MAKE', '2025-08-01 10:00:00'),
      (2006, 1002, 2, 3, 30.00, 1, 'O_MAKE', '2025-08-01 10:00:00');

SET FOREIGN_KEY_CHECKS = 1;
