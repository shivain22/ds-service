package com.ainnotate.aidas.domain;

import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.dto.ProjectDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A AidasProject.
 */



@NamedNativeQuery(name = "Project.findProjectWithUploadCountByUser",
    query = "select  \n" +
        "p.id as id,  \n" +
        "case when p.auto_create_objects=1 then p.total_required_for_grouped else p.total_required end as totalRequired," +
        "case when p.auto_create_objects=1 then uvmpm.total_uploaded_for_grouped else uvmpm.total_uploaded end as totalUploaded, \n" +
        "case when p.auto_create_objects=1 then uvmpm.total_approved_for_grouped else uvmpm.total_approved end as totalApproved , \n" +
        "case when p.auto_create_objects=1 then uvmpm.total_rejected_for_grouped else uvmpm.total_rejected end as totalRejected, \n" +
        "case when p.auto_create_objects=1 then uvmpm.total_pending_for_grouped else uvmpm.total_pending end as totalPending,\n" +
        "p.status ,\n" +
        "p.audio_type ,\n" +
        "p.auto_create_objects ,\n" +
        "p.buffer_percent ,\n" +
        "p.description ,\n" +
        "p.external_dataset_status ,\n" +
        "p.image_type ,\n" +
        "p.name ,\n" +
        "p.number_of_objects ,\n" +
        "p.number_of_uploads_required ,\n" +
        "p.object_prefix ,\n" +
        "p.object_suffix ,\n" +
        "p.project_type ,\n" +
        "p.qc_levels ,\n" +
        "p.rework_status ,\n" +
        "p.video_type,\n" +
        "p.pause_status,\n" +
        "p.consent_form_status,\n" +
        "p.bypass_metadata,\n" +
        "c.name as customer_name,\n" +
        "p.consent_form_link as consent_form_link,\n" +
        "p.project_description_link as project_description_link\n" +
        "from \n" +
        "project p, user_vendor_mapping_project_mapping uvmpm,user_vendor_mapping uvm, customer c " +
        "where uvmpm.project_id=p.id and uvmpm.status=1 and p.status=1 and uvmpm.user_vendor_mapping_id=uvm.id and p.customer_id=c.id and uvm.user_id=?1 and p.pause_status=1 order by p.id desc",
    resultSetMapping = "Mapping.ProjectDTO")


@NamedNativeQuery(name = "Project.findProjectWithUploadCountByUserSearch",
query = "select  \n" +
    "p.id as id,  \n" +
    "case when p.auto_create_objects=1 then p.total_required_for_grouped else p.total_required end as totalRequired," +
    "case when p.auto_create_objects=1 then uvmpm.total_uploaded_for_grouped else uvmpm.total_uploaded end as totalUploaded, \n" +
    "case when p.auto_create_objects=1 then uvmpm.total_approved_for_grouped else uvmpm.total_approved end as totalApproved , \n" +
    "case when p.auto_create_objects=1 then uvmpm.total_rejected_for_grouped else uvmpm.total_rejected end as totalRejected, \n" +
    "case when p.auto_create_objects=1 then uvmpm.total_pending_for_grouped else uvmpm.total_pending end as totalPending,\n" +
    "p.status ,\n" +
    "p.audio_type ,\n" +
    "p.auto_create_objects ,\n" +
    "p.buffer_percent ,\n" +
    "p.description ,\n" +
    "p.external_dataset_status ,\n" +
    "p.image_type ,\n" +
    "p.name ,\n" +
    "p.number_of_objects ,\n" +
    "p.number_of_uploads_required ,\n" +
    "p.object_prefix ,\n" +
    "p.object_suffix ,\n" +
    "p.project_type ,\n" +
    "p.qc_levels ,\n" +
    "p.rework_status ,\n" +
    "p.video_type,\n" +
    "p.pause_status,\n" +
    "p.consent_form_status,\n" +
    "p.bypass_metadata,\n" +
    "c.name as customer_name,\n" +
    "p.consent_form_link as consent_form_link,\n" +
    "p.project_description_link as project_description_link\n" +
    "from \n" +
    "project p, user_vendor_mapping_project_mapping uvmpm,user_vendor_mapping uvm, customer c " +
    "where uvmpm.project_id=p.id and uvmpm.status=1 and p.status=1 and uvmpm.user_vendor_mapping_id=uvm.id and p.customer_id=c.id and uvm.user_id=?1 and p.pause_status=1 and p.name like ?2 order by p.id desc",
resultSetMapping = "Mapping.ProjectDTO")



