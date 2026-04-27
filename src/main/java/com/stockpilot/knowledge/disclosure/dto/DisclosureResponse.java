package com.stockpilot.knowledge.disclosure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stockpilot.knowledge.disclosure.entity.Disclosure;
import com.stockpilot.knowledge.disclosure.entity.DisclosureType;
import com.stockpilot.stock.entity.Stock;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DisclosureResponse {

    private Long id;
    private String receiptNo;
    private String corpCode;
    private String corpName;
    private Long stockId;
    private String ticker;
    private String reportName;
    private DisclosureType disclosureType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime submittedAt;

    private String submitterName;
    private String remark;
    private String dartUrl;

    public static DisclosureResponse from(Disclosure disclosure) {
        Stock stock = disclosure.getStock();
        return DisclosureResponse.builder()
                .id(disclosure.getId())
                .receiptNo(disclosure.getReceiptNo())
                .corpCode(disclosure.getCorpCode())
                .corpName(disclosure.getCorpName())
                .stockId(stock != null ? stock.getId() : null)
                .ticker(stock != null ? stock.getSymbol() : null)
                .reportName(disclosure.getReportName())
                .disclosureType(disclosure.getDisclosureType())
                .submittedAt(disclosure.getSubmittedAt())
                .submitterName(disclosure.getSubmitterName())
                .remark(disclosure.getRemark())
                .dartUrl(disclosure.getDartUrl())
                .build();
    }
}
