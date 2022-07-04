package com.ainnotate.aidas.domain;

import com.ainnotate.aidas.dto.ProjectDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A AidasProject.
 */


@NamedNativeQuery(name = "Project.findProjectWithUploadCountByUser",
    query = "select \n" +
        "projectId as id, \n" +
        "sum(totalRequired) as totalRequired,\n" +
        "sum(totalUploaded) as totalUploaded,\n" +
        "sum(totalApproved) as totalApproved,\n" +
        "sum(totalRejected) as totalRejected,\n" +
        "sum(totalPending) as totalPending,\n" +
        "a.status , \n" +
        "a.audio_type , \n" +
        "a.auto_create_objects , \n" +
        "a.buffer_percent , \n" +
        "a.description , \n" +
        "a.external_dataset_status , \n" +
        "a.image_type , \n" +
        "a.name , \n" +
        "a.num_of_objects , \n" +
        "a.num_of_uploads_reqd , \n" +
        "a.object_prefix , \n" +
        "a.object_suffix , \n" +
        "a.project_type , \n" +
        "a.qc_levels , \n" +
        "a.rework_status , \n" +
        "a.video_type \n" +
        "from (\n" +
        "select \n" +
        "p.id as projectId,\n" +
        "o.id as objectId,\n" +
        "o.number_of_upload_reqd as totalRequired, \n" +
        "count(u.id) as totalUploaded, \n" +
        "sum(CASE WHEN u.approval_status = 1 THEN 1 ELSE 0 END) AS totalApproved,  \n" +
        "sum(CASE WHEN u.approval_status = 0 THEN 1 ELSE 0 END) AS totalRejected,   \n" +
        "sum(CASE WHEN u.approval_status = 2 THEN 1 ELSE 0 END) AS totalPending, \n" +
        "p.status , \n" +
        "p.audio_type , \n" +
        "p.auto_create_objects , \n" +
        "p.buffer_percent , \n" +
        "p.description , \n" +
        "p.external_dataset_status , \n" +
        "p.image_type , \n" +
        "p.name , \n" +
        "p.num_of_objects , \n" +
        "p.num_of_uploads_reqd , \n" +
        "p.object_prefix , \n" +
        "p.object_suffix , \n" +
        "p.project_type , \n" +
        "p.qc_levels , \n" +
        "p.rework_status , \n" +
        "p.video_type \n" +
        "from upload u    \n" +
        "left join user_vendor_mapping_object_mapping uvmom on u.user_vendor_mapping_object_mapping_id=uvmom.id   \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id   \n" +
        "left join project p on o.project_id=p.id \n" +
        "where    uvm.user_id=?1 and uvmom.status=1  \n" +
        "group by o.id,u.user_vendor_mapping_object_mapping_id\n" +
        ") a group by projectId union "+
        " (select " +
        "        p.id," +
        "        sum(o.number_of_upload_reqd) as totalRequired, " +
        "        0 as totalUploaded, " +
        "        0 AS totalApproved,  " +
        "        0 AS totalRejected,   " +
        "        0 AS totalPending," +
        "        p.status ," +
        "        p.audio_type ," +
        "        p.auto_create_objects ," +
        "        p.buffer_percent ," +
        "        p.description ," +
        "        p.external_dataset_status ," +
        "        p.image_type ," +
        "        p.name ," +
        "        p.num_of_objects ," +
        "        p.num_of_uploads_reqd ," +
        "        p.object_prefix ," +
        "        p.object_suffix ," +
        "        p.project_type ," +
        "        p.qc_levels ," +
        "        p.rework_status ," +
        "        p.video_type " +
        "        from  user_vendor_mapping_object_mapping uvmom  " +
        "        left join object o on o.id=uvmom.object_id   " +
        "        left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id   " +
        "        left join project p on o.project_id=p.id" +
        "        where    uvm.user_id=?1 and uvmom.status=1 and p.id not in(" +
            "select " +
        "        p.id" +
        "        from upload u    " +
        "        left join user_vendor_mapping_object_mapping uvmom on u.user_vendor_mapping_object_mapping_id=uvmom.id   " +
        "        left join object o on o.id=uvmom.object_id   " +
        "        left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id   " +
        "        left join project p on o.project_id=p.id" +
        "        where    uvm.user_id=?1 and uvmom.status=1 " +
        "        group by p.id order by p.id desc"+
        ") " +
        "        group by p.id order by p.id desc)  ",
    resultSetMapping = "Mapping.ProjectDTO")

