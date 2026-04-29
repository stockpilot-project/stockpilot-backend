package com.stockpilot.knowledge.disclosure.scheduler;

import com.stockpilot.client.dart.DartApiClient;
import com.stockpilot.knowledge.disclosure.repository.DartCorpRepository;
import com.stockpilot.knowledge.disclosure.service.DisclosureIngestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DisclosureScheduler {

    private final DartApiClient dartApiClient;
    private final DartCorpRepository dartCorpRepository;
    private final DisclosureIngestService ingestService;

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        if (!dartApiClient.isEnabled()) {
            log.info("DART client disabled; skipping startup bootstrap.");
            return;
        }
        try {
            if (dartCorpRepository.count() == 0) {
                log.info("DART corp master is empty. Bootstrapping...");
                ingestService.syncCorpMaster();
            }
            ingestService.ingestForTrackedCorps();
        } catch (RuntimeException e) {
            log.error("DART startup bootstrap failed: {}", e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 6 * * MON", zone = "Asia/Seoul")
    public void syncCorpMasterWeekly() {
        ingestService.syncCorpMaster();
    }

    @Scheduled(cron = "0 */10 9-18 * * MON-FRI", zone = "Asia/Seoul")
    public void pollDisclosures() {
        ingestService.ingestForTrackedCorps();
    }

    @Scheduled(cron = "0 0 19 * * MON-FRI", zone = "Asia/Seoul")
    public void closingSweep() {
        ingestService.ingestForTrackedCorps();
    }
}
