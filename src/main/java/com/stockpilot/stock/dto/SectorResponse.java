package com.stockpilot.stock.dto;

import com.stockpilot.stock.entity.Sector;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SectorResponse {

    private Long id;
    private String name;
    private String nameKr;

    public static SectorResponse from(Sector sector) {
        return SectorResponse.builder()
                .id(sector.getId())
                .name(sector.getName())
                .nameKr(sector.getNameKr())
                .build();
    }
}
