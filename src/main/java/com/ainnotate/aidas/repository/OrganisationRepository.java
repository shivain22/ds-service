package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.Organisation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data SQL repository for the AidasOrganisation entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface OrganisationRepository extends JpaRepository<Organisation, Long> {

    Page<Organisation> findAllById(Long id, Pageable page);

    @Query(value = "select * from organisation where id>?1 and status=1",countQuery = "select count(*) from organisation where id>?1 and status=1",nativeQuery = true)
    Page<Organisation> findAllByIdGreaterThan(Long id, Pageable page);

    @Query(value = "select * from organisation where id>?1 and status=1",countQuery = "select count(*) from organisation where id>?1 and status=1",nativeQuery = true)
    List<Organisation> findAllByIdGreaterThanForDropDown(Long id);

    @Query(value = "select * from organisation where id=?1 and status=1",nativeQuery = true)
    Page<Organisation> findAllByCustomer(Long id, Pageable page);

    @Query(value = "select count(*) from organisation where id > 0 and status=1", nativeQuery = true)
    Long countAllOrgsForSuperAdmin();

    @Query(value = "select * from organisation where is_sample_data=1 order by id asc",nativeQuery = true)
    List<Organisation> getAllSampleOrganisations();

    @Query(value = "delete from organisation where is_sample_data=1",nativeQuery = true)
    List<Organisation> deleteAllSampleOrganisations();
}
