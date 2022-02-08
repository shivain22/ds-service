package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasObject;
import com.ainnotate.aidas.domain.AidasProject;
import com.ainnotate.aidas.domain.AidasUpload;
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

    Long countAidasUploadByStatusFalse();
    Long countAidasUploadByStatusTrue();
    Long countAidasUploadByStatusIsNull();

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac, aidas_organisation ao1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.aidas_organisation_id=ao1.id and ao1.id=?1",nativeQuery = true)
    Long countAidasUploadByAidasOrganisation(Long aidasOrganisationId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac, aidas_organisation ao1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.aidas_organisation_id=ao1.id and ao1.id=?1 and au.status=0",nativeQuery = true)
    Long countAidasUploadByAidasOrganisationAndStatusFalse(Long aidasOrganisationId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac, aidas_organisation ao1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.aidas_organisation_id=ao1.id and ao1.id=?1 and au.status=1",nativeQuery = true)
    Long countAidasUploadByAidasOrganisationAndStatusTrue(Long aidasOrganisationId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac, aidas_organisation ao1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.aidas_organisation_id=ao1.id and ao1.id=?1 and au.status is null",nativeQuery = true)
    Long countAidasUploadByAidasOrganisationAndStatusIsNull(Long aidasOrganisationId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.id=?1",nativeQuery = true)
    Long countAidasUploadByAidasCustomer(Long aidasCustomerId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.id=?1  and au.status=0",nativeQuery = true)
    Long countAidasUploadByAidasCustomerStatusFalse(Long aidasCustomerId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.id=?1  and au.status=1",nativeQuery = true)
    Long countAidasUploadByAidasCustomerStatusTrue(Long aidasCustomerId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_object ao, aidas_project ap, aidas_customer ac where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_object_id=ao.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and ac.id=?1  and au.status is null",nativeQuery = true)
    Long countAidasUploadByAidasCustomerStatusIsNull(Long aidasCustomerId);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_user au1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=au1.id and au1.aidas_vendor_id=?1",nativeQuery = true)
    Long countAidasUploadByAidasVendor(Long aidasVendord);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_user au1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=au1.id and au1.aidas_vendor_id=?1  and au.status=0",nativeQuery = true)
    Long countAidasUploadByAidasVendorStatusFalse(Long aidasVendord);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_user au1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=au1.id and au1.aidas_vendor_id=?1  and au.status=1",nativeQuery = true)
    Long countAidasUploadByAidasVendorStatusTrue(Long aidasVendord);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom, aidas_user au1 where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=au1.id and au1.aidas_vendor_id=?1  and au.status is null",nativeQuery = true)
    Long countAidasUploadByAidasVendorStatusIsNull(Long aidasVendord);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1",nativeQuery = true)
    Long countAidasUploadByAidasVendorUser(Long aidasVendord);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1  and au.status=1  and au.status=0",nativeQuery = true)
    Long countAidasUploadByAidasVendorUserStatusFalse(Long aidasVendord);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1  and au.status=1",nativeQuery = true)
    Long countAidasUploadByAidasVendorUserStatusTrue(Long aidasVendord);

    @Query(value="select count(*) from aidas_upload au, aidas_user_obj_map auom where au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1  and au.status is null",nativeQuery = true)
    Long countAidasUploadByAidasVendorUserStatusIsNull(Long aidasVendord);
}
