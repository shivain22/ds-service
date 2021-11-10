package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasUpload;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AidasUpload entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasUploadRepository extends JpaRepository<AidasUpload, Long> {}
