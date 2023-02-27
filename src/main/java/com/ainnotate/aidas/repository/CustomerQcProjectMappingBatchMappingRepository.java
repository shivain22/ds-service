package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.CustomerQcProjectMapping;
import com.ainnotate.aidas.domain.CustomerQcProjectMappingBatchMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link CustomerQcProjectMapping} entity.
 */
@Repository
@Transactional
public interface CustomerQcProjectMappingBatchMappingRepository extends JpaRepository<CustomerQcProjectMappingBatchMapping, Long> {

    @Query(value="select * from cqpm_batch_mapping cbm where cbm.cqpm_id = ?1 and cbm.batch_no = ?2 and batch_completion_status=2",nativeQuery = true)
    CustomerQcProjectMappingBatchMapping findByCustomerQcProjectMappingIdAndBatchNumber(Long customerQcProjectMappingId, Integer batchNumber);

    @Query(value="select * from cqpm_batch_mapping cbm where cbm.cqpm_id = ?1 and cbm.batch_no = ?2 and cbm.batch_completion_status=1",nativeQuery = true)
    CustomerQcProjectMappingBatchMapping findByCustomerQcProjectMappingIdAndBatchNumberPending(Long customerQcProjectMappingId, Integer batchNumber);

    @Query(value = "select * from cqpm_batch_mapping cbm,customer_qc_project_mapping cqpm where cbm.cqpm_id = cqpm.id and cqpm.project_id=?1 and cqpm.qc_level=?2 and cbm.batch_completion_status=1 and cbm.previous_level_batch_number is null and cbm.next_level_batch_number is  null",nativeQuery = true)
    List<CustomerQcProjectMappingBatchMapping> getAllCompletedBatchNumberForQcLevel(Long projectId, Integer qcLevel);

    @Query(value = "select * from cqpm_batch_mapping cbm,customer_qc_project_mapping cqpm where cbm.cqpm_id = cqpm.id and cqpm.project_id=?1 and cqpm.qc_level=?2 and (cbm.batch_completion_status=1 or cbm.batch_completion_status=3) and cbm.previous_level_batch_number is null and cbm.next_level_batch_number is  null",nativeQuery = true)
    List<CustomerQcProjectMappingBatchMapping> getAllCompletedBatchNumberForQcLevelNonGrouped(Long projectId, Integer qcLevel);

    @Query(value = "select * from cqpm_batch_mapping cbm,customer_qc_project_mapping cqpm where cbm.cqpm_id = cqpm.id and cqpm.project_id=?1 and cqpm.qc_level=?2 and (cbm.batch_completion_status=1 or cbm.batch_completion_status=3) and cbm.previous_level_batch_number is not null and cbm.next_level_batch_number is  null",nativeQuery = true)
    List<CustomerQcProjectMappingBatchMapping> getAllCompletedBatchNumberForQcLevelNonGroupedGreaterThanLevel2(Long projectId, Integer qcLevel);

    @Query(value = "select * from cqpm_batch_mapping cbm,customer_qc_project_mapping cqpm where cbm.cqpm_id = cqpm.id and cqpm.project_id=?1 and cqpm.qc_level=?2 and cbm.batch_completion_status=1 and cbm.next_level_batch_number is null and cbm.previous_level_batch_number is not null",nativeQuery = true)
    List<CustomerQcProjectMappingBatchMapping> getAllCompletedBatchNumberForQcLevelGreaterThan1(Long projectId, Integer qcLevel);

    @Query(value = "select * from cqpm_batch_mapping cbm,customer_qc_project_mapping cqpm where cbm.cqpm_id = cqpm.id and cqpm.project_id=?1 and cqpm.qc_level=?2 and (cbm.batch_completion_status=1 or cbm.batch_completion_status=3) and cbm.next_level_batch_number is null and cbm.previous_level_batch_number is not null",nativeQuery = true)
    List<CustomerQcProjectMappingBatchMapping> getAllCompletedBatchNumberForQcLevelGreaterThan1NonGrouped(Long projectId, Integer qcLevel);
    
    @Query(value="select cbm.id from cqpm_batch_mapping cbm,customer_qc_project_mapping cqpm where cbm.cqpm_id=cqpm.id and cqpm.project_id=?1 and cqpm.qc_level=?2 and cbm.batch_completion_status=1 ",nativeQuery = true)
    List<Long> getQcApprovedBatches(Long projectId,Integer qcLevel);
    
    @Query(value="select cbm.id from cqpm_batch_mapping cbm,customer_qc_project_mapping cqpm where cbm.cqpm_id=cqpm.id and cqpm.project_id=?1 and cqpm.qc_level=?2 and (cbm.batch_completion_status=1 or cbm.batch_completion_status=3) ",nativeQuery = true)
    List<Long> getQcMixedBatches(Long projectId,Integer qcLevel);
    
    @Query(value="select cbm.id from cqpm_batch_mapping cbm,customer_qc_project_mapping cqpm where cbm.cqpm_id=cqpm.id and cqpm.project_id=?1 and cqpm.qc_level=?2 and cbm.batch_completion_status=3 ",nativeQuery = true)
    List<Long> getQcRejectedBatches(Long projectId,Integer qcLevel);
    
    @Query(value="select cqpm.id from cqpm_batch_mapping cbm,customer_qc_project_mapping cqpm where cbm.cqpm_id=cqpm.id and cqpm.project_id=?1 and cqpm.qc_level=?2 and cbm.batch_completion_status=2",nativeQuery = true)
    List<Long> getQcNotCompletedBatches(Long projectId,Integer qcLevel);
}
