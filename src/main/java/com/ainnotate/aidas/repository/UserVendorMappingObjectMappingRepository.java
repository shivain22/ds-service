package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.Organisation;
import com.ainnotate.aidas.domain.User;
import com.ainnotate.aidas.domain.UserVendorMappingObjectMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data SQL repository for the UserVendorMappingObjectMapping entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface UserVendorMappingObjectMappingRepository extends JpaRepository<UserVendorMappingObjectMapping, Long> {

    @Query(value = "select uvmom.* from user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm where uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id=?1 and object_id=?2", nativeQuery = true)
    UserVendorMappingObjectMapping findByUserObject(Long userId, Long objectId);
    
    @Query(value = "select uvmom.* from user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm where uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id=?1 and object_id=?2 for update", nativeQuery = true)
    UserVendorMappingObjectMapping findByUserObjectForUpload(Long userId, Long objectId);
    
    @Query(value = "select uvmom.* from user_vendor_mapping_object_mapping uvmom where id=?1 for update", nativeQuery = true)
    UserVendorMappingObjectMapping findByUserObjectForUpload(Long uvmomId);

    @Query(value = "select * from user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm,object o where uvmom.object_id=o.id and uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id<>?1 and object_id=?2 and uvmom.status=1 and o.project_id=?3", nativeQuery = true)
    List<UserVendorMappingObjectMapping> findByOtherUsersAndObject(Long userId, Long objectId,Long projectId);

    @Query(value = "select * from user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm,object o where uvmom.object_id=o.id and uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id=?1 and object_id<>?2 and o.project_id=?3 and uvmom.status=1", nativeQuery = true)
    List<UserVendorMappingObjectMapping> findByOtherObjectsForUser(Long userId, Long objectId,Long projectId);

    @Query(value = "select * from user_vendor_mapping_object_mapping uvmom where user_vendor_mapping_id=?1 and object_id=?2 and status=1", nativeQuery = true)
    UserVendorMappingObjectMapping findByUserVendorMappingObject(Long userVendorMappingId, Long objectId);

    @Query(value = "select * from user_vendor_mapping_object_mapping uvmom where user_vendor_mapping_id=?1 and object_id=?2", nativeQuery = true)
    UserVendorMappingObjectMapping findAllByUserVendorMappingObject(Long userVendorMappingId, Long objectId);

    @Query(value = " select ((total_required-(select sum(total_uploaded) from consolidated_user_vendor_mapping_object_mapping_view cuvmomv where object_id=?2 group by object_id))+ (select sum(rejected) from consolidated_user_vendor_mapping_object_mapping_view cuvmomv where object_id=?2 group by object_id) ),total_uploaded,approved,rejected,pending from consolidated_user_vendor_mapping_object_mapping_view uvmom where uvm_id= ?1 and object_id= ?2", nativeQuery = true)
    List<Integer[]> findByConsolidatedUpload(Long userVendorMappingId, Long objectId);

    @Query(value= "select count(*) from (select * from user_vendor_mapping_object_mapping uvmom, user_vendor_mapping uvm where uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id  in (select id from user where vendor_id=?1) and object_id=?2) a",nativeQuery = true)
    Integer getCountOfAidasObjectMappingForVendorAdmin(Long vendorId,Long object);

    @Query(value="select * from user_vendor_mapping_object_mapping uvmom where uvmom.object_id>-1", nativeQuery = true)
    Page<UserVendorMappingObjectMapping> findAllMappings(Pageable pageable);

    @Query(value = "select auavm.* " +
        "from " +
        "user au , " +
        "user_vendor_mapping auavm ," +
        "user_authority_mapping auaam " +
        "where " +
        "auavm.user_id=au.id and auavm.vendor_id=?1 " +
        "and auaam.user_id=au.id and auaam.authority_id=5",nativeQuery = true)
    List<UserVendorMappingObjectMapping> findAllByAidasVendor_Id(Long aidasVendorId);

    @Query(value = "select * from user_vendor_mapping_object_mapping where is_sample_data=1 order by id asc",nativeQuery = true)
    List<UserVendorMappingObjectMapping> getAllSampleUserVendorMappingObjectMappings();

    @Query(value = "select id from user_vendor_mapping_object_mapping where is_sample_data=1 order by id asc",nativeQuery = true)
    List<Long> getAllSampleUserVendorMappingObjectMappingsIds();

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "delete from user_vendor_mapping_object_mapping where is_sample_data=1 order by id desc",nativeQuery = true)
    void deleteAllSampleUserVendorMappingObjectMappings();
    
    @Modifying
    @Query(value = "update user_vendor_mapping_object_mapping set total_uploaded=total_uploaded+1, total_pending=total_pending+1 where id=?1",nativeQuery = true)
    void addTotalUploadedAndAddTotalPending(Long id);
    
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update user_vendor_mapping_object_mapping set total_rejected=total_rejected+1,total_pending= total_pending-1 ,total_required=total_required+1 where id=?1",nativeQuery = true)
    void addTotalRejectedAndSubtractTotalPendingAddTotalRequired(Long id);
    
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update user_vendor_mapping_object_mapping set total_rejected=total_rejected+?2,total_pending= total_pending-?2 ,total_required=total_required+?2 where id=?1",nativeQuery = true)
    void addTotalRejectedAndSubtractTotalPendingAddTotalRequired(Long id,Integer numToAddSub);
    
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update user_vendor_mapping_object_mapping set total_rejected=?2,total_pending= ?3 ,total_required=total_required+?2 where id=?1",nativeQuery = true)
    void addTotalRejectedAndSubtractTotalPendingAddTotalRequiredNonGrouped(Long id,Integer totalRejected,Integer totalPending);
    
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update user_vendor_mapping_object_mapping set total_rejected=total_rejected-1,total_required=total_required-1,total_pending=total_pending+1 where id=?1",nativeQuery = true)
    void subTotalRejectedAndSubTotalRequiredAddTotalPending(Long id);
    
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update user_vendor_mapping_object_mapping set total_approved=total_approved+1, total_pending=total_pending  where id=?1",nativeQuery = true)
    void addTotalApprovedSubtractTotalPending(Long id);
    
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update user_vendor_mapping_object_mapping set total_approved=?2, total_pending=?3  where id=?1",nativeQuery = true)
    void addTotalApprovedSubtractTotalPending(Long id, Integer numToAddSub,Integer totalPending);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "update user_vendor_mapping_object_mapping set total_required=total_required-1 where id=?1",nativeQuery = true)
    void subTotalRequired(Long id);
    
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = "insert into user_vendor_mapping_object_mapping(user_vendor_mapping_id,object_id) values(?1,?2)",nativeQuery = true)
    void insertNewUvmom(Long uvmId, Long objectId);
    
    @Query(value = "",nativeQuery = true)
    List<UserVendorMappingObjectMapping> getUserVendorMappingObjectMappingByObjectId(Long objectId);

    @Query(value = "select uvmom.* from user_vendor_mapping_object_mapping uvmom, object o where  uvmom.object_id=o.id and o.project_id=?1",nativeQuery = true)
    List<UserVendorMappingObjectMapping> getAllUserVendorMappingObjectMappingByUserVendorMappingIdsAndObjectId(Long projectId);

    @Query(value = "select uvmom.* from user_vendor_mapping_object_mapping uvmom, object o,user_vendor_mapping uvm where  uvmom.object_id=o.id and o.project_id=?1  and uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id=?2 and uvmom.status=1",nativeQuery = true)
    List<UserVendorMappingObjectMapping> getAllUserVendorMappingObjectMappingByUserVendorMappingIdsAndObjectIdAndStatus1(Long projectId,Long userId);


    @Query(value="select * from user_vendor_mapping_object_mapping where object_id=?1",nativeQuery = true)
    List<UserVendorMappingObjectMapping> getAllUserVendorMappingObjectMappingsByObjectId(Long ObjectId);

    @Query(value = "select uvm.vendor_id from user_vendor_mapping uvm, user_vendor_mapping_object_mapping uvmom,object o where uvmom.object_id=o.id and o.project_id=?1 and uvmom.user_vendor_mapping_id=uvm.id and uvmom.status=1",nativeQuery = true)
    List<Long> getVendorsWhoseUsersAreHavingStatusOne(Long projectId);

    @Query(value="select count(u.id) from upload u,user_vendor_mapping_object_mapping uvmom ,object o\n" +
        "where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and  u.approval_status=1 and o.id=?1 group by uvmom.object_id",nativeQuery = true)
    Integer getTotalApproved(Long ObjectId);

    @Query(value="select count(u.id) from upload u,user_vendor_mapping_object_mapping uvmom ,object o\n" +
        "where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and o.id=?1 and u.approval_status=0 group by uvmom.object_id",nativeQuery = true)
    Integer getTotalRejected(Long ObjectId);

    @Query(value="select pending from object_level_upload_summary where object_id=?1",nativeQuery = true)
    Integer getTotalPending(Long ObjectId);
    
    @Query(value="select * from user_vendor_mapping_object_mapping uvmom where uvmom.id=?1",nativeQuery = true)
    UserVendorMappingObjectMapping getUvmom(Long id);
}
