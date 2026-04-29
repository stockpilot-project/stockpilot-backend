package com.stockpilot.knowledge.indicator.entity;

import com.stockpilot.global.common.entity.Timestamp;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@SQLDelete(sql = "UPDATE indicator_observations SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Table(
        name = "indicator_observations",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_indicator_observation",
                columnNames = {"indicator_id", "observed_at"}
        ),
        indexes = @Index(name = "idx_indicator_observed_at", columnList = "indicator_id, observed_at DESC")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IndicatorObservation extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "indicator_id", nullable = false)
    private EconomicIndicator indicator;

    @Column(name = "observed_at", nullable = false)
    private LocalDate observedAt;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal value;

    @Builder
    public IndicatorObservation(EconomicIndicator indicator, LocalDate observedAt, BigDecimal value) {
        this.indicator = indicator;
        this.observedAt = observedAt;
        this.value = value;
    }
}
