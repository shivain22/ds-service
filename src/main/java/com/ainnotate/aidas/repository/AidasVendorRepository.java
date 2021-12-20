package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasVendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AidasVendor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasVendorRepository extends JpaRepository<AidasVendor, Long> {

    Page<AidasVendor> findAllByIdGreaterThan(Long id, Pageable page);
}
