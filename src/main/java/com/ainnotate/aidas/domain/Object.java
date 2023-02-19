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
/*@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProject",
query = "select \n" +
    "o.id," +
    "uvmom.id as userVendorMappingObjectMappingId,"+
    "o.project_id as projectId,"+
    "o.parent_object_id parentObjectId,"+
    "o.number_of_uploads_required as numberOfUploadsRequired," +
    "o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired," +
    "o.total_required as totalRequired," +
    "uvmom.total_uploaded as totalUploaded, \n" +
    "uvmom.total_approved as totalApproved, \n" +
    "uvmom.total_rejected as totalRejected, \n" +
    "uvmom.total_pending as totalPending,\n" +
    "o.buffer_percent as bufferPercent," +
    "o.name as name," +
    "o.description as description," +
    "o.image_type as imageType," +
    "o.audio_type as audioType," +
    "o.video_type as videoType " +
    "from user_vendor_mapping_object_mapping uvmom  \n" +
    "left join object o on o.id=uvmom.object_id   \n" +
    "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
    "where    uvm.user_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2"
    ,resultSetMapping = "Mapping.ObjectDTO")*/

@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProject",
    query = "select\n" +
        "    o.id,\n" +
        "    uvmom.uvmom_id as userVendorMappingObjectMappingId,\n" +
        "    o.project_id as projectId,\n" +
        "    o.parent_object_id as parentObjectId,\n" +
        "    o.number_of_uploads_required as numberOfUploadsRequired,\n" +
        "    o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired,\n" +
        "    ((uvmom.total_required-(select sum(cuvmomv.total_uploaded) from consolidated_user_vendor_mapping_object_mapping_view cuvmomv where object_id=609 group by object_id))\n" +
        "    +  (select sum(cuvmomv.rejected) from consolidated_user_vendor_mapping_object_mapping_view cuvmomv where object_id=609 group by object_id)) as totalRequired,\n" +
        "    uvmom.total_uploaded as totalUploaded,\n" +
        "    uvmom.approved as totalApproved,\n" +
        "    uvmom.rejected as totalRejected,\n" +
        "    uvmom.pending as totalPending,\n" +
        "    o.buffer_percent as bufferPercent,\n" +
        "    o.name as name,\n" +
        "    o.description as description,\n" +
        "    o.image_type as imageType,\n" +
        "    o.audio_type as audioType,\n" +
        "    o.video_type as videoType ,\n" +
        "    o.name\n" +
        "    from consolidated_user_vendor_mapping_object_mapping_view uvmom,object o where uvmom.object_id=o.id and  o.project_id=?2"
    ,resultSetMapping = "Mapping.ObjectDTO")





@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProject.count",
    query ="select count(o.id) as count  \n" +
            "from user_vendor_mapping_object_mapping uvmom  \n" +
            "left join object o on o.id=uvmom.object_id   \n" +
            "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
            "where    uvm.user_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2",resultSetMapping = "Mapping.ObjectDTOCount"
)
@SqlResultSetMappings(value={
    @SqlResultSetMapping(
        name = "Mapping.ObjectDTOCount",
        columns = { @ColumnResult(name = "count", type = Integer.class) }
    ),
    @SqlResultSetMapping(
        name = "Mapping.ObjectDTO",
        classes = @ConstructorResult(targetClass = ObjectDTO.class,
            columns = {
                @ColumnResult(name = "id",type = Long.class),
                @ColumnResult(name = "userVendorMappingObjectMappingId",type = Long.class),
                @ColumnResult(name = "projectId",type = Long.class),
                @ColumnResult(name = "parentObjectId",type = Long.class),
                @ColumnResult(name = "totalRequired",type = Integer.class),
                @ColumnResult(name = "numberOfUploadsRequired",type = Integer.class),
                @ColumnResult(name = "numberOfBufferedUploadsRequired",type = Integer.class),
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
            })),
    @SqlResultSetMapping(name = "Mapping.ObjectDTOWithProjectId",
        classes = @ConstructorResult(targetClass = ObjectDTO.class,
            columns = {
                @ColumnResult(name = "id",type = Long.class),
                @ColumnResult(name = "userVendorMappingObjectMappingId",type = Long.class),
                @ColumnResult(name = "projectId",type = Long.class),
                @ColumnResult(name = "parentObjectId",type = Long.class),
                @ColumnResult(name = "numberOfUploadsRequired",type = Integer.class),
                @ColumnResult(name = "numberOfBufferedUploadsRequired",type = Integer.class),
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
            })),
    @SqlResultSetMapping(name = "Mapping.ObjectDTOForDropdown",
        classes = @ConstructorResult(targetClass = ObjectDTO.class,
            columns = {
                @ColumnResult(name = "id",type = Long.class),
                @ColumnResult(name = "userVendorMappingObjectMappingId",type = Long.class),
                @ColumnResult(name = "projectId",type = Long.class),
                @ColumnResult(name = "parentObjectId",type = Long.class),
                @ColumnResult(name = "totalRequired",type = Integer.class),
                @ColumnResult(name = "numberOfUploadsRequired",type = Integer.class),
                @ColumnResult(name = "numberOfBufferedUploadsRequired",type = Integer.class),
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
            })),
    @SqlResultSetMapping(name = "Mapping.getAllObjectDTOsOfProject",
        classes = @ConstructorResult(targetClass = ObjectDTO.class,
            columns = {
                @ColumnResult(name = "id",type = Long.class),
                @ColumnResult(name = "name",type = String.class)
            }))
})







