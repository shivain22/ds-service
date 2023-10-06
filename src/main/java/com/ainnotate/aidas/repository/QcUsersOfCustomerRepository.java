package com.ainnotate.aidas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ainnotate.aidas.domain.QcUser;

/**
 * Spring Data SQL repository for the AidasVendor entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface QcUsersOfCustomerRepository extends JpaRepository<QcUser, String> {

    @Query(value="select * from qc_user quoc, project p where quoc.project_id=?1 and quoc.project_id=p.id and p.customer_id=?2 and quoc.user_status=1", nativeQuery = true)
    List<QcUser> getQcUserOfAdmin(Long projectId,Long customerId);

    @Query(value="select quoc.* from qc_user quoc,user_organisation_mapping uom where quoc.project_id=?1 and quoc.uom_id=uom.id  and uom.organisation_id=?2 and quoc.user_status=1 and uom.status=1 "
    		+ " union "
    		+ "select quoc.* from qc_user quoc,user_customer_mapping ucm,customer c where quoc.project_id=?1 and quoc.ucm_id=ucm.id and ucm.customer_id=c.id and c.organisation_id=?2  and quoc.user_status=1 and ucm.status=1"
    		+ " union "
    		+ "select quoc.* from qc_user quoc,user_vendor_mapping uvm, vendor_organisation_mapping vom where quoc.project_id=?1 and vom.organisation_id=?2 and quoc.uvm_id=uvm.id and uvm.vendor_id=vom.vendor_id  and quoc.user_status=1 and uvm.status=1", nativeQuery = true)
    List<QcUser> getQcUserOfOrg(Long projectId,Long organisationId);

    @Query(value="select quoc.* from qc_user quoc where quoc.project_id=?1 and quoc.user_status=1 union "
    		+ "select * from qc_user quoc where quoc.project_id=?1 and quoc.entity_id=2 and quoc.uom_id=-2 and quoc.uvm_id=-2 and quoc.user_status=1\n"
    		+ "and ucm_id in (select ucm.id from user_customer_mapping ucm, uam_ucm_mapping uum,user_authority_mapping uam where\n"
    		+ "uum.ucm_id=ucm.id and uum.uam_id=uam.id and uam.authority_id=8 and ucm.customer_id=?2 and ucm.status=1 and uam.status=1 and uum.status=1 and uam.status=1)", nativeQuery = true)
    List<QcUser> getQcUserOfCustomer(Long projectId,Long customerId);

    @Query(value="select * from qc_user quoc, user_vendor_mapping uvm where quoc.project_id=?1 and quoc.uvm_id=uvm.id and uvm.vendor_id=?2 and quoc.user_status=1 and uvm.status=1", nativeQuery = true)
    List<QcUser> getQcUserOfVendor(Long projectId,Long vendorId);
}
