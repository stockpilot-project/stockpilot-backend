package com.stockpilot.knowledge.disclosure.entity;

import com.stockpilot.global.common.entity.Timestamp;
import com.stockpilot.stock.entity.Stock;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "dart_corps")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DartCorp extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "corp_code", nullable = false, unique = true, length = 8)
    private String corpCode;

    @Column(name = "corp_name", nullable = false, length = 200)
    private String corpName;

    @Column(name = "stock_code", length = 10)
    private String stockCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @Column(name = "modify_date")
    private LocalDate modifyDate;

    @Builder
    public DartCorp(String corpCode, String corpName, String stockCode, Stock stock, LocalDate modifyDate) {
        this.corpCode = corpCode;
        this.corpName = corpName;
        this.stockCode = stockCode;
        this.stock = stock;
        this.modifyDate = modifyDate;
    }

    public void update(String corpName, String stockCode, Stock stock, LocalDate modifyDate) {
        this.corpName = corpName;
        this.stockCode = stockCode;
        this.stock = stock;
        this.modifyDate = modifyDate;
    }
}
