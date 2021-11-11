package com.ainnotate.aidas.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AidasProperties.
 */
@Entity
@Table(name = "aidas_properties")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "aidasproperties")
public class AidasProperties implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 100)
    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @NotNull
    @Size(min = 3, max = 100)
    @Column(name = "value", length = 100, nullable = false, unique = true)
    private String value;

    @NotNull
    @Column(name = "system_property", nullable = false)
    private Boolean systemProperty;

    @NotNull
    @Column(name = "optional", nullable = false)
    private Boolean optional;

    @Column(name = "description")
    private String description;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AidasProperties id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public AidasProperties name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public AidasProperties value(String value) {
        this.setValue(value);
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getSystemProperty() {
        return this.systemProperty;
    }

    public AidasProperties systemProperty(Boolean systemProperty) {
        this.setSystemProperty(systemProperty);
        return this;
    }

    public void setSystemProperty(Boolean systemProperty) {
        this.systemProperty = systemProperty;
    }

    public Boolean getOptional() {
        return this.optional;
    }

    public AidasProperties optional(Boolean optional) {
        this.setOptional(optional);
        return this;
    }

    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

    public String getDescription() {
        return this.description;
    }

    public AidasProperties description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AidasProperties)) {
            return false;
        }
        return id != null && id.equals(((AidasProperties) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AidasProperties{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", value='" + getValue() + "'" +
            ", systemProperty='" + getSystemProperty() + "'" +
            ", optional='" + getOptional() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