@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectWithProjectIdForNonGrouped",
query = "select \n" +
    "o.id," +
    "-1 as userVendorMappingObjectMappingId,"+
    "o.project_id as projectId,"+
    "o.parent_object_id parentObjectId,"+
    "o.number_of_uploads_required as numberOfUploadsRequired," +
    "o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired," +
    "o.total_required as totalRequired," +
    "0 as totalUploaded, \n" +
    "0 as totalApproved, \n" +
    "0 as totalRejected, \n" +
    "0 as totalPending,\n" +
    "o.buffer_percent as bufferPercent," +
    "o.name as name," +
    "o.description as description," +
    "o.image_type as imageType," +
    "o.audio_type as audioType," +
    "o.video_type as videoType " +
    "from object o \n" +
    "where o.status=1 and o.is_dummy=0  and o.project_id=?1"
    ,resultSetMapping = "Mapping.ObjectDTOWithProjectId")



@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectWithProjectIdForNonGrouped.count",
query = "select count(o.id) as count  \n" +
        "from object o  \n" +
        "where o.status=1 and o.is_dummy=0 and o.project_id=?1",resultSetMapping = "Mapping.ObjectDTOCount"
)






@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectWithProjectId",
    query = "select \n" +
        "o.id," +
        "-1 as userVendorMappingObjectMappingId,"+
        "o.project_id as projectId,"+
        "o.parent_object_id parentObjectId,"+
        "o.number_of_uploads_required as numberOfUploadsRequired," +
        "o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired," +
        "o.total_required as totalRequired," +
        "0 as totalUploaded, \n" +
        "0 as totalApproved, \n" +
        "0 as totalRejected, \n" +
        "0 as totalPending,\n" +
        "o.buffer_percent as bufferPercent," +
        "o.name as name," +
        "o.description as description," +
        "o.image_type as imageType," +
        "o.audio_type as audioType," +
        "o.video_type as videoType " +
        "from object o \n" +
        "where o.status=1 and o.is_dummy=0  and o.project_id=?1 and o.object_acquired_by_uvmom_id is null"
        ,resultSetMapping = "Mapping.ObjectDTOWithProjectId")



@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectWithProjectId.count",
    query = "select count(o.id) as count  \n" +
            "from object o  \n" +
            "where o.status=1 and o.is_dummy=0 and o.project_id=?1",resultSetMapping = "Mapping.ObjectDTOCount"
)










@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectWithProjectIdForGroupedForNewRequest",
query = "select \n" +
    "o.id," +
    "uvmom.id as userVendorMappingObjectMappingId,"+
    "o.project_id as projectId,"+
    "o.parent_object_id parentObjectId,"+
    "o.number_of_uploads_required as numberOfUploadsRequired," +
    "o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired," +
    "o.total_required as totalRequired," +
    "uvmom.total_uploaded as totalUploaded, \n" +
    "uvmom.total_approved as totalApproved, \n" +
    "uvmom.total_rejected as totalRejected, \n" +
    "uvmom.total_pending as totalPending,\n" +
    "o.buffer_percent as bufferPercent," +
    "o.name as name," +
    "o.description as description," +
    "o.image_type as imageType," +
    "o.audio_type as audioType," +
    "o.video_type as videoType " +
    "from user_vendor_mapping_object_mapping uvmom  \n" +
    "left join object o on o.id=uvmom.object_id   \n" +
    "where uvmom.user_vendor_mapping_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0  and o.project_id=?2 "
    ,resultSetMapping = "Mapping.ObjectDTOWithProjectId")



