package com.stockpilot.knowledge.disclosure.service;

import com.stockpilot.knowledge.disclosure.entity.DisclosureType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class DisclosureClassifierTest {

    @DisplayName("report_nm 키워드로 공시 유형이 분류된다")
    @ParameterizedTest(name = "[{index}] {0} → {1}")
    @CsvSource({
            "'현금ㆍ현물배당결정',                    DIVIDEND",
            "'주식배당결정',                          DIVIDEND",
            "'감사보고서',                            AUDIT",
            "'주요사항보고서(유형자산 양수 결정)',    MATERIAL",
            "'주식등의대량보유상황보고서',            STAKE",
            "'임원ㆍ주요주주특정증권등소유상황보고서',STAKE",
            "'공정공시',                              FAIR",
            "'수시공시',                              FAIR",
            "'분기보고서 (2026.03)',                  PERIODIC",
            "'반기보고서 (2026.06)',                  PERIODIC",
            "'사업보고서 (2025.12)',                  PERIODIC",
            "'기타경영사항(자율공시)',                ETC"
    })
    void classify(String reportName, DisclosureType expected) {
        assertThat(DisclosureClassifier.classify(reportName)).isEqualTo(expected);
    }

    @DisplayName("우선순위: 배당이 주요사항보다 우선")
    @org.junit.jupiter.api.Test
    void dividendBeatsMaterial() {
        assertThat(DisclosureClassifier.classify("주요사항보고서(현금배당결정)"))
                .isEqualTo(DisclosureType.DIVIDEND);
    }

    @DisplayName("null/빈 문자열은 ETC")
    @org.junit.jupiter.api.Test
    void blankToEtc() {
        assertThat(DisclosureClassifier.classify(null)).isEqualTo(DisclosureType.ETC);
        assertThat(DisclosureClassifier.classify("")).isEqualTo(DisclosureType.ETC);
        assertThat(DisclosureClassifier.classify("   ")).isEqualTo(DisclosureType.ETC);
    }
}
