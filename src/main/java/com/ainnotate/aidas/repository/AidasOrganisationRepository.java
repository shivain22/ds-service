package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasOrganisation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AidasOrganisation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasOrganisationRepository extends JpaRepository<AidasOrganisation, Long> {}
