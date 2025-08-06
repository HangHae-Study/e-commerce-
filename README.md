## 프로젝트

# 🧱 HHPlus 백엔드 시스템

사용자 주문, 결제, 포인트 충전, 쿠폰 발급 등 이커머스 도메인을 헥사고날 아키텍처 기반으로 구성한 백엔드 애플리케이션입니다.

---

## 🗂 패키지 구조 (디렉토리 기준)

```
kr.hhplus.be.server
├── common # 공통 유틸리티 및 예외 처리
│   ├── api
│   └── inmemory
├── config
│   └── jpa
├── domain
│   ├── coupon
│   │   ├── adapter
│   │   │   ├── entity
│   │   │   └── repository
│   │   ├── application
│   │   │   ├── repository
│   │   │   └── service
│   │   ├── controller
│   │   └── dto
│   ├── order
│   │   ├── adapter
│   │   │   ├── entity
│   │   │   └── repository
│   │   ├── application
│   │   │   ├── cached
│   │   │   ├── dto
│   │   │   ├── facade
│   │   │   ├── repository
│   │   │   └── service
│   │   └── controller
│   ├── payment
│   │   ├── adapter
│   │   │   ├── entity
│   │   │   └── repository
│   │   ├── application
│   │   │   ├── dto
│   │   │   ├── repository
│   │   │   └── service
│   │   └── controller
│   ├── product
│   │   ├── adapter
│   │   │   ├── entity
│   │   │   └── repository
│   │   ├── application
│   │   │   ├── facade
│   │   │   ├── repository
│   │   │   └── service
│   │   └── controller
│   │   └── apidto
│   └── user
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── facade
│   ├── repository
│   │   └── table
│   └── service

```
---

## ⚙ 아키텍처 책임 (Hexagonal Architecture)

이 프로젝트는 헥사고날 아키텍처를 기반으로 구성되며, 각 레이어의 책임은 다음과 같습니다:

| 레이어                        | 설명 |
|----------------------------|------|
| **controller**             | 외부 HTTP 요청을 받아 DTO 변환 및 애플리케이션 서비스에 위임 |
| **facade** (Optional)      | 여러 도메인 로직을 조합한 복합 기능 구현 |
| **domain** (application 내) | 순수한 도메인 객체, 상태와 행위를 함께 가짐 |
| **application/dto**        | 외부로부터 들어오거나 나가는 데이터 전송 객체 |
| **application/service**    | 도메인 객체를 조합하여 use-case 단위 비즈니스 로직 처리 |
| **cached / table**         | 메모리 기반 저장소 또는 Lock 등 부가 기능 |
| **application/repository** | 도메인 객체에 의존하는 저장소 인터페이스 (Port) |
| **adapter/repository**     | 실제 DB 구현체 (JpaRepository, InMemory 등) |
| **adapter/entity**         | JPA 엔티티 (영속성 계층) |

💡 **규칙**: 각 레이어는 상위 레이어로 의존하면 안 되며, 반드시 아키텍처의 흐름에 맞는 방향성만 허용합니다.

> - 상위 레이어는 절대로 하위 레이어 구현체에 의존하지 않습니다.
> - domain과 application/service는 interface를 통해 외부 저장소를 접근하며, 실제 구현은 adapter에서 담당합니다.
> - 모든 의존 관계는 한 방향이며, 순환 참조 금지를 원칙으로 합니다.
> - 테스트 시에도 레이어 간의 분리를 유지하여 유지보수성과 확장성을 보장합니다.
```
(controller)
↓
[ facade ]          ← (선택적으로 도입)
↓
(domain / dto / service)  ← application layer
↓
(cached / table)
↓
(application/repository)  ← 저장소 Port (인터페이스)
↓
(adapter/repository)      ← 저장소 Adapter (구현체)
↓
(adapter/entity)          ← 영속성 계층
```

---

## 🧩 도메인 설명

| 도메인    | 설명 |
|-----------|------|
| **user** | 회원 도메인. 사용자 정보 및 포인트 관련 로직을 포함합니다. |
| **point** | 사용자의 포인트 잔액을 관리하며 충전/차감 등의 기능을 담당합니다. |
| **product** | 판매 상품 정보를 관리하며, 옵션(productLine)과 랭킹 정보를 포함합니다. |
| **order** | 사용자의 주문 및 주문 상세(order line)를 포함한 핵심 도메인입니다. |
| **payment** | 주문에 대한 결제 처리, 결제 성공/실패 상태 관리 및 저장을 담당합니다. |
| **coupon** | 쿠폰 발급, 사용 내역을 관리하며, 할인 정책 적용을 담당합니다. |

---

## 🧪 테스트

- 통합 테스트는 SpringBootTest 기반으로 작성되며, `test/resources`의 테스트 데이터 기반으로 수행됩니다.
- 모든 Repository는 도메인명 + `Repository` 형태로 명명됩니다. (예: `OrderRepository`, `ProductLineRepository`)


---
## Getting Started

### Prerequisites

#### Running Docker Containers

`local` profile 로 실행하기 위하여 인프라가 설정되어 있는 Docker 컨테이너를 실행해주셔야 합니다.

```bash
docker-compose up -d
```