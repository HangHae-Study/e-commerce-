### STEP 17 카프카 기초 학습 및 활용
- [O] 카프카에 대한 기본 개념 학습 문서 작성
- [O] 실시간 주문/예약 정보를 카프카 메시지로 발행

### STEP 18 카프카를 활용하여 비즈니스 프로세스 개선
- [△] 카프카를 특징을 활용하도록 쿠폰/대기열 설계문서 작성
- [△] 설계문서대로 카프카를 활용한 기능 구현

---
### 설계문서(보고서)
01. 카프카 기본 활용 : [링크](https://github.com/HangHae-Study/e-commerce-/blob/step17/docs/kafka/01_%EC%B9%B4%ED%94%84%EC%B9%B4_%EA%B8%B0%EB%B3%B8_%ED%99%9C%EC%9A%A9.md)

02. 스프링 카프카 및 도커 설정 : [링크](https://github.com/HangHae-Study/e-commerce-/blob/step17/docs/kafka/02_%EC%8A%A4%ED%94%84%EB%A7%81_%EC%B9%B4%ED%94%84%EC%B9%B4_%EC%84%A4%EC%A0%95.md)

03. 주문정보 카프카 메세지 발행 : [링크](https://github.com/HangHae-Study/e-commerce-/blob/step17/docs/kafka/03_%EC%A3%BC%EB%AC%B8%EC%A0%95%EB%B3%B4_%EC%B9%B4%ED%94%84%EC%B9%B4_%EC%A0%84%EC%86%A1.md)

04. 카프카를 이용한 병렬 쿠폰 발행 설계 : [링크](https://github.com/HangHae-Study/e-commerce-/blob/step17/docs/kafka/04_%EC%B9%B4%ED%94%84%EC%B9%B4%EB%A5%BC_%EC%9D%B4%EC%9A%A9%ED%95%9C_%EB%B3%91%EB%A0%AC%EC%BF%A0%ED%8F%B0%EB%B0%9C%ED%96%89.md)
---
### 리뷰 포인트

- 각각의 문서(보고서)에 카프카 발행부와, 컨슈머 코드를 작성하였습니다.
  카프카를 단순 메세지 큐 개념으로 바라보았고,, 파티션은 키 값에 따른 분리된 병렬 큐로 접근을 하였는데요..
  쿠폰 발행에서 이와 같은 접근을 시도하다가 큰 코를 다쳤습니다.
  (주문 정보 발행까지 마찬가지,,,)

- 질문1
    - > 카프카 컨슈머 부분에서 @Transactional을 호출하는 것이 합당한 방법일 까요?
      > 비동기를 기반으로 수행되는 컨슈머가, @Transactional을 통해 내부 동작들을 추가로 호출하게 된다면, 이는 이벤트 드리븐 끝에 꽤나 동기방식에 편향적인 결과가 될 것 같습니다..
    - 쿠폰 발급 시에 해당 방식으로 진행하게 되었는데,,, (DB에 쿠폰 발급정보를 넣어주기 위해) 해당 내용을 보시고 한 번 피드백 해주시면 감사하겠습니다. (코드가 형편없어,,, 접근 관점에 대해서라도 부탁드릴게요..)
        - [Sequence](https://github.com/HangHae-Study/e-commerce-/blob/step17/docs/kafka/04_%EC%B9%B4%ED%94%84%EC%B9%B4%EB%A5%BC_%EC%9D%B4%EC%9A%A9%ED%95%9C_%EB%B3%91%EB%A0%AC%EC%BF%A0%ED%8F%B0%EB%B0%9C%ED%96%89.md#kafka-sequence)

- 질문2
    - 실무에서는  (Offset을 위한) 자동 커밋 또는 수동 커밋 `ack.acknowledge();` 의 필요한 상황이 존재하는지 궁금합니다.
    - 이번에 실패한 이유중 하나가 Offset이 증가되지 않은 채로, 컨슈머의 결과가 나와서.,..
      > - 컨슈머의 함수에서 한 트랜잭션이 실패하는 경우, `ack.acknowledge`와 같은 수동 커밋도 포함되어있다면,, 실패시에는 Offset을 읽지 못한 채로 다음 컨슈머가 수행되게 될 것 같은데,,, 이에 대한 경험이나 지식이 없어서 여쭙게 되었습니다..
      > - 또는 컨슈머에서 또한, 각각의 단계에 따라서 앱 내부 이벤트를 추가로 또 발행해 연속적인 비동기 흐름을 가져가는게 적절할까요?? (Kafka 공부 다시 열심히,,,하도록 하겠슴다..)
    - 문제의 부분,,, [링크](https://github.com/HangHae-Study/e-commerce-/blob/step17/docs/kafka/04_%EC%B9%B4%ED%94%84%EC%B9%B4%EB%A5%BC_%EC%9D%B4%EC%9A%A9%ED%95%9C_%EB%B3%91%EB%A0%AC%EC%BF%A0%ED%8F%B0%EB%B0%9C%ED%96%89.md#couponkafkaconsumer)


** 요약: 머리로는 이해되지만,,, 막상 원하는데로 이뤄지지 않는,,,(부끄럽습니다)

---

### **간단 회고** (3줄 이내)
- **잘한 점**:  바쁘다는 핑계로 이번 주차는 열심히 안해서 할 말이 없습니다..
- **어려운 점**:  카프카 offset 을 수동 커밋설정으로 해놨지만, 예외 발생시 처리되지 못하는 옾셋의 핸들링 부족
- **다음 시도**: 카프카를 활용한 디버깅 및 개선점 확장 적용 완료하기