package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.Organisation;
import com.ainnotate.aidas.domain.Project;
import com.ainnotate.aidas.domain.QProject;
import com.ainnotate.aidas.domain.QVendor;
import com.ainnotate.aidas.domain.Vendor;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data SQL repository for the AidasVendor entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface VendorRepository extends JpaRepository<Vendor, Long>,QuerydslPredicateExecutor<Vendor>, QuerydslBinderCustomizer<QVendor> {

    @Query(value = "select * from vendor where name=?1 and is_sample_data=1",nativeQuery = true)
    Vendor findSampleVendorByName(String name);

    @Query(value = "select * from vendor where id>0 and status=1",countQuery = "select count(*) from vendor where id>0 and status=1", nativeQuery = true)
    Page<Vendor> findAllByIdGreaterThan(Long id, Pageable page);

    @Query(value = "select * from vendor where id>0 and status=1",countQuery = "select count(*) from vendor where id>0 and status=1", nativeQuery = true)
    List<Vendor> findAllByIdGreaterThanForDropDown(Long id);

    @Query(value = "select * from vendor v where v.status=1 and id>0",nativeQuery = true)
    List<Vendor> getAllVendors();

    @Query(value = "select count(*) from vendor where id>0 and status=1", nativeQuery = true)
    Long countAllVendorsForSuperAdmin();

    @Query(value = "select count(*) from vendor where id>0 and status=1", nativeQuery = true)
    Long countAllVendorsForOrgAdmin();

    @Query(value = "select count(*) from vendor where id>0 and status=1", nativeQuery = true)
    Long countAllVendorsForCustomerAdmin();

    @Query(value = "select * from vendor where is_sample_data=1",nativeQuery = true)
    List<Vendor> getAllSampleVendors();

    @Modifying
    @Query(value = "delete from vendor where is_sample_data=1 order by id desc",nativeQuery = true)
    void deleteAllSampleVendors();
    
    @Override
    default public void customize(
        QuerydslBindings bindings, QVendor root) {
        bindings.bind(String.class)
            .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);

    }

}
