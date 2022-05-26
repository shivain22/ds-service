package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

/**
 * A AidasProjectProperty.
 */
@Entity
@Table(name = "aidas_project_property")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "aidasprojectproperty")
@Audited
public class AidasProjectProperty extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "value")
    private String value;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "aidasCustomer" }, allowSetters = true)
    private AidasProject aidasProject;

    @Column
    private Integer status;

    @Column
    private Integer optional;

    @Override
    public Integer getStatus() {
        return status;
    }

    @Override
    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOptional() {
        return optional;
    }

    public void setOptional(Integer optional) {
        this.optional = optional;
    }

    @ManyToOne
    private AidasProperties aidasProperties;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AidasProjectProperty id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }











    public String getValue() {
        return this.value;
    }

    public AidasProjectProperty value(String value) {
        this.setValue(value);
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public AidasProject getAidasProject() {
        return this.aidasProject;
    }

    public void setAidasProject(AidasProject aidasProject) {
        this.aidasProject = aidasProject;
    }

    public AidasProjectProperty aidasProject(AidasProject aidasProject) {
        this.setAidasProject(aidasProject);
        return this;
    }

    public AidasProperties getAidasProperties() {
        return this.aidasProperties;
    }

    public void setAidasProperties(AidasProperties aidasProperties) {
        this.aidasProperties = aidasProperties;
    }

    public AidasProjectProperty aidasProperties(AidasProperties aidasProperties) {
        this.setAidasProperties(aidasProperties);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AidasProjectProperty)) {
            return false;
        }
        return id != null && id.equals(((AidasProjectProperty) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AidasProjectProperty{" +
            "id=" + getId() +
            ", name='" + this.aidasProperties.getName() + "'" +
            ", description='" + this.aidasProperties.getDescription() + "'" +
            ", value='" + getValue() + "'" +
            "}";
    }
}
