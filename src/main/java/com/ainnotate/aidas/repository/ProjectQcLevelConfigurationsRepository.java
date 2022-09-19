package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.ProjectQcLevelConfigurations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ProjectQcLevelConfigurationsRepository extends JpaRepository<ProjectQcLevelConfigurations,Long> {


    @Query(value="select pqlc.* from project_qc_level_configurations pqlc  where pqlc.project_id=?1  ",nativeQuery = true)
    List<ProjectQcLevelConfigurations> findByProejctId(Long projectId);

    @Query(value="select pqlc.* from project_qc_level_configurations pqlc  where pqlc.project_id=?1 and qc_level=?2 ",nativeQuery = true)
    ProjectQcLevelConfigurations findByProejctIdAndQcLevel(Long projectId,Integer qcLevel);


}
