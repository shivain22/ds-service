package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.QcUsersOfCustomer;
import com.ainnotate.aidas.domain.UsersOfVendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data SQL repository for the AidasVendor entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface QcUsersOfCustomerRepository extends JpaRepository<QcUsersOfCustomer, String> {

    @Query(value="select * from qc_users_of_customer quoc, project p where quoc.project_id=?1 and quoc.project_id=p.id and p.customer_id=?2", nativeQuery = true)
    List<QcUsersOfCustomer> getQcUserOfCustomer(Long projectId,Long customerId);
}
