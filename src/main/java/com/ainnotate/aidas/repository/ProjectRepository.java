package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.dto.GetProjectDTO;
import com.ainnotate.aidas.dto.IUploadDetail;
import com.ainnotate.aidas.dto.ProjectDTO;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the AidasProject entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface ProjectRepository extends JpaRepository<Project, Long>,QuerydslPredicateExecutor<Project>, QuerydslBinderCustomizer<QProject> {

    @Query(value="select * from project p , customer c where p.customer_id=c.id and c.organisation_id=?1 and p.status=1 ",nativeQuery = true)
    Page<Project> findAllByAidasCustomer_AidasOrganisation(Pageable page, Long organisationId);

    @Query(value="select * from project p where ?1 like '% ?2 %'",nativeQuery = true)
    Page<Project> search(Pageable page, String field,String value);

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

    @Query(nativeQuery = true)
    List<ProjectDTO> findProjectsForOrganisationQC(Long userId);

    @Query(nativeQuery = true)
    List<ProjectDTO> findProjectsForVendorQC(Long userId);

    @Query(nativeQuery = true)
    List<ProjectDTO> findProjectsForAdminQC(Long userId);

    @Query(value = "select count(*) from project p , customer c where p.customer_id=c.id and c.organisation_id=?1 and p.status=1",nativeQuery = true)
    Long countAidasProjectByAidasCustomer_AidasOrganisation(Long organisationId);

    @Query(value ="select count(*) from project p where p.customer_id=? and status=1",nativeQuery = true)
    Long countAidasProjectByAidasCustomer(Long customerId);

    @Query(value = "select count(*)from (select ap.id,count(*) from user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm, user au, object ao, project ap where uvmom.object_id=ao.id and uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id=au.id and ao.project_id=ap.id and uvm.vendor_id=?1 and ap.status=1 group by ap.id) a", nativeQuery = true)
    Long countAidasProjectByVendor(Long vendorId);

    @Query(value=" select count(*) from user_vendor_mapping_project_mapping uvmpm,user_vendor_mapping uvm where uvmpm.user_vendor_mapping_id=uvm.id and uvm.user_id=?1 group by uvm.user_id",nativeQuery = true)
    Long countAidasProjectByVendorUser(Long userId);


    @Query(nativeQuery = true)
    Page<ProjectDTO> findProjectWithUploadCountByUser(Pageable page, Long userId);

    @Query(nativeQuery = true)
    Page<ProjectDTO> findProjectWithUploadCountByUserSearch(Pageable page, Long userId,String searchTerm);


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


    @Modifying(flushAutomatically = true,clearAutomatically = true)
    @Query(value = "insert into project_property (status,add_to_metadata,default_prop,optional,passed_from_app,value,category_id,project_id,property_id,project_property_type,show_to_vendor_user)"
    		+ " select p.status,p.add_to_metadata,p.default_prop,p.optional,p.passed_from_app,p.value,?2,?1,p.id,?2,p.show_to_vendor_user from property p where p.customer_id=?3 and (p.category_id=1 or p.category_id=?2)",nativeQuery = true)
    void addProjectProperties(Long projectId,Long categoryId, Long customerId);

    @Query(value="(select count(*) from upload u, user_vendor_mapping_object_mapping uvmom,object o where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and o.project_id=?1 for update)",nativeQuery = true)
    Integer getTotalUploadedForNonGroupedForUpdate(Long projectId);
    
    @Query(value="(select count(*) from upload u, user_vendor_mapping_object_mapping uvmom,object o where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and o.project_id=?1 and u.approval_status=2 for update)",nativeQuery = true)
    Integer getTotalPendingForNonGroupedForUpdate(Long projectId);
    
    @Modifying
    @Query(value = "update project set total_uploaded=total_uploaded+1, total_pending=total_pending+1, total_required=total_required-1 where id=?1",nativeQuery = true)
    void addTotalUploadedAddPendingSubtractRequired(Long projectId);

    @Modifying(flushAutomatically = true,clearAutomatically = true)
    @Query(value = "update project set total_uploaded_for_grouped=total_uploaded_for_grouped+1, "
    		+ "total_pending_for_grouped=total_pending_for_grouped+1, "
    		+ "total_required_for_grouped=total_required_for_grouped-1  "
    		+ "where id=?1",nativeQuery = true)
    void addTotalUploadedAddPendingSubtractRequiredForGrouped(Long projectId);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update project set total_rejected=total_rejected+1,total_pending= total_pending-1 ,total_required=total_required+1 where id=?1",nativeQuery = true)
    void addTotalRejectedAndSubtractTotalPendingAddTotalRequired(Long id);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update project set total_rejected=?2,total_pending= ?2 ,total_required=?2 where id=?1",nativeQuery = true)
    void addTotalRejectedAndSubtractTotalPendingAddTotalRequired(Long projectId,Integer numToAddSub);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update project set total_rejected=?2,total_pending= ?2 ,total_required=?2 where id=?1",nativeQuery = true)
    void addTotalRejectedAndSubtractTotalPendingAddTotalRequiredForLevelGreaterThan1(Long id,Integer numToAddSub);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update project set total_rejected_for_grouped=total_rejected_for_grouped+1,"
    		+ "total_pending_for_grouped= total_pending_for_grouped-1 ,"
    		+ "total_required_for_grouped=total_required_for_grouped+1 where id=?1",nativeQuery = true)
    void addTotalRejectedAndSubtractTotalPendingAddTotalRequiredForGrouped(Long id);


    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update project set total_rejected_for_grouped=total_rejected_for_grouped+1,"
    		+ "total_pending_for_grouped= total_pending_for_grouped-?2 ,"
    		+ "total_required_for_grouped=total_required_for_grouped+?2 where id=?1",nativeQuery = true)
    void addTotalRejectedAndSubtractTotalPendingAddTotalRequiredForGrouped(Long id,Long numToAddSub);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update project set total_rejected=total_rejected-1,total_required=total_required-1,total_pending=total_pending+1 where id=?1",nativeQuery = true)
    void subTotalRejectedAndSubTotalRequiredAddTotalPending(Long id);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update project "
    		+ "set total_rejected_for_grouped=total_rejected_for_grouped-1,"
    		+ "total_required_for_grouped=total_required_for_grouped-1,"
    		+ "total_pending_for_grouped=total_pending_for_grouped+1 "
    		+ "where id=?1",nativeQuery = true)
    void subTotalRejectedAndSubTotalRequiredAddTotalPendingForGrouped(Long id);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update project "
    		+ "set total_rejected_for_grouped=total_rejected_for_grouped-1,"
    		+ "total_required_for_grouped=total_required_for_grouped-1,"
    		+ "total_pending_for_grouped=total_pending_for_grouped+1 "
    		+ "where id=?1",nativeQuery = true)
    void subTotalRejectedAndSubTotalRequiredForGrouped(Long id);

    @Modifying
    @Query(value = "update project set total_approved=total_approved+1, total_pending=total_pending  where id=?1",nativeQuery = true)
    void addTotalApprovedSubtractTotalPending(Long id);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update project set total_approved=total_approved+?2, total_pending=total_pending-?2  where id=?1",nativeQuery = true)
    void addTotalApprovedSubtractTotalPending(Long id,Integer numToAddSub );

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update project set total_approved=?2,total_rejected=?3,total_pending=?4, total_required=(number_of_uploads_required-?3)  where id=?1",nativeQuery = true)
    void addTotalApprovedSubtractTotalPendingNonGrouped(Long id,Integer totalApproved, Integer totalRejected,Integer totalPending );


    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update project set total_approved=?2,total_pending=?2  where id=?1",nativeQuery = true)
    void addTotalApprovedSubtractTotalPendingNonGroupedApproved(Long id,Integer totalApproved, Integer totalPending );

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update project set "
    		+ "total_approved_for_grouped=total_approved_for_grouped+1, "
    		+ "total_pending_for_grouped=total_pending_for_grouped  where id=?1",nativeQuery = true)
    void addTotalApprovedSubtractTotalPendingForGrouped(Long id);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update project set "
    		+ "total_approved_for_grouped=total_approved_for_grouped+?2, "
    		+ "total_pending_for_grouped=total_pending_for_grouped-?2  where id=?1",nativeQuery = true)
    void addTotalApprovedSubtractTotalPendingForGrouped(Long id,Integer numToAddSub);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update project set total_required=total_required-1 where id=?1",nativeQuery = true)
    void subTotalRequired(Long id);

    @Modifying(flushAutomatically = true,clearAutomatically = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update project set number_of_objects=number_of_objects-1 where id=?1",nativeQuery = true)
    void subtractNumberOfObjects(Long projectId);

    @Override
    default public void customize(
        QuerydslBindings bindings, QProject root) {
        bindings.bind(String.class)
            .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);

    }
    @Query(value="select * from project p where id=?1 for share",nativeQuery = true)
    Project getByIdForUpdate(Long projectId);
    
    @Query(nativeQuery = true)
    GetProjectDTO getProjectById(Long projectId);
}
