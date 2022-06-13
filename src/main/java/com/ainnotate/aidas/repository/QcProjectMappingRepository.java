package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AppProperty;
import com.ainnotate.aidas.domain.QcProjectMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link QcProjectMapping} entity.
 */
@Repository
public interface QcProjectMappingRepository extends JpaRepository<QcProjectMapping, Long> {


}
