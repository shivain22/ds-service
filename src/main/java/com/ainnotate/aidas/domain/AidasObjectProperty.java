package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AidasObjectProperty.
 */
@Entity
@Table(name = "aidas_object_property")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "aidasobjectproperty")
public class AidasObjectProperty implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "value")
    private String value;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "aidasProject" }, allowSetters = true)
    private AidasObject aidasObject;

    @ManyToOne(optional = false)
    @NotNull
    private AidasProperties aidasProperties;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AidasObjectProperty id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public String getValue() {
        return this.value;
    }

    public AidasObjectProperty value(String value) {
        this.setValue(value);
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public AidasObject getAidasObject() {
        return this.aidasObject;
    }

    public void setAidasObject(AidasObject aidasObject) {
        this.aidasObject = aidasObject;
    }

    public AidasObjectProperty aidasObject(AidasObject aidasObject) {
        this.setAidasObject(aidasObject);
        return this;
    }

    public AidasProperties getAidasProperties() {
        return this.aidasProperties;
    }

    public void setAidasProperties(AidasProperties aidasProperties) {
        this.aidasProperties = aidasProperties;
    }

    public AidasObjectProperty aidasProperties(AidasProperties aidasProperties) {
        this.setAidasProperties(aidasProperties);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AidasObjectProperty)) {
            return false;
        }
        return id != null && id.equals(((AidasObjectProperty) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AidasObjectProperty{" +
            "id=" + getId() +
            ", name='" + this.aidasProperties.getName() + "'" +
            ", description='" + this.aidasProperties.getDescription() + "'" +
            ", value='" + getValue() + "'" +
            "}";
    }
}