@NamedNativeQuery(name = "Project.findProjectWithUploadCountByUser.count",
    query = "select count(*) from ((select " +
        "        p.id," +
        "        count(u.id) as totalUploaded, " +
        "        SUM(CASE WHEN u.approval_status = 1 THEN 1 ELSE 0 END) AS totalApproved,  " +
        "        SUM(CASE WHEN u.approval_status = 0 THEN 1 ELSE 0 END) AS totalRejected,   " +
        "        SUM(CASE WHEN u.approval_status = 2 THEN 1 ELSE 0 END) AS totalPending," +
        "        p.status ," +
        "        p.audio_type ," +
        "        p.auto_create_objects ," +
        "        p.buffer_percent ," +
        "        p.description ," +
        "        p.external_dataset_status ," +
        "        p.image_type ," +
        "        p.name ," +
        "        p.num_of_objects ," +
        "        p.num_of_uploads_reqd ," +
        "        p.object_prefix ," +
        "        p.object_suffix ," +
        "        p.project_type ," +
        "        p.qc_levels ," +
        "        p.rework_status ," +
        "        p.video_type " +
        "        from upload u    " +
        "        left join user_vendor_mapping_object_mapping uvmom on u.user_vendor_mapping_object_mapping_id=uvmom.id   " +
        "        left join object o on o.id=uvmom.object_id   " +
        "        left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id   " +
        "        left join project p on o.project_id=p.id" +
        "        where    uvm.user_id=?1 and uvmom.status=1 " +
        "        group by p.id order by p.id desc) union  " +
        " (select " +
        "        p.id," +
        "        0 as totalUploaded, " +
        "        0 AS totalApproved,  " +
        "        0 AS totalRejected,   " +
        "        0 AS totalPending," +
        "        p.status ," +
        "        p.audio_type ," +
        "        p.auto_create_objects ," +
        "        p.buffer_percent ," +
        "        p.description ," +
        "        p.external_dataset_status ," +
        "        p.image_type ," +
        "        p.name ," +
        "        p.num_of_objects ," +
        "        p.num_of_uploads_reqd ," +
        "        p.object_prefix ," +
        "        p.object_suffix ," +
        "        p.project_type ," +
        "        p.qc_levels ," +
        "        p.rework_status ," +
        "        p.video_type " +
        "        from  user_vendor_mapping_object_mapping uvmom  " +
        "        left join object o on o.id=uvmom.object_id   " +
        "        left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id   " +
        "        left join project p on o.project_id=p.id" +
        "        where    uvm.user_id=?1 and p.id not in(" +
        "select " +
        "        p.id" +
        "        from upload u    " +
        "        left join user_vendor_mapping_object_mapping uvmom on u.user_vendor_mapping_object_mapping_id=uvmom.id   " +
        "        left join object o on o.id=uvmom.object_id   " +
        "        left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id   " +
        "        left join project p on o.project_id=p.id" +
        "        where    uvm.user_id=?1  " +
        "        group by p.id order by p.id desc"+
        ") " +
        "        group by p.id order by p.id desc))a  ")


