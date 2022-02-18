package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AidasObject.
 */
@Entity
@Table(name = "aidas_object")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "aidasobject")
public class AidasObject implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 500)
    @Column(name = "name", length = 500, nullable = false)
    private String name;

    @Column(name="buffer_percent")
    private Integer bufferPercent;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "number_of_upload_reqd", nullable = false)
    private Integer numberOfUploadReqd;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "aidasCustomer" }, allowSetters = true)
    private AidasProject aidasProject;

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

    @OneToMany(mappedBy = "aidasObject",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "aidasObject" }, allowSetters = true)
    private Set<AidasObjectProperty> aidasObjectProperties = new HashSet<>();

    public Integer getBufferPercent() {
        return bufferPercent;
    }

    public void setBufferPercent(Integer bufferPercent) {
        this.bufferPercent = bufferPercent;
    }

    public Set<AidasObjectProperty> getAidasObjectProperties() {
        return aidasObjectProperties;
    }

    public void setAidasObjectProperties(Set<AidasObjectProperty> aidasObjectProperties) {
        this.aidasObjectProperties = aidasObjectProperties;
    }

    public void addAidasObjectProperty(AidasObjectProperty aidasObjectProperty){
        this.aidasObjectProperties.add(aidasObjectProperty);
    }

    public void removeAidasObjectProperty(AidasObjectProperty aidasObjectProperty){
        this.aidasObjectProperties.remove(aidasObjectProperty);
    }
// jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AidasObject id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public AidasObject name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public AidasObject description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNumberOfUploadReqd() {
        return this.numberOfUploadReqd;
    }

    public AidasObject numberOfUploadReqd(Integer numberOfUploadReqd) {
        this.setNumberOfUploadReqd(numberOfUploadReqd);
        return this;
    }

    public void setNumberOfUploadReqd(Integer numberOfUploadReqd) {
        this.numberOfUploadReqd = numberOfUploadReqd;
    }

    public AidasProject getAidasProject() {
        return this.aidasProject;
    }

    public void setAidasProject(AidasProject aidasProject) {
        this.aidasProject = aidasProject;
    }

    public AidasObject aidasProject(AidasProject aidasProject) {
        this.setAidasProject(aidasProject);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AidasObject)) {
            return false;
        }
        return id != null && id.equals(((AidasObject) o).id);
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
