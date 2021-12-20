package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasCustomer;
import com.ainnotate.aidas.domain.AidasOrganisation;
import com.ainnotate.aidas.domain.AidasUser;
import com.ainnotate.aidas.domain.AidasVendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the AidasUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasUserRepository extends JpaRepository<AidasUser, Long> {


    Optional<AidasUser> findByLogin(String login);

    Optional<AidasUser> findOneByLogin(String login);

    Page<AidasUser> findAllByAidasCustomer(Pageable pageable, AidasCustomer aidasCustomer);
    Page<AidasUser> findAllByAidasOrganisation_OrAidasCustomer_AidasOrganisation(Pageable pageable, AidasOrganisation aidasOrganisation,AidasOrganisation aidasCustomerOrganisation);
    Page<AidasUser> findAllByAidasVendor(Pageable pageable, AidasVendor aidasVendor);

    Page<AidasUser> findAllByIdGreaterThan(Long id,Pageable page);

}
