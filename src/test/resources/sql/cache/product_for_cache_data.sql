INSERT INTO product_line
(product_line_id, product_id, product_line_name, product_line_price, product_line_type, remaining, update_dt)
VALUES
    (1,  1, '상품A 기본형',  10000.00, 'BASIC',   50, NOW()),
    (2,  1, '상품A 고급형',  12000.00, 'DELUXE',  40, NOW()),
    (3,  1, '상품A 한정판',  15000.00, 'DELUXE',  30, NOW()),
    (4,  1, '상품A 세트',    17000.00, 'BASIC',   25, NOW()),
    (5,  1, '상품A 프로',    13000.00, 'BASIC',   60, NOW()),

    (6,  2, '상품B 기본형',  20000.00, 'BASIC',   20, NOW()),
    (7,  2, '상품B 고급형',   9000.00, 'DELUXE',  80, NOW()),
    (8,  2, '상품B 세트',    22000.00, 'DELUXE',  15, NOW()),
    (9,  2, '상품B 패밀리',  11000.00, 'BASIC',   55, NOW()),
    (10, 2, '상품B 프로',    24000.00, 'DELUXE',  10, NOW()),

    (11, 3, '상품C 기본형',   9500.00, 'BASIC',   70, NOW()),
    (12, 3, '상품C 고급형',  18000.00, 'DELUXE',  22, NOW()),
    (13, 3, '상품C 세트',    16000.00, 'BASIC',   18, NOW()),
    (14, 3, '상품C 프로',    12500.00, 'BASIC',   45, NOW()),
    (15, 3, '상품C 패밀리',  26000.00, 'DELUXE',   8, NOW()),

    (16, 4, '상품D 기본형',  10500.00, 'BASIC',   65, NOW()),
    (17, 4, '상품D 고급형',  21000.00, 'DELUXE',  12, NOW()),
    (18, 4, '상품D 세트',    11500.00, 'BASIC',   58, NOW()),
    (19, 4, '상품D 프로',    23000.00, 'DELUXE',   9, NOW()),
    (20, 4, '상품D 패밀리',  12500.00, 'BASIC',   47, NOW()),

    (21, 5, '상품E 기본형',  19500.00, 'BASIC',   13, NOW()),
    (22, 5, '상품E 고급형',   9800.00, 'DELUXE',  72, NOW()),
    (23, 5, '상품E 세트',    25000.00, 'DELUXE',   7, NOW()),
    (24, 5, '상품E 프로',    11200.00, 'BASIC',   63, NOW()),
    (25, 5, '상품E 패밀리',  27500.00, 'DELUXE',   5, NOW()),

    (26, 6, '상품F 기본형',  10000.00, 'BASIC',   52, NOW()),
    (27, 6, '상품F 고급형',  18500.00, 'DELUXE',  19, NOW()),
    (28, 6, '상품F 세트',    13500.00, 'BASIC',   42, NOW()),
    (29, 6, '상품F 프로',    26500.00, 'DELUXE',   6, NOW()),
    (30, 6, '상품F 패밀리',   9900.00, 'BASIC',   68, NOW());


-- 1) 1..100 시퀀스와 매핑 파생 컬럼 생성
WITH RECURSIVE seq(n) AS (
    SELECT 1
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 100
),
               chosen AS (
                   SELECT
                       n                                           AS order_id,
                       ((n - 1) % 20) + 1                          AS user_id,             -- 1..20 사용자 순환
    ((n - 1) % 30) + 1                          AS product_line_id,     -- 1..30 PL 순환
    ((n - 1) % 3) + 1                           AS qty,                 -- 1..3 수량
    LPAD(CAST(n AS CHAR), 4, '0')               AS seq4,
    ((n - 1) % 31) + 1                          AS day_in_aug           -- 1..31일 순환
FROM seq
    ),
    priced AS (
SELECT
    c.order_id,
    c.user_id,
    c.product_line_id,
    c.qty,
    c.seq4,
    c.day_in_aug,
    pl.product_line_price
FROM chosen c
    JOIN product_line pl ON pl.product_line_id = c.product_line_id
    ),
    shaped AS (
SELECT
    p.order_id,
    p.user_id,
    p.product_line_id,
    p.qty,
    p.product_line_price,
    (p.product_line_price * p.qty)          AS line_total,
    -- 2025년 8월 균등 분포된 날짜/시간
    TIMESTAMP(CONCAT('2025-08-', LPAD(p.day_in_aug, 2, '0')),
    MAKETIME(10 + (p.order_id % 8), (p.order_id % 4) * 15, 0)) AS order_dt
FROM priced p
    )

-- 2) orders 100건 삽입 (order_id를 1..100으로 명시)
INSERT INTO orders
(order_id, user_id, total_price, order_code, status, order_dt, update_dt)
SELECT
    s.order_id,
    s.user_id,
    s.line_total                                               AS total_price,
    CONCAT('ORD202508', s.seq4)                                AS order_code,
    'O_MAKE'                                                   AS status,
    s.order_dt,
    NOW()                                                      AS update_dt
FROM shaped s
ORDER BY s.order_id;

-- 3) order_lines 100건 삽입 (각 주문 1라인, 위와 정합성 유지)
INSERT INTO order_lines
(order_id, user_id, product_line_id, quantity, order_line_price, order_dt, order_yymmdd, status, update_dt)
SELECT
    s.order_id,
    s.user_id,
    s.product_line_id,
    s.qty,
    s.product_line_price,
    s.order_dt,
    DATE(s.order_dt)                                           AS order_yymmdd,
    'O_MAKE'                                                   AS status,
    NOW()                                                      AS update_dt
FROM shaped s
ORDER BY s.order_id;