@SqlResultSetMapping(name = "Mapping.ProjectDTO",
    classes = @ConstructorResult(targetClass = ProjectDTO.class,
        columns = {
            @ColumnResult(name = "id",type = Long.class),
            @ColumnResult(name = "totalRequired",type = Integer.class),
            @ColumnResult(name = "totalUploaded",type = Integer.class),
            @ColumnResult(name = "totalApproved",type = Integer.class),
            @ColumnResult(name = "totalRejected",type = Integer.class),
            @ColumnResult(name = "totalPending",type = Integer.class),
            @ColumnResult(name = "status",type = Integer.class),
            @ColumnResult(name = "audio_type",type = String.class),
            @ColumnResult(name = "auto_create_objects",type = Integer.class),
            @ColumnResult(name = "buffer_percent",type = Integer.class),
            @ColumnResult(name = "description",type = String.class),
            @ColumnResult(name = "external_dataset_status",type = Integer.class),
            @ColumnResult(name = "image_type",type = String.class),
            @ColumnResult(name = "name",type = String.class),
            @ColumnResult(name = "num_of_objects",type = Integer.class),
            @ColumnResult(name = "num_of_uploads_reqd",type = Integer.class),
            @ColumnResult(name = "object_prefix",type = String.class),
            @ColumnResult(name = "object_suffix",type = String.class),
            @ColumnResult(name = "project_type",type = String.class),
            @ColumnResult(name = "qc_levels",type = Integer.class),
            @ColumnResult(name = "rework_status",type = Integer.class),
            @ColumnResult(name = "video_type",type = String.class)
        }))


@NamedNativeQuery(name = "Project.findProjectWithUploadCountByUserForDropDown",
    query = "select \n" +
        "projectId as id, \n" +
        "sum(totalRequired) as totalRequired,\n" +
        "sum(totalUploaded) as totalUploaded,\n" +
        "sum(totalApproved) as totalApproved,\n" +
        "sum(totalRejected) as totalRejected,\n" +
        "sum(totalPending) as totalPending,\n" +
        "a.status , \n" +
        "a.audio_type , \n" +
        "a.auto_create_objects , \n" +
        "a.buffer_percent , \n" +
        "a.description , \n" +
        "a.external_dataset_status , \n" +
        "a.image_type , \n" +
        "a.name , \n" +
        "a.num_of_objects , \n" +
        "a.num_of_uploads_reqd , \n" +
        "a.object_prefix , \n" +
        "a.object_suffix , \n" +
        "a.project_type , \n" +
        "a.qc_levels , \n" +
        "a.rework_status , \n" +
        "a.video_type \n" +
        "from (\n" +
        "select \n" +
        "p.id as projectId,\n" +
        "o.id as objectId,\n" +
        "o.number_of_upload_reqd as totalRequired, \n" +
        "count(u.id) as totalUploaded, \n" +
        "sum(CASE WHEN u.approval_status = 1 THEN 1 ELSE 0 END) AS totalApproved,  \n" +
        "sum(CASE WHEN u.approval_status = 0 THEN 1 ELSE 0 END) AS totalRejected,   \n" +
        "sum(CASE WHEN u.approval_status = 2 THEN 1 ELSE 0 END) AS totalPending, \n" +
        "p.status , \n" +
        "p.audio_type , \n" +
        "p.auto_create_objects , \n" +
        "p.buffer_percent , \n" +
        "p.description , \n" +
        "p.external_dataset_status , \n" +
        "p.image_type , \n" +
        "p.name , \n" +
        "p.num_of_objects , \n" +
        "p.num_of_uploads_reqd , \n" +
        "p.object_prefix , \n" +
        "p.object_suffix , \n" +
        "p.project_type , \n" +
        "p.qc_levels , \n" +
        "p.rework_status , \n" +
        "p.video_type \n" +
        "from upload u    \n" +
        "left join user_vendor_mapping_object_mapping uvmom on u.user_vendor_mapping_object_mapping_id=uvmom.id   \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id   \n" +
        "left join project p on o.project_id=p.id \n" +
        "where    uvm.user_id=?1 and uvmom.status=1  \n" +
        "group by o.id,u.user_vendor_mapping_object_mapping_id\n" +
        ") a group by projectId union "+
        " (select " +
        "        p.id," +
        "        sum(o.number_of_upload_reqd) as totalRequired, " +
        "        0 as totalUploaded, " +
        "        0 AS totalApproved,  " +
        "        0 AS totalRejected,   " +
        "        0 AS totalPending," +
        "        p.status ," +
        "        p.audio_type ," +
        "        p.auto_create_objects ," +
        "        p.buffer_percent ," +
        "        p.description ," +
        "        p.external_dataset_status ," +
        "        p.image_type ," +
        "        p.name ," +
        "        p.num_of_objects ," +
        "        p.num_of_uploads_reqd ," +
        "        p.object_prefix ," +
        "        p.object_suffix ," +
        "        p.project_type ," +
        "        p.qc_levels ," +
        "        p.rework_status ," +
        "        p.video_type " +
        "        from  user_vendor_mapping_object_mapping uvmom  " +
        "        left join object o on o.id=uvmom.object_id   " +
        "        left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id   " +
        "        left join project p on o.project_id=p.id" +
        "        where    uvm.user_id=?1 and uvmom.status=1 and p.id not in(" +
        "select " +
        "        p.id" +
        "        from upload u    " +
        "        left join user_vendor_mapping_object_mapping uvmom on u.user_vendor_mapping_object_mapping_id=uvmom.id   " +
        "        left join object o on o.id=uvmom.object_id   " +
        "        left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id   " +
        "        left join project p on o.project_id=p.id" +
        "        where    uvm.user_id=?1 and uvmom.status=1 " +
        "        group by p.id order by p.id desc"+
        ") " +
        "        group by p.id order by p.id desc)  ",
    resultSetMapping = "Mapping.ProjectDTOForDropDown")

