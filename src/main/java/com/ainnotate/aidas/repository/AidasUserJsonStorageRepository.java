package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasUserJsonStorage;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AidasUserJsonStorage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasUserJsonStorageRepository extends JpaRepository<AidasUserJsonStorage, Long> {}