@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectWithProjectIdForGroupedForNewRequest.count",
query = "select count(o.id) as count  \n" +
        "from user_vendor_mapping_object_mapping uvmom  \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "where uvmom.user_vendor_mapping_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2",resultSetMapping = "Mapping.ObjectDTOCount"
)






@NamedNativeQuery(name="Object.getAllObjectsWithUvmom",
query = "select \n" +
    "o.id," +
    "uvmom.id as userVendorMappingObjectMappingId,"+
    "o.project_id as projectId,"+
    "o.parent_object_id parentObjectId,"+
    "o.number_of_uploads_required as numberOfUploadsRequired," +
    "o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired," +
    "o.total_required as totalRequired," +
    "uvmom.total_uploaded as totalUploaded, \n" +
    "uvmom.total_approved as totalApproved, \n" +
    "uvmom.total_rejected as totalRejected, \n" +
    "uvmom.total_pending as totalPending,\n" +
    "o.buffer_percent as bufferPercent," +
    "o.name as name," +
    "o.description as description," +
    "o.image_type as imageType," +
    "o.audio_type as audioType," +
    "o.video_type as videoType " +
    "from user_vendor_mapping_object_mapping uvmom  \n" +
    "left join object o on o.id=uvmom.object_id   \n" +
    "where uvmom.user_vendor_mapping_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0  and o.project_id=?2"
    ,resultSetMapping = "Mapping.ObjectDTOWithProjectId")



@NamedNativeQuery(name="Object.getAllObjectsWithUvmom.count",
query = "select count(o.id) as count  \n" +
        "from user_vendor_mapping_object_mapping uvmom  \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "where uvmom.user_vendor_mapping_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2",resultSetMapping = "Mapping.ObjectDTOCount"
)







@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectWithProjectIdAndObjectAlreadyAssigned",
    query = "select \n" +
        "o.id," +
        "uvmom.id as userVendorMappingObjectMappingId,"+
        "o.project_id as projectId,"+
        "o.parent_object_id parentObjectId,"+
        "o.number_of_uploads_required as numberOfUploadsRequired," +
        "o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired," +
        "o.total_required as totalRequired," +
        "uvmom.id as userVendorMappingObjectMappingId," +
        "uvmom.total_uploaded as totalUploaded, \n" +
        "uvmom.total_approved as totalApproved, \n" +
        "uvmom.total_rejected as totalRejected, \n" +
        "uvmom.total_pending as totalPending,\n" +
        "o.buffer_percent as bufferPercent," +
        "o.name as name," +
        "o.description as description," +
        "o.image_type as imageType," +
        "o.audio_type as audioType," +
        "o.video_type as videoType " +
        "from user_vendor_mapping_object_mapping uvmom  \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
        "where uvm.user_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2 and o.object_acquired_by_uvmom_id in (?3) "
    ,resultSetMapping = "Mapping.ObjectDTOWithProjectId")

@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectWithProjectIdAndObjectAlreadyAssigned.count",
    query = "select count(o.id) as count  \n" +
        "from user_vendor_mapping_object_mapping uvmom  \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
        "where uvm.user_id=?1 and o.status=1 and o.is_dummy=0 and o.project_id=?2 and o.object_acquired_by_uvmom_id in (?3) ",resultSetMapping = "Mapping.ObjectDTOCount"
)





@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectWithProjectIdForGroupedDto",
    query = "select \n" +
        "o.id," +
        "uvmom.id as userVendorMappingObjectMappingId,"+
        "o.project_id as projectId,"+
        "o.parent_object_id parentObjectId,"+
        "o.number_of_uploads_required as numberOfUploadsRequired," +
        "o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired," +
        "o.total_required as totalRequired," +
        "uvmom.id as userVendorMappingObjectMappingId," +
        "uvmom.total_uploaded as totalUploaded, \n" +
        "uvmom.total_approved as totalApproved, \n" +
        "uvmom.total_rejected as totalRejected, \n" +
        "uvmom.total_pending as totalPending,\n" +
        "o.buffer_percent as bufferPercent," +
        "o.name as name," +
        "o.description as description," +
        "o.image_type as imageType," +
        "o.audio_type as audioType," +
        "o.video_type as videoType " +
        "from user_vendor_mapping_object_mapping uvmom  \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
        "where uvm.user_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2 and o.object_acquired_by_uvmom_id in (?3) "
    ,resultSetMapping = "Mapping.ObjectDTOWithProjectId")

