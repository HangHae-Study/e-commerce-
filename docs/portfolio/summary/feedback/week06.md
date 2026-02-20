# 6주차 - STEP 11 & STEP 12

> Redis 분산락 적용 / Cache 적용

---

## Merge Request

### [STEP 11 & 12] 김성인 - e-commerce

#### 핵심 체크리스트

**분산락 적용**
- [O] 적절한 곳에 분산락이 사용되었는가?
- [O] 트랜젝션 순서와 락순서가 보장되었는가? ->`@Order(Ordered.HIGHEST_PRECEDENCE)`
> 1) 코치님 께서 분산락을 통해 DB에 부담을 주지 않아도 된다면, 굳이 이전의 비/낙관적 락을 유지할 필요가 없다고 해서 일부분, @Lock을 이용한 조회를 제외하였습니다.
> 2) 쿠폰/충전의 경우 AOP와 어노테이션을 적용하여, 락을 걸어서 진행할 수 있었습니다.
> 3) 결제(주문완료)의 경우, 한 주문 안에 여러 상품이 존재할 수 있으므로, 키를 파싱할 때 콜렉션 타입도 멀티 락에 포함될 수 있도록 하였습니다.
>   - 포인트의 경우 lock:point:{userId}
>   - 쿠폰의 경우 lock:coupon:{couponId}
>   - 결제의 경우 lock:order:{orderId}, lock:stock:{productLineId}, lock:point:{userId}
>   와 같은 형태로 키를 할당 하였습니다.

**통합 테스트**
- [O] infrastructure 레이어를 포함하는 통합 테스트가 작성되었는가?
- [O] 핵심 기능에 대한 흐름이 테스트에서 검증되었는가?
- [O] 동시성을 검증할 수 있는 테스트코드로 작성 되었는가?
- [O] Test Container 가 적용 되었는가?
> 1) 이전에 DB락을 통해서 실행했던 테스트 코드들이, 분산락 적용 이후에도 Facade Layer에서 모두 통과되는 것을 확인하였습니다.
> 2) 테스트 컨테이너 O

**Cache 적용**
- [O] 적절하게 Key 적용이 되었는가?
> - 키만 보고도 해당 캐시가 어떤 데이터를 담는지 나타내기 위해
> - `cache:top-order-products:{start}:{end}` - 지난 3일간 인기 상품 상위 5개 와 같은 형태로 키를 선정하였습니다.

---

#### 구현 완료 기능

