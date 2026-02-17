package com.stockpilot.stock.repository;

import com.stockpilot.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {

    List<Stock> findByMarket(String market);

    List<Stock> findBySectorId(Long sectorId);

    List<Stock> findByStarterPackTrue();

    List<Stock> findByNameContainingIgnoreCaseOrNameKrContainingIgnoreCaseOrSymbolContainingIgnoreCase(
            String name, String nameKr, String symbol);
}
