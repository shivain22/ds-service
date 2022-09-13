package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.UploadCustomerQcProjectMappingBatchInfo;
import com.ainnotate.aidas.dto.QcResultDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
@Repository
@Transactional
public interface UploadCustomerQcProjectMappingBatchInfoRepository extends JpaRepository<UploadCustomerQcProjectMappingBatchInfo, Long> {


    @Query(value="select count(*) from upload_cqpm_batch_info where customer_qc_project_mapping_id=?1 and batch_number=?2 and qc_status=2", nativeQuery = true)
    Integer getQcPendingCount(Long customerQcProjectMappingId, Integer batchNumber);

    @Query(value="select upload_id from upload_cqpm_batch_info where customer_qc_project_mapping_id=?1 and batch_number=?2 order by upload_id",nativeQuery = true)
    List<Long> getUploadIdByCustomerQcProjectMappingAndBatchNumber(Long customerQcProjectMappingId, Integer batchNumber);

    @Query(value="select ucbi.* from upload_cqpm_batch_info ucbi where customer_qc_project_mapping_id=?1 and batch_number=?2 and ucbi.upload_id=?3",nativeQuery = true)
    UploadCustomerQcProjectMappingBatchInfo getUploadIdByCustomerQcProjectMappingAndBatchNumber(Long customerQcProjectMappingId, Integer batchNumber,Long uploadId);

    @Query(nativeQuery = true)
    List<QcResultDTO> getQcLevelStatus(Long uploadId, Integer qcLevel);
}
