package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A AidasProject.
 */
@Entity
@Table(name = "project")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "aidasproject")
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
    private Customer customer;
    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "project" }, allowSetters = true)
    @Field(type=FieldType.Nested,store = false,storeNullValue = false)
    private Set<ProjectProperty> projectProperties=new HashSet<>();
    @Column
    private Integer numOfUploadsReqd;
    @Column(name="buffer_percent")
    private Integer bufferPercent;

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
