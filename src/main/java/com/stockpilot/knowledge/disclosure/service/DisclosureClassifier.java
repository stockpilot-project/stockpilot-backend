package com.stockpilot.knowledge.disclosure.service;

import com.stockpilot.knowledge.disclosure.entity.DisclosureType;

/**
 * report_nm 키워드로 공시 유형을 분류한다.
 * 우선순위: DIVIDEND > AUDIT > MATERIAL > STAKE > FAIR > PERIODIC > ETC
 */
public final class DisclosureClassifier {

    private DisclosureClassifier() {}

    public static DisclosureType classify(String reportName) {
        if (reportName == null || reportName.isBlank()) return DisclosureType.ETC;
        String name = reportName.trim();

        if (containsAny(name, "배당", "현금ㆍ현물배당", "현금배당", "주식배당")) {
            return DisclosureType.DIVIDEND;
        }
        if (containsAny(name, "감사보고서", "감사의견", "감사범위")) {
            return DisclosureType.AUDIT;
        }
        if (name.contains("주요사항보고서")) {
            return DisclosureType.MATERIAL;
        }
        if (containsAny(name, "주식등의대량보유", "임원ㆍ주요주주", "임원·주요주주", "지분", "최대주주")) {
            return DisclosureType.STAKE;
        }
        if (containsAny(name, "공정공시", "수시공시", "조회공시")) {
            return DisclosureType.FAIR;
        }
        if (containsAny(name, "분기보고서", "반기보고서", "사업보고서")) {
            return DisclosureType.PERIODIC;
        }
        return DisclosureType.ETC;
    }

    private static boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) return true;
        }
        return false;
    }
}
