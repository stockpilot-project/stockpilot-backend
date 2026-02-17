package com.stockpilot.stock.repository;

import com.stockpilot.config.TestcontainersConfig;
import com.stockpilot.stock.entity.Sector;
import com.stockpilot.stock.entity.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestcontainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private SectorRepository sectorRepository;

    private Sector tech;

    @BeforeEach
    void setUp() {
        stockRepository.deleteAll();
        sectorRepository.deleteAll();

        tech = sectorRepository.save(Sector.builder().name("Technology").nameKr("기술").build());

        stockRepository.save(Stock.builder()
                .symbol("AAPL").name("Apple Inc.").nameKr("애플")
                .market("US").sector(tech).starterPack(true).build());
        stockRepository.save(Stock.builder()
                .symbol("005930").name("Samsung Electronics").nameKr("삼성전자")
                .market("KR").sector(tech).starterPack(true).build());
        stockRepository.save(Stock.builder()
                .symbol("MSFT").name("Microsoft Corporation").nameKr("마이크로소프트")
                .market("US").sector(tech).starterPack(false).build());
    }

    @Test
    @DisplayName("마켓별 종목을 조회한다")
    void findByMarket() {
        List<Stock> usStocks = stockRepository.findByMarket("US");
        assertThat(usStocks).hasSize(2);
    }

    @Test
    @DisplayName("섹터별 종목을 조회한다")
    void findBySectorId() {
        List<Stock> stocks = stockRepository.findBySectorId(tech.getId());
        assertThat(stocks).hasSize(3);
    }

    @Test
    @DisplayName("초보 추천 종목을 조회한다")
    void findByStarterPackTrue() {
        List<Stock> starters = stockRepository.findByStarterPackTrue();
        assertThat(starters).hasSize(2);
    }

    @Test
    @DisplayName("종목을 검색한다")
    void searchStocks() {
        List<Stock> result = stockRepository
                .findByNameContainingIgnoreCaseOrNameKrContainingIgnoreCaseOrSymbolContainingIgnoreCase(
                        "apple", "apple", "apple");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSymbol()).isEqualTo("AAPL");
    }

    @Test
    @DisplayName("한글 종목명으로 검색한다")
    void searchStocks_korean() {
        List<Stock> result = stockRepository
                .findByNameContainingIgnoreCaseOrNameKrContainingIgnoreCaseOrSymbolContainingIgnoreCase(
                        "삼성", "삼성", "삼성");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSymbol()).isEqualTo("005930");
    }
}
