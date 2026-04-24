package com.stockpilot.global.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockpilot.knowledge.entity.Difficulty;
import com.stockpilot.knowledge.entity.InvestmentTerm;
import com.stockpilot.knowledge.entity.InvestmentTermRelation;
import com.stockpilot.knowledge.entity.TermCategory;
import com.stockpilot.knowledge.indicator.entity.EconomicIndicator;
import com.stockpilot.knowledge.indicator.entity.Frequency;
import com.stockpilot.knowledge.indicator.entity.IndicatorSource;
import com.stockpilot.knowledge.indicator.repository.EconomicIndicatorRepository;
import com.stockpilot.knowledge.repository.InvestmentTermRelationRepository;
import com.stockpilot.knowledge.repository.InvestmentTermRepository;
import com.stockpilot.knowledge.repository.TermCategoryRepository;
import com.stockpilot.stock.entity.DailyPrice;
import com.stockpilot.stock.entity.Sector;
import com.stockpilot.stock.entity.Stock;
import com.stockpilot.stock.repository.DailyPriceRepository;
import com.stockpilot.stock.repository.SectorRepository;
import com.stockpilot.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
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
    private final TermCategoryRepository termCategoryRepository;
    private final InvestmentTermRepository investmentTermRepository;
    private final InvestmentTermRelationRepository termRelationRepository;
    private final EconomicIndicatorRepository economicIndicatorRepository;
    private final ObjectMapper objectMapper;

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

        if (termCategoryRepository.count() == 0 && investmentTermRepository.count() == 0) {
            initInvestmentTerms();
        } else {
            log.info("Investment terms already exist. Skipping term initialization.");
        }

        if (economicIndicatorRepository.count() == 0) {
            initEconomicIndicators();
        } else {
            log.info("Economic indicators already exist. Skipping indicator initialization.");
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

    private void initInvestmentTerms() {
        log.info("Initializing investment terms...");

        InvestmentTermSeed seed;
        try (InputStream is = new ClassPathResource("data/investment-terms.json").getInputStream()) {
            seed = objectMapper.readValue(is, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("Failed to load investment-terms.json seed file", e);
            return;
        }

        Map<String, TermCategory> categoryByCode = new HashMap<>();
        for (CategorySeed c : seed.categories()) {
            TermCategory saved = termCategoryRepository.save(TermCategory.builder()
                    .code(c.code())
                    .name(c.name())
                    .displayOrder(c.displayOrder())
                    .build());
            categoryByCode.put(c.code(), saved);
        }

        Map<String, InvestmentTerm> termByName = new HashMap<>();
        for (TermSeed t : seed.terms()) {
            TermCategory category = categoryByCode.get(t.categoryCode());
            if (category == null) {
                log.warn("Unknown category code '{}' for term '{}'. Skipping.", t.categoryCode(), t.name());
                continue;
            }
            InvestmentTerm saved = investmentTermRepository.save(InvestmentTerm.builder()
                    .category(category)
                    .name(t.name())
                    .fullName(t.fullName())
                    .summary(t.summary())
                    .description(t.description())
                    .formula(t.formula())
                    .example(t.example())
                    .difficulty(Difficulty.valueOf(t.difficulty()))
                    .build());
            termByName.put(t.name(), saved);
        }

        int relationCount = 0;
        for (TermSeed t : seed.terms()) {
            InvestmentTerm source = termByName.get(t.name());
            if (source == null || t.relatedTermNames() == null) continue;
            for (String relatedName : t.relatedTermNames()) {
                InvestmentTerm target = termByName.get(relatedName);
                if (target == null || target.getId().equals(source.getId())) continue;
                if (!termRelationRepository.existsByTermIdAndRelatedTermId(source.getId(), target.getId())) {
                    termRelationRepository.save(InvestmentTermRelation.builder()
                            .term(source)
                            .relatedTerm(target)
                            .build());
                    relationCount++;
                }
                if (!termRelationRepository.existsByTermIdAndRelatedTermId(target.getId(), source.getId())) {
                    termRelationRepository.save(InvestmentTermRelation.builder()
                            .term(target)
                            .relatedTerm(source)
                            .build());
                    relationCount++;
                }
            }
        }

        log.info("Investment terms initialized: {} categories, {} terms, {} relations.",
                categoryByCode.size(), termByName.size(), relationCount);
    }

    private void initEconomicIndicators() {
        log.info("Initializing economic indicators (metadata only — observations backfilled by scheduler)...");

        saveIndicator(
                "BASE_RATE", "한국 기준금리", "%", Frequency.DAILY, IndicatorSource.BOK,
                "722Y001", "0101000",
                "한국은행 금융통화위원회가 결정하는 정책금리. 시중 모든 금리의 앵커 역할.",
                "기준금리 인상은 성장주 밸류에이션에 부정적, 예금/채권에 긍정적. 인하는 반대.",
                "기준금리", 1
        );
        saveIndicator(
                "USD_KRW", "원/달러 환율", "원", Frequency.DAILY, IndicatorSource.BOK,
                "731Y001", "0000001",
                "미국 달러에 대한 원화의 매매기준율.",
                "원화 약세(환율 상승)는 수출주에 유리, 수입·내수 기업에 비용 부담. 외국인 자본 유출입에도 즉시 영향.",
                "환율", 2
        );
        saveIndicator(
                "CPI", "소비자물가지수", "지수", Frequency.MONTHLY, IndicatorSource.BOK,
                "901Y009", "0",
                "가계 소비 재화·서비스 가격을 가중평균한 물가지수. 인플레이션 측정의 대표 지표.",
                "CPI가 목표(2%)를 크게 웃돌면 중앙은행이 금리 인상으로 대응 → 주식시장 압박.",
                "CPI", 3
        );
        saveIndicator(
                "GDP_GROWTH", "GDP 성장률", "%", Frequency.QUARTERLY, IndicatorSource.BOK,
                "200Y106", "10111",
                "실질 GDP의 전년동기 대비 성장률.",
                "2분기 연속 역성장은 기술적 경기침체. 성장률 둔화는 기업 실적 전망과 직결.",
                "GDP", 4
        );

        log.info("Economic indicator metadata initialized.");
    }

    private void saveIndicator(String code, String name, String unit, Frequency frequency,
                               IndicatorSource source, String statCode, String itemCode,
                               String description, String whyItMatters,
                               String relatedTermName, int displayOrder) {
        InvestmentTerm relatedTerm = investmentTermRepository.findByName(relatedTermName).orElse(null);
        if (relatedTerm == null) {
            log.warn("Related term '{}' not found for indicator '{}'. Saving without link.", relatedTermName, code);
        }
        economicIndicatorRepository.save(EconomicIndicator.builder()
                .code(code)
                .name(name)
                .unit(unit)
                .frequency(frequency)
                .source(source)
                .sourceStatCode(statCode)
                .sourceItemCode(itemCode)
                .description(description)
                .whyItMatters(whyItMatters)
                .relatedTerm(relatedTerm)
                .displayOrder(displayOrder)
                .build());
    }

    private record InvestmentTermSeed(List<CategorySeed> categories, List<TermSeed> terms) {}

    private record CategorySeed(String code, String name, int displayOrder) {}

    private record TermSeed(
            String categoryCode,
            String name,
            String fullName,
            String summary,
            String description,
            String formula,
            String example,
            String difficulty,
            List<String> relatedTermNames
    ) {}
}
