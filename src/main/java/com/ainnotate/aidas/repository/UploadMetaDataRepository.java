package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.Organisation;
import com.ainnotate.aidas.domain.UploadMetaData;
import com.ainnotate.aidas.dto.UploadMetadataDTO;
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
    List<UploadMetaData> getAllUploadMetaDataForUploadForQc(Long uploadId);

    @Query(value="select project_property_id from upload_meta_data where upload_id=? and project_property_id is not null",nativeQuery = true)
    List<Long> getAllProjectPropertyIdsOfUploadMetaDataForUpload(Long uploadId);


    @Query(value="select object_property_id from upload_meta_data where upload_id=? and object_property_id is not null",nativeQuery = true)
    List<Long> getAllObjectPropertyIdsOfUploadMetaDataForUpload(Long uploadId);

    @Query(nativeQuery = true)
    List<UploadMetadataDTO> getAllUploadMetaDataForProject(Long projectId);

    @Query(nativeQuery = true)
    List<UploadMetadataDTO> getAllUploadMetaDataForUpload(Long uploadId);

    @Query(nativeQuery = true)
    List<UploadMetadataDTO> getAllUploadMetaDataForObject(Long projectId);

    @Query(nativeQuery = true)
    List<UploadMetadataDTO> getAllUploadMetaDataForProjectWithStatus(Long projectId,Integer status);

    @Query(value="select * from upload_meta_data umd,project_property pp,property p where umd.upload_id=?1 and umd.project_property_id=pp.id and pp.property_id=p.id and p.name=?2 ",nativeQuery = true)
    UploadMetaData getUploadMetaDataByProjectPropertyName(Long uploadId, String propertyName);

    @Query(value="select * from upload_meta_data umd,object_property op,property p where umd.upload_id=?1 and umd.object_property_id=op.id and op.property_id=p.id and p.name=?2 ",nativeQuery = true)
    UploadMetaData getUploadMetaDataByObjectPropertyName(Long uploadId, String propertyName);

    @Query(value="select * from upload_meta_data umd where umd.upload_id=?1 and umd.project_property_id=?2 ",nativeQuery = true)
    UploadMetaData getUploadMetaDataByProjectPropertyId(Long uploadId, Long projectPropertyId);

    @Query(value="select * from upload_meta_data umd where umd.upload_id=?1 and umd.object_property_id=?2 ",nativeQuery = true)
    UploadMetaData getUploadMetaDataByObjectPropertyId(Long uploadId, Long objectPropertyId);

    @Query(value="select count(*) from upload_meta_data umd,project_property pp where umd.upload_id=?1 and umd.project_property_id=pp.property_id and (umd.value is null or length(trim(umd.value))=0) and pp.optional=0 and pp.add_to_metadata=0 ",nativeQuery = true)
    Integer getUploadMetadataCountMandatoryProjectPropertyNotFilled(Long uploadId);

    @Query(value="select count(*) from upload_meta_data umd,project_property pp where umd.upload_id=?1 and umd.project_property_id=pp.property_id and (umd.value is null or length(trim(umd.value))=0) and pp.optional=0 and pp.add_to_metadata=0 ",nativeQuery = true)
    Integer getUploadMetadataCountMandatoryObjectPropertyNotFilled(Long uploadId);
}
