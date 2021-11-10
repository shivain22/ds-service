package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AidasUpload.
 */
@Entity
@Table(name = "aidas_upload")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "aidasupload")
public class AidasUpload implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 500)
    @Column(name = "name", length = 500, nullable = false)
    private String name;

    @Column(name = "date_uploaded")
    private ZonedDateTime dateUploaded;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "aidasOrganisation", "aidasCustomer", "aidasVendor" }, allowSetters = true)
    private AidasUser aidasUser;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "aidasProject" }, allowSetters = true)
    private AidasObject aidasObject;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AidasUpload id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public AidasUpload name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZonedDateTime getDateUploaded() {
        return this.dateUploaded;
    }

    public AidasUpload dateUploaded(ZonedDateTime dateUploaded) {
        this.setDateUploaded(dateUploaded);
        return this;
    }

    public void setDateUploaded(ZonedDateTime dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public AidasUser getAidasUser() {
        return this.aidasUser;
    }

    public void setAidasUser(AidasUser aidasUser) {
        this.aidasUser = aidasUser;
    }

    public AidasUpload aidasUser(AidasUser aidasUser) {
        this.setAidasUser(aidasUser);
        return this;
    }

    public AidasObject getAidasObject() {
        return this.aidasObject;
    }

    public void setAidasObject(AidasObject aidasObject) {
        this.aidasObject = aidasObject;
    }

    public AidasUpload aidasObject(AidasObject aidasObject) {
        this.setAidasObject(aidasObject);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AidasUpload)) {
            return false;
        }
        return id != null && id.equals(((AidasUpload) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AidasUpload{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", dateUploaded='" + getDateUploaded() + "'" +
            "}";
    }
}
