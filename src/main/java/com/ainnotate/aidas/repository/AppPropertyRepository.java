package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AppProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link AppProperty} entity.
 */
@Repository
@Transactional
public interface AppPropertyRepository extends JpaRepository<AppProperty, Long> {

    @Query(value = "select * from app_property app where app.user_id=?1 and app.name=?2",nativeQuery = true)
    AppProperty getAppProperty(Long aidasUserId, String key );

    @Query(value = "select * from app_property app where app.user_id=?1 and app.name like concat('%',?2) ",nativeQuery = true)
    List<AppProperty> getAppPropertyLike(Long aidasUserId, String key );

    @Query(value = "select * from app_property app where app.organisation_id=?1 and app.name=?2",nativeQuery = true)
    AppProperty getAppPropertyOrg(Long aidasOrganisationId, String key );

    @Query(value = "select * from app_property app where app.organisation_id=?1 and app.name like concat('%',?2) ",nativeQuery = true)
    List<AppProperty> getAppPropertyOrgLike(Long aidasOrganisationId, String key );

    @Query(value = "select * from app_property app where app.customer_id=?1 and app.name=?2",nativeQuery = true)
    AppProperty getAppPropertyCustomer(Long aidasCustomerId, String key );

    @Query(value = "select * from app_property app where app.customer_id=?1 and app.name  like concat('%',?2) ",nativeQuery = true)
    List<AppProperty> getAppPropertyCustomerLike(Long aidasCustomerId, String key );

    @Query(value = "select * from app_property app where app.vendor_id=?1 and app.name=?2",nativeQuery = true)
    AppProperty getAppPropertyVendor(Long aidasVendorId, String key );

    @Query(value = "select * from app_property app where app.vendor_id=?1 and app.name like concat('%',?2) ",nativeQuery = true)
    List<AppProperty> getAppPropertyVendorLike(Long aidasVendorId, String key );
}
