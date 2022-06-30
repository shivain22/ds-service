package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.Organisation;
import com.ainnotate.aidas.domain.UploadMetaData;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data SQL repository for the AidasUploadMetaData entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface UploadMetaDataRepository extends JpaRepository<UploadMetaData, Long> {

    @Query(value = "select * from upload_meta_data where is_sample_data=1 order by id asc",nativeQuery = true)
    List<UploadMetaData> getAllSampleUploadMetadata();

    @Modifying
    @Query(value = "delete from upload_meta_data where is_sample_data=1 order by id desc",nativeQuery = true)
    void deleteAllSampleUploadMetadata();

    @Query(value="select * from upload_meta_data where upload_id=?",nativeQuery = true)
    List<UploadMetaData> getAllUploadMetaDataForUpload(Long uploadId);


}
