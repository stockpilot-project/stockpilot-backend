package com.stockpilot.stock.service;

import com.stockpilot.global.error.exception.EntityNotFoundException;
import com.stockpilot.global.error.exception.ErrorCode;
import com.stockpilot.stock.dto.DailyPriceResponse;
import com.stockpilot.stock.dto.SectorResponse;
import com.stockpilot.stock.dto.StockDetailResponse;
import com.stockpilot.stock.dto.StockResponse;
import com.stockpilot.stock.entity.DailyPrice;
import com.stockpilot.stock.entity.Stock;
import com.stockpilot.stock.repository.DailyPriceRepository;
import com.stockpilot.stock.repository.SectorRepository;
import com.stockpilot.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private final StockRepository stockRepository;
    private final SectorRepository sectorRepository;
    private final DailyPriceRepository dailyPriceRepository;

    @Cacheable(value = "stocks", key = "#market ?: 'all'")
    public List<StockResponse> getStocks(String market) {
        List<Stock> stocks = (market != null && !market.isBlank())
                ? stockRepository.findByMarket(market)
                : stockRepository.findAll();
        return stocks.stream().map(StockResponse::from).toList();
    }

    public StockDetailResponse getStock(Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STOCK_NOT_FOUND));
        DailyPrice latestPrice = dailyPriceRepository.findFirstByStockIdOrderByTradeDateDesc(id)
                .orElse(null);
        return StockDetailResponse.of(stock, latestPrice);
    }

    public List<StockResponse> searchStocks(String query) {
        return stockRepository
                .findByNameContainingIgnoreCaseOrNameKrContainingIgnoreCaseOrSymbolContainingIgnoreCase(
                        query, query, query)
                .stream()
                .map(StockResponse::from)
                .toList();
    }

    @Cacheable(value = "starterStocks")
    public List<StockResponse> getStarterStocks() {
        return stockRepository.findByStarterPackTrue()
                .stream()
                .map(StockResponse::from)
                .toList();
    }

    public List<DailyPriceResponse> getPrices(Long stockId, LocalDate from, LocalDate to) {
        if (!stockRepository.existsById(stockId)) {
            throw new EntityNotFoundException(ErrorCode.STOCK_NOT_FOUND);
        }
        return dailyPriceRepository
                .findByStockIdAndTradeDateBetweenOrderByTradeDateDesc(stockId, from, to)
                .stream()
                .map(DailyPriceResponse::from)
                .toList();
    }

    public List<SectorResponse> getSectors() {
        return sectorRepository.findAll()
                .stream()
                .map(SectorResponse::from)
                .toList();
    }

    public List<StockResponse> getStocksBySector(Long sectorId) {
        if (!sectorRepository.existsById(sectorId)) {
            throw new EntityNotFoundException(ErrorCode.SECTOR_NOT_FOUND);
        }
        return stockRepository.findBySectorId(sectorId)
                .stream()
                .map(StockResponse::from)
                .toList();
    }
}