@NamedNativeQuery(name = "Project.findProjectWithUploadCountByUser.count",
    query ="select  \n" +
        "count(p.id) as count  \n" +
        "from \n" +
        "project p, user_vendor_mapping_project_mapping uvmpm,user_vendor_mapping uvm, customer c" +
        " where uvmpm.project_id=p.id and uvmpm.status=1 and p.status=1 and uvmpm.user_vendor_mapping_id=uvm.id and p.customer_id=c.id and uvm.user_id=?1 and p.pause_status=1 ",resultSetMapping = "Mapping.findProjectWithUploadCountByUserCount")


@NamedNativeQuery(name = "Project.findProjectWithUploadCountByUserForAllowedProjects",
    query = "select  \n" +
        "p.id as id,  \n" +
        "p.total_required as totalRequired, \n" +
        "uvmpm.total_uploaded as totalUploaded, \n" +
        "uvmpm.total_approved as totalApproved, \n" +
        "uvmpm.total_rejected as totalRejected, \n" +
        "uvmpm.total_pending as totalPending,\n" +
        "p.status ,\n" +
        "p.audio_type ,\n" +
        "p.auto_create_objects ,\n" +
        "p.buffer_percent ,\n" +
        "p.description ,\n" +
        "p.external_dataset_status ,\n" +
        "p.image_type ,\n" +
        "p.name ,\n" +
        "p.number_of_objects ,\n" +
        "p.number_of_uploads_required ,\n" +
        "p.object_prefix ,\n" +
        "p.object_suffix ,\n" +
        "p.project_type ,\n" +
        "p.qc_levels ,\n" +
        "p.rework_status ,\n" +
        "p.video_type,\n" +
        "p.pause_status,\n" +
        "p.consent_form_status,\n" +
        "p.bypass_metadata,\n" +
        "c.name as customer_name,\n"+
        "p.consent_form_link as consent_form_link,\n" +
        "p.project_description_link as project_description_link\n" +
        "from \n" +
        "project p\n" +
        "left join user_vendor_mapping_project_mapping uvmpm on p.id=uvmpm.project_id\n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmpm.user_vendor_mapping_id \n" +
        "left join customer c on p.customer_id=c.id \n" +
        "where p.status=1 and uvm.user_id=?1 and uvm.status=1 and uvmpm.status=1 and p.id in (?2)",
    resultSetMapping = "Mapping.ProjectDTO")



@NamedNativeQuery(name = "Project.findProjectWithUploadCountByUserForAllowedProjects.count",
    query ="select  \n" +
        "count(p.id) as count  \n" +
        "from \n" +
        "project p\n" +
        "left join user_vendor_mapping_project_mapping uvmpm on p.id=uvmpm.project_id\n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmpm.user_vendor_mapping_id \n" +
        "where p.status=1 and uvm.user_id=?1 and uvm.status=1 and uvmpm.status=1 and p.id in (?2)",resultSetMapping = "Mapping.findProjectWithUploadCountByUserCount")

