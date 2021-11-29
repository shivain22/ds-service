package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the {@link AidasAuthority} entity.
 */
public interface AidasAuthorityRepository extends JpaRepository<AidasAuthority, Long> {

    AidasAuthority findByName(String name);
}
