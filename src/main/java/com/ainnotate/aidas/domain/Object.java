package com.ainnotate.aidas.domain;

import com.ainnotate.aidas.dto.ObjectDTO;
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
import org.hibernate.annotations.Filter;
import org.hibernate.envers.Audited;

/**
 * A AidasObject.
 */
@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProject",
query = "(select \n" +
    "o.id," +
    "o.project_id as projectId,"+
    "o.parent_object_id parentObjectId,"+
    "o.number_of_upload_reqd as totalRequired," +
    "count(u.id) as totalUploaded, \n" +
    "sum(CASE WHEN u.approval_status = 1 THEN 1 ELSE 0 END) AS totalApproved,  \n" +
    "sum(CASE WHEN u.approval_status = 0 THEN 1 ELSE 0 END) AS totalRejected,   \n" +
    "sum(CASE WHEN u.approval_status = 2 THEN 1 ELSE 0 END) AS totalPending, \n" +
    "o.buffer_percent as bufferPercent," +
    "o.name as name," +
    "o.description as description," +
    "o.image_type as imageType," +
    "o.audio_type as audioType," +
    "o.video_type as videoType " +
    "from upload u    \n" +
    "left join user_vendor_mapping_object_mapping uvmom on u.user_vendor_mapping_object_mapping_id=uvmom.id   \n" +
    "left join object o on o.id=uvmom.object_id   \n" +
    "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
    "where    uvm.user_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 \n" +
    "group by o.id,u.user_vendor_mapping_object_mapping_id ) union "+
    "(select \n" +
    "o.id," +
    "o.project_id as projectId,"+
    "o.parent_object_id parentObjectId,"+
    "o.number_of_upload_reqd as totalRequired," +
    "0 as totalUploaded, \n" +
    "0 AS totalApproved,  \n" +
    "0 AS totalRejected,   \n" +
    "0 AS totalPending, \n" +
    "o.buffer_percent as bufferPercent," +
    "o.name as name," +
    "o.description as description," +
    "o.image_type as imageType," +
    "o.audio_type as audioType," +
    "o.video_type as videoType " +
    "from user_vendor_mapping_object_mapping uvmom    \n" +
    "left join object o on o.id=uvmom.object_id   \n" +
    "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
    "where    uvm.user_id=?1  and o.status=1 and o.is_dummy=0 \n" +
    " and uvmom.id not in (" +
    "(select \n" +
    "uvmom.id " +
    "from upload u    \n" +
    "left join user_vendor_mapping_object_mapping uvmom on u.user_vendor_mapping_object_mapping_id=uvmom.id   \n" +
    "left join object o on o.id=uvmom.object_id   \n" +
    "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
    "where    uvm.user_id=?1 and o.status=1 and o.is_dummy=0 \n" +
    "group by o.id,u.user_vendor_mapping_object_mapping_id ) "+
    ")"+
    "group by o.id ) "

    ,resultSetMapping = "Mapping.ObjectDTO")

