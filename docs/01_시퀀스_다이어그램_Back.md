### 잔액 충전 / 조회 API

```mermaid
sequenceDiagram
    actor Client
    participant PointController
    participant PointService
    participant UserRepo
    participant PointRepo

    Note left of Client: 잔액 조회 API
    Client->>PointController: GET /points/{userId}
    PointController->>PointService: 포인트 조회 호출(userId)
    PointService->>UserRepo: 유저 조회(userId)
    alt 유저 존재
        UserRepo->>PointService: User 정보
        PointService->>PointRepo: 유저 포인트 조회(userId)
        PointRepo->>PointService: Point with userId
        PointService->>PointController: Point 정보
        PointController->>Client: 200 OK<br/>
    else 유저 미존재(orElseThrow) 
        UserRepo->>PointService: Optional with null
        PointService->>PointController: 예외 발생
        PointController->>Client: 404 Not Found
    end
```
```mermaid
sequenceDiagram
    actor Client
    participant PointController
    participant PointService
    participant UserRepo
    participant PointRepo

    Note left of Client: 잔액 충전 API
    Client->>PointController: PATCH /points/{userId}

    alt Valid Point
        PointController->>PointService: 유저 충전 호출(userId)
        PointService->>UserRepo: 유저 조회(userId)
        alt 유저 존재 
            PointService->>PointRepo: 유저 포인트 조회(userId)
            PointRepo->>PointService: Point with userId
            PointService->>PointService: 포인트 증가, 새 포인트 내역 생성
            create participant PointRecordRepo
            PointService->>PointRecordRepo: 새 포인트 내역 반영
            PointService->>PointController: 새로운 Point 정보
            PointController->>Client: 200 OK
        else 유저 미존재 (orElseThrow)
            PointService->>PointController: 예외 발생
            PointController->>Client: 404 Not Found
        end
    else Invalid Point
        PointController->>Client: 400 Bad Request 
    end
```

---

### 상품 조회 API
```mermaid
sequenceDiagram
    actor Client
    participant ProductController 
    participant ProductService
    participant ProductRepo
    
    Note left of Client: 상품 조회 API
    Client->>ProductController: GET /products
    ProductController->>ProductService: 상품 전체 목록 호출
    ProductService->>ProductRepo: 상품 전체 조회
    ProductRepo->>ProductService: Product List 
    ProductService->>ProductController: Product List 정보
    ProductController->>Client: 200 OK
```


```mermaid
sequenceDiagram
    actor Client
    participant ProductController 
    participant ProductService
    participant ProductLineRepo
    
    Note left of Client: 상품 상세 조회 API
    Client->>ProductController: GET /products/{productsId}
    ProductController->>ProductService: 상품 상세 정보 호출(productId)
    ProductService->>ProductLineRepo: 상품 상세 정보 조회
    alt 유효한 상품 ID
        ProductLineRepo->>ProductService: Product Line List
        ProductService->>ProductController: 상품 상세(구성)List 정보
        ProductController->>Client: 200 OK 
    else 유효하지 않은 상품 ID
        ProductService->>ProductController: 예외 발생
        ProductController->>Client: 404 Not Found
    end
```
---
### 주문/결제 API

OrderFacde의 존재 이유
- 추후에, 레디스나 카프카를 도입할 경우에는 인-메모리 기반의 저장소가 존재하지만,
- 없다면? 나중에 OrderService의 내부에 코드 블럭을 수정/추가할 필요가 있을 것 같습니다.
- 그렇다면, OrderFacade을 두고서, 내부에 메모리 코드를 사용하는 로직을 작성한다면,(또는 Only True로 락 허용 가정)
- 추후에 레디스 or 카프카를 도입할 때가 되어, 해당 외부 메모리를 사용하는 실제 구현체로 대체 가능할 수 있다고 생각하였습니다.
- (Ext Memory은 레디스 or 카프카 또는 다른 캐싱 메모리 구조를 확장하기 위함)

```mermaid
sequenceDiagram
    actor Client
    participant OrderController
    participant OrderService
    participant OrderFacade
    participant Ext Memory
    participant OrderRepo
    participant CouponRepo

    Note left of Client: 주문 식별자<br/> 발행 API
    Client->>OrderController: GET /orders/key
    OrderController->>OrderService: 상품 식별자 발행 호출
    OrderService->>OrderFacade: 상품 식별자 내부 동작 실행
    OrderFacade->>OrderFacade: 상품 UUID 발행
    OrderFacade->>Ext Memory: 상품 UUID 캐싱
    Ext Memory->>+Ext Memory: UUID 최신화(TTL 적용)
    OrderFacade->>OrderService: 상품 식별자
    OrderService->>OrderController: 상품 식별자 정보<br/>orderId
    OrderController->Client: 200 OK
    
    Note left of Client: 주문 생성 API
    Client->>OrderController: POST /orders<br/>{orderId, 상품 선택 목록, 쿠폰 정보}
    OrderController->>OrderService: 상품 주문 호출<br/>(orderId, 상품 목록, 쿠폰 정보)
    OrderService->>OrderFacade: 상품 식별자<br/>유효성 확인동작 실행
    OrderFacade->>Ext Memory: 상품 식별자 유효성 확인
    alt valid orderId
        Ext Memory->>OrderFacade: 상품 식별자 유효 O
        OrderFacade->>Ext Memory: 상품 식별자 사용 완료
        OrderFacade->>OrderService: 정상적인 주문 요청
        OrderService->>OrderService: 상품 목록 기반, 주문 정보 생성
        alt 쿠폰 사용
            OrderService->>CouponRepo: 쿠폰 적용, 쿠폰 상태 변경
            OrderService->>OrderService: 할인 금액 반영
        end
        OrderService->>OrderRepo: 주문 정보 생성
        OrderService->>OrderController: 신규 주문 정보
        OrderController->>Client: 200 OK
    else
        Ext Memory->>OrderFacade: 상품 식별자 유효 X
        OrderFacade->>OrderService: 비정상적인 주문 요청
        OrderService->>OrderController: 예외 발생
        OrderController->>Client: 409 Conflict
    end
```

