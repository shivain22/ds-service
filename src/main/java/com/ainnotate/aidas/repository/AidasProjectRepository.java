package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasProject;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AidasProject entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasProjectRepository extends JpaRepository<AidasProject, Long> {}
