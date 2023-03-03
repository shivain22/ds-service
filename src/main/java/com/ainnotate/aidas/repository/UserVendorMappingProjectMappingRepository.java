package com.ainnotate.aidas.repository;


import com.ainnotate.aidas.domain.UserVendorMappingObjectMapping;
import com.ainnotate.aidas.domain.UserVendorMappingProjectMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
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
    @Query(value = "update user_vendor_mapping_project_mapping set total_rejected=total_rejected+1,total_pending= total_pending-1 ,total_required=total_required+1 where id=?1",nativeQuery = true)
    void addTotalRejectedAndSubtractTotalPendingAddTotalRequired(Long id);
    
    
    
    @Modifying
    @Query(value = "update user_vendor_mapping_project_mapping set total_rejected=total_rejected-1,total_required=total_required-1,total_pending=total_pending+1 where id=?1",nativeQuery = true)
    void subTotalRejectedAndSubTotalRequiredAddTotalPending(Long id);
    
    @Modifying
    @Query(value = "update user_vendor_mapping_project_mapping set total_approved=total_approved+1, total_pending=total_pending-1  where id=?1",nativeQuery = true)
    void addTotalApprovedSubtractTotalPending(Long id);
    
    
    
    @Modifying
    @Query(value = "update user_vendor_mapping_project_mapping set total_required=total_required-1 where id=?1",nativeQuery = true)
    void subTotalRequired(Long id);


}
