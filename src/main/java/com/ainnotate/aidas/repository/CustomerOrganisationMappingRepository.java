package com.ainnotate.aidas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.CustomerOrganisationMapping;
import com.ainnotate.aidas.domain.VendorOrganisationMapping;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
@Repository
@Transactional
public interface CustomerOrganisationMappingRepository extends JpaRepository<CustomerOrganisationMapping, Long> {

	@Query(value="select * from customer_organisation_mapping com where com.organisation_id=?1 and com.customer_id=?2",nativeQuery = true)
    CustomerOrganisationMapping getByOrgIdAndCustomerId(Long orgId, Long customerId);
}
