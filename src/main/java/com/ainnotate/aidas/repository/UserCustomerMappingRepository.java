package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link UserCustomerMapping} entity.
 */
@Repository
@Transactional
public interface UserCustomerMappingRepository extends JpaRepository<UserCustomerMapping, Long> {

    @Query(value="select * from user_customer_mapping where customer_id=?1 and user_id=?2",nativeQuery = true)
    UserCustomerMapping findByCustomerIdAndUserId(Long customerId, Long userId);

    @Query(value = "select * from user_customer_mapping where is_sample_data=1",nativeQuery = true)
    List<UserCustomerMapping> getAllSampleUserCustomerMappings();

    @Modifying
    @Query(value = "delete from user_customer_mapping where is_sample_data=1 order by id desc",nativeQuery = true)
    void deleteAllSampleUserCustomerMappings();

    @Query(value = "select * from user_customer_mapping ucm, user_authority_mapping uam where ucm.user_id=uam.user_id and uam.authority_id=6",nativeQuery = true)
    List<UserCustomerMapping> getAllQcUserCustomerMapping(Long customerId);

    @Query(value = "select * from user_customer_mapping ucm, user_authority_mapping uam where ucm.user_id=uam.user_id and uam.authority_id=6 and ucm.is_sample_data=1 ",nativeQuery = true)
    List<UserCustomerMapping> getAllSampleUserCustomerMappingsForQc();
    
    @Query(value="select * from user_customer_mapping ucm where ucm.user_id=?1", nativeQuery = true)
    List<UserCustomerMapping> getAllCustomerForSelectedUser(Long userId);
}
