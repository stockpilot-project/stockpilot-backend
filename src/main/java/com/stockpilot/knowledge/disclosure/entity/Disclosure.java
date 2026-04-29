package com.stockpilot.knowledge.disclosure.entity;

import com.stockpilot.global.common.entity.Timestamp;
import com.stockpilot.stock.entity.Stock;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "disclosures",
        indexes = {
                @Index(name = "idx_disclosure_submitted_at", columnList = "submitted_at DESC"),
                @Index(name = "idx_disclosure_stock_submitted", columnList = "stock_id, submitted_at DESC"),
                @Index(name = "idx_disclosure_type_submitted", columnList = "disclosure_type, submitted_at DESC")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Disclosure extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "corp_code", nullable = false, length = 8)
    private String corpCode;

    @Column(name = "corp_name", nullable = false, length = 200)
    private String corpName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @Column(name = "receipt_no", nullable = false, unique = true, length = 14)
    private String receiptNo;

    @Column(name = "report_name", nullable = false, length = 500)
    private String reportName;

    @Enumerated(EnumType.STRING)
    @Column(name = "disclosure_type", nullable = false, length = 20)
    private DisclosureType disclosureType;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "submitter_name", length = 200)
    private String submitterName;

    @Column(length = 20)
    private String remark;

    @Column(name = "dart_url", nullable = false, length = 500)
    private String dartUrl;

    @Builder
    public Disclosure(String corpCode, String corpName, Stock stock, String receiptNo,
                      String reportName, DisclosureType disclosureType, LocalDateTime submittedAt,
                      String submitterName, String remark, String dartUrl) {
        this.corpCode = corpCode;
        this.corpName = corpName;
        this.stock = stock;
        this.receiptNo = receiptNo;
        this.reportName = reportName;
        this.disclosureType = disclosureType;
        this.submittedAt = submittedAt;
        this.submitterName = submitterName;
        this.remark = remark;
        this.dartUrl = dartUrl;
    }
}
