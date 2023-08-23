package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.dto.UserAuthorityMappingDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Spring Data JPA repository for the {@link UserAuthorityMapping} entity.
 */
@Repository
@Transactional
public interface UserAuthorityMappingRepository extends JpaRepository<UserAuthorityMapping, Long> {

    @Query(value="select * from user_authority_mapping uam where uam.authority_id=?1 and user_id=?2",nativeQuery = true)
    UserAuthorityMapping findByUamIdAndUserId(Long uamId, Long userId);
    
    @Query(value="select * from user_authority_mapping uam where uam.authority_id=?1 and user_id=?2",nativeQuery = true)
    UserAuthorityMapping findByAuthorityIdAndUserId(Long authorityId, Long userId);
    
    
    @Query(value="select * from user_authority_mapping uam where  user_id=?1 and uam.status=1",nativeQuery = true)
    Set<UserAuthorityMapping> findByUserId( Long userId);
    
    @Query(value="select a.* from user_authority_mapping where  user_id=?1",nativeQuery = true)
    Set<Authority> findUserAuthoritiesByUserId( Long userId);

    @Query(value = "select * from user_authority_mapping where is_sample_data=1",nativeQuery = true)
    List<UserAuthorityMapping> getAllSampleUserAuthorityMappings();

    @Modifying
    @Query(value = "delete from user_authority_mapping where is_sample_data=1 order by id desc",nativeQuery = true)
    void deleteAllSampleUserAuthorityMappings();
    
    @Query(nativeQuery = true)
    List<UserAuthorityMappingDTO> getAllAuthoritiesOfUser(Long userId);

}
