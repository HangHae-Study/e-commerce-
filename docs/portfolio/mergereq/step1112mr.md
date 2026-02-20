### **핵심 체크리스트** :white_check_mark:

#### :one: 분산락 적용 (3개)
- [O] 적절한 곳에 분산락이 사용되었는가?
- [O] 트랜젝션 순서와 락순서가 보장되었는가? ->`@Order(Ordered.HIGHEST_PRECEDENCE)`
> 1) 코치님 께서 분산락을 통해 DB에 부담을 주지 않아도 된다면, 굳이 이전의 비/낙관적 락을 유지할 필요가 없다고 해서 일부분, @Lock을 이용한 조회를 제외하였습니다.
> 2) 쿠폰/충전의 경우 AOP와 어노테이션을 적용하여, 락을 걸어서 진행할 수 있었습니다.
> 3) 결제(주문완료)의 경우, 한 주문 안에 여러 상품이 존재할 수 있으므로, 키를 파싱할 때 콜렉션 타입도 멀티 락에 포함될 수 있도록 하였습니다.
     >   - 포인트의 경우 lock:point:{userId}
>   - 쿠폰의 경우 lock:coupon:{couponId}
>   - 결제의 경우 lock:order:{orderId}, lock:stock:{productLineId}, lock:point:{userId}
      > 와 같은 형태로 키를 할당 하였습니다.

#### :two: 통합 테스트 (2개)
- [O] infrastructure 레이어를 포함하는 통합 테스트가 작성되었는가?
- [O] 핵심 기능에 대한 흐름이 테스트에서 검증되었는가?
- [O] 동시성을 검증할 수 있는 테스트코드로 작성 되었는가?
- [O] Test Container 가 적용 되었는가?
> 1) 이전에 DB락을 통해서 실행했던 테스트 코드들이, 분산락 적용 이후에도 Facade Layer에서 모두 통과되는 것을 확인하였습니다.
> 2) 테스트 컨테이너 O

#### :three: Cache 적용 (3개)
- [O] 적절하게 Key 적용이 되었는가?
> - 키만 보고도 해당 캐시가 어떤 데이터를 담는지 나타내기 위해
> - `cache:top-order-products:{start}:{end}` - 지난 3일간 인기 상품 상위 5개 와 같은 형태로 키를 선정하였습니다.

---
#### STEP11 `링크 참조 바랍니다`
- [O] Redis 분산락 적용 => ([AOP 컨피그 클래스 링크](https://github.com/HangHae-Study/e-commerce-/blob/step11/src/main/java/kr/hhplus/be/server/config/aop/lock/DistributedLockAspect.java#L45))
- [O] Test Container 구성
- [O] 기능별 통합 테스트

#### STEP12
- [O] 캐시 필요한 부분 분석
- [O] redis 기반의 캐시 적용 => ([ProductFacade.java - 조회 시 캐시 확인](https://github.com/HangHae-Study/e-commerce-/blob/step11/src/main/java/kr/hhplus/be/server/domain/product/application/facade/ProductFacade.java#L69))
- [O] 성능 개선 등을 포함한 보고서 제출 => ([지난 3일간 인기 상품 Top5 캐시 사용/미사용 보고서](https://github.com/HangHae-Study/e-commerce-/blob/step11/docs/cache/%EC%A7%80%EB%82%9C_3%EC%9D%BC%EA%B0%84_%EC%83%81%EC%9C%84_%ED%8C%90%EB%A7%A4_%EC%83%81%ED%92%88_%EC%A1%B0%ED%9A%8C.md))

---

### 리뷰 포인트
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

---

### **간단 회고** (3줄 이내)
- **잘한 점**: 캐시 사용/미사용에 대한 실제 시간을 확인해볼 수 있었습니다.
- **어려운 점**: 분산락을 적용했지만,, 제가 한 방법에 대한 확신은 잘 들지 않습니다..
- **개선할 점** : 현재 패키지/클래스 구조가 도메인별로 일치하지 않는 부분들이 있어, 조금 더 깔끔한 프로젝트 구조를 가져갈 수 있도록 리팩토링이 필요합니다.
