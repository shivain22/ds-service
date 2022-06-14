package com.ainnotate.aidas.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.envers.Audited;

/**
 * A AidasProperties.
 */
@Entity
@Table(name = "property")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "aidasproperty")
@Audited
@FilterDef(name="statusFilter", parameters=@ParamDef(name="status",type ="java.lang.Integer"))
public class Property extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @NotNull
    @Column(name = "value", length = 100, nullable = false)
    private String value;

    @NotNull
    @Column(name = "system_property", nullable = false)
    private Integer systemProperty;

    @NotNull
    @Column(name="property_type", nullable = false)
    private Integer propertyType;

    @NotNull
    @Column(name = "optional", nullable = false)
    private Integer optional;

    @Column(name = "description")
    private String description;

    @Column(name = "default_prop")
    private Integer defaultProp;

    @ManyToOne
    private User user;

    @Column(name="add_to_metadata")
    private Integer addToMetadata;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getAddToMetadata() {
        return addToMetadata;
    }

    public void setAddToMetadata(Integer addToMetadata) {
        this.addToMetadata = addToMetadata;
    }

    public Integer getDefaultProp() {
        return defaultProp;
    }

    public void setDefaultProp(Integer defaultProp) {
        this.defaultProp = defaultProp;
    }

    public Integer getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Integer propertyType) {
        this.propertyType = propertyType;
    }
// jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Property id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Property name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public Property value(String value) {
        this.setValue(value);
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }



    public Property systemProperty(Integer systemProperty) {
        this.setSystemProperty(systemProperty);
        return this;
    }

    public Integer getSystemProperty() {
        return systemProperty;
    }

    public void setSystemProperty(Integer systemProperty) {
        this.systemProperty = systemProperty;
    }

    public Property optional(Integer optional) {
        this.setOptional(optional);
        return this;
    }

    public Integer getOptional() {
        return optional;
    }

    public void setOptional(Integer optional) {
        this.optional = optional;
    }

    public String getDescription() {
        return this.description;
    }

    public Property description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Property)) {
            return false;
        }
        return id != null && id.equals(((Property) o).id);
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
