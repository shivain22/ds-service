package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AidasProperties entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasPropertiesRepository extends JpaRepository<AidasProperties, Long> {

    Page<AidasProperties> findAllByPropertyTypeEquals(Pageable page, Long propertyType);
}