@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProject.count",
    query =
        "select count(*) from ((select \n" +
            "o.id," +
            "o.project_id as projectId,"+
            "o.parent_object_id parentObjectId,"+
            "o.number_of_upload_reqd as totalRequired," +
            "count(u.id) as totalUploaded, \n" +
            "sum(CASE WHEN u.approval_status = 1 THEN 1 ELSE 0 END) AS totalApproved,  \n" +
            "sum(CASE WHEN u.approval_status = 0 THEN 1 ELSE 0 END) AS totalRejected,   \n" +
            "sum(CASE WHEN u.approval_status = 2 THEN 1 ELSE 0 END) AS totalPending, \n" +
            "o.buffer_percent as bufferPercent," +
            "o.name as name," +
            "o.description as description," +
            "o.image_type as imageType," +
            "o.audio_type as audioType," +
            "o.video_type as videoType " +
            "from upload u    \n" +
            "left join user_vendor_mapping_object_mapping uvmom on u.user_vendor_mapping_object_mapping_id=uvmom.id   \n" +
            "left join object o on o.id=uvmom.object_id   \n" +
            "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
            "where    uvm.user_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 \n" +
            "group by o.id,u.user_vendor_mapping_object_mapping_id ) union "+
            "(select \n" +
            "o.id," +
            "o.project_id as projectId,"+
            "o.parent_object_id parentObjectId,"+
            "o.number_of_upload_reqd as totalRequired," +
            "0 as totalUploaded, \n" +
            "0 AS totalApproved,  \n" +
            "0 AS totalRejected,   \n" +
            "0 AS totalPending, \n" +
            "o.buffer_percent as bufferPercent," +
            "o.name as name," +
            "o.description as description," +
            "o.image_type as imageType," +
            "o.audio_type as audioType," +
            "o.video_type as videoType " +
            "from user_vendor_mapping_object_mapping uvmom    \n" +
            "left join object o on o.id=uvmom.object_id   \n" +
            "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
            "where    uvm.user_id=?1 and o.status=1 and o.is_dummy=0 \n" +
            " and uvmom.id not in (" +
            "(select \n" +
            "uvmom.id " +
            "from upload u    \n" +
            "left join user_vendor_mapping_object_mapping uvmom on u.user_vendor_mapping_object_mapping_id=uvmom.id   \n" +
            "left join object o on o.id=uvmom.object_id   \n" +
            "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
            "where    uvm.user_id=?1  \n" +
            "group by o.id,u.user_vendor_mapping_object_mapping_id ) "+
            ")"+
            "group by o.id ))a "
)
@SqlResultSetMapping(name = "Mapping.ObjectDTO",
    classes = @ConstructorResult(targetClass = ObjectDTO.class,
        columns = {
            @ColumnResult(name = "id",type = Long.class),
            @ColumnResult(name = "userVendorMappingObjectMappingId",type = Long.class),
            @ColumnResult(name = "totalRequired",type = Integer.class),
            @ColumnResult(name = "totalUploaded",type = Integer.class),
            @ColumnResult(name = "totalApproved",type = Integer.class),
            @ColumnResult(name = "totalRejected",type = Integer.class),
            @ColumnResult(name = "totalPending",type = Integer.class),
            @ColumnResult(name = "projectId",type = Long.class),
            @ColumnResult(name = "parentObjectId",type = Long.class),
            @ColumnResult(name = "bufferPercent",type = Integer.class),
            @ColumnResult(name = "name",type = String.class),
            @ColumnResult(name = "description",type = String.class),
            @ColumnResult(name = "imageType",type = String.class),
            @ColumnResult(name = "audioType",type = String.class),
            @ColumnResult(name = "videoType",type = String.class)
        }))





@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectWithProjectId",
    query = "(select \n" +
        "o.id," +
        "o.project_id as projectId,"+
        "uvmom.id as userVendorMappingObjectMappingId,"+
        "o.parent_object_id parentObjectId,"+
        "o.number_of_upload_reqd as totalRequired," +
        "count(u.id) as totalUploaded, \n" +
        "sum(CASE WHEN u.approval_status = 1 THEN 1 ELSE 0 END) AS totalApproved,  \n" +
        "sum(CASE WHEN u.approval_status = 0 THEN 1 ELSE 0 END) AS totalRejected,   \n" +
        "sum(CASE WHEN u.approval_status = 2 THEN 1 ELSE 0 END) AS totalPending, \n" +
        "o.buffer_percent as bufferPercent," +
        "o.name as name," +
        "o.description as description," +
        "o.image_type as imageType," +
        "o.audio_type as audioType," +
        "o.video_type as videoType " +
        "from upload u    \n" +
        "left join user_vendor_mapping_object_mapping uvmom on u.user_vendor_mapping_object_mapping_id=uvmom.id   \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
        "where    uvm.user_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2 \n" +
        "group by o.id,uvmom.id ) union "+
        "(select \n" +
        "o.id," +
        "o.project_id as projectId,"+
        "uvmom.id as userVendorMappingObjectMappingId,"+
        "o.parent_object_id parentObjectId,"+
        "o.number_of_upload_reqd as totalRequired," +
        "0 as totalUploaded, \n" +
        "0 AS totalApproved,  \n" +
        "0 AS totalRejected,   \n" +
        "0 AS totalPending, \n" +
        "o.buffer_percent as bufferPercent," +
        "o.name as name," +
        "o.description as description," +
        "o.image_type as imageType," +
        "o.audio_type as audioType," +
        "o.video_type as videoType " +
        "from user_vendor_mapping_object_mapping uvmom    \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
        "where    uvm.user_id=?1  and o.status=1 and o.is_dummy=0 and o.project_id=?2\n" +
        " and uvmom.id not in (" +
        "(select \n" +
        "uvmom.id " +
        "from upload u    \n" +
        "left join user_vendor_mapping_object_mapping uvmom on u.user_vendor_mapping_object_mapping_id=uvmom.id   \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
        "where    uvm.user_id=?1 and o.status=1 and o.is_dummy=0 and o.project_id=?2 \n" +
        "group by o.id,uvmom.id ) "+
        ")"+
        "group by o.id,uvmom.id ) "

    ,resultSetMapping = "Mapping.ObjectDTOWithProjectId")

