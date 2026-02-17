package com.stockpilot.watchlist.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WatchlistCreateRequest {

    @NotBlank(message = "관심 목록 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "세션 ID는 필수입니다.")
    private String sessionId;
}
