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
    List<UploadMetadataDTO>  getAllUploadMetaDataForUpload(Long uploadId);
    
    @Query(nativeQuery = true)
    List<UploadMetadataDTO> getAllUploadMetaDataForProjectPropertiesForUpload(Long uploadId);
    
    @Query(nativeQuery = true)
    List<UploadMetadataDTO> getAllUploadMetaDataProjectAndObjectPropertiesForUpload(Long uploadId);

    @Query(nativeQuery = true)
    List<UploadMetadataDTO> getAllUploadMetaDataForObjectPropertiesForUpload(Long uploadId);

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
    
    @Modifying
    @Query(value = "insert into upload_meta_data (upload_id,project_property_id) (select ?1, pp.id from  project_property pp where pp.project_id=?2)",nativeQuery = true)
    void insertUploadMetaDataForProjectProperties(Long uploadId,Long projectId);
    
    @Modifying
    @Query(value = "insert into upload_meta_data (upload_id,object_property_id) (select ?1, op.id from object_property op where op.object_id=?2)",nativeQuery = true)
    void insertUploadMetaDataForObjectProperties(Long uploadId,Long objectId);
    
    @Modifying
    @Query(value = "insert into upload_meta_data (upload_id,project_property_id,value) (select u.id,?1,?3 from upload u, user_vendor_mapping_object_mapping uvmom, object o where u.user_vendor_mapping_object_mapping_id=uvmom.id and uvmom.object_id=o.id and o.project_id=?2)",nativeQuery = true)
    void insertUploadMetaDataForCustomProperties(Long projectPropertyId,Long projectId,String value);
    
    
    @Modifying
    @Query(value = "update upload_meta_data umd, project_property pp, property p set umd.value=?3 where  umd.project_property_id=pp.id and pp.property_id=p.id and p.name=?2 and umd.upload_id=?1   ",nativeQuery = true)
    void updateUploadMetaDataProjectPropertyFromUpload(Long uploadId, String propertyName,String value);
    
    @Modifying
    @Query(value = "update upload_meta_data umd, object_property op, property p set umd.value=?3 where  umd.object_property_id=op.id and op.property_id=p.id and p.name=?2 and umd.upload_id=?1   ",nativeQuery = true)
    void updateUploadMetaDataObjectPropertyFromUpload(Long uploadId, String propertyName,String value);
    
    @Modifying
    @Query(value = "update upload_meta_data set value=?1 where upload_id=?2 and project_property_id=?3",nativeQuery = true)
    void updateUploadMetadataProjectProperty(String value, Long uploadId,Long projectPropertyId);
    
    @Modifying
    @Query(value = "update upload_meta_data set value=?1 where upload_id=?2 and object_property_id=?3",nativeQuery = true)
    void updateUploadMetadataObjectProperty(String value, Long uploadId,Long objectPropertyId);
    
    @Query(nativeQuery = true)
    List<UploadMetadataDTO> getAllUploadMetadataProjectProperties(Long userVendorMappingId, Long objectId);
    
    @Query(nativeQuery = true)
    List<UploadMetadataDTO> findAllByUserAndProjectAllForMetadataUploadWiseForNew(Long userId, Long objectId);
    
    
    @Query(nativeQuery = true)
    List<UploadMetadataDTO> getAllUploadMetadataObjectProperties(Long userVendorMappingId, Long objectId);
    
    
}
