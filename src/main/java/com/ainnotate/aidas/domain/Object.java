package com.ainnotate.aidas.domain;

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
@Entity
@Table(name = "object")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "aidasobject")
@Audited
public class Object extends AbstractAuditingEntity  implements Serializable {

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
    @JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
    private Project project;

    @Column(name="is_dummy")
    private Boolean dummy;

    public Boolean getDummy() {
        return dummy;
    }

    public Boolean isDummy() {
        return dummy;
    }

    public void setDummy(Boolean dummy) {
        this.dummy = dummy;
    }

    @ManyToOne(optional = true)
    @JsonIgnoreProperties(value = { "project","customer" }, allowSetters = true)
    private Object parentObject;

    public Object getParentAidasObject() {
        return parentObject;
    }

    public void setParentAidasObject(Object parentObject) {
        this.parentObject = parentObject;
    }

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

    @OneToMany(mappedBy = "object",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @Filter(name = "objectPropertyStatusFilter",condition="status = 1")
    @JsonIgnoreProperties(value = { "object" }, allowSetters = true)
    private Set<ObjectProperty> objectProperties = new HashSet<>();

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

    public Object id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Object name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Object description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNumberOfUploadReqd() {
        return this.numberOfUploadReqd;
    }

    public Object numberOfUploadReqd(Integer numberOfUploadReqd) {
        this.setNumberOfUploadReqd(numberOfUploadReqd);
        return this;
    }

    public void setNumberOfUploadReqd(Integer numberOfUploadReqd) {
        this.numberOfUploadReqd = numberOfUploadReqd;
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

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

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
