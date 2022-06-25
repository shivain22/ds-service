package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AppProperty;
import com.ainnotate.aidas.domain.Organisation;
import com.ainnotate.aidas.domain.UserOrganisationMapping;
import com.ainnotate.aidas.domain.UserVendorMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link UserOrganisationMapping} entity.
 */
@Repository
@Transactional
public interface UserOrganisationMappingRepository extends JpaRepository<UserOrganisationMapping, Long> {

    @Query(value="select * from user_organisation_mapping where organisation_id=?1 and user_id=?2",nativeQuery = true)
    UserOrganisationMapping findByOrganisationIdAndUserId(Long organisationId,Long userId);
    @Query(value = "select * from user_organisation_mapping where is_sample_data=1",nativeQuery = true)
    List<UserOrganisationMapping> getAllSampleUserOrganisationMappings();

    @Modifying
    @Query(value = "delete from user_organisation_mapping where is_sample_data=1 order by id desc",nativeQuery = true)
    void deleteAllSampleUserOrganisationMappings();
}
