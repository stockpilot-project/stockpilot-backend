package com.stockpilot.stock.repository;

import com.stockpilot.stock.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectorRepository extends JpaRepository<Sector, Long> {
}
