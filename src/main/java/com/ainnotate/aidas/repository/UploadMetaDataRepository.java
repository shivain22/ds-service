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

    @Query(value="select project_property_id from upload_meta_data where upload_id=? and project_property_id is not null",nativeQuery = true)
    List<Long> getAllProjectPropertyIdsOfUploadMetaDataForUpload(Long uploadId);


    @Query(value="select object_property_id from upload_meta_data where upload_id=? and object_property_id is not null",nativeQuery = true)
    List<Long> getAllObjectPropertyIdsOfUploadMetaDataForUpload(Long uploadId);



    @Query(value="select id," +
        "        created_by," +
        "        created_date," +
        "        last_modified_by," +
        "        last_modified_date," +
        "        rating," +
        "        is_sample_data," +
        "        status," +
        "        name," +
        "        value," +
        "        object_property_id," +
        "        project_property_id," +
        "        upload_id from (select umd.*,p.id as prop_id " +
        "from " +
        "upload_meta_data umd, " +
        "upload u, " +
        "user_vendor_mapping_object_mapping uvmom, " +
        "object o," +
        "project_property pp," +
        "property p" +
        " where " +
        "umd.upload_id=u.id and " +
        "u.user_vendor_mapping_object_mapping_id=uvmom.id and " +
        "uvmom.object_id=o.id and " +
        "umd.project_property_id=pp.id and" +
        " pp.add_to_metadata=1 and" +
        " o.project_id=?1 and " +
        " pp.property_id=p.id" +
        " union " +
        "select umd.*,p.id as prop_id " +
        "from " +
        "upload_meta_data umd, " +
        "upload u, " +
        "user_vendor_mapping_object_mapping uvmom, " +
        "object o," +
        "object_property op," +
        "property p" +
        " where " +
        "umd.upload_id=u.id and " +
        "u.user_vendor_mapping_object_mapping_id=uvmom.id and " +
        "uvmom.object_id=o.id and " +
        " op.add_to_metadata=1 and " +
        " op.property_id=p.id and " +
        "umd.object_property_id=op.id and" +
        " op.property_id not in (select property_id from project_property where project_id=?1) and" +
        " o.project_id=?1) umd order by umd.upload_id, umd.prop_id",nativeQuery = true)
    List<UploadMetaData> getAllUploadMetaDataForProject(Long projectId);


    @Query(value="select * from upload_meta_data umd,project_property pp,property p where umd.upload_id=?1 and umd.project_property_id=pp.id and pp.property_id=p.id and p.name=?2 ",nativeQuery = true)
    UploadMetaData getUploadMetaDataByProjectPropertyName(Long uploadId, String propertyName);

    @Query(value="select * from upload_meta_data umd,object_property op,property p where umd.upload_id=?1 and umd.object_property_id=op.id and op.property_id=p.id and p.name=?2 ",nativeQuery = true)
    UploadMetaData getUploadMetaDataByObjectPropertyName(Long uploadId, String propertyName);

    @Query(value="select * from upload_meta_data umd where umd.upload_id=?1 and umd.project_property_id=?2 ",nativeQuery = true)
    UploadMetaData getUploadMetaDataByProjectPropertyId(Long uploadId, Long projectPropertyId);

    @Query(value="select * from upload_meta_data umd where umd.upload_id=?1 and umd.object_property_id=?2 ",nativeQuery = true)
    UploadMetaData getUploadMetaDataByObjectPropertyId(Long uploadId, Long objectPropertyId);



}
