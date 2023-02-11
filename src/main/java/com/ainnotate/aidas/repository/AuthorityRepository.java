package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
@Repository
@Transactional
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Authority findByName(String name);
    @Query(nativeQuery = true,value = "select * from authority where id>=?1")
    List<Authority> getAllAuthority(Long currentAuthID);

}
