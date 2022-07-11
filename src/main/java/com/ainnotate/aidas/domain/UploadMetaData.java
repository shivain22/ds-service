package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

/**
 * A AidasUploadMetaData.
 */
@Entity
@Table(name = "upload_meta_data",indexes = {
    @Index(name="idx_umd_opid",columnList = "object_property_id"),
    @Index(name="idx_umd_ppid",columnList = "project_property_id"),
    @Index(name="idx_umd_upid",columnList = "upload_id")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_umd_opid_ppid_upid",columnNames={"object_property_id", "project_property_id","upload_id"})
    })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "uploadmetadata")
@Audited
public class UploadMetaData extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private String value;

    @ManyToOne
    @JsonIgnoreProperties(value = { "aidasUserAidasObjectMapping" }, allowSetters = true)
    @JoinColumn(name = "upload_id", nullable = true, foreignKey = @ForeignKey(name="fk_umd_upload"))
    private Upload upload;

    @ManyToOne
    @JsonIgnoreProperties(value = { "aidasUserAidasObjectMapping" }, allowSetters = true)
    @JoinColumn(name = "project_property_id", nullable = true, foreignKey = @ForeignKey(name="fk_umd_project_property"))
    private ProjectProperty projectProperty;

    @ManyToOne
    @JsonIgnoreProperties(value = { "aidasUserAidasObjectMapping" }, allowSetters = true)
    @JoinColumn(name = "object_property_id", nullable = true, foreignKey = @ForeignKey(name="fk_umd_object_property_id"))
    private ObjectProperty objectProperty;

    public ObjectProperty getObjectProperty() {
        return objectProperty;
    }

    public void setObjectProperty(ObjectProperty objectProperty) {
        this.objectProperty = objectProperty;
    }

    public ProjectProperty getProjectProperty() {
        return projectProperty;
    }

    public void setProjectProperty(ProjectProperty projectProperty) {
        this.projectProperty = projectProperty;
    }
// jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UploadMetaData id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public UploadMetaData name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public UploadMetaData value(String value) {
        this.setValue(value);
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JsonIgnore
    public Upload getUpload() {
        return this.upload;
    }

    public void setUpload(Upload upload) {
        this.upload = upload;
    }

    public UploadMetaData upload(Upload upload) {
        this.setUpload(upload);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UploadMetaData)) {
            return false;
        }
        return id != null && id.equals(((UploadMetaData) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        if(this.projectProperty!=null && this.projectProperty.getProperty()!=null){
            return "UploadMetaData{id="+id+",upload_id="+this.upload.getId()+",project_property_id="+this.projectProperty.getId()+",property_id="+this.projectProperty.getProperty().getId()+"}";
        }
        if(this.objectProperty!=null && this.objectProperty.getProperty()!=null){
            return "UploadMetaData{id="+id+",upload_id="+this.upload.getId()+",object_property_id"+this.objectProperty.getId()+",property_id="+this.objectProperty.getProperty().getId()+"}";
        }else{
            return "UploadMetaData{id="+id+",upload_id="+this.upload.getId()+"}";
        }
    }
}
