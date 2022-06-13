package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AppProperty;
import com.ainnotate.aidas.domain.UserVendorMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link AppProperty} entity.
 */
@Repository
public interface UserVendorMappingRepository extends JpaRepository<UserVendorMapping, Long> {

    @Query(value = "select auavm.* " +
        "from " +
        "user au , " +
        "user_vendor_mapping auavm ," +
        "user_authority_mapping auaam " +
        "where " +
        "auavm.user_id=au.id and auavm.vendor_id=?1 " +
        "and auaam.user_id=au.id and auaam.authority_id=5",nativeQuery = true)
    List<UserVendorMapping> findAllByAidasVendor_Id(Long aidasVendorId);

    @Query(value = "select auavm.* " +
        "from " +
        "user au , " +
        "user_vendor_mapping auavm ," +
        "user_authority_mapping auaam " +
        "where " +
        "auavm.user_id=au.id and auavm.vendor_id=?1 and auavm.user_id=?2 " +
        "and auaam.user_id=au.id and auaam.user_id=?2 and auaam.authority_id=5",nativeQuery = true)
    UserVendorMapping findByUserAndVendor(Long aidasVendorId, Long aidasUserId);
}
