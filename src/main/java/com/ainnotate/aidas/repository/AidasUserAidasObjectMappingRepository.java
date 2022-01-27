package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AidasUserAidasObjectMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the AidasUserAidasObjectMapping entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AidasUserAidasObjectMappingRepository extends JpaRepository<AidasUserAidasObjectMapping, Long> {

    AidasUserAidasObjectMapping findByAidasUser_IdAndAidasObject_Id(Long aidasUserId, Long aidasObject);

    @Query(value= "select count(*) from (select * from aidas_user_obj_map where aidas_user_id in (select id from aidas_user where aidas_vendor_id=?1) and aidas_object_id=?2) a",nativeQuery = true)
    Integer getCountOfAidasObjectMappingForVendorAdmin(Long vendorId,Long aidasObject);

    @Query(value="select * from aidas_user_obj_map where aidas_object_id>-1", nativeQuery = true)
    Page<AidasUserAidasObjectMapping> findAllMappings(Pageable pageable);
}
