-- USERS (50명)
INSERT INTO users (username) VALUES ('user01');
INSERT INTO users (username) VALUES ('user02');
INSERT INTO users (username) VALUES ('user03');
INSERT INTO users (username) VALUES ('user04');
INSERT INTO users (username) VALUES ('user05');
INSERT INTO users (username) VALUES ('user06');
INSERT INTO users (username) VALUES ('user07');
INSERT INTO users (username) VALUES ('user08');
INSERT INTO users (username) VALUES ('user09');
INSERT INTO users (username) VALUES ('user10');
INSERT INTO users (username) VALUES ('user11');
INSERT INTO users (username) VALUES ('user12');
INSERT INTO users (username) VALUES ('user13');
INSERT INTO users (username) VALUES ('user14');
INSERT INTO users (username) VALUES ('user15');
INSERT INTO users (username) VALUES ('user16');
INSERT INTO users (username) VALUES ('user17');
INSERT INTO users (username) VALUES ('user18');
INSERT INTO users (username) VALUES ('user19');
INSERT INTO users (username) VALUES ('user20');
INSERT INTO users (username) VALUES ('user21');
INSERT INTO users (username) VALUES ('user22');
INSERT INTO users (username) VALUES ('user23');
INSERT INTO users (username) VALUES ('user24');
INSERT INTO users (username) VALUES ('user25');
INSERT INTO users (username) VALUES ('user26');
INSERT INTO users (username) VALUES ('user27');
INSERT INTO users (username) VALUES ('user28');
INSERT INTO users (username) VALUES ('user29');
INSERT INTO users (username) VALUES ('user30');
INSERT INTO users (username) VALUES ('user31');
INSERT INTO users (username) VALUES ('user32');
INSERT INTO users (username) VALUES ('user33');
INSERT INTO users (username) VALUES ('user34');
INSERT INTO users (username) VALUES ('user35');
INSERT INTO users (username) VALUES ('user36');
INSERT INTO users (username) VALUES ('user37');
INSERT INTO users (username) VALUES ('user38');
INSERT INTO users (username) VALUES ('user39');
INSERT INTO users (username) VALUES ('user40');
INSERT INTO users (username) VALUES ('user41');
INSERT INTO users (username) VALUES ('user42');
INSERT INTO users (username) VALUES ('user43');
INSERT INTO users (username) VALUES ('user44');
INSERT INTO users (username) VALUES ('user45');
INSERT INTO users (username) VALUES ('user46');
INSERT INTO users (username) VALUES ('user47');
INSERT INTO users (username) VALUES ('user48');
INSERT INTO users (username) VALUES ('user49');
INSERT INTO users (username) VALUES ('user50');


-- users 테이블 예시 (이미 있으실 테니 참고만)
-- INSERT INTO users (user_id, username, create_dt, update_dt)
-- VALUES (1, 'user1', NOW(), NOW()), (2, 'user2', NOW(), NOW()), ...;

-- points 테이블 초기화 (userId 1~50, balance 0)
INSERT INTO points (point_id, user_id, balance, update_dt)
VALUES (1, 1,   999999.00, NOW()),
       (2, 2,   999999.00, NOW()),
       (3, 3,   999999.00, NOW()),
       (4, 4,   999999.00, NOW()),
       (5, 5,   999999.00, NOW()),
       (6, 6,   999999.00, NOW()),
       (7, 7,   999999.00, NOW()),
       (8, 8,   999999.00, NOW()),
       (9, 9,   999999.00, NOW()),
       (10, 10, 999999.00, NOW()),
       (11, 11, 999999.00, NOW()),
       (12, 12, 999999.00, NOW()),
       (13, 13, 999999.00, NOW()),
       (14, 14, 999999.00, NOW()),
       (15, 15, 999999.00, NOW()),
       (16, 16, 999999.00, NOW()),
       (17, 17, 999999.00, NOW()),
       (18, 18, 999999.00, NOW()),
       (19, 19, 999999.00, NOW()),
       (20, 20, 999999.00, NOW()),
       (21, 21, 999999.00, NOW()),
       (22, 22, 999999.00, NOW()),
       (23, 23, 999999.00, NOW()),
       (24, 24, 999999.00, NOW()),
       (25, 25, 999999.00, NOW()),
       (26, 26, 999999.00, NOW()),
       (27, 27, 999999.00, NOW()),
       (28, 28, 999999.00, NOW()),
       (29, 29, 999999.00, NOW()),
       (30, 30, 999999.00, NOW()),
       (31, 31, 999999.00, NOW()),
       (32, 32, 999999.00, NOW()),
       (33, 33, 999999.00, NOW()),
       (34, 34, 999999.00, NOW()),
       (35, 35, 999999.00, NOW()),
       (36, 36, 999999.00, NOW()),
       (37, 37, 999999.00, NOW()),
       (38, 38, 999999.00, NOW()),
       (39, 39, 999999.00, NOW()),
       (40, 40, 999999.00, NOW()),
       (41, 41, 999999.00, NOW()),
       (42, 42, 999999.00, NOW()),
       (43, 43, 999999.00, NOW()),
       (44, 44, 999999.00, NOW()),
       (45, 45, 999999.00, NOW()),
       (46, 46, 999999.00, NOW()),
       (47, 47, 999999.00, NOW()),
       (48, 48, 999999.00, NOW()),
       (49, 49, 999999.00, NOW()),
       (50, 50, 999999.00, NOW());

