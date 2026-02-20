# 7주차 - STEP 13 & STEP 14

> Redis 기반 랭킹 설계 / 선착순 쿠폰 발급 비동기 설계

---

## Merge Request

### [STEP 13 & 14] 김성인 - e-commerce

#### 핵심 체크리스트

**Ranking Design**
- [O] 적절한 설계를 기반으로 랭킹기능이 개발되었는가?
- [O] 적절한 자료구조를 선택하였는가?

**Asynchronous Design**
- [O] 적절한 설계를 기반으로 쿠폰 발급 or 대기열 기능이 개발되었는가?
- [O] 적절한 자료구조를 선택하였는가?

**통합 테스트**
- [△] redis 테스트 컨테이너를 통해 적절하게 통합 테스트가 작성되었는가?(독립적 테스트 환경을 보장하는가?)
- [△] 핵심 기능에 대한 흐름이 테스트에서 검증되었는가?

---

#### 구현 완료 기능

- STEP 13 Ranking Design
    - **이커머스 시나리오**
    - [X] 가장 많이 주문한 상품 랭킹을 Redis 기반으로 설계
    - [X] 설계를 기반으로 개발 및 구현

- STEP 14 Asynchronous Design
    - **이커머스 시나리오**
    - [X] 선착순 쿠폰발급 기능에 대해 Redis 기반의 설계
    - [X] 적절하게 동작할 수 있도록 쿠폰 발급 로직을 개선해 제출
    - [X] 시스템 ( 랭킹, 비동기 ) 디자인 설계 및 개발 후 회고 내용을 담은 보고서 제출

---

