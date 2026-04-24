# Code Convention

이 프로젝트의 모든 Java 코드는 아래 컨벤션을 따른다. 새 코드 작성/수정 시 항상 적용한다.

## 패키지 구조

```
com.stockpilot.<domain>/
├── controller/   REST 엔드포인트
├── service/      비즈니스 로직
├── repository/   JPA Repository 인터페이스
├── entity/       JPA Entity
└── dto/          Request / Response DTO
```

- 전역 공통(설정, 예외, 응답 래퍼)은 `com.stockpilot.global` 하위
- 외부 API 클라이언트는 `com.stockpilot.client.<vendor>`

## 네이밍

| 항목 | 규칙 | 예시 |
|------|------|------|
| Entity / Table | 단수형 / 복수형 snake_case | `Stock` / `stocks` |
| Request DTO | `{Domain}{Action}Request` | `WatchlistCreateRequest` |
| Response DTO | `{Domain}Response`, `{Domain}DetailResponse` | `StockResponse`, `StockDetailResponse` |
| ErrorCode | `{DOMAIN}_{SITUATION}` + prefix `S###`/`W###`/... | `STOCK_NOT_FOUND` (`S001`) |

## Entity

- `com.stockpilot.global.common.entity.Timestamp` 를 상속 (createdAt/updatedAt/deletedAt/softDelete 자동)
- `@NoArgsConstructor(access = AccessLevel.PROTECTED)` 로 무분별한 기본 생성자 차단
- `@Setter` 금지 — 상태 변경은 의미 있는 메서드로 표현 (`update(...)`, `addItem(...)`)
- 생성은 `@Builder` 로
- 연관관계 기본값: `@ManyToOne(fetch = FetchType.LAZY)`
- `@OneToMany`는 `mappedBy` + 필요 시 `cascade`, `orphanRemoval`

## Repository

- `JpaRepository<Entity, ID>` 확장
- 쿼리 메서드 네이밍(`findByXxxAndYyy`)으로 먼저 시도 → 복잡하면 `@Query` JPQL
- Service/Controller에 native SQL 직접 사용 금지

## DTO

**Request**
- `@Getter` + `@NoArgsConstructor`
- Jakarta Bean Validation (`@NotBlank`, `@NotNull`, `@Size` 등) 부여
- Controller에서 `@Valid` 로 트리거

**Response**
- `@Getter` + `@Builder`
- `static from(Entity)` 또는 `static of(...)` 팩토리 메서드로 변환
- Entity를 그대로 반환하지 않는다 (순환참조/지연로딩 방지)
- 연관 엔티티는 중첩 Response DTO로 변환

## Service

- `@Service` + `@RequiredArgsConstructor` (생성자 주입)
- 클래스 레벨: `@Transactional(readOnly = true)`
- 변경 메서드에만 `@Transactional` 오버라이드
- 조회 부하가 큰 메서드는 `@Cacheable(value = "...", key = "...")` 적용 (`CacheConfig` 이미 구성됨)
- 검증/미존재 시 `BusinessException`/`EntityNotFoundException` throw
- HTTP 예외(`ResponseStatusException`, `HttpException`) 또는 `RuntimeException` 직접 throw 금지

## Controller

- `@RestController` + `@RequestMapping("/api/v1/<복수형 리소스>")`
- 모든 응답은 `ApiResponse<T>` 로 래핑 (`ApiResponse.ok(data)` / `ApiResponse.ok()`)
- 생성 API: `@ResponseStatus(HttpStatus.CREATED)` 추가
- Controller는 얇게 유지 — 파라미터 바인딩 → Service 호출 → 반환만
- 비즈니스 로직, DB 접근, 예외 처리 모두 Controller에서 하지 않음
- Swagger 어노테이션 필수: `@Tag`(클래스), `@Operation`(메서드), `@Parameter`(파라미터)

## REST URL 규칙

- 접두사: `/api/v1/`
- 리소스: **복수형 명사** (`/stocks`, `/watchlists`)
- 동사 URL 금지 (❌ `/getStocks` → ✅ `GET /stocks`)
- 계층(중첩) 리소스로 소유 관계 표현: `/watchlists/{id}/stocks/{stockId}`
- 검색/필터는 Query Parameter: `/stocks?market=US`, `/stocks/search?q=samsung`

## 예외 처리

```
BusinessException (RuntimeException)
├── EntityNotFoundException   리소스 미존재 (404)
└── InvalidValueException     유효성/도메인 규칙 위반 (400)
```

1. 필요한 에러를 `global/error/exception/ErrorCode.java` enum에 먼저 추가
2. 적절한 Exception throw — 직접 enum 생성자 호출 가능
3. `GlobalExceptionHandler`가 자동으로 `ApiResponse.error(code, message)` + 상태 코드로 변환

```java
// ❌ WRONG
throw new ResponseStatusException(HttpStatus.NOT_FOUND, "...");
throw new RuntimeException("이미 존재합니다");

// ✅ CORRECT
throw new EntityNotFoundException(ErrorCode.FEATURE_NOT_FOUND);
throw new BusinessException(ErrorCode.FEATURE_DUPLICATE);
```

## 안티패턴

- Entity에 `@Setter` / public setter 추가
- Controller/Service에서 `ResponseStatusException` 직접 throw
- Entity를 Response로 그대로 반환
- Service에서 `PrismaService`/`EntityManager`를 직접 다루기 (Repository 통해서만)
- Request DTO에 `@Valid` 없이 서비스 내부에서 수동 검증
- `/api` 버전 접두사(`/v1`) 생략
- 응답 바디를 `ApiResponse`로 래핑하지 않고 raw 객체 반환
