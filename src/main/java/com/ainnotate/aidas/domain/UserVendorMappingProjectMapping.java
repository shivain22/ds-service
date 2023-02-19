package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * A AidasUserAidasObjectMapping.
 */
@Entity
@Table(name = "user_vendor_mapping_project_mapping",indexes = {
    @Index(name="idx_uvmpm_project",columnList = "project_id"),
    @Index(name="idx_uvmpm_uvm",columnList = "user_vendor_mapping_id")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_uvmid_pid",columnNames={"user_vendor_mapping_id", "project_id"})
    })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "uservendormappingprojectmapping")
@Audited
public class UserVendorMappingProjectMapping extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date_assigned")
    private ZonedDateTime dateAssigned;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @NotNull
    @JsonIgnoreProperties(value = { "organisation", "customer", "vendor" }, allowSetters = true)
    @JoinColumn(name = "user_vendor_mapping_id", nullable = true, foreignKey = @ForeignKey(name="fk_uvmpm_uvm"))
    private UserVendorMapping userVendorMapping;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @NotNull
    @JsonIgnoreProperties(value = { "project" }, allowSetters = true)
    @JoinColumn(name = "project_id", nullable = true, foreignKey = @ForeignKey(name="fk_uvmpm_project"))
    private Project project;

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

    @Column(name ="qc_start_status",columnDefinition = "integer default 0")
    @JsonProperty
    private Integer qcStartStatus=0;

    public Integer getQcStartStatus() {
        return qcStartStatus;
    }
    @Column(name="current_qc_level" ,columnDefinition = "integer default null")
    private Integer currentQcLevel=0;

    public Integer getCurrentQcLevel() {
		return currentQcLevel;
	}

	public void setCurrentQcLevel(Integer currentQcLevel) {
		this.currentQcLevel = currentQcLevel;
	}
    public void setQcStartStatus(Integer qcStartStatus) {
        this.qcStartStatus = qcStartStatus;
    }

    @Column(name="uvmom_ids")
    private String uvmomIds;

    public String getUvmomIds() {
        return uvmomIds;
    }

    public void setUvmomIds(String uvmomIds) {
        this.uvmomIds = uvmomIds;
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

    public Integer getTotalRequired() {
        return totalRequired;
    }

    public void setTotalRequired(Integer totalRequired) {
        this.totalRequired = totalRequired;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserVendorMappingProjectMapping id(Long id) {
        this.setId(id);
        return this;
    }

    public ZonedDateTime getDateAssigned() {
        return this.dateAssigned;
    }

    public void setDateAssigned(ZonedDateTime dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public UserVendorMappingProjectMapping dateAssigned(ZonedDateTime dateAssigned) {
        this.setDateAssigned(dateAssigned);
        return this;
    }

    public UserVendorMappingProjectMapping status(Integer status) {
        this.setStatus(status);
        return this;
    }


    public UserVendorMapping getUserVendorMapping() {
        return userVendorMapping;
    }

    public void setUserVendorMapping(UserVendorMapping userVendorMapping) {
        this.userVendorMapping = userVendorMapping;
    }

    public UserVendorMappingProjectMapping user(UserVendorMapping userVendorMapping) {
        this.setUserVendorMapping(userVendorMapping);
        return this;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserVendorMappingProjectMapping)) {
            return false;
        }
        return id != null && id.equals(((UserVendorMappingProjectMapping) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserVendorMappingProjectMapping{user_vendor_mapping_id="+id+",project_id="+this.project.getId()+"}";
    }
}
