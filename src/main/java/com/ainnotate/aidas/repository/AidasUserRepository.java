package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasUser;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the AidasUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasUserRepository extends JpaRepository<AidasUser, Long> {


    Optional<AidasUser> findByLogin(String login);
}
