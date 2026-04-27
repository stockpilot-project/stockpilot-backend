package com.stockpilot.client.bok.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EcosRow {

    @JsonProperty("STAT_CODE")
    private String statCode;

    @JsonProperty("STAT_NAME")
    private String statName;

    @JsonProperty("ITEM_CODE1")
    private String itemCode1;

    @JsonProperty("ITEM_NAME1")
    private String itemName1;

    @JsonProperty("UNIT_NAME")
    private String unitName;

    @JsonProperty("TIME")
    private String time;

    @JsonProperty("DATA_VALUE")
    private String dataValue;
}
