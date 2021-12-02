package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasCustomer;
import com.ainnotate.aidas.domain.AidasObject;
import com.ainnotate.aidas.domain.AidasOrganisation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AidasObject entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasObjectRepository extends JpaRepository<AidasObject, Long> {

    Page<AidasObject> findAllByAidasProject_AidasCustomer_AidasOrganisation
        (Pageable pageable, AidasOrganisation aidasOrganisation);

    Page<AidasObject> findAllByAidasProject_AidasCustomer(Pageable pageable, AidasCustomer aidasCustomer);
}
