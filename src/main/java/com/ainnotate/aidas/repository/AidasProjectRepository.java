package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasCustomer;
import com.ainnotate.aidas.domain.AidasOrganisation;
import com.ainnotate.aidas.domain.AidasProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AidasProject entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasProjectRepository extends JpaRepository<AidasProject, Long> {

    Page<AidasProject> findAllByAidasCustomer_AidasOrganisation(Pageable page, AidasOrganisation organisation);
    Page<AidasProject> findAllByAidasCustomer(Pageable page,AidasCustomer aidasCustomer);
}
