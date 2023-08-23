package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.Organisation;
import com.ainnotate.aidas.domain.Project;
import com.ainnotate.aidas.domain.QOrganisation;
import com.ainnotate.aidas.domain.QProject;
import com.ainnotate.aidas.domain.UserAuthorityMappingUserOrganisationMapping;
import com.ainnotate.aidas.dto.UserOrganisationMappingDTO;
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
import java.util.Set;

/**
 * Spring Data SQL repository for the AidasOrganisation entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface OrganisationRepository extends JpaRepository<Organisation, Long>,QuerydslPredicateExecutor<Organisation>, QuerydslBinderCustomizer<QOrganisation> {

    Page<Organisation> findAllById(Long id, Pageable page);

    @Query(value = "select * from organisation where is_sample_data=1 and name=?1", nativeQuery = true)
    Organisation findSampleOrganisationByName(String name);

    @Query(value = "select * from organisation where id>?1 and status=1",countQuery = "select count(*) from organisation where id>?1 and status=1",nativeQuery = true)
    Page<Organisation> findAllByIdGreaterThan(Long id, Pageable page);

    @Query(value = "select * from organisation where id>?1 and status=1",countQuery = "select count(*) from organisation where id>?1 and status=1",nativeQuery = true)
    List<Organisation> findAllByIdGreaterThanForDropDown(Long id);

    @Query(value = "select o.* from organisation o, customer c where c.organisation_id=o.id and o.id=?1 and o.status=1",nativeQuery = true)
    Page<Organisation> findAllByCustomer(Long id, Pageable page);

    @Query(value = "select * from organisation where id=?1 and status=1",nativeQuery = true)
    List<Organisation> findOrgOfCustomer(Long id);

    @Query(value = "select count(*) from organisation where id > 0 and status=1", nativeQuery = true)
    Long countAllOrgsForSuperAdmin();

    @Query(value = "select * from organisation where is_sample_data=1 order by id asc",nativeQuery = true)
    List<Organisation> getAllSampleOrganisations();

    @Modifying
    @Query(value = "delete from organisation where is_sample_data=1 order by id desc",nativeQuery = true)
    void deleteAllSampleOrganisations();
    
    @Query(value="select o.* from organisation o, \n"
    		+ "user_organisation_mapping uom,\n"
    		+ "user_authority_mapping uam, \n"
    		+ "uam_uom_mapping uum \n"
    		+ "where uum.uam_id=uam.id \n"
    		+ "and uam.authority_id=?2\n"
    		+ "and uum.uom_id=uom.id \n"
    		+ "and uam.user_id=?1 \n"
    		+ "and uom.organisation_id=o.id \n"
    		+ "and uum.status=1 and o.id>-1",nativeQuery = true)
    List<Organisation> getOrganisations(Long userId,Long authorityId);
    
    @Override
    default public void customize(
        QuerydslBindings bindings, QOrganisation root) {
        bindings.bind(String.class)
            .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);

    }
    @Query(nativeQuery = true)
    List<UserOrganisationMappingDTO> getAllOrganisationsWithUamId(Long userId, Long authorityId);
    
    @Query(nativeQuery = true)
    List<UserOrganisationMappingDTO> getAllOrganisationsWithoutUamId(Long userId);
    
    @Query(nativeQuery = true)
    List<UserOrganisationMappingDTO> getAllOrganisationsWithUamIdAndOrgId(Long uamId,Long orgId);
}
