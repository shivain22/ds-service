package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AidasProject.
 */
@Entity
@Table(name = "aidas_project")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "aidasproject")
public class AidasProject implements Serializable {

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

    @Column(name = "project_type")
    private String projectType;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "aidasOrganisation" }, allowSetters = true)
    private AidasCustomer aidasCustomer;


    @OneToMany(mappedBy = "aidasProject",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "aidasProject" }, allowSetters = true)
    private Set<AidasProjectProperty> aidasProjectProperties=new HashSet<>();


    public Set<AidasProjectProperty> getAidasProjectProperties() {
        return aidasProjectProperties;
    }

    public void addAidasProjectProperty(AidasProjectProperty aidasProjectProperty){
        this.aidasProjectProperties.add(aidasProjectProperty);
    }
    public void removeAidasProjectProperty(AidasProjectProperty aidasProjectProperty){
        this.aidasProjectProperties.remove(aidasProjectProperty);
    }
    public void setAidasProjectProperties(Set<AidasProjectProperty> aidasProjectProperties) {
        this.aidasProjectProperties = aidasProjectProperties;
    }
// jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AidasProject id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public AidasProject name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public AidasProject description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProjectType() {
        return this.projectType;
    }

    public AidasProject projectType(String projectType) {
        this.setProjectType(projectType);
        return this;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public AidasCustomer getAidasCustomer() {
        return this.aidasCustomer;
    }

    public void setAidasCustomer(AidasCustomer aidasCustomer) {
        this.aidasCustomer = aidasCustomer;
    }

    public AidasProject aidasCustomer(AidasCustomer aidasCustomer) {
        this.setAidasCustomer(aidasCustomer);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AidasProject)) {
            return false;
        }
        return id != null && id.equals(((AidasProject) o).id);
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
