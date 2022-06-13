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
    AppProperty getAidasAppProperty(Long aidasUserId, String key );

    @Query(value = "select * from app_property app where app.organisation_id=?1 and app.name=?2",nativeQuery = true)
    AppProperty getAidasAppPropertyOrg(Long aidasOrganisationId, String key );

    @Query(value = "select * from app_property app where app.customer_id=?1 and app.name=?2",nativeQuery = true)
    AppProperty getAidasAppPropertyCustomer(Long aidasCustomerId, String key );

    @Query(value = "select * from app_property app where app.vendor_id=?1 and app.name=?2",nativeQuery = true)
    AppProperty getAidasAppPropertyVendor(Long aidasVendorId, String key );
}
