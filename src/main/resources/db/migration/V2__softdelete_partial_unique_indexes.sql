-- =====================================================================
-- V2: Soft Delete 도입에 따른 unique 제약 → 부분 unique 인덱스 변환
--
-- @SoftDelete 적용 후, 같은 자연키(name/code/receipt_no 등)로 등록 → 삭제 →
-- 재등록 시 unique 제약이 deleted 행과 충돌해 INSERT가 실패하는 문제를 방지.
-- WHERE deleted_at IS NULL 부분 인덱스로 활성 행만 unique 보장.
-- =====================================================================


-- sectors.name
ALTER TABLE sectors DROP CONSTRAINT IF EXISTS uk_sectors_name;
CREATE UNIQUE INDEX IF NOT EXISTS uk_sectors_name_active
    ON sectors (name) WHERE deleted_at IS NULL;

-- term_categories.code
ALTER TABLE term_categories DROP CONSTRAINT IF EXISTS uk_term_categories_code;
CREATE UNIQUE INDEX IF NOT EXISTS uk_term_categories_code_active
    ON term_categories (code) WHERE deleted_at IS NULL;

-- investment_terms.name
ALTER TABLE investment_terms DROP CONSTRAINT IF EXISTS uk_investment_terms_name;
CREATE UNIQUE INDEX IF NOT EXISTS uk_investment_terms_name_active
    ON investment_terms (name) WHERE deleted_at IS NULL;

-- investment_term_relations(term_id, related_term_id)
ALTER TABLE investment_term_relations DROP CONSTRAINT IF EXISTS uk_term_relation_pair;
CREATE UNIQUE INDEX IF NOT EXISTS uk_term_relation_pair_active
    ON investment_term_relations (term_id, related_term_id) WHERE deleted_at IS NULL;

-- economic_indicators.code
ALTER TABLE economic_indicators DROP CONSTRAINT IF EXISTS uk_economic_indicators_code;
CREATE UNIQUE INDEX IF NOT EXISTS uk_economic_indicators_code_active
    ON economic_indicators (code) WHERE deleted_at IS NULL;

-- indicator_observations(indicator_id, observed_at)
ALTER TABLE indicator_observations DROP CONSTRAINT IF EXISTS uk_indicator_observation;
CREATE UNIQUE INDEX IF NOT EXISTS uk_indicator_observation_active
    ON indicator_observations (indicator_id, observed_at) WHERE deleted_at IS NULL;

-- dart_corps.corp_code
ALTER TABLE dart_corps DROP CONSTRAINT IF EXISTS uk_dart_corps_corp_code;
CREATE UNIQUE INDEX IF NOT EXISTS uk_dart_corps_corp_code_active
    ON dart_corps (corp_code) WHERE deleted_at IS NULL;

-- disclosures.receipt_no
ALTER TABLE disclosures DROP CONSTRAINT IF EXISTS uk_disclosures_receipt_no;
CREATE UNIQUE INDEX IF NOT EXISTS uk_disclosures_receipt_no_active
    ON disclosures (receipt_no) WHERE deleted_at IS NULL;
