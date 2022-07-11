package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.CustomerQcProjectMapping;
import com.ainnotate.aidas.domain.OrganisationQcProjectMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link CustomerQcProjectMapping} entity.
 */
@Repository
@Transactional
public interface OrganisationQcProjectMappingRepository extends JpaRepository<OrganisationQcProjectMapping, Long> {


    @Query(value = "select * from organisation_qc_project_mapping oqpm,  user_customer_mapping ucm where " +
        "oqpm.project_id=?1 and oqpm.user_organisation_mapping_id = ucm.id and ucm.customer_id=?2 and ucm.user_id=?3 and oqpm.qc_level=?4",nativeQuery = true)
    OrganisationQcProjectMapping getQcProjectMappingByProjectAndOrganisationAndUserAndLevel(Long projectId, Long organisationId, Long userId, Long qcLevel);

    @Query(value = "select * from organisation_qc_project_mapping oqpm,  user_customer_mapping ucm where " +
        "oqpm.project_id=?1 and oqpm.user_organisation_mapping_id = ucm.id and ucm.customer_id=?2 and ucm.user_id=?3 order by oqpm.qc_level asc",nativeQuery = true)
    List<OrganisationQcProjectMapping> getQcProjectMappingByProjectAndOrganisationAndUser(Long projectId, Long organisationId, Long userId);




}
