package com.ainnotate.aidas.repository;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.dto.IObjectDTO;
import com.ainnotate.aidas.dto.IUploadDetail;
import com.ainnotate.aidas.dto.ObjectDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data SQL repository for the AidasObject entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface ObjectRepository extends JpaRepository<Object, Long> {

    @Query(value = "select * from object o, project p, customer c where o.project_id=p.id and p.customer_id=c.id and c.organisation_id=?1 and o.status=1 and o.is_dummy=0"
        ,countQuery = "select count(*) from object o, project p, customer c where o.project_id=p.id and p.customer_id=c.id and c.organisation_id=? and o.status=1 and o.is_dummy=0",nativeQuery = true)
    Page<Object> findAllByAidasProject_AidasCustomer_AidasOrganisation (Pageable pageable, Long organisationId);

    @Query(value = "select * from object o, project p, customer c where o.project_id=p.id and p.customer_id=c.id and c.organisation_id=?1 and p.id=?2 and o.status=1 and o.is_dummy=0",
        countQuery = "select count(*) from object o, project p, customer c where o.project_id=p.id and p.customer_id=c.id and c.organisation_id=?1 and p.id=?2 and o.status=1 and o.is_dummy=0",nativeQuery = true)
    Page<Object> findAllByAidasProject_AidasCustomer_AidasOrganisationAndAidasProject_Id (Pageable pageable, Long organisationId, Long projectId);


    @Query(value = "select * from object o, project p, customer c where o.project_id=p.id and p.customer_id=?1 and p.id=?2 and o.status=1 and o.is_dummy=0",
        countQuery = "select count(*) from object o, project p, customer c where o.project_id=p.id and p.customer_id=?1 and p.id=?2 and o.status=1 and o.is_dummy=0",nativeQuery = true)
    Page<Object> findAllByAidasProject_AidasCustomer(Pageable pageable, Long customerId);

    @Query(value = "select * from object o, project p, customer c where o.project_id=p.id and p.customer_id=?1 and p.id=?2 and o.status=1 and o.is_dummy=0",
        countQuery = "select count(*) from object o, project p, customer c where o.project_id=p.id and p.customer_id=?1 and p.id=?2 and o.status=1 and o.is_dummy=0",nativeQuery = true)
    List<Object> getAllByAidasProject_AidasCustomerAndAidasProject_Id(Long customerId, Long projectId);

    @Query(value="select * from object where status=1 and id>0 and is_dummy=0",nativeQuery = true)
    Page<Object> findAllByIdGreaterThan(Long id, Pageable page);

    @Query(value="select * from object where status=1 and id>0 and is_dummy=0 and object.project_id=?1",
        countQuery = "select count(*) from object where status=1 and id>0 and is_dummy=0 and object.project_id=?1",nativeQuery = true)
    Page<Object> findAllByIdGreaterThanAndAidasProject_Id(Long id, Long projectId, Pageable page);


    @Query(value="select o.* from object o where status=1 and is_dummy=0 and project_id=?1",nativeQuery = true)
    List<Object> getAllObjectsOfProject(Long projectId);

    @Query(nativeQuery = true)
    List<ObjectDTO> getAllObjectDTOsOfProject(Long projectId);

    @Query(value="select ao.* from project ap, object ao,  user_vendor_mapping_object_mapping auavmaom,user_vendor_mapping auavm ,user au where  auavmaom.object_id=ao.id and ao.project_id=ap.id and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=au.id and au.id= ?1 and ao.status=1 and ao.is_dummy=0",nativeQuery = true)
    Page<Object> findAllObjectsByVendorUser(Pageable page, User user);

    @Query(value="select ao.* from project ap, object ao,  user_vendor_mapping_object_mapping auavmaom,user_vendor_mapping auavm ,user au where  auavmaom.object_id=ao.id and ao.project_id=ap.id and auavmaom.user_vendor_mapping_id=auavm.id and auavm.vendor_id= ?1 and ao.status=0 and ao.is_dummy=0",nativeQuery = true)
    Page<Object> findAllObjectsByVendorAdmin(Pageable page, Vendor vendor);

    @Query(value="select ao.* ,count(au.id) total_uploaded, SUM(CASE WHEN au.status = 1 THEN 1 ELSE 0 END) AS total_approved, SUM(CASE WHEN au.status = 0 THEN 1 ELSE 0 END) AS total_rejected, SUM(CASE WHEN au.status = 2 THEN 1 ELSE 0 END) AS total_pending from object ao left outer join  user_vendor_mapping_object_mapping auavmaom on auavmaom.object_id=ao.id left outer join upload au on au.user_vendor_mapping_object_mapping_id=auavmaom.id left outer join user_vendor_mapping auavm on auavmaom.user_vendor_mapping_id = auavm.id left outer join user au1 on auavm.user_id=au1.id where  au1.id= ?1 and ao.project_id=?2 and ao.status=1 and ao.is_dummy=0 group by ao.id",nativeQuery = true)
    Page<Object> findAllObjectsByVendorUserProject(Pageable pageable, User user, Long aidasProjectId);

    @Query(value="select ao.* from project ap, object ao,  user_vendor_mapping_object_mapping auavmaom,user au,user_vendor_mapping auavm  where  auavmaom.object_id=ao.id and ao.project_id=ap.id and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=au.id and auavm.vendor_id= ?1 and ap.id=?2 and ao.id>-1",nativeQuery = true)
    Page<Object> findAllObjectsByVendorAdminProject(Pageable pageable, Vendor vendor, Long aidasProjectId);

    @Query(nativeQuery = true)
    List<ObjectDTO> getAllObjectsByVendorUserProject(Pageable pageable,Long userId);

    @Query( value = "select \n" +
        "o.*" +
        "from user_vendor_mapping_object_mapping uvmom  \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
        "where uvm.user_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2 and o.object_acquired_by_uvmom_id is null",
        countQuery = "select count(o.id) as count  \n" +
            "from user_vendor_mapping_object_mapping uvmom  \n" +
            "left join object o on o.id=uvmom.object_id   \n" +
            "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
            "where uvm.user_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2 order by o.id desc",
        nativeQuery = true)
    Page<Object> getAllObjectsByVendorUserProjectWithProjectId(Pageable pageable, Long userId, Long projectId);


    @Query( value = "select * from ((select \n" +
        "o.* " +
        "from user_vendor_mapping_object_mapping uvmom  \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
        "where uvm.user_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2 and o.object_acquired_by_uvmom_id is null limit ?4) " +
        "union (select \n" +
        "o.* " +
        "from user_vendor_mapping_object_mapping uvmom  \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
        "where uvm.user_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2 and o.object_acquired_by_uvmom_id in (?3) order by o.id desc, o.total_pending asc))a "
        ,
        countQuery = "select count(*) from ((select o.id as count  \n" +
            "from user_vendor_mapping_object_mapping uvmom  \n" +
            "left join object o on o.id=uvmom.object_id   \n" +
            "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
            "where uvm.user_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2) union "+
            " (select o.id as count  \n" +
            "from user_vendor_mapping_object_mapping uvmom  \n" +
            "left join object o on o.id=uvmom.object_id   \n" +
            "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
            "where uvm.user_id=?1 and o.status=1 and o.is_dummy=0 and o.project_id=?2 and o.object_acquired_by_uvmom_id in (?3) )) a",
        nativeQuery = true)
    Page<Object> getAllObjectsByVendorUserProjectWithProjectIdWithAlreadyCompleted(Pageable pageable, Long userId, Long projectId,List<Long> uvmomIds,Integer newObjectLimit);

    @Query(value = "select \n" +
        "o.* " +
        "from user_vendor_mapping_object_mapping uvmom  \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
        "where uvm.user_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2 and o.object_acquired_by_uvmom_id in (?3) order by o.id desc, o.total_pending asc ",
        countQuery = "select count(o.id) as count  \n" +
            "from user_vendor_mapping_object_mapping uvmom  \n" +
            "left join object o on o.id=uvmom.object_id   \n" +
            "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
            "where uvm.user_id=?1 and o.status=1 and o.is_dummy=0 and o.project_id=?2 and o.object_acquired_by_uvmom_id in (?3) ",
        nativeQuery = true)
    Page<Object> getAllObjectsByVendorUserProjectWithProjectIdAndObjectAlreadyAssigned(Pageable pageable,Long userId,Long projectId,List<Long> uvmomIds);


    @Query(nativeQuery = true)
    List<ObjectDTO> getAllObjectsByVendorUserProjectForDropdown(Long userId,Long projectId);

    @Query(value="select \n" +
        "o.*, \n" +
        "count(u.id) as totalUploaded, \n" +
        "sum(CASE WHEN u.approval_status = 1 THEN 1 ELSE 0 END) AS totalApproved,  \n" +
        "sum(CASE WHEN u.approval_status = 0 THEN 1 ELSE 0 END) AS totalRejected,   \n" +
        "sum(CASE WHEN u.approval_status = 2 THEN 1 ELSE 0 END) AS totalPending \n" +
        "from upload u    \n" +
        "left join user_vendor_mapping_object_mapping uvmom on u.user_vendor_mapping_object_mapping_id=uvmom.id   \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
        "where    uvm.vendor_id=?1  \n" +
        "group by o.id,u.user_vendor_mapping_object_mapping_id",nativeQuery = true)
    List<Object> getAllObjectsByVendorAdminProject(Long vendorId);

    @Query(value="select ao.* from project ap, object ao,  user_vendor_mapping_object_mapping auavmaom,user_vendor_mapping auavm ,user au where  auavmaom.object_id=ao.id and ao.project_id=ap.id and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=au.id and au.id= ?1 and ap.id=?2 and ao.status=1 and ao.is_dummy=0",nativeQuery = true)
    Page<Object> findAllObjectsByCustomerAdminProject(Pageable pageable, Customer customer, Long aidasProjectId);

    @Query(value="select o.* from project p, object o,  user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm,user u where  uvmom.object_id=o.id and o.project_id=p.id and uvmom.user_vendor_mapping_id=uvm.id and  uvm.user_id=u.id and uvm.vendor_id= ?1 and p.id=?2",nativeQuery = true)
    Page<Object> findAllObjectsByOrgAdminProject(Pageable pageable, Organisation organisation, Long aidasProjectId);

    @Query(value = "select count(*) from object o, project p, customer c where o.project_id=p.id and p.customer_id=c.id and c.organisation_id=? and o.status=1 and o.is_dummy=0",nativeQuery = true)
    Long countAidasObjectByAidasProject_AidasCustomer_AidasOrganisation(Long organisationId);

    @Query(value = "select count(*) from object o, project p, customer c where o.project_id=p.id and p.customer_id=?1 and o.status=1 and o.is_dummy=0",nativeQuery = true)
    Long countAidasObjectByAidasProject_AidasCustomer(Long customerId);

    @Query(value = "select ao.* from object ao where ao.project_id=?1 and ao.status=1 and ao.is_dummy=0",nativeQuery = true)
    List<Object> findAllObjectsOfProject(Long aidasProjectId);

    @Query(value = "select ao.* from object ao where ao.project_id=?1 ",nativeQuery = true)
    List<Object> findAllObjectsOfProjectInlcudingDummy(Long aidasProjectId);

    @Query(value="select count(*) from (select ao.id,count(*) from user_vendor_mapping_object_mapping  auavmaom, user_vendor_mapping auavm, user au, object ao where auavmaom.object_id=ao.id and auavmaom.user_vendor_mapping_id=auavm.id and auavm.user_id=au.id and au.vendor_id=?1 and ao.status=1 and ao.is_dummy=0 group by ao.id )a",nativeQuery = true)
    Long countAidasObjectByVendor(Long aidasVendorId);

    @Query(value=" select count(*) from (select ao.id from user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm, object ao where  uvmom.object_id=ao.id and uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id=?1 group by ao.id)a",nativeQuery = true)
    Long countAidasProjectByVendorUser(Long aidasVendorUserId);

    @Query(value="select \n" +
        "o.id as objectId ,\n" +
        "count(u.id) totalUploaded, \n" +
        "SUM(CASE WHEN u.approval_status = 1 THEN 1 ELSE 0 END) AS totalApproved, \n" +
        "SUM(CASE WHEN u.approval_status = 0 THEN 1 ELSE 0 END) AS totalRejected, \n" +
        "SUM(CASE WHEN u.approval_status = 2 THEN 1 ELSE 0 END) AS totalPending \n" +
        "from upload u \n" +
        "left  join  user_vendor_mapping_object_mapping uvmom on u.user_vendor_mapping_object_mapping_id=uvmom.id\n" +
        "left join user_vendor_mapping uvm on uvmom.user_vendor_mapping_id=uvm.id \n" +
        "left join object o on uvmom.object_id=o.id\n" +
        "where  uvm.id= ?1 and o.id=?2 and o.status=1  group by o.id \n" +
        "union \n" +
        "select \n" +
        "o.id as objectId ,\n" +
        "count(u.id) totalUploaded, \n" +
        "SUM(CASE WHEN u.approval_status = 1 THEN 1 ELSE 0 END) AS totalApproved, \n" +
        "SUM(CASE WHEN u.approval_status = 0 THEN 1 ELSE 0 END) AS totalRejected, \n" +
        "SUM(CASE WHEN u.approval_status = 2 THEN 1 ELSE 0 END) AS totalPending \n" +
        "from upload u \n" +
        "right  join  user_vendor_mapping_object_mapping uvmom on u.user_vendor_mapping_object_mapping_id=uvmom.id\n" +
        "right join user_vendor_mapping uvm on uvmom.user_vendor_mapping_id=uvm.id \n" +
        "right join object o on uvmom.object_id=o.id\n" +
        "where  uvm.id= ?1 and o.id=?2 and o.status=1  group by o.id",nativeQuery = true)
    IUploadDetail countUploadsByObjectAndUser(Long aidasUserId, Long aidasObjectId);


    @Query(value = "select  " +
        "o.id as objectId, " +
        "count(au.id) as totalUploaded,  " +
        "SUM(CASE WHEN au.status = 1 THEN 1 ELSE 0 END) AS totalApproved,  " +
        "SUM(CASE WHEN au.status = 0 THEN 1 ELSE 0 END) AS totalRejected,  " +
        "SUM(CASE WHEN au.status = 2 THEN 1 ELSE 0 END) AS totalPending " +
        "from  " +
        "object o " +
        "left  join  user_vendor_mapping_object_mapping auavmaom on auavmaom.object_id=o.id  " +
        "left  join upload au on au.user_vendor_mapping_object_mapping_id=auavmaom.id  " +
        "where " +
        "o.id=?1 " +
        "and o.status=1 and o.is_dummy=0 "+
        "group by o.id " +
        "union " +
        "select  " +
        "o.id as objectId, " +
        "count(au.id) as totalUploaded,  " +
        "SUM(CASE WHEN au.status = 1 THEN 1 ELSE 0 END) AS totalApproved,  " +
        "SUM(CASE WHEN au.status = 0 THEN 1 ELSE 0 END) AS totalRejected,  " +
        "SUM(CASE WHEN au.status = 2 THEN 1 ELSE 0 END) AS totalPending " +
        "from  " +
        "object o " +
        "right  join  user_vendor_mapping_object_mapping auavmaom on auavmaom.object_id=o.id  " +
        "right  join upload au on au.user_vendor_mapping_object_mapping_id=auavmaom.id  " +
        "where " +
        "o.id=?1  " +
        "and o.status=1 and o.is_dummy=0 "+
        "group by o.id",nativeQuery = true)
    IUploadDetail countUploadsByObject(Long objectId);





    @Query(value = "select count(*) from object where id>0 and status=1 ", nativeQuery = true)
    Long countAllObjectsForSuperAdmin();

    @Query(value = "select * from object where is_sample_data=1",nativeQuery = true)
    List<Object> getAllSampleObjects();

    @Modifying
    @Query(value = "delete from object where is_sample_data=1 order by id desc",nativeQuery = true)
    void deleteAllSampleObjects();

    @Query(value = "select * from object where is_dummy=1 and project_id=?1",nativeQuery = true)
    Object getDummyObjectOfProject(Long projectId);

    @Query(value = "select * from object_property op where op.object_id=?1 and op.add_to_metadata=1 ",nativeQuery = true)
    List<Long> findAllObjectPropertyForExport(Long projectId);

    @Query(value = "select count(*) from object o where o.object_acquired_by_uvmom_id is not null and o.project_id=?1",nativeQuery = true)
    Integer findAllObjectWithoutAcquiredByAnyUser(Long projectId);

    Object getObjectByName(String name);

    @Query(value ="select p.id from user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm, object o,project p where uvmom.object_id=o.id and uvmom.user_vendor_mapping_id=uvm.id and uvm.user_id=?1 and uvmom.status=1 and o.project_id=p.id and o.object_acquired_by_uvmom_id in (select uvmom1.id from user_vendor_mapping_object_mapping uvmom1, object o1 , user_vendor_mapping uvm1 where uvmom1.user_vendor_mapping_id=uvm1.id and uvm1.user_id=?1 and uvmom.object_id=o.id)",nativeQuery = true)
    List<Long> getObjectsEnabledForUser(Long userId);

    @Query(value = "select o.* from object o where o.object_acquired_by_uvmom_id in (?1)", nativeQuery = true)
    List<Object> getOBjectsForUvmoms(List<Long> uvmomIds);

    @Query(value = "select count(*) from object o where o.project_id=?1 and o.object_acquired_by_uvmom_id is null and o.is_dummy=0", nativeQuery = true)
    Integer getObjectNotAllocatedYet(Long projectId);

    @Query(value = "select count(*)  from object o where  o.object_acquired_by_uvmom_id in (?1) and o.total_uploaded<=o.number_of_uploads_required", nativeQuery = true)
    Integer getObjectsNotCompleted(List<Long> uvmomIds);

}
