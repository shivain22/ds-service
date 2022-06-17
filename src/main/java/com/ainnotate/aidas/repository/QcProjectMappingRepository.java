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


    @Query(value = "select * from qc_project_mapping qpm,  user_customer_mapping ucm where " +
        "qpm.project_id=?1 and qpm.user_customer_mapping_id = ucm.id and ucm.customer_id=?2 and ucm.user_id=?3",nativeQuery = true)
    QcProjectMapping getQcProjectMappingByProjectAndCustomerAndUser(Long projectId, Long customerId, Long userId);
}
