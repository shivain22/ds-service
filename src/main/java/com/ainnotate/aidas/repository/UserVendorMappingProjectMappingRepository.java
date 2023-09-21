package com.ainnotate.aidas.repository;


import com.ainnotate.aidas.domain.UserVendorMappingObjectMapping;
import com.ainnotate.aidas.domain.UserVendorMappingProjectMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Spring Data SQL repository for the UserVendorMappingProjectMapping entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface UserVendorMappingProjectMappingRepository extends JpaRepository<UserVendorMappingProjectMapping, Long> {


    @Query(value="select * from user_vendor_mapping_project_mapping uvmpm where uvmpm.user_vendor_mapping_id=?1 and uvmpm.project_id=?2",nativeQuery = true)
    UserVendorMappingProjectMapping findByUserVendorMappingIdProjectId(Long userVendorMappingId, Long projectId);
    
    @Query(value="select * from user_vendor_mapping_project_mapping uvmpm where uvmpm.user_vendor_mapping_id=?1 and uvmpm.project_id=?2 for update",nativeQuery = true)
    UserVendorMappingProjectMapping findByUserVendorMappingIdProjectIdForUpload(Long userVendorMappingId, Long projectId);

    @Query(value = "select uvmpm.* from user_vendor_mapping_project_mapping uvmpm where  uvmpm.project_id=?1",nativeQuery = true)
    List<UserVendorMappingProjectMapping> getAllUserVendorMappingProjectMappingByProjectId(Long projectId);
    
    @Modifying
    @Query(value = "update user_vendor_mapping_project_mapping set total_uploaded=total_uploaded+1, total_pending=total_pending+1 where id=?1",nativeQuery = true)
    void addTotalUploadedAndAddTotalPending(Long id);
    
    @Modifying
    @Query(value = "update user_vendor_mapping_project_mapping set total_uploaded_for_grouped=total_uploaded_for_grouped+1, total_pending_for_grouped=total_pending_for_grouped+1 where id=?1",nativeQuery = true)
    void addTotalUploadedAndAddTotalPendingForGrouped(Long id);
    
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update user_vendor_mapping_project_mapping set total_rejected=total_rejected+1,total_pending= total_pending-1 ,total_required=total_required+1 where id=?1",nativeQuery = true)
    void addTotalRejectedAndSubtractTotalPendingAddTotalRequired(Long id);
    
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update user_vendor_mapping_project_mapping set total_rejected=?2,total_pending= ?3 ,total_required=?2 where id=?1",nativeQuery = true)
    void addTotalRejectedAndSubtractTotalPendingAddTotalRequired(Long id,Integer rejected,Integer pending);
    
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update user_vendor_mapping_project_mapping set total_rejected=?2,total_pending= ?2 ,total_required=?2 where id=?1",nativeQuery = true)
    void addTotalRejectedAndSubtractTotalPendingAddTotalRequiredForLevelGreaterThan1(Long id,Integer numToAddSub);
    
    
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update user_vendor_mapping_project_mapping "
    		+ "set total_rejected_for_grouped=total_rejected_for_grouped+1,"
    		+ "total_pending_for_grouped= total_pending_for_grouped-1 ,"
    		+ "total_required_for_grouped=total_required_for_grouped+1 where id=?1",nativeQuery = true)
    void addTotalRejectedAndSubtractTotalPendingAddTotalRequiredForGrouped(Long id);
    
    
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update user_vendor_mapping_project_mapping "
    		+ "set total_rejected_for_grouped=total_rejected_for_grouped+1,"
    		+ "total_pending_for_grouped= total_pending_for_grouped-1 ,"
    		+ "total_required_for_grouped=total_required_for_grouped+1 where id=?1",nativeQuery = true)
    void addTotalRejectedAndSubtractTotalPendingAddTotalRequiredForGrouped(Long id,Long numToAddSub);
    
    
    
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update user_vendor_mapping_project_mapping set total_rejected=total_rejected-1,total_required=total_required-1,total_pending=total_pending+1 where id=?1",nativeQuery = true)
    void subTotalRejectedAndSubTotalRequiredAddTotalPending(Long id);
    
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update user_vendor_mapping_project_mapping set "
    		+ "total_rejected_for_grouped=total_rejected_for_grouped-1,"
    		+ "total_required_for_grouped=total_required_for_grouped-1,"
    		+ "total_pending_for_grouped=total_pending_for_grouped+1 where id=?1",nativeQuery = true)
    void subTotalRejectedAndSubTotalRequiredAddTotalPendingForGrouped(Long id);
    
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update user_vendor_mapping_project_mapping set "
    		+ "total_rejected_for_grouped=total_rejected_for_grouped-1,"
    		+ "total_required_for_grouped=total_required_for_grouped-1,"
    		+ "total_pending_for_grouped=total_pending_for_grouped+1 where id=?1",nativeQuery = true)
    void subTotalRejectedAndSubTotalRequiredForGrouped(Long id);
    
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update user_vendor_mapping_project_mapping set total_approved=total_approved+1, total_pending=total_pending  where id=?1",nativeQuery = true)
    void addTotalApprovedSubtractTotalPending(Long id);
    
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update user_vendor_mapping_project_mapping set total_approved=total_approved+?2, total_pending=total_pending-?2  where id=?1",nativeQuery = true)
    void addTotalApprovedSubtractTotalPending(Long id,Integer numToAddSub);
    
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update user_vendor_mapping_project_mapping set total_approved=?2, total_pending=?3  where id=?1",nativeQuery = true)
    void addTotalApprovedSubtractTotalPendingForNonGrouped(Long id,Integer totalApproved, Integer totalPending);
    
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update user_vendor_mapping_project_mapping set "
    		+ "total_approved_for_grouped=total_approved_for_grouped+1, "
    		+ "total_pending_for_grouped=total_pending_for_grouped-1  where id=?1",nativeQuery = true)
    void addTotalApprovedSubtractTotalPendingForGrouped(Long id);
    
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update user_vendor_mapping_project_mapping set "
    		+ "total_approved_for_grouped=total_approved_for_grouped+?2, "
    		+ "total_pending_for_grouped=total_pending_for_grouped-?2  where id=?1",nativeQuery = true)
    void addTotalApprovedSubtractTotalPendingForGrouped(Long id,Integer numToAddSub);
    
    
    
    @Modifying
    @Query(value = "update user_vendor_mapping_project_mapping set total_required=total_required-1 where id=?1",nativeQuery = true)
    void subTotalRequired(Long id);


}
