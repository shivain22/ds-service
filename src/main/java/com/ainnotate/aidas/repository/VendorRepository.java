package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the AidasVendor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    @Query(value = "select * from vendor where id>0 and status=1",countQuery = "select count(*) from vendor where id>0 and status=1", nativeQuery = true)
    Page<Vendor> findAllByIdGreaterThan(Long id, Pageable page);

    @Query(value = "select * from vendor where id>0 and status=1",countQuery = "select count(*) from vendor where id>0 and status=1", nativeQuery = true)
    List<Vendor> findAllByIdGreaterThanForDropDown(Long id);

    @Query(value = "select * from vendor v where v.status=1 and id>0",nativeQuery = true)
    List<Vendor> getAllVendors();

    @Query(value = "select count(*) from vendor where id>0 and status=1", nativeQuery = true)
    Long countAllVendorsForSuperAdmin();

    @Query(value = "select count(*) from vendor where id>0 and status=1", nativeQuery = true)
    Long countAllVendorsForOrgAdmin();

    @Query(value = "select count(*) from vendor where id>0 and status=1", nativeQuery = true)
    Long countAllVendorsForCustomerAdmin();


}
