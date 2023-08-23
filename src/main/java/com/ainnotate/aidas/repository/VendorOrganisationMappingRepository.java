package com.ainnotate.aidas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.VendorOrganisationMapping;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
@Repository
@Transactional
public interface VendorOrganisationMappingRepository extends JpaRepository<VendorOrganisationMapping, Long> {

	@Query(value="select * from vendor_organisation_mapping vom where vom.organisation_id=?1 and vom.vendor_id=?2",nativeQuery = true)
    VendorOrganisationMapping getByOrgIdAndVendorId(Long orgId, Long vendorId);
}
