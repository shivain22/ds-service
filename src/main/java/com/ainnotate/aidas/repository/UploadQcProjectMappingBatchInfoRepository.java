package com.ainnotate.aidas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.Upload;
import com.ainnotate.aidas.domain.UploadQcProjectMappingBatchInfo;
import com.ainnotate.aidas.dto.QcResultDTO;
import com.ainnotate.aidas.dto.UploadSummaryForQCFinalize;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
@Repository
@Transactional
public interface UploadQcProjectMappingBatchInfoRepository extends JpaRepository<UploadQcProjectMappingBatchInfo, Long> {


    @Query(value="select ucbi.upload_id from upload_qpm_batch_info ucbi where ucbi.qc_project_mapping_id=?1 and ucbi.batch_number=?2 and ucbi.qc_status=2", nativeQuery = true)
    List<Long> getQcPendingCount(Long qcProjectMappingId, Integer batchNumber);

    @Query(value="select ucbi.batch_number from upload_qpm_batch_info ucbi, qc_project_mapping qpm where ucbi.qc_project_mapping_id=qpm.id and qpm.project_id=?1 and qpm.id=?2 and ucbi.qc_status=2", nativeQuery = true)
    List<Integer> getQcPendingInAllCount(Long projectId,Long qcProjectMappingId);

    @Query(value="select distinct uvmom.object_id from upload_qpm_batch_info ucbi, upload u, user_vendor_mapping_object_mapping uvmom, qc_project_mapping qpm where ucbi.upload_id=u.id and u.user_vendor_mapping_object_mapping_id=uvmom.id and ucbi.qc_project_mapping_id=qpm.id and qpm.project_id=?1 and qpm.id=?2 and ucbi.qc_status=2 and ucbi.batch_number in (?3)", nativeQuery = true)
    List<Long> getQcPendingObjectInAllCount(Long projectId,Long qcProjectMappingId,List<Integer> batchNumber);

    @Query(value="select upload_id from upload_qpm_batch_info where qc_project_mapping_id=?1 and batch_number=?2 order by upload_id",nativeQuery = true)
    List<Long> getUploadIdByqcProjectMappingAndBatchNumber(Long qcProjectMappingId, Integer batchNumber);

    @Query(value="select ucbi.* from upload_qpm_batch_info ucbi where qc_project_mapping_id=?1 and batch_number=?2 and ucbi.upload_id=?3",nativeQuery = true)
    UploadQcProjectMappingBatchInfo getUploadIdByqcProjectMappingAndBatchNumber(Long qcProjectMappingId, Integer batchNumber,Long uploadId);
    
    @Query(value="select ucbi.* from upload_qpm_batch_info ucbi where  batch_number=?1 and ucbi.upload_id=?2",nativeQuery = true)
    UploadQcProjectMappingBatchInfo getUploadIdByBatchNumber(Integer batchNumber,Long uploadId);

    @Query(value="select ucbi.upload_id from upload_qpm_batch_info ucbi where qc_project_mapping_id=?1 and batch_number=?2 and ucbi.qc_status=2",nativeQuery = true)
    List<Long> getUploadsPendingByQcLevelGreaterThan1AndShowToQcIs1(Long qcProjectMappingId, Integer batchNumber);

    @Query(value="select ucbi.upload_id from upload_qpm_batch_info ucbi where qc_project_mapping_id=?1 and batch_number=?2 and ucbi.qc_status=1",nativeQuery = true)
    List<Long> getUploadsApprovedByQcLevelGreaterThan1AndShowToQcIs1(Long qcProjectMappingId, Integer batchNumber);

    @Query(value="select ucbi.upload_id from upload_qpm_batch_info ucbi where ucbi.qc_project_mapping_id=?1 and ucbi.batch_number=?2 and ucbi.qc_status=1",nativeQuery = true)
    List<Long> getAllApprovedInBatch(Long qcProjectMappingId, Integer batchNumber);
    
    
    @Query(value="select count(ucbi.upload_id) \n"
    		+ "from upload_qpm_batch_info ucbi \n"
    		+ "where ucbi.qc_project_mapping_id=?1 \n"
    		+ "and ucbi.batch_number=?2 \n"
    		+ "and ucbi.qc_status=2 \n"
    		+ "and show_to_qc=1",nativeQuery = true)
    Integer getAllShowToQcAndPending(Long qcProjectMappingId, Integer batchNumber);
    
