package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasObjectProperty;
import com.ainnotate.aidas.domain.AidasProjectProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AidasProjectProperty entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasProjectPropertyRepository extends JpaRepository<AidasProjectProperty, Long> {

    @Query(value = "select * from aidas_project_property aop where aop.aidas_project_id>?1",nativeQuery = true)
    Page<AidasProjectProperty> findAllByAidasProjectIdGreaterThan(Pageable page, Long objectId);
}
