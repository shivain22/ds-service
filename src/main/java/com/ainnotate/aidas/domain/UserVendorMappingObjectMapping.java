package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

/**
 * A AidasUserAidasObjectMapping.
 */
@Entity
@Table(name = "user_vendor_mapping_object_mapping",indexes = {
    @Index(name="idx_uvmom_object",columnList = "object_id"),
    @Index(name="idx_uvmom_uvm",columnList = "user_vendor_mapping_id")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_uvmid_oid",columnNames={"user_vendor_mapping_id", "object_id"})
    })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "uservendorobjectmapping")
@Audited
public class UserVendorMappingObjectMapping extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date_assigned")
    private ZonedDateTime dateAssigned;

    @ManyToOne(optional = false,fetch = FetchType.EAGER)
    @NotNull
    @JsonIgnoreProperties(value = { "organisation", "customer", "vendor" }, allowSetters = true)
    @JoinColumn(name = "user_vendor_mapping_id", nullable = true, foreignKey = @ForeignKey(name="fk_uvmom_uvm"))
    private UserVendorMapping userVendorMapping;

    @ManyToOne(optional = false,fetch = FetchType.EAGER)
    @NotNull
    @JsonIgnoreProperties(value = { "project" }, allowSetters = true)
    @JoinColumn(name = "object_id", nullable = true, foreignKey = @ForeignKey(name="fk_uvmom_object"))
    private Object object;

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

    public UserVendorMappingObjectMapping id(Long id) {
        this.setId(id);
        return this;
    }

    public ZonedDateTime getDateAssigned() {
        return this.dateAssigned;
    }

    public void setDateAssigned(ZonedDateTime dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public UserVendorMappingObjectMapping dateAssigned(ZonedDateTime dateAssigned) {
        this.setDateAssigned(dateAssigned);
        return this;
    }

    public UserVendorMappingObjectMapping status(Integer status) {
        this.setStatus(status);
        return this;
    }


    public UserVendorMapping getUserVendorMapping() {
        return userVendorMapping;
    }

    public void setUserVendorMapping(UserVendorMapping userVendorMapping) {
        this.userVendorMapping = userVendorMapping;
    }

    public UserVendorMappingObjectMapping user(UserVendorMapping userVendorMapping) {
        this.setUserVendorMapping(userVendorMapping);
        return this;
    }

    public Object getObject() {
        return this.object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public UserVendorMappingObjectMapping object(Object object) {
        this.setObject(object);
        return this;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserVendorMappingObjectMapping)) {
            return false;
        }
        return id != null && id.equals(((UserVendorMappingObjectMapping) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserVendorMappingObjectMapping{user_vendor_mapping_id="+id+",object_id="+this.object.getId()+"}";
    }
}
