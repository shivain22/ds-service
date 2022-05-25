package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;

/**
 * A AidasUpload.
 */
@Entity
@Table(name = "aidas_upload")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "aidasupload")
@Audited
public class AidasUpload extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @Column(name = "name", length = 500, nullable = false)
    private String name;

    @Column(name = "upload_url",  nullable = false)
    private String uploadUrl;

    @Column(name = "upload_etag",  nullable = false)
    private String uploadEtag;

    @Column(name = "date_uploaded")
    private Instant dateUploaded;



    @Column(name = "status_modified_date")
    private ZonedDateTime statusModifiedDate;

    @OneToMany(mappedBy = "aidasUpload",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "aidasUpload" }, allowSetters = true)
    private Set<AidasUploadRejectMapping> aidasUploadRejectMappings = new HashSet<>();

    public Set<AidasUploadRejectMapping> getAidasUploadRejectMappings() {
        return aidasUploadRejectMappings;
    }

    public void setAidasUploadRejectMappings(Set<AidasUploadRejectMapping> aidasUploadRejectMappings) {
        this.aidasUploadRejectMappings = aidasUploadRejectMappings;
    }

    @Column(name = "object_key",  nullable = false)
    private String objectKey;

    @OneToMany(mappedBy = "aidasUpload",fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "aidasUpload","aidasProject","aidasObject" }, allowSetters = true)
    private Set<AidasUploadMetaData> aidasUploadMetaDataSet = new HashSet<>();

    @Column(name="status", nullable=false)
    private Integer status;

    @Column(name="qc_status", nullable=false)
    private Integer qcStatus;

    @Column(name="metadata_status", nullable=false)
    private Integer metadataStatus;

    @Column(name="qc_done_by", nullable=false)
    private Long qcDoneBy;

    @Column(name="qc_start_date", nullable=false)
    private Instant qcStartDate;

    @Column(name="qc_end_date", nullable=false)
    private Instant qcEndDate;

    public Set<AidasUploadMetaData> getAidasUploadMetaDataSet() {
        return aidasUploadMetaDataSet;
    }

    public void setAidasUploadMetaDataSet(Set<AidasUploadMetaData> aidasUploadMetaDataSet) {
        this.aidasUploadMetaDataSet = aidasUploadMetaDataSet;
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
    @JsonIgnoreProperties(value = { "aidasUser", "aidasObject", "aidasUploads" }, allowSetters = true)
    private AidasUserAidasObjectMapping aidasUserAidasObjectMapping;



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

    public AidasUpload id(Long id) {
        this.setId(id);
        return this;
    }
    public AidasUpload status(Integer status) {
        this.setStatus(status);
        return this;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public AidasUpload name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getDateUploaded() {
        return this.dateUploaded;
    }

    public AidasUpload dateUploaded(Instant dateUploaded) {
        this.setDateUploaded(dateUploaded);
        return this;
    }

    public void setDateUploaded(Instant dateUploaded) {
        this.dateUploaded = dateUploaded;
    }



    public ZonedDateTime getStatusModifiedDate() {
        return this.statusModifiedDate;
    }

    public AidasUpload statusModifiedDate(ZonedDateTime statusModifiedDate) {
        this.setStatusModifiedDate(statusModifiedDate);
        return this;
    }

    public void setStatusModifiedDate(ZonedDateTime statusModifiedDate) {
        this.statusModifiedDate = statusModifiedDate;
    }



    public AidasUserAidasObjectMapping getAidasUserAidasObjectMapping() {
        return this.aidasUserAidasObjectMapping;
    }

    public void setAidasUserAidasObjectMapping(AidasUserAidasObjectMapping aidasUserAidasObjectMapping) {
        this.aidasUserAidasObjectMapping = aidasUserAidasObjectMapping;
    }

    public AidasUpload aidasUserAidasObjectMapping(AidasUserAidasObjectMapping aidasUserAidasObjectMapping) {
        this.setAidasUserAidasObjectMapping(aidasUserAidasObjectMapping);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AidasUpload)) {
            return false;
        }
        return id != null && id.equals(((AidasUpload) o).id);
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
