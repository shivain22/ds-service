package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasAuthority;
import com.ainnotate.aidas.domain.AidasUploadRejectReason;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the {@link AidasAuthority} entity.
 */
public interface AidasUploadRejectReasonRepository extends JpaRepository<AidasUploadRejectReason, Long> {

    AidasUploadRejectReason findByReason(String reason);
}
