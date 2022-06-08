package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasOrganisation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AidasOrganisation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasOrganisationRepository extends JpaRepository<AidasOrganisation, Long> {

    Page<AidasOrganisation> findAllById(Long id, Pageable page);

    @Query(value = "select * from aidas_organisation where id>?1 and status=1",countQuery = "select count(*) from aidas_organisation where id>?1 and status=1",nativeQuery = true)
    Page<AidasOrganisation> findAllByIdGreaterThan(Long id, Pageable page);

    @Query(value = "select * from aidas_organisation where id=?1",nativeQuery = true)
    Page<AidasOrganisation> findAllByCustomer(Long id, Pageable page);

    @Query(value = "select count(*) from aidas_organisation where id > 0 ", nativeQuery = true)
    Long countAllOrgsForSuperAdmin();

}
