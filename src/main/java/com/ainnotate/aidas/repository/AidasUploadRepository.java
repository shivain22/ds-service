package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasObject;
import com.ainnotate.aidas.domain.AidasProject;
import com.ainnotate.aidas.domain.AidasUpload;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the AidasUpload entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasUploadRepository extends JpaRepository<AidasUpload, Long> {

    Integer countAidasUploadByAidasUserAidasObjectMapping_AidasObject(AidasObject aidasObject);

    List<AidasUpload> getAidasUploadsByAidasUserAidasObjectMapping_AidasObjectAndStatusIsTrue(AidasObject aidasObject);

    List<AidasUpload> getAidasUploadsByAidasUserAidasObjectMapping_AidasObjectAndStatusIsFalse(AidasObject aidasObject);

    List<AidasUpload> getAidasUploadsByAidasUserAidasObjectMapping_AidasObjectAndStatusIsNull(AidasObject aidasObject);

    List<AidasUpload> getAidasUploadsByAidasUserAidasObjectMapping_AidasObject_AidasProjectAndStatusIsTrue(AidasProject aidasProject);

    List<AidasUpload> getAidasUploadsByAidasUserAidasObjectMapping_AidasObject_AidasProjectAndStatusIsFalse(AidasProject aidasProject);

    List<AidasUpload> getAidasUploadsByAidasUserAidasObjectMapping_AidasObject_AidasProjectAndStatusIsNull(AidasProject aidasProject);

}