- STEP11 `링크 참조 바랍니다`
    - [O] Redis 분산락 적용 => ([AOP 컨피그 클래스 링크](https://github.com/HangHae-Study/e-commerce-/blob/step11/src/main/java/kr/hhplus/be/server/config/aop/lock/DistributedLockAspect.java#L45))
    - [O] Test Container 구성
    - [O] 기능별 통합 테스트
- STEP12
    - [O] 캐시 필요한 부분 분석
    - [O] redis 기반의 캐시 적용 => ([ProductFacade.java - 조회 시 캐시 확인](https://github.com/HangHae-Study/e-commerce-/blob/step11/src/main/java/kr/hhplus/be/server/domain/product/application/facade/ProductFacade.java#L69))
    - [O] 성능 개선 등을 포함한 보고서 제출 => ([지난 3일간 인기 상품 Top5 캐시 사용/미사용 보고서](https://github.com/HangHae-Study/e-commerce-/blob/step11/docs/cache/%EC%A7%80%EB%82%9C_3%EC%9D%BC%EA%B0%84_%EC%83%81%EC%9C%84_%ED%8C%90%EB%A7%A4_%EC%83%81%ED%92%88_%EC%A1%B0%ED%9A%8C.md))

---

#### 리뷰 포인트
1. PaymentFacade에서의 트랜잭션 관리 X
- 기존 : 페이먼트 퍼사드에서 모든 트랜잭션 관리 -> 지정된 예외 발생시 트랜잭셔널 전체 롤백
- 변경 : 페이먼트 퍼사드에서 주문 Id를 기반으로 상품라인목록 조회 -> 주문 ID, 상품 라인 ID, 유저 ID락 얻은 후 트랜잭션 진행
> 기존에 비/낙관적락 이라는 이지선다 내에서만, 락을 적용하여야 했던 약간의 답답한 상황 속에서 분산락을 통해 보다 편한 정합성을 보장할 수 있었습니다.

2. 주문 완료 로직에 대해서..
- 유저가 `주문->결제` 이라는 흐름에서 결제(주문완료) 요청 시에 예외가 발생했다고 가정한다면,
    - 예를 들어 유저가 다시 충전하고 동일 주문을 할 수 있도록, `주문 취소` 상태로 변경하지 않고 여전히 `주문 대기`로 두는게 좋을까요.. 아니면 실패 상태로 변경하는 것이 좋을까요?
    - 결제(주문완료) 기능에서 결제에서 `재고차감`/`유저 포인트 감소` 등을 하기보다 주문에서 하도록 코드를 변경하였습니다.
        - `주문 락`->`재고 락`->`포인트 락` 순으로 진행하였는데, 코드 내용이 적절한지 간단한 검토(?) 요청 드리겠습니다 ...ㅎㅎ
        - step11 커밋 : [[87c3ee7]](https://github.com/HangHae-Study/e-commerce-/commit/87c3ee732a0213dfafaff3a275e5a1c6ae66cc26#diff-37e743517de14d5accd0bb3c221bae90ecd76249e76acb45a8e7485ce8a75914)
        - [PaymentFacade.java](https://github.com/HangHae-Study/e-commerce-/blob/step11/src/main/java/kr/hhplus/be/server/domain/payment/application/facade/PaymentFacade.java#L36)
            - [OrderFacde.java](https://github.com/HangHae-Study/e-commerce-/blob/step11/src/main/java/kr/hhplus/be/server/domain/order/application/facade/OrderFacade.java#L105)

#### 간단 회고
- **잘한 점**: 캐시 사용/미사용에 대한 실제 시간을 확인해볼 수 있었습니다.
- **어려운 점**: 분산락을 적용했지만,, 제가 한 방법에 대한 확신은 잘 들지 않습니다..
- **개선할 점** : 현재 패키지/클래스 구조가 도메인별로 일치하지 않는 부분들이 있어, 조금 더 깔끔한 프로젝트 구조를 가져갈 수 있도록 리팩토링이 필요합니다.

---

## Feedback

- 적절한 곳에 분산락이 사용되었는가? - O

- 적절하게 Key 적용이 되었는가? - △

- 유저가 같은날 데이터를 조회할때 다른 날짜기준의 데이터가 필요하지 않은데 여러 키가 필요한가요?

- 캐시 스탬피드 현상이 발생할 것 같은데 방지할 수 있을까요?

- 파사드 레이어를 둔 것은 여러 도메인 서비스를 호출함으로써 단순히 흐름을 관리하고자 하는 역할일거라고 기대했는데, PaymentFacade가 OrderFacade를 호출해도 괜찮은가요?

- 저는 주문의 결제는 Order에서 API를 제공해야하고 따라서 OrderFacade가 중심이 되어야 한다고 생각해요. 주문에 대한 결제로 보는 것이 제 생각에는 좀 더 자연스러운 것 같아요. 성인님도 해당 작업의 비즈니스를 관리하는 주체가 주문이라고 생각했기에 OrderFacade를 호출한 것 같고요. 그렇다면, OrderController -> OrderFacade로 가는게 나을 것 같습니다.

- 유저의 주문시점에 예외가 발생할때 주문의 상태는.. 서비스마다 기획자마다 다른 결정을 내릴 수 있는 부분이라 어떤게 좋은지 제가 답변하기가 모호하네요..ㅎㅎ 성인님이 구현 중인 서비스 flow로 봤을때는 자연스러운 구조일 수 있을 것 같아요.

- 멀티락 구현 잘 해주셨어요. Resource로 멀티락을 잡으면서도 도메인 키를 구분하는 구현이 좋았습니다.
