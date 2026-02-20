## :clipboard: 핵심 체크리스트 :white_check_mark:

### STEP09 - Concurrency (2개)
- [O] 애플리케이션 내에서 발생 가능한 **동시성 문제를 식별**했는가?
- [O] 보고서에 DB를 활용한 **동시성 문제 해결 방안**이 포함되어 있는가?
    - 포인트 충전 보고서 : [링크](https://github.com/HangHae-Study/e-commerce-/blob/step09/docs/concurrency/%ED%8F%AC%EC%9D%B8%ED%8A%B8_%EB%9D%BD_%ED%85%8C%EC%8A%A4%ED%8A%B8.md)
    - 쿠폰 사용/발급 보고서 : [링크](https://github.com/HangHae-Study/e-commerce-/blob/step09/docs/concurrency/%EC%BF%A0%ED%8F%B0_%EB%9D%BD_%ED%85%8C%EC%8A%A4%ED%8A%B8.md)
    - 결제(주문완료) 보고서 : [링크](https://github.com/HangHae-Study/e-commerce-/blob/step09/docs/concurrency/%EA%B2%B0%EC%A0%9C_%EB%9D%BD_%ED%85%8C%EC%8A%A4%ED%8A%B8.md)
---

### STEP10 - Finalize (1개)
- [△] **동시성 문제를 드러낼 수 있는 통합 테스트**를 작성했는가?

---

### 리뷰 포인트
- 버전을 도메인으로 옮기는 것에 대해
    - 이 부분은 도메인에 버전칼럼을 넣어, JPA까지 연동이 잘 되는 것을 확인하였습니다

- 트랜잭션의 일관화에 대하여...
    - 초기에 PaymentFacade.process 위에 @Transactional을 달지 않고, 여러 원인으로 인해 실패되는 경우 예외를 받아서 원복 로직을 호출하려 하였습니다..
    - 하지만, 이 경우 주문을 "낙관적 락"으로 하다보니, (재고/포인트는 비관적) 주문을 완료하기 전, 여러 스레드를 통해 요청시 재고가 먼저 감소되어 실패된 주문으로 변경되는 현상이 발생하였습니다...
    -  이 부분 은, 예외 처리 로직에서 직접 주문 실패 상태로 변경하는 함수를 호출하지 않고,, 임시 방편으로 조치하였는데요..
    - 적절한 트랜잭션 전파단위와, JPA에 대한 이해가 더 있었으면 어땟을까 하는 아쉬움이 있습니다...

[질문1]
> 1) 한 트랜잭션에서 주문 도메인도 비관적락으로 가져가는게 맞앗을까요..?
> 2) 현재 하나의 단위로 트랜잭션을 묶어서,,, 처리가 가능한가?라는 의구심을 가지고 결국에 Payment.Process 위에 @Transactional을 달았습니다...
> 3) 좀 더 세부적으로 전파단위를 만들거나, 하위 속성에서 @Transactional을 어떻게 호출하는 것이 맞는지... 간단하게 봐주시면 감사하겠습니다.
     > (Transactional을 PaymentFacade에 달지 않으면,,,,, 주문 완료되기전,,, 실패 발생을 해서... 이런 문제를 해결하려면 어떻게 할지 잘 모르겠습니다..)
>
>- [PaymetnFacade.java](https://github.com/HangHae-Study/e-commerce-/blob/step09/src/main/java/kr/hhplus/be/server/domain/payment/application/facade/PaymentFacade.java)
   >   - [InventoryFacade.java](https://github.com/HangHae-Study/e-commerce-/blob/step09/src/main/java/kr/hhplus/be/server/domain/product/application/facade/InventoryFacade.java#L24)
>   - [UserService.java](https://github.com/HangHae-Study/e-commerce-/blob/step09/src/main/java/kr/hhplus/be/server/domain/user/application/service/UserService.java#L103)
>   -  [OrderService.java](https://github.com/HangHae-Study/e-commerce-/blob/step09/src/main/java/kr/hhplus/be/server/domain/order/application/service/OrderService.java#L46)

[질문2]
> 트랜잭션 전파단위와,,,, 롤백 또는 원복 로직 설계에 대한 노하우와 제가 생각한 기준이 맞는지 판단이 서지 않습니다..
> 제가 생각한 기준이 적절한지 간단한 피드백 부탁드립니다..
> - 쿠폰 발급 : 충돌 가능성 높음(비관적락), 재시도는 쿠폰 발급에 적절하지 않음.
> - 포인트 : 충돌 가능성 낮음(낙관적 락 고려해볼만도), 중복 요청에 대한 처리를 위해 추가 로직이 필요함 (비관적락을 통해 방지)
> - 결제(주문):
    >   - 주문 자체는 충돌 가능성 낮음(낙관적 락), 재고는 충돌 가능성 높음(비관적 락)
>   - 재시도 : (주문 실패 시) 유저에게 재 주문을 하라고 던져주거나 시스템 내에서 재시도 가능할 순 있음
>   - 시스템 내에서 재시도 해야할 내용들이 많아짐... (재고 원복, 포인트 원복)..
---

## ✍️ 간단 회고 (3줄 이내)
- **잘한 점**: 비관적/낙관적 락에 대한 고민을 살짝 해보기..
- **어려웠던 점**: 실제 고민했던 점이랑, 스레드를 여러개 돌려서 설계한 코드의 결과가 매우매우매우 다름
- **다음 시도**: 트랜잭션 전파단위를 좀 더 이해하고, 각각의 예외 처리 계층과 역할을 명확하게 하기
  