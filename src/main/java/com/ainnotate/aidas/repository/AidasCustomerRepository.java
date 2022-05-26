package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasCustomer;
import com.ainnotate.aidas.domain.AidasOrganisation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AidasCustomer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasCustomerRepository extends JpaRepository<AidasCustomer, Long> {
    Page<AidasCustomer> findAllByAidasOrganisation(Pageable page, AidasOrganisation aidasOrganisation);
    Page<AidasCustomer> findAllByIdGreaterThan(Pageable page, Long id);
    Page<AidasCustomer> findAllByIdEquals(Pageable page, Long customerId);

    Long countAidasCustomerByAidasOrganisation(AidasOrganisation aidasOrganisation);

    @Query(value = "select count(*) from (select ac.id,count(*) from aidas_user_obj_map auom, aidas_user au, aidas_object ao, aidas_project ap, aidas_customer ac where auom.aidas_object_id=ao.id and auom.aidas_user_id=au.id and ao.aidas_project_id=ap.id and ap.aidas_customer_id=ac.id and au.aidas_vendor_id=?1 group by ac.id)a",nativeQuery = true)
    Long countAidasCustomerCountForVendorAdmin(Long aidasVendorId);

    @Query(value="select count(*) from aidas_customer where id>0", nativeQuery = true)
    Long countAidasCustomersForSuperAdmin();

}
