package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.Customer;
import com.ainnotate.aidas.domain.Organisation;
import com.ainnotate.aidas.domain.Project;
import com.ainnotate.aidas.domain.QProject;
import com.ainnotate.aidas.domain.QVendor;
import com.ainnotate.aidas.domain.Vendor;
import com.ainnotate.aidas.domain.VendorCustomerMapping;
import com.ainnotate.aidas.dto.UserCustomerMappingDTO;
import com.ainnotate.aidas.dto.UserVendorMappingDTO;
import com.ainnotate.aidas.dto.VendorCustomerMappingDTO;
import com.ainnotate.aidas.dto.VendorOrganisationMappingDTO;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Spring Data SQL repository for the AidasVendor entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface VendorRepository extends JpaRepository<Vendor, Long>,QuerydslPredicateExecutor<Vendor>, QuerydslBinderCustomizer<QVendor> {

    @Query(value = "select * from vendor where name=?1 and is_sample_data=1",nativeQuery = true)
    Vendor findSampleVendorByName(String name);

    @Query(value = "select * from vendor where id>0 and status=1",countQuery = "select count(*) from vendor where id>0 and status=1", nativeQuery = true)
    Page<Vendor> findAllByIdGreaterThan(Long id, Pageable page);
    
    @Query(value = "select * from vendor where id>0 and id=?1 and status=1",
    		countQuery = "select count(*) from vendor where id>0 and id=?1 and status=1", nativeQuery = true)
    Page<Vendor> findAllByVendor(Long vendorId, Pageable page);
    
    @Query(value = "select * from vendor v, vendor_organisation_mapping vom where v.id>0 and vom.vendor_id=v.id and vom.organisation_id=?1 and v.status=1",
    		countQuery = "select count(*) from vendorv, vendor_organisation_mapping vom where v.id>0 and vom.vendor_id=v.id and vom.organisation_id=?1 and v.status=1", nativeQuery = true)
    Page<Vendor> findAllByOrganisation(Long orgId, Pageable page);
    
    @Query(value = "select * from vendor v, vendor_customer_mapping vcm where v.id>0 and vcm.vendor_id=v.id and vcm.customer_id=?1 and v.status=1",
    		countQuery = "select count(*) from vendor v, vendor_customer_mapping vcm where v.id>0 and vcm.vendor_id=v.id and vcm.customer_id=?1 and v.status=1", nativeQuery = true)
    Page<Vendor> findAllByCustomer(Long customerId, Pageable page);

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

    @Query(value = "select * from vendor where is_sample_data=1",nativeQuery = true)
    List<Vendor> getAllSampleVendors();

    @Modifying
    @Query(value = "delete from vendor where is_sample_data=1 order by id desc",nativeQuery = true)
    void deleteAllSampleVendors();
    

    @Query(value="select v.* from vendor v, user_vendor_mapping uvm,user_authority_mapping uam, uam_uvm_mapping uum where "
    		+ "uum.uam_id=uam.id and uam.authority_id=?2 and uam.user_id=?1 and uum.uvm_id=uvm.id and uum.uam_id=uam.id and uum.status=1 and uvm.vendor_id=v.id and uvm.user_id=?1 and uam.authority_id=?2 and v.id>-1",nativeQuery = true)
    List<Vendor> getVendors(Long userId,Long authorityId);
    
    @Override
    default public void customize(
        QuerydslBindings bindings, QVendor root) {
        bindings.bind(String.class)
            .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);

    }
    @Query(nativeQuery = true)
    List<UserVendorMappingDTO> getAllVendorsWithUamId(Long userId, Long authorityId);
    
    @Query(nativeQuery = true)
    List<UserVendorMappingDTO> getAllVendorsWithoutUamId(Long userId);
    
    
    @Query(nativeQuery = true)
    List<UserVendorMappingDTO> getAllVendorsOfOrganisation(Long organisationId);
    
    @Query(nativeQuery = true)
    List<UserVendorMappingDTO> getAllVendorsOfCustomer(Long customerId);
    
    
    @Query(nativeQuery = true)
    List<VendorCustomerMappingDTO> getAllCustomers(Long vendorId);
    
    @Query(nativeQuery = true)
    List<VendorOrganisationMappingDTO> getAllOrganisations(Long vendorId);
}
