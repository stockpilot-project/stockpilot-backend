# StockPilot Backend

Spring Boot 기반 주식 정보 제공 REST API. Yahoo Finance로 시세를 수집하고, 종목/섹터/시장/관심목록 API를 제공한다.

## Tech Stack

- **Java 21** + **Spring Boot 3.4.2** (Gradle Kotlin DSL)
- **Spring Data JPA** + **PostgreSQL**
- **Spring Cache** + **Caffeine**
- **SpringDoc OpenAPI 2.8.4** (Swagger UI: `/docs`)
- **Lombok**
- 테스트: **JUnit 5** + **Testcontainers (postgresql)**

## 주요 명령어

```bash
./gradlew bootRun          # 로컬 실행 (기본 포트 8080, profile: local)
./gradlew test             # 테스트 실행
./gradlew build            # 빌드 (테스트 포함)
./gradlew clean build      # 클린 빌드
```

## 디렉토리 구조

```
src/main/java/com/stockpilot/
├── stock/          종목 도메인 (Stock, Sector, DailyPrice)
├── market/         시장 데이터 (급등/급락/트렌딩)
├── watchlist/      관심 종목
├── client/yahoo/   Yahoo Finance API 클라이언트
└── global/
    ├── config/     JPA, Cache, Scheduler, WebConfig, DataInitializer
    ├── common/     ApiResponse, Timestamp (base entity)
    └── error/      GlobalExceptionHandler, BusinessException, ErrorCode
```

각 도메인은 `controller/` `service/` `repository/` `entity/` `dto/` 하위 구조를 따른다.

## 코드 컨벤션

모든 Java 코드 작성/수정 시 @.claude/code_convention.md 의 규칙을 따른다.

요약:
- Entity는 `Timestamp` 상속, `@Setter` 금지 — 변경은 메서드로
- Service 클래스 `@Transactional(readOnly = true)` + 변경 메서드 `@Transactional`
- Controller 응답은 `ApiResponse<T>` 래핑, 생성은 `201`
- 예외는 `ErrorCode` enum 추가 후 `BusinessException`/`EntityNotFoundException` throw
- REST URL: `/api/v1/<복수형 리소스>`

## Skills

자주 수행하는 작업은 `.claude/skills/` 에 정의되어 자동 매칭된다.

- `commit` — Git 커밋 (Conventional Commit 한국어)
- `new-api` — 새 도메인 REST API 보일러플레이트 생성

## Git 주의사항

- **개인 프로젝트** — 반드시 개인 계정(`suhyeon3484@naver.com`)으로 커밋
- 회사 계정(`rio`)으로 설정돼 있으면 **커밋 중단**하고 사용자에게 알림
- 브랜치 형식: `<type>/<summary-kebab-case>` (예: `feat/watchlist`)

## 외부 의존

- **Yahoo Finance API** (RapidAPI) — 환경변수 `YAHOO_FINANCE_API_KEY` 필요
- PostgreSQL — 로컬 실행 시 별도 기동 필요 (Testcontainers는 테스트에서만 사용)

## Swagger

앱 실행 후 `http://localhost:8080/docs` 에서 API 문서 확인.
