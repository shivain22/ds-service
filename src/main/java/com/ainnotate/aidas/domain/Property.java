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
@Table(name = "property",indexes = {
    @Index(name="idx_property_category",columnList = "category_id"),
    @Index(name="idx_property_user",columnList = "user_id"),
    @Index(name="idx_property_customer",columnList = "customer_id")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_property_cid_uid",columnNames={"name", "customer_id","user_id"})
    })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "property")
@Audited
@FilterDef(name="statusFilter", parameters=@ParamDef(name="status",type ="java.lang.Integer"))
public class Property extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", length = 100, nullable = true, unique = false)
    private String name;

    @NotNull
    @Column(name = "value", length = 100, nullable = true)
    private String value;

    @NotNull
    @Column(name="property_type", nullable = true)
    private Integer propertyType;

    @NotNull
    @Column(name = "optional", nullable = true)
    private Integer optional;

    @Column(name = "description")
    private String description;

    @Column(name = "default_prop")
    private Integer defaultProp;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true, foreignKey = @ForeignKey(name="fk_property_user"))
    private User user;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = true, foreignKey = @ForeignKey(name="fk_property_customer"))
    private Customer customer;

    @Column(name="passed_from_app",columnDefinition = "integer default 0")
    private Integer passedFromApp;
    @Column(name="add_to_metadata")
    private Integer addToMetadata;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true, foreignKey = @ForeignKey(name="fk_property_category"))
    private Category category;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Integer getPassedFromApp() {
        return passedFromApp;
    }

    public void setPassedFromApp(Integer passedFromApp) {
        this.passedFromApp = passedFromApp;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

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

    public void setId(Long id) {
        this.id = id;
    }

    public Property id(Long id) {
        this.setId(id);
        return this;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Property name(String name) {
        this.setName(name);
        return this;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Property value(String value) {
        this.setValue(value);
        return this;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public Property description(String description) {
        this.setDescription(description);
        return this;
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
        return "Property{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", value='" + getValue() + "'" +
            ", optional='" + getOptional() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
