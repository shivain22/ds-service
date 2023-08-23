package com.ainnotate.aidas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.VendorCustomerMapping;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
@Repository
@Transactional
public interface VendorCustomerMappingRepository extends JpaRepository<VendorCustomerMapping, Long> {

	@Query(value="select * from vendor_customer_mapping vcm where vcm.customer_id=?1 and vcm.vendor_id=?2",nativeQuery = true)
	VendorCustomerMapping getByCustomerAndVendor(Long customerId, Long vendorId);
    
}
