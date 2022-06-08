package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasObject;
import com.ainnotate.aidas.domain.AidasProject;
import com.ainnotate.aidas.domain.AidasUpload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


import java.util.List;

/**
 * Spring Data SQL repository for the AidasUpload entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasUploadRepository extends JpaRepository<AidasUpload, Long> {

    Integer countAidasUploadByAidasUserAidasObjectMapping_AidasObject(AidasObject aidasObject);

    @Query(value = "select * from aidas_upload au, aidas_user_obj_map auom, aidas_object ao where auom.aidas_object_id=ao.id and ao.aidas_project_id=?1 and au.approval_status=?2",nativeQuery = true)
    List<AidasUpload>getAidasUploadsByProject(Long aidasProjectId, Integer approvalStatus);

    @Query(value = "select * from aidas_upload au, aidas_user_obj_map auom where auom.aidas_object_id=?1 and au.approval_status=?2",nativeQuery = true)
    List<AidasUpload>getAidasUploadsByObject(Long aidasProjectId, Integer approvalStatus);

    @Query(value="select count(*) from aidas_upload  au", nativeQuery = true)
    Long countAidasUploads();

    @Query(value="select count(*) from aidas_upload  au where au.approval_status=?1", nativeQuery = true)
    Long countAidasUploads(Integer approvalStatus);


    //For org
    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac, aidas_organisation ao1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.aidas_organisation_id=ao1.id and ao1.id=?1",nativeQuery = true)
    Long countAidasUploadByAidasOrganisation(Long aidasOrganisationId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac, aidas_organisation ao1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.aidas_organisation_id=?1 and au.approval_status=?2",nativeQuery = true)
    Long countAidasUploadByAidasOrganisation(Long aidasOrganisationId,Integer approvalStatus);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac, aidas_organisation ao1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.aidas_organisation_id=ao1.id and ao1.id=?1",
        countQuery = "select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac, aidas_organisation ao1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.aidas_organisation_id=ao1.id and ao1.id=?1",
        nativeQuery = true)
    Page<AidasUpload> findAidasUploadByAidasOrganisation(Long aidasOrganisationId, Pageable pageable);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac, aidas_organisation ao1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.aidas_organisation_id=ao1.id and ao1.id=?1 ",nativeQuery = true)
    List<AidasUpload> findAidasUploadByAidasOrganisation(Long aidasOrganisationId);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac, aidas_organisation ao1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.aidas_organisation_id=ao1.id and ao1.id=?1 and au.approval_status=?2",nativeQuery = true)
    List<AidasUpload> findAidasUploadByAidasOrganisation(Long aidasOrganisationId,Integer approvalStatus);


    //For cust
    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.id=?1",nativeQuery = true)
    Long countAidasUploadByAidasCustomer(Long aidasCustomerId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.id=?1  and au.approval_status=?2",nativeQuery = true)
    Long countAidasUploadByAidasCustomer(Long aidasCustomerId,Integer approvalStatus);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.id=?1",nativeQuery = true)
    Page<AidasUpload> findAidasUploadByAidasCustomer(Long aidasCustomerId,Pageable pageable);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.id=?1",nativeQuery = true)
    List<AidasUpload> findAidasUploadByAidasCustomer(Long aidasCustomerId);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.id=?1  and au.approval_status=?2",nativeQuery = true)
    List<AidasUpload> findAidasUploadByAidasCustomer(Long aidasCustomerId,Integer approvalStatus);


    //For Vendor
    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_user au1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=au1.id and au1.aidas_vendor_id=?1",nativeQuery = true)
    Long countAidasUploadByAidasVendor(Long aidasVendorId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_user au1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=au1.id and au1.aidas_vendor_id=?1 and au.approval_status=?2",nativeQuery = true)
    Long countAidasUploadByAidasVendor(Long aidasVendorId,Integer approvalStatus);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom, aidas_user au1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=au1.id and au1.aidas_vendor_id=?1",nativeQuery = true)
    Page<AidasUpload> findAidasUploadByAidasVendor(Long aidasVendorId,Pageable pageable);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom, aidas_user au1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=au1.id and au1.aidas_vendor_id=?1",nativeQuery = true)
    List<AidasUpload> findAidasUploadByAidasVendor(Long aidasVendorId);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom, aidas_user au1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=au1.id and au1.aidas_vendor_id=?1  and au.approval_status=?2",nativeQuery = true)
    List<AidasUpload> findAidasUploadByAidasVendor(Long aidasVendorId,Integer approvalStatus);


//For vendor user
    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1",nativeQuery = true)
    Long countAidasUploadByAidasVendorUser(Long aidasVendorId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1 and au.approval_status=?2",nativeQuery = true)
    Long countAidasUploadByAidasVendorUser(Long aidasVendorId,Integer approvalStatus);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1 and auom.aidas_object_id=?2",
        countQuery = "select count(*) from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1 and auom.aidas_object_id=?2",
        nativeQuery = true)
    Page<AidasUpload> findAllByUserAndObject(Long userId, Long objectId,Pageable pageable);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1 and auom.aidas_object_id=?2 and au.approval_status=?3",
        countQuery = "select count(*) from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1 and auom.aidas_object_id=?2 and au.approval_status=?3",
        nativeQuery = true)
    Page<AidasUpload> findAllByUserAndObject(Long userId, Long objectId,Integer approvalStatus,Pageable pageable);



    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom,aidas_object ao, aidas_project ap where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.id=?2 and auom.aidas_user_id=?1",nativeQuery = true)
    Page<AidasUpload> findAllByUserAndProject(Long userId, Long projectId,Pageable pageable);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom,aidas_object ao, aidas_project ap where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.id=?2 and auom.aidas_user_id=?1 and au.approval_status=?3",nativeQuery = true)
    Page<AidasUpload> findAllByUserAndProject(Long userId, Long projectId,Integer approvalStatus,Pageable pageable);



    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id  and auom.aidas_user_id=?1",nativeQuery = true)
    Page<AidasUpload> findAllByUser(Long userId,Pageable pageable);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id  and auom.aidas_user_id=?1 and au.approval_status=?2",nativeQuery = true)
    Page<AidasUpload> findAllByUser(Long userId,Integer approvalStatus,Pageable pageable);


    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom,aidas_object ao, aidas_project ap where au.status=2 and au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.id=?2 and auom.aidas_user_id=?1",nativeQuery = true)
    List<AidasUpload> findAllByUserAndProjectAllForMetadata(Long userId, Long projectId);

    @Query(value="select * from aidas_upload where qc_done_by is null and qc_status=0 and qc_end_date is null and qc_start_date is null limit 1",nativeQuery = true)
    AidasUpload findTopByQcNotDoneYet();

    @Query(value="select * from aidas_upload where qc_done_by is not null and qc_end_date is null and qc_start_date is not null and qc_status=0 and TIMEstampDIFF(SECOND,qc_start_date,now())>(select value from aidas_app_properties where name='qc_clean_up_time')",nativeQuery = true)
    List<AidasUpload> findUploadsHeldByQcForMoreThan10Mins();

    @Query(value = "select count(*) from aidas_upload where id>0", nativeQuery = true)
    Long countAllUploadsForSuperAdmin();

    @Query(value = "select count(*) from aidas_upload where id>0 and approval_status=?1", nativeQuery = true)
    Long countAllUploadsForSuperAdmin(Integer approvalStatus);

}