@SqlResultSetMappings(value = {
    @SqlResultSetMapping(
        name = "Mapping.findProjectWithUploadCountByUserCount",
        columns = { @ColumnResult(name = "count", type = Long.class) }
    ),
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
                @ColumnResult(name = "number_of_objects",type = Integer.class),
                @ColumnResult(name = "number_of_uploads_required",type = Integer.class),
                @ColumnResult(name = "object_prefix",type = String.class),
                @ColumnResult(name = "object_suffix",type = String.class),
                @ColumnResult(name = "project_type",type = String.class),
                @ColumnResult(name = "qc_levels",type = Integer.class),
                @ColumnResult(name = "rework_status",type = Integer.class),
                @ColumnResult(name = "video_type",type = String.class),
                @ColumnResult(name = "pause_status",type = Integer.class),
                @ColumnResult(name = "consent_form_status",type = Integer.class),
                @ColumnResult(name = "bypass_metadata",type = Integer.class),
                @ColumnResult(name = "customer_name",type = String.class),
                @ColumnResult(name = "consent_form_link",type = String.class),
                @ColumnResult(name = "project_description_link",type = String.class),
                
            })),
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
                @ColumnResult(name = "number_of_objects",type = Integer.class),
                @ColumnResult(name = "number_of_uploads_required",type = Integer.class),
                @ColumnResult(name = "object_prefix",type = String.class),
                @ColumnResult(name = "object_suffix",type = String.class),
                @ColumnResult(name = "project_type",type = String.class),
                @ColumnResult(name = "qc_levels",type = Integer.class),
                @ColumnResult(name = "rework_status",type = Integer.class),
                @ColumnResult(name = "video_type",type = String.class)
            })),
    @SqlResultSetMapping(name = "Mapping.findAllByIdGreaterThanForDropDown",
        classes = @ConstructorResult(targetClass = ProjectDTO.class,
            columns = {
                @ColumnResult(name = "id",type = Long.class),
                @ColumnResult(name = "name",type = String.class),
                @ColumnResult(name = "project_type",type = String.class),
                @ColumnResult(name = "auto_create_objects",type = Integer.class)
            })),
    @SqlResultSetMapping(name = "Mapping.findAllByAidasCustomer_AidasOrganisationForDropDown",
        classes = @ConstructorResult(targetClass = ProjectDTO.class,
            columns = {
                @ColumnResult(name = "id",type = Long.class),
                @ColumnResult(name = "name",type = String.class),
                @ColumnResult(name = "project_type",type = String.class),
                @ColumnResult(name = "auto_create_objects",type = Integer.class)
            })),
    @SqlResultSetMapping(name = "Mapping.findAllByAidasCustomerForDropDown",
        classes = @ConstructorResult(targetClass = ProjectDTO.class,
            columns = {
                @ColumnResult(name = "id",type = Long.class),
                @ColumnResult(name = "name",type = String.class),
                @ColumnResult(name = "project_type",type = String.class),
                @ColumnResult(name = "auto_create_objects",type = Integer.class)
            })),
    @SqlResultSetMapping(name = "Mapping.findAllProjectsByVendorAdminDropDown",
        classes = @ConstructorResult(targetClass = ProjectDTO.class,
            columns = {
                @ColumnResult(name = "id",type = Long.class),
                @ColumnResult(name = "name",type = String.class),
                @ColumnResult(name = "project_type",type = String.class),
                @ColumnResult(name = "auto_create_objects",type = Integer.class)
            })),
    @SqlResultSetMapping(name = "Mapping.findProjectsForCustomerQC",
        classes = @ConstructorResult(targetClass = ProjectDTO.class,
            columns = {
                @ColumnResult(name = "id",type = Long.class),
                @ColumnResult(name = "name",type = String.class),
                @ColumnResult(name = "project_type",type = String.class),
                @ColumnResult(name = "auto_create_objects",type = Integer.class)
            })),
    @SqlResultSetMapping(name = "Mapping.findProjectsForOrganisationQC",
        classes = @ConstructorResult(targetClass = ProjectDTO.class,
            columns = {
                @ColumnResult(name = "id",type = Long.class),
                @ColumnResult(name = "name",type = String.class),
                @ColumnResult(name = "project_type",type = String.class),
                @ColumnResult(name = "auto_create_objects",type = Integer.class)
            }))
})




