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

    List<AidasUpload> getAidasUploadsByAidasUserAidasObjectMapping_AidasObjectAndStatusIsTrue(AidasObject aidasObject);

    List<AidasUpload> getAidasUploadsByAidasUserAidasObjectMapping_AidasObjectAndStatusIsFalse(AidasObject aidasObject);

    List<AidasUpload> getAidasUploadsByAidasUserAidasObjectMapping_AidasObjectAndStatusIsNull(AidasObject aidasObject);

    List<AidasUpload> getAidasUploadsByAidasUserAidasObjectMapping_AidasObject_AidasProjectAndStatusIsTrue(AidasProject aidasProject);

    List<AidasUpload> getAidasUploadsByAidasUserAidasObjectMapping_AidasObject_AidasProjectAndStatusIsFalse(AidasProject aidasProject);

    List<AidasUpload> getAidasUploadsByAidasUserAidasObjectMapping_AidasObject_AidasProjectAndStatusIsNull(AidasProject aidasProject);

    @Query(value="select count(*) from aidas_upload  au where au.status=0", nativeQuery = true)
    Long countAidasUploadByStatusFalse();
    @Query(value="select count(*) from aidas_upload  au where au.status=1", nativeQuery = true)
    Long countAidasUploadByStatusTrue();
    @Query(value="select count(*) from aidas_upload  au where au.status=2", nativeQuery = true)
    Long countAidasUploadByStatusIsNull();



    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac, aidas_organisation ao1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.aidas_organisation_id=ao1.id and ao1.id=?1",nativeQuery = true)
    Long countAidasUploadByAidasOrganisation(Long aidasOrganisationId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac, aidas_organisation ao1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.aidas_organisation_id=ao1.id and ao1.id=?1 and au.status=0",nativeQuery = true)
    Long countAidasUploadByAidasOrganisationAndStatusFalse(Long aidasOrganisationId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac, aidas_organisation ao1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.aidas_organisation_id=ao1.id and ao1.id=?1 and au.status=1",nativeQuery = true)
    Long countAidasUploadByAidasOrganisationAndStatusTrue(Long aidasOrganisationId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac, aidas_organisation ao1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.aidas_organisation_id=ao1.id and ao1.id=?1 and au.status =2",nativeQuery = true)
    Long countAidasUploadByAidasOrganisationAndStatusIsNull(Long aidasOrganisationId);


    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac, aidas_organisation ao1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.aidas_organisation_id=ao1.id and ao1.id=?1",
        countQuery = "select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac, aidas_organisation ao1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.aidas_organisation_id=ao1.id and ao1.id=?1",
        nativeQuery = true)
    Page<AidasUpload> findAidasUploadByAidasOrganisation(Long aidasOrganisationId, Pageable pageable);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac, aidas_organisation ao1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.aidas_organisation_id=ao1.id and ao1.id=?1 and au.status=0",nativeQuery = true)
    List<AidasUpload> findAidasUploadByAidasOrganisationAndStatusFalse(Long aidasOrganisationId);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac, aidas_organisation ao1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.aidas_organisation_id=ao1.id and ao1.id=?1 and au.status=1",nativeQuery = true)
    List<AidasUpload> findAidasUploadByAidasOrganisationAndStatusTrue(Long aidasOrganisationId);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac, aidas_organisation ao1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.aidas_organisation_id=ao1.id and ao1.id=?1 and au.status =2",nativeQuery = true)
    List<AidasUpload> findAidasUploadByAidasOrganisationAndStatusIsNull(Long aidasOrganisationId);



    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.id=?1",nativeQuery = true)
    Long countAidasUploadByAidasCustomer(Long aidasCustomerId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.id=?1  and au.status=0",nativeQuery = true)
    Long countAidasUploadByAidasCustomerStatusFalse(Long aidasCustomerId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.id=?1  and au.status=1",nativeQuery = true)
    Long countAidasUploadByAidasCustomerStatusTrue(Long aidasCustomerId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.id=?1  and au.status =2",nativeQuery = true)
    Long countAidasUploadByAidasCustomerStatusIsNull(Long aidasCustomerId);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.id=?1",nativeQuery = true)
    Page<AidasUpload> findAidasUploadByAidasCustomer(Long aidasCustomerId,Pageable pageable);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.id=?1  and au.status=0",nativeQuery = true)
    List<AidasUpload> findAidasUploadByAidasCustomerStatusFalse(Long aidasCustomerId);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.id=?1  and au.status=1",nativeQuery = true)
    List<AidasUpload> findAidasUploadByAidasCustomerStatusTrue(Long aidasCustomerId);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.id=?1  and au.status =2",nativeQuery = true)
    List<AidasUpload> findAidasUploadByAidasCustomerStatusIsNull(Long aidasCustomerId);




    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_user au1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=au1.id and au1.aidas_vendor_id=?1",nativeQuery = true)
    Long countAidasUploadByAidasVendor(Long aidasVendorId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_user au1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=au1.id and au1.aidas_vendor_id=?1  and au.status=0",nativeQuery = true)
    Long countAidasUploadByAidasVendorStatusFalse(Long aidasVendorId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_user au1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=au1.id and au1.aidas_vendor_id=?1  and au.status=1",nativeQuery = true)
    Long countAidasUploadByAidasVendorStatusTrue(Long aidasVendorId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_user au1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=au1.id and au1.aidas_vendor_id=?1  and au.status =2",nativeQuery = true)
    Long countAidasUploadByAidasVendorStatusIsNull(Long aidasVendorId);


    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom, aidas_user au1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=au1.id and au1.aidas_vendor_id=?1",nativeQuery = true)
    Page<AidasUpload> findAidasUploadByAidasVendor(Long aidasVendorId,Pageable pageable);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom, aidas_user au1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=au1.id and au1.aidas_vendor_id=?1  and au.status=0",nativeQuery = true)
    List<AidasUpload> findAidasUploadByAidasVendorStatusFalse(Long aidasVendorId);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom, aidas_user au1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=au1.id and au1.aidas_vendor_id=?1  and au.status=1",nativeQuery = true)
    List<AidasUpload> findAidasUploadByAidasVendorStatusTrue(Long aidasVendorId);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom, aidas_user au1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=au1.id and au1.aidas_vendor_id=?1  and au.status =2",nativeQuery = true)
    List<AidasUpload> findAidasUploadByAidasVendorStatusIsNull(Long aidasVendorId);




    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1",nativeQuery = true)
    Long countAidasUploadByAidasVendorUser(Long aidasVendorId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1  and  au.status=0",nativeQuery = true)
    Long countAidasUploadByAidasVendorUserStatusFalse(Long aidasVendorId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1  and au.status=1",nativeQuery = true)
    Long countAidasUploadByAidasVendorUserStatusTrue(Long aidasVendorId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1  and au.status =2",nativeQuery = true)
    Long countAidasUploadByAidasVendorUserStatusIsNull(Long aidasVendorId);


    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1 and auom.aidas_object_id=?2",
        countQuery = "select count(*) from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1 and auom.aidas_object_id=?2",
        nativeQuery = true)
    Page<AidasUpload> findAllByUserAndObjectAll(Long userId, Long objectId,Pageable pageable);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1 and auom.aidas_object_id=?2 and au.status=1",
        countQuery = "select count(*) from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1 and auom.aidas_object_id=?2 and au.status=1",
        nativeQuery = true)
    Page<AidasUpload> findAllByUserAndObjectApproved(Long userId, Long objectId,Pageable pageable);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1 and auom.aidas_object_id=?2 and au.status=0",
        countQuery = "select count(*) from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1 and auom.aidas_object_id=?2 and au.status=0",
        nativeQuery = true)
    Page<AidasUpload> findAllByUserAndObjectRejected(Long userId, Long objectId,Pageable pageable);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1 and auom.aidas_object_id=?2 and au.status=2",
        countQuery = "select count(*) from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1 and auom.aidas_object_id=?2 and au.status=2",
        nativeQuery = true)
    Page<AidasUpload> findAllByUserAndObjectPending(Long userId, Long objectId,Pageable pageable);


    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom,aidas_object ao, aidas_project ap where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.id=?2 and auom.aidas_user_id=?1",nativeQuery = true)
    Page<AidasUpload> findAllByUserAndProjectAll(Long userId, Long projectId,Pageable pageable);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom,aidas_object ao, aidas_project ap where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.id=?2 and auom.aidas_user_id=?1 and au.status=1",nativeQuery = true)
    Page<AidasUpload> findAllByUserAndProjectApproved(Long userId, Long projectId,Pageable pageable);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom,aidas_object ao, aidas_project ap where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.id=?2 and auom.aidas_user_id=?1 and au.status=0",nativeQuery = true)
    Page<AidasUpload> findAllByUserAndProjectRejected(Long userId, Long projectId,Pageable pageable);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom,aidas_object ao, aidas_project ap where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.id=?2 and auom.aidas_user_id=?1 and au.status=2",nativeQuery = true)
    Page<AidasUpload> findAllByUserAndProjectPending(Long userId, Long projectId,Pageable pageable);


    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id  and auom.aidas_user_id=?1",nativeQuery = true)
    Page<AidasUpload> findAllByUserAll(Long userId,Pageable pageable);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id  and auom.aidas_user_id=?1 and au.status=1",nativeQuery = true)
    Page<AidasUpload> findAllByUserApproved(Long userId,Pageable pageable);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id  and auom.aidas_user_id=?1 and au.status=0",nativeQuery = true)
    Page<AidasUpload> findAllByUserRejected(Long userId,Pageable pageable);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id  and auom.aidas_user_id=?1 and au.status=2",nativeQuery = true)
    Page<AidasUpload> findAllByUserPending(Long userId,Pageable pageable);

    @Query(value="select * from aidas_upload au, aidas_user_obj_map auom,aidas_object ao, aidas_project ap where au.status=2 and au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.id=?2 and auom.aidas_user_id=?1",nativeQuery = true)
    List<AidasUpload> findAllByUserAndProjectAllForMetadata(Long userId, Long projectId);

    @Query(value="select * from aidas_upload where qc_done_by is null and qc_status=0 and qc_end_date is null and qc_start_date is null limit 1",nativeQuery = true)
    AidasUpload findTopByQcNotDoneYet();

    @Query(value="select * from aidas_upload where qc_done_by is not null and qc_end_date is null and qc_start_date is not null and qc_status=0 and TIMEstampDIFF(SECOND,qc_start_date,now())>(select value from aidas_app_properties where name='qc_clean_up_time')",nativeQuery = true)
    List<AidasUpload> findUploadsHeldByQcForMoreThan10Mins();

    @Query(value = "select count(*) from aidas_upload where id>0", nativeQuery = true)
    Long countAllUploadsForSuperAdmin();

    @Query(value = "select count(*) from aidas_upload where id>0 and status=1", nativeQuery = true)
    Long countAllApprovedUploadsForSuperAdmin();

    @Query(value = "select count(*) from aidas_upload where id>0 and status=0", nativeQuery = true)
    Long countAllRejectedUploadsForSuperAdmin();

    @Query(value = "select count(*) from aidas_upload where id>0 and status=2", nativeQuery = true)
    Long countAllPendingUploadsForSuperAdmin();
}
