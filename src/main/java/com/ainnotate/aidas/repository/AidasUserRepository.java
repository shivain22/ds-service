package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.*;
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

    Page<AidasUser> findAllByDeletedIsFalseAndAidasCustomer(Pageable pageable, AidasCustomer aidasCustomer);
    Page<AidasUser> findAllByDeletedIsFalseAndAidasOrganisation_OrAidasCustomer_AidasOrganisation(Pageable pageable, AidasOrganisation aidasOrganisation,AidasOrganisation aidasCustomerOrganisation);
    Page<AidasUser> findAllByDeletedIsFalseAndAidasVendor(Pageable pageable, AidasVendor aidasVendor);

    Page<AidasUser> findAllByIdGreaterThanAndDeletedIsFalse(Long id,Pageable page);

    List<AidasUser>findAllByAidasAuthoritiesEquals(AidasAuthority aidasAuthority);

    Long countAllByAidasOrganisation(AidasOrganisation aidasOrganisation);
    Long countAllByAidasCustomer(AidasCustomer aidasCustomer);
    Long countAllByAidasVendor(AidasVendor aidasVendor);

    Long countAllByAidasCustomer_AidasOrganisation(AidasOrganisation aidasOrganisation);

    @Query(value="select count(*) from aidas_user au, aidas_user_aidas_authority auaa where au.id=auaa.aidas_user_id and auaa.aidas_authority_id=5", nativeQuery = true)
    Long countAllVendorUsers();

}
