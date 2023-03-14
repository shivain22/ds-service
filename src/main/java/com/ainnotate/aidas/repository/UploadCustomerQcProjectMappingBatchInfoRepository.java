package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.Upload;
import com.ainnotate.aidas.domain.UploadCustomerQcProjectMappingBatchInfo;
import com.ainnotate.aidas.dto.QcResultDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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


    @Query(value="select ucbi.upload_id from upload_cqpm_batch_info ucbi where ucbi.customer_qc_project_mapping_id=?1 and ucbi.batch_number=?2 and ucbi.qc_status=2", nativeQuery = true)
    List<Long> getQcPendingCount(Long customerQcProjectMappingId, Integer batchNumber);

    @Query(value="select ucbi.batch_number from upload_cqpm_batch_info ucbi, customer_qc_project_mapping cqpm where ucbi.customer_qc_project_mapping_id=cqpm.id and cqpm.project_id=?1 and cqpm.id=?2 and ucbi.qc_status=2", nativeQuery = true)
    List<Integer> getQcPendingInAllCount(Long projectId,Long customerQcProjectMappingId);

    @Query(value="select distinct uvmom.object_id from upload_cqpm_batch_info ucbi, upload u, user_vendor_mapping_object_mapping uvmom, customer_qc_project_mapping cqpm where ucbi.upload_id=u.id and u.user_vendor_mapping_object_mapping_id=uvmom.id and ucbi.customer_qc_project_mapping_id=cqpm.id and cqpm.project_id=?1 and cqpm.id=?2 and ucbi.qc_status=2 and ucbi.batch_number in (?3)", nativeQuery = true)
    List<Long> getQcPendingObjectInAllCount(Long projectId,Long customerQcProjectMappingId,List<Integer> batchNumber);

    @Query(value="select upload_id from upload_cqpm_batch_info where customer_qc_project_mapping_id=?1 and batch_number=?2 order by upload_id",nativeQuery = true)
    List<Long> getUploadIdByCustomerQcProjectMappingAndBatchNumber(Long customerQcProjectMappingId, Integer batchNumber);

    @Query(value="select ucbi.* from upload_cqpm_batch_info ucbi where customer_qc_project_mapping_id=?1 and batch_number=?2 and ucbi.upload_id=?3",nativeQuery = true)
    UploadCustomerQcProjectMappingBatchInfo getUploadIdByCustomerQcProjectMappingAndBatchNumber(Long customerQcProjectMappingId, Integer batchNumber,Long uploadId);
    
    @Query(value="select ucbi.* from upload_cqpm_batch_info ucbi where  batch_number=?1 and ucbi.upload_id=?2",nativeQuery = true)
    UploadCustomerQcProjectMappingBatchInfo getUploadIdByBatchNumber(Integer batchNumber,Long uploadId);

    @Query(value="select ucbi.upload_id from upload_cqpm_batch_info ucbi where customer_qc_project_mapping_id=?1 and batch_number=?2 and ucbi.qc_status=2",nativeQuery = true)
    List<Long> getUploadsPendingByQcLevelGreaterThan1AndShowToQcIs1(Long customerQcProjectMappingId, Integer batchNumber);

    @Query(value="select ucbi.upload_id from upload_cqpm_batch_info ucbi where customer_qc_project_mapping_id=?1 and batch_number=?2 and ucbi.qc_status=1",nativeQuery = true)
    List<Long> getUploadsApprovedByQcLevelGreaterThan1AndShowToQcIs1(Long customerQcProjectMappingId, Integer batchNumber);

    @Query(value="select ucbi.upload_id from upload_cqpm_batch_info ucbi where ucbi.customer_qc_project_mapping_id=?1 and ucbi.batch_number=?2 and ucbi.qc_status=1",nativeQuery = true)
    List<Long> getAllApprovedInBatch(Long customerQcProjectMappingId, Integer batchNumber);
    
    
    @Query(value="select count(ucbi.upload_id) \n"
    		+ "from upload_cqpm_batch_info ucbi \n"
    		+ "where ucbi.customer_qc_project_mapping_id=?1 \n"
    		+ "and ucbi.batch_number=?2 \n"
    		+ "and ucbi.qc_status=2 \n"
    		+ "and show_to_qc=1",nativeQuery = true)
    Integer getAllShowToQcAndPending(Long customerQcProjectMappingId, Integer batchNumber);
    
	/*
	 * @Query(value="select count(ucbi.id) \n" +
	 * "from upload_cqpm_batch_info ucbi \n" + "where " +
	 * "ucbi.customer_qc_project_mapping_id=?1 \n" + "and ucbi.batch_number=?2 \n" +
	 * "and ucbi.qc_status=2 \n" + "and ucbi.show_to_qc=1",nativeQuery = true)
	 * Integer getAllShowToQcAndPendingForGroupedProject(Long
	 * customerQcProjectMappingId, Integer batchNumber,Long uvmomId);
	 */
    
    @Query(value="select ucbi.upload_id,ucbi.id from upload_cqpm_batch_info ucbi where ucbi.customer_qc_project_mapping_id=?1 and ucbi.batch_number=?2 and ucbi.qc_status=2 and show_to_qc=0",nativeQuery = true)
    List<Long[]> getAllNotShowToQcAndPending(Long customerQcProjectMappingId, Integer batchNumber);
    
    @Query(value="select ucbi.upload_id,ucbi.id "
    		+ "from upload_cqpm_batch_info ucbi, "
    		+ "upload u, \n"
    		+ "user_vendor_mapping_object_mapping uvmom \n"
    		+ "where "
    		+ "ucbi.upload_id=u.id \n"
    		+ "and u.user_vendor_mapping_object_mapping_id=uvmom.id \n"
    		+ "and ucbi.customer_qc_project_mapping_id=?1 "
    		+ "and ucbi.batch_number=?2 "
    		+ "and ucbi.qc_status=2 "
    		+ "and ucbi.show_to_qc=0 ",nativeQuery = true)
    List<Long[]> getAllNotShowToQcAndPendingForGroupedProject(Long customerQcProjectMappingId, Integer batchNumber,Long uvmomId);

    @Query(value="select ucbi.upload_id from upload_cqpm_batch_info ucbi where ucbi.customer_qc_project_mapping_id=?1 and ucbi.batch_number=?2",nativeQuery = true)
    List<Long> getAllInBatch(Long customerQcProjectMappingId, Integer batchNumber);

    @Query(value="select u.id from upload_cqpm_batch_info ucbi,upload u,user_vendor_mapping_object_mapping uvmom  where ucbi.customer_qc_project_mapping_id=?1 and u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=?2 and ucbi.batch_number=?3 and ucbi.qc_status=2 and ucbi.upload_id=u.id",nativeQuery = true)
    List<Long> getAllInBatchGrouped(Long customerQcProjectMappingId,Long objectId, Integer batchNumber);


    @Query(value="select u.id "
    		+ "from upload_cqpm_batch_info ucbi,upload u"
    		+ " where ucbi.customer_qc_project_mapping_id=?1 "
    		+ "and ucbi.batch_number=?2 "
    		+ "and (ucbi.qc_status=2)  "
    		+ "and ucbi.upload_id=u.id",nativeQuery = true)
    List<Long> getRemainingUploadsInBatchIncludingCurrentUpload(Long customerQcProjectMappingId, Integer batchNumber);
    
    @Query(value="select u.id "
    		+ "from upload_cqpm_batch_info ucbi,upload u"
    		+ " where ucbi.customer_qc_project_mapping_id=?1 "
    		+ "and ucbi.batch_number=?2 "
    		+ "and (ucbi.qc_status=2 or ucbi.qc_status=1)  "
    		+ "and ucbi.upload_id=u.id",nativeQuery = true)
    List<Long> getRemainingUploadsInBatchIncludingCurrentUploadAndPreviouslyApproved(Long customerQcProjectMappingId, Integer batchNumber);
    
    @Query(value="select count(*) from upload_cqpm_batch_info ucbi where ucbi.batch_number=?2 and ucbi.customer_qc_project_mapping_id=?1 and ucbi.qc_status=0",nativeQuery = true)
    Integer getRejectedCount(Long customerQcProjectMappingId, Integer batchNumber);
    
    @Query(value="select count(*) from upload_cqpm_batch_info ucbi where ucbi.batch_number=?2 and ucbi.customer_qc_project_mapping_id=?1 and ucbi.qc_status=1",nativeQuery = true)
    Integer getApprovedCount(Long customerQcProjectMappingId, Integer batchNumber);

    @Query(value="select u.id from upload_cqpm_batch_info ucbi,upload u where ucbi.customer_qc_project_mapping_id=?1 and ucbi.batch_number=?2 and ucbi.qc_status=2 and ucbi.upload_id=u.id and ucbi.upload_id not in (?3)",nativeQuery = true)
    List<Long> getRemainingUploadsInBatchIncludingCurrentUploadNew(Long customerQcProjectMappingId, Integer batchNumber,Long currentUploadId);

    @Query(value="select u.id from upload_cqpm_batch_info ucbi,upload u,user_vendor_mapping_object_mapping uvmom  where ucbi.customer_qc_project_mapping_id=?1 and u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=?2 and ucbi.batch_number=?2 and ucbi.qc_status=2 and ucbi.upload_id=u.id",nativeQuery = true)
    List<Long> getRemainingUploadsInBatchIncludingCurrentUploadGrouped(Long customerQcProjectMappingId, Long objectId,Integer batchNumber);

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

    @Query(value="select ucbi.upload_id from upload_cqpm_batch_info ucbi, customer_qc_project_mapping cqpm where ucbi.customer_qc_project_mapping_id=cqpm.id and ucbi.qc_status=2 and cqpm.project_id=?1 and cqpm.qc_level=?2 and cqpm.id <> ?3",nativeQuery = true)
    List<Long> getAllPendingBatchInfoForProjectAndLevelAndOtherThanCqpmId(Long projectId, Integer level,Long customerQcProjectMappingId);

    @Query(value="select ucbi.upload_id from upload_cqpm_batch_info ucbi, customer_qc_project_mapping cqpm where ucbi.customer_qc_project_mapping_id=cqpm.id and ucbi.qc_status=2 and cqpm.project_id=?1 and cqpm.qc_level=?2 and cqpm.id=?3 and batch_number=?4",nativeQuery = true)
    List<Long> getAllPendingBatchInfoForProjectAndLevelForLoggedInUserLevel1(Long projectId, Integer level,Long customerQcProjectMappingId, Integer batchNumber);

    @Query(value="select ucbi.upload_id from upload_cqpm_batch_info ucbi, customer_qc_project_mapping cqpm where ucbi.customer_qc_project_mapping_id=cqpm.id  and cqpm.project_id=?1 and cqpm.qc_level=?2 and cqpm.id=?3 and batch_number=?4",nativeQuery = true)
    List<Long> getAllAllBatchInfoForProjectAndLevelForLoggedInUserLevel1(Long projectId, Integer level,Long customerQcProjectMappingId, Integer batchNumber);

    /*@Query(value="select ucbi.upload_id from upload_cqpm_batch_info ucbi,upload u, user_vendor_mapping_object_mapping uvmom, customer_qc_project_mapping cqpm where ucbi.upload_id=u.id and u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id in (?5) and ucbi.customer_qc_project_mapping_id=cqpm.id and ucbi.qc_status=2 and cqpm.project_id=?1 and cqpm.qc_level=?2 and cqpm.id=?3 and ucbi.batch_number=?4 and ucbi.show_to_qc=1",nativeQuery = true)
    List<Long> getAllPendingBatchInfoForProjectAndLevelForLoggedInUserGreaterThanLevel1(Long projectId, Integer level,Long customerQcProjectMappingId, Integer batchNumber);
*/
    @Query(value="select ucbi.upload_id from upload_cqpm_batch_info ucbi, customer_qc_project_mapping cqpm where ucbi.customer_qc_project_mapping_id=cqpm.id and ucbi.qc_status=2 and cqpm.project_id=?1 and cqpm.qc_level=?2 and cqpm.id=?3 and ucbi.batch_number in (select max(ucbi1.batch_number) from upload_cqpm_batch_info ucbi1 where ucbi1.customer_qc_project_mapping_id=?3 and ucbi.qc_status=2)",nativeQuery = true)
    List<Long> getAllPendingBatchInfoForProjectAndLevelForLoggedInUserLevel1(Long projectId, Integer level,Long customerQcProjectMappingId);

    @Query(value="select ucbi.upload_id from upload_cqpm_batch_info ucbi, customer_qc_project_mapping cqpm where ucbi.customer_qc_project_mapping_id=cqpm.id and ucbi.qc_status=2 and cqpm.project_id=?1 and cqpm.qc_level=?2 and cqpm.id=?3 and ucbi.batch_number in (select max(ucbi1.batch_number) from upload_cqpm_batch_info ucbi1 where ucbi1.customer_qc_project_mapping_id=?3 and ucbi.qc_status=2) and ucbi.show_to_qc=1",nativeQuery = true)
    List<Long> getAllPendingBatchInfoForProjectAndLevelForLoggedInUserGreaterThanLevel1(Long projectId, Integer level,Long customerQcProjectMappingId);


    @Query(value="select ucbi.upload_id from upload_cqpm_batch_info ucbi, customer_qc_project_mapping cqpm where ucbi.customer_qc_project_mapping_id=cqpm.id  and cqpm.project_id=?1 and cqpm.id=?2 and ucbi.batch_number=?3",nativeQuery = true)
    List<Long> getAllPendingBatchInfoForProjectAndLevelForLoggedInUserPreviousThanLevel1(Long projectId,Long customerQcProjectMappingId,Integer batchNumber);


    @Query(value="select ucbi.upload_id from upload_cqpm_batch_info ucbi,  customer_qc_project_mapping cqpm where  ucbi.customer_qc_project_mapping_id=cqpm.id and cqpm.project_id=?1 and cqpm.qc_level=?2 and cqpm.id=?3 and ucbi.batch_number=?4 and ucbi.show_to_qc=1",nativeQuery = true)
    List<Long> getAllPendingBatchInfoForProjectAndLevelForLoggedInUserGreaterThanLevel1(Long projectId, Integer level,Long customerQcProjectMappingId, Integer batchNumber);

    @Query(value="select count(*) from upload_cqpm_batch_info ucbi where ucbi.customer_qc_project_mapping_id=?1 and ucbi.batch_number=?2 and ucbi.qc_status=2",nativeQuery = true)
    Integer getQcPendingCountInCurrentBatch(Long customerQcProjectMappingId, Long batchNumber);

    @Query(value="select u.* from upload_cqpm_batch_info ucbi,upload u where ucbi.customer_qc_project_mapping_id=?1 and ucbi.batch_number=?2 and ucbi.upload_id=u.id",nativeQuery = true)
    List<Upload> getUploadByBatchNumber(Long customerQcProjectMappingId, Integer batchNumber);

    @Query(value="select count(ucbi.id) from upload_cqpm_batch_info ucbi where ucbi.customer_qc_project_mapping_id=?1 and ucbi.batch_number=?2 and (ucbi.qc_status=1 or ucbi.qc_status=2) ",nativeQuery = true)
    Integer countUploadsByCustomerQcProjectMappingAndBatchNumber(Long customerQcProjectMappingId,Integer batchNumber);
    
    @Query(value="select count(ucbi.id) as total,\n"
    		+ "sum(case when ucbi.qc_status=1 then 1 else 0 end) as approved, \n"
    		+ "sum(case when ucbi.qc_status=0 then 1 else 0 end) as rejected,\n"
    		+ "sum(case when ucbi.qc_status=2 then 1 else 0 end) as pending  \n"
    		+ "from upload_cqpm_batch_info ucbi \n"
    		+ "where ucbi.customer_qc_project_mapping_id=?1 \n"
    		+ "and ucbi.batch_number=?2 ",nativeQuery = true)
    List<Integer[]> countUploadsByCustomerQcProjectMappingAndBatchNumberForFinalize(Long customerQcProjectMappingId,Long batchNumber);

    @Query(value="select count(ucbi.id) from upload_cqpm_batch_info ucbi where ucbi.customer_qc_project_mapping_id=?1 and ucbi.batch_number=?2  and ucbi.qc_status=?3",nativeQuery = true)
    Integer countUploadsByCustomerQcProjectMappingAndBatchNumber(Long customerQcProjectMappingId,Long batchNumber,Integer qcStatus);

    @Query(value="select * from upload_cqpm_batch_info ucbi where ucbi.upload_id=?1 and ucbi.customer_qc_project_mapping_id=?2 and ucbi.batch_number=?3",nativeQuery = true)
    UploadCustomerQcProjectMappingBatchInfo findByUploadIdAndCustomerQcProjectMappingId(Long uploadId,Long customerQcProjectMappingId,Integer batchNumber);
    
    
    
    @Query(value="select count(*)  from upload_cqpm_batch_info ucbi where ucbi.qc_status=1 and ucbi.batch_number in (?1)",nativeQuery = true)
    Integer getApprovedUploadsCount(List<Long> batchNumber);
    
    @Query(value="select * from upload_cqpm_batch_info ucbi where ucbi.upload_id=?1",nativeQuery = true)
    UploadCustomerQcProjectMappingBatchInfo getByUploadId(Long uploadId);
    
    @Modifying
    @Query(value="update upload_cqpm_batch_info set qc_status=1 where batch_number=?2 and customer_qc_project_mapping_id=?1 and show_to_qc=0 and qc_status=2",nativeQuery =  true)
    void updateUcqpmbi(Long customerQcProjectMappingId,Long batchNumber);
    
    
    @Modifying
    @Query(value="select "
    		+ " p.id as project_id,"
    		+ " uvmpm.id as uvmpm_id,"
    		+ " u.user_vendor_mapping_object_mapping_id as uvmom_id,"
    		+ " o.id as object_id,"
    		+ " count(*) as total, \n"
    		+ " sum(case when ucbi.qc_status=1 then 1 else 0 end) as total_approved, \n"
    		+ " sum(case when ucbi.qc_status=0 then 1 else 0 end) as total_rejected, \n"
    		+ " sum(case when ucbi.qc_status=2 then 1 else 0 end) as total_pending \n"
    		+ " from  \n"
    		+ " upload_cqpm_batch_info ucbi, \n"
    		+ " upload u,\n"
    		+ " user_vendor_mapping_object_mapping uvmom, \n"
    		+ " user_vendor_mapping_project_mapping uvmpm, \n"
    		+ " object o ,\n"
    		+ " project p \n"
    		+ " where \n"
    		+ " ucbi.upload_id=u.id \n"
    		+ " and  u.user_vendor_mapping_object_mapping_id = uvmom.id \n"
    		+ " and uvmom.object_id=o.id \n"
    		+ " and o.project_id=p.id \n"
    		+ " and uvmpm.project_id=p.id \n"
    		+ " and ucbi.batch_number=?2 \n"
    		+ " and ucbi.customer_qc_project_mapping_id=?1 \n"
    		+ " and ucbi.show_to_qc=0 \n"
    		+ " and ucbi.qc_status=2 \n"
    		+ " group by u.user_vendor_mapping_object_mapping_id,o.id,uvmpm.id, p.id",nativeQuery =  true)
    List<Long[]> getUvmomObjectIdsOfFinalLevelPendingAndNotShown(Long customerQcProjectMappingId,Long batchNumber);
    
    @Modifying
    @Query(value="select "
    		+ " p.id as project_id,"
    		+ " uvmpm.id as uvmpm_id,"
    		+ " u.user_vendor_mapping_object_mapping_id as uvmom_id,"
    		+ " o.id as object_id,"
    		+ " count(*) as total, \n"
    		+ " sum(case when ucbi.qc_status=1 then 1 else 0 end) as total_approved, \n"
    		+ " sum(case when ucbi.qc_status=0 then 1 else 0 end) as total_rejected, \n"
    		+ " sum(case when ucbi.qc_status=2 then 1 else 0 end) as total_pending, \n"
    		+ " sum(ucbi.show_to_qc) as show_to_qc \n"
    		+ " from  \n"
    		+ " upload_cqpm_batch_info ucbi, \n"
    		+ " upload u,\n"
    		+ " user_vendor_mapping_object_mapping uvmom, \n"
    		+ " user_vendor_mapping_project_mapping uvmpm, \n"
    		+ " object o ,\n"
    		+ " project p \n"
    		+ " where \n"
    		+ " ucbi.upload_id=u.id \n"
    		+ " and  u.user_vendor_mapping_object_mapping_id = uvmom.id \n"
    		+ " and uvmom.object_id=o.id \n"
    		+ " and o.project_id=p.id \n"
    		+ " and uvmpm.project_id=p.id \n"
    		+ " and ucbi.batch_number=?2 \n"
    		+ " and ucbi.customer_qc_project_mapping_id=?1 \n"
    		+ " group by u.user_vendor_mapping_object_mapping_id,o.id,uvmpm.id, p.id",nativeQuery =  true)
    List<Long[]> getUvmomObjectIdsOfBatch(Long customerQcProjectMappingId,Long batchNumber);
    
    
    
    
    
    @Modifying
    @Query(value="update upload_cqpm_batch_info ucbi, upload u "
    		+ "set ucbi.qc_status=1, u.qc_status=1, u.approval_status=1,"
    		+ "u.qc_end_date=now() where ucbi.upload_id=u.id and ucbi.batch_number=?2 "
    		+ "and ucbi.customer_qc_project_mapping_id=?1 and ucbi.show_to_qc=?3 and ucbi.qc_status=2",nativeQuery =  true)
    void updateUcqpmbiUploadFinalLevel(Long customerQcProjectMappingId,Long batchNumber,Integer showToQc);
    
    @Modifying
    @Query(value = "insert into upload_cqpm_batch_info(batch_number,upload_id,customer_qc_project_mapping_id,show_to_qc,qc_status) (select ?2,u.id,?3,?4,?5 from upload u where u.id in (?1)) ",nativeQuery = true)
    void insertUploadCqpmBatchInfo(List<Long> uploadIds,Long batchNumber, Long customerQcProjectMappingId,Integer showToQc, Integer qcStatus);
    
    

}
