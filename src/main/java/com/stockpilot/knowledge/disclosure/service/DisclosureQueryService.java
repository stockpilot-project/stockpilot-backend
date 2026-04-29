package com.stockpilot.knowledge.disclosure.service;

import com.stockpilot.global.error.exception.EntityNotFoundException;
import com.stockpilot.global.error.exception.ErrorCode;
import com.stockpilot.knowledge.disclosure.dto.DisclosureDetailResponse;
import com.stockpilot.knowledge.disclosure.dto.DisclosureResponse;
import com.stockpilot.knowledge.disclosure.entity.Disclosure;
import com.stockpilot.knowledge.disclosure.entity.DisclosureType;
import com.stockpilot.knowledge.disclosure.repository.DisclosureRepository;
import com.stockpilot.stock.repository.StockRepository;
import com.stockpilot.watchlist.entity.WatchlistItem;
import com.stockpilot.watchlist.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DisclosureQueryService {

    private final DisclosureRepository disclosureRepository;
    private final StockRepository stockRepository;
    private final WatchlistRepository watchlistRepository;

    public Page<DisclosureResponse> search(DisclosureType type,
                                           LocalDate from,
                                           LocalDate to,
                                           String q,
                                           Pageable pageable) {
        LocalDateTime fromDt = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toDt = (to != null) ? to.atTime(23, 59, 59) : null;
        String normalized = (q == null || q.isBlank()) ? null : q.trim();
        return disclosureRepository.search(type, fromDt, toDt, normalized, pageable)
                .map(DisclosureResponse::from);
    }

    public DisclosureDetailResponse get(Long id) {
        Disclosure disclosure = disclosureRepository.findWithStockById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.DISCLOSURE_NOT_FOUND));
        return DisclosureDetailResponse.from(disclosure);
    }

    public Page<DisclosureResponse> findByStock(Long stockId, DisclosureType type, Pageable pageable) {
        if (!stockRepository.existsById(stockId)) {
            throw new EntityNotFoundException(ErrorCode.STOCK_NOT_FOUND);
        }
        return disclosureRepository.findByStockId(stockId, type, pageable)
                .map(DisclosureResponse::from);
    }

    public Page<DisclosureResponse> findByWatchlist(Long watchlistId, DisclosureType type, Pageable pageable) {
        var watchlist = watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.WATCHLIST_NOT_FOUND));

        List<Long> stockIds = watchlist.getItems().stream()
                .map(WatchlistItem::getStock)
                .map(stock -> stock.getId())
                .toList();
        if (stockIds.isEmpty()) {
            return Page.empty(pageable);
        }
        return disclosureRepository.findByStockIds(stockIds, type, pageable)
                .map(DisclosureResponse::from);
    }
}
