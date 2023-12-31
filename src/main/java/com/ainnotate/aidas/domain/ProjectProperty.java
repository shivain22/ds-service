package com.ainnotate.aidas.domain;

import com.ainnotate.aidas.dto.ProjectPropertyDTO;
import com.ainnotate.aidas.dto.UploadMetadataDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;


@NamedNativeQuery(name = "ProjectProperty.getAllUploadMetaDataForProjectProperty",
query=" select umd.project_property_id as projectPropertyId,p.name as name,pp.optional as optional,umd.value as value " +
    "from " +
    "upload_meta_data umd, " +
    "upload u, " +
    "user_vendor_mapping_object_mapping uvmom, " +
    "object o," +
    "project_property pp," +
    "property p," +
    "project pr " +
    " where " +
    "umd.upload_id=u.id and " +
    "u.user_vendor_mapping_object_mapping_id=uvmom.id and " +
    "uvmom.object_id=o.id and " +
    "umd.project_property_id=pp.id and" +
    " pp.add_to_metadata=1 and" +
    " o.project_id=p.id  and o.project_id=pr.id and" +
    " pp.property_id=p.id and u.id=?1 order by pp.id",resultSetMapping = "Mapping.ProjectPropertyDTO")

@SqlResultSetMapping(name = "Mapping.ProjectPropertyDTO",
classes = @ConstructorResult(targetClass = ProjectPropertyDTO.class,
    columns = {
        @ColumnResult(name = "projectPropertyId",type = Long.class),
        @ColumnResult(name = "name",type = String.class),
        @ColumnResult(name = "optional",type = Integer.class),
        @ColumnResult(name = "value",type = String.class)
    }))


/**
 * A AidasProjectProperty.
 */
@Entity
@Table(name = "project_property",indexes = {
    @Index(name="idx_project_property_category",columnList = "category_id"),
    @Index(name="idx_project_property_object",columnList = "project_id"),
    @Index(name="idx_project_property_property",columnList = "property_id")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_project_property_object_property",columnNames={"property_id", "project_id"})
    })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "projectproperty")
@Audited
public class ProjectProperty extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "value")
    private String value;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "customer","projectProperties" }, allowSetters = true)
    @JoinColumn(name = "project_id", nullable = false, foreignKey = @ForeignKey(name="fk_project_property_object"))
    private Project project;

    @Column
    private Integer status;

    @Column
    private Integer optional;

    @Column(name = "default_prop")
    private Integer defaultProp;
    @Column(name="passed_from_app",columnDefinition = "integer default 0")
    private Integer passedFromApp=0;
    @Column(name="add_to_metadata")
    private Integer addToMetadata;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true, foreignKey = @ForeignKey(name="fk_project_property_category"))
    private Category category;

    public Integer getProjectPropertyType() {
        return projectPropertyType;
    }

    public void setProjectPropertyType(Integer projectPropertyType) {
        this.projectPropertyType = projectPropertyType;
    }

    @Column(name="project_property_type")
    private Integer projectPropertyType;

    public Integer getShowToVendorUser() {
        return showToVendorUser;
    }

    public void setShowToVendorUser(Integer showToVendorUser) {
        this.showToVendorUser = showToVendorUser;
    }

    @Column(name="show_to_vendor_user")
    private Integer showToVendorUser;


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
    private Property property;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProjectProperty id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }











    public String getValue() {
        return this.value;
    }

    public ProjectProperty value(String value) {
        this.setValue(value);
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public ProjectProperty project(Project project) {
        this.setProject(project);
        return this;
    }

    public Property getProperty() {
        return this.property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public ProjectProperty aidasProperties(Property property) {
        this.setProperty(property);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectProperty)) {
            return false;
        }
        return id != null && id.equals(((ProjectProperty) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ProjectProperty{id=" + getId() +", property_name='" + this.property.getName() + "'" +", description='" + this.property.getDescription() + "'" +", value='" + getValue() + "'" +"}";
    }
}
