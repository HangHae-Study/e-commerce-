-- src/test/resources/sql/noindex_scan.sql

-- (A) users
INSERT INTO users(user_id, username) VALUES (1, 'user1');

-- (B) product_line: 남은 재고 정보 포함
INSERT INTO product_line(
    product_line_id, product_id, product_line_name, product_line_price, product_line_type, remaining, update_dt
) VALUES
      ( 1, 1, 'Line-A', 11.00,'T1', 5, '2025-07-31 00:00:00'),  -- 재고 있음
      ( 2, 1, 'Line-B', 12.00,'T2', 0, '2025-07-31 00:00:00'),  -- 재고 없음
      ( 3, 2, 'Line-C', 21.00,'T3',10, '2025-07-31 00:00:00');  -- 재고 있음

-- (C) orders: 날짜별, 상태별로 일부만 포함
INSERT INTO orders(order_id, order_code, user_id, total_price, order_dt, status) VALUES
     -- 2025-07-28 (3일 전 경계 밖) 완료
     (10,'OLD-001',1,100.00,'2025-07-28 10:00:00','O_CMPL'),
     -- 2025-07-29~31 (최근 3일), 완료(=O_CMPL)만
     (11,'NEW-A',1,150.00,'2025-07-29 11:00:00','O_CMPL'),
     (12,'NEW-B',1,200.00,'2025-07-30 12:00:00','O_CMPL'),
     (13,'NEW-C',1,120.00,'2025-07-31 13:00:00','O_CMPL'),
     -- 미완료 주문 (O_MAKE)는 제외 대상
     (14,'NEW-D',1,130.00,'2025-07-31 14:00:00','O_MAKE');

-- (D) order_lines: 각 라인별 수량 합계
INSERT INTO order_lines(
    order_line_id, order_id, user_id, product_line_id,
    order_line_price, quantity, status, order_dt, update_dt
) VALUES
      -- NEW-A: pl1×2, pl2×3
      (101,11,1,1,  50.00,2,'O_CMPL','2025-07-29 11:00:00','2025-07-29 11:00:00'),
      (102,11,1,2,  75.00,3,'O_CMPL','2025-07-29 11:00:00','2025-07-29 11:00:00'),
      -- NEW-B: pl1×1, pl3×4
      (103,12,1,1, 100.00,1,'O_CMPL','2025-07-30 12:00:00','2025-07-30 12:00:00'),
      (104,12,1,3, 105.00,4,'O_CMPL','2025-07-30 12:00:00','2025-07-30 12:00:00'),
      -- NEW-C: pl3×2
      (105,13,1,3,  60.00,2,'O_CMPL','2025-07-31 13:00:00','2025-07-31 13:00:00');
