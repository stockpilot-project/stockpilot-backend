package com.stockpilot.client.dart.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DartListItem {

    @JsonProperty("corp_code")
    private String corpCode;

    @JsonProperty("corp_name")
    private String corpName;

    @JsonProperty("stock_code")
    private String stockCode;

    @JsonProperty("rcept_no")
    private String receiptNo;

    @JsonProperty("report_nm")
    private String reportName;

    @JsonProperty("flr_nm")
    private String submitterName;

    @JsonProperty("rcept_dt")
    private String receiptDate;

    @JsonProperty("rm")
    private String remark;
}
