package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AppProperty;
import com.ainnotate.aidas.domain.UserAuthorityMapping;
import com.ainnotate.aidas.domain.UserCustomerMapping;
import com.ainnotate.aidas.domain.UserVendorMapping;
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
}
