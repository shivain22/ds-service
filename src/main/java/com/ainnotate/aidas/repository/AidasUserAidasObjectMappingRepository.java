package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasUserAidasObjectMapping;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AidasUserAidasObjectMapping entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasUserAidasObjectMappingRepository extends JpaRepository<AidasUserAidasObjectMapping, Long> {}
