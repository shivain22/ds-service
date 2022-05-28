package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the AidasObject entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasObjectRepository extends JpaRepository<AidasObject, Long> {

    Page<AidasObject> findAllByAidasProject_AidasCustomer_AidasOrganisation (Pageable pageable, AidasOrganisation aidasOrganisation);

    Page<AidasObject> findAllByAidasProject_AidasCustomer_AidasOrganisationAndAidasProject_Id (Pageable pageable, AidasOrganisation aidasOrganisation,Long projectId);

    Page<AidasObject> findAllByAidasProject_AidasCustomer(Pageable pageable, AidasCustomer aidasCustomer);

    Page<AidasObject> findAllByAidasProject_AidasCustomerAndAidasProject_Id(Pageable pageable, AidasCustomer aidasCustomer,Long projectId);

    Page<AidasObject> findAllByIdGreaterThan(Long id, Pageable page);

    Page<AidasObject> findAllByIdGreaterThanAndAidasProject_Id(Long id,Long projectId, Pageable page);

    @Query(value="select o.* from aidas_project p, aidas_object o,  aidas_user_obj_map am ,aidas_user u where  am.aidas_object_id=o.id and o.aidas_project_id=p.id and am.aidas_user_id=u.id and u.id= ?1",nativeQuery = true)
    Page<AidasObject> findAllObjectsByVendorUser(Pageable page, AidasUser aidasUser);

    @Query(value="select o.* from aidas_project p, aidas_object o,  aidas_user_obj_map am ,aidas_user u where  am.aidas_object_id=o.id and o.aidas_project_id=p.id and am.aidas_user_id=u.id and u.aidas_vendor_id= ?1",nativeQuery = true)
    Page<AidasObject> findAllObjectsByVendorAdmin(Pageable page, AidasVendor aidasVendor);

    @Query(value="select ao.* ,count(au.id) total_uploaded, SUM(CASE WHEN au.status = 1 THEN 1 ELSE 0 END) AS total_approved, SUM(CASE WHEN au.status = 0 THEN 1 ELSE 0 END) AS total_rejected, SUM(CASE WHEN au.status = 2 THEN 1 ELSE 0 END) AS total_pending from aidas_object ao left outer join  aidas_user_obj_map am on am.aidas_object_id=ao.id left outer join aidas_upload au on au.aidas_user_aidas_object_mapping_id=am.id where  am.aidas_user_id= ?1 and ao.aidas_project_id=?2 group by ao.id",nativeQuery = true)
    Page<AidasObject> findAllObjectsByVendorUserProject(Pageable pageable,AidasUser aidasUser, Long aidasProjectId);

    @Query(value="select o.* from aidas_project p, aidas_object o,  aidas_user_obj_map am ,aidas_user u where  am.aidas_object_id=o.id and o.aidas_project_id=p.id and am.aidas_user_id=u.id and u.aidas_vendor_id= ?1 and p.id=?2 and o.id>-1",nativeQuery = true)
    Page<AidasObject> findAllObjectsByVendorAdminProject(Pageable pageable, AidasVendor aidasVendor, Long aidasProjectId);

    @Query(value="select o.* from aidas_project p, aidas_object o,  aidas_user_obj_map am ,aidas_user u where  am.aidas_object_id=o.id and o.aidas_project_id=p.id and am.aidas_user_id=u.id and u.id= ?1 and p.id=?2",nativeQuery = true)
    Page<AidasObject> findAllObjectsByCustomerAdminProject(Pageable pageable,AidasCustomer aidasCustomer,Long aidasProjectId);

    @Query(value="select o.* from aidas_project p, aidas_object o,  aidas_user_obj_map am ,aidas_user u where  am.aidas_object_id=o.id and o.aidas_project_id=p.id and am.aidas_user_id=u.id and u.aidas_vendor_id= ?1 and p.id=?2",nativeQuery = true)
    Page<AidasObject> findAllObjectsByOrgAdminProject(Pageable pageable,AidasOrganisation aidasOrganisation,Long aidasProjectId);

    Long countAidasObjectByAidasProject_AidasCustomer_AidasOrganisation(AidasOrganisation aidasOrganisation);

    Long countAidasObjectByAidasProject_AidasCustomer(AidasCustomer aidasCustomer);

    @Query(value="select count(*) from (select ao.id,count(*) from aidas_user_obj_map auom, aidas_user au, aidas_object ao where auom.aidas_object_id=ao.id and auom.aidas_user_id=au.id and au.aidas_vendor_id=?1 group by ao.id)a",nativeQuery = true)
    Long countAidasObjectByVendor(Long aidasVendorId);

    @Query(value=" select count(*) from (select ao.id from aidas_user_obj_map auom, aidas_object ao where  auom.aidas_object_id=ao.id and auom.aidas_user_id=?1 group by ao.id)a",nativeQuery = true)
    Long countAidasProjectByVendorUser(Long aidasVendorUserId);

    @Query(value="select ao.id as objectId ,count(au.id) totalUploaded, SUM(CASE WHEN au.status = 1 THEN 1 ELSE 0 END) AS totalApproved, SUM(CASE WHEN au.status = 0 THEN 1 ELSE 0 END) AS totalRejected, SUM(CASE WHEN au.status = 2 THEN 1 ELSE 0 END) AS totalPending from aidas_object ao left  join  aidas_user_obj_map am on am.aidas_object_id=ao.id left  join aidas_upload au on au.aidas_user_aidas_object_mapping_id=am.id where  am.aidas_user_id= ?1 and ao.id=?2 group by ao.id " +
        " union " +
        " select ao.id as objectId ,count(au.id) totalUploaded, SUM(CASE WHEN au.status = 1 THEN 1 ELSE 0 END) AS totalApproved, SUM(CASE WHEN au.status = 0 THEN 1 ELSE 0 END) AS totalRejected, SUM(CASE WHEN au.status = 2 THEN 1 ELSE 0 END) AS totalPending from aidas_object ao right  join  aidas_user_obj_map am on am.aidas_object_id=ao.id right  join aidas_upload au on au.aidas_user_aidas_object_mapping_id=am.id where  am.aidas_user_id= ?1 and ao.id=?2 group by ao.id",nativeQuery = true)
    UploadDetail countUploadsByObjectAndUser(Long aidasUserId, Long aidasObjectId);


    @Query(value = "select  " +
        "ao.id as objectId, " +
        "count(au.id) as totalUploaded,  " +
        "SUM(CASE WHEN au.status = 1 THEN 1 ELSE 0 END) AS totalApproved,  " +
        "SUM(CASE WHEN au.status = 0 THEN 1 ELSE 0 END) AS totalRejected,  " +
        "SUM(CASE WHEN au.status = 2 THEN 1 ELSE 0 END) AS totalPending " +
        "from  " +
        "aidas_object ao " +
        "left  join  aidas_user_obj_map auom on auom.aidas_object_id=ao.id  " +
        "left  join aidas_upload au on au.aidas_user_aidas_object_mapping_id=auom.id  " +
        "left join aidas_project ap on ap.id=ao.aidas_project_id " +
        "left join aidas_customer ac on ac.id=ap.aidas_customer_id " +
        "left join aidas_organisation ao1 on ao1.id=ac.aidas_organisation_id " +
        "where " +
        "ao1.id=?2 and " +
        "ao.id=?1 " +
        "group by ao.id " +
        "union " +
        "select  " +
        "ao.id as objectId, " +
        "count(au.id) as totalUploaded,  " +
        "SUM(CASE WHEN au.status = 1 THEN 1 ELSE 0 END) AS totalApproved,  " +
        "SUM(CASE WHEN au.status = 0 THEN 1 ELSE 0 END) AS totalRejected,  " +
        "SUM(CASE WHEN au.status = 2 THEN 1 ELSE 0 END) AS totalPending " +
        "from  " +
        "aidas_object ao " +
        "right  join  aidas_user_obj_map auom on auom.aidas_object_id=ao.id  " +
        "right  join aidas_upload au on au.aidas_user_aidas_object_mapping_id=auom.id  " +
        "right join aidas_project ap on ap.id=ao.aidas_project_id " +
        "right join aidas_customer ac on ac.id=ap.aidas_customer_id " +
        "right join aidas_organisation ao1 on ao1.id=ac.aidas_organisation_id " +
        "where " +
        "ao1.id=?1 and " +
        "ao.id=?2 " +
        "group by ao.id",nativeQuery = true)
    UploadDetail countUploadsByObjectAndAidasOrganisation(Long aidasOrganisationId, Long aidasObjectId);

    @Query(value = "select  " +
        "ao.id as objectId, " +
        "count(au.id) as totalUploaded,  " +
        "SUM(CASE WHEN au.status = 1 THEN 1 ELSE 0 END) AS totalApproved,  " +
        "SUM(CASE WHEN au.status = 0 THEN 1 ELSE 0 END) AS totalRejected,  " +
        "SUM(CASE WHEN au.status = 2 THEN 1 ELSE 0 END) AS totalPending " +
        "from  " +
        "aidas_object ao " +
        "left  join  aidas_user_obj_map auom on auom.aidas_object_id=ao.id  " +
        "left  join aidas_upload au on au.aidas_user_aidas_object_mapping_id=auom.id  " +
        "left join aidas_project ap on ap.id=ao.aidas_project_id " +
        "left join aidas_customer ac on ac.id=ap.aidas_customer_id " +
        "where " +
        "ac.id=?2 and " +
        "ao.id=?1 " +
        "group by ao.id " +
        "union " +
        "select  " +
        "ao.id as objectId, " +
        "count(au.id) as totalUploaded,  " +
        "SUM(CASE WHEN au.status = 1 THEN 1 ELSE 0 END) AS totalApproved,  " +
        "SUM(CASE WHEN au.status = 0 THEN 1 ELSE 0 END) AS totalRejected,  " +
        "SUM(CASE WHEN au.status = 2 THEN 1 ELSE 0 END) AS totalPending " +
        "from  " +
        "aidas_object ao " +
        "right  join  aidas_user_obj_map auom on auom.aidas_object_id=ao.id  " +
        "right  join aidas_upload au on au.aidas_user_aidas_object_mapping_id=auom.id  " +
        "right join aidas_project ap on ap.id=ao.aidas_project_id " +
        "right join aidas_customer ac on ac.id=ap.aidas_customer_id " +
        "where " +
        "ac.id=?1 and " +
        "ao.id=?2 " +
        "group by ao.id",nativeQuery = true)
    UploadDetail countUploadsByObjectAndAidasCustomer(Long aidasCustomerId, Long aidasObjectId);

    @Query(value = "select  " +
        "ao.id as objectId, " +
        "count(au.id) as totalUploaded,  " +
        "SUM(CASE WHEN au.status = 1 THEN 1 ELSE 0 END) AS totalApproved,  " +
        "SUM(CASE WHEN au.status = 0 THEN 1 ELSE 0 END) AS totalRejected,  " +
        "SUM(CASE WHEN au.status = 2 THEN 1 ELSE 0 END) AS totalPending " +
        "from  " +
        "aidas_object ao " +
        "left  join  aidas_user_obj_map auom on auom.aidas_object_id=ao.id  " +
        "left  join aidas_upload au on au.aidas_user_aidas_object_mapping_id=auom.id  " +
        "left join aidas_user au1 on au1.id=auom.aidas_user_id " +
        "where " +
        "au1.id=?2 and " +
        "ao.id=?1 " +
        "group by ao.id " +
        "union " +
        "select  " +
        "ao.id as objectId, " +
        "count(au.id) as totalUploaded,  " +
        "SUM(CASE WHEN au.status = 1 THEN 1 ELSE 0 END) AS totalApproved,  " +
        "SUM(CASE WHEN au.status = 0 THEN 1 ELSE 0 END) AS totalRejected,  " +
        "SUM(CASE WHEN au.status = 2 THEN 1 ELSE 0 END) AS totalPending " +
        "from  " +
        "aidas_object ao " +
        "right  join  aidas_user_obj_map auom on auom.aidas_object_id=ao.id  " +
        "right  join aidas_upload au on au.aidas_user_aidas_object_mapping_id=auom.id  " +
        "right join aidas_user au1 on au1.id=auom.aidas_user_id " +
        "where " +
        "au1.id=?1 and " +
        "ao.id=?2 " +
        "group by ao.id",nativeQuery = true)
    UploadDetail countUploadsByObjectAndAidasVendor(Long aidasVendorId, Long aidasObjectId);

    @Query(value = "select  " +
        "ao.id as objectId, " +
        "count(au.id) as totalUploaded,  " +
        "SUM(CASE WHEN au.status = 1 THEN 1 ELSE 0 END) AS totalApproved,  " +
        "SUM(CASE WHEN au.status = 0 THEN 1 ELSE 0 END) AS totalRejected,  " +
        "SUM(CASE WHEN au.status = 2 THEN 1 ELSE 0 END) AS totalPending " +
        "from  " +
        "aidas_object ao " +
        "left  join  aidas_user_obj_map auom on auom.aidas_object_id=ao.id  " +
        "left  join aidas_upload au on au.aidas_user_aidas_object_mapping_id=auom.id  " +
        "where " +
        "ao.id=?1 " +
        "group by ao.id " +
        "union " +
        "select  " +
        "ao.id as objectId, " +
        "count(au.id) as totalUploaded,  " +
        "SUM(CASE WHEN au.status = 1 THEN 1 ELSE 0 END) AS totalApproved,  " +
        "SUM(CASE WHEN au.status = 0 THEN 1 ELSE 0 END) AS totalRejected,  " +
        "SUM(CASE WHEN au.status = 2 THEN 1 ELSE 0 END) AS totalPending " +
        "from  " +
        "aidas_object ao " +
        "right  join  aidas_user_obj_map auom on auom.aidas_object_id=ao.id  " +
        "right  join aidas_upload au on au.aidas_user_aidas_object_mapping_id=auom.id  " +
        "where " +
        "ao.id=?1 " +
        "group by ao.id",nativeQuery = true)
    UploadDetail countUploadsByObjectAndAidasAdmin(Long aidasObjectId);

    @Query(value = "select count(*) from aidas_object where id>0", nativeQuery = true)
    Long countAllObjectsForSuperAdmin();

}
