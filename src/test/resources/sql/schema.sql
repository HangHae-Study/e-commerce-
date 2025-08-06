CREATE TABLE users (
                       user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(20),
                       create_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
                       update_dt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE product (
                         product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         product_name VARCHAR(100) NOT NULL,
                         product_price DECIMAL(12,2) NOT NULL,
                         update_dt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE product_line (
                              product_line_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              product_id BIGINT NOT NULL,
                              product_line_name VARCHAR(100) NOT NULL,
                              product_line_price DECIMAL(12,2),
                              product_line_type VARCHAR(255) NOT NULL,
                              remaining BIGINT,
                              update_dt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    -- no FK(product_id)
);

CREATE TABLE coupon (
                        coupon_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        total_issued BIGINT,
                        remaining BIGINT,
                        discount_rate DECIMAL(12,2),
                        expire_date DATETIME,
                        update_dt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE coupon_issue (
                              coupon_issue_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              coupon_code VARCHAR(255),
                              coupon_id BIGINT,
                              user_id BIGINT,
                              coupon_valid VARCHAR(255),
                              discount_rate DECIMAL(12,2),
                              expire_date DATETIME,
                              update_dt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    -- no FK(coupon_id), no FK(user_id)
);

CREATE TABLE orders (
                        order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        order_code VARCHAR(255),
                        user_id BIGINT NOT NULL,
                        total_price DECIMAL(12,2),
                        order_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
                        status VARCHAR(255) NOT NULL,
                        update_dt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    -- no FK(user_id)
);

CREATE TABLE order_lines (
                             order_line_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             order_id BIGINT,
                             user_id BIGINT NOT NULL,
                             product_line_id BIGINT NOT NULL,
                             order_line_price DECIMAL(12,2),
                             quantity INT,
                             coupon_yn VARCHAR(1) NOT NULL DEFAULT 'N',
                             coupon_code VARCHAR(255),
                             dis_count_price DECIMAL(12,2),
                             status VARCHAR(255) NOT NULL,
                             order_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
                             update_dt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                             FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

CREATE TABLE payment (
                         payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         order_id BIGINT NOT NULL,
                         total_price DECIMAL(12,2) NOT NULL,
                         payment_dt DATETIME NOT NULL,
                         status VARCHAR(255),
                         update_dt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    -- no FK(user_id), no FK(order_id)
);

CREATE TABLE points (
                                  point_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  user_id BIGINT,
                                  balance DECIMAL(12,2) NOT NULL,
                                  update_dt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    -- no FK(user_id)
);
