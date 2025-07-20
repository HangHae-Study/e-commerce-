### 엔티티

- 사용자
  - 유저 식별자(user_id)
  - 유저명


- 포인트
  - 포인트 식별자(point_id)
  - 유저 식별자(user_id)
  - 잔액 (balance)
  - update_dt

- 포인트 내역
  - 포인트 내역 식별자(point_record_id)
  - 포인트 식별자(point_id)
  - 유저 식별자(user_id)
  - 포인트 금액
  - 포인트 타입(충전, 사용)
  - update_dt


- 상품
  - 상품 식별자(product_id)
  - 상품명
  - 상품 금액
  - update_dt
- 상품 LINE
  - 상품 식별자(product_id)
  - 상품 LINE 식별자(product_line_id)
  - 상품 금액
  - 상품 타입(추가 정보)
  - 재고 수량,잔량
  - update_dt



- 주문
  - 주문 식별자 (order_id)
  - 유저 식별자 (user_id)
  - 주문 총 금액
  - 주문 일시
  - 주문 상태
  - update_dt
- 주문 LINE (주문 내역)
  - 주문 식별자(order_id)
  - 주문 LINE 식별자(order_line_id)
  - 상품 식별자 (product_id)
  - 상품 LINE 식별자(product_line_id)
  - 주문 금액 (주문 발생 시점 당시 금액)
  - 주문 수량
  - 쿠폰 적용 여부
  - 쿠폰 번호(코드)
  - 할인 적용 금액
  - 주문 상태
  - 주문 일시
  - 유저 식별자 (user_id)
  - update_dt


- 결제
  - 결제 내역 식별자(payment_id)
  - 유저 식별자(user_id)
  - 주문 식별자(order_id)
  - 결제 총 금액
  - 결제 일시
  - 결제 상태
  - update_dt


- 쿠폰
  - 쿠폰 식별자 (coupon_id)
  - 쿠폰 총 발급 개수
  - 쿠폰 잔량
  - 쿠폰 내역(할인율)
  - 쿠폰 만료일자

- 쿠폰 발급 내역 
  - 쿠폰 식별자(coupon_id)
  - 쿠폰 번호(coupon_issue_id)
  - 유저 식별자 (user_id)
  - 유효 쿠폰 여부 (coupon_valid)
  - 쿠폰 내역(할인율)
  - 쿠폰 만료 일자
  - 쿠폰 발급일

