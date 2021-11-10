package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasObject;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AidasObject entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasObjectRepository extends JpaRepository<AidasObject, Long> {}
