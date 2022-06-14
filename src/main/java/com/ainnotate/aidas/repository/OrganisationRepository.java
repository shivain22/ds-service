package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.Organisation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AidasOrganisation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Long> {

    Page<Organisation> findAllById(Long id, Pageable page);

    @Query(value = "select * from organisation where id>?1 and status=1",countQuery = "select count(*) from organisation where id>?1 and status=1",nativeQuery = true)
    Page<Organisation> findAllByIdGreaterThan(Long id, Pageable page);

    @Query(value = "select * from organisation where id=?1 and status=1",nativeQuery = true)
    Page<Organisation> findAllByCustomer(Long id, Pageable page);

    @Query(value = "select count(*) from organisation where id > 0 and status=1", nativeQuery = true)
    Long countAllOrgsForSuperAdmin();

}