package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.UploadMetaData;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AidasUploadMetaData entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UploadMetaDataRepository extends JpaRepository<UploadMetaData, Long> {}
