package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.dto.IUserDTO;
import com.ainnotate.aidas.dto.UserDTO;
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
import java.util.Optional;

/**
 * Spring Data SQL repository for the AidasUser entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long>,QuerydslPredicateExecutor<User>, QuerydslBinderCustomizer<QUser> {



    Optional<User> findByLogin(String login);


    @Query(value="select * from user u where u.login=?1 for update",nativeQuery = true)
    User getUserByLogin(String login);

    @Query(value="select * from user u where u.email=?1",nativeQuery = true)
    User getUserByEmail(String email);

    @Query(value = "select * from user u where id=?1",nativeQuery = true)
    User getById(Long id);

    @Query(value = "select * from user u where email=?1",nativeQuery = true)
    User getByEmail(String email );

    @Query(value = "select * from user u where login=?1",nativeQuery = true)
    User getByLogin(String login );

    @Query(value="select u.id as userId, u.first_name as firstName, u.last_name as lastName, uvm.id as userVendorMappingId, u.login as login from user u, user_authority_mapping uam, user_vendor_mapping uvm where uam.user_id=u.id and uvm.user_id=u.id and uvm.vendor_id=?1 and uam.authority_id=5",nativeQuery = true)
    List<IUserDTO> findAllUsersOfVendor(Long vendorId);

    @Query(value="select \n" +
        "u.id as userId, \n" +
        "u.first_name as firstName, \n" +
        "u.last_name as lastName, \n" +
        "uvm.id as userVendorMappingId, \n" +
        "uvmpm.id as userVendorMappingProjectMappingId,\n" +
        "u.login as login \n" +
        "from \n" +
        "user u, user_authority_mapping uam, \n" +
        "user_vendor_mapping uvm,\n" +
        "user_vendor_mapping_project_mapping uvmpm\n" +
        "where \n" +
        "uam.user_id=u.id and \n" +
        "uvm.user_id=u.id and\n" +
        "uvmpm.user_vendor_mapping_id=uvm.id and\n" +
        "uvmpm.project_id=?1  and \n" +
        "uvm.vendor_id=?2 and \n" +
        "uam.authority_id=5",nativeQuery = true)
    List<IUserDTO> findAllVendorUserMappingObjectMappingOfVendor(Long projectId,Long vendorId);

    @Query(nativeQuery = true)
    List<UserDTO> findAllUsersOfVendorWithProject(Long projectId);

    @Query(nativeQuery = true)
    List<UserDTO> findAllUsersOfVendorWithObject(Long objectId);

    Optional<User> findOneByLogin(String login);

    @Query(value = "select * from user u, user_customer_mapping ucm where u.id=ucm.user_id and ucm.customer_id=?1 and u.status=1 and deleted=0"
        ,countQuery = "select count(*) from user u, user_customer_mapping ucm where u.id=ucm.user_id and ucm.customer_id=?1 and u.status=1 and deleted=0",nativeQuery = true)
    Page<User> findAllByDeletedIsFalseAndAidasCustomer(Pageable pageable, Long customerId);

    @Query(value = "select * from user u, user_customer_mapping ucm where u.id=ucm.user_id and ucm.customer_id=?1 and u.status=1 and deleted=0"
        ,countQuery = "select count(*) from user u, user_customer_mapping ucm where u.id=ucm.user_id and ucm.customer_id=?1 and u.status=1 and deleted=0",nativeQuery = true)
    List<User> findAllByDeletedIsFalseAndAidasCustomer(Long customerId);

    @Query(value = "select u.* from user u where u.parent_organisation_id=?1 and u.status=1 and deleted=0" +
        " union "
        + "select u.* from user u, user_organisation_mapping uom where uom.user_id=u.id and uom.organisation_id=?1"
        ,countQuery = "select count(*) from (select u.id from user u where u.parent_organisation_id.id=?1 and u.status=1 and deleted=0)" +
        " ) as orgusers",nativeQuery = true)
    Page<User> findAllByDeletedIsFalseAndAidasOrganisation_OrAidasCustomer_AidasOrganisation(Pageable pageable, Organisation organisation, Organisation aidasCustomerOrganisation);

    
    @Query(value = "select a.* from (select u.* from user u where u.parent_organisation_id=?1 and u.status=1 and deleted=0 \n"
    		+ "union \n"
    		+ "select u.* from user u, user_organisation_mapping uom where uom.user_id=u.id and uom.organisation_id=?1 and u.status=1 and deleted=0 \n"
    		+ "union \n"
    		+ "select u.* from user u, user_customer_mapping ucm,customer c where ucm.user_id=u.id and ucm.customer_id=c.id and c.organisation_id=?1 and u.status=1 and deleted=0 \n"
    		+ "union \n"
    		+ "select u.* from user u, user_vendor_mapping uvm,vendor_organisation_mapping vom, vendor v where uvm.user_id = u.id and uvm.vendor_id=v.id and vom.vendor_id=v.id and vom.organisation_id=?1 and u.status=1 and deleted=0 \n"
    		+ "union \n"
    		+ "select u.* from user u, user_vendor_mapping uvm,vendor_customer_mapping vcm,customer c, vendor v where uvm.user_id = u.id and uvm.vendor_id=v.id and vcm.vendor_id=v.id and vcm.customer_id=c.id and c.organisation_id=?1 and u.status=1 and deleted=0)a order by a.id desc \n"
            ,
            countQuery = "select sum(count) from (\n"
            		+ "select count(*) as count from user u where u.parent_organisation_id=?1 and u.status=1 and deleted=0 \n"
            		+ "union \n"
            		+ "select count(*) as count from user u, user_organisation_mapping uom where uom.user_id=u.id and uom.organisation_id=?1 and u.status=1 and deleted=0 \n"
            		+ "union \n"
            		+ "select count(*) as count from user u, user_customer_mapping ucm,customer c where ucm.user_id=u.id and ucm.customer_id=c.id and c.organisation_id=?1 and u.status=1 and deleted=0 \n"
            		+ "union \n"
            		+ "select count(*) as count from user u, user_vendor_mapping uvm,vendor_organisation_mapping vom, vendor v where uvm.user_id = u.id and uvm.vendor_id=v.id and vom.vendor_id=v.id and vom.organisation_id=?1 and u.status=1 and deleted=0 \n"
            		+ "union \n"
            		+ "select count(*) as count from user u, user_vendor_mapping uvm,vendor_customer_mapping vcm,customer c, vendor v where uvm.user_id = u.id and uvm.vendor_id=v.id and vcm.vendor_id=v.id and vcm.customer_id=c.id and c.organisation_id=?1 and u.status=1 and deleted=0 \n"
            		+ ")a",nativeQuery = true)
        Page<User> findAllByParentOrganisationId(Pageable pageable, Long parentOrganisationId);
    


    @Query(value = "select * from user u, user_customer_mapping ucm,customer c where u.id=ucm.user_id and ucm.customer_id=c.id and c.organisation_id=?1 and u.status=1 and deleted=0" +
        " union select * from user u, user_organisation_mapping uom where uom.user_id=u.id and uom.organisation_id=?1"
        ,countQuery = "select count(*) from (select * from user u, user_customer_mapping ucm,customer c where u.id=ucm.user_id and ucm.customer_id=c.id and c.organisation_id=?1 and u.status=1 and deleted=0" +
        " union select * from user u, user_organisation_mapping uom where uom.user_id=u.id and uom.organisation_id=?1 ) as orgusers",nativeQuery = true)
    List<User> findAllByDeletedIsFalseAndAidasOrganisation_OrAidasCustomer_AidasOrganisation(Organisation organisation, Organisation aidasCustomerOrganisation);


    @Query(value = "select * from user u, user_vendor_mapping uvm where u.id=uvm.user_id and uvm.vendor_id=?1 and u.status=1 and deleted=0"
        ,countQuery = "select count(*) from user u, user_vendor_mapping uvm where u.id=uvm.user_id and uvm.vendor_id=?1 and u.status=1 and deleted=0",nativeQuery = true)
    Page<User> findAllByDeletedIsFalseAndAidasVendor(Pageable pageable, Vendor vendor);

    @Query(value = "select * from user u, user_vendor_mapping uvm where u.id=uvm.user_id and uvm.vendor_id=?1 and u.status=1 and deleted=0"
        ,countQuery = "select count(*) from user u, user_vendor_mapping uvm where u.id=uvm.user_id and uvm.vendor_id=?1 and u.status=1 and deleted=0",nativeQuery = true)
    List<User> findAllByDeletedIsFalseAndAidasVendor( Vendor vendor);

    @Query(value = "select * from user u where  u.status=1 and deleted=0 and u.id>0 order by u.id desc"
        ,countQuery = "select count(*) from user u where  u.status=1 and deleted=0 and u.id>0",nativeQuery = true)
    Page<User> findAllByIdGreaterThanAndDeletedIsFalse(Pageable page);

    @Query(value = "select * from user u where 1=2",nativeQuery = true)
        Page<User> findNone(Pageable page);

    @Query(value = "select * from user u where  u.status=1 and deleted=0 order by u.id desc"
        ,countQuery = "select count(*) from user u where  u.status=1 and deleted=0",nativeQuery = true)
    List<User> findAllByIdGreaterThanAndDeletedIsFalse(Long id);

    @Query(value = "select u.* from user u, user_authority_mapping uaa where uaa.user_id=u.id  and uaa.authority_id=?1 order by u.id desc", nativeQuery = true)
    List<User>findAllByAidasAuthoritiesEquals(Long aidasAuthorityId);



    @Query(value = "select \n" +
        "u.id as userId, \n" +
        "u.first_name as firstName, \n" +
        "u.last_name lastName, \n" +
        "0 as userVendorMappingId,\n" +
        "qpm.id as userMappingMappingId,\n" +
        "ucm.id as qcProjectMappingId, \n" + //changed this to get qpc.id instead of customer mapping id so that the add function can directly work on qpc
        "qpm.status as status,   \n" +
        "qpm.qc_level as qcLevel "+
        "from \n" +
        "qc_project_mapping qpm, \n" +
        "user u,\n" +
        "user_customer_mapping ucm \n" +
        "where \n" +
        "qpm.user_customer_mapping_id=ucm.id and\n" +
        "ucm.user_id=u.id and\n" +
        "qpm.project_id=?1 \n"
        , nativeQuery = true)
    List<IUserDTO>findAllByQcUsersByCustomerAndProject(Long projectId);





    @Query(value = "select * from user u, user_authority_mapping uam where uam.user_id=u.id and u.id not in (select a.uid from (select user_id as uid, count(uvm.vendor_id) as cntvid,uvm.vendor_id as vid from user_vendor_mapping uvm group by uvm.vendor_id) a where a.vid=-1 and cntvid=1 ) ", nativeQuery = true)
    List<User>findAllFreelanceUsers();



    @Query(value ="select count(*) from user u, user_organisation_mapping uom where u.id=uom.user_id and uom.organisation_id=?1", nativeQuery = true)
    Long countAllByOrganisation(Long organisationId);
    @Query(value ="select count(*) from user u, user_customer_mapping ucm where u.id=ucm.user_id and ucm.customer_id=?1", nativeQuery = true)
    Long countAllByCustomer(Long customerId);
    @Query(value ="select count(*) from user u, user_vendor_mapping uvm where u.id=uvm.user_id and uvm.vendor_id=?1", nativeQuery = true)
    Long countAllByVendor(Long vendorId);

    @Query(value="select count(*) from user u where u.id in (select ucm.user_id from user_customer_mapping  ucm, customer c where ucm.customer_id=c.id and c.organisation_id=?1 )",nativeQuery = true)
    Long countAllByCustomer_Organisation(Long organisationId);

    @Query(value="select count(*) from user au, user_authority_mapping auaa where au.id=auaa.user_id and auaa.authority_id=5", nativeQuery = true)
    Long countAllVendorUsers();

    @Query(value = "select au.* " +
        "from " +
        "user au , " +
        "user_vendor_mapping auavm ," +
        "user_authority_mapping auaam " +
        "where " +
        "auavm.user_id=au.id and auavm.vendor_id=?1 " +
        "and auaam.user_id=au.id and auaam.authority_id=5",nativeQuery = true)
    List<User> findAllByAidasVendor_Id(Long aidasVendorId);

    @Query(value = "select * from user au where au.id in (select uvm.user_id from user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm,user_authority_mapping auaam  where uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id=auaam.user_id and auaam.authority_id=5 and uvmom.object_id=?1) ",nativeQuery = true)
    List<User> getUsersByAssignedToObject(Long objectId);

    @Query(value = "select * from user au where au.id not in (select uvm.user_id from user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm,user_authority_mapping auaam  where uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id=auaam.user_id and auaam.authority_id=5 and uvmom.object_id=?1) ",nativeQuery = true)
    List<User> getUsersByNotAssignedToObject(Long objectId);

    @Query(value = "select * from user au where au.id in (select uvm.user_id from user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm,user_authority_mapping auaam  where uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id=auaam.user_id and auaam.authority_id=5 and uvmom.object_id=?1) ",nativeQuery = true)
    List<User> getVendorsByAssignedToObject(Long objectId);

    @Query(value = "select * from user au where au.id not in (select uvm.user_id from user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm,user_authority_mapping auaam  where uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id=auaam.user_id and auaam.authority_id=5 and uvmom.object_id=?1) ",nativeQuery = true)
    List<User> getVendorsByNotAssignedToObject(Long objectId);

    @Query(value = "select count(*) from user where id>0 and status=1", nativeQuery = true)
    Long countAllForSuperAdmin();

    @Query(value = "select * from user where is_sample_data=1",nativeQuery = true)
    List<User> getAllSampleUsers();

    @Modifying
    @Query(value = "delete from user where is_sample_data=1",nativeQuery = true)
    void deleteAllSampleUsers();


    @Override
    default public void customize(
        QuerydslBindings bindings, QUser root) {
        bindings.bind(String.class)
            .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);

    }
}
