package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the AidasProperties entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    Page<Property> findAllByPropertyTypeEquals(Pageable page, Long propertyType);
    @Query(value="select * from property where default_prop=1",nativeQuery = true)
    List<Property> findAllDefaultProps();

    @Query(value="select * from property where name=?1 and customer_id=?2",nativeQuery = true)
    Property getByNameAndUserId(String name, Long customerId);
}
