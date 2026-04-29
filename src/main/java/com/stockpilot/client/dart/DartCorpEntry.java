package com.stockpilot.client.dart;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class DartCorpEntry {

    private final String corpCode;
    private final String corpName;
    private final String stockCode;
    private final LocalDate modifyDate;
}
