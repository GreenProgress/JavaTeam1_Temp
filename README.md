이번 업데이트는 사용자의 답변 시나리오에 따라 맞춤형 법률 분석 결과를 제공하는 핵심 로직을 구현하고, 관련 데이터 모델을 리팩토링하는 데 중점을 두었습니다. 또한 사용자 편의를 위한 검색 기능이 추가되었습니다.

## 1. 핵심 기능 구현: 시나리오 기반 분석

`AnalysisService`가 임시(placeholder) 로직에서 벗어나, 실제 비즈니스 로직을 수행하는 핵심 엔진으로 구현되었습니다.

* **정교한 시나리오 분석**:
    * `AnalysisService`가 `QuestionRepository`와 연동하여, 질문의 **표시 순서(orderIndex)를 인지**하도록 수정되었습니다.
    * 사용자가 제출한 답변(`UserResponse`) 중 **최신 답변 세트**만을 필터링합니다.
    * 답변을 질문 순서대로 **정렬**하여 `YES_NO_YES`와 같은 고유한 시나리오 키(Scenario Key)를 생성합니다.
* **맞춤형 결과 제공**:
    * `buildAnalysisResult` 메서드 내부에 거대한 `switch` 문을 구현했습니다.
    * 수십 개의 **(상황 ID + 답변 조합)** 케이스에 대해 각각 다른 **결론 요약, 행동 절차, 필요 서류, 관련 법령**을 반환하도록 구현되었습니다.

## 2. 데이터 모델 리팩토링 (String -> ObjectId)

데이터베이스 쿼리 및 참조의 일관성을 위해 `situationId`를 `String`에서 MongoDB의 `ObjectId`로 변경했습니다.

* **`domain/Question`**: `situationId` 필드 타입을 `ObjectId`로 변경했습니다.
* **`domain/UserResponse`**: `situationId` 필드 타입을 `ObjectId`로 변경했습니다.
* **Repositories**: `QuestionRepository`, `UserResponseRepository`에서 `situationId`를 조회하는 메서드들이 `String` 대신 `ObjectId`를 파라미터로 받도록 수정되었습니다.
* **Services / Controllers**:
    * `QuestionService`: `String`으로 받은 `situationId`를 `ObjectId`로 변환하여 리포지토리를 호출합니다.
    * `ResponseController`: 클라이언트로부터 `String` 형식의 `situationId`를 받아 `new ObjectId()`로 변환하여 저장합니다.

## 3. 신규 기능 추가

### A. 법령 조항 상세 조회 API
분석 결과에서 관련 법령의 원문을 쉽게 조회할 수 있도록 특정 조항만 가져오는 API를 추가했습니다.

* `LegalDocumentRepository`: `findByLawIdAndArticleNoIn` 메서드 추가
* `LegalDocumentService`: `findArticles` 메서드 추가
* `LegalDocumentController`: `GET /api/LegalDocument/articles` 엔드포인트 추가

### B. 상황(Situation) 키워드 검색 API
사용자가 메인 페이지에서 원하는 법률 상황을 쉽게 찾을 수 있도록 키워드 검색 기능을 추가했습니다.

* `SituationRepository`: `findByTitleContainingOrDescriptionContaining` 메서드 추가
* `SituationService`: `searchSituations` 메서드 추가
* `SituationController`: `GET /api/Situation/search` 엔드포인트 추가

## 4. API 수정 및 단순화

`src2`에서는 사용자에게 직접 노출되는 API 중심으로 일부 엔드포인트를 정리하고 단순화했습니다.

* **`SituationController`**:
    * `getSituationById`가 `Optional`을 반환하는 대신, `Situation` 객체 또는 `null`을 반환하도록 서비스단(`SituationService`)과 함께 수정되었습니다.
    * `src1`에 있던 `POST` (생성) 및 `PUT` (수정) 엔드포인트가 **제거**되었습니다. (관리자 기능 분리)
* **`QuestionService`**:
    * `src1`에 있던 `createQuestion`, `updateQuestion` 등 CUD(관리자) 메서드가 **제거**되었습니다.
    * `getQuestionsBySituationId` (조회) 기능만 남겨 서비스의 역할을 명확히 했습니다.
* **`ResponseController`**:
    * 엔드포인트 경로가 `/api/UserResponse`에서 **`/api/responses`**로 변경되었습니다.
    * 답변 저장 시 `orderIndex`를 함께 저장하는 로직이 추가되었습니다.
