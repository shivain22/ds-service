package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.ObjectProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data SQL repository for the AidasObjectProperty entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface ObjectPropertyRepository extends JpaRepository<ObjectProperty, Long> {

    @Query(value = "select * from object_property aop where aop.object_id>?1",nativeQuery = true)
    Page<ObjectProperty> findAllByAidasObjectIdGreaterThan(Pageable page, Long objectId);

    @Query(value = "select * from object_property op  where op.object_id=?1 and op.property_id=?2",nativeQuery = true)
    ObjectProperty findByAidasObject_IdAndAidasProperty_Id(Long aidasObjectId, Long aidasPropertiesId);

    //property_type =2 which is equal to property_type metadata
    @Query(value="select * from object_property aop,property ap where aop.object_id=?1 and aop.property_id=ap.id and ap.property_type=2",nativeQuery = true)
    List<ObjectProperty> findAllAidasObjectPropertyForMetadata(Long objectId);

    @Query(value = "select op.* from object_property op where op.object_id=?1 and op.object_property_type=2 and op.show_to_vendor_user=1 ",nativeQuery = true)
    List<ObjectProperty> findAllMetaDataToBeFilledByVendorUser(Long objectId);

    @Query(value = "select count(*) from object_property aop where aop.object_id=?1 and aop.property_id not in (select property_id from project_property where project_id=?2) and aop.optional=1",nativeQuery = true)
    Long findAllUncommonMandatoryAidasObjectPropertyForMetadata(Long aidasObjectId, Long aidasProjectId);

    @Query(value = "select count(*) from object_property op where op.object_id=?1 and op.optional=?2",nativeQuery = true)
    Long countObjectProperties(Long objectId, Integer optional);

    @Modifying
    @Query(value = "delete from object_property where is_sample_data=1 order by id desc",nativeQuery = true)
    void deleteAllSampleObjectProperty();

    @Query(value = "select * from object_property where object_id=?1",nativeQuery = true)
    List<ObjectProperty> getAllObjectPropertyForObject(Long objectId);

    @Query(value = "select count(*) from object_property op where op.object_id=?1 and op.optional=0",nativeQuery = true)
    Integer getMandatoryPropertyCountForObject(Long objectId);
}
