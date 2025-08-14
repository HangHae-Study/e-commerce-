DELETE FROM order_lines WHERE order_id BETWEEN 20001 AND 20100;
DELETE FROM orders      WHERE order_id BETWEEN 20001 AND 20100;
DELETE FROM product_line WHERE product_line_id BETWEEN 1 AND 30;

INSERT INTO users(user_id, username)
VALUES (1, 'user1')
ON DUPLICATE KEY UPDATE username = VALUES(username);

INSERT INTO product_line(
    product_line_id, product_id, product_line_name, product_line_price, product_line_type, remaining, update_dt
)
SELECT
    pl_id,
    1 + ((pl_id - 1) % 10) AS product_id,
    CONCAT('PL-', LPAD(pl_id, 2, '0')) AS product_line_name,
    10.00 + pl_id AS product_line_price,
    CONCAT('T', 1 + ((pl_id - 1) % 5)) AS product_line_type,
    100 AS remaining,
    '2025-07-31 00:00:00' AS update_dt
FROM (
         SELECT  1 AS pl_id UNION ALL SELECT  2 UNION ALL SELECT  3 UNION ALL SELECT  4 UNION ALL SELECT  5
         UNION ALL SELECT  6 UNION ALL SELECT  7 UNION ALL SELECT  8 UNION ALL SELECT  9 UNION ALL SELECT 10
         UNION ALL SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15
         UNION ALL SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20
         UNION ALL SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24 UNION ALL SELECT 25
         UNION ALL SELECT 26 UNION ALL SELECT 27 UNION ALL SELECT 28 UNION ALL SELECT 29 UNION ALL SELECT 30
     ) t
ON DUPLICATE KEY UPDATE
                     product_line_name = VALUES(product_line_name),
                     product_line_price = VALUES(product_line_price),
                     product_line_type = VALUES(product_line_type),
                     remaining = VALUES(remaining),
                     update_dt = VALUES(update_dt);


INSERT INTO orders(order_id, order_code, user_id, total_price, order_dt, status)
SELECT
    20001 + n AS order_id,
    CONCAT('AUG-', LPAD(n + 1, 3, '0')) AS order_code,
    1 AS user_id,
    0.00 AS total_price, -- 나중에 order_lines 합으로 업데이트
    TIMESTAMP(
            DATE_ADD('2025-08-01', INTERVAL (n % 31) DAY),
            MAKETIME((n * 37) % 24, (n * 13) % 60, 0)
    ) AS order_dt,
    'O_CMPL' AS status
FROM (
         SELECT ones.n + tens.n * 10 AS n
         FROM (
                  SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                  UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
              ) AS ones
                  CROSS JOIN (
             SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
             UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
         ) AS tens
     ) nums
WHERE n < 100;


INSERT INTO order_lines(
    order_id, user_id, product_line_id,
    order_line_price, quantity, status, order_dt, update_dt
)
SELECT
    20001 + nums.n AS order_id,
    1 AS user_id,
    (( (nums.n * 7) + (lg.line_idx * 3) ) % 30) + 1 AS product_line_id,
    ROUND( (pl.product_line_price * ( ((nums.n * lg.line_idx) % 5) + 1 )), 2 ) AS order_line_price,
    ((nums.n * lg.line_idx) % 5) + 1 AS quantity,
    'O_CMPL' AS status,
    TIMESTAMP(
            DATE_ADD('2025-08-01', INTERVAL (nums.n % 31) DAY),
            MAKETIME((nums.n * 37) % 24, (nums.n * 13) % 60, 0)
    ) AS order_dt,
    TIMESTAMP(
            DATE_ADD('2025-08-01', INTERVAL (nums.n % 31) DAY),
            MAKETIME((nums.n * 37) % 24, (nums.n * 13) % 60, 0)
    ) AS update_dt
FROM (
         SELECT ones.n + tens.n * 10 AS n
         FROM (
                  SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                  UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
              ) AS ones
                  CROSS JOIN (
             SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
             UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
         ) AS tens
     ) nums
         JOIN (
    SELECT 1 AS line_idx UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
) lg
              ON lg.line_idx <= ((nums.n % 4) + 1)        -- 주문당 1~4 라인
         JOIN product_line pl
              ON pl.product_line_id = (((nums.n * 7) + (lg.line_idx * 3)) % 30) + 1
WHERE nums.n < 100;


UPDATE orders o
    JOIN (
        SELECT order_id, SUM(order_line_price) AS total_price
        FROM order_lines
        WHERE order_id BETWEEN 20001 AND 20100
        GROUP BY order_id
    ) t ON t.order_id = o.order_id
SET o.total_price = t.total_price
WHERE o.order_id BETWEEN 20001 AND 20100;
