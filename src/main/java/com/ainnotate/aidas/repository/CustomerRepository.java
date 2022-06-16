package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.Customer;
import com.ainnotate.aidas.domain.Organisation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the AidasCustomer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {


    @Query(value="select * from customer ac where ac.organisation_id=?1 and status=1",nativeQuery = true)
    Page<Customer> findAllByAidasOrganisation(Pageable page, Organisation organisation);

    @Query(value="select * from customer ac where id>?1 and status=1",nativeQuery = true)
    Page<Customer> findAllByIdGreaterThan(Pageable page, Long id);

    @Query(value="select * from customer ac where ac.id=?1 and status=1",nativeQuery = true)
    Page<Customer> findAllByIdEquals(Pageable page, Long customerId);

    @Query(value="select count(*) from customer ac where ac.organisation_id=?1 and status=1",nativeQuery = true)
    Long countAidasCustomerByAidasOrganisation(Organisation organisation);

    @Query(value = "select count(*) from (select ac.id,count(*) from user_vendor_mapping_object_mapping auavmaom, user_vendor_mapping auavm, user au, object ao, project ap, customer ac where auavmaom.object_id=ao.id and auavmaom.user_vendor_mapping_id=auavm.id and  auavm.user_id=au.id and ao.project_id=ap.id and ap.customer_id=ac.id and auavm.vendor_id=?1 group by ac.id)a",nativeQuery = true)
    Long countAidasCustomerCountForVendorAdmin(Long aidasVendorId);

    @Query(value="select count(*) from customer where id>0 and status=1", nativeQuery = true)
    Long countAidasCustomersForSuperAdmin();

    @Query(value = "select * from customer where status=1",nativeQuery = true)
    List<Customer> findAllCustomer();


    @Query(value = "select * from customer where status=1 and organisation_id=?1",nativeQuery = true)
    List<Customer> findAllCustomer(Long organisationId);


}
