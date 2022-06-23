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
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

/**
 * A AidasUserAidasObjectMapping.
 */
@Entity
@Table(name = "user_vendor_mapping_object_mapping")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "aidasuseraidasobjectmapping")
@Audited
public class UserVendorMappingObjectMapping extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGen")
    @SequenceGenerator(name = "seqGen", sequenceName = "seq", initialValue = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "date_assigned")
    private ZonedDateTime dateAssigned;

    @ManyToOne(optional = false,fetch = FetchType.EAGER)
    @NotNull
    @JsonIgnoreProperties(value = { "organisation", "customer", "vendor" }, allowSetters = true)
    private UserVendorMapping userVendorMapping;

    @ManyToOne(optional = false,fetch = FetchType.EAGER)
    @NotNull
    @JsonIgnoreProperties(value = { "project" }, allowSetters = true)
    private Object object;

    @OneToMany(mappedBy = "userVendorMappingObjectMapping")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "aidasUserAidasObjectMapping" }, allowSetters = true)
    private Set<Upload> uploads = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserVendorMappingObjectMapping id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDateAssigned() {
        return this.dateAssigned;
    }

    public UserVendorMappingObjectMapping dateAssigned(ZonedDateTime dateAssigned) {
        this.setDateAssigned(dateAssigned);
        return this;
    }

    public void setDateAssigned(ZonedDateTime dateAssigned) {
        this.dateAssigned = dateAssigned;
    }


    public UserVendorMappingObjectMapping status(Integer status) {
        this.setStatus(status);
        return this;
    }


    public UserVendorMapping getUserVendorMapping() {
        return userVendorMapping;
    }

    public void setUserVendorMapping(UserVendorMapping userVendorMapping) {
        this.userVendorMapping = userVendorMapping;
    }

    public UserVendorMappingObjectMapping user(UserVendorMapping userVendorMapping) {
        this.setUserVendorMapping(userVendorMapping);
        return this;
    }

    public Object getObject() {
        return this.object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public UserVendorMappingObjectMapping object(Object object) {
        this.setObject(object);
        return this;
    }

    public Set<Upload> getAidasUploads() {
        return this.uploads;
    }

    public void setAidasUploads(Set<Upload> uploads) {
        if (this.uploads != null) {
            this.uploads.forEach(i -> i.setAidasUserAidasObjectMapping(null));
        }
        if (uploads != null) {
            uploads.forEach(i -> i.setAidasUserAidasObjectMapping(this));
        }
        this.uploads = uploads;
    }

    public UserVendorMappingObjectMapping aidasUploads(Set<Upload> uploads) {
        this.setAidasUploads(uploads);
        return this;
    }

    public UserVendorMappingObjectMapping addAidasUpload(Upload upload) {
        this.uploads.add(upload);
        upload.setAidasUserAidasObjectMapping(this);
        return this;
    }

    public UserVendorMappingObjectMapping removeAidasUpload(Upload upload) {
        this.uploads.remove(upload);
        upload.setAidasUserAidasObjectMapping(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserVendorMappingObjectMapping)) {
            return false;
        }
        return id != null && id.equals(((UserVendorMappingObjectMapping) o).id);
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
