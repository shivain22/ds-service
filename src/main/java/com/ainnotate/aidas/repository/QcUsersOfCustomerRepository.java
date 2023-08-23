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

    @Query(value="select * from qc_user quoc, project p where quoc.project_id=?1 and quoc.project_id=p.id and p.customer_id=?2", nativeQuery = true)
    List<QcUser> getQcUserOfAdmin(Long projectId,Long customerId);

    @Query(value="select * from qc_user quoc,user_organisation_mapping uom where quoc.project_id=?1 and quoc.uom_id=uom.id  and uom.organisation_id=?2 ", nativeQuery = true)
    List<QcUser> getQcUserOfOrg(Long projectId,Long organisationId);

    @Query(value="select * from qc_user quoc, user_customer_mapping ucm where quoc.project_id=?1 and quoc.ucm_id=ucm.id and ucm.customer_id=?2 ", nativeQuery = true)
    List<QcUser> getQcUserOfCustomer(Long projectId,Long customerId);

    @Query(value="select * from qc_user quoc, user_vendor_mapping uvm where quoc.project_id=?1 and quoc.uvm_id=uvm.id and uvm.vendor_id=?2", nativeQuery = true)
    List<QcUser> getQcUserOfVendor(Long projectId,Long vendorId);
}