@NamedNativeQuery(name = "Project.findProjectWithUploadCountByUserForDropDown.count",
    query = "select count(*) from ((select " +
        "        p.id," +
        "        count(u.id) as totalUploaded, " +
        "        SUM(CASE WHEN u.approval_status = 1 THEN 1 ELSE 0 END) AS totalApproved,  " +
        "        SUM(CASE WHEN u.approval_status = 0 THEN 1 ELSE 0 END) AS totalRejected,   " +
        "        SUM(CASE WHEN u.approval_status = 2 THEN 1 ELSE 0 END) AS totalPending," +
        "        p.status ," +
        "        p.audio_type ," +
        "        p.auto_create_objects ," +
        "        p.buffer_percent ," +
        "        p.description ," +
        "        p.external_dataset_status ," +
        "        p.image_type ," +
        "        p.name ," +
        "        p.num_of_objects ," +
        "        p.num_of_uploads_reqd ," +
        "        p.object_prefix ," +
        "        p.object_suffix ," +
        "        p.project_type ," +
        "        p.qc_levels ," +
        "        p.rework_status ," +
        "        p.video_type " +
        "        from upload u    " +
        "        left join user_vendor_mapping_object_mapping uvmom on u.user_vendor_mapping_object_mapping_id=uvmom.id   " +
        "        left join object o on o.id=uvmom.object_id   " +
        "        left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id   " +
        "        left join project p on o.project_id=p.id" +
        "        where    uvm.user_id=?1 and uvmom.status=1 " +
        "        group by p.id order by p.id desc) union  " +
        " (select " +
        "        p.id," +
        "        0 as totalUploaded, " +
        "        0 AS totalApproved,  " +
        "        0 AS totalRejected,   " +
        "        0 AS totalPending," +
        "        p.status ," +
        "        p.audio_type ," +
        "        p.auto_create_objects ," +
        "        p.buffer_percent ," +
        "        p.description ," +
        "        p.external_dataset_status ," +
        "        p.image_type ," +
        "        p.name ," +
        "        p.num_of_objects ," +
        "        p.num_of_uploads_reqd ," +
        "        p.object_prefix ," +
        "        p.object_suffix ," +
        "        p.project_type ," +
        "        p.qc_levels ," +
        "        p.rework_status ," +
        "        p.video_type " +
        "        from  user_vendor_mapping_object_mapping uvmom  " +
        "        left join object o on o.id=uvmom.object_id   " +
        "        left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id   " +
        "        left join project p on o.project_id=p.id" +
        "        where    uvm.user_id=?1 and p.id not in(" +
        "select " +
        "        p.id" +
        "        from upload u    " +
        "        left join user_vendor_mapping_object_mapping uvmom on u.user_vendor_mapping_object_mapping_id=uvmom.id   " +
        "        left join object o on o.id=uvmom.object_id   " +
        "        left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id   " +
        "        left join project p on o.project_id=p.id" +
        "        where    uvm.user_id=?1  " +
        "        group by p.id order by p.id desc"+
        ") " +
        "        group by p.id order by p.id desc))a  ")