@NamedNativeQuery(name = "Project.findProjectWithUploadCountByUserForDropDown",
    query = "select  \n" +
        "p.id as id,  \n" +
        "p.total_required as totalRequired, \n" +
        "cuvmpmv.total_uploaded as totalUploaded, \n" +
        "cuvmpmv.approved as totalApproved, \n" +
        "cuvmpmv.rejected as totalRejected, \n" +
        "cuvmpmv.pending as totalPending,\n" +
        "p.status ,\n" +
        "p.audio_type ,\n" +
        "p.auto_create_objects ,\n" +
        "p.buffer_percent ,\n" +
        "p.description ,\n" +
        "p.external_dataset_status ,\n" +
        "p.image_type ,\n" +
        "p.name ,\n" +
        "p.number_of_objects ,\n" +
        "p.number_of_uploads_required ,\n" +
        "p.object_prefix ,\n" +
        "p.object_suffix ,\n" +
        "p.project_type ,\n" +
        "p.qc_levels ,\n" +
        "p.rework_status ,\n" +
        "p.video_type\n" +
        "from \n" +
        "project p,\n" +
        "consolidated_user_vendor_mapping_project_mapping_view cuvmpmv, \n" +
        "user_vendor_mapping uvm \n" +
        "where \n" +
        "p.id=cuvmpmv.project_id and\n" +
        "uvm.id=cuvmpmv.uvm_id and\n" +
        "uvm.user_id=?1 and \n" +
        "uvm.status=1  \n" +
        "and p.id>0  \n" +
        "and cuvmpmv.uvmpm_status>0\n" +
        "order by p.id desc;\n",
    resultSetMapping = "Mapping.ProjectDTOForDropDown")

@NamedNativeQuery(name = "Project.findProjectWithUploadCountByUserForDropDown.count",
    query = "select count(*) from (" +
        "select  \n" +
        "p.id as id  \n" +
        "from \n" +
        "project p\n" +
        "left join user_vendor_mapping_project_mapping uvmpm on p.id=uvmpm.project_id\n" +
        "left join user_vendor_mapping uvm on uvm.id=uvmpm.user_vendor_mapping_id \n" +
        "where uvm.user_id=?1 and uvm.status=1 and uvmpm.status=1"+
        ")a  ")





@NamedNativeQuery(
    name = "Project.findAllByIdGreaterThanForDropDown",
    query="select p.* from project p where id>0 and p.status=1 order by id desc",
    resultSetMapping = "Mapping.findAllByIdGreaterThanForDropDown"
)


@NamedNativeQuery(
    name = "Project.findAllByAidasCustomer_AidasOrganisationForDropDown",
    query="select p.* from project p , customer c where p.customer_id=c.id and c.organisation_id=?1 and p.status=1 order by p.id desc",
    resultSetMapping = "Mapping.findAllByAidasCustomer_AidasOrganisationForDropDown"
)


@NamedNativeQuery(
    name = "Project.findAllByAidasCustomerForDropDown",
    query="select p.* from project p  where p.customer_id=?1 and p.status=1 order by p.id desc",
    resultSetMapping = "Mapping.findAllByAidasCustomerForDropDown"
)


@NamedNativeQuery(
    name = "Project.findAllProjectsByVendorAdminDropDown",
    query="select p.* from project p, object o,  user_vendor_mapping_object_mapping uvmom,user_vendor_mapping uvm ,user u where  uvmom.object_id=o.id and o.project_id=p.id and uvmom.user_vendor_mapping_id=uvm.id and uvm.vendor_id= ?1   and p.status=1 order by p.id desc",
    resultSetMapping = "Mapping.findAllProjectsByVendorAdminDropDown"
)


@NamedNativeQuery(
    name = "Project.findProjectsForCustomerQC",
    query="select distinct p.* from project p, customer_qc_project_mapping cqpm, user_customer_mapping ucm where cqpm.user_customer_mapping_id=ucm.id and ucm.user_id=?1 and cqpm.project_id=p.id and p.status=1 and cqpm.status=1 and ucm.status=1 and p.id>0 order by p.id desc",
    resultSetMapping = "Mapping.findProjectsForCustomerQC"
)


