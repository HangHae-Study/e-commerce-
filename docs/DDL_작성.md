
```sql
-- 1. 사용자 테이블
CREATE TABLE user (
    user_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
COMMENT='유저 정보';
```

```sql
-- 2. 포인트 테이블
CREATE TABLE point (
    point_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id INT UNSIGNED NOT NULL,
    balance INT NOT NULL DEFAULT 0 COMMENT '현재 잔액',
    update_dt DATETIME NOT NULL
        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    PRIMARY KEY (point_id),
    KEY idx_points_user (user_id),
    
    CONSTRAINT fk_points_user
    FOREIGN KEY (user_id) REFERENCES user(user_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
COMMENT='유저별 포인트 잔액';


-- 3. 포인트 내역 테이블
CREATE TABLE point_record (
    point_record_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    point_id INT UNSIGNED NOT NULL,
    user_id INT UNSIGNED NOT NULL,
    amount INT NOT NULL COMMENT '변경된 포인트 금액 (+충전, -사용)',
    type ENUM('충전','사용') NOT NULL,
    update_dt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (point_record_id),
    KEY idx_pr_point (point_id),
    KEY idx_pr_user  (user_id),
    
    CONSTRAINT fk_pr_point
    FOREIGN KEY (point_id) REFERENCES point(point_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    
    CONSTRAINT fk_pr_user
    FOREIGN KEY (user_id) REFERENCES user(user_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
COMMENT='포인트 충전/사용 내역';
```
---
```sql
-- 4. 상품 테이블
CREATE TABLE product (
    product_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '상품 식별자',
    name       VARCHAR(200)    NOT NULL               COMMENT '상품명',
    price      DECIMAL(12,2)   NOT NULL               COMMENT '기본 상품 금액',
    update_dt  DATETIME        NOT NULL
    DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP   COMMENT '수정 시각',
PRIMARY KEY (product_id)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
COMMENT='상품 정보';


-- 5. 상품 라인(옵션)
CREATE TABLE product_line (
    product_line_id INT UNSIGNED PRIMARY KEY,
    product_id      INT UNSIGNED NOT NULL,
    line_price      DECIMAL(12,2) NOT NULL,
    line_type       VARCHAR(100)  NOT NULL,
    remaining       INT NOT NULL DEFAULT 0,
    update_dt       DATETIME       NOT NULL
      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX(idx_pl_product) (product_id),
    FOREIGN KEY (product_id) REFERENCES product(product_id)  
      ON DELETE CASCADE
)ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
COMMENT='상품 라인(옵션)';
```

---
```sql
-- 1. 주문 테이블
CREATE TABLE `orders` (
  order_id       VARCHAR(50) NOT NULL '주문 식별자',
  user_id        INT UNSIGNED NOT NULL                  COMMENT '유저 식별자 (FK)',
  total_price   DECIMAL(12,2)   NOT NULL               COMMENT '주문 총 금액',
  order_dt       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '주문 일시',
  status         ENUM('O_WAIT','O_CMPL','O_FAIL') NOT NULL DEFAULT '주문 대기' 
      NOT NULL DEFAULT 'NEW' COMMENT '주문 상태',
  update_dt      DATETIME        NOT NULL 
      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시각',
    
  PRIMARY KEY (order_id),
  KEY idx_order_user (user_id),
  CONSTRAINT fk_order_user
    FOREIGN KEY (user_id) REFERENCES user(user_id)
      ON DELETE RESTRICT
      ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
COMMENT = '주문 정보';

-- 2. 주문 LINE 테이블
CREATE TABLE order_line (
  order_line_id   INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '주문 LINE 식별자',
  order_id        VARCHAR(50) NOT NULL                  COMMENT '주문 식별자 (FK)',
  product_id      INT UNSIGNED NOT NULL                  COMMENT '상품 식별자 (FK)',
  product_line_id INT UNSIGNED NOT NULL                  COMMENT '상품 LINE 식별자 (FK)',
  order_price    DECIMAL(12,2)   NOT NULL               COMMENT '주문 금액 (발생 시점)',
  quantity        INT              NOT NULL DEFAULT 1    COMMENT '주문 수량',
  coupon_yn  VARCHAR(1)       NOT NULL DEFAULT 'N'    COMMENT '쿠폰 적용 여부 (N=미적용,Y=적용)',
  coupon_code     VARCHAR(100)     DEFAULT NULL           COMMENT '쿠폰 번호',
  discount_price DECIMAL(12,2)    NOT NULL DEFAULT 0    COMMENT '할인 적용 금액',
  status          ENUM('O_WAIT','O_CMPL','O_FAIL') NOT NULL DEFAULT '주문 대기' COMMENT '주문 LINE 상태',
  order_dt        DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '주문 일시 (중복 저장)',
  user_id         INT UNSIGNED     NOT NULL               COMMENT '유저 식별자 (FK)',
  update_dt       DATETIME         NOT NULL 
      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시각',
  PRIMARY KEY (order_line_id),
  KEY idx_ol_order (order_id),
  KEY idx_ol_user  (user_id),
  KEY idx_ol_prod  (product_id),
  KEY idx_ol_pline (product_line_id),
    
  CONSTRAINT fk_ol_order
    FOREIGN KEY (order_id) REFERENCES `orders`(order_id)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
  CONSTRAINT fk_ol_user
    FOREIGN KEY (user_id) REFERENCES user(user_id)
      ON DELETE RESTRICT
      ON UPDATE CASCADE,
  CONSTRAINT fk_ol_product
    FOREIGN KEY (product_id) REFERENCES product(product_id)
      ON DELETE RESTRICT
      ON UPDATE CASCADE,
  CONSTRAINT fk_ol_pline
    FOREIGN KEY (product_line_id) REFERENCES product_line(product_line_id)
      ON DELETE RESTRICT
      ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '주문 상세 내역';
```

