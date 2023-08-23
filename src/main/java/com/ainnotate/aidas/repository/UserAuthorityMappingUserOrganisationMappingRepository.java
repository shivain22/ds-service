package com.ainnotate.aidas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.UserAuthorityMappingUserOrganisationMapping;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
@Repository
@Transactional
public interface UserAuthorityMappingUserOrganisationMappingRepository extends JpaRepository<UserAuthorityMappingUserOrganisationMapping, Long> {

   @Query(value = "select * from uam_uom_mapping uamuom where uamuom.uom_id=?1 and uamuom.uam_id=?2",nativeQuery = true)
   UserAuthorityMappingUserOrganisationMapping getByUomIdAndUamId(Long uomId,Long uamId);
   
  
   @Query(value = "select * from uam_uom_mapping uamuom where uamuom.uam_id=?1",nativeQuery = true)
   List<UserAuthorityMappingUserOrganisationMapping> getByUamId(Long uamId);

}