@NamedNativeQuery(
    name = "Project.findProjectsForOrganisationQC",
    query="select p.* from project p, organisation_qc_project_mapping oqpm, user_customer_mapping ucm where oqpm.user_organisation_mapping_id=ucm.id and ucm.user_id=? and oqpm.project_id=p.id and p.status=1 and oqpm.status=1 and ucm.status=1 and p.id>0 order by p.id desc ",
    resultSetMapping = "Mapping.findProjectsForOrganisationQC"
)



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
    private String projectType="";

    @Column(name = "rework_status")
    private Integer reworkStatus=0;

    @Column(name = "qc_levels")
    private Integer qcLevels=1;


    @Column (name = "total_uploaded",columnDefinition = "integer default 0")
    @JsonProperty
    private Integer totalUploaded=0;


    @Column(name = "total_approved",columnDefinition = "integer default 0")
    @JsonProperty
    private Integer totalApproved=0;


    @Column(name = "total_rejected",columnDefinition = "integer default 0")
    @JsonProperty
    private Integer totalRejected=0;


    @Column(name = "total_pending",columnDefinition = "integer default 0")
    @JsonProperty
    private Integer totalPending=0;

    @Column(name = "total_required",columnDefinition = "integer default 0")
    @JsonProperty
    private Integer totalRequired=0;

    @Column(name="auto_create_objects",columnDefinition = "integer default 0")
    @JsonProperty
    private Integer autoCreateObjects= AidasConstants.CREATE_MANUAL_OBJECTS;

    @Column(name="number_of_objects",columnDefinition = "integer default 0")
    @JsonProperty
    private Integer numberOfObjects=0;

    @Column(name="object_prefix",columnDefinition = "varchar(50) default ' '")
    @JsonProperty
    private String objectPrefix="";

    @Column(name="object_suffix",columnDefinition = "varchar(50) default ' '")
    @JsonProperty
    private String objectSuffix="";

    @Column(name="external_dataset_status",columnDefinition = "integer default 0")
    @JsonProperty
    private Integer externalDatasetStatus=0;

    @Column(name="image_type")
    @JsonProperty
    private String imageType="";

    @Column(name="video_type")
    @JsonProperty
    private String videoType="";

    @Column(name="uvmom_ids")
    private String uvmomIds;
    
    @Column(name="pause_status")
    private Integer pauseStatus=0;
    
    
    
    public String getProjectDescriptionLink() {
		return projectDescriptionLink;
	}

	public void setProjectDescriptionLink(String projectDescriptionLink) {
		this.projectDescriptionLink = projectDescriptionLink;
	}

	public String getConsentFormLink() {
		return consentFormLink;
	}

	public void setConsentFormLink(String consentFormLink) {
		this.consentFormLink = consentFormLink;
	}
	@Column(name="consent_form_status")
    private Integer consentFormStatus=0;
    
    @Column(name="bypass_metadata")
    private Integer bypassMetatdata=0;

    public Integer getPauseStatus() {
		return pauseStatus;
	}

	public void setPauseStatus(Integer pauseStatus) {
		this.pauseStatus = pauseStatus;
	}

	

	public Integer getConsentFormStatus() {
		return consentFormStatus;
	}

	public void setConsentFormStatus(Integer consentFormStatus) {
		this.consentFormStatus = consentFormStatus;
	}

	public Integer getBypassMetatdata() {
		return bypassMetatdata;
	}

	public void setBypassMetatdata(Integer bypassMetatdata) {
		this.bypassMetatdata = bypassMetatdata;
	}

	public String getUvmomIds() {
        return uvmomIds;
    }
    @Column(name="current_qc_level" ,columnDefinition = "integer default null")
    private Integer currentQcLevel=0;

    public Integer getCurrentQcLevel() {
		return currentQcLevel;
	}

	public void setCurrentQcLevel(Integer currentQcLevel) {
		this.currentQcLevel = currentQcLevel;
	}
    public void setUvmomIds(String uvmomIds) {
        this.uvmomIds = uvmomIds;
    }
    @Column(name="qc_start_status" ,columnDefinition = "integer default null")
    private Integer qcStartStatus=0;

    public Integer getQcStartStatus() {
        return qcStartStatus;
    }

    public void setQcStartStatus(Integer qcStartStatus) {
        this.qcStartStatus = qcStartStatus;
    }
    
    @Transient
    @JsonProperty
    Integer actualUploadsRequired;

    public Integer getActualUploadsRequired() {
        return actualUploadsRequired;
    }

    public void setActualUploadsRequired(Integer actualUploadsRequired) {
        this.actualUploadsRequired = actualUploadsRequired;
    }

    @Column(name="audio_type")
    @JsonProperty
    private String audioType="";

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "organisation" }, allowSetters = true)
    @Field(type = FieldType.Nested,store = false,storeNullValue = false)
    @org.springframework.data.annotation.Transient
    @JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name="fk_project_customer"))
    private Customer customer;

    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = { "project" }, allowSetters = true)
    @Field(type=FieldType.Nested,store = false,storeNullValue = false)
    @org.springframework.data.annotation.Transient
    private Set<ProjectProperty> projectProperties=new HashSet<>();
    
    
    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL)
    private Set<UserVendorMappingProjectMapping> userVendorMappingProjectMappings=new HashSet<>();

    public Set<UserVendorMappingProjectMapping> getUserVendorMappingProjectMappings() {
		return userVendorMappingProjectMappings;
	}

	public void setUserVendorMappingProjectMappings(Set<UserVendorMappingProjectMapping> userVendorMappingProjectMappings) {
		this.userVendorMappingProjectMappings = userVendorMappingProjectMappings;
	}
	@Column(name="number_of_uploads_required")
    private Integer numberOfUploadsRequired=0;

    @Column(name="buffer_percent")
    private Integer bufferPercent=0;
    
    @Column(name="total_required_for_grouped")
    private Integer totalRequiredForGrouped=0;
    
    @Column(name="project_description_link")
    private String projectDescriptionLink;
    
    @Column(name="consent_form_link")
    private String consentFormLink;

    public Integer getTotalRequiredForGrouped() {
		return totalRequiredForGrouped;
	}

	public void setTotalRequiredForGrouped(Integer totalRequiredForGrouped) {
		this.totalRequiredForGrouped = totalRequiredForGrouped;
	}
	@Column(name="buffer_status")
    private Integer bufferStatus=AidasConstants.PROJECT_BUFFER_STATUS_PROJECT_LEVEL;

    @Column(name="buffer_strategy")
    private Integer bufferStrategy=AidasConstants.EQUAL_DISTRIBUTION;

    @Column(name="buffer_ignore_strategy")
    private Integer bufferIgnoreStrategy=AidasConstants.IGNORE_ALREADY_UPLOADED;

    public Integer getBufferIgnoreStrategy() {
        return bufferIgnoreStrategy;
    }

    public void setBufferIgnoreStrategy(Integer bufferIgnoreStrategy) {
        this.bufferIgnoreStrategy = bufferIgnoreStrategy;
    }

    public Integer getBufferStatus() {
        return bufferStatus;
    }

    public void setBufferStatus(Integer bufferStatus) {
        this.bufferStatus = bufferStatus;
    }

    public Integer getBufferStrategy() {
        return bufferStrategy;
    }

    public void setBufferStrategy(Integer bufferStrategy) {
        this.bufferStrategy = bufferStrategy;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "category_id", nullable = true, foreignKey = @ForeignKey(name="fk_project_category"))
    @org.springframework.data.annotation.Transient
    private Category category;

    @ManyToOne(optional = true)
    @JoinColumn(name = "sub_category_id", nullable = true, foreignKey = @ForeignKey(name="fk_project_sub_category"))
    @org.springframework.data.annotation.Transient
    private SubCategory subCategory;

    @Column(name="user_added_status",columnDefinition = "integer default 0")
    private Integer userAddedStatus=0;
    @Column(name ="number_of_buffered_uploads_required",columnDefinition = "integer default 0")
    private Integer numberOfBufferedUploadsdRequired=0;

    @OneToMany(mappedBy="project",cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"project","customer"})
    @org.springframework.data.annotation.Transient
    private Set<ProjectQcLevelConfigurations> projectQcLevelConfigurations =new HashSet<>();

    @Column(name="objects_availability_status" ,columnDefinition = "integer default 1")
    private Integer objectAvailabilityStatus=1;

    @Column(name="number_of_objects_can_be_assigned_to_vuser" ,columnDefinition = "integer default 5")
    private Integer numberOfObjectsCanBeAssignedToVendorUser=10;

    @Column(name="number_of_objects_for_qc_level" ,columnDefinition = "integer default 5")
    private Integer numberOfObjectsForQcLevel=1;

    public Integer getNumberOfObjectsCanBeAssignedToVendorUser() {
        return numberOfObjectsCanBeAssignedToVendorUser;
    }

    public void setNumberOfObjectsCanBeAssignedToVendorUser(Integer numberOfObjectsCanBeAssignedToVendorUser) {
        this.numberOfObjectsCanBeAssignedToVendorUser = numberOfObjectsCanBeAssignedToVendorUser;
    }

    public Integer getNumberOfObjectsForQcLevel() {
        return numberOfObjectsForQcLevel;
    }

    public void setNumberOfObjectsForQcLevel(Integer numberOfObjectsForQcLevel) {
        this.numberOfObjectsForQcLevel = numberOfObjectsForQcLevel;
    }

    public Set<ProjectQcLevelConfigurations> getProjectQcLevelConfigurations() {
        return projectQcLevelConfigurations;
    }

    public void setProjectQcLevelConfigurations(Set<ProjectQcLevelConfigurations> projectQcLevelConfigurations) {
        this.projectQcLevelConfigurations = projectQcLevelConfigurations;
    }



    public Integer getObjectAvailabilityStatus() {
        return objectAvailabilityStatus;
    }

    public void setObjectAvailabilityStatus(Integer objectAvailabilityStatus) {
        this.objectAvailabilityStatus = objectAvailabilityStatus;
    }

    public SubCategory getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(SubCategory subCategory) {
        this.subCategory = subCategory;
    }

    public Integer getUserAddedStatus() {
        return userAddedStatus;
    }

    public void setUserAddedStatus(Integer userAddedStatus) {
        this.userAddedStatus = userAddedStatus;
    }

    public Integer getTotalRequired() {
        return totalRequired;
    }

    public void setTotalRequired(Integer totalRequired) {
        this.totalRequired = totalRequired;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public Integer getExternalDatasetStatus() {
        return externalDatasetStatus;
    }

    public void setExternalDatasetStatus(Integer externalDatasetStatus) {
        this.externalDatasetStatus = externalDatasetStatus;
    }

    public Integer getAutoCreateObjects() {
        return autoCreateObjects;
    }

    public void setAutoCreateObjects(Integer autoCreateObjects) {
        this.autoCreateObjects = autoCreateObjects;
    }

    public Integer getNumberOfObjects() {
        return numberOfObjects;
    }

    public void setNumberOfObjects(Integer numberOfObjects) {
        this.numberOfObjects = numberOfObjects;
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

    public Integer getNumberOfUploadsRequired() {
        return numberOfUploadsRequired;
    }

    public void setNumberOfUploadsRequired(Integer numberOfUploadsRequired) {
        this.numberOfUploadsRequired = numberOfUploadsRequired;
    }

    public Integer getNumberOfBufferedUploadsdRequired() {
        return numberOfBufferedUploadsdRequired;
    }

    public void setNumberOfBufferedUploadsdRequired(Integer numberOfBufferedUploadsdRequired) {
        this.numberOfBufferedUploadsdRequired = numberOfBufferedUploadsdRequired;
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Project{id=" + getId() +", name='" + getName() + "'" +", description='" + getDescription() + "'" +", projectType='" + getProjectType() + "'" +"}";
    }
}
