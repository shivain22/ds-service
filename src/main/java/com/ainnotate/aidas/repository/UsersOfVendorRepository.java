package com.ainnotate.aidas.repository;

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
public interface UsersOfVendorRepository extends JpaRepository<UsersOfVendor, String> {

    @Query(value="select *  from vendor_user_project_level_status where project_id=?1", nativeQuery = true)
    List<UsersOfVendor> getUserOfVendor(Long projectId);
}
