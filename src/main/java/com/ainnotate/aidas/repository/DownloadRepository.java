package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.Download;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data SQL repository for the AidasUpload entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface DownloadRepository extends JpaRepository<Download, Long> {


}
