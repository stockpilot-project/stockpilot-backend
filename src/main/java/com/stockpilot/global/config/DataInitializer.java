package com.stockpilot.global.config;

import com.stockpilot.stock.entity.DailyPrice;
import com.stockpilot.stock.entity.Sector;
import com.stockpilot.stock.entity.Stock;
import com.stockpilot.stock.repository.DailyPriceRepository;
import com.stockpilot.stock.repository.SectorRepository;
import com.stockpilot.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SectorRepository sectorRepository;
    private final StockRepository stockRepository;
    private final DailyPriceRepository dailyPriceRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (sectorRepository.count() == 0) {
            initSectorsAndStocks();
        }

        if (dailyPriceRepository.count() == 0) {
            initDailyPrices();
        } else {
            log.info("Daily price data already exists. Skipping price initialization.");
        }
    }

    private void initSectorsAndStocks() {
        log.info("Initializing sectors and stocks...");

        Sector tech = sectorRepository.save(Sector.builder().name("Technology").nameKr("기술").build());
        Sector finance = sectorRepository.save(Sector.builder().name("Finance").nameKr("금융").build());
        Sector healthcare = sectorRepository.save(Sector.builder().name("Healthcare").nameKr("헬스케어").build());
        Sector consumer = sectorRepository.save(Sector.builder().name("Consumer").nameKr("소비재").build());
        sectorRepository.save(Sector.builder().name("Energy").nameKr("에너지").build());
        Sector semiconductor = sectorRepository.save(Sector.builder().name("Semiconductor").nameKr("반도체").build());

        stockRepository.save(Stock.builder().symbol("AAPL").name("Apple Inc.").nameKr("애플").market("US").sector(tech).starterPack(true).build());
        stockRepository.save(Stock.builder().symbol("MSFT").name("Microsoft Corporation").nameKr("마이크로소프트").market("US").sector(tech).starterPack(true).build());
        stockRepository.save(Stock.builder().symbol("GOOGL").name("Alphabet Inc.").nameKr("구글").market("US").sector(tech).starterPack(true).build());
        stockRepository.save(Stock.builder().symbol("AMZN").name("Amazon.com Inc.").nameKr("아마존").market("US").sector(consumer).starterPack(true).build());
        stockRepository.save(Stock.builder().symbol("NVDA").name("NVIDIA Corporation").nameKr("엔비디아").market("US").sector(semiconductor).starterPack(true).build());
        stockRepository.save(Stock.builder().symbol("TSLA").name("Tesla Inc.").nameKr("테슬라").market("US").sector(consumer).starterPack(true).build());
        stockRepository.save(Stock.builder().symbol("JPM").name("JPMorgan Chase & Co.").nameKr("JP모건").market("US").sector(finance).starterPack(true).build());
        stockRepository.save(Stock.builder().symbol("JNJ").name("Johnson & Johnson").nameKr("존슨앤존슨").market("US").sector(healthcare).starterPack(true).build());
        stockRepository.save(Stock.builder().symbol("005930").name("Samsung Electronics").nameKr("삼성전자").market("KR").sector(semiconductor).starterPack(true).build());
        stockRepository.save(Stock.builder().symbol("000660").name("SK Hynix").nameKr("SK하이닉스").market("KR").sector(semiconductor).starterPack(true).build());
        stockRepository.save(Stock.builder().symbol("035420").name("NAVER Corporation").nameKr("네이버").market("KR").sector(tech).starterPack(true).build());
        stockRepository.save(Stock.builder().symbol("035720").name("Kakao Corp.").nameKr("카카오").market("KR").sector(tech).starterPack(true).build());

        log.info("Sectors and stocks initialization completed.");
    }

    private void initDailyPrices() {
        log.info("Initializing daily price data...");

        List<Stock> stocks = stockRepository.findAll();
        if (stocks.isEmpty()) {
            log.warn("No stocks found. Skipping daily price initialization.");
            return;
        }

        // Daily Price seed data (90 trading days)
        Map<String, double[]> priceMap = Map.ofEntries(
                Map.entry("AAPL",   new double[]{190.0, 50_000_000}),
                Map.entry("MSFT",   new double[]{420.0, 25_000_000}),
                Map.entry("GOOGL",  new double[]{175.0, 20_000_000}),
                Map.entry("AMZN",   new double[]{185.0, 40_000_000}),
                Map.entry("NVDA",   new double[]{130.0, 60_000_000}),
                Map.entry("TSLA",   new double[]{250.0, 80_000_000}),
                Map.entry("JPM",    new double[]{195.0, 10_000_000}),
                Map.entry("JNJ",    new double[]{155.0, 8_000_000}),
                Map.entry("005930", new double[]{78000.0, 15_000_000}),
                Map.entry("000660", new double[]{180000.0, 5_000_000}),
                Map.entry("035420", new double[]{210000.0, 3_000_000}),
                Map.entry("035720", new double[]{55000.0, 7_000_000})
        );

        Random random = new Random(42);
        LocalDate today = LocalDate.now();

        for (Stock stock : stocks) {
            double[] defaults = priceMap.getOrDefault(stock.getSymbol(), new double[]{100.0, 1_000_000});
            double price = defaults[0];
            long baseVolume = (long) defaults[1];

            LocalDate date = today.minusDays(120);
            int tradingDays = 0;

            while (!date.isAfter(today) && tradingDays < 90) {
                if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    date = date.plusDays(1);
                    continue;
                }

                double changePercent = (random.nextGaussian() * 2.5);
                double change = price * changePercent / 100.0;
                double closePrice = price + change;
                double openPrice = price + (random.nextGaussian() * price * 0.005);
                double highPrice = Math.max(openPrice, closePrice) + Math.abs(random.nextGaussian() * price * 0.008);
                double lowPrice = Math.min(openPrice, closePrice) - Math.abs(random.nextGaussian() * price * 0.008);
                long volume = baseVolume + (long) (random.nextGaussian() * baseVolume * 0.3);
                if (volume < 100_000L) volume = 100_000L;

                dailyPriceRepository.save(DailyPrice.builder()
                        .stock(stock)
                        .tradeDate(date)
                        .openPrice(BigDecimal.valueOf(openPrice).setScale(4, RoundingMode.HALF_UP))
                        .highPrice(BigDecimal.valueOf(highPrice).setScale(4, RoundingMode.HALF_UP))
                        .lowPrice(BigDecimal.valueOf(lowPrice).setScale(4, RoundingMode.HALF_UP))
                        .closePrice(BigDecimal.valueOf(closePrice).setScale(4, RoundingMode.HALF_UP))
                        .volume(volume)
                        .changeRate(BigDecimal.valueOf(changePercent).setScale(4, RoundingMode.HALF_UP))
                        .build());

                price = closePrice;
                date = date.plusDays(1);
                tradingDays++;
            }
        }

        log.info("Seed data initialization completed. Generated daily prices for {} stocks.", stocks.size());
    }
}
