package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.AppProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Spring Data JPA repository for the {@link AppProperty} entity.
 */
@Repository
@Transactional
public interface AppPropertyRepository extends JpaRepository<AppProperty, Long> {

    @Query(value = "select * from app_property app where app.user_id=?1 and app.name=?2",nativeQuery = true)
    AppProperty getAppProperty(Long aidasUserId, String key );

    @Query(value = "select * from app_property app where app.organisation_id=?1",nativeQuery = true)
    Set<AppProperty> getAppPropertyOfOrganisation(Long organisationId);

    @Query(value = "select * from app_property app where app.customer_id=?1",nativeQuery = true)
    Set<AppProperty> getAppPropertyOfCustomer(Long customerId);

    @Query(value = "select * from app_property app where app.vendor_id=?1",nativeQuery = true)
    Set<AppProperty> getAppPropertyOfVendor(Long vendorId);

    @Query(value = "select * from app_property app where app.user_id=?1",nativeQuery = true)
    Set<AppProperty> getAppPropertyOfUser(Long userId);

    @Query(value = "select * from app_property app where app.user_id=?1 and app.name like concat('%',?2) ",nativeQuery = true)
    List<AppProperty> getAppPropertyLike(Long aidasUserId, String key );

    @Query(value = "select * from app_property app where app.organisation_id=?1 and app.name=?2",nativeQuery = true)
    AppProperty getAppPropertyOrg(Long aidasOrganisationId, String key );

    @Query(value = "select * from app_property app where app.organisation_id=?1 and app.name like concat('%',?2) ",nativeQuery = true)
    List<AppProperty> getAppPropertyOrgLike(Long aidasOrganisationId, String key );

    @Query(value = "select * from app_property app where app.customer_id=?1 and app.name=?2",nativeQuery = true)
    AppProperty getAppPropertyCustomer(Long aidasCustomerId, String key );
    
    @Query(value = "select * from app_property app where app.user_id=?1 and app.name=?2",nativeQuery = true)
    AppProperty getAppPropertyUser(Long aidasCustomerId, String key );

    @Query(value = "select * from app_property app where app.customer_id=?1 and app.name  like concat('%',?2) ",nativeQuery = true)
    List<AppProperty> getAppPropertyCustomerLike(Long aidasCustomerId, String key );

    @Query(value = "select * from app_property app where app.vendor_id=?1 and app.name=?2",nativeQuery = true)
    AppProperty getAppPropertyVendor(Long aidasVendorId, String key );

    @Query(value = "select * from app_property app where app.vendor_id=?1 and app.name like concat('%',?2) ",nativeQuery = true)
    List<AppProperty> getAppPropertyVendorLike(Long aidasVendorId, String key );

    @Modifying
    @Query(value="insert into app_property (is_sample_data, status,  name,  value, customer_id,created_by,last_modified_by,created_date,last_modified_date)  (select is_sample_data, status,  name, value,?1,?2,?2,now(),now() from app_property where customer_id=-1 )",nativeQuery = true)
    void addCustomerAppProperties(Long customerId,Long userId);

    @Modifying
    @Query(value="insert into app_property (is_sample_data, status,  name,  value, organisation_id,created_by,last_modified_by,created_date,last_modified_date)  (select is_sample_data, status,  name, value,?1,?2,?2,now(),now() from app_property where customer_id=-1 )",nativeQuery = true)
    void addOrganisationAppProperties(Long organisationId,Long userId);

    @Modifying
    @Query(value="insert into app_property (is_sample_data, status,  name,  value, vendor_id,created_by,last_modified_by,created_date,last_modified_date)  (select is_sample_data, status,  name, value,?1,?2,?2,now(),now() from app_property where customer_id=-1 )",nativeQuery = true)
    void addVendorAppProperties(Long vendorId,Long userId);
    
    @Query(value = "select * from app_property app where app.organisation_id=?1 and app.name in (?2) ",nativeQuery = true)
    List<AppProperty> getAppPropertyOrg(Long orgId, List<String> key );
    
    @Query(value = "select * from app_property app where app.organisation_id=?1 and app.name in (?2) ",nativeQuery = true)
    List<AppProperty> getAppPropertyCust(Long custId, List<String> key );
    
    @Modifying
    @Query(value="update app_property set value=aes_encrypt(value,'b693b2f6-350f-11ee-be56-0242ac120002') where name in"
    		+ " ('downloadBucketName','downloadRegion','downloadAccessKey','downloadAccessSecret',"
    		+ "'uploadBucketName','uploadRegion','uploadAccessKey','uploadAccessSecret') and organisation_id=?1",nativeQuery = true)
    void encryptOrg(Long orgId);
    
    @Modifying
    @Query(value="update app_property set value=aes_encrypt(value,'b693b2f6-350f-11ee-be56-0242ac120002') where name in"
    		+ " ('downloadBucketName','downloadRegion','downloadAccessKey','downloadAccessSecret',"
    		+ "'uploadBucketName','uploadRegion','uploadAccessKey','uploadAccessSecret') and customer_id=?1",nativeQuery = true)
    void encryptCustomer(Long custId);
    
    @Modifying
    @Query(value="update app_property set value=aes_encrypt(value,'b693b2f6-350f-11ee-be56-0242ac120002') where name in"
    		+ " ('downloadBucketName','downloadRegion','downloadAccessKey','downloadAccessSecret',"
    		+ "'uploadBucketName','uploadRegion','uploadAccessKey','uploadAccessSecret') and user_id=?1",nativeQuery = true)
    void encryptUser(Long userId);
    
   
}