-- PRODUCTS (10개)
INSERT INTO product (product_name, product_price) VALUES ('상품1', 10000.00);
INSERT INTO product (product_name, product_price) VALUES ('상품2', 20000.00);
INSERT INTO product (product_name, product_price) VALUES ('상품3', 30000.00);
INSERT INTO product (product_name, product_price) VALUES ('상품4', 40000.00);
INSERT INTO product (product_name, product_price) VALUES ('상품5', 50000.00);
INSERT INTO product (product_name, product_price) VALUES ('상품6', 60000.00);
INSERT INTO product (product_name, product_price) VALUES ('상품7', 70000.00);
INSERT INTO product (product_name, product_price) VALUES ('상품8', 80000.00);
INSERT INTO product (product_name, product_price) VALUES ('상품9', 90000.00);
INSERT INTO product (product_name, product_price) VALUES ('상품10', 100000.00);

-- PRODUCT LINES (각 상품당 1개씩, 재고 3000개)
INSERT INTO product_line (product_id, product_line_name, product_line_price, product_line_type, remaining)
VALUES (1, '상품1-기본', 10000.00, 'BASIC', 3000);
INSERT INTO product_line (product_id, product_line_name, product_line_price, product_line_type, remaining)
VALUES (2, '상품2-기본', 20000.00, 'BASIC', 3000);
INSERT INTO product_line (product_id, product_line_name, product_line_price, product_line_type, remaining)
VALUES (3, '상품3-기본', 30000.00, 'BASIC', 3000);
INSERT INTO product_line (product_id, product_line_name, product_line_price, product_line_type, remaining)
VALUES (4, '상품4-기본', 40000.00, 'BASIC', 3000);
INSERT INTO product_line (product_id, product_line_name, product_line_price, product_line_type, remaining)
VALUES (5, '상품5-기본', 50000.00, 'BASIC', 3000);
INSERT INTO product_line (product_id, product_line_name, product_line_price, product_line_type, remaining)
VALUES (6, '상품6-기본', 60000.00, 'BASIC', 3000);
INSERT INTO product_line (product_id, product_line_name, product_line_price, product_line_type, remaining)
VALUES (7, '상품7-기본', 70000.00, 'BASIC', 3000);
INSERT INTO product_line (product_id, product_line_name, product_line_price, product_line_type, remaining)
VALUES (8, '상품8-기본', 80000.00, 'BASIC', 3000);
INSERT INTO product_line (product_id, product_line_name, product_line_price, product_line_type, remaining)
VALUES (9, '상품9-기본', 90000.00, 'BASIC', 3000);
INSERT INTO product_line (product_id, product_line_name, product_line_price, product_line_type, remaining)
VALUES (10, '상품10-기본', 100000.00, 'BASIC', 3000);

