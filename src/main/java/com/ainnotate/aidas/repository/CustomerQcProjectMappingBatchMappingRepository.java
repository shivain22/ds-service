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
    
    @Query(value="select cbm.id,count(ucbi.id) from cqpm_batch_mapping cbm,customer_qc_project_mapping cqpm,upload_cqpm_batch_info ucbi where ucbi.batch_number=cbm.id and cbm.cqpm_id=cqpm.id and cqpm.project_id=?1 and cqpm.qc_level=?2 and cbm.batch_completion_status=1 group by cbm.id",nativeQuery = true)
    List<Integer[]> getQcApprovedBatches(Long projectId,Integer qcLevel);
    
    @Query(value="select cbm.id as cbmId,sum(case when ucbi.qc_status=1 then 1 else 0 end) as approvedCount \n"
    		+ "from cqpm_batch_mapping cbm,customer_qc_project_mapping cqpm,upload_cqpm_batch_info ucbi  \n"
    		+ "where ucbi.batch_number=cbm.id  \n"
    		+ "and cbm.cqpm_id=cqpm.id \n"
    		+ "and cqpm.project_id=?1 \n"
    		+ "and cqpm.qc_level=?2 \n"
    		+ "and cbm.next_level_batch_number is  null \n"
    		+ "and cbm.previous_level_batch_number is null \n"
    		+ "and (cbm.batch_completion_status=1 or cbm.batch_completion_status=3) \n"
    		+ "group by cbm.id",nativeQuery = true)
    List<Integer[]> getQcMixedBatches(Long projectId,Integer qcLevel);
    
    
    @Query(value="select ucbi.upload_id from upload_cqpm_batch_info ucbi where batch_number = ?1",nativeQuery = true)
    List<Long> getApprovedUploadIds(Integer batchNumber);
    
    @Query(value="select uvmom.id as uvmom_id "
    		+ "from cqpm_batch_mapping cbm,customer_qc_project_mapping cqpm,upload_cqpm_batch_info ucbi,upload u, user_vendor_mapping_object_mapping uvmom "
    		+ "where ucbi.batch_number=cbm.id "
    		+ "and ucbi.upload_id=u.id "
    		+ "and u.user_vendor_mapping_object_mapping_id=uvmom.id "
    		+ "and cbm.cqpm_id=cqpm.id "
    		+ "and cqpm.project_id=?1 "
    		+ "and cqpm.qc_level=?2 "
    		+ "and (cbm.batch_completion_status=1 or cbm.batch_completion_status=3) "
    		+ "and cbm.id=?3 "
    		+ "group by cbm.id,uvmom.id "
    		+ "having sum(case when ucbi.qc_status=0 then 1 else 0 end)=0",nativeQuery = true)
    List<Long> getQcApprovedUvmomIds(Long projectId,Integer qcLevel,Integer batchNumber);
    
    
    @Query(value="select uvmom.id as uvmom_id "
    		+ "from cqpm_batch_mapping cbm,customer_qc_project_mapping cqpm,upload_cqpm_batch_info ucbi,upload u, user_vendor_mapping_object_mapping uvmom "
    		+ "where ucbi.batch_number=cbm.id "
    		+ "and ucbi.upload_id=u.id "
    		+ "and u.user_vendor_mapping_object_mapping_id=uvmom.id "
    		+ "and cbm.cqpm_id=cqpm.id "
    		+ "and cqpm.project_id=?1 "
    		+ "and cqpm.qc_level=?2 "
    		+ "and (cbm.batch_completion_status=1 or cbm.batch_completion_status=3) "
    		+ "and cbm.id=?3 "
    		+ "group by cbm.id,uvmom.id"
    		+ " having sum(case when ucbi.qc_status=0 then 1 else 0 end)>0",nativeQuery = true)
    List<Long> getQcRejectedUvmomIds(Long projectId,Integer qcLevel,Integer batchNumber);
    
    
    @Query(value="select cbm.id,count(ucbi.id) "
    		+ "from cqpm_batch_mapping cbm,customer_qc_project_mapping cqpm,upload_cqpm_batch_info ucbi "
    		+ "where ucbi.batch_number=cbm.id "
    		+ "and cbm.cqpm_id=cqpm.id "
    		+ "and cqpm.project_id=?1 "
    		+ "and cqpm.qc_level=?2 "
    		+ "and cbm.next_level_batch_number is null "
    		+ "and (cbm.batch_completion_status=1 or cbm.batch_completion_status=3) "
    		+ "group by cbm.id",nativeQuery = true)
    List<Integer[]> getQcMixedBatchesForLevelGreaterThanOne(Long projectId,Integer qcLevel);
    
    @Query(value="select cbm.id,count(ucbi.id) from cqpm_batch_mapping cbm,customer_qc_project_mapping cqpm,upload_cqpm_batch_info ucbi "
    		+ "where ucbi.batch_number=cbm.id "
    		+ "and cbm.cqpm_id=cqpm.id "
    		+ "and cqpm.project_id=?1 "
    		+ "and cqpm.qc_level=?2 "
    		+ "and cbm.next_level_batch_number is null "
    		+ "and cbm.previous_level_batch_number is not null "
    		+ "and (cbm.batch_completion_status=1 or cbm.batch_completion_status=3) "
    		+ "group by cbm.id order by cbm.id",nativeQuery = true)
    List<Integer[]> getQcMixedBatchesForLevelThree(Long projectId,Integer qcLevel);
    
    @Query(value="select cbm.id,count(ucbi.id) from cqpm_batch_mapping cbm,customer_qc_project_mapping cqpm,upload_cqpm_batch_info ucbi "
    		+ "where ucbi.batch_number=cbm.id "
    		+ "and cbm.cqpm_id=cqpm.id "
    		+ "and cqpm.project_id=?1 "
    		+ "and cqpm.qc_level=?2 "
    		+ "and cbm.next_level_batch_number is not null "
    		+ "and cbm.previous_level_batch_number is not null "
    		+ "and (cbm.batch_completion_status=1 or cbm.batch_completion_status=3) "
    		+ "group by cbm.id",nativeQuery = true)
    List<Integer[]> getQcMixedBatchesForLevelGreaterThanThree(Long projectId,Integer qcLevel);
    
    @Query(value="select cbm.id,uvmom.id,count(ucbi.id),count(case when ucbi.qc_status=1 then 1 else 0 end)as approved,"
    		+ "count(case when ucbi.qc_status=2 then 1 else 0 end)as pending,"
    		+ "count(case when ucbi.qc_status=0 then 1 else 0 end)as rejected"
    		+ " from cqpm_batch_mapping cbm,customer_qc_project_mapping cqpm,upload_cqpm_batch_info ucbi,upload u, user_vendor_mapping_object_mapping uvmom "
    		+ "where ucbi.batch_number=cbm.id "
    		+ "and ucbi.upload_id=u.id "
    		+ "and u.user_vendor_mapping_object_mapping_id=uvmom.id"
    		+ "and cbm.cqpm_id=cqpm.id "
    		+ "and cqpm.project_id=?1 "
    		+ "and cqpm.qc_level=?2 "
    		+ "and cbm.next_level_batch_number is not null "
    		+ "and cbm.previous_level_batch_number is not null "
    		+ "and (cbm.batch_completion_status=1 or cbm.batch_completion_status=3) "
    		+ "group by cbm.id",nativeQuery = true)
    List<Integer[]> getQcMixedBatchesForLevelGreaterThanTwoForGrouped(Long projectId,Integer qcLevel);
    
    @Query(value="select cbm.id from cqpm_batch_mapping cbm,customer_qc_project_mapping cqpm where cbm.cqpm_id=cqpm.id and cqpm.project_id=?1 and cqpm.qc_level=?2 and cbm.batch_completion_status=3 ",nativeQuery = true)
    List<Long> getQcRejectedBatches(Long projectId,Integer qcLevel);
    
    @Query(value="select cbm.id,\n"
    		+ "count(case when ucbi.qc_status=1 then 1 end )as approved, \n"
    		+ "count(case when ucbi.qc_status=0 then 1 end)as rejected,\n"
    		+ "count(case when ucbi.qc_status=2 then 2 end)as pending \n"
    		+ "from \n"
    		+ "cqpm_batch_mapping cbm,\n"
    		+ "customer_qc_project_mapping cqpm,\n"
    		+ "upload_cqpm_batch_info ucbi \n"
    		+ "where \n"
    		+ "ucbi.batch_number=cbm.id and \n"
    		+ "cqpm.id=?3 and \n"
    		+ "cbm.cqpm_id=cqpm.id and \n"
    		+ "cqpm.project_id=?1 and \n"
    		+ "cqpm.qc_level=?2 and \n"
    		+ "cbm.batch_completion_status=2 group by cbm.id",nativeQuery = true)
    List<Long[]> getQcNotCompletedBatches(Long projectId,Integer qcLevel,Long cqpmId);
}
