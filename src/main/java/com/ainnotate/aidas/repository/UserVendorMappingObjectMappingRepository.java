package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.Organisation;
import com.ainnotate.aidas.domain.User;
import com.ainnotate.aidas.domain.UserVendorMappingObjectMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data SQL repository for the UserVendorMappingObjectMapping entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface UserVendorMappingObjectMappingRepository extends JpaRepository<UserVendorMappingObjectMapping, Long> {

    @Query(value = "select * from user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm where uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id=?1 and object_id=?2", nativeQuery = true)
    UserVendorMappingObjectMapping findByUserObject(Long userId, Long objectId);

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
    @Query(value = "delete from user_vendor_mapping_object_mapping where is_sample_data=1 order by id desc",nativeQuery = true)
    void deleteAllSampleUserVendorMappingObjectMappings();

    @Query(value = "",nativeQuery = true)
    List<UserVendorMappingObjectMapping> getUserVendorMappingObjectMappingByObjectId(Long objectId);

    @Query(value = "select uvmom.* from user_vendor_mapping_object_mapping uvmom, object o where  uvmom.object_id=o.id and o.project_id=?1",nativeQuery = true)
    List<UserVendorMappingObjectMapping> getAllUserVendorMappingObjectMappingByUserVendorMappingIdsAndObjectId(Long projectId);


    @Query(value="select * from user_vendor_mapping_object_mapping where object_id=?1",nativeQuery = true)
    List<UserVendorMappingObjectMapping> getAllUserVendorMappingObjectMappingsByObjectId(Long ObjectId);

    @Query(value = "select uvm.vendor_id from user_vendor_mapping uvm, user_vendor_mapping_object_mapping uvmom,object o where uvmom.object_id=o.id and o.project_id=?1 and uvmom.user_vendor_mapping_id=uvm.id and uvmom.status=1",nativeQuery = true)
    List<Long> getVendorsWhoseUsersAreHavingStatusOne(Long projectId);
}
