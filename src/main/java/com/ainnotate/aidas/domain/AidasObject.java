package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
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

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "number_of_upload_reqd", nullable = false)
    private Integer numberOfUploadReqd;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "aidasCustomer" }, allowSetters = true)
    private AidasProject aidasProject;

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
