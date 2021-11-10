package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasObjectProperty;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AidasObjectProperty entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasObjectPropertyRepository extends JpaRepository<AidasObjectProperty, Long> {}
