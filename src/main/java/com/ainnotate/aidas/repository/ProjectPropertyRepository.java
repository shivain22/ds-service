package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.ProjectProperty;
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
public interface ProjectPropertyRepository extends JpaRepository<ProjectProperty, Long> {

    @Query(value = "select * from project_property app where app.project_id>?1",nativeQuery = true)
    Page<ProjectProperty> findAllByAidasProjectIdGreaterThan(Pageable page, Long projectId);

    @Query(value="select * from project_property app,property ap where app.project_id=?1 and app.property_id=ap.id and ap.property_type=2 " +
        "union " +
        "select * from project_property app,property ap where app.project_id=?1 and app.property_id=ap.id and ap.property_type=1 and ap.add_to_metadata=1",nativeQuery = true)
    List<ProjectProperty> findAllAidasProjectPropertyForMetadata(Long projectId);

    @Query(value = "select * from project_property pp where pp.project_id=?1 and pp.property_id=?2",nativeQuery = true)
    ProjectProperty findByProjectAndProperty(Long aidasProjectId, Long aidasPrpoertiesId);

    @Query(value="select count(*) from project_property pp where pp.project_id=?1 and pp.optional=?2",nativeQuery = true)
    Long countProjectPropertyByProjectAndOptional(Long projectId, Integer optional);
}
