package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.UserJsonStorage;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data SQL repository for the AidasUserJsonStorage entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface UserJsonStorageRepository extends JpaRepository<UserJsonStorage, Long> {}
