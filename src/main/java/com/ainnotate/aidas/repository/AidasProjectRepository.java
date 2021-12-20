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
    @Query(value="select p.* from aidas_project p, aidas_object o,  aidas_user_obj_map am ,aidas_user u where  am.aidas_object_id=o.id and o.aidas_project_id=p.id and am.aidas_user_id=u.id and u.id= ?1",nativeQuery = true)
    Page<AidasProject> findAllProjectsByVendorUser(Pageable page, AidasUser aidasUser);
    @Query(value="select p.* from aidas_project p, aidas_object o,  aidas_user_obj_map am ,aidas_user u where  am.aidas_object_id=o.id and o.aidas_project_id=p.id and am.aidas_user_id=u.id and u.aidas_vendor_id= ?1",nativeQuery = true)
    Page<AidasProject> findAllProjectsByVendorAdmin(Pageable page, AidasVendor aidasVendor);

    @Query(value="select p.* from aidas_project p, aidas_object o,  aidas_user_obj_map am ,aidas_user u where  am.aidas_object_id=o.id and o.aidas_project_id=p.id and am.aidas_user_id=u.id and u.id= ?1 and p.id=?2",nativeQuery = true)
    Optional<AidasProject> findAllProjectsByVendorUserProject(AidasUser aidasUser, Long aidasProjectId);
    @Query(value="select p.* from aidas_project p, aidas_object o,  aidas_user_obj_map am ,aidas_user u where  am.aidas_object_id=o.id and o.aidas_project_id=p.id and am.aidas_user_id=u.id and u.aidas_vendor_id= ?1 and p.id=?2",nativeQuery = true)
    Optional<AidasProject> findAllProjectsByVendorAdminProject(AidasVendor aidasVendor, Long aidasProjectId);

    @Query(value="select p.* from aidas_project p, aidas_object o,  aidas_user_obj_map am ,aidas_user u where  am.aidas_object_id=o.id and o.aidas_project_id=p.id and am.aidas_user_id=u.id and u.id= ?1 and p.id=?2",nativeQuery = true)
    Optional<AidasProject> findAllProjectsByCustomerAdminProject(AidasCustomer aidasCustomer,Long aidasProjectId);
    @Query(value="select p.* from aidas_project p, aidas_object o,  aidas_user_obj_map am ,aidas_user u where  am.aidas_object_id=o.id and o.aidas_project_id=p.id and am.aidas_user_id=u.id and u.aidas_vendor_id= ?1 and p.id=?2",nativeQuery = true)
    Optional<AidasProject> findAllProjectsByOrgAdminProject(AidasOrganisation aidasOrganisation,Long aidasProjectId);

    Page<AidasProject> findAllByIdGreaterThan(Long id, Pageable page);

}