@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectWithProjectId.count",
    query =
        "select count(*) from ((select \n" +
            "o.id," +
            "uvmom.id as userVendorMappingObjectMappingId,"+
            "o.project_id as projectId,"+
            "o.parent_object_id parentObjectId,"+
            "o.number_of_upload_reqd as totalRequired," +
            "count(u.id) as totalUploaded, \n" +
            "sum(CASE WHEN u.approval_status = 1 THEN 1 ELSE 0 END) AS totalApproved,  \n" +
            "sum(CASE WHEN u.approval_status = 0 THEN 1 ELSE 0 END) AS totalRejected,   \n" +
            "sum(CASE WHEN u.approval_status = 2 THEN 1 ELSE 0 END) AS totalPending, \n" +
            "o.buffer_percent as bufferPercent," +
            "o.name as name," +
            "o.description as description," +
            "o.image_type as imageType," +
            "o.audio_type as audioType," +
            "o.video_type as videoType " +
            "from upload u    \n" +
            "left join user_vendor_mapping_object_mapping uvmom on u.user_vendor_mapping_object_mapping_id=uvmom.id   \n" +
            "left join object o on o.id=uvmom.object_id   \n" +
            "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
            "where    uvm.user_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2  \n" +
            "group by o.id ) union "+
            "(select \n" +
            "o.id," +
            "uvmom.id as userVendorMappingObjectMappingId,"+
            "o.project_id as projectId,"+
            "o.parent_object_id parentObjectId,"+
            "o.number_of_upload_reqd as totalRequired," +
            "0 as totalUploaded, \n" +
            "0 AS totalApproved,  \n" +
            "0 AS totalRejected,   \n" +
            "0 AS totalPending, \n" +
            "o.buffer_percent as bufferPercent," +
            "o.name as name," +
            "o.description as description," +
            "o.image_type as imageType," +
            "o.audio_type as audioType," +
            "o.video_type as videoType " +
            "from user_vendor_mapping_object_mapping uvmom    \n" +
            "left join object o on o.id=uvmom.object_id   \n" +
            "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
            "where    uvm.user_id=?1 and o.status=1 and o.is_dummy=0 and o.project_id=?2  \n" +
            " and uvmom.id not in (" +
            "(select \n" +
            "uvmom.id " +
            "from upload u    \n" +
            "left join user_vendor_mapping_object_mapping uvmom on u.user_vendor_mapping_object_mapping_id=uvmom.id   \n" +
            "left join object o on o.id=uvmom.object_id   \n" +
            "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
            "where    uvm.user_id=?1 and o.project_id=?2  \n" +
            "group by o.id ) "+
            ")"+
            "group by o.id ))a "
)
@SqlResultSetMapping(name = "Mapping.ObjectDTOWithProjectId",
    classes = @ConstructorResult(targetClass = ObjectDTO.class,
        columns = {
            @ColumnResult(name = "id",type = Long.class),
            @ColumnResult(name = "projectId",type = Long.class),
            @ColumnResult(name = "userVendorMappingObjectMappingId",type = Long.class),
            @ColumnResult(name = "parentObjectId",type = Long.class),
            @ColumnResult(name = "totalRequired",type = Integer.class),
            @ColumnResult(name = "totalUploaded",type = Integer.class),
            @ColumnResult(name = "totalApproved",type = Integer.class),
            @ColumnResult(name = "totalRejected",type = Integer.class),
            @ColumnResult(name = "totalPending",type = Integer.class),
            @ColumnResult(name = "bufferPercent",type = Integer.class),
            @ColumnResult(name = "name",type = String.class),
            @ColumnResult(name = "description",type = String.class),
            @ColumnResult(name = "imageType",type = String.class),
            @ColumnResult(name = "audioType",type = String.class),
            @ColumnResult(name = "videoType",type = String.class)
        }))







