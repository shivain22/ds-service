package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.CustomerQcProjectMapping;
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
public interface CustomerQcProjectMappingRepository extends JpaRepository<CustomerQcProjectMapping, Long> {


    @Query(value = "select * from customer_qc_project_mapping cqpm,  user_customer_mapping ucm where " +
        "cqpm.project_id=?1 and cqpm.user_customer_mapping_id = ucm.id and ucm.customer_id=?2 and ucm.user_id=?3 and cqpm.qc_level=?4",nativeQuery = true)
    CustomerQcProjectMapping getQcProjectMappingByProjectAndCustomerAndUserAndLevel(Long projectId, Long customerId, Long userId, Long qcLevel);

    @Query(value = "select * from customer_qc_project_mapping cqpm,  user_customer_mapping ucm where " +
        "cqpm.project_id=?1 and cqpm.user_customer_mapping_id = ucm.id and ucm.customer_id=?2 and ucm.user_id=?3 order by cqpm.qc_level asc",nativeQuery = true)
    List<CustomerQcProjectMapping> getQcProjectMappingByProjectAndCustomerAndUser(Long projectId, Long customerId, Long userId);




}
