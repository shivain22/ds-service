package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.UserVendorMappingObjectMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the UserVendorMappingObjectMapping entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserVendorMappingObjectMappingRepository extends JpaRepository<UserVendorMappingObjectMapping, Long> {

    @Query(value = "select * from user_vendor_mapping_object_mapping auavaom where auavaom.user_vendor_mapping_id=?1 and object_id=?2", nativeQuery = true)
    UserVendorMappingObjectMapping findByAidasUser_IdAndAidasObject_Id(Long aidasUserId, Long object);

    @Query(value= "select count(*) from (select * from user_obj_map where user_id in (select id from user where vendor_id=?1) and object_id=?2) a",nativeQuery = true)
    Integer getCountOfAidasObjectMappingForVendorAdmin(Long vendorId,Long object);

    @Query(value="select * from user_obj_map where object_id>-1", nativeQuery = true)
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
}
