package com.ainnotate.aidas.repository;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ainnotate.aidas.domain.Upload;
import com.ainnotate.aidas.dto.UploadDTOForQC;
import com.ainnotate.aidas.dto.UploadMetadataDTO;

/**
 * Spring Data SQL repository for the AidasUpload entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface UploadRepository extends JpaRepository<Upload, Long> {

    @Query(value="select * from upload where id=?1",nativeQuery = true)
    Upload getUploadById(Long id);

    @Query(value = "select count(*) from upload au, object ao,user_vendor_mapping_object_mapping auavmaom where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.object_id=ao.id and ao.id=?1 and ao.status=1 and ao.is_dummy=0 ",nativeQuery = true)
    Integer countAidasUploadByAidasUserAidasObjectMapping_AidasObject(Long aidasObjectId);

    @Query(value = "select u.* from upload u, user_vendor_mapping_object_mapping uvmom,object o where u.user_vendor_mapping_object_mapping_id=uvmom.id "
    		+ " and  uvmom.object_id=o.id and o.project_id=?1 and u.approval_status=?2 order by o.project_id, o.id, uvmom.id,u.id",nativeQuery = true)
    List<Upload>getAidasUploadsByProject(Long aidasProjectId, Integer approvalStatus);
    
    @Query(value = "select u.* from upload u, user_vendor_mapping_object_mapping uvmom,object o where u.user_vendor_mapping_object_mapping_id=uvmom.id "
    		+ " and  uvmom.object_id=o.id and o.project_id=?1 "
    		+ " and (select count(*) from upload u1,user_vendor_mapping_object_mapping uvmom1,object o1 where u1.user_vendor_mapping_object_mapping_id=uvmom1.id "
    		+ " and  uvmom1.object_id=o1.id and o1.project_id=?1 "
    		+ " and u1.approval_status=1 and o1.id=o.id) = o.number_of_uploads_required"
    		+ " order by o.project_id, o.id, uvmom.id,u.id",nativeQuery = true)
    List<Upload>getAidasUploadsByGroupedProjectApproved(Long aidasProjectId);
    
    @Query(value = "select u.* from upload u, user_vendor_mapping_object_mapping uvmom,object o where u.user_vendor_mapping_object_mapping_id=uvmom.id "
    		+ " and  uvmom.object_id=o.id and o.project_id=?1 "
    		+ " and o.id in "
    		+ "(select o.id from upload u, user_vendor_mapping_object_mapping uvmom, object o where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and u.approval_status=0 and o.project_id=?1 group by o.id)"
    		+ " order by o.project_id, o.id, uvmom.id,u.id",nativeQuery = true)
    List<Upload>getAidasUploadsByGroupedProjectRejected(Long aidasProjectId);

    @Query(value = "select u.* from upload u, user_vendor_mapping_object_mapping uvmom,object o where u.user_vendor_mapping_object_mapping_id=uvmom.id "
    		+ " and uvmom.object_id=o.id and o.project_id=?1 order by o.project_id,o.id,uvmom.id,u.id",nativeQuery = true)
    List<Upload>getAidasUploadsByProject(Long aidasProjectId);

    @Query(value = "select u.* from upload u, user_vendor_mapping_object_mapping uvmom where u.user_vendor_mapping_object_mapping_id=uvmom.id and  "
    		+ " uvmom.object_id=?1 and u.approval_status=?2 order by uvmom.object_id, uvmom.id, u.id",nativeQuery = true)
    List<Upload>getAidasUploadsByObject(Long aidasObjectId, Integer approvalStatus);
    
    @Query(value = "select u.* from upload u, user_vendor_mapping_object_mapping uvmom,object o where u.user_vendor_mapping_object_mapping_id=uvmom.id "
    		+ " and  uvmom.object_id=o.id and o.id=?1 "
    		+ " and (select count(*) from upload u1,user_vendor_mapping_object_mapping uvmom1,object o1 where u1.user_vendor_mapping_object_mapping_id=uvmom1.id "
    		+ " and  uvmom1.object_id=o1.id and o1.id=?1 "
    		+ " and u1.approval_status=1) = o.number_of_uploads_required"
    		+ " order by o.project_id, o.id, uvmom.id,u.id",nativeQuery = true)
    List<Upload>getAidasUploadsByGroupedProjectApprovedObject(Long aidasObjectId);
    
    @Query(value = "select u.* from upload u, user_vendor_mapping_object_mapping uvmom,object o where u.user_vendor_mapping_object_mapping_id=uvmom.id "
    		+ " and  uvmom.object_id=o.id and o.id=?1 "
    		+ " and (select count(*) from upload u1,user_vendor_mapping_object_mapping uvmom1,object o1 where u1.user_vendor_mapping_object_mapping_id=uvmom1.id "
    		+ " and  uvmom1.object_id=o1.id and o1.id=?1 "
    		+ " and u1.approval_status=0)>1"
    		+ " order by o.project_id, o.id, uvmom.id,u.id",nativeQuery = true)
    List<Upload>getAidasUploadsByGroupedProjectRejectedObject(Long aidasObjectId);

    @Query(value = "select u.* from upload au, user_vendor_mapping_object_mapping uvmom where au.user_vendor_mapping_object_mapping_id=uvmom.id and"
    		+ "  uvmom.object_id=?1 order by uvmom.object_id, uvmom.id, u.id",nativeQuery = true)
    List<Upload>getAidasUploadsByObject(Long aidasProjectId);

    @Query(value="select count(*) from upload  au", nativeQuery = true)
    Long countAidasUploads();

    @Query(value="select count(*) from upload  au where au.approval_status=?1", nativeQuery = true)
    Long countAidasUploads(Integer approvalStatus);


    //For org
    @Query(value="select count(*) from upload au, user_vendor_mapping_object_mapping auavmaom, object ao, project ap, customer ac, organisation ao1 where au.user_vendor_mapping_object_mapping_id=auavmaom.id  and auavmaom.object_id=ao.id and ao.project_id=ap.id and ap.customer_id=ac.id and ac.organisation_id=ao1.id and ao1.id=?1",nativeQuery = true)
    Long countAidasUploadByAidasOrganisation(Long aidasOrganisationId);

    @Query(value="select count(*) from upload au, user_vendor_mapping_object_mapping auavmaom, object ao, project ap, customer ac, organisation ao1 where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.object_id=ao.id and ao.project_id=ap.id and ap.customer_id=ac.id and ac.organisation_id=?1 and au.approval_status=?2",nativeQuery = true)
    Long countAidasUploadByAidasOrganisation(Long aidasOrganisationId,Integer approvalStatus);

    @Query(value="select * from upload au, user_vendor_mapping_object_mapping auavmaom, object ao, project ap, customer ac, organisation ao1 where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.object_id=ao.id and ao.project_id=ap.id and ap.customer_id=ac.id and ac.organisation_id=ao1.id and ao1.id=?1",
        countQuery = "select count(*) from upload au, user_vendor_mapping_object_mapping auavmaom, object ao, project ap, customer ac, organisation ao1 where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.object_id=ao.id and ao.project_id=ap.id and ap.customer_id=ac.id and ac.organisation_id=ao1.id and ao1.id=?1",
        nativeQuery = true)
    Page<Upload> findAidasUploadByAidasOrganisation(Long aidasOrganisationId, Pageable pageable);

    @Query(value="select au.* from upload au, user_vendor_mapping_object_mapping auavmaom, object ao, project ap, customer ac, organisation ao1 where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.object_id=ao.id and ao.project_id=ap.id and ap.customer_id=ac.id and ac.organisation_id=ao1.id and ao1.id=?1 ",nativeQuery = true)
    List<Upload> findAidasUploadByAidasOrganisation(Long aidasOrganisationId);

    @Query(value="select au.* from upload au, user_vendor_mapping_object_mapping auavmaom, object ao, project ap, customer ac, organisation ao1 where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.object_id=ao.id and ao.project_id=ap.id and ap.customer_id=ac.id and ac.organisation_id=ao1.id and ao1.id=?1 and au.approval_status=?2",nativeQuery = true)
    List<Upload> findAidasUploadByAidasOrganisation(Long aidasOrganisationId, Integer approvalStatus);


    //For cust
    @Query(value="select count(*) from upload au, user_vendor_mapping_object_mapping auavmaom, object ao, project ap, customer ac where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.object_id=ao.id and ao.project_id=ap.id and ap.customer_id=ac.id and ac.id=?1",nativeQuery = true)
    Long countAidasUploadByAidasCustomer(Long aidasCustomerId);

    @Query(value="select count(*) from upload au, user_vendor_mapping_object_mapping auavmaom, object ao, project ap, customer ac where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.object_id=ao.id and ao.project_id=ap.id and ap.customer_id=ac.id and ac.id=?1  and au.approval_status=?2",nativeQuery = true)
    Long countAidasUploadByAidasCustomer(Long aidasCustomerId,Integer approvalStatus);

    @Query(value="select au.* from upload au, user_vendor_mapping_object_mapping auavmaom, object ao, project ap, customer ac where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.object_id=ao.id and ao.project_id=ap.id and ap.customer_id=ac.id and ac.id=?1",nativeQuery = true)
    Page<Upload> findAidasUploadByAidasCustomer(Long aidasCustomerId, Pageable pageable);

    @Query(value="select au.* from upload au, user_vendor_mapping_object_mapping auavmaom, object ao, project ap, customer ac where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.object_id=ao.id and ao.project_id=ap.id and ap.customer_id=ac.id and ac.id=?1",nativeQuery = true)
    List<Upload> findAidasUploadByAidasCustomer(Long aidasCustomerId);

    @Query(value="select au.* from upload au, user_vendor_mapping_object_mapping auavmaom, object ao, project ap, customer ac where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.object_id=ao.id and ao.project_id=ap.id and ap.customer_id=ac.id and ac.id=?1  and au.approval_status=?2",nativeQuery = true)
    List<Upload> findAidasUploadByAidasCustomer(Long aidasCustomerId, Integer approvalStatus);


    //For Vendor
    @Query(value="select count(*) from upload au, user_vendor_mapping_object_mapping auavmaom,user_vendor_mapping auavm, user au1 where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=au1.id  and auavm.vendor_id=?1",nativeQuery = true)
    Long countAidasUploadByAidasVendor(Long aidasVendorId);

    @Query(value="select count(*) from upload au, user_vendor_mapping_object_mapping auavmaom,user_vendor_mapping auavm, user au1 where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=au1.id and auavm.vendor_id=?1 and au.approval_status=?2",nativeQuery = true)
    Long countAidasUploadByAidasVendor(Long aidasVendorId,Integer approvalStatus);

    @Query(value="select au.* from upload au, user_vendor_mapping_object_mapping auavmaom,user_vendor_mapping auavm, user au1 where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=au1.id and auavm.vendor_id=?1",nativeQuery = true)
    Page<Upload> findAidasUploadByAidasVendor(Long aidasVendorId, Pageable pageable);

    @Query(value="select au.* from upload au, user_vendor_mapping_object_mapping auavmaom,user_vendor_mapping auavm, user au1 where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=au1.id and auavm.vendor_id=?1",nativeQuery = true)
    List<Upload> findAidasUploadByAidasVendor(Long aidasVendorId);

    @Query(value="select au.* from upload au, user_vendor_mapping_object_mapping auavmaom,user_vendor_mapping auavm, user au1 where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=au1.id and auavm.vendor_id=?1  and au.approval_status=?2",nativeQuery = true)
    List<Upload> findAidasUploadByAidasVendor(Long aidasVendorId, Integer approvalStatus);


//For vendor user
    @Query(value="select count(*) from upload u, user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id=?1",nativeQuery = true)
    Long countAidasUploadByAidasVendorUser(Long aidasVendorId);

    @Query(value="select count(*) from upload au, user_vendor_mapping_object_mapping auavmaom,user_vendor_mapping auavm where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=?1 and au.approval_status=?2",nativeQuery = true)
    Long countAidasUploadByAidasVendorUser(Long aidasVendorId,Integer approvalStatus);

    @Query(value="select au.* from upload au, user_vendor_mapping_object_mapping auavmaom,user_vendor_mapping auavm where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=?1 and auavmaom.object_id=?2",
        countQuery = "select count(*) from upload au, user_vendor_mapping_object_mapping auavmaom,user_vendor_mapping auavm where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=?1 and auavmaom.object_id=?2",
        nativeQuery = true)
    Page<Upload> findAllByUserAndObject(Long userId, Long objectId, Pageable pageable);

    @Query(value="select au.* from upload au, user_vendor_mapping_object_mapping auavmaom,user_vendor_mapping auavm where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=?1 and auavmaom.object_id=?2 and au.approval_status=?3",
        countQuery = "select count(*) from upload au, user_vendor_mapping_object_mapping auavmaom,user_vendor_mapping auavm where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=?1 and auavmaom.object_id=?2 and au.approval_status=?3",
        nativeQuery = true)
    Page<Upload> findAllByUserAndObject(Long userId, Long objectId, Integer approvalStatus, Pageable pageable);



    @Query(value="select * from upload au, user_vendor_mapping_object_mapping auavmaom,object ao, project ap,user_vendor_mapping auavm where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.object_id=ao.id and ao.project_id=ap.id and ap.id=?2 and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=?1"
    		,countQuery = "select count(*) from upload au, user_vendor_mapping_object_mapping auavmaom,object ao, project ap,user_vendor_mapping auavm where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.object_id=ao.id and ao.project_id=ap.id and ap.id=?2 and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=?1"
    		,nativeQuery = true)
	Page<Upload> findAllByUserAndProject(Long userId, Long projectId, Pageable pageable);

    @Query(value="select * from upload au, user_vendor_mapping_object_mapping auavmaom,object ao, project ap,user_vendor_mapping auavm where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.object_id=ao.id and ao.project_id=ap.id and ap.id=?2 and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=?1 and au.approval_status=?3",
    		countQuery = "select count(*) from upload au, user_vendor_mapping_object_mapping auavmaom,object ao, project ap,user_vendor_mapping auavm where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.object_id=ao.id and ao.project_id=ap.id and ap.id=?2 and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=?1 and au.approval_status=?3",
    		nativeQuery = true)
    Page<Upload> findAllByUserAndProject(Long userId, Long projectId, Integer approvalStatus, Pageable pageable);



    @Query(value="select * from upload au, user_vendor_mapping_object_mapping auavmaom,user_vendor_mapping auavm where au.user_vendor_mapping_object_mapping_id=auavmaom.id  and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=?1",nativeQuery = true)
    Page<Upload> findAllByUser(Long userId, Pageable pageable);

    @Query(value="select * from upload au, user_vendor_mapping_object_mapping auavmaom,user_vendor_mapping auavm where au.user_vendor_mapping_object_mapping_id=auavmaom.id  and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=?1 and au.approval_status=?2",nativeQuery = true)
    Page<Upload> findAllByUser(Long userId, Integer approvalStatus, Pageable pageable);

    @Query(value="select au.* from upload au, user_vendor_mapping_object_mapping auavmaom,object ao, project ap,user_vendor_mapping auavm where au.approval_status=2 and au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.object_id=ao.id and ao.project_id=ap.id and ap.id=?2 and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=?1 and au.metadata_status=0",nativeQuery = true)
    List<Upload> findAllByUserAndProjectAllForMetadataProjectWise(Long userId, Long projectId);

    @Query(value="select au.* from upload au, user_vendor_mapping_object_mapping auavmaom,object ao, project ap,user_vendor_mapping auavm where au.approval_status=2 and au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.object_id=ao.id and ao.project_id=ap.id and ao.id=?2 and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=?1 and au.metadata_status=0",nativeQuery = true)
    List<Upload> findAllByUserAndProjectAllForMetadataObjectWise(Long userId, Long objectId);
    
    
    @Query(value="select u.id, o.name, u.upload_url from upload u, user_vendor_mapping_object_mapping uvmom, object o where u.user_vendor_mapping_object_mapping_id=uvmom.id and object_id=o.id and u.id=?1",nativeQuery = true)
    List<java.lang.Object[]> findAllByUserAndProjectAllForMetadataObjectWiseNew(Long uploadId);

    
    
    @Query(value="select au.* from upload au, user_vendor_mapping_object_mapping auavmaom,object ao, project ap,user_vendor_mapping auavm where au.approval_status=2 and au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.object_id=ao.id and ao.project_id=ap.id and au.id=?2 and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=?1 and au.metadata_status=0",nativeQuery = true)
    List<Upload> findAllByUserAndProjectAllForMetadataUploadWise(Long userId, Long uploadId);

    @Query(value="select u.* from upload u, user_vendor_mapping_object_mapping uvmom, object o where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and o.project_id=?1 and u.qc_done_by_id is null and u.qc_start_date is null and u.qc_end_date is null  and ( u.qc_status is null or u.qc_status=2 ) and  u.metadata_status=1  limit 10",nativeQuery = true)
    List<Upload> findTopByQcNotDoneYetForQcLevel(Long projectId, Integer qcLevel);

    @Query(value="select u.* from upload u, user_vendor_mapping_object_mapping uvmom, object o where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and o.project_id=?1 and u.qc_done_by_id is null and u.qc_start_date is null and u.qc_end_date is null  and ( u.qc_status is null or u.qc_status=2 ) and  u.metadata_status=1 and u.current_qc_level=?2 and o.id =?3  order by uvmom.id, o.id limit ?4 ",nativeQuery = true)
    List<Upload> findTopByQcNotDoneYetForQcLevel(Long projectId,Integer qcLevel, Integer batchNumber);

    @Query(value="select u.* from upload u where u.qc_done_by_id is null and u.qc_start_date is null and u.qc_end_date is null  and u.qc_status=2  and  u.metadata_status=1 and u.current_qc_level=1 and u.current_batch_number=?1 ",nativeQuery = true)
    List<Upload> getUploadsForProjectPendingByQcLevel1InBatch(Integer batchNumber);

    @Query(value="select u.* from upload u, user_vendor_mapping_object_mapping uvmom, object o where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and o.project_id=?1 and u.qc_done_by_id is null and u.qc_start_date is null and u.qc_end_date is null  and  u.qc_status=2 and  u.metadata_status=1 and u.current_qc_level=1 and o.id in (?2) and (u.current_batch_number is null or u.current_batch_number=0) and u.user_vendor_mapping_object_mapping_id in (?3) order by o.id limit ?4",nativeQuery = true)
    List<Upload> getUploadsForProjectToBeAssignedToQcLevel1FromUploads(Long projectId, Long objectId, Long userVendorMappingObjectMappingId,Integer batchSize);

    @Query(value="select u.* from upload u, user_vendor_mapping_object_mapping uvmom, object o where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and o.project_id=?1 and u.qc_done_by_id is null and u.qc_start_date is null and u.qc_end_date is null  and  u.qc_status=2 and  u.metadata_status=1 and u.current_qc_level=1 and o.id in (?2) and (u.current_batch_number is null or u.current_batch_number=0) and u.user_vendor_mapping_object_mapping_id in (?3) order by o.id limit ?4",nativeQuery = true)
    List<Upload> getUploadsForProjectToBeAssignedToQcLevel1FromUploads(Long projectId, List<Long> objectId, List<Long> userVendorMappingObjectMappingId,Integer batchSize);

    @Query(value="select u.* from upload u, user_vendor_mapping_object_mapping uvmom, object o where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and o.project_id=?1 and u.qc_done_by_id is null and u.qc_start_date is null and u.qc_end_date is null  and  u.qc_status=2 and  u.metadata_status=1 and u.current_qc_level=1 and o.id in (?2) and (u.current_batch_number is null or u.current_batch_number=0)  order by o.id ",nativeQuery = true)
    List<Upload> getUploadsForGroupedProjectToBeAssignedToQcLevel1FromUploads(Long projectId, List<Long> objectId);

    @Query(value="select u.* from upload u, user_vendor_mapping_object_mapping uvmom, object o where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and o.project_id=?1 and u.qc_done_by_id is null and u.qc_start_date is null and u.qc_end_date is null  and  u.qc_status=2 and  u.metadata_status=1 and u.current_qc_level=1 and o.id in (select o1.id from object o1 where o1.project_id=?1 ) and u.id not in (?3) order by o.id limit ?2",nativeQuery = true)
    List<Upload> getUploadsForProjectToBeAssignedToQcLevel1FromUploads(Long projectId, Integer numberOfObjectsForQcLevel,List<Long> uploadIdsAlreadySelected);

    @Query(value="select  count(*) from\n" +
        "upload u,\n" +
        "user_vendor_mapping_object_mapping uvmom,\n" +
        "object o\n" +
        "where\n" +
        "u.user_vendor_mapping_object_mapping_id=uvmom.id and\n" +
        "uvmom.object_id=o.id and o.project_id=?1 and\n" +
        "u.qc_done_by_id is not null and\n" +
        "u.qc_start_date is not null and\n" +
        "u.qc_end_date is not null  and\n" +
        " u.metadata_status=1 and\n" +
        "u.current_qc_level=?2 and o.id=?3 and u.user_vendor_mapping_object_mapping_id=?4",nativeQuery = true)
    Long findAllUploadsPendingQcForLevelGreaterThan1(Long projectId,Integer qcLevel,Long objectId,Long uvmomId);

    @Query(value="select  count(*) from\n" +
        "upload u,\n" +
        "user_vendor_mapping_object_mapping uvmom,\n" +
        "object o\n" +
        "where\n" +
        "u.user_vendor_mapping_object_mapping_id=uvmom.id and\n" +
        "uvmom.object_id=o.id and o.project_id=?1 and\n" +
        "u.qc_done_by_id is not null and\n" +
        "u.qc_start_date is not null and\n" +
        "u.qc_end_date is not null  and\n" +
        " u.metadata_status=1 and\n" +
        "u.current_qc_level=?2 and o.id=?3 and u.user_vendor_mapping_object_mapping_id=?4",nativeQuery = true)
    Long findAllUploadsPendingQcForLevelGreaterThan1ForGroupedProject(Long projectId,Integer qcLevel,Long objectId);

    @Query(value="select a.id from (\n" +
        "select b.id as id,b.nour, sum(b.approved), sum(b.pending), sum(b.rejected) from (\n" +
        "select  o.id as id,o.number_of_uploads_required as nour,case when u.qc_status=1 then 1 else 0 end as approved,case when u.qc_status=2 then 1 else 0 end as pending ,case when u.qc_status=0 then 1 else 0 end as rejected from\n" +
        "upload u,\n" +
        "user_vendor_mapping_object_mapping uvmom,\n" +
        "object o\n" +
        "where\n" +
        "u.user_vendor_mapping_object_mapping_id=uvmom.id and\n" +
        "uvmom.object_id=o.id and o.project_id=?1 and\n" +
        "u.qc_done_by_id is not null and\n" +
        "u.qc_start_date is not null and\n" +
        "u.qc_end_date is not null  and\n" +
        " u.metadata_status=1 and\n" +
        "u.current_qc_level=?2 ) b group by id ) a",nativeQuery = true)
    List<Long> findAllObjectsPendingQcForLevelGreaterThan1(Long projectId,Integer qcLevel);

    @Query(value="select u.user_vendor_mapping_object_mapping_id from upload u,user_vendor_mapping_object_mapping uvmom where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=?1 and u.current_qc_level=?2",nativeQuery = true)
    List<Long> findAllUvmomForObject(Long objectId, Integer qcLevel);

    @Query(value="select a.id from (\n" +
        "select b.id as id,b.nour, sum(b.approved), sum(b.pending), sum(b.rejected) from (\n" +
        "select  o.id as id,o.number_of_uploads_required as nour,case when u.qc_status=1 then 1 else 0 end as approved,case when u.qc_status=2 then 1 else 0 end as pending ,case when u.qc_status=0 then 1 else 0 end as rejected from\n" +
        "upload u,\n" +
        "user_vendor_mapping_object_mapping uvmom,\n" +
        "object o\n" +
        "where\n" +
        "u.user_vendor_mapping_object_mapping_id=uvmom.id and\n" +
        "uvmom.object_id=o.id and o.project_id=1 and\n" +
        "u.qc_done_by_id is not null and\n" +
        "u.qc_start_date is not null and\n" +
        "u.qc_end_date is not null  and\n" +
        " u.metadata_status=1 and\n" +
        "u.current_qc_level=?2 ) b group by id ) a limit ?3",nativeQuery = true)
    List<Long> findAllObjectsQcInAssignedForLevelGreaterThan1(Long projectId,Integer qcLevel,Integer numberOfObjectsForQc);

    @Query(value="select a.id from (\n" +
        "select  o.id,o.number_of_uploads_required,count(u.qc_status=2 or null) as pending,count(u.qc_status=1 or null)as approved,count(u.qc_status=0 or null)as rejected from\n" +
        "upload u,\n" +
        "user_vendor_mapping_object_mapping uvmom,\n" +
        "object o\n" +
        "where\n" +
        "u.user_vendor_mapping_object_mapping_id=uvmom.id and\n" +
        "uvmom.object_id=o.id and o.project_id=?1 and\n" +
        "u.qc_done_by_id is not null and\n" +
        "u.qc_start_date is not null and\n" +
        "u.qc_end_date is not null  and\n" +
        "u.qc_status is not null  and  u.metadata_status=1 and\n" +
        "u.id in(?4) and\n" +
        "u.current_qc_level=?2 and u.current_batch_number is null  group by o.id) a  limit ?3",nativeQuery = true)
    List<Long> findAllObjectsQcInAssignedForLevelGreaterThan1(Long projectId,Integer qcLevel,Integer numberOfObjectsForQc,List<Long> uploadIds);

    @Query(value="select a.id from (\n" +
        "select  o.id,o.number_of_uploads_required,count(u.qc_status=2 or null) as pending,count(u.qc_status=1 or null)as approved,count(u.qc_status=0 or null)as rejected from\n" +
        "upload u,\n" +
        "user_vendor_mapping_object_mapping uvmom,\n" +
        "object o\n" +
        "where\n" +
        "u.user_vendor_mapping_object_mapping_id=uvmom.id and\n" +
        "uvmom.object_id=o.id and o.project_id=?1 and\n" +
        "u.qc_done_by_id is not null and\n" +
        "u.qc_start_date is not null and\n" +
        "u.qc_end_date is not null  and\n" +
        "u.qc_status is not null  and  u.metadata_status=1 and\n" +
        "u.current_qc_level=?2 and u.current_batch_number is null  group by o.id) a where cast(a.number_of_uploads_required as signed) <> cast(a.approved as signed)",nativeQuery = true)
    List<Long> findAllObjectsQcInUnAssignedForLevelGreaterThan1(Long projectId,Integer qcLevel);




    @Query(value="select  distinct uvmom.* from\n" +
        "    upload u,\n" +
        "    user_vendor_mapping_object_mapping uvmom,\n" +
        "    object o\n" +
        "    where\n" +
        "    u.user_vendor_mapping_object_mapping_id=uvmom.id and\n" +
        "    uvmom.object_id=o.id and o.project_id=?1 and\n" +
        "    u.qc_done_by_id is not null and\n" +
        "    u.qc_start_date is not null and\n" +
        "    u.qc_end_date is not null  and\n" +
        "    u.qc_status is not null  and  u.metadata_status=1 and\n" +
        "    u.current_qc_level=?2",nativeQuery = true)
    List<Long> findUvmomsQcNotStarted(Long projectId, Integer qcLevel);

    @Query(value="select  distinct o.id from\n" +
        "    upload u,\n" +
        "    user_vendor_mapping_object_mapping uvmom,\n" +
        "    object o\n" +
        "    where\n" +
        "    u.user_vendor_mapping_object_mapping_id=uvmom.id and\n" +
        "    uvmom.object_id=o.id and o.project_id=?1 and\n" +
        "    u.qc_done_by_id is null and\n" +
        "    u.qc_start_date is null and\n" +
        "    u.qc_end_date is null  and\n" +
        "    (u.qc_status is not null or u.qc_status=2)  and  u.metadata_status=1 and\n" +
        "    u.current_qc_level=1",nativeQuery = true)
    List<Long> findObjectsQcNotStarted(Long projectId,Integer qcLevel);

    @Query(value="select  distinct o.id as object_id,uvmom.id as uvmom_id from\n" +
        "    upload u,\n" +
        "    user_vendor_mapping_object_mapping uvmom,\n" +
        "    object o\n" +
        "    where\n" +
        "    u.user_vendor_mapping_object_mapping_id=uvmom.id and\n" +
        "    uvmom.object_id=o.id and o.project_id=?1 and\n" +
        "    u.qc_done_by_id is null and\n" +
        "    u.qc_start_date is null and\n" +
        "    u.qc_end_date is null  and\n" +
        "    (u.qc_status is not null or u.qc_status=2)  and  u.metadata_status=1 and\n" +
        "    u.current_qc_level=1",nativeQuery = true)
    List<Long[]> findObjectsWithUvmomQcNotStarted(Long projectId,Integer qcLevel);
    
    @Query(value="select u.* from upload u where u.user_vendor_mapping_object_mapping_id in (?1) and"+
    		"    u.qc_done_by_id is null and\n" +
            "    u.qc_start_date is null and\n" +
            "    u.qc_end_date is null  and\n" +
            "    (u.qc_status is null or u.qc_status=2)  and  u.metadata_status=1 " +
    		"    order by u.user_vendor_mapping_object_mapping_id,u.id limit ?2",nativeQuery = true)
    List<Upload> findAllUploadsNonGrouped(List<Long> uvmomIds,Integer batchSize);
    
    
    @Query(value="select u.id from upload u where u.user_vendor_mapping_object_mapping_id in (?1) and"+
    		"    u.qc_done_by_id is null and\n" +
            "    u.qc_start_date is null and\n" +
            "    u.qc_end_date is null  and\n" +
            "    (u.qc_status is null or u.qc_status=2)  and  u.metadata_status=1 " +
    		"    order by u.user_vendor_mapping_object_mapping_id,u.id limit ?2",nativeQuery = true)
    List<UploadDTOForQC> findAllUploadIdsNonGrouped(List<Long> uvmomIds,Integer batchSize);
    
    
    
    @Query(value="select u.* from upload u where u.user_vendor_mapping_object_mapping_id in (?1) and"+
    		"    u.qc_done_by_id is null and\n" +
            "    u.qc_start_date is null and\n" +
            "    u.qc_end_date is null  and\n" +
            "    (u.qc_status is null or u.qc_status=2)  and  u.metadata_status=1 " +
    		"    order by u.user_vendor_mapping_object_mapping_id,u.id",nativeQuery = true)
    List<Upload> findAllUploadsGroupedNew(List<Long> uvmomIds);
    
    @Query(nativeQuery = true)
    List<UploadDTOForQC> findAllUploadIdsGroupedNew(List<Long> uvmomIds);
    
    @Query(nativeQuery = true)
    List<UploadDTOForQC> findAllUploadIdsNonGroupedNew(List<Long> uvmomIds,Integer batchSize);
    
    
    
    @Query(value="select u.* from upload u where u.user_vendor_mapping_object_mapping_id in (?1) order by u.user_vendor_mapping_object_mapping_id,u.id",nativeQuery = true)
    List<Upload> findAllUploadIdsGrouped(List<Long> uvmomIds);
    
    @Query(value="select o.id from object o where o.qc_start_status=0 and o.project_id=?1 and o.current_qc_level=?2 order by o.id limit ?3",nativeQuery = true)
    List<Long> findAllObjectsQcNotStarted(Long projectId,Integer qcLevel,Integer batchSize);
    
    
    
    @Query(value="select distinct uvmom.id  "
    		+ "from user_vendor_mapping_object_mapping uvmom,object o,upload u "
    		+ "where u.user_vendor_mapping_object_mapping_id=uvmom.id and "
    		+ "uvmom.object_id=o.id and "
    		+ "o.project_id=?1 and "
    		+ "o.is_dummy=0 and u.current_batch_number =0 and u.current_qc_level=?2 order by uvmom.id limit ?3",nativeQuery = true)
    List<Long> findAllUvmomsQcNotStarted(Long projectId,Integer qcLevel,Integer batchSize);

    @Query(value="select u.* from upload u, user_vendor_mapping_object_mapping uvmom, object o where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and o.project_id=?1 and  u.metadata_status=1 and u.current_qc_level=?2 and o.id=?4 and u.user_vendor_mapping_object_mapping_id=?5 order by uvmom.id, o.id limit ?3  ",nativeQuery = true)
    List<Upload> findTopByQcNotDoneYetForQcLevelGreaterThan1(Long projectId,Integer qcLevel, Long batchSize,Long objectId, Long uvmomId);

    @Query(value="select u.* from upload u, user_vendor_mapping_object_mapping uvmom, object o where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and o.project_id=?1 and  u.metadata_status=1 and u.current_qc_level=?2 and o.id=?3 order by uvmom.id, o.id  limit ?4 ",nativeQuery = true)
    List<Upload> findTopByQcNotDoneYetForQcLevelGreaterThan1(Long projectId,Integer qcLevel,Long objectIdNotDone, Integer batchSize);

    @Query(value="select u.* from upload u, user_vendor_mapping_object_mapping uvmom, object o where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and o.project_id=?1 and  u.metadata_status=1 and u.current_qc_level=?2 order by uvmom.id, o.id  limit ?3 ",nativeQuery = true)
    List<Upload> findTopByQcNotDoneYetForQcLevelGreaterThan1(Long projectId,Integer qcLevel );

    @Query(value="select u.* from upload u where u.user_vendor_mapping_object_mapping_id=?1 ",nativeQuery = true)
    List<Upload> findUploadsByUvmom(Long uvmomId);

    @Query(value="select count(u.id) from upload u, user_vendor_mapping_object_mapping uvmom, object o where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and o.project_id=?1 and u.qc_status=1 and  u.current_qc_level=?2 ",nativeQuery = true)
    Integer getTotalApprovedForLevel(Long projectId,Integer qcLevel);

    @Query(value="select o.id from upload u, user_vendor_mapping_object_mapping uvmom, object o where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and o.project_id=?1 and u.qc_status=1 and  u.current_qc_level=?2 and u.current_batch_number is null group by o.id",nativeQuery = true)
    List<Long> getTotalApprovedObjectForLevelAndProject(Long projectId,Integer qcLevel);

    @Query(value="select u.* from upload u, user_vendor_mapping_object_mapping uvmom, object o where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and o.project_id=?1 and u.qc_status=1 and  u.current_qc_level=?2 ",nativeQuery = true)
    Set<Upload> getApprovedUploadForLevel(Long projectId, Integer qcLevel);

    @Query(value="select u.* from upload u, user_vendor_mapping_object_mapping uvmom, object o where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and o.id=?2 and o.project_id=?1 and u.qc_status=1 and  u.current_qc_level=?3 ",nativeQuery = true)
    Set<Upload> getApprovedUploadForLevel(Long projectId,Long objectId, Integer qcLevel);

    @Query(value="select u.* from upload u where u.id in (?1)", nativeQuery = true)
    Set<Upload> getUploadsByIds(List<Long> uploadIds);

    @Query(value="select count(u.id) from upload u, user_vendor_mapping_object_mapping uvmom, object o where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and o.project_id=?2 and  u.qc_done_by_id=?1 and u.qc_status=0 and  u.metadata_status=1  limit 10",nativeQuery = true)
    Integer findPendingQcDoneByCQPM(Long customerQcProjectMappingId, Long projectId);

    @Query(value="select upload.* from upload where upload.qc_done_by_id is not null and qc_end_date is null and qc_start_date is not null and qc_status=0 and TIMEstampDIFF(SECOND,qc_start_date,now())>(select value from app_property where name='qc_clean_up_time')",nativeQuery = true)
    List<Upload> findUploadsHeldByQcForMoreThan10Mins();

    @Query(value = "select count(*) from upload where status=1", nativeQuery = true)
    Long countAllUploadsForSuperAdmin();

    @Query(value = "select count(*) from upload where id>0 and approval_status=?1 and status=1", nativeQuery = true)
    Long countAllUploadsForSuperAdmin(Integer approvalStatus);

    @Query(value = "select upload.* from upload where is_sample_data=1 order by id asc",nativeQuery = true)
    List<Upload> getAllSampleUploads();

    @Modifying
    @Query(value = "delete from upload where is_sample_data=1 order by id desc",nativeQuery = true)
    void deleteAllSampleUploads();

    @Modifying
    @Query(value = "update upload set metadata_status=?2 where id=?1",nativeQuery = true)
    void updateMetadataStatus(Long id,Integer status);
    
    @Modifying
    @Query(value = "update user_vendor_mapping_object_mapping set qc_start_status=0,current_qc_level=1 where id=?1",nativeQuery = true)
    void updateUvmomQcStatus(Long uvmomId);
    
    
    @Modifying
    @Query(value = "update object set qc_start_status=0,current_qc_level=1 where id=?1",nativeQuery = true)
    void updateObjectQcStatus(Long objectId);
    
    @Modifying
    @Query(value = "update project set qc_start_status=0,current_qc_level=1 where id=?1",nativeQuery = true)
    void updateProjectQcStatus(Long projectId);

    @Query(value="select u.* from upload u, user_vendor_mapping_object_mapping uvmom, object o where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and o.project_id=?1",nativeQuery = true)
    List<Upload> findAllUploadByProject(Long projectId);

    @Query(value="select u.*  from upload_cqpm_batch_info ucbi,"
    		+ "upload u where ucbi.upload_id =u.id and ucbi.batch_number=?1 "
    		+ "and ucbi.show_to_qc=1 order by u.id limit ?3 offset ?2",nativeQuery = true)
    List<Upload> getUploadIdsInBatch(Long batchNumber,Integer pageNumber, Integer size);
    
    
    
    @Query(nativeQuery = true)
    List<UploadDTOForQC> getUploadDTOForQCPendingInBatch(Long batchNumber,Integer pageNumber, Integer size);
    
    
    
    
    @Query(value="select u.id as upload_id, o.id as object_id,u.user_vendor_mapping_object_mapping_id as uvmomvId, o.name as object_name   from object o,user_vendor_mapping_object_mapping uvom, "
    		+ " upload_cqpm_batch_info ucbi,upload u where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and  ucbi.upload_id =u.id and ucbi.batch_number=?1",nativeQuery = true)
    List<String[]> getUploadIdsInBatchDto(Long batchNumber);
    
    @Query(value="select u.* from upload_cqpm_batch_info ucbi,upload u where ucbi.batch_number=?1 and ucbi.upload_id=u.id",nativeQuery = true)
    List<Upload> getUploadByBatchNumber(Integer batchNumber);
    
    @Query(nativeQuery = true)
    List<UploadDTOForQC> getUploadDTOForQCInBatch(Integer batchNumber);
    
	/*
	 * @Query(nativeQuery = true) List<Upload> getUploadDTOForQCForNewBatch(Integer
	 * batchNumber);
	 */
    
    @Query(value="select count(u.id) from upload u,user_vendor_mapping_object_mapping uvmom "
    		+ "where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=?1",nativeQuery = true)
    Integer getNumberOfUploads(Long objectId);
    
    @Query(value="select count(u.id) from upload u,user_vendor_mapping_object_mapping uvmom "
    		+ "where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=?1 and u.approval_status=0",nativeQuery = true)
    Integer getNumberOfRejectedUploads(Long objectId);
    
    @Query(value="select count(u.id) from upload u,user_vendor_mapping_object_mapping uvmom "
    		+ "where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=?1 and u.approval_status=1",nativeQuery = true)
    Integer getNumberOfApprovedUploads(Long objectId);
    
    
    @Modifying
    @Query(value = "update upload set show_to_qc=?1, qc_status=?2,qc_done_by_id=?3,qc_start_date=?4,current_batch_number=?5 where id in (?6)",nativeQuery = true)
    void updateUploadQcStatus(Integer showToQc, Integer qcStatus, Long qcDoneBy, Instant qcStartDate, Long currentBatchNumber, List<Long> uploadId);
    
    
    @Query(value="select * from upload where user_vendor_mapping_object_mapping_id=?1 and object_key=?2",nativeQuery = true)
    Upload getUploadByFileNameUvmomId(Long uvmomId,String objectKey);
    

}
