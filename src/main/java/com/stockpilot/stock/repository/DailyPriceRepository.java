package com.stockpilot.stock.repository;

import com.stockpilot.stock.entity.DailyPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyPriceRepository extends JpaRepository<DailyPrice, Long> {

    List<DailyPrice> findByStockIdAndTradeDateBetweenOrderByTradeDateDesc(
            Long stockId, LocalDate from, LocalDate to);

    Optional<DailyPrice> findFirstByStockIdOrderByTradeDateDesc(Long stockId);

    List<DailyPrice> findByTradeDateOrderByChangeRateDesc(LocalDate tradeDate);

    List<DailyPrice> findByTradeDateOrderByChangeRateAsc(LocalDate tradeDate);

    List<DailyPrice> findByTradeDateOrderByVolumeDesc(LocalDate tradeDate);
}