-- ORDERS + ORDER LINES (최근 3일 ~ 오늘까지)
-- 예시: 유저1이 상품1을 하루 단위로 주문 완료
-- DAY -3
INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (1, 'ORD-001', 1, 20000.00, DATE_SUB(SYSDATE(), INTERVAL 3 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (1, 1, 1, 1, 10000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 3 DAY), DATE_SUB(SYSDATE(), INTERVAL 3 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (2, 'ORD-002', 2, 40000.00, DATE_SUB(SYSDATE(), INTERVAL 3 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (2, 2, 2, 2, 20000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 3 DAY), DATE_SUB(SYSDATE(), INTERVAL 3 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (3, 'ORD-003', 3, 30000.00, DATE_SUB(SYSDATE(), INTERVAL 3 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (3, 3, 3, 3, 30000.00, 1, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 3 DAY), DATE_SUB(SYSDATE(), INTERVAL 3 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (4, 'ORD-004', 4, 40000.00, DATE_SUB(SYSDATE(), INTERVAL 3 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (4, 4, 4, 4, 20000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 3 DAY), DATE_SUB(SYSDATE(), INTERVAL 3 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (5, 'ORD-005', 5, 50000.00, DATE_SUB(SYSDATE(), INTERVAL 3 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (5, 5, 5, 5, 50000.00, 1, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 3 DAY), DATE_SUB(SYSDATE(), INTERVAL 3 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (6, 'ORD-006', 6, 60000.00, DATE_SUB(SYSDATE(), INTERVAL 3 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (6, 6, 6, 6, 30000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 3 DAY), DATE_SUB(SYSDATE(), INTERVAL 3 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (7, 'ORD-007', 7, 70000.00, DATE_SUB(SYSDATE(), INTERVAL 3 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (7, 7, 7, 7, 35000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 3 DAY), DATE_SUB(SYSDATE(), INTERVAL 3 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (8, 'ORD-008', 8, 80000.00, DATE_SUB(SYSDATE(), INTERVAL 3 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (8, 8, 8, 8, 40000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 3 DAY), DATE_SUB(SYSDATE(), INTERVAL 3 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (9, 'ORD-009', 9, 90000.00, DATE_SUB(SYSDATE(), INTERVAL 3 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (9, 9, 9, 9, 45000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 3 DAY), DATE_SUB(SYSDATE(), INTERVAL 3 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (10, 'ORD-010', 10, 100000.00, DATE_SUB(SYSDATE(), INTERVAL 3 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (10, 10, 10, 10, 50000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 3 DAY), DATE_SUB(SYSDATE(), INTERVAL 3 DAY));

-- DAY -2
INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (11, 'ORD-011', 11, 20000.00, DATE_SUB(SYSDATE(), INTERVAL 2 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (11, 11, 1, 1, 10000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 2 DAY), DATE_SUB(SYSDATE(), INTERVAL 2 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (12, 'ORD-012', 12, 40000.00, DATE_SUB(SYSDATE(), INTERVAL 2 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (12, 12, 2, 2, 20000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 2 DAY), DATE_SUB(SYSDATE(), INTERVAL 2 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (13, 'ORD-013', 13, 30000.00, DATE_SUB(SYSDATE(), INTERVAL 2 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (13, 13, 3, 3, 30000.00, 1, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 2 DAY), DATE_SUB(SYSDATE(), INTERVAL 2 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (14, 'ORD-014', 14, 40000.00, DATE_SUB(SYSDATE(), INTERVAL 2 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (14, 14, 4, 4, 20000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 2 DAY), DATE_SUB(SYSDATE(), INTERVAL 2 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (15, 'ORD-015', 15, 50000.00, DATE_SUB(SYSDATE(), INTERVAL 2 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (15, 15, 5, 5, 50000.00, 1, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 2 DAY), DATE_SUB(SYSDATE(), INTERVAL 2 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (16, 'ORD-016', 16, 60000.00, DATE_SUB(SYSDATE(), INTERVAL 2 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (16, 16, 6, 6, 30000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 2 DAY), DATE_SUB(SYSDATE(), INTERVAL 2 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (17, 'ORD-017', 17, 70000.00, DATE_SUB(SYSDATE(), INTERVAL 2 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (17, 17, 7, 7, 35000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 2 DAY), DATE_SUB(SYSDATE(), INTERVAL 2 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (18, 'ORD-018', 18, 80000.00, DATE_SUB(SYSDATE(), INTERVAL 2 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (18, 18, 8, 8, 40000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 2 DAY), DATE_SUB(SYSDATE(), INTERVAL 2 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (19, 'ORD-019', 19, 90000.00, DATE_SUB(SYSDATE(), INTERVAL 2 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (19, 19, 9, 9, 45000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 2 DAY), DATE_SUB(SYSDATE(), INTERVAL 2 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (20, 'ORD-020', 20, 100000.00, DATE_SUB(SYSDATE(), INTERVAL 2 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (20, 20, 10, 10, 50000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 2 DAY), DATE_SUB(SYSDATE(), INTERVAL 2 DAY));

-- DAY -1
INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (21, 'ORD-021', 21, 20000.00, DATE_SUB(SYSDATE(), INTERVAL 1 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (21, 21, 1, 1, 10000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 1 DAY), DATE_SUB(SYSDATE(), INTERVAL 1 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (22, 'ORD-022', 22, 40000.00, DATE_SUB(SYSDATE(), INTERVAL 1 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (22, 22, 2, 2, 20000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 1 DAY), DATE_SUB(SYSDATE(), INTERVAL 1 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (23, 'ORD-023', 23, 30000.00, DATE_SUB(SYSDATE(), INTERVAL 1 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (23, 23, 3, 3, 30000.00, 1, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 1 DAY), DATE_SUB(SYSDATE(), INTERVAL 1 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (24, 'ORD-024', 24, 40000.00, DATE_SUB(SYSDATE(), INTERVAL 1 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (24, 24, 4, 4, 20000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 1 DAY), DATE_SUB(SYSDATE(), INTERVAL 1 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (25, 'ORD-025', 25, 50000.00, DATE_SUB(SYSDATE(), INTERVAL 1 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (25, 25, 5, 5, 50000.00, 1, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 1 DAY), DATE_SUB(SYSDATE(), INTERVAL 1 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (26, 'ORD-026', 26, 60000.00, DATE_SUB(SYSDATE(), INTERVAL 1 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (26, 26, 6, 6, 30000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 1 DAY), DATE_SUB(SYSDATE(), INTERVAL 1 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (27, 'ORD-027', 27, 70000.00, DATE_SUB(SYSDATE(), INTERVAL 1 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (27, 27, 7, 7, 35000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 1 DAY), DATE_SUB(SYSDATE(), INTERVAL 1 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (28, 'ORD-028', 28, 80000.00, DATE_SUB(SYSDATE(), INTERVAL 1 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (28, 28, 8, 8, 40000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 1 DAY), DATE_SUB(SYSDATE(), INTERVAL 1 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (29, 'ORD-029', 29, 90000.00, DATE_SUB(SYSDATE(), INTERVAL 1 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (29, 29, 9, 9, 45000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 1 DAY), DATE_SUB(SYSDATE(), INTERVAL 1 DAY));

INSERT INTO orders (order_id, order_code, user_id, total_price, order_dt, status)
VALUES (30, 'ORD-030', 30, 100000.00, DATE_SUB(SYSDATE(), INTERVAL 1 DAY), 'O_CMPL');
INSERT INTO order_lines (order_line_id, order_id, user_id, product_line_id, order_line_price, quantity, status, order_dt, order_yymmdd)
VALUES (30, 30, 10, 10, 50000.00, 2, 'O_CMPL', DATE_SUB(SYSDATE(), INTERVAL 1 DAY), DATE_SUB(SYSDATE(), INTERVAL 1 DAY));


-- COUPON 샘플 데이터
INSERT INTO coupon (coupon_id, total_issued, remaining, discount_rate, expire_date, update_dt)
VALUES (1, 100, 100, 20.00, DATE_ADD(SYSDATE(), INTERVAL 90 DAY), SYSDATE());

INSERT INTO coupon (coupon_id, total_issued, remaining, discount_rate, expire_date, update_dt)
VALUES (2, 200, 200, 15.00, DATE_ADD(SYSDATE(), INTERVAL 60 DAY), SYSDATE());

INSERT INTO coupon (coupon_id, total_issued, remaining, discount_rate, expire_date, update_dt)
VALUES (3, 50, 50, 30.00, DATE_ADD(SYSDATE(), INTERVAL 30 DAY), SYSDATE());
