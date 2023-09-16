package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.UsersOfVendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data SQL repository for the AidasVendor entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface UsersOfVendorRepository extends JpaRepository<UsersOfVendor, String> {

    @Query(value="select *  from vendor_user_project_level_status where project_id=?1", nativeQuery = true)
    List<UsersOfVendor> getUserOfVendor(Long projectId);
    
    @Query(value="select *  from vendor_user_project_level_status vupls where vupls.project_id=?1 and "
    		+ "vupls.vendor_id in "
    		+ "(select vendor_id from vendor_organisation_mapping vom where vom.organisation_id=?2 "
    		+ "union "
    		+ "select vendor_id from vendor_organisation_mapping vom where vom.organisation_id=-1)", nativeQuery = true)
    List<UsersOfVendor> getUserOfVendorForOrganisation(Long projectId,Long orgId);
    
    @Query(value="select *  from vendor_user_project_level_status vupls where  vupls.project_id=?1  "
    		+ " and  vendor_id in (select vendor_id from vendor_customer_mapping vcm where vcm.customer_id=?2 "
    		+ " union select vendor_id from vendor_organisation_mapping vom, customer c where vom.organisation_id=c.organisation_id and c.id=?2"
    		+ " union select vendor_id from vendor_customer_mapping vcm where vcm.customer_id=-1"
    		+ " union select vendor_id from vendor_organisation_mapping vom where vom.organisation_id=-1)", nativeQuery = true)
    List<UsersOfVendor> getUserOfVendorCustomer(Long projectId, Long customerId);
    
    @Query(value="select *  from vendor_user_project_level_status where project_id=?1 and vupls.vendor_id=?2", nativeQuery = true)
    List<UsersOfVendor> getUserOfVendorForVendor(Long projectId, Long vendorId);
}
