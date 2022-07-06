package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.Organisation;
import com.ainnotate.aidas.domain.Upload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

/**
 * Spring Data SQL repository for the AidasUpload entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface UploadRepository extends JpaRepository<Upload, Long> {

    @Query(value = "select count(*) from upload au, object ao,user_vendor_mapping_object_mapping auavmaom where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.object_id=ao.id and ao.id=?1 and ao.status=1 and ao.is_dummy=0 ",nativeQuery = true)
    Integer countAidasUploadByAidasUserAidasObjectMapping_AidasObject(Long aidasObjectId);

    @Query(value = "select au.* from upload au, user_vendor_mapping_object_mapping auavmaom,object ao where au.user_vendor_mapping_object_mapping_id=auavmaom.id and  auavmaom.object_id=ao.id and ao.project_id=?1 and au.approval_status=?2",nativeQuery = true)
    List<Upload>getAidasUploadsByProject(Long aidasProjectId, Integer approvalStatus);

    @Query(value = "select au.* from upload au, user_vendor_mapping_object_mapping auavmaom,object ao where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.object_id=ao.id and ao.project_id=?1",nativeQuery = true)
    List<Upload>getAidasUploadsByProject(Long aidasProjectId);

    @Query(value = "select au.* from upload au, user_vendor_mapping_object_mapping auavmaom where au.user_vendor_mapping_object_mapping_id=auavmaom.id and  auavmaom.object_id=?1 and au.approval_status=?2",nativeQuery = true)
    List<Upload>getAidasUploadsByObject(Long aidasProjectId, Integer approvalStatus);

    @Query(value = "select au.* from upload au, user_vendor_mapping_object_mapping auavmaom where au.user_vendor_mapping_object_mapping_id=auavmaom.id and  auavmaom.object_id=?1 ",nativeQuery = true)
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



    @Query(value="select * from upload au, user_vendor_mapping_object_mapping auavmaom,object ao, project ap,user_vendor_mapping auavm where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.object_id=ao.id and ao.project_id=ap.id and ap.id=?2 and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=?1",nativeQuery = true)
    Page<Upload> findAllByUserAndProject(Long userId, Long projectId, Pageable pageable);

    @Query(value="select * from upload au, user_vendor_mapping_object_mapping auavmaom,object ao, project ap,user_vendor_mapping auavm where au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.object_id=ao.id and ao.project_id=ap.id and ap.id=?2 and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=?1 and au.approval_status=?3",nativeQuery = true)
    Page<Upload> findAllByUserAndProject(Long userId, Long projectId, Integer approvalStatus, Pageable pageable);



    @Query(value="select * from upload au, user_vendor_mapping_object_mapping auavmaom,user_vendor_mapping auavm where au.user_vendor_mapping_object_mapping_id=auavmaom.id  and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=?1",nativeQuery = true)
    Page<Upload> findAllByUser(Long userId, Pageable pageable);

    @Query(value="select * from upload au, user_vendor_mapping_object_mapping auavmaom,user_vendor_mapping auavm where au.user_vendor_mapping_object_mapping_id=auavmaom.id  and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=?1 and au.approval_status=?2",nativeQuery = true)
    Page<Upload> findAllByUser(Long userId, Integer approvalStatus, Pageable pageable);


    @Query(value="select au.* from upload au, user_vendor_mapping_object_mapping auavmaom,object ao, project ap,user_vendor_mapping auavm where au.approval_status=2 and au.user_vendor_mapping_object_mapping_id=auavmaom.id and auavmaom.object_id=ao.id and ao.project_id=ap.id and ap.id=?2 and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=?1 and au.metadata_status=0",nativeQuery = true)
    List<Upload> findAllByUserAndProjectAllForMetadata(Long userId, Long projectId);

    @Query(value="select u.* from upload u, user_vendor_mapping_object_mapping uvmom, object o where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and o.project_id=?1 and u.qc_done_by_id is null and u.qc_status=0 and u.qc_end_date is null and qc_start_date is null and u.metadata_status=1  limit 1",nativeQuery = true)
    Upload findTopByQcNotDoneYet(Long projectId);

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

}
