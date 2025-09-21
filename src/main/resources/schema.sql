DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS point_records;
DROP TABLE IF EXISTS points;
DROP TABLE IF EXISTS product_line;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS order_lines;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS coupon_issue;
DROP TABLE IF EXISTS coupon;
DROP TABLE IF EXISTS payment;

-- USERS
CREATE TABLE users (
                       user_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username    VARCHAR(20),
                       create_dt   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
                       update_dt   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP
);

-- POINTS
CREATE TABLE points (
                        point_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id     BIGINT,
                        balance     DECIMAL(12,2) NOT NULL,
                        update_dt   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP
);

-- POINT RECORDS
CREATE TABLE point_records (
                               point_record_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               point_id        BIGINT,
                               user_id         BIGINT,
                               request_id      VARCHAR(100) NOT NULL UNIQUE,
                               amount          DECIMAL(12,2),
                               type            VARCHAR(10),
                               update_dt       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
                               CONSTRAINT fk_point_record_point FOREIGN KEY (point_id) REFERENCES points(point_id)
);

-- PRODUCT
CREATE TABLE product (
                         product_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
                         product_name  VARCHAR(100) NOT NULL,
                         product_price DECIMAL(12,2) NOT NULL,
                         update_dt     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP
);

-- PRODUCT LINE
CREATE TABLE product_line (
                              product_line_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
                              product_id        BIGINT NOT NULL,
                              product_line_name VARCHAR(100) NOT NULL,
                              product_line_price DECIMAL(12,2),
                              product_line_type VARCHAR(50) NOT NULL,
                              remaining         BIGINT,
                              update_dt         DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP
);

-- ORDERS
CREATE TABLE orders (
                        order_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
                        order_code   VARCHAR(255),
                        user_id      BIGINT NOT NULL,
                        total_price  DECIMAL(12,2),
                        order_dt     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
                        status       VARCHAR(50) NOT NULL,
                        update_dt    DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
                        version      BIGINT DEFAULT 0 NOT NULL
);

-- ORDER LINES
CREATE TABLE order_lines (
                             order_line_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             order_id      BIGINT,
                             user_id       BIGINT NOT NULL,
                             product_line_id BIGINT,
                             order_line_price DECIMAL(12,2),
                             quantity      INT,
                             coupon_yn     VARCHAR(1) DEFAULT 'N' NOT NULL,
                             coupon_code   VARCHAR(255),
                             discount_price DECIMAL(12,2),
                             status        VARCHAR(10) DEFAULT 'O_MAKE' NOT NULL,
                             order_dt      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
                             order_yymmdd  DATE,
                             update_dt     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
                             version       BIGINT DEFAULT 0 NOT NULL,
                             INDEX idx_status_orderyyyy (status, order_yymmdd),
                             CONSTRAINT fk_order_line_order FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

-- PAYMENT
CREATE TABLE payment (
                         payment_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id      BIGINT NOT NULL,
                         order_id     BIGINT NOT NULL,
                         total_price  DECIMAL(12,2) NOT NULL,
                         payment_dt   DATETIME NOT NULL,
                         status       VARCHAR(50),
                         update_dt    DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP
);

-- COUPON
CREATE TABLE coupon (
                        coupon_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
                        total_issued BIGINT,
                        remaining    BIGINT,
                        discount_rate DECIMAL(12,2),
                        expire_date  DATETIME,
                        update_dt    DATETIME
);

-- COUPON ISSUE
CREATE TABLE coupon_issue (
                              coupon_issue_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              coupon_code     VARCHAR(255),
                              coupon_id       BIGINT,
                              user_id         BIGINT,
                              coupon_valid    VARCHAR(50),
                              discount_rate   DECIMAL(12,2),
                              expire_date     DATETIME,
                              version         BIGINT DEFAULT 0 NOT NULL,
                              update_dt       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP
);
