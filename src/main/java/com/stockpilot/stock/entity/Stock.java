package com.stockpilot.stock.entity;

import com.stockpilot.global.common.entity.Timestamp;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "stocks")
@SQLDelete(sql = "UPDATE stocks SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "name_kr", length = 200)
    private String nameKr;

    @Column(nullable = false, length = 10)
    private String market;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id")
    private Sector sector;

    @Column(name = "is_starter_pack", nullable = false)
    private boolean starterPack;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder
    public Stock(String symbol, String name, String nameKr, String market,
                 Sector sector, boolean starterPack, String description) {
        this.symbol = symbol;
        this.name = name;
        this.nameKr = nameKr;
        this.market = market;
        this.sector = sector;
        this.starterPack = starterPack;
        this.description = description;
    }
}
