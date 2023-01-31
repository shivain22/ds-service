package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
@Repository
@Transactional
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Authority findByName(String name);


}
