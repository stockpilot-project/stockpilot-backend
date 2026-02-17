package com.stockpilot.stock.service;

import com.stockpilot.client.yahoo.YahooFinanceClient;
import com.stockpilot.client.yahoo.dto.YahooQuoteResponse;
import com.stockpilot.stock.entity.DailyPrice;
import com.stockpilot.stock.entity.Stock;
import com.stockpilot.stock.repository.DailyPriceRepository;
import com.stockpilot.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockPriceCollector {

    private final StockRepository stockRepository;
    private final DailyPriceRepository dailyPriceRepository;
    private final YahooFinanceClient yahooFinanceClient;

    // 미국 장 마감 후 (한국 시간 06:30)
    @Scheduled(cron = "0 30 6 * * MON-FRI")
    public void collectUSStockPrices() {
        log.info("US stock price collection started.");
        collectPrices("US");
    }

    // 한국 장 마감 후 (15:45)
    @Scheduled(cron = "0 45 15 * * MON-FRI")
    public void collectKRStockPrices() {
        log.info("KR stock price collection started.");
        collectPrices("KR");
    }

    @Transactional
    public void collectPrices(String market) {
        List<Stock> stocks = stockRepository.findByMarket(market);
        if (stocks.isEmpty()) {
            log.warn("No stocks found for market: {}", market);
            return;
        }

        List<String> symbols = stocks.stream()
                .map(Stock::getSymbol)
                .toList();

        yahooFinanceClient.getQuotes(symbols).ifPresentOrElse(
                quotes -> savePrices(stocks, quotes),
                () -> log.error("Failed to fetch quotes for market: {}", market)
        );
    }

    private void savePrices(List<Stock> stocks, List<YahooQuoteResponse.Quote> quotes) {
        Map<String, Stock> stockMap = stocks.stream()
                .collect(Collectors.toMap(Stock::getSymbol, s -> s));

        LocalDate today = LocalDate.now();
        int savedCount = 0;

        for (YahooQuoteResponse.Quote quote : quotes) {
            Stock stock = stockMap.get(quote.getSymbol());
            if (stock == null || quote.getRegularMarketPrice() == null) {
                continue;
            }

            boolean alreadyExists = dailyPriceRepository
                    .findByStockIdAndTradeDateBetweenOrderByTradeDateDesc(stock.getId(), today, today)
                    .stream()
                    .findAny()
                    .isPresent();

            if (alreadyExists) {
                continue;
            }

            DailyPrice dailyPrice = DailyPrice.builder()
                    .stock(stock)
                    .tradeDate(today)
                    .openPrice(toBigDecimal(quote.getRegularMarketOpen()))
                    .highPrice(toBigDecimal(quote.getRegularMarketDayHigh()))
                    .lowPrice(toBigDecimal(quote.getRegularMarketDayLow()))
                    .closePrice(toBigDecimal(quote.getRegularMarketPrice()))
                    .volume(quote.getRegularMarketVolume() != null ? quote.getRegularMarketVolume() : 0L)
                    .changeRate(toBigDecimal(quote.getRegularMarketChangePercent()))
                    .build();

            dailyPriceRepository.save(dailyPrice);
            savedCount++;
        }

        log.info("Saved {} price records.", savedCount);
    }

    private BigDecimal toBigDecimal(Double value) {
        return value != null ? BigDecimal.valueOf(value) : BigDecimal.ZERO;
    }
}
