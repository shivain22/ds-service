package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AppProperty;
import com.ainnotate.aidas.domain.Organisation;
import com.ainnotate.aidas.domain.UserOrganisationMapping;
import com.ainnotate.aidas.domain.UserVendorMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link UserOrganisationMapping} entity.
 */
@Repository
public interface UserOrganisationMappingRepository extends JpaRepository<UserOrganisationMapping, Long> {

    @Query(value="select * from user_organisation_mapping where organisation_id=?1 and user_id=?2",nativeQuery = true)
    UserOrganisationMapping findByOrganisationIdAndUserId(Long organisationId,Long userId);
    @Query(value = "select * from organisation where is_sample_data=1",nativeQuery = true)
    List<Organisation> getAllSampleUserOrganisationMappings();

    @Query(value = "delete from organisation where is_sample_data=1",nativeQuery = true)
    List<Organisation> deleteAllSampleUserOrganisationMappings();
}
