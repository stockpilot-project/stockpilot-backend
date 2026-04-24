package com.stockpilot.knowledge.service;

import com.stockpilot.global.error.exception.BusinessException;
import com.stockpilot.global.error.exception.EntityNotFoundException;
import com.stockpilot.global.error.exception.ErrorCode;
import com.stockpilot.knowledge.dto.InvestmentTermCreateRequest;
import com.stockpilot.knowledge.dto.InvestmentTermDetailResponse;
import com.stockpilot.knowledge.dto.InvestmentTermSummaryResponse;
import com.stockpilot.knowledge.dto.InvestmentTermUpdateRequest;
import com.stockpilot.knowledge.dto.TermCategoryResponse;
import com.stockpilot.knowledge.entity.Difficulty;
import com.stockpilot.knowledge.entity.InvestmentTerm;
import com.stockpilot.knowledge.entity.InvestmentTermRelation;
import com.stockpilot.knowledge.entity.TermCategory;
import com.stockpilot.knowledge.repository.InvestmentTermRelationRepository;
import com.stockpilot.knowledge.repository.InvestmentTermRepository;
import com.stockpilot.knowledge.repository.TermCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvestmentTermService {

    private final InvestmentTermRepository termRepository;
    private final InvestmentTermRelationRepository relationRepository;
    private final TermCategoryRepository categoryRepository;

    public Page<InvestmentTermSummaryResponse> getTerms(
            String categoryCode,
            Difficulty difficulty,
            String q,
            Pageable pageable
    ) {
        String normalizedQ = (q == null || q.isBlank()) ? null : q.trim();
        String normalizedCategory = (categoryCode == null || categoryCode.isBlank()) ? null : categoryCode;

        return termRepository
                .search(normalizedCategory, difficulty, normalizedQ, pageable)
                .map(InvestmentTermSummaryResponse::from);
    }

    public InvestmentTermDetailResponse getTerm(Long id) {
        InvestmentTerm term = termRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INVESTMENT_TERM_NOT_FOUND));
        List<InvestmentTerm> related = relationRepository.findRelatedTerms(id);
        return InvestmentTermDetailResponse.of(term, related);
    }

    public List<InvestmentTermSummaryResponse> autocomplete(String q, int limit) {
        if (q == null || q.isBlank()) return List.of();
        int size = Math.max(1, Math.min(limit, 20));
        Pageable pageable = PageRequest.of(0, size, Sort.by("name").ascending());
        return termRepository.autocomplete(q.trim(), pageable)
                .stream()
                .map(InvestmentTermSummaryResponse::from)
                .toList();
    }

    @Cacheable(value = "termCategories")
    public List<TermCategoryResponse> getCategories() {
        return categoryRepository.findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(category -> TermCategoryResponse.from(
                        category,
                        termRepository.countByCategoryId(category.getId())))
                .toList();
    }

    @Transactional
    @CacheEvict(value = "termCategories", allEntries = true)
    public InvestmentTermDetailResponse createTerm(InvestmentTermCreateRequest request) {
        if (termRepository.existsByName(request.getName())) {
            throw new BusinessException(ErrorCode.INVESTMENT_TERM_DUPLICATE);
        }

        TermCategory category = categoryRepository.findByCode(request.getCategoryCode())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TERM_CATEGORY_NOT_FOUND));

        InvestmentTerm term = InvestmentTerm.builder()
                .category(category)
                .name(request.getName())
                .fullName(request.getFullName())
                .summary(request.getSummary())
                .description(request.getDescription())
                .formula(request.getFormula())
                .example(request.getExample())
                .difficulty(request.getDifficulty())
                .build();
        termRepository.save(term);

        applyRelations(term, request.getRelatedTermNames());

        List<InvestmentTerm> related = relationRepository.findRelatedTerms(term.getId());
        return InvestmentTermDetailResponse.of(term, related);
    }

    @Transactional
    @CacheEvict(value = "termCategories", allEntries = true)
    public InvestmentTermDetailResponse updateTerm(Long id, InvestmentTermUpdateRequest request) {
        InvestmentTerm term = termRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INVESTMENT_TERM_NOT_FOUND));

        TermCategory category = null;
        if (request.getCategoryCode() != null) {
            category = categoryRepository.findByCode(request.getCategoryCode())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TERM_CATEGORY_NOT_FOUND));
        }

        if (request.getName() != null && !request.getName().equals(term.getName())
                && termRepository.existsByName(request.getName())) {
            throw new BusinessException(ErrorCode.INVESTMENT_TERM_DUPLICATE);
        }

        term.update(
                category,
                request.getName(),
                request.getFullName(),
                request.getSummary(),
                request.getDescription(),
                request.getFormula(),
                request.getExample(),
                request.getDifficulty()
        );

        if (request.getRelatedTermNames() != null) {
            term.getRelations().clear();
            termRepository.flush();
            applyRelations(term, request.getRelatedTermNames());
        }

        List<InvestmentTerm> related = relationRepository.findRelatedTerms(term.getId());
        return InvestmentTermDetailResponse.of(term, related);
    }

    @Transactional
    @CacheEvict(value = "termCategories", allEntries = true)
    public void deleteTerm(Long id) {
        InvestmentTerm term = termRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INVESTMENT_TERM_NOT_FOUND));
        termRepository.delete(term);
    }

    private void applyRelations(InvestmentTerm term, List<String> relatedNames) {
        if (relatedNames == null || relatedNames.isEmpty()) return;
        for (String relatedName : relatedNames) {
            if (relatedName == null || relatedName.isBlank()) continue;
            if (relatedName.equals(term.getName())) continue;

            termRepository.findByName(relatedName).ifPresent(related -> {
                if (!relationRepository.existsByTermIdAndRelatedTermId(term.getId(), related.getId())) {
                    relationRepository.save(InvestmentTermRelation.builder()
                            .term(term)
                            .relatedTerm(related)
                            .build());
                }
                if (!relationRepository.existsByTermIdAndRelatedTermId(related.getId(), term.getId())) {
                    relationRepository.save(InvestmentTermRelation.builder()
                            .term(related)
                            .relatedTerm(term)
                            .build());
                }
            });
        }
    }
}
