package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.ProjectProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data SQL repository for the AidasProjectProperty entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface ProjectPropertyRepository extends JpaRepository<ProjectProperty, Long> {

    @Query(value = "select * from project_property app where app.project_id>?1",nativeQuery = true)
    Page<ProjectProperty> findAllByAidasProjectIdGreaterThan(Pageable page, Long projectId);

    @Query(value = "select * from project_property app where app.project_id=?1",nativeQuery = true)
    List<ProjectProperty> findAllByAidasProjectIdGreaterThanForDropDown(Long projectId);

    @Query(value="select * from project_property pp where pp.project_id=?1 and pp.project_property_type=2 and pp.show_to_vendor_user=1",nativeQuery = true)
    List<ProjectProperty> findAllMetaDataToBeFilledByVendorUser(Long projectId);

    @Query(value = "select * from project_property pp where pp.project_id=?1 and pp.property_id=?2",nativeQuery = true)
    ProjectProperty findByProjectAndProperty(Long aidasProjectId, Long aidasPrpoertiesId);

    @Query(value="select count(*) from project_property pp where pp.project_id=?1 and pp.optional=?2",nativeQuery = true)
    Long countProjectPropertyByProjectAndOptional(Long projectId, Integer optional);

    @Query(value = "select * from project_property where project_id=?1",nativeQuery = true)
    List<ProjectProperty> findAllProjectProperty(Long projectId);

    @Modifying
    @Query(value = "delete from project_property where is_sample_data=1 order by id desc",nativeQuery = true)
    void deleteAllSampleProjectProperty();

    @Query(value = "select count(*) from project_property pp where pp.project_id=?1 and pp.optional=0",nativeQuery = true)
    Integer getMandatoryPropertyCountForProject(Long projectId);
}
