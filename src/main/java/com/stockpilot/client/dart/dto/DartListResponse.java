package com.stockpilot.client.dart.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DartListResponse {

    private String status;
    private String message;

    @JsonProperty("page_no")
    private Integer pageNo;

    @JsonProperty("page_count")
    private Integer pageCount;

    @JsonProperty("total_count")
    private Integer totalCount;

    @JsonProperty("total_page")
    private Integer totalPage;

    private List<DartListItem> list;
}
