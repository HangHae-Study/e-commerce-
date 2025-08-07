SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE point_records;
TRUNCATE TABLE points;
TRUNCATE TABLE users;

-- 1) 단일 유저 & 충분한 포인트
INSERT INTO users (user_id, username) VALUES
    (1, 'user1');
INSERT INTO points (point_id, user_id, balance) VALUES
    (1, 1, 1000.00);

SET FOREIGN_KEY_CHECKS = 1;
