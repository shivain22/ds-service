package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;

/**
 * A AidasUpload.
 */
@Entity
@Table(name = "upload")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "aidasupload")
@Audited
public class Upload extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @Column(name = "name", length = 500, nullable = true)
    private String name;

    @Column(name = "upload_url",  nullable = true)
    private String uploadUrl;

    @Column(name = "upload_etag",  nullable = true)
    private String uploadEtag;

    @Column(name = "date_uploaded")
    private Instant dateUploaded;

    @ManyToOne(optional = true)
    @JsonIgnoreProperties(value = { "object" }, allowSetters = true)
    private Upload reworkUpload;

    public Upload getReworkAidasUpload() {
        return reworkUpload;
    }

    public void setReworkAidasUpload(Upload reworkUpload) {
        this.reworkUpload = reworkUpload;
    }

    @Column(name = "status_modified_date")
    private ZonedDateTime statusModifiedDate;

    @OneToMany(mappedBy = "upload",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "upload" }, allowSetters = true)
    private Set<UploadRejectMapping> uploadRejectMappings = new HashSet<>();

    public Set<UploadRejectMapping> getAidasUploadRejectMappings() {
        return uploadRejectMappings;
    }

    public void setAidasUploadRejectMappings(Set<UploadRejectMapping> uploadRejectMappings) {
        this.uploadRejectMappings = uploadRejectMappings;
    }

    @Column(name = "object_key",  nullable = true)
    private String objectKey;

    @OneToMany(mappedBy = "upload",fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "upload","project","object" }, allowSetters = true)
    private Set<UploadMetaData> uploadMetaDataSet = new HashSet<>();

    @Column(name="status", nullable=false)
    private Integer status;

    @Column(name="approval_status",nullable = true)
    private Integer approvalStatus;

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    @Column(name="qc_status", nullable=true)
    private Integer qcStatus;

    @Column(name="metadata_status", nullable=true)
    private Integer metadataStatus;

    @Column(name="qc_done_by", nullable=true)
    private Long qcDoneBy;

    @Column(name="qc_start_date", nullable=true)
    private Instant qcStartDate;

    @Column(name="qc_end_date", nullable=true)
    private Instant qcEndDate;
    @Column(name="external_dataset_status")
    @JsonProperty
    private Integer externalDatasetStatus;

    public Integer getExternalDatasetStatus() {
        return externalDatasetStatus;
    }

    public void setExternalDatasetStatus(Integer externalDatasetStatus) {
        this.externalDatasetStatus = externalDatasetStatus;
    }
    @Transient
    private Integer reworkCount;

    public Upload getReworkUpload() {
        return reworkUpload;
    }

    public void setReworkUpload(Upload reworkUpload) {
        this.reworkUpload = reworkUpload;
    }

    public Set<UploadRejectMapping> getUploadRejectMappings() {
        return uploadRejectMappings;
    }

    public void setUploadRejectMappings(Set<UploadRejectMapping> uploadRejectMappings) {
        this.uploadRejectMappings = uploadRejectMappings;
    }

    public Set<UploadMetaData> getUploadMetaDataSet() {
        return uploadMetaDataSet;
    }

    public void setUploadMetaDataSet(Set<UploadMetaData> uploadMetaDataSet) {
        this.uploadMetaDataSet = uploadMetaDataSet;
    }

    public Integer getReworkCount() {
        return reworkCount;
    }

    public void setReworkCount(Integer reworkCount) {
        this.reworkCount = reworkCount;
    }

    public UserVendorMappingObjectMapping getUserVendorMappingObjectMapping() {
        return userVendorMappingObjectMapping;
    }

    public void setUserVendorMappingObjectMapping(UserVendorMappingObjectMapping userVendorMappingObjectMapping) {
        this.userVendorMappingObjectMapping = userVendorMappingObjectMapping;
    }

    public Set<UploadMetaData> getAidasUploadMetaDataSet() {
        return uploadMetaDataSet;
    }

    public void setAidasUploadMetaDataSet(Set<UploadMetaData> uploadMetaDataSet) {
        this.uploadMetaDataSet = uploadMetaDataSet;
    }

    public Integer getMetadataStatus() {
        return metadataStatus;
    }

    public void setMetadataStatus(Integer metadataStatus) {
        this.metadataStatus = metadataStatus;
    }

    @Override
    public Integer getStatus() {
        return status;
    }

    @Override
    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getQcStatus() {
        return qcStatus;
    }

    public void setQcStatus(Integer qcStatus) {
        this.qcStatus = qcStatus;
    }

    public Long getQcDoneBy() {
        return qcDoneBy;
    }

    public void setQcDoneBy(Long qcDoneBy) {
        this.qcDoneBy = qcDoneBy;
    }

    public Instant getQcStartDate() {
        return qcStartDate;
    }

    public void setQcStartDate(Instant qcStartDate) {
        this.qcStartDate = qcStartDate;
    }

    public Instant getQcEndDate() {
        return qcEndDate;
    }

    public void setQcEndDate(Instant qcEndDate) {
        this.qcEndDate = qcEndDate;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    @ManyToOne(optional = false,fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "user", "object", "aidasUploads" }, allowSetters = true)
    private UserVendorMappingObjectMapping userVendorMappingObjectMapping;



    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getUploadEtag() {
        return uploadEtag;
    }

    public void setUploadEtag(String uploadEtag) {
        this.uploadEtag = uploadEtag;
    }


// jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Upload id(Long id) {
        this.setId(id);
        return this;
    }
    public Upload status(Integer status) {
        this.setStatus(status);
        return this;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Upload name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getDateUploaded() {
        return this.dateUploaded;
    }

    public Upload dateUploaded(Instant dateUploaded) {
        this.setDateUploaded(dateUploaded);
        return this;
    }

    public void setDateUploaded(Instant dateUploaded) {
        this.dateUploaded = dateUploaded;
    }



    public ZonedDateTime getStatusModifiedDate() {
        return this.statusModifiedDate;
    }

    public Upload statusModifiedDate(ZonedDateTime statusModifiedDate) {
        this.setStatusModifiedDate(statusModifiedDate);
        return this;
    }

    public void setStatusModifiedDate(ZonedDateTime statusModifiedDate) {
        this.statusModifiedDate = statusModifiedDate;
    }



    public UserVendorMappingObjectMapping getAidasUserAidasObjectMapping() {
        return this.userVendorMappingObjectMapping;
    }

    public void setAidasUserAidasObjectMapping(UserVendorMappingObjectMapping userVendorMappingObjectMapping) {
        this.userVendorMappingObjectMapping = userVendorMappingObjectMapping;
    }

    public Upload aidasUserAidasObjectMapping(UserVendorMappingObjectMapping userVendorMappingObjectMapping) {
        this.setAidasUserAidasObjectMapping(userVendorMappingObjectMapping);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Upload)) {
            return false;
        }
        return id != null && id.equals(((Upload) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AidasUpload{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", dateUploaded='" + getDateUploaded() + "'" +
            ", status='" + getStatus() + "'" +
            ", statusModifiedDate='" + getStatusModifiedDate() + "'" +
            "}";
    }
}
