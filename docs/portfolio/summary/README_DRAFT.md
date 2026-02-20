# HHPlus 백엔드 시스템

사용자 주문, 결제, 포인트 충전, 쿠폰 발급 등 이커머스 도메인을 헥사고날 아키텍처 기반으로 구성한 백엔드 애플리케이션입니다.

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

## 포트폴리오

> 각 주차별 학습 내용, 작성 문서, 핵심 개념, 그리고 PR 및 피드백 요약입니다.

| 주차 | 내용 | 작성 문서 | 핵심 개념 | PR 및 피드백 |
|:----:|------|-----------|-----------|-------------|
| **1주차**<br>(Step01~02) | TDD 기반 기능 추가 및 테스트 구현 | [기능 추가 및 테스트 구현](docs/portfolio/Step01/Step01_01(기능_추가_및_테스트_구현).md)<br>[코드 구현](docs/portfolio/Step01/Step01_02(코드_구현).md)<br>[PR 및 피드백](docs/portfolio/Step01/Step01_03(PR_및_피드백).md) | TDD (Red-Green-Refactor)<br>단위 테스트<br>MockMVC<br>ObjectMapper 활용 | [주차별 요약](docs/portfolio/summary/feedback/week01.md) |
| **2주차**<br>(Step03~04) | 서비스 시나리오 분석, ERD 설계,<br>Mock API 및 API 명세서 작성 | [시나리오 분석 및 초기 설계](docs/portfolio/Step03/Step03_01(서비스_시나리오_요구사항_분석_프로젝트_초기_설계).md)<br>[DB 테이블 설계](docs/portfolio/Step03/Step03_02(DB_테이블_설계).md)<br>[Mock API 및 테스트](docs/portfolio/Step04/Step04(Mock_API_생성_및_테스트).md)<br>[ERD](docs/02_ERD.md) · [시퀀스 다이어그램](docs/01_시퀀스_다이어그램.md) · [상태 다이어그램](docs/03_상태_다이어그램.md) | 시퀀스 다이어그램<br>ERD 설계<br>상태 다이어그램<br>REST Docs / Swagger<br>도메인 분리 기준 | [주차별 요약](docs/portfolio/summary/feedback/week02.md) |
| **3주차**<br>(Step05~06) | 헥사고날 아키텍처 기반<br>핵심 기능 구현 (상품/주문/결제) | [소프트웨어 설계 아키텍처](docs/portfolio/Step05/Step05_01(소프트웨어_설계_아키텍처).md) | 헥사고날 아키텍처<br>Port-Adapter 패턴<br>DIP (의존성 역전)<br>Facade 패턴<br>도메인-엔티티 분리 | [주차별 요약](docs/portfolio/summary/feedback/week03.md) |
| **4주차**<br>(Step07~08) | Infrastructure Layer 구성,<br>통합 테스트, DB 인덱스 최적화 | [인덱스 설정](docs/index/01_인덱스_설정.md) | Testcontainers<br>통합 테스트 (@Sql)<br>쿼리 실행계획 (EXPLAIN)<br>커버링 인덱스<br>반정규화 | [주차별 요약](docs/portfolio/summary/feedback/week04.md) |
| **5주차**<br>(Step09~10) | 동시성 문제 식별 및<br>DB 락 기반 해결 | [포인트 락 테스트](docs/concurrency/포인트_락_테스트.md)<br>[쿠폰 락 테스트](docs/concurrency/쿠폰_락_테스트.md)<br>[결제 락 테스트](docs/concurrency/결제_락_테스트.md)<br>[트랜잭션 테스트](docs/concurrency/트랜잭션_테스트.md) | 비관적 락 (Pessimistic)<br>낙관적 락 (Optimistic)<br>@Version<br>트랜잭션 전파 단위<br>동시성 통합 테스트 | [주차별 요약](docs/portfolio/summary/feedback/week05.md) |
| **6주차**<br>(Step11~12) | Redis 분산락 적용,<br>캐시 전략 수립 및 적용 | [Top5 캐시 보고서](docs/cache/지난_3일간_상위_판매_상품_조회.md) | 분산락 (Redisson AOP)<br>멀티 락 (Multi-Lock)<br>Redis 캐싱 전략<br>캐시 스탬피드<br>TestContainer (Redis) | [주차별 요약](docs/portfolio/summary/feedback/week06.md) |
| **7주차**<br>(Step13~14) | Redis 기반 인기상품 랭킹,<br>선착순 쿠폰 비동기 설계 | [인기상품 보고서](docs/cache/7주차_레디스_활용_보고서(인기상품).md)<br>[선착순 쿠폰 보고서](docs/cache/7주차_레디스_활용_보고서(선착순%20쿠폰).md) | ZSET (Sorted Set)<br>ZUNIONSTORE<br>Redis 기반 대기열<br>비동기 쿠폰 발급<br>워커(스케줄러) 패턴 | [주차별 요약](docs/portfolio/summary/feedback/week07.md) |
| **8주차**<br>(Step15~16) | 이벤트 기반 관심사 분리,<br>분산 트랜잭션 설계 (Saga) | [분산 트랜잭션 설계문서](docs/distributed/8주차_분산_트랜잭션을_위한_설계문서.md) | @TransactionalEventListener<br>Saga 패턴 (오케스트레이션)<br>상태 머신 (State Machine)<br>보상 트랜잭션<br>관심사 분리 | [주차별 요약](docs/portfolio/summary/feedback/week08.md) |
| **9주차**<br>(Step17~18) | 카프카 학습 및 활용,<br>비즈니스 프로세스 개선 | [카프카 기본 활용](docs/kafka/01_카프카_기본_활용.md)<br>[스프링 카프카 설정](docs/kafka/02_스프링_카프카_설정.md)<br>[주문정보 카프카 전송](docs/kafka/03_주문정보_카프카_전송.md)<br>[병렬 쿠폰 발행](docs/kafka/04_카프카를_이용한_병렬쿠폰발행.md) | Kafka (Producer/Consumer)<br>Outbox 패턴<br>Offset 관리 (수동/자동 커밋)<br>DLQ (Dead Letter Queue)<br>컨슈머 책임 분리 | [주차별 요약](docs/portfolio/summary/feedback/week09.md) |
| **10주차**<br>(Step19~20) | 부하 테스트 및<br>성능 지표 분석·개선 | [모니터링 분석 보고서](docs/monitor/상품_주문_시나리오_모니터링_분석.md)<br>[포인트 충전 모니터링](docs/monitor/2분동안_유저_최대_10명_포인트충전.md) | k6 부하 테스트<br>RPS / Latency 분석<br>비관적 락 vs 분산 락 성능<br>캐싱 vs DB 성능<br>단일 vs 분산 트랜잭션 성능 | [주차별 요약](docs/portfolio/summary/feedback/week10.md) |

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
