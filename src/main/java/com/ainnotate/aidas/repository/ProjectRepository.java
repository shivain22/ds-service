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

    @Query(value="select * from project p , customer c where p.customer_id=c.id and c.organisation_id=?1 and p.status=1",nativeQuery = true)
    List<Project> findAllByAidasCustomer_AidasOrganisationForDropDown(Long organisationId);

    @Query(value="select * from project p  where p.customer_id=?1 and status=1",nativeQuery = true)
    Page<Project> findAllByAidasCustomer(Pageable page, Long customerId);

    @Query(value="select * from project p  where p.customer_id=?1 and status=1",nativeQuery = true)
    List<Project> findAllByAidasCustomerForDropDown(Long customerId);


    @Query(value="select p.* from project p, object o,  user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm  where  uvmom.object_id=o.id and o.project_id=p.id and uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id =?1 and p.id>-1  and p.status=1 group by p.id ",nativeQuery = true)
    List<Project> findAllProjectsByVendorUser(Long userId);

    @Query(value="select p.* from project p, object o,  user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm  where  uvmom.object_id=o.id and o.project_id=p.id and uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id =?1 and p.id>-1  and p.status=1 group by p.id ",nativeQuery = true)
    List<Project> findAllProjectsByVendorUserList(Long userId);

    @Query(value="select p.* from project p, object o,  user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm ,user u where  uvmom.object_id=o.id and o.project_id=p.id and uvmom.user_vendor_mapping_id=uvm.id and uvm.vendor_id= ?1   and p.status=1 ",nativeQuery = true)
    Page<Project> findAllProjectsByVendorAdmin(Pageable page, Long vendorId);

    @Query(value="select p.* from project p, object o,  user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm ,user u where  uvmom.object_id=o.id and o.project_id=p.id and uvmom.user_vendor_mapping_id=uvm.id and uvm.vendor_id= ?1   and p.status=1 ",nativeQuery = true)
    List<Project> findAllProjectsByVendorAdminDropDown(Long vendorId);

    @Query(value="select p.* from project p, object o,  user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm ,user u where  uvmom.object_id=o.id and o.project_id=p.id and uvmom.user_vendor_mapping_id=uvm.id  and uvm.user_id= ?1 and p.id=?2   and p.status=1 ",nativeQuery = true)
    Optional<Project> findAllProjectsByVendorUserProject(Long userId, Long aidasProjectId);

    @Query(value="select p.* from project p, object o,  user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm ,user u where  uvmom.object_id=o.id and o.project_id=p.id and uvmom.user_vendor_mapping_id=uvm.id and uvm.vendor_id= ?1 and p.id=?2   and p.status=1 ",nativeQuery = true)
    Optional<Project> findAllProjectsByVendorAdminProject(Long vendorId, Long aidasProjectId);

    @Query(value="select p.* from project p, object o,  user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm ,user u where  uvmom.object_id=o.id and o.project_id=p.id and uvmom.user_vendor_mapping_id=uvm.id  and uvm.user_id= ?1 and p.id=?2   and p.status=1 ",nativeQuery = true)
    Optional<Project> findAllProjectsByCustomerAdminProject(Customer customer, Long aidasProjectId);

    @Query(value="select p.* from project p where  p.customer_id=?1 and p.id=?2   and p.status=1 ",nativeQuery = true)
    Optional<Project> findAllProjectsByCustomerAdminProject(Long aidasCustomerId, Long projectId);

    @Query(value="select p.* from project p, object o,  user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm ,user u where  uvmom.object_id=o.id and o.project_id=p.id and uvmom.user_vendor_mapping_id=uvm.id and uvm.vendor_id= ?1 and p.id=?2   and p.status=1 ",nativeQuery = true)
    Optional<Project> findAllProjectsByOrgAdminProject(Long organisationId, Long aidasProjectId);

    @Query(value = "select * from project where status=1 and id>0 order by id desc",nativeQuery = true)
    Page<Project> findAllByIdGreaterThan(Long id, Pageable page);

    @Query(value = "select * from project where status=1 and id>0 order by id desc",nativeQuery = true)
    List<Project> findAllByIdGreaterThanForDropDown(Long id);

    @Query(value = "select p.* from project p, qc_project_mapping qpm, user_customer_mapping ucm where qpm.user_customer_mapping_id=ucm.id and ucm.user_id=? and qpm.project_id=p.id and p.status=1 and qpm.status=1 and ucm.status=1 and p.id>0 order by p.id desc",nativeQuery = true)
    List<Project> findProjectsForQC(Long userId);

    @Query(value = "select count(*) from project p , customer c where p.customer_id=c.id and c.organisation_id=?1 and p.status=1",nativeQuery = true)
    Long countAidasProjectByAidasCustomer_AidasOrganisation(Long organisationId);

    @Query(value ="select count(*) from project p where p.customer_id=? and status=1",nativeQuery = true)
    Long countAidasProjectByAidasCustomer(Long customerId);

    @Query(value = "select count(*)from (select ap.id,count(*) from user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm, user au, object ao, project ap where uvmom.object_id=ao.id and uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id=au.id and ao.project_id=ap.id and uvm.vendor_id=?1 and ap.status=1 group by ap.id) a", nativeQuery = true)
    Long countAidasProjectByVendor(Long vendorId);

    @Query(value=" select count(*) from (select ap.id from user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm, object ao, project ap where ao.project_id=ap.id and uvmom.object_id=ao.id  and uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id=?1 and ap.status=1 and uvmom.status=1 group by ap.id)a",nativeQuery = true)
    Long countAidasProjectByVendorUser(Long aidasVendorUserId);

    @Query(nativeQuery = true)
    List<ProjectDTO> findProjectWithUploadCountByUser(Pageable page, Long userId);

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

}
