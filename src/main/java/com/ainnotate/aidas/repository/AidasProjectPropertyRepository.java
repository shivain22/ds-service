package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasObjectProperty;
import com.ainnotate.aidas.domain.AidasProjectProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the AidasProjectProperty entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasProjectPropertyRepository extends JpaRepository<AidasProjectProperty, Long> {

    @Query(value = "select * from aidas_project_property app where app.aidas_project_id>?1",nativeQuery = true)
    Page<AidasProjectProperty> findAllByAidasProjectIdGreaterThan(Pageable page, Long projectId);

    @Query(value="select * from aidas_project_property app,aidas_properties ap where app.aidas_project_id=?1 and app.aidas_properties_id=ap.id and ap.property_type=2",nativeQuery = true)
    List<AidasProjectProperty> findAllAidasProjectPropertyForMetadata(Long projectId);

    AidasProjectProperty findByAidasProject_IdAndAidasProperties_Id(Long aidasProjectId, Long aidasPrpoertiesId);

    Long countAidasProjectPropertiesByAidasProject_IdAndOptionalEquals(Long projectId, Integer optional);
}
