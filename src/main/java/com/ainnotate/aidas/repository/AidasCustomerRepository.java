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

}
