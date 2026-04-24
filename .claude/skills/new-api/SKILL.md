---
name: new-api
description: Use when creating a new REST API endpoint or adding a new domain (controller/service/repository/entity/DTO) in this Spring Boot + JPA project. Generates boilerplate following project conventions — Timestamp-extending entity, @Transactional(readOnly = true) service, ApiResponse<T>-wrapped controller under /api/v1, ErrorCode + BusinessException/EntityNotFoundException, and Swagger annotations. For detailed conventions see .claude/code_convention.md.
---

# Create REST API

새 도메인의 REST API 보일러플레이트를 생성한다.
상세 컨벤션은 `.claude/code_convention.md` 참고.

## Procedure

1. 도메인 패키지 생성: `com.stockpilot.<domain>/{controller,service,repository,entity,dto}`
2. Entity → Repository → DTO → Service → Controller 순으로 작성
3. 새 에러를 `global/error/exception/ErrorCode.java`에 추가
4. 필요 시 Swagger 확인용으로 앱 실행 후 `/swagger-ui.html` 로 검증

## Entity

```java
@Entity
@Table(name = "features")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feature extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Builder
    public Feature(String name) {
        this.name = name;
    }

    public void update(String name) {
        this.name = name;
    }
}
```

## Repository

```java
public interface FeatureRepository extends JpaRepository<Feature, Long> {
    boolean existsByName(String name);
}
```

## DTO

```java
// FeatureCreateRequest.java
@Getter @NoArgsConstructor
public class FeatureCreateRequest {
    @NotBlank(message = "이름은 필수입니다.")
    private String name;
}

// FeatureResponse.java
@Getter @Builder
public class FeatureResponse {
    private Long id;
    private String name;

    public static FeatureResponse from(Feature feature) {
        return FeatureResponse.builder()
                .id(feature.getId())
                .name(feature.getName())
                .build();
    }
}
```

## Service

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeatureService {

    private final FeatureRepository featureRepository;

    @Transactional
    public FeatureResponse createFeature(FeatureCreateRequest request) {
        if (featureRepository.existsByName(request.getName())) {
            throw new BusinessException(ErrorCode.FEATURE_DUPLICATE);
        }
        Feature feature = Feature.builder().name(request.getName()).build();
        featureRepository.save(feature);
        return FeatureResponse.from(feature);
    }

    public FeatureResponse getFeature(Long id) {
        Feature feature = featureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FEATURE_NOT_FOUND));
        return FeatureResponse.from(feature);
    }
}
```

## Controller

```java
@Tag(name = "Feature", description = "기능 API")
@RestController
@RequestMapping("/api/v1/features")
@RequiredArgsConstructor
public class FeatureController {

    private final FeatureService featureService;

    @Operation(summary = "기능 생성")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<FeatureResponse> createFeature(
            @Valid @RequestBody FeatureCreateRequest request) {
        return ApiResponse.ok(featureService.createFeature(request));
    }

    @Operation(summary = "기능 상세 조회")
    @GetMapping("/{id}")
    public ApiResponse<FeatureResponse> getFeature(
            @Parameter(description = "기능 ID", example = "1")
            @PathVariable Long id) {
        return ApiResponse.ok(featureService.getFeature(id));
    }
}
```

## ErrorCode 추가

`com.stockpilot.global.error.exception.ErrorCode` 에 enum 항목 추가:

```java
// Feature
FEATURE_NOT_FOUND(HttpStatus.NOT_FOUND, "F001", "Feature not found"),
FEATURE_DUPLICATE(HttpStatus.CONFLICT, "F002", "Feature already exists"),
```

## Checklist

- [ ] 패키지 구조 (`controller/service/repository/entity/dto`) 생성
- [ ] Entity가 `Timestamp` 상속 + `@NoArgsConstructor(PROTECTED)` + `@Builder`
- [ ] Response DTO에 `static from(Entity)` 팩토리
- [ ] Service 클래스에 `@Transactional(readOnly = true)`, 변경 메서드에 `@Transactional`
- [ ] Controller는 `ApiResponse<T>` 래핑 + 생성 API는 `201`
- [ ] `ErrorCode` 추가 후 `BusinessException`/`EntityNotFoundException` throw
- [ ] Swagger `@Tag`, `@Operation` 어노테이션 부여
