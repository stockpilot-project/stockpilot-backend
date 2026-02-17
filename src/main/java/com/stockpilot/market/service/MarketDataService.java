package com.stockpilot.market.service;

import com.stockpilot.market.dto.MarketStockResponse;
import com.stockpilot.stock.entity.DailyPrice;
import com.stockpilot.stock.repository.DailyPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MarketDataService {

    private final DailyPriceRepository dailyPriceRepository;

    @Cacheable(value = "marketGainers", key = "#market ?: 'all'")
    public List<MarketStockResponse> getGainers(String market) {
        LocalDate latestDate = findLatestTradeDate();
        if (latestDate == null) return List.of();

        return dailyPriceRepository.findByTradeDateOrderByChangeRateDesc(latestDate)
                .stream()
                .filter(dp -> dp.getChangeRate() != null && dp.getChangeRate().doubleValue() > 0)
                .filter(dp -> market == null || market.isBlank() || dp.getStock().getMarket().equals(market))
                .limit(10)
                .map(MarketStockResponse::from)
                .toList();
    }

    @Cacheable(value = "marketLosers", key = "#market ?: 'all'")
    public List<MarketStockResponse> getLosers(String market) {
        LocalDate latestDate = findLatestTradeDate();
        if (latestDate == null) return List.of();

        return dailyPriceRepository.findByTradeDateOrderByChangeRateAsc(latestDate)
                .stream()
                .filter(dp -> dp.getChangeRate() != null && dp.getChangeRate().doubleValue() < 0)
                .filter(dp -> market == null || market.isBlank() || dp.getStock().getMarket().equals(market))
                .limit(10)
                .map(MarketStockResponse::from)
                .toList();
    }

    @Cacheable(value = "marketTrending")
    public List<MarketStockResponse> getTrending() {
        LocalDate latestDate = findLatestTradeDate();
        if (latestDate == null) return List.of();

        return dailyPriceRepository.findByTradeDateOrderByVolumeDesc(latestDate)
                .stream()
                .limit(10)
                .map(MarketStockResponse::from)
                .toList();
    }

    private LocalDate findLatestTradeDate() {
        return dailyPriceRepository.findAll()
                .stream()
                .map(DailyPrice::getTradeDate)
                .max(LocalDate::compareTo)
                .orElse(null);
    }
}
