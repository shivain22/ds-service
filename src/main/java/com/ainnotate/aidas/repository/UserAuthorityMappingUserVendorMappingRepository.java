package com.ainnotate.aidas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.UserAuthorityMappingUserVendorMapping;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
@Repository
@Transactional
public interface UserAuthorityMappingUserVendorMappingRepository extends JpaRepository<UserAuthorityMappingUserVendorMapping, Long> {
	@Query(value = "select * from uam_uvm_mapping uamuvm where uamuvm.uvm_id=?1 and uamuvm.uam_id=?2",nativeQuery = true)
	UserAuthorityMappingUserVendorMapping getByUvmIdAndUamId(Long uvmId,Long uamId);
	
	@Query(value = "select * from uam_uvm_mapping uamuvm where uamuvm.uam_id=?1",nativeQuery = true)
	List<UserAuthorityMappingUserVendorMapping> getByUamId(Long uamId);

}
