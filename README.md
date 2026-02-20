# HHPlus 백엔드 시스템

사용자 주문, 결제, 포인트 충전, 쿠폰 발급 등 이커머스 도메인을 헥사고날 아키텍처 기반으로 구성한 백엔드 애플리케이션입니다.

---

## 포트폴리오

> 각 주차별 학습 내용, 작성 문서, 핵심 개념, 그리고 PR 및 피드백 요약입니다.

| 주차 | 내용 | 작성 문서 | 핵심 개념 | PR 및 피드백 |
|:----:|------|-----------|-----------|-------------|
| **1주차**<br>(Step01~02) | TDD 기반 기능 추가 및 테스트 구현 | - [기능 추가 및 테스트 구현](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/portfolio/Step01/Step01_01%28%EA%B8%B0%EB%8A%A5_%EC%B6%94%EA%B0%80_%EB%B0%8F_%ED%85%8C%EC%8A%A4%ED%8A%B8_%EA%B5%AC%ED%98%84%29.md)<br>- [코드 구현](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/portfolio/Step01/Step01_02%28%EC%BD%94%EB%93%9C_%EA%B5%AC%ED%98%84%29.md)<br>- [PR 및 피드백](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/portfolio/Step01/Step01_03%28PR_%EB%B0%8F_%ED%94%BC%EB%93%9C%EB%B0%B1%29.md) | TDD (Red-Green-Refactor)<br>단위 테스트<br>MockMVC<br>ObjectMapper 활용 | [주차별 요약](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/portfolio/summary/feedback/week01.md) |
| **2주차**<br>(Step03~04) | 서비스 시나리오 분석, ERD 설계,<br>Mock API 및 API 명세서 작성 | - [시나리오 분석 및 초기 설계](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/portfolio/Step03/Step03_01%28%EC%84%9C%EB%B9%84%EC%8A%A4_%EC%8B%9C%EB%82%98%EB%A6%AC%EC%98%A4_%EC%9A%94%EA%B5%AC%EC%82%AC%ED%95%AD_%EB%B6%84%EC%84%9D_%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8_%EC%B4%88%EA%B8%B0_%EC%84%A4%EA%B3%84%29.md)<br>- [DB 테이블 설계](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/portfolio/Step03/Step03_02%28DB_%ED%85%8C%EC%9D%B4%EB%B8%94_%EC%84%A4%EA%B3%84%29.md)<br>- [Mock API 및 테스트](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/portfolio/Step04/Step04%28Mock_API_%EC%83%9D%EC%84%B1_%EB%B0%8F_%ED%85%8C%EC%8A%A4%ED%8A%B8%29.md)<br>- [ERD](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/02_ERD.md) · [시퀀스 다이어그램](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/01_%EC%8B%9C%ED%80%80%EC%8A%A4_%EB%8B%A4%EC%9D%B4%EC%96%B4%EA%B7%B8%EB%9E%A8.md) · [상태 다이어그램](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/03_%EC%83%81%ED%83%9C_%EB%8B%A4%EC%9D%B4%EC%96%B4%EA%B7%B8%EB%9E%A8.md) | 시퀀스 다이어그램<br>ERD 설계<br>상태 다이어그램<br>REST Docs / Swagger<br>도메인 분리 기준 | [주차별 요약](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/portfolio/summary/feedback/week02.md) |
| **3주차**<br>(Step05~06) | 헥사고날 아키텍처 기반<br>핵심 기능 구현 (상품/주문/결제) | - [소프트웨어 설계 아키텍처](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/portfolio/Step05/Step05_01%28%EC%86%8C%ED%94%84%ED%8A%B8%EC%9B%A8%EC%96%B4_%EC%84%A4%EA%B3%84_%EC%95%84%ED%82%A4%ED%85%8D%EC%B2%98%29.md) | 헥사고날 아키텍처<br>Port-Adapter 패턴<br>DIP (의존성 역전)<br>Facade 패턴<br>도메인-엔티티 분리 | [주차별 요약](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/portfolio/summary/feedback/week03.md) |
| **4주차**<br>(Step07~08) | Infrastructure Layer 구성,<br>통합 테스트, DB 인덱스 최적화 | - [인덱스 설정](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/index/01_%EC%9D%B8%EB%8D%B1%EC%8A%A4_%EC%84%A4%EC%A0%95.md) | Testcontainers<br>통합 테스트 (@Sql)<br>쿼리 실행계획 (EXPLAIN)<br>커버링 인덱스<br>반정규화 | [주차별 요약](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/portfolio/summary/feedback/week04.md) |
| **5주차**<br>(Step09~10) | 동시성 문제 식별 및<br>DB 락 기반 해결 | - [포인트 락 테스트](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/concurrency/%ED%8F%AC%EC%9D%B8%ED%8A%B8_%EB%9D%BD_%ED%85%8C%EC%8A%A4%ED%8A%B8.md)<br>- [쿠폰 락 테스트](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/concurrency/%EC%BF%A0%ED%8F%B0_%EB%9D%BD_%ED%85%8C%EC%8A%A4%ED%8A%B8.md)<br>- [결제 락 테스트](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/concurrency/%EA%B2%B0%EC%A0%9C_%EB%9D%BD_%ED%85%8C%EC%8A%A4%ED%8A%B8.md)<br>- [트랜잭션 테스트](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/concurrency/%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98_%ED%85%8C%EC%8A%A4%ED%8A%B8.md) | 비관적 락 (Pessimistic)<br>낙관적 락 (Optimistic)<br>@Version<br>트랜잭션 전파 단위<br>동시성 통합 테스트 | [주차별 요약](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/portfolio/summary/feedback/week05.md) |
| **6주차**<br>(Step11~12) | Redis 분산락 적용,<br>캐시 전략 수립 및 적용 | - [Top5 캐시 보고서](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/cache/%EC%A7%80%EB%82%9C_3%EC%9D%BC%EA%B0%84_%EC%83%81%EC%9C%84_%ED%8C%90%EB%A7%A4_%EC%83%81%ED%92%88_%EC%A1%B0%ED%9A%8C.md) | 분산락 (Redisson AOP)<br>멀티 락 (Multi-Lock)<br>Redis 캐싱 전략<br>캐시 스탬피드<br>TestContainer (Redis) | [주차별 요약](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/portfolio/summary/feedback/week06.md) |
| **7주차**<br>(Step13~14) | Redis 기반 인기상품 랭킹,<br>선착순 쿠폰 비동기 설계 | - [인기상품 보고서](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/cache/7%EC%A3%BC%EC%B0%A8_%EB%A0%88%EB%94%94%EC%8A%A4_%ED%99%9C%EC%9A%A9_%EB%B3%B4%EA%B3%A0%EC%84%9C%28%EC%9D%B8%EA%B8%B0%EC%83%81%ED%92%88%29.md)<br>- [선착순 쿠폰 보고서](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/cache/7%EC%A3%BC%EC%B0%A8_%EB%A0%88%EB%94%94%EC%8A%A4_%ED%99%9C%EC%9A%A9_%EB%B3%B4%EA%B3%A0%EC%84%9C%28%EC%84%A0%EC%B0%A9%EC%88%9C%20%EC%BF%A0%ED%8F%B0%29.md) | ZSET (Sorted Set)<br>ZUNIONSTORE<br>Redis 기반 대기열<br>비동기 쿠폰 발급<br>워커(스케줄러) 패턴 | [주차별 요약](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/portfolio/summary/feedback/week07.md) |
| **8주차**<br>(Step15~16) | 이벤트 기반 관심사 분리,<br>분산 트랜잭션 설계 (Saga) | - [분산 트랜잭션 설계문서](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/distributed/8%EC%A3%BC%EC%B0%A8_%EB%B6%84%EC%82%B0_%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98%EC%9D%84_%EC%9C%84%ED%95%9C_%EC%84%A4%EA%B3%84%EB%AC%B8%EC%84%9C.md) | @TransactionalEventListener<br>Saga 패턴 (오케스트레이션)<br>상태 머신 (State Machine)<br>보상 트랜잭션<br>관심사 분리 | [주차별 요약](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/portfolio/summary/feedback/week08.md) |
| **9주차**<br>(Step17~18) | 카프카 학습 및 활용,<br>비즈니스 프로세스 개선 | - [카프카 기본 활용](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/kafka/01_%EC%B9%B4%ED%94%84%EC%B9%B4_%EA%B8%B0%EB%B3%B8_%ED%99%9C%EC%9A%A9.md)<br>- [스프링 카프카 설정](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/kafka/02_%EC%8A%A4%ED%94%84%EB%A7%81_%EC%B9%B4%ED%94%84%EC%B9%B4_%EC%84%A4%EC%A0%95.md)<br>- [주문정보 카프카 전송](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/kafka/03_%EC%A3%BC%EB%AC%B8%EC%A0%95%EB%B3%B4_%EC%B9%B4%ED%94%84%EC%B9%B4_%EC%A0%84%EC%86%A1.md)<br>- [병렬 쿠폰 발행](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/kafka/04_%EC%B9%B4%ED%94%84%EC%B9%B4%EB%A5%BC_%EC%9D%B4%EC%9A%A9%ED%95%9C_%EB%B3%91%EB%A0%AC%EC%BF%A0%ED%8F%B0%EB%B0%9C%ED%96%89.md) | Kafka (Producer/Consumer)<br>Outbox 패턴<br>Offset 관리 (수동/자동 커밋)<br>DLQ (Dead Letter Queue)<br>컨슈머 책임 분리 | [주차별 요약](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/portfolio/summary/feedback/week09.md) |
| **10주차**<br>(Step19~20) | 부하 테스트 및<br>성능 지표 분석·개선 | - [모니터링 분석 보고서](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/monitor/%EC%83%81%ED%92%88_%EC%A3%BC%EB%AC%B8_%EC%8B%9C%EB%82%98%EB%A6%AC%EC%98%A4_%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81_%EB%B6%84%EC%84%9D.md)<br>- [포인트 충전 모니터링](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/monitor/2%EB%B6%84%EB%8F%99%EC%95%88_%EC%9C%A0%EC%A0%80_%EC%B5%9C%EB%8C%80_10%EB%AA%85_%ED%8F%AC%EC%9D%B8%ED%8A%B8%EC%B6%A9%EC%A0%84.md) | k6 부하 테스트<br>RPS / Latency 분석<br>비관적 락 vs 분산 락 성능<br>캐싱 vs DB 성능<br>단일 vs 분산 트랜잭션 성능 | [주차별 요약](https://github.com/HangHae-Study/e-commerce-/blob/portfolio/docs/portfolio/summary/feedback/week10.md) |

---

## 패키지 구조

```
kr.hhplus.be.server
├── common/                          # 공통 유틸리티 및 예외 처리
│   ├── api/                         # API 응답 래퍼
│   ├── exception/                   # 도메인별 예외 정의
│   ├── inmemory/                    # 인메모리 테이블 추상화
│   └── optimistic/                  # 낙관적 락 지원
├── config/                          # Spring 설정
│   ├── aop/lock/                    # 분산락 AOP (@DistributedLock)
│   ├── jpa/                         # JPA 설정
│   ├── kafka/                       # Kafka Producer/Consumer 설정
│   └── redis/                       # Redis Template 설정
└── domain/                          # 핵심 도메인 (아래 구조 공통 적용)
    └── {도메인명}/                    # coupon, order, payment, product, user
        ├── controller/              # HTTP 요청 처리, DTO 변환
        ├── application/
        │   ├── dto/                 # 데이터 전송 객체
        │   ├── facade/              # 여러 도메인 서비스 조합 (선택)
        │   ├── saga/                # 사가 패턴 (분산 트랜잭션)
        │   ├── service/             # 유스케이스 비즈니스 로직
        │   └── repository/          # 저장소 Port (인터페이스)
        └── adapter/
            ├── entity/              # JPA 엔티티 (영속성 계층)
            ├── repository/          # 저장소 Adapter (JPA 구현체)
            ├── cache/               # Redis 캐싱 구현
            ├── event/               # 이벤트 발행
            └── kafka/               # Kafka Producer/Consumer
```

---

## 아키텍처 (Hexagonal Architecture)

### 의존 흐름

```
[Inbound Adapter]                              [Outbound Adapter]

  Controller ──→ Facade ──→ Service ──→ Port(Interface)
                              │                    │
                              ▼                    ▼
                         Domain Model        Adapter (구현체)
                                              ├── JPA Repository
                                              ├── Redis Cache
                                              ├── Kafka Producer
                                              └── Event Publisher
```

### 레이어별 책임

| 레이어 | 위치 | 책임 |
|--------|------|------|
| **Controller** | `controller/` | 외부 HTTP 요청 수신, 요청/응답 DTO 변환 후 Facade 또는 Service에 위임 |
| **Facade** | `application/facade/` | 여러 도메인 서비스의 흐름을 조합하여 복합 유스케이스 수행 (선택적 도입) |
| **Saga** | `application/saga/` | 분산 트랜잭션 상태 관리 및 보상 로직 (주문-결제 흐름) |
| **Service** | `application/service/` | 단일 도메인 내 유스케이스 비즈니스 로직 처리 |
| **Port** | `application/repository/` | 저장소 인터페이스 — 도메인이 외부 인프라에 의존하지 않도록 추상화 |
| **Adapter** | `adapter/repository/` | Port 구현체 — JPA, InMemory 등 실제 저장소 접근 |
| **Entity** | `adapter/entity/` | JPA 엔티티 — 영속성 계층, 도메인 모델과 분리 |
| **Cache** | `adapter/cache/` | Redis 기반 캐싱 — 인기상품 랭킹, 쿠폰 발급 수량 등 |
| **Event/Kafka** | `adapter/event/`, `adapter/kafka/` | 이벤트 발행 및 Kafka 메시지 처리 (비동기 관심사 분리) |

### 아키텍처 규칙

> - 모든 의존 관계는 **안쪽(도메인) 방향** 단방향으로 흐르며, 순환 참조를 금지합니다.
> - Service/Domain은 **Port 인터페이스**를 통해 외부 저장소에 접근하고, 실제 구현은 Adapter가 담당합니다.
> - 도메인 모델과 JPA 엔티티는 **분리**되어 있어 영속성 계층의 변경이 비즈니스 로직에 영향을 주지 않습니다.
> - 부가 로직(랭킹 반영, 데이터 전송 등)은 **이벤트/Kafka**를 통해 메인 트랜잭션과 분리합니다.

---

## 도메인 설명

| 도메인 | 설명 |
|--------|------|
| **user** | 회원 도메인. 사용자 정보 및 포인트 잔액 관리, 충전/차감 기능을 포함합니다. |
| **product** | 판매 상품 정보를 관리하며, 상품 옵션(ProductLine)과 인기상품 랭킹 정보를 포함합니다. |
| **order** | 사용자의 주문 생성, 주문 상세(OrderLine), 주문 상태 관리를 담당하는 핵심 도메인입니다. |
| **payment** | 주문에 대한 결제 처리, 결제 성공/실패 상태 관리 및 저장을 담당합니다. |
| **coupon** | 선착순 쿠폰 발급, 사용 내역 관리, 할인 정책 적용을 담당합니다. |

---

## 테스트

- 통합 테스트는 SpringBootTest + Testcontainers (MySQL 8.0, Redis 7.2) 기반으로 작성됩니다.
- 테스트 데이터는 `test/resources/sql/` 의 SQL 스크립트를 `@Sql` 어노테이션으로 로드합니다.
- 모든 테스트는 UTC 타임존으로 실행됩니다.

---

## Getting Started

### Prerequisites

`local` profile 로 실행하기 위하여 인프라가 설정되어 있는 Docker 컨테이너를 실행해주셔야 합니다.

```bash
docker-compose up -d
```
