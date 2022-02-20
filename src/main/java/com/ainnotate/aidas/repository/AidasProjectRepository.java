package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the AidasProject entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasProjectRepository extends JpaRepository<AidasProject, Long> {

    Page<AidasProject> findAllByAidasCustomer_AidasOrganisation(Pageable page, AidasOrganisation organisation);
    Page<AidasProject> findAllByAidasCustomer(Pageable page,AidasCustomer aidasCustomer);

    @Query(value="select p.* from aidas_project p, aidas_object o,  aidas_user_obj_map am  where  am.aidas_object_id=o.id and o.aidas_project_id=p.id and am.aidas_user_id= ?1 and p.id>-1 group by p.id ",nativeQuery = true)
    Page<AidasProject> findAllProjectsByVendorUser(Pageable page, AidasUser aidasUser);

    @Query(value="select p.* from aidas_project p, aidas_object o,  aidas_user_obj_map am ,aidas_user u where  am.aidas_object_id=o.id and o.aidas_project_id=p.id and am.aidas_user_id=u.id and u.aidas_vendor_id= ?1",nativeQuery = true)
    Page<AidasProject> findAllProjectsByVendorAdmin(Pageable page, AidasVendor aidasVendor);

    @Query(value="select p.* from aidas_project p, aidas_object o,  aidas_user_obj_map am ,aidas_user u where  am.aidas_object_id=o.id and o.aidas_project_id=p.id and am.aidas_user_id=u.id and u.id= ?1 and p.id=?2",nativeQuery = true)
    Optional<AidasProject> findAllProjectsByVendorUserProject(AidasUser aidasUser, Long aidasProjectId);

    @Query(value="select p.* from aidas_project p, aidas_object o,  aidas_user_obj_map am ,aidas_user u where  am.aidas_object_id=o.id and o.aidas_project_id=p.id and am.aidas_user_id=u.id and u.aidas_vendor_id= ?1 and p.id=?2",nativeQuery = true)
    Optional<AidasProject> findAllProjectsByVendorAdminProject(AidasVendor aidasVendor, Long aidasProjectId);

    @Query(value="select p.* from aidas_project p, aidas_object o,  aidas_user_obj_map am ,aidas_user u where  am.aidas_object_id=o.id and o.aidas_project_id=p.id and am.aidas_user_id=u.id and u.id= ?1 and p.id=?2",nativeQuery = true)
    Optional<AidasProject> findAllProjectsByCustomerAdminProject(AidasCustomer aidasCustomer,Long aidasProjectId);

    @Query(value="select p.* from aidas_project p where  p.aidas_customer_id=?1 and p.id=?2",nativeQuery = true)
    Optional<AidasProject> findAllProjectsByCustomerAdminProject(Long aidasCustomerId,Long projectId);

    @Query(value="select p.* from aidas_project p, aidas_object o,  aidas_user_obj_map am ,aidas_user u where  am.aidas_object_id=o.id and o.aidas_project_id=p.id and am.aidas_user_id=u.id and u.aidas_vendor_id= ?1 and p.id=?2",nativeQuery = true)
    Optional<AidasProject> findAllProjectsByOrgAdminProject(AidasOrganisation aidasOrganisation,Long aidasProjectId);

    Page<AidasProject> findAllByIdGreaterThan(Long id, Pageable page);

    Long countAidasProjectByAidasCustomer_AidasOrganisation(AidasOrganisation aidasOrganisation);

    Long countAidasProjectByAidasCustomer(AidasCustomer aidasCustomer);

    @Query(value = "select count(*)from (select ap.id,count(*) from aidas_user_obj_map auom, aidas_user au, aidas_object ao, aidas_project ap where auom.aidas_object_id=ao.id and auom.aidas_user_id=au.id and ao.aidas_project_id=ap.id and au.aidas_vendor_id=?1 group by ap.id) a", nativeQuery = true)
    Long countAidasProjectByVendor(Long vendorId);

    @Query(value="select count(*) from (select ap.id from aidas_upload au,aidas_user_obj_map auom, aidas_object ao, aidas_project ap where ao.aidas_project_id=ap.id and auom.aidas_object_id=ao.id and au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1 group by ap.id)a;",nativeQuery = true)
    Long countAidasProjectByVendorUser(Long aidasVendorUserId);

    @Query(value = "select ap.id as projectId, count(au.id) totalUploaded,SUM(CASE WHEN au.status = 1 THEN 1 ELSE 0 END) AS totalApproved,SUM(CASE WHEN au.status = 0 THEN 1 ELSE 0 END) AS totalRejected,SUM(CASE WHEN au.status = 2 THEN 1 ELSE 0 END) AS totalPending from aidas_project ap left  join aidas_object ao on ao.aidas_project_id=ap.id left  join aidas_user_obj_map am on am.aidas_object_id=ao.id left  join aidas_upload au on au.aidas_user_aidas_object_mapping_id=am.id where am.aidas_user_id= ?2 and  ap.id=?1 group by ap.id " +
        " union " +
        " select ap.id as projectId, count(au.id) totalUploaded,SUM(CASE WHEN au.status = 1 THEN 1 ELSE 0 END) AS totalApproved,SUM(CASE WHEN au.status = 0 THEN 1 ELSE 0 END) AS totalRejected,SUM(CASE WHEN au.status = 2 THEN 1 ELSE 0 END) AS totalPending from aidas_project ap right  join aidas_object ao on ao.aidas_project_id=ap.id right  join aidas_user_obj_map am on am.aidas_object_id=ao.id right  join aidas_upload au on au.aidas_user_aidas_object_mapping_id=am.id where am.aidas_user_id= ?2 and  ap.id=?1 group by ap.id", nativeQuery = true)
    UploadDetail countUploadsByProjectAndUser(Long projectId, Long userId);

}
