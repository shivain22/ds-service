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


    @Query(value="select count(*) from upload_cqpm_batch_info ucbi, customer_qc_project_mapping cqpm  where ucbi.customer_qc_project_mapping_id=?1 and ucbi.batch_number=?2 and ucbi.qc_status=2 and cqpm.qc_level=?3", nativeQuery = true)
    Integer getQcPendingCount(Long customerQcProjectMappingId, Integer batchNumber, Integer qcLevel);

    @Query(value="select count(*) from upload_cqpm_batch_info ucbi, customer_qc_project_mapping cqpm where ucbi.customer_qc_project_mapping_id=cqpm.id and cqpm.project_id=?1 and cqpm.id=?1 and qc_status=2", nativeQuery = true)
    Integer getQcPendingInAllCount(Long projectId,Long customerQcProjectMappingId);

    @Query(value="select upload_id from upload_cqpm_batch_info where customer_qc_project_mapping_id=?1 and batch_number=?2 order by upload_id",nativeQuery = true)
    List<Long> getUploadIdByCustomerQcProjectMappingAndBatchNumber(Long customerQcProjectMappingId, Integer batchNumber);

    @Query(value="select ucbi.* from upload_cqpm_batch_info ucbi where customer_qc_project_mapping_id=?1 and batch_number=?2 and ucbi.upload_id=?3",nativeQuery = true)
    UploadCustomerQcProjectMappingBatchInfo getUploadIdByCustomerQcProjectMappingAndBatchNumber(Long customerQcProjectMappingId, Integer batchNumber,Long uploadId);

    @Query(nativeQuery = true)
    List<QcResultDTO> getQcLevelStatus(Long uploadId, Integer qcLevel);

    @Query(value="select max(batch_number) from upload_cqpm_batch_info where customer_qc_project_mapping_id=?1",nativeQuery = true)
    Integer getMaxBatchNoForQcLevel(Long customerQcProjectMappingId);

    @Query(value="select batch_number from (\n" +
        "select min(batch_number) batch_number,count(ucbi.qc_status=2 or null) as pending,count(ucbi.qc_status=1 or null)as approved,count(ucbi.qc_status=0 or null)as rejected  from \n" +
        "upload_cqpm_batch_info ucbi\n" +
        "where \n" +
        "ucbi.id=1 group by ucbi.batch_number)a where cast(a.pending as signed) >0",nativeQuery = true)
    Integer getMinPendingBatchNoForQcLevel(Long projectId, Long customerQcProjectMappingId, Integer qcLevel);

    @Query(value="select max(batch_number) from upload_cqpm_batch_info where customer_qc_project_mapping_id=?1 and qc_status=2",nativeQuery = true)
    Integer getMinBatchNoPendingForQcLevel(Long customerQcProjectMappingId);

    @Query(value="select id from upload_cqpm_batch_info ucbi, customer_qc_project_mapping cqpm where ucbi.customer_qc_project_mapping_id=cqpm.id and ucbi.qc_status=2 and cqpm.project_id=?1 and cqpm.qc_level=?2",nativeQuery = true)
    List<Long> getAllPendingBatchInfoForProjectAndLevel(Long projectId, Integer level);

    @Query(value="select batch_number from upload_cqpm_batch_info ucbi, customer_qc_project_mapping cqpm where ucbi.customer_qc_project_mapping_id=cqpm.id and cqpm.id in (?2) and cqpm.project_id=?1 and ucbi.qc_status=2 group by ucbi.batch_number ",nativeQuery = true)
    List<Long> getAllPendingBatchNumbersForProjectAndLevel(Long projectId, List<Long> cqpmIds);
}