@SqlResultSetMapping(name = "Mapping.ProjectDTOForDropDown",
    classes = @ConstructorResult(targetClass = ProjectDTO.class,
        columns = {
            @ColumnResult(name = "id",type = Long.class),
            @ColumnResult(name = "totalRequired",type = Integer.class),
            @ColumnResult(name = "totalUploaded",type = Integer.class),
            @ColumnResult(name = "totalApproved",type = Integer.class),
            @ColumnResult(name = "totalRejected",type = Integer.class),
            @ColumnResult(name = "totalPending",type = Integer.class),
            @ColumnResult(name = "status",type = Integer.class),
            @ColumnResult(name = "audio_type",type = String.class),
            @ColumnResult(name = "auto_create_objects",type = Integer.class),
            @ColumnResult(name = "buffer_percent",type = Integer.class),
            @ColumnResult(name = "description",type = String.class),
            @ColumnResult(name = "external_dataset_status",type = Integer.class),
            @ColumnResult(name = "image_type",type = String.class),
            @ColumnResult(name = "name",type = String.class),
            @ColumnResult(name = "num_of_objects",type = Integer.class),
            @ColumnResult(name = "num_of_uploads_reqd",type = Integer.class),
            @ColumnResult(name = "object_prefix",type = String.class),
            @ColumnResult(name = "object_suffix",type = String.class),
            @ColumnResult(name = "project_type",type = String.class),
            @ColumnResult(name = "qc_levels",type = Integer.class),
            @ColumnResult(name = "rework_status",type = Integer.class),
            @ColumnResult(name = "video_type",type = String.class)
        }))


@NamedNativeQuery(
    name = "Project.findAllByIdGreaterThanForDropDown",
    query="select id, name from project where id>0",
    resultSetMapping = "Mapping.findAllByIdGreaterThanForDropDown"
)
@SqlResultSetMapping(name = "Mapping.findAllByIdGreaterThanForDropDown",
    classes = @ConstructorResult(targetClass = ProjectDTO.class,
        columns = {
            @ColumnResult(name = "id",type = Long.class),
            @ColumnResult(name = "name",type = String.class)
        }))

@NamedNativeQuery(
    name = "Project.findAllByAidasCustomer_AidasOrganisationForDropDown",
    query="select * from project p , customer c where p.customer_id=c.id and c.organisation_id=?1 and p.status=1",
    resultSetMapping = "Mapping.findAllByAidasCustomer_AidasOrganisationForDropDown"
)
@SqlResultSetMapping(name = "Mapping.findAllByAidasCustomer_AidasOrganisationForDropDown",
    classes = @ConstructorResult(targetClass = ProjectDTO.class,
        columns = {
            @ColumnResult(name = "id",type = Long.class),
            @ColumnResult(name = "name",type = String.class)
        }))

