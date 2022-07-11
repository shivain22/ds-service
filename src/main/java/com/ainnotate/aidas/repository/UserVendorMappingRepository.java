package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AppProperty;
import com.ainnotate.aidas.domain.Organisation;
import com.ainnotate.aidas.domain.UserAuthorityMapping;
import com.ainnotate.aidas.domain.UserVendorMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link AppProperty} entity.
 */
@Repository
@Transactional
public interface UserVendorMappingRepository extends JpaRepository<UserVendorMapping, Long> {

    @Query(value = "select uvm.* " +
        "from " +
        "user au , " +
        "user_vendor_mapping uvm ," +
        "user_authority_mapping uam " +
        "where " +
        "uvm.user_id=au.id and uvm.vendor_id=?1 " +
        "and uam.user_id=au.id and uam.authority_id=5",nativeQuery = true)
    List<UserVendorMapping> findAllVendorUserMappings(Long aidasVendorId);


    @Query(value = "select uvm.* " +
        "from " +
        "user au , " +
        "user_vendor_mapping uvm ," +
        "user_authority_mapping uam " +
        "where " +
        "uvm.user_id=au.id " +
        "and uam.user_id=au.id and uam.authority_id=5",nativeQuery = true)
    List<UserVendorMapping> findAllVendorUserMappings();

    @Query(value = "select uvm.* " +
        "from " +
        "user au , " +
        "user_vendor_mapping uvm ," +
        "user_authority_mapping uam " +
        "where " +
        "uvm.user_id=au.id and uvm.vendor_id=?1 " +
        "and uam.user_id=au.id and uam.authority_id=5 and uvm.id not in (select user_vendor_mapping_id from user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm where uvmom.object_id=?2 and uvm.id=uvmom.user_vendor_mapping_id and uvm.vendor_id=?1)",nativeQuery = true)
    List<UserVendorMapping> findAllVendorUserMappingsNewlyAdded(Long aidasVendorId,Long objectId);

    @Query(value = "select auavm.* " +
        "from " +
        "user au , " +
        "user_vendor_mapping auavm ," +
        "user_authority_mapping auaam " +
        "where " +
        "auavm.user_id=au.id and auavm.vendor_id=?1 and auavm.user_id=?2 " +
        "and auaam.user_id=au.id and auaam.user_id=?2 and auaam.authority_id=5",nativeQuery = true)
    UserVendorMapping findByUserAndVendor(Long aidasVendorId, Long aidasUserId);

    @Query(value="select * from user_vendor_mapping where vendor_id=?1 and user_id=?2",nativeQuery = true)
    UserVendorMapping findByVendorIdAndUserId(Long vendorId, Long userId);

    @Query(value = "select * from user_vendor_mapping uvm, user_authority_mapping uam where uvm.user_id=uam.user_id and uam.authority_id=5 and uvm.is_sample_data=1 ",nativeQuery = true)
    List<UserVendorMapping> getAllSampleUserVendorMappingsOfVendorUsers();

    @Modifying
    @Query(value = "delete from user_vendor_mapping where is_sample_data=1 order by id desc",nativeQuery = true)
    void deleteAllSampleUserVendorMappings();

    @Query(value = "select * from user_vendor_mapping uvm where uvm.vendor_id in (?1)",nativeQuery = true)
    List<UserVendorMapping> findAllUserVendorMappingByVendorIds(List<Long> vendorIds);

    @Query(value = "select * from user_vendor_mapping uvm where uvm.vendor_id in (?1)",nativeQuery = true)
    List<UserVendorMapping> findAllUserVendorMappingByUserIds(List<Long> userIds);

}