@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectWithProjectIdForGroupedDto.count",
    query = "select count(o.id) as count  \n" +
        "from user_vendor_mapping_object_mapping uvmom  \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
        "where uvm.user_id=?1 and o.status=1 and o.is_dummy=0 and o.project_id=?2 and o.object_acquired_by_uvmom_id in (?3) ",resultSetMapping = "Mapping.ObjectDTOCount"
)





@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectForDropdown",
    query = "select \n" +
        "o.id," +
        "uvmom.id as userVendorMappingObjectMappingId,"+
        "o.project_id as projectId,"+
        "o.parent_object_id parentObjectId,"+
        "o.number_of_uploads_required as numberOfUploadsRequired," +
        "o.number_of_buffered_uploads_required as numberOfBufferedUploadsRequired," +
        "o.total_required as totalRequired," +
        "uvmom.total_uploaded as totalUploaded, \n" +
        "uvmom.total_approved as totalApproved, \n" +
        "uvmom.total_rejected as totalRejected, \n" +
        "uvmom.total_pending as totalPending,\n" +
        "o.buffer_percent as bufferPercent," +
        "o.name as name," +
        "o.description as description," +
        "o.image_type as imageType," +
        "o.audio_type as audioType," +
        "o.video_type as videoType " +
        "from user_vendor_mapping_object_mapping uvmom  \n" +
        "left join object o on o.id=uvmom.object_id   \n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
        "where    uvm.user_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2"
        ,resultSetMapping = "Mapping.ObjectDTOForDropdown")

@NamedNativeQuery(name="Object.getAllObjectsByVendorUserProjectForDropdown.count",
    query ="select count(o.id) as count  \n" +
            "from user_vendor_mapping_object_mapping uvmom  \n" +
            "left join object o on o.id=uvmom.object_id   \n" +
            "left join user_vendor_mapping uvm on uvm.id=uvmom.user_vendor_mapping_id \n" +
            "where    uvm.user_id=?1 and uvmom.status=1 and o.status=1 and o.is_dummy=0 and o.project_id=?2",resultSetMapping = "Mapping.ObjectDTOCount"
)


@NamedNativeQuery(
    name = "Object.getAllObjectDTOsOfProjectOld",
    query="select id,name from object where status=1 and is_dummy=0 and project_id=?1",
    resultSetMapping = "Mapping.getAllObjectDTOsOfProject"
)

