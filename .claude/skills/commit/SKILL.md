---
name: commit
description: Use when the user asks to create a git commit in this personal Spring Boot project. Verifies the personal git account (suhyeon3484@naver.com, not the rio work account), reads git status/diff, then writes a Korean Conventional Commit message (feat/fix/docs/style/refactor/test/chore/set) with optional domain scope (stock/market/watchlist/client/global/scheduler/jpa/test). Branch naming is <type>/<summary-kebab-case>; no Jira ticket prefix in the commit message.
---

# Git Commit

변경 사항을 검토하고 Conventional Commit 형식에 따라 한국어로 커밋 메시지를 작성한다.

## Procedure

1. `git config user.email`로 현재 git 계정이 **개인 계정(`suhyeon3484@naver.com`)** 인지 확인
   - 회사 계정(`rio`)으로 설정되어 있으면 커밋 중단하고 사용자에게 알림
2. `git branch --show-current`로 현재 브랜치명 확인 (`<type>/<summary>` 형식)
3. `git status`로 변경 사항 확인
4. `git diff`로 diff 검토 (staged/unstaged 모두)
5. Conventional Commit 형식에 맞는 한국어 커밋 메시지 작성
6. 커밋 실행

## Branch Naming

브랜치는 작업 타입과 요약으로 생성한다.

```
<type>/<summary-kebab-case>
```

### Examples
- `feat/watchlist` — 관심 종목 기능 추가
- `feat/seed-daily-prices` — 일별 시세 시드 데이터 추가
- `fix/yahoo-timeout` — Yahoo API 타임아웃 버그 수정
- `refactor/stock-service` — 종목 서비스 리팩토링
- `test` — 테스트 코드 작업 (단일 타입 브랜치도 허용)

## Conventional Commit Format

```
<type>: <한국어 설명>

[optional body - 한국어]

[optional footer]
```

> **주의**: 개인 프로젝트이므로 Jira 티켓/브랜치 prefix(`[...]`)를 커밋 메시지에 붙이지 않는다.

### Type
- `feat`: 새로운 기능
- `fix`: 버그 수정
- `docs`: 문서 변경
- `style`: 코드 포맷팅 (기능 변경 없음)
- `refactor`: 리팩토링 (기능 변경 없음)
- `test`: 테스트 추가/수정
- `chore`: 빌드, 설정, 의존성 변경
- `set`: 프로젝트 초기 설정 (기존 git log 컨벤션 유지)

### Scope (optional)
Spring Boot 프로젝트의 실제 도메인/패키지 구조 기반.

- `stock`: 종목 도메인 (`com.stockpilot.stock`)
- `market`: 시장 데이터 도메인 (`com.stockpilot.market`)
- `watchlist`: 관심 종목 도메인 (`com.stockpilot.watchlist`)
- `client`: 외부 API 클라이언트 (`com.stockpilot.client`, 예: `yahoo`)
- `global`: 전역 설정/공통 모듈 (`com.stockpilot.global`)
  - 세부: `config`, `error`, `response` 등
- `scheduler`: 스케줄러/배치
- `jpa`: JPA 엔티티/리포지토리 관련
- `test`: 테스트 인프라

### Examples

기존 git log 스타일(scope 생략):

```
feat: 관심 종목(Watchlist) CRUD API 구현
```

```
feat: Yahoo Finance API 클라이언트 연동

- WebClient 기반 비동기 호출 적용
- DTO 매핑 및 예외 변환 처리
```

Scope 포함 스타일:

```
fix(yahoo): API 응답 타임아웃 시 재시도 로직 추가

고부하 상황에서 간헐적 타임아웃이 발생하여
exponential backoff 기반 재시도를 적용한다.
```

```
refactor(stock): StockService 책임 분리

- 조회 로직을 StockQueryService로 분리
- 시세 수집 로직을 StockPriceCollector로 분리
```

```
test: 시장 데이터, 관심 종목 테스트 코드 추가
```

```
chore: Spring Boot 3.x 의존성 업데이트
```

## Pre-commit Checklist

커밋 전에 반드시 확인:

1. ✅ git 계정이 개인 계정(`suhyeon3484@naver.com`)인지
2. ✅ 변경 사항에 관련 없는 파일이 포함되지 않았는지
3. ✅ `.env`, 인증 키 등 민감 파일이 staged되지 않았는지
4. ✅ 커밋 메시지가 "무엇"보다 "왜"를 설명하는지
