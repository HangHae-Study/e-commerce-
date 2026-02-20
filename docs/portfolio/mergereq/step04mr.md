[STEP-4] MockAPI 작성 및 API 명세서 작성

## PR 설명
1. 환경 셋팅 : [[c44a167]](https://github.com/HangHae-Study/e-commerce-/commit/c44a167488bbc2f5e7fda43ed87027fe9d77bb55) - [[4a75b83]](https://github.com/HangHae-Study/e-commerce-/commit/4a75b837747e458bde7313d1b5d5ec0233d75dc2)
2. Mock API 구성 컴포넌트 추가 : [[3ece011]](https://github.com/HangHae-Study/e-commerce-/commit/3ece0115837cd9757a14e98e3dd6e38c9a4dc2f6)
3. RestDocs 테스트 코드 작성 : [[f9fd631]](https://github.com/HangHae-Study/e-commerce-/commit/f9fd631f41d7d0cff9ea691e7bbfef583ce7f4d8)
4. API 명세서 소스 생성 : [[5003e4c]](https://github.com/HangHae-Study/e-commerce-/commit/5003e4cb86f1d61c383ea4463e1c91ba2dc8e1bd)

- API 명세서
    - 링크: [Swagger UI (CDN)](https://raw.githack.com/HangHae-Study/e-commerce-/step04/src/main/resources/static/docs/index.html)

## 리뷰 포인트
1. MockAPI 란?
- 프론트에서 작업을 빠르게 전개하기 위해, 전달해주는 더미 Req, Res 인 것으로 알고 있습니다.
- MockAPI 작성시에는 무조건 200 OK에 대한 값만을 작성 해주는지 궁금합니다.
- 당연히,, 에러에 대한 화면 또한 개발해야하니 아닐거라고 생각합니다..
- 또한, 각 요청에 대한 응답 코드(200, 400, 404..)를 뱉는 Request를 산정하고 Reseponse 를 가지별로 작성하였는데, 이 부분이 적절한지 한 번 확인해주시면 감사하겠습니다. (각 컨트롤러 클래스에 있는 Mapping)

2. 추후 변경 및 확장에 대해
- 시퀀스 다이어그램을 기준으로, 어떤 값을 받고 어떤 값을 뱉어 줘야할지를 생각해보며 Rest Docs를 위한 테스트 코드 필드들을 작성하였습니다.
- 이런 경우에,,, 추후 요구사항에 따라 반드시 Req, Res Dto 가 반드시 바뀔 것 같은데,, 이렇다면 제가 설계를 잘못한 것으로 간주해야할까요??
- 아니면 이런 변경에 대한 과정이 당연한 것인지 잘 모르겠습니다..
  (IT 회사에 다니지 않고 전산 시스템 개발 만하다보니.. 백/프론트 경계가 나눠져 있지 않고 협업에 대한 경험도 현저해서 잘 몰라요,.,.)
- 시스템 개발의 초입 단계에서 각 개발자간의 원활한 협업을 위해 MockAPI를 작성하는 것에 대해서, 서로 간의 간격을 좁히는 실무진에서의 노하우(?) 또는 사례가 있는지 개인적으로 궁금합니다

## Definition of Done (DoD)
[O] REST 규칙에 따라서 API를 문서화하였는가?
[O] 더미데이터를 제공하는 Mock API를 작성하였는가?