	/*
	 * @Query(value="select count(ucbi.id) \n" +
	 * "from upload_qpm_batch_info ucbi \n" + "where " +
	 * "ucbi.qc_project_mapping_id=?1 \n" + "and ucbi.batch_number=?2 \n" +
	 * "and ucbi.qc_status=2 \n" + "and ucbi.show_to_qc=1",nativeQuery = true)
	 * Integer getAllShowToQcAndPendingForGroupedProject(Long
	 * qcProjectMappingId, Integer batchNumber,Long uvmomId);
	 */
    
    @Query(value="select ucbi.upload_id,ucbi.id from upload_qpm_batch_info ucbi where ucbi.qc_project_mapping_id=?1 and ucbi.batch_number=?2 and ucbi.qc_status=2 and show_to_qc=0",nativeQuery = true)
    List<Long[]> getAllNotShowToQcAndPending(Long qcProjectMappingId, Integer batchNumber);
    
    @Query(value="select ucbi.upload_id,ucbi.id "
    		+ "from upload_qpm_batch_info ucbi, "
    		+ "upload u, \n"
    		+ "user_vendor_mapping_object_mapping uvmom \n"
    		+ "where "
    		+ "ucbi.upload_id=u.id \n"
    		+ "and u.user_vendor_mapping_object_mapping_id=uvmom.id \n"
    		+ "and ucbi.qc_project_mapping_id=?1 "
    		+ "and ucbi.batch_number=?2 "
    		+ "and ucbi.qc_status=2 "
    		+ "and ucbi.show_to_qc=0 ",nativeQuery = true)
    List<Long[]> getAllNotShowToQcAndPendingForGroupedProject(Long qcProjectMappingId, Integer batchNumber,Long uvmomId);

    @Query(value="select ucbi.upload_id from upload_qpm_batch_info ucbi where ucbi.qc_project_mapping_id=?1 and ucbi.batch_number=?2",nativeQuery = true)
    List<Long> getAllInBatch(Long qcProjectMappingId, Integer batchNumber);

    @Query(value="select u.id from upload_qpm_batch_info ucbi,upload u,user_vendor_mapping_object_mapping uvmom  where ucbi.qc_project_mapping_id=?1 and u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=?2 and ucbi.batch_number=?3 and ucbi.qc_status=2 and ucbi.upload_id=u.id",nativeQuery = true)
    List<Long> getAllInBatchGrouped(Long qcProjectMappingId,Long objectId, Integer batchNumber);


    @Query(value="select u.id "
    		+ "from upload_qpm_batch_info ucbi,upload u"
    		+ " where ucbi.qc_project_mapping_id=?1 "
    		+ "and ucbi.batch_number=?2 "
    		+ "and (ucbi.qc_status=2)  "
    		+ "and ucbi.upload_id=u.id",nativeQuery = true)
    List<Long> getRemainingUploadsInBatchIncludingCurrentUpload(Long qcProjectMappingId, Integer batchNumber);
    
    @Query(value="select u.id "
    		+ "from upload_qpm_batch_info ucbi,upload u"
    		+ " where ucbi.qc_project_mapping_id=?1 "
    		+ "and ucbi.batch_number=?2 "
    		+ "and (ucbi.qc_status=2 or ucbi.qc_status=1)  "
    		+ "and ucbi.upload_id=u.id",nativeQuery = true)
    List<Long> getRemainingUploadsInBatchIncludingCurrentUploadAndPreviouslyApproved(Long qcProjectMappingId, Integer batchNumber);
    
    @Query(value="select count(*) from upload_qpm_batch_info ucbi where ucbi.batch_number=?2 and ucbi.qc_project_mapping_id=?1 and ucbi.qc_status=0",nativeQuery = true)
    Integer getRejectedCount(Long qcProjectMappingId, Integer batchNumber);
    
    @Query(value="select count(*) from upload_qpm_batch_info ucbi where ucbi.batch_number=?2 and ucbi.qc_project_mapping_id=?1 and ucbi.qc_status=1",nativeQuery = true)
    Integer getApprovedCount(Long qcProjectMappingId, Integer batchNumber);

