package com.ainnotate.aidas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.dto.AuthorityDTO;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
@Repository
@Transactional
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Authority findByName(String name);
   
    @Query(nativeQuery = true,value = "select * from authority where id>=?1")
    List<Authority> getAllAuthority(Long currentAuthID);
    
    @Query(nativeQuery = true,value = "select * from authority where name in (?1)")
    List<Authority> getAllAuthorities(List<String> authorities);
    
    @Query(nativeQuery = true)
    List<AuthorityDTO>getAllAuthorityForRoleAssignment(Long currentAuthId);
    
    @Query(nativeQuery = true,value = "select * from authority a, user_authority_mapping uam where uam.authority_id=a.id and uam.id=?1")
    Authority getByUamId(Long uamId);
}
