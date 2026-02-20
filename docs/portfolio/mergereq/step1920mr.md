### STEP 19 부하 테스트 스크립트 작성 및 진행
- [X] 부하 테스트 대상 선정 및 목적, 시나리오 등의 계획을 세우고 이를 문서로 작성
- [X] 적합한 테스트 스크립트를 작성하고 수행


### STEP 20 부하 테스트로 인한 문제 개선 및 보고서 작성
- [X] 테스트를 진행하며 획득한 다양한 성능 지표를 분석 및 시스템 내의 병목을 탐색 및 개선함
- [X] 가상의 장애 대응 문서를 작성하고 제출함

---
### 리뷰 포인트
- 포인트 충전, 주문 요청 시 동일 자원에 동시 트래픽에 대해서 `락`, `트랜잭션` 기반의 순차 보장이 되는 것을 직접 볼 수 있었습니다.

- 지난 주차 동안 했던 내역에 대해서 직접 트래픽을 흘려 보내고, API 수행 시간에 대해서 확인하였습니다.
    - 시나리오 : `포인트 충전` → `상품 조회` → `주문 생성` → `결제 요청`
        - 전체 문서 : [[링크](https://github.com/HangHae-Study/e-commerce-/blob/step19/docs/monitor/%EC%83%81%ED%92%88_%EC%A3%BC%EB%AC%B8_%EC%8B%9C%EB%82%98%EB%A6%AC%EC%98%A4_%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81_%EB%B6%84%EC%84%9D.md)]
        - 포인트 충전 - 성능 확인 및 개선 방향 (비관적 락 vs 분산 락) : [[링크](https://github.com/HangHae-Study/e-commerce-/blob/step19/docs/monitor/%EC%83%81%ED%92%88_%EC%A3%BC%EB%AC%B8_%EC%8B%9C%EB%82%98%EB%A6%AC%EC%98%A4_%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81_%EB%B6%84%EC%84%9D.md#-1-%EC%9C%A0%EC%A0%80-%ED%8F%AC%EC%9D%B8%ED%8A%B8-%EC%B6%A9%EC%A0%84-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%A0%9C%EC%96%B4%EC%97%90-%EB%8C%80%ED%95%98%EC%97%AC)]
        - 인기상품 조회 - 성능 확인 및 개선 방향 (캐싱 vs DB 커넥션) : [[링크](https://github.com/HangHae-Study/e-commerce-/blob/step19/docs/monitor/%EC%83%81%ED%92%88_%EC%A3%BC%EB%AC%B8_%EC%8B%9C%EB%82%98%EB%A6%AC%EC%98%A4_%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81_%EB%B6%84%EC%84%9D.md#-2-%EC%9D%B8%EA%B8%B0-%EC%83%81%ED%92%88-%EC%A1%B0%ED%9A%8C-%EC%8B%9C-%EC%BA%90%EC%8B%B1-%EC%A0%84%EB%9E%B5%EC%97%90-%EB%8C%80%ED%95%98%EC%97%AC)]
        - 주문 결제 - 성능 확인 및 개선 방향 (단일 vs 분산 트랜잭션) : [[링크](https://github.com/HangHae-Study/e-commerce-/blob/step19/docs/monitor/%EC%83%81%ED%92%88_%EC%A3%BC%EB%AC%B8_%EC%8B%9C%EB%82%98%EB%A6%AC%EC%98%A4_%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81_%EB%B6%84%EC%84%9D.md#-3-%EC%A3%BC%EB%AC%B8-%EA%B2%B0%EC%A0%9C-%EC%8B%9C-%EB%8B%A8%EC%9D%BC-vs-%EB%B6%84%EC%82%B0-%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98%EC%97%90-%EB%8C%80%ED%95%98%EC%97%AC)]

- 쿠폰은 지난 설계 및 구현에서 다소 부족한 부분이 많아서,,, 자체적으로 복습하며 다시 진행해볼 예정 입니다..ㅎㅎ
- (장애 대응 문서는 안 내어도 된다고 하셔서,,, 제외 하였습니다 ..ㅎㅎ)
---

### **간단 회고** (3줄 이내)
- **잘한 점**: 테스트 코드로만 돌려봤던 로직을, 실제 API를 통해 트래픽을 발생시켜 정상동작 하는 모습을 볼 수 있었습니다.
- **어려운 점**: 기존에 정확하게 설계하지 않았던 부분이 들어나 수정도 조금 했습니다..ㅎ
- **다음 시도**: 복습 및 아쉬웠던 부분 채워서 포트폴리오로 전환시키기