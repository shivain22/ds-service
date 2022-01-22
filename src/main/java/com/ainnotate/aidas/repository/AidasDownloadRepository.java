package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasDownload;
import com.ainnotate.aidas.domain.AidasObject;
import com.ainnotate.aidas.domain.AidasProject;
import com.ainnotate.aidas.domain.AidasUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the AidasUpload entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasDownloadRepository extends JpaRepository<AidasDownload, Long> {


}
