package com.stockpilot.global.config;

import com.stockpilot.stock.entity.Sector;
import com.stockpilot.stock.entity.Stock;
import com.stockpilot.stock.repository.SectorRepository;
import com.stockpilot.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SectorRepository sectorRepository;
    private final StockRepository stockRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (sectorRepository.count() > 0) {
            log.info("Seed data already exists. Skipping initialization.");
            return;
        }

        log.info("Initializing seed data...");

        // Sectors
        Sector tech = sectorRepository.save(Sector.builder().name("Technology").nameKr("기술").build());
        Sector finance = sectorRepository.save(Sector.builder().name("Finance").nameKr("금융").build());
        Sector healthcare = sectorRepository.save(Sector.builder().name("Healthcare").nameKr("헬스케어").build());
        Sector consumer = sectorRepository.save(Sector.builder().name("Consumer").nameKr("소비재").build());
        Sector energy = sectorRepository.save(Sector.builder().name("Energy").nameKr("에너지").build());
        Sector semiconductor = sectorRepository.save(Sector.builder().name("Semiconductor").nameKr("반도체").build());

        // US Starter Stocks
        stockRepository.save(Stock.builder().symbol("AAPL").name("Apple Inc.").nameKr("애플").market("US").sector(tech).starterPack(true).build());
        stockRepository.save(Stock.builder().symbol("MSFT").name("Microsoft Corporation").nameKr("마이크로소프트").market("US").sector(tech).starterPack(true).build());
        stockRepository.save(Stock.builder().symbol("GOOGL").name("Alphabet Inc.").nameKr("구글").market("US").sector(tech).starterPack(true).build());
        stockRepository.save(Stock.builder().symbol("AMZN").name("Amazon.com Inc.").nameKr("아마존").market("US").sector(consumer).starterPack(true).build());
        stockRepository.save(Stock.builder().symbol("NVDA").name("NVIDIA Corporation").nameKr("엔비디아").market("US").sector(semiconductor).starterPack(true).build());
        stockRepository.save(Stock.builder().symbol("TSLA").name("Tesla Inc.").nameKr("테슬라").market("US").sector(consumer).starterPack(true).build());
        stockRepository.save(Stock.builder().symbol("JPM").name("JPMorgan Chase & Co.").nameKr("JP모건").market("US").sector(finance).starterPack(true).build());
        stockRepository.save(Stock.builder().symbol("JNJ").name("Johnson & Johnson").nameKr("존슨앤존슨").market("US").sector(healthcare).starterPack(true).build());

        // KR Starter Stocks
        stockRepository.save(Stock.builder().symbol("005930").name("Samsung Electronics").nameKr("삼성전자").market("KR").sector(semiconductor).starterPack(true).build());
        stockRepository.save(Stock.builder().symbol("000660").name("SK Hynix").nameKr("SK하이닉스").market("KR").sector(semiconductor).starterPack(true).build());
        stockRepository.save(Stock.builder().symbol("035420").name("NAVER Corporation").nameKr("네이버").market("KR").sector(tech).starterPack(true).build());
        stockRepository.save(Stock.builder().symbol("035720").name("Kakao Corp.").nameKr("카카오").market("KR").sector(tech).starterPack(true).build());

        log.info("Seed data initialization completed.");
    }
}
