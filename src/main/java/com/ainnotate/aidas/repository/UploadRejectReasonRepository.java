package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.UploadRejectReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
@Repository
public interface UploadRejectReasonRepository extends JpaRepository<UploadRejectReason, Long> {

    UploadRejectReason findByReason(String reason);
}