#### 설계문서(보고서)
- 인기 상품 조회 : [링크](https://github.com/HangHae-Study/e-commerce-/blob/step13/docs/cache/7%EC%A3%BC%EC%B0%A8_%EB%A0%88%EB%94%94%EC%8A%A4_%ED%99%9C%EC%9A%A9_%EB%B3%B4%EA%B3%A0%EC%84%9C(%EC%9D%B8%EA%B8%B0%EC%83%81%ED%92%88).md)
- 선착순 쿠폰 발급 : [링크](https://github.com/HangHae-Study/e-commerce-/blob/step13/docs/cache/7%EC%A3%BC%EC%B0%A8_%EB%A0%88%EB%94%94%EC%8A%A4_%ED%99%9C%EC%9A%A9_%EB%B3%B4%EA%B3%A0%EC%84%9C(%EC%84%A0%EC%B0%A9%EC%88%9C%20%EC%BF%A0%ED%8F%B0).md)

---

#### 리뷰 포인트

- 인기 상품 조회 기능
    - 기능
        - 코드: [[TopProductCacheRepository.java]](https://github.com/HangHae-Study/e-commerce-/blob/step13/src/main/java/kr/hhplus/be/server/domain/product/adapter/cache/TopProductCacheRepository.java#L82)
        - 커밋 : 인기 상품 관련 레디스 담당 클래스 - [[02a59dc]](https://github.com/HangHae-Study/e-commerce-/commit/02a59dca3d201b62e1e3159889f72df088397d06#diff-a32bd18ec99c1f35aec1f829205d424f524c7c2d5d9d916b547050d84af4d30c)
    - 테스트 코드 : [[TopProductRankWithRedisTest.java]](https://github.com/HangHae-Study/e-commerce-/blob/step13/src/test/java/kr/hhplus/be/server/cache/top/product/TopProductRankWithRedisTest.java)
    - 지난 3일간 Top5 인기 상품을 조회하기 위해, 매일 ZSET 기반의 실시간 인기 상품을 주문 수량 Score를 통해 Rankf를 세우도록 하였습니다.
    - 이후 3일의 간의 집계는 Redis의 ZUNIONSTORE 기능을 사용하였습니다.
    - ZSET에 집계된 3일간의 상위 5개 판매 수량을 가진 productLineId를 기반으로, DB 조회를 통해 캐싱 데이터를 반영하였습니다.
    - 지난 주차에 작성하였던, 캐싱 데이터를 이용한 지난 3일간 상위 5개 판매 테스트 코드 또한 잘 통과하는 것을 확인하였습니다.
    - 아쉬웠던 점: 캐싱 레포지토리(레디스 활용) 클래스를 추상화 시키진 못하였고,, 책임도 꽤 한 클래스에 가중된 것 같은 느낌이 듭니다.. 이번 주차는, 설계에 대한 이상적인 흐름은 순탄하였지만,, 구현이 깔끔하지 못해 아쉬운 한 주 였습니다..

- 선착순 쿠폰 발급
  `(신규 쿠폰 발생 시, 시스템 혹은 어드민에 의해 캐시(redis)에 쿠폰 잔여 수량이 최초로 반드시 올라간다는 가정하에 코드를 작성하엿습니다)`
    - 기능
        - 서비스 코드 : [[CouponService.java]](https://github.com/HangHae-Study/e-commerce-/blob/step13/src/main/java/kr/hhplus/be/server/domain/coupon/application/service/CouponService.java#L52)
        - 커밋: 쿠폰 관련 레디스 담당 클래스 - [[9addc27]](https://github.com/HangHae-Study/e-commerce-/commit/9addc27b03edb79bae31dab3bed68618cf8d6503)
    - 테스트 코드 : [[CouponCacheWithRedisTest.java]](https://github.com/HangHae-Study/e-commerce-/blob/step13/src/test/java/kr/hhplus/be/server/cache/coupon/CouponCacheWithRedisTest.java)
    - 쿠폰 서비스에서 claim 시에 (issued)상태에 따른 동작 방식을 너무 한 함수에 몰아 넣은 것 같아서 책임이 과중된 느낌입니다..
        - 또, 쿠폰 발급 내역에 대해 캐싱을 하긴 하였지만, `유저 claim 요청 -> 쿠폰 발급 됐지만 캐싱 만료 ->최초 요청으로 인식` 와 같은 시나리오에서, 올바른 판별 조건과 캐싱 전략을 활용할 수 있도록 했어야하는데, 완벽하게 작성하진 못하였습니다.
    - 테스트 코드 또한, 레디스를 활용한 CacheRepository 클래스에 대한 단위 테스트는 완료하였지만, 통합테스트나 워커(스케줄러)를 활용한 시나리오 테스트까지는 하지 못하여서 매우 아쉽습니다.. (시간 부족 이슈,, :d)

> - 서비스 코드에서 유저 Claim에 대한 리턴 값이나 함수를 조금 더 가볍게 구현 할만한 방법이 있을지 간단한 피드백 해주시면 감사하겠습니다 :D
> - 구현 근거는 위에 있는 보고서를 기반으로 하였습니다.  (선착순 쿠폰 발급 : [링크](https://github.com/HangHae-Study/e-commerce-/blob/step13/docs/cache/7%EC%A3%BC%EC%B0%A8_%EB%A0%88%EB%94%94%EC%8A%A4_%ED%99%9C%EC%9A%A9_%EB%B3%B4%EA%B3%A0%EC%84%9C(%EC%84%A0%EC%B0%A9%EC%88%9C%20%EC%BF%A0%ED%8F%B0).md))
    쿠폰발급 보고서 마지막에, 워커의 [큐를 바라보는 기준(?)](https://github.com/HangHae-Study/e-commerce-/blob/step13/docs/cache/7%EC%A3%BC%EC%B0%A8_%EB%A0%88%EB%94%94%EC%8A%A4_%ED%99%9C%EC%9A%A9_%EB%B3%B4%EA%B3%A0%EC%84%9C(%EC%84%A0%EC%B0%A9%EC%88%9C%20%EC%BF%A0%ED%8F%B0).md#5-%EC%B6%94%ED%9B%84-%ED%99%95%EC%9E%A5-%EB%B0%A9%EC%95%88)도 어떻게 생각하시는지 확인해주시면 감사하겠습니다!
> - Claim에 대한 요청 상태 : {ISSUED(발급완료), FAILED(발급실패), WAITED(발급대기), PROCESSING(발급처리중), INIT(최초발급)}

#### 간단 회고
- **잘한 점**: 레디스 자료 구조를 통해서, 기존의 DB 에서 진행했던 일들을 캐싱하는 형태로 간소화할 수 있었습니다.
- **어려운 점**: 비동기적으로 쿠폰을 처리하는 것이 꽤 어려웠습니다.. 또한, 테스트 코드를 이번에는 다 작성하지 못하였어요..
- **다음 시도**: Pub/Sub 기반의 비동기 트리거 생성, 테스트 코드 완료. /   `코드리팩토링 필수!!!!!`

---

## Feedback

- 적절한 설계를 기반으로 랭킹기능이 개발되었는가? - O
- 적절한 설계를 기반으로 쿠폰 발급 or 대기열 기능이 개발되었는가?- O

---

- 요구사항의 변경에 따라 특정 클래스의 역할이 단일 책임의 원칙에 위배되는 느낌도 들고, 복잡도도 올라가는 느낌이 들 수 있어요. 초기 설계부터 모든 클래스의 책임과 역할을 잘 나누어 보면 더 나은 구조로 리펙토링 할 수 있을 거에요.
- 쿠폰 별로 발급 요청 큐가 있기 때문에 워커를 작성하기 어렵죠? 제 생각에는 모든 요청이 하나의 큐에 인입되도록 구현하는 것이 가장 심플할 것 같아요. 처리량 관점에서는 이후에 카프카를 활용해서 처리량을 쉽게 늘리는 방법을 배울거에요.
- 하나의 메서드에서 발급도 해주고 상태도 확인해주도록 구현되어 있어서 코드가 더 복잡해지는 것 같아요. 발급대기/발급처리중에 대한 구현은 발급상태 조회등의 메서드로 분리해서 구현하면 좀 더 심플할 것 같네요.
