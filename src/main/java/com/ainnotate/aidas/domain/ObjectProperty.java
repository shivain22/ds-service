package com.ainnotate.aidas.domain;

import com.ainnotate.aidas.dto.ObjectPropertyDTO;
import com.ainnotate.aidas.dto.ProjectPropertyDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.validation.constraints.*;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.hibernate.envers.Audited;

@NamedNativeQuery(name = "ObjectProperty.getAllUploadMetaDataForObjectProperty",
query=" select umd.project_property_id as projectPropertyId,p.name as name,op.optional as optional,umd.value as value " +
    "from " +
    "upload_meta_data umd, " +
    "upload u, " +
    "user_vendor_mapping_object_mapping uvmom, " +
    "object o," +
    "object_property op," +
    "property p," +
    "project pr " +
    " where " +
    "umd.upload_id=u.id and " +
    "u.user_vendor_mapping_object_mapping_id=uvmom.id and " +
    "uvmom.object_id=o.id and " +
    "umd.project_property_id=op.id and" +
    " op.add_to_metadata=1 and" +
    " o.project_id=p.id  and o.project_id=pr.id and" +
    " op.property_id=p.id and u.id=?1 order by op.id",resultSetMapping = "Mapping.ObjectPropertyDTO")

@SqlResultSetMapping(name = "Mapping.ObjectPropertyDTO",
classes = @ConstructorResult(targetClass = ObjectPropertyDTO.class,
    columns = {
        @ColumnResult(name = "projectPropertyId",type = Long.class),
        @ColumnResult(name = "name",type = String.class),
        @ColumnResult(name = "optional",type = Integer.class),
        @ColumnResult(name = "value",type = String.class)
    }))
/**
 * A AidasObjectProperty.
 */
@Entity
@Table(name = "object_property",indexes = {
    @Index(name="idx_object_property_category",columnList = "category_id"),
    @Index(name="idx_object_property_object",columnList = "object_id"),
    @Index(name="idx_object_property_property",columnList = "property_id")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_object_property_object_property",columnNames={"property_id", "object_id"})
    })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "objectproperty")
@Audited
@FilterDef(name="objectPropertyStatusFilter", parameters=@ParamDef(name="status",type ="java.lang.Integer"))
public class ObjectProperty extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "value")
    private String value;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "project","objectProperties","customer" }, allowSetters = true)
    @JsonIgnore
    @JoinColumn(name = "object_id", nullable = false, foreignKey = @ForeignKey(name="fk_object_property_object"))
    private Object object;

    @ManyToOne(optional = false)
    @NotNull
    @JoinColumn(name = "property_id", nullable = false, foreignKey = @ForeignKey(name="fk_object_property_property"))
    private Property property;

    @Column
    private Integer optional;

    @Column(name = "default_prop")
    private Integer defaultProp;
    @Column(name="passed_from_app",columnDefinition = "integer default 0")
    private Integer passedFromApp;
    @Column(name="add_to_metadata")
    private Integer addToMetadata;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true, foreignKey = @ForeignKey(name="fk_object_property_category"))
    private Category category;

    @Column(name="show_to_vendor_user")
    private Integer showToVendorUser;

    public Integer getShowToVendorUser() {
        return showToVendorUser;
    }
    @Column(name="object_property_type")
    private Integer objectPropertyType;

    public Integer getObjectPropertyType() {
        return objectPropertyType;
    }

    public void setObjectPropertyType(Integer objectPropertyType) {
        this.objectPropertyType = objectPropertyType;
    }

    public void setShowToVendorUser(Integer showToVendorUser) {
        this.showToVendorUser = showToVendorUser;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Integer getDefaultProp() {
        return defaultProp;
    }

    public void setDefaultProp(Integer defaultProp) {
        this.defaultProp = defaultProp;
    }

    public Integer getPassedFromApp() {
        return passedFromApp;
    }

    public void setPassedFromApp(Integer passedFromApp) {
        this.passedFromApp = passedFromApp;
    }

    public Integer getAddToMetadata() {
        return addToMetadata;
    }

    public void setAddToMetadata(Integer addToMetadata) {
        this.addToMetadata = addToMetadata;
    }

    public Integer getOptional() {
        return optional;
    }

    public void setOptional(Integer optional) {
        this.optional = optional;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ObjectProperty id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public String getValue() {
        return this.value;
    }

    public ObjectProperty value(String value) {
        this.setValue(value);
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Object getObject() {
        return this.object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public ObjectProperty Object(Object object) {
        this.setObject(object);
        return this;
    }

    public Property getProperty() {
        return this.property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public ObjectProperty property(Property property) {
        this.setProperty(property);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ObjectProperty)) {
            return false;
        }
        return id != null && id.equals(((ObjectProperty) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ObjectProperty{id=" + getId() +", property_name='" + this.property.getName() + "'" +", description='" + this.property.getDescription() + "'" +", value='" + getValue() + "'" +"}";
    }
}
