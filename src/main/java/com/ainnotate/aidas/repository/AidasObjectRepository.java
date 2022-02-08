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

    @Query(value="select o.* from aidas_project p, aidas_object o,  aidas_user_obj_map am ,aidas_user u where  am.aidas_object_id=o.id and o.aidas_project_id=p.id and am.aidas_user_id=u.id and u.id= ?1 and p.id=?2",nativeQuery = true)
    Page<AidasObject> findAllObjectsByVendorUserProject(Pageable pageable,AidasUser aidasUser, Long aidasProjectId);

    @Query(value="select o.* from aidas_project p, aidas_object o,  aidas_user_obj_map am ,aidas_user u where  am.aidas_object_id=o.id and o.aidas_project_id=p.id and am.aidas_user_id=u.id and u.aidas_vendor_id= ?1 and p.id=?2",nativeQuery = true)
    Page<AidasObject> findAllObjectsByVendorAdminProject(Pageable pageable, AidasVendor aidasVendor, Long aidasProjectId);

    @Query(value="select o.* from aidas_project p, aidas_object o,  aidas_user_obj_map am ,aidas_user u where  am.aidas_object_id=o.id and o.aidas_project_id=p.id and am.aidas_user_id=u.id and u.id= ?1 and p.id=?2",nativeQuery = true)
    Page<AidasObject> findAllObjectsByCustomerAdminProject(Pageable pageable,AidasCustomer aidasCustomer,Long aidasProjectId);

    @Query(value="select o.* from aidas_project p, aidas_object o,  aidas_user_obj_map am ,aidas_user u where  am.aidas_object_id=o.id and o.aidas_project_id=p.id and am.aidas_user_id=u.id and u.aidas_vendor_id= ?1 and p.id=?2",nativeQuery = true)
    Page<AidasObject> findAllObjectsByOrgAdminProject(Pageable pageable,AidasOrganisation aidasOrganisation,Long aidasProjectId);

    Long countAidasObjectByAidasProject_AidasCustomer_AidasOrganisation(AidasOrganisation aidasOrganisation);

    Long countAidasObjectByAidasProject_AidasCustomer(AidasCustomer aidasCustomer);

    @Query(value="select count(*) from (select ao.id,count(*) from aidas_user_obj_map auom, aidas_user au, aidas_object ao where auom.aidas_object_id=ao.id and auom.aidas_user_id=au.id and au.aidas_vendor_id=?1 group by ao.id)a",nativeQuery = true)
    Long countAidasObjectByVendor(Long aidasVendorId);

    @Query(value="select count(*) from (select ao.id from aidas_upload au,aidas_user_obj_map auom, aidas_object ao where  auom.aidas_object_id=ao.id and au.aidas_user_aidas_object_mapping_id=auom.id and auom.aidas_user_id=?1 group by ao.id)a;",nativeQuery = true)
    Long countAidasProjectByVendorUser(Long aidasVendorUserId);

}
