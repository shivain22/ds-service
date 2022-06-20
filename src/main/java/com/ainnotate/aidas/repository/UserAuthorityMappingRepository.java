package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link UserAuthorityMapping} entity.
 */
@Repository
public interface UserAuthorityMappingRepository extends JpaRepository<UserAuthorityMapping, Long> {

    @Query(value="select * from user_authority_mapping where authority_id=?1 and user_id=?2",nativeQuery = true)
    UserAuthorityMapping findByAuthorityIdAndUserId(Long authorityId, Long userId);

    @Query(value = "select * from user_authority_mapping where is_sample_data=1",nativeQuery = true)
    List<Organisation> getAllSampleUserAuthorityMappings();

    @Query(value = "delete from user_authority_mapping where is_sample_data=1",nativeQuery = true)
    List<Organisation> deleteAllSampleUserAuthorityMappings();


}
