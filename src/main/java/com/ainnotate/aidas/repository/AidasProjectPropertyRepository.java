package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasProjectProperty;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AidasProjectProperty entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasProjectPropertyRepository extends JpaRepository<AidasProjectProperty, Long> {}
