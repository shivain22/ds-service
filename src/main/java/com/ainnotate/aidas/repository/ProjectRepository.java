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
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query(value="select * from project p , customer c where p.customer_id=c.id and c.organisation_id=?1 and status=1",nativeQuery = true)
    Page<Project> findAllByAidasCustomer_AidasOrganisation(Pageable page, Long organisationId);
    @Query(value="select * from project p  where p.customer_id=?1 and status=1",nativeQuery = true)
    Page<Project> findAllByAidasCustomer(Pageable page, Long customerId);


    @Query(value="select p.* from project p, object o,  user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm  where  uvmom.object_id=o.id and o.project_id=p.id and uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id =?1 and p.id>-1  and p.status=1 group by p.id ",nativeQuery = true)
    Page<Project> findAllProjectsByVendorUser(Pageable page, Long userId);

    @Query(value="select p.* from project p, object o,  user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm ,user u where  uvmom.object_id=o.id and o.project_id=p.id and uvmom.user_vendor_mapping_id=uvm.id and uvm.vendor_id= ?1   and p.status=1 ",nativeQuery = true)
    Page<Project> findAllProjectsByVendorAdmin(Pageable page, Long vendorId);

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

    @Query(value = "select * from project where status=1 and id>0",nativeQuery = true)
    Page<Project> findAllByIdGreaterThan(Long id, Pageable page);

    @Query(value = "select count(*) from project p , customer c where p.customer_id=c.id and c.organisation_id=?1 and p.status=1",nativeQuery = true)
    Long countAidasProjectByAidasCustomer_AidasOrganisation(Long organisationId);
    @Query(value ="select count(*) from project p where p.customer_id=? and status=1",nativeQuery = true)
    Long countAidasProjectByAidasCustomer(Long customerId);

    @Query(value = "select count(*)from (select ap.id,count(*) from user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm, user au, object ao, project ap where uvmom.object_id=ao.id and uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id=au.id and ao.project_id=ap.id and uvm.vendor_id=?1 and ap.status=1 group by ap.id) a", nativeQuery = true)
    Long countAidasProjectByVendor(Long vendorId);

    @Query(value=" select count(*) from (select ap.id from user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm, object ao, project ap where ao.project_id=ap.id and uvmom.object_id=ao.id  and uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id=?1 and ap.status=1 group by ap.id)a",nativeQuery = true)
    Long countAidasProjectByVendorUser(Long aidasVendorUserId);

    @Query(value = "select ap.id as projectId, count(au.id) totalUploaded,SUM(CASE WHEN au.approval_status = 1 THEN 1 ELSE 0 END) AS totalApproved,SUM(CASE WHEN au.approval_status = 0 THEN 1 ELSE 0 END) AS totalRejected,(select sum(ao1.number_of_upload_reqd) from object ao1 where project_id=?1) AS totalPending from project ap left  join object ao on ao.project_id=ap.id left  join user_vendor_mapping_object_mapping uvmom on uvmom.object_id=ao.id left  join upload au on au.user_vendor_mapping_object_mapping_id=uvmom.id left join user_vendor_mapping uvm on uvmom.user_vendor_mapping_id=uvm.id where uvm.user_id= ?2 and  ap.id=?1 and ap.status=1 group by ap.id " +
        " union " +
        " select ap.id as projectId, count(au.id) totalUploaded,SUM(CASE WHEN au.approval_status = 1 THEN 1 ELSE 0 END) AS totalApproved,SUM(CASE WHEN au.approval_status = 0 THEN 1 ELSE 0 END) AS totalRejected,(select sum(ao1.number_of_upload_reqd) from object ao1 where project_id=?1) AS totalPending from project ap right  join object ao on ao.project_id=ap.id right  join user_vendor_mapping_object_mapping uvmom on uvmom.object_id=ao.id right  join upload au on au.user_vendor_mapping_object_mapping_id=uvmom.id right join user_vendor_mapping uvm on uvmom.user_vendor_mapping_id=uvm.id where uvm.user_id= ?2 and  ap.id=?1 group by ap.id", nativeQuery = true)
    UploadDetail countUploadsByProjectAndUser(Long projectId, Long userId);

    @Query(value = "select count(*) from project where id>0 and status=1", nativeQuery = true)
    Long countAllProjectsForSuperAdmin();

}