    @Query(value="select u.id from upload_qpm_batch_info ucbi,upload u where ucbi.qc_project_mapping_id=?1 and ucbi.batch_number=?2 and ucbi.qc_status=2 and ucbi.upload_id=u.id and ucbi.upload_id not in (?3)",nativeQuery = true)
    List<Long> getRemainingUploadsInBatchIncludingCurrentUploadNew(Long qcProjectMappingId, Integer batchNumber,Long currentUploadId);

    @Query(value="select u.id from upload_qpm_batch_info ucbi,upload u,user_vendor_mapping_object_mapping uvmom  where ucbi.qc_project_mapping_id=?1 and u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=?2 and ucbi.batch_number=?2 and ucbi.qc_status=2 and ucbi.upload_id=u.id",nativeQuery = true)
    List<Long> getRemainingUploadsInBatchIncludingCurrentUploadGrouped(Long qcProjectMappingId, Long objectId,Integer batchNumber);

    @Query(nativeQuery = true)
    List<QcResultDTO> getQcLevelStatus(Long uploadId, Integer qcLevel);

    @Query(value="select max(batch_number) from upload_qpm_batch_info where qc_project_mapping_id=?1",nativeQuery = true)
    Integer getMaxBatchNoForQcLevel(Long qcProjectMappingId);

    @Query(value="select batch_number from (\n" +
        "select min(batch_number) batch_number,count(ucbi.qc_status=2 or null) as pending,count(ucbi.qc_status=1 or null)as approved,count(ucbi.qc_status=0 or null)as rejected  from \n" +
        "upload_qpm_batch_info ucbi\n" +
        "where \n" +
        "ucbi.id=1 group by ucbi.batch_number)a where cast(a.pending as signed) >0",nativeQuery = true)
    Integer getMinPendingBatchNoForQcLevel(Long projectId, Long qcProjectMappingId, Integer qcLevel);

    @Query(value="select max(batch_number) from upload_qpm_batch_info where qc_project_mapping_id=?1 and qc_status=2",nativeQuery = true)
    Integer getMinBatchNoPendingForQcLevel(Long qcProjectMappingId);

    @Query(value="select id from upload_qpm_batch_info ucbi, qc_project_mapping qpm where ucbi.qc_project_mapping_id=qpm.id and ucbi.qc_status=2 and qpm.project_id=?1 and qpm.qc_level=?2",nativeQuery = true)
    List<Long> getAllPendingBatchInfoForProjectAndLevel(Long projectId, Integer level);

    @Query(value="select batch_number from upload_qpm_batch_info ucbi, qc_project_mapping qpm where ucbi.qc_project_mapping_id=qpm.id and qpm.id in (?2) and qpm.project_id=?1 and ucbi.qc_status=2 group by ucbi.batch_number ",nativeQuery = true)
    List<Long> getAllPendingBatchNumbersForProjectAndLevel(Long projectId, List<Long> qpmIds);

    @Query(value="select ucbi.upload_id from upload_qpm_batch_info ucbi, qc_project_mapping qpm where ucbi.qc_project_mapping_id=qpm.id and ucbi.qc_status=2 and qpm.project_id=?1 and qpm.qc_level=?2 and qpm.id <> ?3",nativeQuery = true)
    List<Long> getAllPendingBatchInfoForProjectAndLevelAndOtherThanqpmId(Long projectId, Integer level,Long qcProjectMappingId);

    @Query(value="select ucbi.upload_id from upload_qpm_batch_info ucbi, qc_project_mapping qpm where ucbi.qc_project_mapping_id=qpm.id and ucbi.qc_status=2 and qpm.project_id=?1 and qpm.qc_level=?2 and qpm.id=?3 and batch_number=?4",nativeQuery = true)
    List<Long> getAllPendingBatchInfoForProjectAndLevelForLoggedInUserLevel1(Long projectId, Integer level,Long qcProjectMappingId, Integer batchNumber);

