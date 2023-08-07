package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.*;
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

    @Query(value = "select * from user_organisation_mapping uom, user_authority_mapping uam where uom.user_id=uam.user_id and uam.authority_id=6 and uom.is_sample_data=1 ",nativeQuery = true)
    List<UserOrganisationMapping> getAllSampleUserOrganisationMappingsForQc();
    
    @Query(value="select * from user_organisation_mapping uom where uom.user_id=?1 and uom.status=1", nativeQuery =  true)
    List<UserOrganisationMapping> getAllOrganisationForSelectedUser(Long userId);
}
