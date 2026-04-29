package com.stockpilot.knowledge.disclosure.service;

import com.stockpilot.client.dart.DartApiClient;
import com.stockpilot.client.dart.DartApiException;
import com.stockpilot.client.dart.DartCorpEntry;
import com.stockpilot.client.dart.dto.DartListItem;
import com.stockpilot.knowledge.disclosure.config.DisclosureProperties;
import com.stockpilot.knowledge.disclosure.entity.DartCorp;
import com.stockpilot.knowledge.disclosure.entity.Disclosure;
import com.stockpilot.knowledge.disclosure.entity.DisclosureType;
import com.stockpilot.knowledge.disclosure.repository.DartCorpRepository;
import com.stockpilot.knowledge.disclosure.repository.DisclosureRepository;
import com.stockpilot.stock.entity.Stock;
import com.stockpilot.stock.repository.StockRepository;
import com.stockpilot.watchlist.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisclosureIngestService {

    private static final int LOOKBACK_DAYS = 3;
    private static final String DART_URL_TEMPLATE = "https://dart.fss.or.kr/dsaf001/main.do?rcpNo=";
    private static final DateTimeFormatter RCEPT_DT_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final DartApiClient dartApiClient;
    private final DartCorpRepository dartCorpRepository;
    private final DisclosureRepository disclosureRepository;
    private final StockRepository stockRepository;
    private final WatchlistRepository watchlistRepository;
    private final DisclosureProperties disclosureProperties;

    @Transactional
    public int syncCorpMaster() {
        if (!dartApiClient.isEnabled()) {
            log.info("DART client disabled. Skipping corp master sync.");
            return 0;
        }

        List<DartCorpEntry> entries = dartApiClient.fetchCorpCodes();
        log.info("Fetched {} corp entries from DART.", entries.size());

        int upserted = 0;
        for (DartCorpEntry entry : entries) {
            Stock stock = (entry.getStockCode() != null)
                    ? stockRepository.findByMarket("KR").stream()
                            .filter(s -> entry.getStockCode().equals(s.getSymbol()))
                            .findFirst()
                            .orElse(null)
                    : null;

            DartCorp existing = dartCorpRepository.findByCorpCode(entry.getCorpCode()).orElse(null);
            if (existing == null) {
                dartCorpRepository.save(DartCorp.builder()
                        .corpCode(entry.getCorpCode())
                        .corpName(entry.getCorpName())
                        .stockCode(entry.getStockCode())
                        .stock(stock)
                        .modifyDate(entry.getModifyDate())
                        .build());
            } else {
                existing.update(entry.getCorpName(), entry.getStockCode(), stock, entry.getModifyDate());
            }
            upserted++;
        }
        log.info("DART corp master sync completed: {} entries upserted.", upserted);
        return upserted;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "recentDisclosures", allEntries = true),
            @CacheEvict(value = "stockDisclosures", allEntries = true),
            @CacheEvict(value = "watchlistDisclosures", allEntries = true)
    })
    public int ingestForTrackedCorps() {
        if (!dartApiClient.isEnabled()) {
            log.info("DART client disabled. Skipping disclosure ingest.");
            return 0;
        }

        Set<String> tickers = collectTrackedTickers();
        if (tickers.isEmpty()) {
            log.info("No tracked tickers configured. Skipping ingest.");
            return 0;
        }

        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(LOOKBACK_DAYS);
        int total = 0;
        for (String ticker : tickers) {
            DartCorp corp = dartCorpRepository.findByStockCode(ticker).orElse(null);
            if (corp == null) {
                log.warn("DART corp mapping not found for ticker '{}'. Skipping.", ticker);
                continue;
            }
            try {
                total += ingestForCorp(corp, from, to);
            } catch (DartApiException e) {
                if (e.isRateLimited()) {
                    log.warn("DART rate limit hit while ingesting '{}'. Aborting current cycle.", ticker);
                    break;
                }
                log.error("DART ingest failed for '{}': {}", ticker, e.getMessage());
            } catch (RuntimeException e) {
                log.error("Unexpected error ingesting '{}': {}", ticker, e.getMessage());
            }
        }
        log.info("Disclosure ingest completed: {} new disclosures across {} tickers.", total, tickers.size());
        return total;
    }

    private int ingestForCorp(DartCorp corp, LocalDate from, LocalDate to) {
        List<DartListItem> items = dartApiClient.fetchDisclosures(corp.getCorpCode(), from, to);
        int inserted = 0;
        for (DartListItem item : items) {
            if (item.getReceiptNo() == null || item.getReceiptNo().isBlank()) continue;
            if (disclosureRepository.existsByReceiptNo(item.getReceiptNo())) continue;

            DisclosureType type = DisclosureClassifier.classify(item.getReportName());
            LocalDateTime submittedAt = parseReceiptDateTime(item.getReceiptDate());

            disclosureRepository.save(Disclosure.builder()
                    .corpCode(corp.getCorpCode())
                    .corpName(item.getCorpName() != null ? item.getCorpName() : corp.getCorpName())
                    .stock(corp.getStock())
                    .receiptNo(item.getReceiptNo())
                    .reportName(item.getReportName())
                    .disclosureType(type)
                    .submittedAt(submittedAt)
                    .submitterName(item.getSubmitterName())
                    .remark(item.getRemark())
                    .dartUrl(DART_URL_TEMPLATE + item.getReceiptNo())
                    .build());
            inserted++;
        }
        return inserted;
    }

    private Set<String> collectTrackedTickers() {
        Set<String> tickers = new LinkedHashSet<>();
        if (disclosureProperties.getTrackedTickers() != null) {
            tickers.addAll(disclosureProperties.getTrackedTickers());
        }
        Set<String> watchlistTickers = new HashSet<>();
        watchlistRepository.findAll().forEach(watchlist ->
                watchlist.getItems().forEach(item ->
                        watchlistTickers.add(item.getStock().getSymbol())));
        tickers.addAll(watchlistTickers);
        return tickers;
    }

    private LocalDateTime parseReceiptDateTime(String receiptDate) {
        if (receiptDate == null || receiptDate.isBlank()) {
            return LocalDateTime.now();
        }
        try {
            return LocalDate.parse(receiptDate.trim(), RCEPT_DT_FORMAT).atStartOfDay();
        } catch (Exception e) {
            log.warn("Failed to parse rcept_dt '{}'. Falling back to now().", receiptDate);
            return LocalDateTime.now();
        }
    }
}