@NamedNativeQuery(
    name = "Project.findAllByAidasCustomerForDropDown",
    query="select * from project p  where p.customer_id=?1 and p.status=1",
    resultSetMapping = "Mapping.findAllByAidasCustomerForDropDown"
)
@SqlResultSetMapping(name = "Mapping.findAllByAidasCustomerForDropDown",
    classes = @ConstructorResult(targetClass = ProjectDTO.class,
        columns = {
            @ColumnResult(name = "id",type = Long.class),
            @ColumnResult(name = "name",type = String.class)
        }))

@NamedNativeQuery(
    name = "Project.findAllProjectsByVendorAdminDropDown",
    query="select p.* from project p, object o,  user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm ,user u where  uvmom.object_id=o.id and o.project_id=p.id and uvmom.user_vendor_mapping_id=uvm.id and uvm.vendor_id= ?1   and p.status=1",
    resultSetMapping = "Mapping.findAllProjectsByVendorAdminDropDown"
)
@SqlResultSetMapping(name = "Mapping.findAllProjectsByVendorAdminDropDown",
    classes = @ConstructorResult(targetClass = ProjectDTO.class,
        columns = {
            @ColumnResult(name = "id",type = Long.class),
            @ColumnResult(name = "name",type = String.class)
        }))

@NamedNativeQuery(
    name = "Project.findProjectsForQC",
    query="select p.* from project p, qc_project_mapping qpm, user_customer_mapping ucm where qpm.user_customer_mapping_id=ucm.id and ucm.user_id=? and qpm.project_id=p.id and p.status=1 and qpm.status=1 and ucm.status=1 and p.id>0 order by p.id desc",
    resultSetMapping = "Mapping.findProjectsForQC"
)
@SqlResultSetMapping(name = "Mapping.findProjectsForQC",
    classes = @ConstructorResult(targetClass = ProjectDTO.class,
        columns = {
            @ColumnResult(name = "id",type = Long.class),
            @ColumnResult(name = "name",type = String.class)
        }))


@Entity
@Table(name = "project",indexes = {
    @Index(name="idx_project_category",columnList = "category_id"),
    @Index(name="idx_project_customer",columnList = "customer_id")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_project_customer_name",columnNames={"name", "customer_id"})
    })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "project")