    @Query(value="select ucbi.upload_id from upload_qpm_batch_info ucbi, qc_project_mapping qpm where ucbi.qc_project_mapping_id=qpm.id  and qpm.project_id=?1 and qpm.qc_level=?2 and qpm.id=?3 and batch_number=?4",nativeQuery = true)
    List<Long> getAllAllBatchInfoForProjectAndLevelForLoggedInUserLevel1(Long projectId, Integer level,Long qcProjectMappingId, Integer batchNumber);

    /*@Query(value="select ucbi.upload_id from upload_qpm_batch_info ucbi,upload u, user_vendor_mapping_object_mapping uvmom, qc_project_mapping qpm where ucbi.upload_id=u.id and u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id in (?5) and ucbi.qc_project_mapping_id=qpm.id and ucbi.qc_status=2 and qpm.project_id=?1 and qpm.qc_level=?2 and qpm.id=?3 and ucbi.batch_number=?4 and ucbi.show_to_qc=1",nativeQuery = true)
    List<Long> getAllPendingBatchInfoForProjectAndLevelForLoggedInUserGreaterThanLevel1(Long projectId, Integer level,Long qcProjectMappingId, Integer batchNumber);
*/
    @Query(value="select ucbi.upload_id from upload_qpm_batch_info ucbi, qc_project_mapping qpm where ucbi.qc_project_mapping_id=qpm.id and ucbi.qc_status=2 and qpm.project_id=?1 and qpm.qc_level=?2 and qpm.id=?3 and ucbi.batch_number in (select max(ucbi1.batch_number) from upload_qpm_batch_info ucbi1 where ucbi1.qc_project_mapping_id=?3 and ucbi.qc_status=2)",nativeQuery = true)
    List<Long> getAllPendingBatchInfoForProjectAndLevelForLoggedInUserLevel1(Long projectId, Integer level,Long qcProjectMappingId);

    @Query(value="select ucbi.upload_id from upload_qpm_batch_info ucbi, qc_project_mapping qpm where ucbi.qc_project_mapping_id=qpm.id and ucbi.qc_status=2 and qpm.project_id=?1 and qpm.qc_level=?2 and qpm.id=?3 and ucbi.batch_number in (select max(ucbi1.batch_number) from upload_qpm_batch_info ucbi1 where ucbi1.qc_project_mapping_id=?3 and ucbi.qc_status=2) and ucbi.show_to_qc=1",nativeQuery = true)
    List<Long> getAllPendingBatchInfoForProjectAndLevelForLoggedInUserGreaterThanLevel1(Long projectId, Integer level,Long qcProjectMappingId);


    @Query(value="select ucbi.upload_id from upload_qpm_batch_info ucbi, qc_project_mapping qpm where ucbi.qc_project_mapping_id=qpm.id  and qpm.project_id=?1 and qpm.id=?2 and ucbi.batch_number=?3",nativeQuery = true)
    List<Long> getAllPendingBatchInfoForProjectAndLevelForLoggedInUserPreviousThanLevel1(Long projectId,Long qcProjectMappingId,Integer batchNumber);


    @Query(value="select ucbi.upload_id from upload_qpm_batch_info ucbi,  qc_project_mapping qpm where  ucbi.qc_project_mapping_id=qpm.id and qpm.project_id=?1 and qpm.qc_level=?2 and qpm.id=?3 and ucbi.batch_number=?4 and ucbi.show_to_qc=1",nativeQuery = true)
    List<Long> getAllPendingBatchInfoForProjectAndLevelForLoggedInUserGreaterThanLevel1(Long projectId, Integer level,Long qcProjectMappingId, Integer batchNumber);

    @Query(value="select count(*) from upload_qpm_batch_info ucbi where ucbi.qc_project_mapping_id=?1 and ucbi.batch_number=?2 and ucbi.qc_status=2",nativeQuery = true)
    Integer getQcPendingCountInCurrentBatch(Long qcProjectMappingId, Long batchNumber);

    @Query(value="select u.* from upload_qpm_batch_info ucbi,upload u where ucbi.qc_project_mapping_id=?1 and ucbi.batch_number=?2 and ucbi.upload_id=u.id",nativeQuery = true)
    List<Upload> getUploadByBatchNumber(Long qcProjectMappingId, Integer batchNumber);

