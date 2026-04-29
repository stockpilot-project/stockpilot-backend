import java.sql.DriverManager

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        // Flyway Gradle 플러그인이 PostgreSQL DB 모듈을 인식하도록 buildscript classpath에 추가
        classpath("org.flywaydb:flyway-database-postgresql:10.20.1")
        classpath("org.postgresql:postgresql:42.7.4")
    }
}

plugins {
    java
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.flywaydb.flyway") version "10.20.1"
}

group = "com.stockpilot"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Database
    runtimeOnly("org.postgresql:postgresql")

    // Migration
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")

    // Cache
    implementation("com.github.ben-manes.caffeine:caffeine")

    // Swagger / OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Test Lombok
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Flyway CLI 태스크 (./gradlew flywayInfo / flywayRepair / flywayMigrate / flywayValidate)
flyway {
    url = System.getenv("FLYWAY_URL") ?: "jdbc:postgresql://localhost:5432/stockpilot"
    user = System.getenv("FLYWAY_USER") ?: "stockpilot"
    password = System.getenv("FLYWAY_PASSWORD") ?: "stockpilot1234"
    locations = arrayOf("filesystem:src/main/resources/db/migration")
    baselineOnMigrate = true
    baselineVersion = "0"
}

// flyway_schema_history 의 stale 행을 모두 삭제. 다음 부팅 때 baseline-on-migrate 가 V1 으로 재기록.
// 일반 운영용이 아니라 마이그레이션 환경 정리용 일회성 태스크.
tasks.register("flywayResetHistory") {
    group = "flyway"
    description = "Deletes all rows in flyway_schema_history so the next bootRun re-baselines. Use only when stale ghost entries exist."

    doLast {
        val url = System.getenv("FLYWAY_URL") ?: "jdbc:postgresql://localhost:5432/stockpilot"
        val user = System.getenv("FLYWAY_USER") ?: "stockpilot"
        val password = System.getenv("FLYWAY_PASSWORD") ?: "stockpilot1234"

        val conn = DriverManager.getConnection(url, user, password)
        try {
            val stmt = conn.createStatement()
            try {
                val rs = stmt.executeQuery(
                    "SELECT COUNT(*) FROM information_schema.tables " +
                            "WHERE table_name = 'flyway_schema_history'"
                )
                val tableExists = try {
                    rs.next() && rs.getInt(1) > 0
                } finally {
                    rs.close()
                }

                if (!tableExists) {
                    println("flyway_schema_history table not found. Nothing to reset.")
                } else {
                    stmt.executeUpdate("DROP TABLE flyway_schema_history")
                    println("Dropped flyway_schema_history. Run ./gradlew flywayBaseline (or bootRun) to re-baseline as V1.")
                }
            } finally {
                stmt.close()
            }
        } finally {
            conn.close()
        }
    }
}
