package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasUploadMetaData;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AidasUploadMetaData entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasUploadMetaDataRepository extends JpaRepository<AidasUploadMetaData, Long> {}
