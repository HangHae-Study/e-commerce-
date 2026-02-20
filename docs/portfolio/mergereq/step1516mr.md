### STEP 15 Application Event
- [O] 주문/예약 정보를 원 트랜잭션이 종료된 이후에 전송
- [O] 주문/예약 정보를 전달하는 부가 로직에 대한 관심사를 메인 서비스에서 분리

### STEP 16 Transaction Diagnosis
- [O] 도메인별로 트랜잭션이 분리되었을 때 발생 가능한 문제 파악
- [O] 트랜잭션이 분리되더라도 데이터 일관성을 보장할 수 있는 분산 트랜잭션 설계

---

### 설계문서(보고서)
- 8주차_분산_트랜잭션을_위한_설계문서: [링크](https://github.com/HangHae-Study/e-commerce-/blob/step15/docs/distributed/8%EC%A3%BC%EC%B0%A8_%EB%B6%84%EC%82%B0_%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98%EC%9D%84_%EC%9C%84%ED%95%9C_%EC%84%A4%EA%B3%84%EB%AC%B8%EC%84%9C.md)

---

### 리뷰 포인트
- 기존 주문 수량에 대한 랭킹을 레디스에 반영하는 로직을, `@TransactionalEventListener`와 `EventHandler`를 기반으로 작성하여, 트랜잭션 종료 이후에 전송이 되도록 하였습니다.
    - OrderFacade 및 EventHandler 코드 작성 커밋 : [[7416f40]](https://github.com/HangHae-Study/e-commerce-/commit/7416f40cc6099d1ab19dd61817249b3babefcb17#diff-37e743517de14d5accd0bb3c221bae90ecd76249e76acb45a8e7485ce8a75914)

- 코치님이 알려주신데로, OrderSaga 클래스를 작성하여서 내부에서 로직 흐름을 하나의 유즈 케이스 처럼 보장 하였습니다.
    - 기존의 통합 및 다중 락을 기반으로 하였던, 테스트 코드 또한 모두 잘 통과하였습니다. :D
    - OrderSaga 클래스 작성 및 기존 코드 수정 커밋 : [[46884ea]](https://github.com/HangHae-Study/e-commerce-/commit/46884ead350e267f8876cdb6de703a57c5030cb9#diff-46e618cf2e7c134b643c354ab186bbd30cc1213c56d893f6a9c15416d6b6d248)
    - 설계 문서의 마지막([아쉬웠던 점](https://github.com/HangHae-Study/e-commerce-/blob/step15/docs/distributed/8%EC%A3%BC%EC%B0%A8_%EB%B6%84%EC%82%B0_%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98%EC%9D%84_%EC%9C%84%ED%95%9C_%EC%84%A4%EA%B3%84%EB%AC%B8%EC%84%9C.md#6-%EC%95%84%EC%89%AC%EC%9B%A0%EB%8D%98-%EC%A0%90)) 내용에 대한 질문이 있습니다.

        1. 상태 머신 관리를 위한 `transition` 코드를 모든 호출 서비스 전에 계속 해서 기록하는 형태로 코드를 작성한 것이 적절할까요..? [[코드]](https://github.com/HangHae-Study/e-commerce-/blob/step15/src/main/java/kr/hhplus/be/server/domain/order/application/saga/OrderSaga.java#L47)

      >    제가 작성한 방식이 틀린 방법은 아니라고 생각하지만,, 그렇다고 맞는 방식 처럼도 느껴지진 않습니다 ..ㅠ
      좀 더 깔끔하고 명확한 코드 구조를 가져갈 수 있는 방법이 있을지 고민입니다..
      (AOP를 통해서 특정 어노테이션 을 기반으로 한 상태머신 관리(관심사 분리)를 진행한다면??)

        2. OrderSaga 클래스를 생성 시 내부에서 서비스 함수를 호출하기 위해 관련된 객체를 가지는 것이 당연한 구조가 될 것 같습니다. 매 요청마다 객체를 생성하여, 빈으로 등록된 서비스 인스턴스를 생성자를 통해 코드에서 직접 주입 하는 형태로 작성한 결과물은.... 꽤나 보기 좋지 않은 형태를 띕니다.. [[코드]](https://github.com/HangHae-Study/e-commerce-/blob/step15/src/main/java/kr/hhplus/be/server/domain/order/application/facade/OrderFacade.java#L101)

      > 이런 형태에 대해서 OrderSaga에 필드를 직접 주입하는 방식이 적절한지 한 번 검토해주시면 감사하겠습니다.

- 주문 결제를 시작할 때, orderId에 대한 레디스 락 키를 가지고 결제가 진행되도록 하였습니다.
    - 주문에 대한 중복 결제가 들어왔을 때, 한 주문이 처리 완료되고 다른 주문이 주문을 시작한다면 `Order.isPending` 상태에 의해 예외 처리가 되도록,,
    - 또한, Order 도메인 객체 내에 상태를 성공으로 변경하는 함수에서도 현재 주문에 대한 상태를 확인하도록 하였습니다.
```
 1. 결제 요청 
 2. OrderFacade -> 주문 키 락,  (@Transactional 없이)
     2-1. OrderSaga 생성 및 사가 start (@Transactional 없이)
     2-2. 재고 차감  (호출부가 아닌 구현부에서 상품 키 락, @Transactional)
     2-3. 포인트 차감(호출부가 아닌 구현부에서 유저 키 락, @Transactional)
     2-4. 주문 상태 변경(호출부가 아닌 구현부에서 @Transactional)
3. 결제 완료
```
ㄴ 2번 단계에서 주문에 대한 하나의 키를 잡고, 사가 코드가 시작될 때 내부에 리소스에 대한 락을 구현부에서 관리하며
ㅤ`@Transactional` 기반의 분산 트랜잭션을 유도하는 구조가 괜찮을 것이라고 생각해서 진행해보았는데요.
> - 서로 다른 유저간의 주문키가 겹칠 일은 없음.
> - 주문키 외 다른 리소스에 대한 동시 접근은, 분산 트랜잭션 및 분산락을 통해 해결
> - 위와 같은 구조가 적절한 설계인지 한 번 확인해주시면 감사하겠습니다.
---

### **간단 회고** (3줄 이내)
- **잘한 점**: 고민 했던 사가 패턴을 클래스 형태로 어찌됐든 구현하였고, 그에 대한 분산 트랜잭션이 정상동작하는 것을 테스트코드로 확인하였습니다.
- **어려운 점**: 사가 패턴을 모놀리식 구조의 프로젝트에서 클래스 코드로만 구현하려니,, 다소 부족한 점이 느껴졌던 것 같습니다.
- **다음 시도**: 롤백 로직(보상 트랜잭션)에 대해서 비동기적으로 수행할 수 있는 이벤트 방식을 고민해보고, 추후에는 복합 로직에 대한 결합도를 낮출 수 있는 코레오그래피 방식도 고려해보는 것이 좋은 방향이 될 것 같습니다.