@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectForDropdown",
    query = "(select \n" +
        "o.id," +
        "o.project_id as projectId,"+
        "o.parent_object_id parentObjectId,"+
        "o.number_of_upload_reqd as totalRequired," +
        "count(u.id) as totalUploaded, \n" +
        "sum(CASE WHEN u.approval_status = 1 THEN 1 ELSE 0 END) AS totalApproved,  \n" +
        "sum(CASE WHEN u.approval_status = 0 THEN 1 ELSE 0 END) AS totalRejected,   \n" +
        "sum(CASE WHEN u.approval_status = 2 THEN 1 ELSE 0 END) AS totalPending, \n" +
        "o.buffer_percent as bufferPercent," +
        "o.name as name," +
        "o.description as description," +
        "o.image_type as imageType," +
        "o.audio_type as audioType," +
        "o.video_type as videoType " +
        "from upload u    \n" +
        "left join user_vendor_mapping_object_mapping uvmom on u.user_vendor_mapping_object_mapping_id=uvmom.id   \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
        "where    uvm.user_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2\n" +
        "group by o.id,u.user_vendor_mapping_object_mapping_id ) union "+
        "(select \n" +
        "o.id," +
        "o.project_id as projectId,"+
        "o.parent_object_id parentObjectId,"+
        "o.number_of_upload_reqd as totalRequired," +
        "0 as totalUploaded, \n" +
        "0 AS totalApproved,  \n" +
        "0 AS totalRejected,   \n" +
        "0 AS totalPending, \n" +
        "o.buffer_percent as bufferPercent," +
        "o.name as name," +
        "o.description as description," +
        "o.image_type as imageType," +
        "o.audio_type as audioType," +
        "o.video_type as videoType " +
        "from user_vendor_mapping_object_mapping uvmom    \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
        "where    uvm.user_id=?1  and o.status=1 and o.is_dummy=0 and o.project_id=?2 \n" +
        " and uvmom.id not in (" +
        "(select \n" +
        "uvmom.id " +
        "from upload u    \n" +
        "left join user_vendor_mapping_object_mapping uvmom on u.user_vendor_mapping_object_mapping_id=uvmom.id   \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
        "where    uvm.user_id=?1 and o.status=1 and o.is_dummy=0 and o.project_id=?2 \n" +
        "group by o.id,u.user_vendor_mapping_object_mapping_id ) "+
        ")"+
        "group by o.id ) "

    ,resultSetMapping = "Mapping.ObjectDTOForDropdown")

