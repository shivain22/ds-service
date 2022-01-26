package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AidasUpload.
 */
@Entity
@Table(name = "aidas_upload")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "aidasupload")
public class AidasUpload implements Serializable {

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

    @Column(name = "status")
    private Boolean status;

    @Column(name = "status_modified_date")
    private ZonedDateTime statusModifiedDate;

    @Column(name = "reject_reason")
    private String rejectReason;

    @Column(name = "object_key",  nullable = false)
    private String objectKey;

    @Column(name = "upload_object_meta",  nullable = false)
    private String uploadObjectMeta;

    public String getUploadObjectMeta() {
        return uploadObjectMeta;
    }

    public void setUploadObjectMeta(String uploadObjectMeta) {
        this.uploadObjectMeta = uploadObjectMeta;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    @ManyToOne(optional = false)
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

    public Boolean getStatus() {
        return this.status;
    }

    public AidasUpload status(Boolean status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Boolean status) {
        this.status = status;
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

    public String getRejectReason() {
        return this.rejectReason;
    }

    public AidasUpload rejectReason(String rejectReason) {
        this.setRejectReason(rejectReason);
        return this;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
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
            ", rejectReason='" + getRejectReason() + "'" +
            "}";
    }
}