```mermaid
sequenceDiagram
    actor Client
    participant OrderController
    participant OrderService
    participant OrderLineRepo
    participant ProductLineService
    participant PaymentService
    participant PointRepo
    participant PaymentRepo

    Client->>OrderController: Post /payments<br/>{orderId}
    OrderController->>OrderService: 주문 ID 기반 결제 호출<br/>(orderId)
    OrderService->>OrderLineRepo: 주문 ID에 속한 상품 내역 조회
    OrderLineRepo->>OrderService: 주문 LINE NO 기준 정보 반환
    OrderService->>ProductLineService: 재고 수량 감소 호출
    ProductLineService->>ProductLineService: 재고 수량 충분 검증(+Mem)
    alt 재고 수량 충분
        ProductLineService->>ProductLineService: 재고 수량 감소(+Repo)
        ProductLineService->>OrderService: 재고 수량 감소 완료
        OrderService->>PaymentService: 주문 결제 요청(+포인트 차감)
        PaymentService->>PointRepo: 포인트 조회
        PointRepo->>PaymentService: 현재 포인트 조회 완료
        alt 포인트 >= 주문 금액
            PaymentService->>PointRepo: 포인트 차감
            PaymentService->>PaymentService: 결제 정보 생성
            PaymentService->>PaymentRepo: 결제 정보 저장
            PaymentService->>OrderService: 결제 성공(+포인트 차감 성공)<br/>Payment
            OrderService->OrderController: Payment 정보(결제 완료)
            OrderController->>Client: 200 OK
        else 포인트 < 주문 금액
            PaymentService->>OrderService: 결제 실패
            OrderService->>ProductLineService: 재고 수량 감소 원복 호출
            ProductLineService->>ProductLineService: 재고 수량 원복 (+Mem, +Repo)
            OrderService->>OrderController: 예외 발생
            OrderController->>Client: 400 Bad Request 
        end
    else 재고 수량 부족
        ProductLineService->>OrderService: 재고 수량 감소 실패
        OrderService->>OrderController: 예외 발생
        OrderController->>Client: 409 Conflict
    end
```

---
### 선착순 쿠폰 API
```mermaid
sequenceDiagram
    actor Client
    participant CouponController
    participant RedisCouponService
    participant Redis
    participant CouponIssueRepo
    participant CouponTable

    Client->>CouponController: POST /coupons<br/>{userId, couponId}
    CouponController->>RedisCouponService: issueCoupon(userId,1)
    RedisCouponService->>Redis: DECR coupon:remaining:1
    alt remaining ≥ 0
        Redis-->>RedisCouponService: newRemaining
        RedisCouponService->>CouponIssueRepo: save(issueRecord)
        CouponIssueRepo->>CouponTable: INSERT coupon_issue
        RedisCouponService-->>CouponController: couponCode
        CouponController-->>Client: 200 OK (code)
    else remaining < 0
        Redis-->>RedisCouponService: newRemaining(-1)
        RedisCouponService->>Redis: INCR coupon:remaining:1
        RedisCouponService-->>CouponController: BusinessException
        CouponController-->>Client: 409 Conflict
    end
```


### 인기 판매 상품 조회 API
```mermaid
sequenceDiagram
    actor Client
    participant ProductController
    participant OrderLineService
    participant OrderLineRepository
    
    Client->>ProductController: GET  /products/top<br/> {reqParam (Limit: ? => default: 5)}
    alt 잘못된 상위 랭킹 개수 제한 요청
        ProductController->>Client: 400 Bad Request
        
    end
    ProductController->>OrderLineService: 날짜 기준 인기 판매<br/> 상품 목록 호출
    OrderLineService->>OrderLineService: 날짜 기준 조회 범위 데이터 생성
    OrderLineService->>OrderLineRepository: 날짜 기준 조회 요청<br/>(+조회 기준)
    OrderLineRepository->>OrderLineRepository: JOIN Product<br/>WHERE 주문완료, 날짜 범위<br/>GROUP BY 상품 ID 집계<br/>ROW_NUM <= limit
    OrderLineRepository->>OrderLineService: 주문 완료 정보 중<br/>상품 정보 집계<br/>상위 n개 목록 반환<br/>OrderRanking List
    OrderLineService->>ProductController: OrderRanking 목록 정보
    ProductController->>Client: 200 OK
```
