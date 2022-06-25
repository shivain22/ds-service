package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.Organisation;
import com.ainnotate.aidas.domain.Project;
import com.ainnotate.aidas.domain.ProjectProperty;
import com.ainnotate.aidas.domain.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data SQL repository for the AidasProperties entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface PropertyRepository extends JpaRepository<Property, Long> {

    Page<Property> findAllByPropertyTypeEquals(Pageable page, Long propertyType);

    @Query(value="select * from property where default_prop=1",nativeQuery = true)
    List<Property> findAllDefaultProps();


    @Query(value="select * from property where name=?1 and customer_id=?2",nativeQuery = true)
    Property getByNameAndUserId(String name, Long customerId);

    @Query(value="select * from property where user_id=-1",nativeQuery = true)
    List<Property>findAllStandardProperties();

    @Modifying
    @Query(value="insert into property (is_sample_data, status, add_to_metadata, default_prop, description, name, optional, property_type, system_property, value, customer_id)  (select is_sample_data, status, add_to_metadata, default_prop, description, name, optional, property_type, system_property, value,?1 from property where user_id=-1 )",nativeQuery = true)
    void addNewProperty(Long customerId);

    @Query(value = "select * from property where is_sample_data=1",nativeQuery = true)
    List<Property> getAllSampleProperties();

    @Query(value = "select * from property where is_sample_data=1 and customer_id=?1",nativeQuery = true)
    List<Property> getAllSampleProperties(Long customerId);

    @Modifying
    @Query(value = "delete from property where is_sample_data=1 order by id desc",nativeQuery = true)
    void deleteAllSampleProperties();
}
