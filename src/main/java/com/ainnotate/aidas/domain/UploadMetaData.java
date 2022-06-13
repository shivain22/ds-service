package com.ainnotate.aidas.domain;

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
@Table(name = "upload_meta_data")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "aidasuploadmetadata")
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
    private Upload upload;

    @ManyToOne
    @JsonIgnoreProperties(value = { "aidasUserAidasObjectMapping" }, allowSetters = true)
    private ProjectProperty projectProperty;

    @ManyToOne
    @JsonIgnoreProperties(value = { "aidasUserAidasObjectMapping" }, allowSetters = true)
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
        return "AidasUploadMetaData{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", value='" + getValue() + "'" +
            "}";
    }
}
