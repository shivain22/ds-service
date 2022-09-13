package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.UploadRejectReason;
import com.ainnotate.aidas.domain.UploadRejectReasonMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
@Repository
@Transactional
public interface UploadRejectReasonMappingRepository extends JpaRepository<UploadRejectReasonMapping, Long> {

    @Query(value="select * from upload_reject_reason_mapping where upload_id=?1 and upload_reject_reason_id=?2", nativeQuery = true)
    UploadRejectReasonMapping getUploadRejectReasonMapping(Long uploadId, Long uploadRejectReasonId);
}
