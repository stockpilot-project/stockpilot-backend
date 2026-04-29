package com.stockpilot.global.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 모든 도메인 엔티티의 공통 시간 추적 / 소프트 삭제 컬럼 베이스.
 *
 * <p>소프트 삭제는 각 엔티티에 부착된 {@code @SQLDelete} / {@code @SQLRestriction}
 * 어노테이션이 처리한다 (Hibernate). {@code repository.delete(entity)} 호출 시
 * DELETE 대신 {@code UPDATE ... SET deleted_at = NOW()} 가 실행되고, 모든 SELECT 에
 * {@code WHERE deleted_at IS NULL} 가 자동 추가된다.
 *
 * <p>삭제된 행까지 포함해 조회해야 하는 운영 쿼리는 native query 로 작성한다.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Timestamp {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
