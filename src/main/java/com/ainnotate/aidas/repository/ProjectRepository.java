package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.dto.IUploadDetail;
import com.ainnotate.aidas.dto.ProjectDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the AidasProject entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query(value="select * from project p , customer c where p.customer_id=c.id and c.organisation_id=?1 and p.status=1",nativeQuery = true)
    Page<Project> findAllByAidasCustomer_AidasOrganisation(Pageable page, Long organisationId);

    @Query(nativeQuery = true)
    List<ProjectDTO> findAllByAidasCustomer_AidasOrganisationForDropDown(Long organisationId);

    @Query(value="select * from project p  where p.customer_id=?1 and status=1",nativeQuery = true)
    Page<Project> findAllByAidasCustomer(Pageable page, Long customerId);

    @Query(value="select * from project p  where p.customer_id=?1 and status=1",nativeQuery = true)
    List<Project> findAllByAidasCustomer(Long customerId);

    @Query(nativeQuery = true)
    List<ProjectDTO> findAllByAidasCustomerForDropDown(Long customerId);


    @Query(value="select p.* from project p, object o,  user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm  where  uvmom.object_id=o.id and o.project_id=p.id and uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id =?1 and p.id>-1  and p.status=1 group by p.id ",nativeQuery = true)
    List<Project> findAllProjectsByVendorUser(Long userId);

    @Query(value="select p.* from project p, object o,  user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm  where  uvmom.object_id=o.id and o.project_id=p.id and uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id =?1 and p.id>-1  and p.status=1 group by p.id ",nativeQuery = true)
    List<Project> findAllProjectsByVendorUserList(Long userId);

    @Query(value="select distinct p.* from user_vendor_mapping_object_mapping uvmom, user_vendor_mapping uvm, object o, project p where uvmom.user_vendor_mapping_id=uvm.id and uvm.vendor_id=?1 and uvmom.object_id=o.id and o.project_id=p.id;",nativeQuery = true)
    Page<Project> findAllProjectsByVendorAdmin(Pageable page, Long vendorId);

    @Query(nativeQuery = true)
    List<ProjectDTO> findAllProjectsByVendorAdminDropDown(Long vendorId);

    @Query(value="select p.* from project p, object o,  user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm ,user u where  uvmom.object_id=o.id and o.project_id=p.id and uvmom.user_vendor_mapping_id=uvm.id  and uvm.user_id= ?1 and p.id=?2   and p.status=1 ",nativeQuery = true)
    Optional<Project> findAllProjectsByVendorUserProject(Long userId, Long aidasProjectId);

    @Query(value="select p.* from project p, object o,  user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm ,user u where  uvmom.object_id=o.id and o.project_id=p.id and uvmom.user_vendor_mapping_id=uvm.id and uvm.vendor_id= ?1 and p.id=?2   and p.status=1 ",nativeQuery = true)
    Optional<Project> findAllProjectsByVendorAdminProject(Long vendorId, Long aidasProjectId);

    @Query(value="select p.* from project p, object o,  user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm ,user u where  uvmom.object_id=o.id and o.project_id=p.id and uvmom.user_vendor_mapping_id=uvm.id  and uvm.user_id= ?1 and p.id=?2   and p.status=1 ",nativeQuery = true)
    Optional<Project> findAllProjectsByCustomerAdminProject(Customer customer, Long aidasProjectId);

    @Query(value="select p.* from project p where  p.customer_id=?1 and p.id=?2   and p.status=1 ",nativeQuery = true)
    Optional<Project> findAllProjectsByCustomerAdminProject(Long aidasCustomerId, Long projectId);

    @Query(value="select p.* from project p, customer c where p.customer_id=c.id and c.organisation_id=?1 and p.status=1 ",nativeQuery = true)
    Project findAllProjectsByOrgAdminProject(Long organisationId, Long aidasProjectId);

    @Query(value = "select * from project where status=1 and id>0 order by id desc",nativeQuery = true)
    Page<Project> findAllByIdGreaterThan(Long id, Pageable page);

    @Query(nativeQuery = true)
    List<ProjectDTO> findAllByIdGreaterThanForDropDown();

    @Query(nativeQuery = true)
    List<ProjectDTO> findProjectsForCustomerQC(Long userId);

    @Query(value = "select count(*) from project p , customer c where p.customer_id=c.id and c.organisation_id=?1 and p.status=1",nativeQuery = true)
    Long countAidasProjectByAidasCustomer_AidasOrganisation(Long organisationId);

    @Query(value ="select count(*) from project p where p.customer_id=? and status=1",nativeQuery = true)
    Long countAidasProjectByAidasCustomer(Long customerId);

    @Query(value = "select count(*)from (select ap.id,count(*) from user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm, user au, object ao, project ap where uvmom.object_id=ao.id and uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id=au.id and ao.project_id=ap.id and uvm.vendor_id=?1 and ap.status=1 group by ap.id) a", nativeQuery = true)
    Long countAidasProjectByVendor(Long vendorId);

    @Query(value=" select count(*) from (select p.id from consolidated_user_vendor_mapping_project_mapping_view cuvmpmv,project p, user u where cuvmpmv.project_id=p.id and p.status=1 and cuvmpmv.user_id=u.id and p.id<>-1 group by p.id)a",nativeQuery = true)
    Long countAidasProjectByVendorUser(Long aidasVendorUserId);

    @Query(nativeQuery = true)
    Page<ProjectDTO> findProjectWithUploadCountByUser(Pageable page, Long userId);

    @Query(nativeQuery = true)
    Page<ProjectDTO> findProjectWithUploadCountByUserForAllowedProjects(Pageable page, Long userId,List<Long> enabledProjectIds);

    @Query(value="select count(o.id) as count \n" +
        "from user_vendor_mapping_object_mapping uvmom  \n" +
        "left join object o on o.id=uvmom.object_id  \n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id\n" +
        "where uvm.user_id<>?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2", nativeQuery = true)
    Integer findProjectWithAnyObjectEnabledForUser(Long userId,Long projectId);

    @Query(nativeQuery = true)
    List<ProjectDTO> findProjectWithUploadCountByUserForDropDown(Long userId);

    @Query(value=" select count(*) from upload u, user_vendor_mapping_object_mapping uvmom, object o where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and u.approval_status=1 and o.project_id=?1 group by o.project_id ",nativeQuery = true)
    Integer countUploadsByProject(Long projectId);

    @Query(value = "select count(*) from project where id>0 and status=1", nativeQuery = true)
    Long countAllProjectsForSuperAdmin();

    @Query(value = "select * from project where is_sample_data=1",nativeQuery = true)
    List<Project> getAllSampleProjects();

    @Modifying
    @Query(value = "delete from project where is_sample_data=1 order by id desc",nativeQuery = true)
    void deleteAllSampleProjects();

    @Query(value="select count(*) from\n" +
        "(" +
        "select pp.* from project_property pp where pp.project_id=?1 and pp.add_to_metadata=1 " +
        " union " +
        " select op.* from object_property op, object o where op.object_id=o.id and o.project_id=?1 and op.add_to_metadata=1 and op.property_id not in (select property_id from project_property where project_id=?1)" +
        ")a",nativeQuery = true)
    Integer getTotalPropertyCountForExport(Long projectId);

    @Query(value="select a.name from (select p.id,p.name from project_property pp,property p where pp.property_id=p.id and pp.project_id=?1 and pp.add_to_metadata=1 " +
        " union " +
        " select p.id,p.name from object_property op,property p, object o where op.property_id=p.id and op.object_id=o.id and o.project_id=?1 and op.add_to_metadata=1 and op.property_id not in (select property_id from project_property where project_id=?1)) a order by a.id"
        ,nativeQuery = true)
    List<String> getTotalPropertyNamesForExport(Long projectId);

    @Query(value="select number_of_upload_required_with_buffer_for_project,number_of_upload_required_with_buffer_using_object_buffer from project_level_upload_requirements where project_id=?1",nativeQuery = true)
    List<Integer> getProjectLevelUploadRequirements(Long projectId);


}
