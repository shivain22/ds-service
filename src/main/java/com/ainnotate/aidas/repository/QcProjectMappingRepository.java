package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AppProperty;
import com.ainnotate.aidas.domain.QcProjectMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link QcProjectMapping} entity.
 */
@Repository
@Transactional
public interface QcProjectMappingRepository extends JpaRepository<QcProjectMapping, Long> {


    @Query(value = "select * from qc_project_mapping qpm,  user_customer_mapping ucm where " +
        "qpm.project_id=?1 and qpm.user_customer_mapping_id = ucm.id and ucm.customer_id=?2 and ucm.user_id=?3 and qpm.qc_level=?4",nativeQuery = true)
    QcProjectMapping getQcProjectMappingByProjectAndCustomerAndUserAndLevel(Long projectId, Long customerId, Long userId,Long qcLevel);

    @Query(value = "select * from qc_project_mapping qpm,  user_customer_mapping ucm where " +
        "qpm.project_id=?1 and qpm.user_customer_mapping_id = ucm.id and ucm.customer_id=?2 and ucm.user_id=?3 order by qpm.qc_level asc",nativeQuery = true)
    List<QcProjectMapping> getQcProjectMappingByProjectAndCustomerAndUser(Long projectId, Long customerId, Long userId);




}