@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectForDropdown.count",
    query =
        "select count(*) from ((select \n" +
            "o.id," +
            "o.project_id as projectId,"+
            "o.parent_object_id parentObjectId,"+
            "o.number_of_upload_reqd as totalRequired," +
            "count(u.id) as totalUploaded, \n" +
            "sum(CASE WHEN u.approval_status = 1 THEN 1 ELSE 0 END) AS totalApproved,  \n" +
            "sum(CASE WHEN u.approval_status = 0 THEN 1 ELSE 0 END) AS totalRejected,   \n" +
            "sum(CASE WHEN u.approval_status = 2 THEN 1 ELSE 0 END) AS totalPending, \n" +
            "o.buffer_percent as bufferPercent," +
            "o.name as name," +
            "o.description as description," +
            "o.image_type as imageType," +
            "o.audio_type as audioType," +
            "o.video_type as videoType " +
            "from upload u    \n" +
            "left join user_vendor_mapping_object_mapping uvmom on u.user_vendor_mapping_object_mapping_id=uvmom.id   \n" +
            "left join object o on o.id=uvmom.object_id   \n" +
            "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
            "where    uvm.user_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 \n" +
            "group by o.id,u.user_vendor_mapping_object_mapping_id ) union "+
            "(select \n" +
            "o.id," +
            "o.project_id as projectId,"+
            "o.parent_object_id parentObjectId,"+
            "o.number_of_upload_reqd as totalRequired," +
            "0 as totalUploaded, \n" +
            "0 AS totalApproved,  \n" +
            "0 AS totalRejected,   \n" +
            "0 AS totalPending, \n" +
            "o.buffer_percent as bufferPercent," +
            "o.name as name," +
            "o.description as description," +
            "o.image_type as imageType," +
            "o.audio_type as audioType," +
            "o.video_type as videoType " +
            "from user_vendor_mapping_object_mapping uvmom    \n" +
            "left join object o on o.id=uvmom.object_id   \n" +
            "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
            "where    uvm.user_id=?1 and o.status=1 and o.is_dummy=0 \n" +
            " and uvmom.id not in (" +
            "(select \n" +
            "uvmom.id " +
            "from upload u    \n" +
            "left join user_vendor_mapping_object_mapping uvmom on u.user_vendor_mapping_object_mapping_id=uvmom.id   \n" +
            "left join object o on o.id=uvmom.object_id   \n" +
            "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
            "where    uvm.user_id=?1  \n" +
            "group by o.id,u.user_vendor_mapping_object_mapping_id ) "+
            ")"+
            "group by o.id ))a "
)
@SqlResultSetMapping(name = "Mapping.ObjectDTOForDropdown",
    classes = @ConstructorResult(targetClass = ObjectDTO.class,
        columns = {
            @ColumnResult(name = "id",type = Long.class),
            @ColumnResult(name = "totalRequired",type = Integer.class),
            @ColumnResult(name = "totalUploaded",type = Integer.class),
            @ColumnResult(name = "totalApproved",type = Integer.class),
            @ColumnResult(name = "totalRejected",type = Integer.class),
            @ColumnResult(name = "totalPending",type = Integer.class),
            @ColumnResult(name = "projectId",type = Long.class),
            @ColumnResult(name = "parentObjectId",type = Long.class),
            @ColumnResult(name = "bufferPercent",type = Integer.class),
            @ColumnResult(name = "name",type = String.class),
            @ColumnResult(name = "description",type = String.class),
            @ColumnResult(name = "imageType",type = String.class),
            @ColumnResult(name = "audioType",type = String.class),
            @ColumnResult(name = "videoType",type = String.class)
        }))

@NamedNativeQuery(
    name = "Object.getAllObjectDTOsOfProject",
    query="select id,name from object where status=1 and is_dummy=0 and project_id=?1",
    resultSetMapping = "Mapping.getAllObjectDTOsOfProject"
)
@SqlResultSetMapping(name = "Mapping.getAllObjectDTOsOfProject",
    classes = @ConstructorResult(targetClass = ObjectDTO.class,
        columns = {
            @ColumnResult(name = "id",type = Long.class),
            @ColumnResult(name = "name",type = String.class)
        }))




@Entity
@Table(name = "object",indexes = {
    @Index(name="idx_object_parent_object",columnList = "parent_object_id"),
    @Index(name="idx_object_project",columnList = "project_id")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_object_project_name",columnNames={"name", "project_id"})
    })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "object")
