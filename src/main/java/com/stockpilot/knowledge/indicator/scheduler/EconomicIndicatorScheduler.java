package com.stockpilot.knowledge.indicator.scheduler;

import com.stockpilot.client.bok.BokApiClient;
import com.stockpilot.knowledge.indicator.entity.Frequency;
import com.stockpilot.knowledge.indicator.service.EconomicIndicatorIngestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EconomicIndicatorScheduler {

    private final EconomicIndicatorIngestService ingestService;
    private final BokApiClient bokApiClient;

    @EventListener(ApplicationReadyEvent.class)
    public void backfillOnStartup() {
        if (!bokApiClient.isEnabled()) {
            log.info("BOK client disabled; skipping startup backfill.");
            return;
        }
        try {
            int total = ingestService.ingestAll();
            log.info("Startup backfill completed: {} observations ingested.", total);
        } catch (RuntimeException e) {
            log.error("Startup backfill failed: {}", e.getMessage());
        }
    }

    @Scheduled(cron = "0 30 7 * * *", zone = "Asia/Seoul")
    public void ingestDaily() {
        ingestService.ingestByFrequency(Frequency.DAILY);
    }

    @Scheduled(cron = "0 0 8 5 * *", zone = "Asia/Seoul")
    public void ingestMonthly() {
        ingestService.ingestByFrequency(Frequency.MONTHLY);
    }

    @Scheduled(cron = "0 0 9 5 1,4,7,10 *", zone = "Asia/Seoul")
    public void ingestQuarterly() {
        ingestService.ingestByFrequency(Frequency.QUARTERLY);
    }
}