@NamedNativeQuery(
    name = "Object.getAllObjectDTOsOfProject",
    query="select o.id,o.name from object o, consolidated_user_vendor_mapping_object_mapping_view cuvmomv where o.status=1 and o.is_dummy=0 and o.id=cuvmomv.object_id and cuvmomv.uvmom_status>0 and o.project_id=?1 ",
    resultSetMapping = "Mapping.getAllObjectDTOsOfProject"
)


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
    @Column(name = "name", length = 500, nullable = true)
    private String name;

    @Column(name="buffer_percent")
    private Integer bufferPercent=20;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "number_of_uploads_required", nullable = true)
    private Integer numberOfUploadsRequired=0;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
    @JoinColumn(name = "project_id", nullable = false, foreignKey = @ForeignKey(name="fk_object_project"))
    private Project project;

    @Column(name="is_dummy")
    private Integer dummy=0;
    @ManyToOne(optional = true)
    @JsonIgnoreProperties(value = { "project","customer" }, allowSetters = true)
    @JoinColumn(name = "parent_object_id", nullable = true, foreignKey = @ForeignKey(name="fk_object_parent_object"))
    private Object parentObject;

    @Column(name ="total_uploaded",columnDefinition = "integer default 0")
    @JsonProperty
    private Integer totalUploaded=0;

    @Column(name ="total_approved",columnDefinition = "integer default 0")
    @JsonProperty
    private Integer totalApproved=0;

    @Column(name ="total_rejected",columnDefinition = "integer default 0")
    @JsonProperty
    private Integer totalRejected=0;

    @Column(name ="total_pending",columnDefinition = "integer default 0")
    @JsonProperty
    private Integer totalPending=0;

    @Column(name ="total_required",columnDefinition = "integer default 0")
    @JsonProperty
    private Integer totalRequired=0;

    @Column(name ="number_of_buffered_uploads_required",columnDefinition = "integer default 0")
    private Integer numberOfBufferedUploadsRequired=0;


    @Column(name="image_type")
    @JsonProperty
    private String imageType="";
    @Column(name="video_type")
    @JsonProperty
    private String videoType="";
    @Column(name="audio_type")
    @JsonProperty
    private String audioType="";

    @OneToMany(mappedBy = "object",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @Filter(name = "objectPropertyStatusFilter",condition="status = 1")
    @JsonIgnoreProperties(value = { "object" }, allowSetters = true)
    private Set<ObjectProperty> objectProperties = new HashSet<>();

    //This is userVendorMappingObjectMappingId not the userId
    @Column(name="object_acquired_by_uvmom_id" ,columnDefinition = "integer default null")
    private Long objectAcquiredByUvmomId;

    
    @Column(name="qc_start_status" ,columnDefinition = "integer default null")
    private Integer qcStartStatus=0;

    @Column(name="current_qc_level" ,columnDefinition = "integer default null")
    private Integer currentQcLevel=0;

    public Integer getCurrentQcLevel() {
		return currentQcLevel;
	}

	public void setCurrentQcLevel(Integer currentQcLevel) {
		this.currentQcLevel = currentQcLevel;
	}

	public Integer getQcStartStatus() {
        return qcStartStatus;
    }

    public void setQcStartStatus(Integer qcStartStatus) {
        this.qcStartStatus = qcStartStatus;
    }

    @Transient
    @JsonProperty
    private Long userVendorMappingObjectMappingId;



    public Long getUserVendorMappingObjectMappingId() {
        return userVendorMappingObjectMappingId;
    }

    public void setUserVendorMappingObjectMappingId(Long userVendorMappingObjectMappingId) {
        this.userVendorMappingObjectMappingId = userVendorMappingObjectMappingId;
    }
/*@Column(name="batch_status" ,columnDefinition = "integer default null")
    private Integer batchStatus;

    public Integer getBatchStatus() {
        return batchStatus;
    }

    public void setBatchStatus(Integer batchStatus) {
        this.batchStatus = batchStatus;
    }*/

    public Long getObjectAcquiredByUvmomId() {
        return objectAcquiredByUvmomId;
    }

    public void setObjectAcquiredByUvmomId(Long objectAcquiredByUvmomId) {
        this.objectAcquiredByUvmomId = objectAcquiredByUvmomId;
    }

    public Integer getTotalRequired() {
        return totalRequired;
    }

    public void setTotalRequired(Integer totalRequired) {
        this.totalRequired = totalRequired;
    }

    public Integer getDummy() {
        return dummy;
    }

    public void setDummy(Integer dummy) {
        this.dummy = dummy;
    }

    public Object getParentObject() {
        return parentObject;
    }

    public void setParentObject(Object parentObject) {
        this.parentObject = parentObject;
    }

    public Object getParentAidasObject() {
        return parentObject;
    }

    public void setParentAidasObject(Object parentObject) {
        this.parentObject = parentObject;
    }

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

    public void setId(Long id) {
        this.id = id;
    }

    public Object id(Long id) {
        this.setId(id);
        return this;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object name(String name) {
        this.setName(name);
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object description(String description) {
        this.setDescription(description);
        return this;
    }

    public Integer getNumberOfUploadsRequired() {
        return numberOfUploadsRequired;
    }

    public void setNumberOfUploadsRequired(Integer numberOfUploadsRequired) {
        this.numberOfUploadsRequired = numberOfUploadsRequired;
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

    public Integer getNumberOfBufferedUploadsRequired() {
        return numberOfBufferedUploadsRequired;
    }

    public void setNumberOfBufferedUploadsRequired(Integer numberOfBufferedUploadsRequired) {
        this.numberOfBufferedUploadsRequired = numberOfBufferedUploadsRequired;
    }

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
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "AidasObject{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", numberOfUploadReqd=" + getNumberOfUploadsRequired() +
            "}";
    }
}