@Audited
public class Object extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 500)
    @Column(name = "name", length = 500, nullable = true)
    private String name;

    @Column(name="buffer_percent")
    private Integer bufferPercent;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "number_of_upload_reqd", nullable = true)
    private Integer numberOfUploadReqd;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
    @JoinColumn(name = "project_id", nullable = false, foreignKey = @ForeignKey(name="fk_object_project"))
    private Project project;

    @Column(name="is_dummy")
    private Integer dummy;

    public Integer getDummy() {
        return dummy;
    }



    public Object getParentObject() {
        return parentObject;
    }

    public void setParentObject(Object parentObject) {
        this.parentObject = parentObject;
    }

    public void setDummy(Integer dummy) {
        this.dummy = dummy;
    }

    @ManyToOne(optional = true)
    @JsonIgnoreProperties(value = { "project","customer" }, allowSetters = true)
    @JoinColumn(name = "parent_object_id", nullable = true, foreignKey = @ForeignKey(name="fk_object_parent_object"))
    private Object parentObject;

    public Object getParentAidasObject() {
        return parentObject;
    }

    public void setParentAidasObject(Object parentObject) {
        this.parentObject = parentObject;
    }

    @Transient
    @JsonProperty
    private Integer uploadsCompleted;

    @Transient
    @JsonProperty
    private Integer uploadsRemaining;


    @Transient
    @JsonProperty
    private Integer totalUploaded;

    @Transient
    @JsonProperty
    private Integer totalApproved;

    @Transient
    @JsonProperty
    private Integer totalRejected;

    @Transient
    @JsonProperty
    private Integer totalPending;

    @Column(name="image_type")
    @JsonProperty
    private String imageType;

    @Column(name="video_type")
    @JsonProperty
    private String videoType;

    @Column(name="audio_type")
    @JsonProperty
    private String audioType;

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

    public Integer getUploadsCompleted() {
        return uploadsCompleted;
    }

    public void setUploadsCompleted(Integer uploadsCompleted) {
        this.uploadsCompleted = uploadsCompleted;
    }

    public Integer getUploadsRemaining() {
        return uploadsRemaining;
    }

    public void setUploadsRemaining(Integer uploadsRemaining) {
        this.uploadsRemaining = uploadsRemaining;
    }

    @OneToMany(mappedBy = "object",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @Filter(name = "objectPropertyStatusFilter",condition="status = 1")
    @JsonIgnoreProperties(value = { "object" }, allowSetters = true)
    private Set<ObjectProperty> objectProperties = new HashSet<>();

    public Integer getBufferPercent() {
        return bufferPercent;
    }

    public void setBufferPercent(Integer bufferPercent) {
        this.bufferPercent = bufferPercent;
    }

    public Set<ObjectProperty> getObjectProperties() {
        return objectProperties;
    }

    public void setObjectProperties(Set<ObjectProperty> objectProperties) {
        this.objectProperties = objectProperties;
    }

    public void addAidasObjectProperty(ObjectProperty objectProperty){
        this.objectProperties.add(objectProperty);
    }

    public void removeAidasObjectProperty(ObjectProperty objectProperty){
        this.objectProperties.remove(objectProperty);
    }
// jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Object id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Object name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Object description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNumberOfUploadReqd() {
        return this.numberOfUploadReqd;
    }

    public Object numberOfUploadReqd(Integer numberOfUploadReqd) {
        this.setNumberOfUploadReqd(numberOfUploadReqd);
        return this;
    }

    public void setNumberOfUploadReqd(Integer numberOfUploadReqd) {
        this.numberOfUploadReqd = numberOfUploadReqd;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Object project(Project project) {
        this.setProject(project);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Object)) {
            return false;
        }
        return id != null && id.equals(((Object) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AidasObject{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", numberOfUploadReqd=" + getNumberOfUploadReqd() +
            "}";
    }
}
