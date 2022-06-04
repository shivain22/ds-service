package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasObjectProperty;
import com.ainnotate.aidas.domain.AidasProjectProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the AidasObjectProperty entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasObjectPropertyRepository extends JpaRepository<AidasObjectProperty, Long> {

    @Query(value = "select * from aidas_object_property aop where aop.aidas_object_id>?1",nativeQuery = true)
    Page<AidasObjectProperty> findAllByAidasObjectIdGreaterThan(Pageable page, Long objectId);

    AidasObjectProperty findByAidasObject_IdAndAidasProperties_Id(Long aidasObjectId, Long aidasPropertiesId);

    //aidas_property_type =2 which is equal to property_type metadata
    @Query(value="select * from aidas_object_property aop,aidas_properties ap where aop.aidas_object_id=?1 and aop.aidas_properties_id=ap.id and ap.property_type=2",nativeQuery = true)
    List<AidasObjectProperty> findAllAidasObjectPropertyForMetadata(Long objectId);

    @Query(value = "select * from aidas_object_property aop where aop.aidas_object_id=?1 and aop.aidas_properties_id not in (select aidas_properties_id from aidas_project_property where aidas_project_id=?2)",nativeQuery = true)
    List<AidasObjectProperty> findAllUncommonAidasObjectPropertyForMetadata(Long objectId,Long projectId);

    @Query(value = "select count(*) from aidas_object_property aop where aop.aidas_object_id=?1 and aop.aidas_properties_id not in (select aidas_properties_id from aidas_project_property where aidas_project_id=?2) and aop.optional=1",nativeQuery = true)
    Long findAllUncommonMandatoryAidasObjectPropertyForMetadata(Long aidasObjectId, Long aidasProjectId);

    Long countAidasObjectPropertiesByAidasObject_IdAndOptionalEquals(Long objectId, Integer optional);
}