    @Query(value="select count(ucbi.id) from upload_qpm_batch_info ucbi where ucbi.qc_project_mapping_id=?1 and ucbi.batch_number=?2 and (ucbi.qc_status=1 or ucbi.qc_status=2) ",nativeQuery = true)
    Integer countUploadsByqcProjectMappingAndBatchNumber(Long qcProjectMappingId,Integer batchNumber);
    
    @Query(nativeQuery = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    UploadSummaryForQCFinalize countUploadsByqcProjectMappingAndBatchNumberForFinalize(Long qcProjectMappingId,Long batchNumber);

    @Query(value="select count(ucbi.id) from upload_qpm_batch_info ucbi where ucbi.qc_project_mapping_id=?1 and ucbi.batch_number=?2  and ucbi.qc_status=?3",nativeQuery = true)
    Integer countUploadsByqcProjectMappingAndBatchNumber(Long qcProjectMappingId,Long batchNumber,Integer qcStatus);

    @Query(value="select * from upload_qpm_batch_info ucbi where ucbi.upload_id=?1 and ucbi.qc_project_mapping_id=?2 and ucbi.batch_number=?3",nativeQuery = true)
    UploadQcProjectMappingBatchInfo findByUploadIdAndqcProjectMappingId(Long uploadId,Long qcProjectMappingId,Integer batchNumber);
    
    
    
    @Query(value="select count(*)  from upload_qpm_batch_info ucbi where ucbi.qc_status=1 and ucbi.batch_number in (?1)",nativeQuery = true)
    Integer getApprovedUploadsCount(List<Long> batchNumber);
    
    @Query(value="select * from upload_qpm_batch_info ucbi where ucbi.upload_id=?1",nativeQuery = true)
    UploadQcProjectMappingBatchInfo getByUploadId(Long uploadId);
    
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value="update upload u,upload_qpm_batch_info ucbi set ucbi.qc_status=1,u.qc_status=1, "
    		+ "  ucbi.last_modified_date=now(), ucbi.last_modified_by=?3 where batch_number=?2 "
    		+ "and qc_project_mapping_id=?1 and ucbi.show_to_qc=0 and ucbi.qc_status=2 and ucbi.upload_id=u.id",nativeQuery =  true)
    void updateUqpmbi(Long qcProjectMappingId,Long batchNumber, String lastModifiedBy);
    
    
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
    		+ " upload_qpm_batch_info ucbi, \n"
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
    		+ " and ucbi.qc_project_mapping_id=?1 \n"
    		+ " and ucbi.show_to_qc=0 \n"
    		+ " and ucbi.qc_status=2 \n"
    		+ " group by u.user_vendor_mapping_object_mapping_id,o.id,uvmpm.id, p.id",nativeQuery =  true)
    List<Long[]> getUvmomObjectIdsOfFinalLevelPendingAndNotShown(Long qcProjectMappingId,Long batchNumber);
    
  
    @Query(nativeQuery =  true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    List<UploadSummaryForQCFinalize> getUvmomObjectIdsOfBatch(Long qcProjectMappingId,Long batchNumber);
    
    
    
    
    
    @Modifying(clearAutomatically = true,flushAutomatically = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value="update upload_qpm_batch_info ucbi, upload u "
    		+ "set u.qc_status=1, u.approval_status=1, ucbi.last_modified_date=now(), ucbi.qc_status=1,"
    		+ "u.qc_end_date=now() where ucbi.upload_id=u.id and ucbi.batch_number=?2 "
    		+ "and ucbi.qc_project_mapping_id=?1 and ucbi.show_to_qc=0 and ucbi.qc_status=2",nativeQuery =  true)
    void updateUqpmbiUploadFinalLevel(Long qcProjectMappingId,Long batchNumber);
    
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "insert into upload_qpm_batch_info(batch_number,upload_id,qc_project_mapping_id,show_to_qc,qc_status,qc_seen_status) (select ?2,u.id,?3,?4,?5,0 from upload u where u.id in (?1)) ",nativeQuery = true)
    void insertUploadqpmBatchInfo(List<Long> uploadIds,Long batchNumber, Long qcProjectMappingId,Integer showToQc, Integer qcStatus);
    
    

}