@Audited
public class Project extends AbstractAuditingEntity  implements Serializable {



    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 500)
    @Column(name = "name", length = 500, nullable = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "project_type")
    private String projectType;

    @Column(name = "rework_status")
    private Integer reworkStatus=0;

    @Column(name = "qc_levels")
    private Integer qcLevels;

    @Transient
    @Column (name = "total_uploaded")
    @JsonProperty
    private Integer totalUploaded;

    @Transient
    @Column(name = "total_approved")
    @JsonProperty
    private Integer totalApproved;

    @Transient
    @Column(name = "total_rejected")
    @JsonProperty
    private Integer totalRejected;

    @Transient
    @Column(name = "total_pending")
    @JsonProperty
    private Integer totalPending;


    @Column(name="auto_create_objects")
    @JsonProperty
    private Integer autoCreateObjects=0;

    @Column(name="num_of_objects")
    @JsonProperty
    private Integer numOfObjects;

    @Column(name="object_prefix")
    @JsonProperty
    private String objectPrefix;

    @Column(name="object_suffix")
    @JsonProperty
    private String objectSuffix;

    @Column(name="external_dataset_status")
    @JsonProperty
    private Integer externalDatasetStatus;

    @Column(name="image_type")
    @JsonProperty
    private String imageType;

    @Column(name="video_type")
    @JsonProperty
    private String videoType;

    @Column(name="audio_type")
    @JsonProperty
    private String audioType;
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "organisation" }, allowSetters = true)
    @Field(type = FieldType.Nested,store = false,storeNullValue = false)
    @JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name="fk_project_customer"))
    private Customer customer;

    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "project" }, allowSetters = true)
    @Field(type=FieldType.Nested,store = false,storeNullValue = false)
    private Set<ProjectProperty> projectProperties=new HashSet<>();
    @Column
    private Integer numOfUploadsReqd;
    @Column(name="buffer_percent")
    private Integer bufferPercent;

    @ManyToOne(optional = true)
    @JoinColumn(name = "category_id", nullable = true, foreignKey = @ForeignKey(name="fk_project_category"))
    private Category category;

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getAudioType() {
        return audioType;
    }

    public void setAudioType(String audioType) {
        this.audioType = audioType;
    }

    public Integer getExternalDatasetStatus() {
        return externalDatasetStatus;
    }

    public void setExternalDatasetStatus(Integer externalDatasetStatus) {
        this.externalDatasetStatus = externalDatasetStatus;
    }

    public Integer getNumOfUploadsReqd() {
        return numOfUploadsReqd;
    }

    public void setNumOfUploadsReqd(Integer numOfUploadsReqd) {
        this.numOfUploadsReqd = numOfUploadsReqd;
    }



    public Integer getAutoCreateObjects() {
        return autoCreateObjects;
    }

    public void setAutoCreateObjects(Integer autoCreateObjects) {
        this.autoCreateObjects = autoCreateObjects;
    }

    public Integer getNumOfObjects() {
        return numOfObjects;
    }

    public void setNumOfObjects(Integer numOfObjects) {
        this.numOfObjects = numOfObjects;
    }

    public String getObjectPrefix() {
        return objectPrefix;
    }

    public void setObjectPrefix(String objectPrefix) {
        this.objectPrefix = objectPrefix;
    }

    public Integer getReworkStatus() {
        return reworkStatus;
    }

    public void setReworkStatus(Integer reworkStatus) {
        this.reworkStatus = reworkStatus;
    }

    public Integer getQcLevels() {
        return qcLevels;
    }

    public void setQcLevels(Integer qcLevels) {
        this.qcLevels = qcLevels;
    }

    public String getObjectSuffix() {
        return objectSuffix;
    }

    public void setObjectSuffix(String objectSuffix) {
        this.objectSuffix = objectSuffix;
    }

    public Integer getBufferPercent() {
        return bufferPercent;
    }

    public void setBufferPercent(Integer bufferPercent) {
        this.bufferPercent = bufferPercent;
    }

    public Integer getTotalUploaded() {
        return totalUploaded;
    }

    public void setTotalUploaded(Integer totalUploaded) {
        this.totalUploaded = totalUploaded;
    }

    public Integer getTotalApproved() {
        return totalApproved;
    }

    public void setTotalApproved(Integer totalApproved) {
        this.totalApproved = totalApproved;
    }

    public Integer getTotalRejected() {
        return totalRejected;
    }

    public void setTotalRejected(Integer totalRejected) {
        this.totalRejected = totalRejected;
    }

    public Integer getTotalPending() {
        return totalPending;
    }

    public void setTotalPending(Integer totalPending) {
        this.totalPending = totalPending;
    }

    public Set<ProjectProperty> getProjectProperties() {
        return projectProperties;
    }

    public void setProjectProperties(Set<ProjectProperty> projectProperties) {
        this.projectProperties = projectProperties;
    }

    public void addAidasProjectProperty(ProjectProperty projectProperty){
        this.projectProperties.add(projectProperty);
    }

    public void removeAidasProjectProperty(ProjectProperty projectProperty){
        this.projectProperties.remove(projectProperty);
    }
// jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project id(Long id) {
        this.setId(id);
        return this;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Project name(String name) {
        this.setName(name);
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Project description(String description) {
        this.setDescription(description);
        return this;
    }

    public String getProjectType() {
        return this.projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public Project projectType(String projectType) {
        this.setProjectType(projectType);
        return this;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Project customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Project)) {
            return false;
        }
        return id != null && id.equals(((Project) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AidasProject{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", projectType='" + getProjectType() + "'" +
            "}";
    }
}
