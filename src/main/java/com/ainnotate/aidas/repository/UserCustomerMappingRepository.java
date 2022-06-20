package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link UserCustomerMapping} entity.
 */
@Repository
public interface UserCustomerMappingRepository extends JpaRepository<UserCustomerMapping, Long> {

    @Query(value="select * from user_customer_mapping where customer_id=?1 and user_id=?2",nativeQuery = true)
    UserCustomerMapping findByCustomerIdAndUserId(Long customerId, Long userId);

    @Query(value = "select * from user_customer_mapping where is_sample_data=1",nativeQuery = true)
    List<Organisation> getAllSampleUserCustomerMappings();

    @Query(value = "delete from user_customer_mapping where is_sample_data=1",nativeQuery = true)
    List<Organisation> deleteAllSampleUserCustomerMappings();
}
