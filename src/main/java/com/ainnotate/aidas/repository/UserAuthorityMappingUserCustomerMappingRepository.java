package com.ainnotate.aidas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.UserAuthorityMappingUserCustomerMapping;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
@Repository
@Transactional
public interface UserAuthorityMappingUserCustomerMappingRepository extends JpaRepository<UserAuthorityMappingUserCustomerMapping, Long> {

    
	@Query(value = "select * from uam_ucm_mapping uamucm where uamucm.ucm_id=?1 and uamucm.uam_id=?2",nativeQuery = true)
	UserAuthorityMappingUserCustomerMapping getByUcmIdAndUamId(Long ucmId,Long uamId);
	
	@Query(value = "select * from uam_ucm_mapping uamucm where uamucm.uam_id=?1",nativeQuery = true)
	List<UserAuthorityMappingUserCustomerMapping> getByUamId(Long uamId);
}
