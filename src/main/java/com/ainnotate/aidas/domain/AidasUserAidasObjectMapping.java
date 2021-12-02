package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AidasUserAidasObjectMapping.
 */
@Entity
@Table(name = "aidas_user_obj_map")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "aidasuseraidasobjectmapping")
public class AidasUserAidasObjectMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date_assigned")
    private ZonedDateTime dateAssigned;

    @Column(name = "status")
    private Boolean status;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "aidasOrganisation", "aidasCustomer", "aidasVendor" }, allowSetters = true)
    private AidasUser aidasUser;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "aidasProject" }, allowSetters = true)
    private AidasObject aidasObject;

    @OneToMany(mappedBy = "aidasUserAidasObjectMapping")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "aidasUserAidasObjectMapping" }, allowSetters = true)
    private Set<AidasUpload> aidasUploads = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AidasUserAidasObjectMapping id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDateAssigned() {
        return this.dateAssigned;
    }

    public AidasUserAidasObjectMapping dateAssigned(ZonedDateTime dateAssigned) {
        this.setDateAssigned(dateAssigned);
        return this;
    }

    public void setDateAssigned(ZonedDateTime dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public AidasUserAidasObjectMapping status(Boolean status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public AidasUser getAidasUser() {
        return this.aidasUser;
    }

    public void setAidasUser(AidasUser aidasUser) {
        this.aidasUser = aidasUser;
    }

    public AidasUserAidasObjectMapping aidasUser(AidasUser aidasUser) {
        this.setAidasUser(aidasUser);
        return this;
    }

    public AidasObject getAidasObject() {
        return this.aidasObject;
    }

    public void setAidasObject(AidasObject aidasObject) {
        this.aidasObject = aidasObject;
    }

    public AidasUserAidasObjectMapping aidasObject(AidasObject aidasObject) {
        this.setAidasObject(aidasObject);
        return this;
    }

    public Set<AidasUpload> getAidasUploads() {
        return this.aidasUploads;
    }

    public void setAidasUploads(Set<AidasUpload> aidasUploads) {
        if (this.aidasUploads != null) {
            this.aidasUploads.forEach(i -> i.setAidasUserAidasObjectMapping(null));
        }
        if (aidasUploads != null) {
            aidasUploads.forEach(i -> i.setAidasUserAidasObjectMapping(this));
        }
        this.aidasUploads = aidasUploads;
    }

    public AidasUserAidasObjectMapping aidasUploads(Set<AidasUpload> aidasUploads) {
        this.setAidasUploads(aidasUploads);
        return this;
    }

    public AidasUserAidasObjectMapping addAidasUpload(AidasUpload aidasUpload) {
        this.aidasUploads.add(aidasUpload);
        aidasUpload.setAidasUserAidasObjectMapping(this);
        return this;
    }

    public AidasUserAidasObjectMapping removeAidasUpload(AidasUpload aidasUpload) {
        this.aidasUploads.remove(aidasUpload);
        aidasUpload.setAidasUserAidasObjectMapping(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AidasUserAidasObjectMapping)) {
            return false;
        }
        return id != null && id.equals(((AidasUserAidasObjectMapping) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AidasUserAidasObjectMapping{" +
            "id=" + getId() +
            ", dateAssigned='" + getDateAssigned() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
