package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.UserJsonStorage;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AidasUserJsonStorage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserJsonStorageRepository extends JpaRepository<UserJsonStorage, Long> {}
