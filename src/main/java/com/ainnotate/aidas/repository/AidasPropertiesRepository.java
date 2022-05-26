package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasProperties;
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
public interface AidasPropertiesRepository extends JpaRepository<AidasProperties, Long> {

    Page<AidasProperties> findAllByPropertyTypeEquals(Pageable page, Long propertyType);
    @Query(value="select * from aidas_properties where default_prop=1",nativeQuery = true)
    List<AidasProperties> findAllDefaultProps();
}
