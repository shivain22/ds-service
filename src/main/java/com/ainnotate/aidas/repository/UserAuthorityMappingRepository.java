package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AppProperty;
import com.ainnotate.aidas.domain.UserAuthorityMapping;
import com.ainnotate.aidas.domain.UserOrganisationMapping;
import com.ainnotate.aidas.domain.UserVendorMapping;
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
}
