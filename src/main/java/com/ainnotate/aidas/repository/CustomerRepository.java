package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.Customer;
import com.ainnotate.aidas.domain.Organisation;
import com.ainnotate.aidas.domain.Project;
import com.ainnotate.aidas.domain.QCustomer;
import com.ainnotate.aidas.domain.QProject;
import com.ainnotate.aidas.dto.UserCustomerMappingDTO;
import com.ainnotate.aidas.dto.UserOrganisationMappingDTO;
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
 * Spring Data SQL repository for the AidasCustomer entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface CustomerRepository extends JpaRepository<Customer, Long>,QuerydslPredicateExecutor<Customer>, QuerydslBinderCustomizer<QCustomer> {


    @Query(value = "select c.* from customer c, organisation o where c.organisation_id=o.id and c.name=? and o.name=? and c.is_sample_data=1 and o.is_sample_data=1",nativeQuery = true)
    Customer findSampleCustomerByNameAndOrgName(String customerName,String orgName);

    @Query(value="select * from customer ac where ac.organisation_id=?1 and status=1",nativeQuery = true)
    Page<Customer> findAllByAidasOrganisation(Pageable page, Organisation organisation);
    
    @Query(value="select * from customer ac where 1=2",nativeQuery = true)
    Page<Customer> findNone(Pageable page);

    @Query(value="select * from customer ac where ac.organisation_id=?1 and status=1",nativeQuery = true)
    List<Customer> findAllByAidasOrganisationForDropDown(Long organisationId);

    @Query(value="select * from customer ac where id>?1 and status=1",nativeQuery = true)
    Page<Customer> findAllByIdGreaterThan(Pageable page, Long id);

    @Query(value="select * from customer ac where id>?1 and status=1",nativeQuery = true)
    List<Customer> findAllByIdGreaterThanForDropDown(Long id);

    @Query(value="select * from customer ac where ac.id=?1 and status=1",nativeQuery = true)
    Page<Customer> findAllByIdEquals(Pageable page, Long customerId);

    @Query(value="select * from customer ac where ac.id=?1 and status=1",nativeQuery = true)
    List<Customer> findAllByIdEqualsForDropDown(Long customerId);

    @Query(value="select count(*) from customer ac where ac.organisation_id=?1 and status=1",nativeQuery = true)
    Long countAidasCustomerByAidasOrganisation(Long organisationId);

    @Query(value = "select count(*) from (select ac.id,count(*) from user_vendor_mapping_object_mapping auavmaom, user_vendor_mapping auavm, user au, object ao, project ap, customer ac where auavmaom.object_id=ao.id and auavmaom.user_vendor_mapping_id=auavm.id and  auavm.user_id=au.id and ao.project_id=ap.id and ap.customer_id=ac.id and auavm.vendor_id=?1 group by ac.id)a",nativeQuery = true)
    Long countAidasCustomerCountForVendorAdmin(Long aidasVendorId);

    @Query(value="select count(*) from customer where id>0 and status=1", nativeQuery = true)
    Long countAidasCustomersForSuperAdmin();

    @Query(value = "select * from customer where status=1",nativeQuery = true)
    List<Customer> findAllCustomer();


    @Query(value = "select * from customer where status=1 and organisation_id=?1",nativeQuery = true)
    List<Customer> findAllCustomer(Long organisationId);

    @Query(value = "select * from customer where is_sample_data=1",nativeQuery = true)
    List<Customer> getAllSampleCustomers();

    @Modifying
    @Query(value = "delete from customer where is_sample_data=1 order by id desc",nativeQuery = true)
    void deleteAllSampleCustomers();
    

    @Query(value="select c.* from customer c, user_customer_mapping ucm,user_authority_mapping uam, "
    		+ "uam_ucm_mapping uum where uum.uam_id=uam.id and uam.authority_id=?2 and uam.user_id=?1 and uum.ucm_id=ucm.id and uum.uam_id=uam.id "
    		+ "and uum.status=1 and ucm.customer_id=c.id and ucm.user_id=?1 and c.id>-1",nativeQuery = true)
    List<Customer> getCustomers(Long userId, Long authorityId);
    @Override
    default public void customize(
        QuerydslBindings bindings, QCustomer root) {
        bindings.bind(String.class)
            .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);

    }
    @Query(nativeQuery = true)
    List<UserCustomerMappingDTO> getAllCustomersWithUamId(Long userId, Long authorityId);
    @Query(nativeQuery = true)
    List<UserCustomerMappingDTO> getAllCustomersWithoutUamId(Long userId);
    
    @Query(nativeQuery = true)
    List<UserCustomerMappingDTO> getAllCustomersWithUamIdAndCustomerId(Long uamId,Long customerId);
    
    @Query(nativeQuery = true)
    List<UserCustomerMappingDTO> getAllCustomersWithUamIdAndOrgId(Long uamId,Long orgId);
    
    
}
