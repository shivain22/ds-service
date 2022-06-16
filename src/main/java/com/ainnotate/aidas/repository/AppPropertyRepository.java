package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AppProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link AppProperty} entity.
 */
@Repository
public interface AppPropertyRepository extends JpaRepository<AppProperty, Long> {

    @Query(value = "select * from app_property app where app.user_id=?1 and app.name=?2",nativeQuery = true)
    AppProperty getAppProperty(Long aidasUserId, String key );

    @Query(value = "select * from app_property app where app.user_id=?1 and app.name=?2",nativeQuery = true)
    AppProperty getAppPropertyLike(Long aidasUserId, String key );

    @Query(value = "select * from app_property app where app.organisation_id=?1 and app.name=?2",nativeQuery = true)
    AppProperty getAppPropertyOrg(Long aidasOrganisationId, String key );

    @Query(value = "select * from app_property app where app.organisation_id=?1 and app.name=?2",nativeQuery = true)
    AppProperty getAppPropertyOrgLike(Long aidasOrganisationId, String key );

    @Query(value = "select * from app_property app where app.customer_id=?1 and app.name=?2",nativeQuery = true)
    AppProperty getAppPropertyCustomer(Long aidasCustomerId, String key );

    @Query(value = "select * from app_property app where app.customer_id=?1 and app.name like ?2",nativeQuery = true)
    AppProperty getAppPropertyCustomerLike(Long aidasCustomerId, String key );

    @Query(value = "select * from app_property app where app.vendor_id=?1 and app.name=?2",nativeQuery = true)
    AppProperty getAppPropertyVendor(Long aidasVendorId, String key );

    @Query(value = "select * from app_property app where app.vendor_id=?1 and app.name like concat('%',?2) ",nativeQuery = true)
    AppProperty getAppPropertyVendorLike(Long aidasVendorId, String key );
}