---

```sql
CREATE TABLE payment (
    payment_id    INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '결제 내역 식별자',
    user_id       INT UNSIGNED NOT NULL                  COMMENT '유저 식별자 (FK)',
    order_id      VARCHAR(50) NOT NULL                  COMMENT '주문 식별자 (FK)',
    total_amount  DECIMAL(12,2)   NOT NULL               COMMENT '결제 총 금액',
    payment_dt    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '결제 일시',
    status        ENUM(
                   'P_CMPL', -- 결제 성공
                   'P_FAIL'  -- 결제 실패
                 )  COMMENT '결제 상태',
    update_dt     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP 
                                         ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시각',
    PRIMARY KEY (payment_id),
    KEY idx_payment_user  (user_id),
    KEY idx_payment_orders (order_id),
    
    CONSTRAINT fk_payment_user
    FOREIGN KEY (user_id) REFERENCES user(user_id)
      ON DELETE RESTRICT
      ON UPDATE CASCADE,
    CONSTRAINT fk_payment_order
    FOREIGN KEY (order_id) REFERENCES `orders`(order_id)
      ON DELETE RESTRICT
      ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '결제 정보';

```

---

```sql
-- 1. 쿠폰 테이블
CREATE TABLE coupon (
  coupon_id       INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '쿠폰 식별자',
  total_issue     INT            NOT NULL DEFAULT 0       COMMENT '총 발급 개수',
  remaining       INT            NOT NULL DEFAULT 0       COMMENT '남은 쿠폰 개수',
  discount_rate   DECIMAL(5,2)   NOT NULL                  COMMENT '할인율(%)',
  expire_date     DATE           NOT NULL                  COMMENT '쿠폰 만료일자',
  update_dt       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP 
                                             ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시각',
  
  PRIMARY KEY (coupon_id)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '쿠폰 기본 정보';


-- 2. 쿠폰 발급 내역 테이블
CREATE TABLE coupon_issue (
  coupon_issue_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '쿠폰 발급 내역 식별자',
  coupon_code   VARCHAR2(100)    NOT NULL               COMMENT '쿠폰 번호(PK)',
  coupon_id       INT UNSIGNED     NOT NULL               COMMENT '쿠폰 식별자 (FK)',
  user_id         INT UNSIGNED     NOT NULL               COMMENT '유저 식별자 (FK)',
  coupon_valid    ENUM('Y','N')    NOT NULL DEFAULT 'Y'   COMMENT '유효 쿠폰 여부',
  discount_rate   DECIMAL(5,2)     NOT NULL               COMMENT '할인율(%)',
  expire_date     DATE             NOT NULL               COMMENT '쿠폰 만료일자',
  issue_dt        DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '쿠폰 발급일시',
  update_dt       DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP 
                                               ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시각',
  
  PRIMARY KEY (coupon_issue_id, coupon_code),
  KEY idx_ci_coupon (coupon_id),
  KEY idx_ci_user   (user_id),
  
  CONSTRAINT fk_ci_coupon
    FOREIGN KEY (coupon_id) REFERENCES coupon(coupon_id)
      ON DELETE CASCADE
      ON UPDATE CASCADE,
  CONSTRAINT fk_ci_user
    FOREIGN KEY (user_id) REFERENCES user(user_id)
      ON DELETE CASCADE
      ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '쿠폰 발급 및 소지 정보';

```