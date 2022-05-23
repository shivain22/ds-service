package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasObjectProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AidasObjectProperty entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasObjectPropertyRepository extends JpaRepository<AidasObjectProperty, Long> {

    @Query(value = "select * from aidas_object_property aop where aop.aidas_object_id>?1",nativeQuery = true)
    Page<AidasObjectProperty> findAllByAidasObjectIdGreaterThan(Pageable page, Long objectId);

    AidasObjectProperty findByAidasObject_IdAndAidasProperties_Id(Long aidasObjectId, Long aidasPropertiesId);

}
