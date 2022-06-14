package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.dto.IUserDTO;
import com.ainnotate.aidas.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the AidasUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByLogin(String login);

    User getAidasUserByLogin(String login);

    @Query(value="select u.id as userId, u.first_name as firstName, u.last_name as lastName, uvm.id as userVendorMappingId, u.login as login from user u, user_authority_mapping uam, user_vendor_mapping uvm where uam.user_id=u.id and uvm.user_id=u.id and uvm.vendor_id=?1 and uam.authority_id=5",nativeQuery = true)
    List<IUserDTO> findAllUsersOfVendor(Long vendorId);

    @Query(value="select a.userId as userId,a.firstName as firstName,a.lastName as lastName,a.userVendorMappingId as userVendorMappingId,a.login as login,count(a.status) as status from (\n" +
        "select \n" +
        "u.id as userId, \n" +
        "u.first_name as firstName, \n" +
        "u.last_name as lastName, \n" +
        "uvm.id as userVendorMappingId, \n" +
        "u.login as login,\n" +
        "1 as status \n" +
        "from \n" +
        "user u, \n" +
        "user_vendor_mapping_object_mapping uvmom,\n" +
        "user_vendor_mapping uvm ,\n" +
        "object o\n" +
        "where \n" +
        "uvmom.user_vendor_mapping_id=uvm.id and\n" +
        "uvm.user_id=u.id \n" +
        "and uvm.vendor_id=?1\n" +
        "and o.project_id=?2\n" +
        "union\n" +
        "\n" +
        "select \n" +
        "u.id as userId, \n" +
        "u.first_name as firstName, \n" +
        "u.last_name as lastName, \n" +
        "uvm.id as userVendorMappingId, \n" +
        "u.login as login ,\n" +
        "0 as status\n" +
        "from \n" +
        "user u, \n" +
        "user_vendor_mapping uvm \n" +
        "where \n" +
        "uvm.user_id=u.id \n" +
        "and uvm.vendor_id=?1) a group by a.userId,a.firstName,a.lastName,a.userVendorMappingId,a.login",nativeQuery = true)
    List<IUserDTO> findAllUsersOfVendorWithProject(Long vendorId, Long projectId);

    Optional<User> findOneByLogin(String login);

    @Query(value = "select * from user u, user_customer_mapping ucm where u.id=ucm.user_id and ucm.customer_id=?1 and u.status=1 and deleted=0"
        ,countQuery = "select count(*) from user u, user_customer_mapping ucm where u.id=ucm.user_id and ucm.customer_id=?1 and u.status=1 and deleted=0",nativeQuery = true)
    Page<User> findAllByDeletedIsFalseAndAidasCustomer(Pageable pageable, Long customerId);

    @Query(value = "select * from user u, user_customer_mapping ucm,customer c where u.id=ucm.user_id and ucm.customer_id=c.id and c.organisation_id=?1 and u.status=1 and deleted=0" +
        " union select * from user u, user_organisation_mapping uom where uom.user_id=u.id and uom.organisation_id=?1"
        ,countQuery = "select count(*) from (select * from user u, user_customer_mapping ucm,customer c where u.id=ucm.user_id and ucm.customer_id=c.id and c.organisation_id=?1 and u.status=1 and deleted=0" +
        " union select * from user u, user_organisation_mapping uom where uom.user_id=u.id and uom.organisation_id=?1 ) as orgusers",nativeQuery = true)
    Page<User> findAllByDeletedIsFalseAndAidasOrganisation_OrAidasCustomer_AidasOrganisation(Pageable pageable, Organisation organisation, Organisation aidasCustomerOrganisation);

    @Query(value = "select * from user u, user_vendor_mapping uvm where u.id=uvm.user_id and uvm.vendor_id=?1 and u.status=1 and deleted=0"
        ,countQuery = "select count(*) from user u, user_vendor_mapping uvm where u.id=uvm.user_id and uvm.vendor_id=?1 and u.status=1 and deleted=0",nativeQuery = true)
    Page<User> findAllByDeletedIsFalseAndAidasVendor(Pageable pageable, Vendor vendor);

    @Query(value = "select * from user u where  u.status=1 and deleted=0"
        ,countQuery = "select count(*) from user u where  u.status=1 and deleted=0",nativeQuery = true)
    Page<User> findAllByIdGreaterThanAndDeletedIsFalse(Long id, Pageable page);

    @Query(value = "select u.* from user u, user_authority_mapping uaa where uaa.user_id=u.id  and uaa.authority_id=?1", nativeQuery = true)
    List<User>findAllByAidasAuthoritiesEquals(Long aidasAuthorityId);


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

}
