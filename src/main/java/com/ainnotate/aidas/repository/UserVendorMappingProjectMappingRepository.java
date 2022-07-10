package com.ainnotate.aidas.repository;


import com.ainnotate.aidas.domain.UserVendorMappingProjectMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


/**
 * Spring Data SQL repository for the UserVendorMappingProjectMapping entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface UserVendorMappingProjectMappingRepository extends JpaRepository<UserVendorMappingProjectMapping, Long> {


}
