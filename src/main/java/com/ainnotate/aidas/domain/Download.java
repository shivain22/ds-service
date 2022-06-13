package com.ainnotate.aidas.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A AidasUpload.
 */
@Entity
@Table(name = "download")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "aidasdownload")
@Audited
public class Download extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "upload_url",  nullable = false)
    private String uploadUrl;

    @Column(name = "upload_etag",  nullable = false)
    private String uploadEtag;

    @Column(name = "date_uploaded")
    private Instant dateUploaded;

    @Column(name = "bucket_name", length = 150, nullable = false)
    private String bucketName;

    @Column(name = "aws_key", length = 150, nullable = false)
    private String awsKey;

    @Column(name = "aws_secret", length = 150, nullable = false)
    private String awsSecret;

    @Column(name = "region", length = 150, nullable = false)
    private String region;

    @Column(name = "object_key", length = 150, nullable = false)
    private String objectKey;

    @Column(name = "upload_object_ids", length = 3500, nullable = false)
    private String uploadedObjectIds;

    public String getUploadedObjectIds() {
        return uploadedObjectIds;
    }

    public void setUploadedObjectIds(String uploadedObjectIds) {
        this.uploadedObjectIds = uploadedObjectIds;
    }

    @ManyToOne
    private Object object;

    @ManyToOne
    private Project project;

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

    public Download id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Download name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getDateUploaded() {
        return this.dateUploaded;
    }

    public Download dateUploaded(Instant dateUploaded) {
        this.setDateUploaded(dateUploaded);
        return this;
    }

    public void setDateUploaded(Instant dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getAwsKey() {
        return awsKey;
    }

    public void setAwsKey(String awsKey) {
        this.awsKey = awsKey;
    }

    public String getAwsSecret() {
        return awsSecret;
    }

    public void setAwsSecret(String awsSecret) {
        this.awsSecret = awsSecret;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

// jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Download)) {
            return false;
        }
        return id != null && id.equals(((Download) o).id);
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
            "}";
    }
}
