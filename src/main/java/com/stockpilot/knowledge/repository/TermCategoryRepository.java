package com.stockpilot.knowledge.repository;

import com.stockpilot.knowledge.entity.TermCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TermCategoryRepository extends JpaRepository<TermCategory, Long> {

    Optional<TermCategory> findByCode(String code);

    List<TermCategory> findAllByOrderByDisplayOrderAsc();
}
