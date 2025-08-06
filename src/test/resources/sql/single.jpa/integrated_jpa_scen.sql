-- src/test/resources/sql/integrated_scenarios.sql

-- (1) 사용자·포인트
INSERT INTO users           (user_id, username) VALUES (1, 'user1');
INSERT INTO points(point_id, user_id, balance)
VALUES (1, 1, 1000.00);

-- (2) 상품 10개
INSERT INTO product(product_id, product_name, product_price) VALUES
                                                                 (1,'P1',10.00),  (2,'P2',20.00),  (3,'P3',30.00),  (4,'P4',40.00),  (5,'P5',50.00),
                                                                 (6,'P6',60.00),  (7,'P7',70.00),  (8,'P8',80.00),  (9,'P9',90.00),  (10,'P10',100.00);

-- (3) 상품 ID=1에 대한 5개 라인
INSERT INTO product_line(
    product_line_id, product_id, product_line_name, product_line_price, product_line_type, remaining
) VALUES
      (1,1,'P1-L1',11.00,'T1',100),
      (2,1,'P1-L2',12.00,'T2',100),
      (3,1,'P1-L3',13.00,'T3',100),
      (4,1,'P1-L4',14.00,'T4',100),
      (5,1,'P1-L5',15.00,'T5',100);

-- (4) 쿠폰 10개 (remaining 각기 다르게)
INSERT INTO coupon(coupon_id, total_issued, remaining, discount_rate, expire_date) VALUES
                                                                                       (1,100,5, 5.00,'2025-12-31 23:59:59'),
                                                                                       (2,100,10,5.00,'2025-12-31 23:59:59'),
                                                                                       (3,100,15,5.00,'2025-12-31 23:59:59'),
                                                                                       (4,100,20,5.00,'2025-12-31 23:59:59'),
                                                                                       (5,100,25,5.00,'2025-12-31 23:59:59'),
                                                                                       (6,100,30,5.00,'2025-12-31 23:59:59'),
                                                                                       (7,100,35,5.00,'2025-12-31 23:59:59'),
                                                                                       (8,100,40,5.00,'2025-12-31 23:59:59'),
                                                                                       (9,100,45,5.00,'2025-12-31 23:59:59'),
                                                                                       (10,100,50,5.00,'2025-12-31 23:59:59');

-- (6) 유저별 발급 쿠폰 2개
INSERT INTO coupon_issue(
    coupon_issue_id, coupon_code, coupon_id, user_id, coupon_valid, discount_rate, expire_date
) VALUES
      (1,'CODE1',1,1,'Y',10.00,'2025-12-31 23:59:59'),
      (2,'CODE2',1,1,'Y',10.00,'2025-12-31 23:59:59');

-- (8) 저장된 주문 + 라인
INSERT INTO orders(order_id, order_code, user_id, total_price, status) VALUES
    (100,'ORD-008',1,150.00,'O_MAKE');

INSERT INTO order_lines(
    order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status
) VALUES
      (100,100,1,1,  50.00,1,'O_MAKE'),
      (101,100,1,2, 100.00,1,'O_MAKE');
