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
@Table(name = "user_vendor_mapping_object_mapping",indexes = {
    @Index(name="idx_uvmom_object",columnList = "object_id"),
    @Index(name="idx_uvmom_uvm",columnList = "user_vendor_mapping_id")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_uvmid_oid",columnNames={"user_vendor_mapping_id", "object_id"})
    })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "uservendorobjectmapping")
@Audited
public class UserVendorMappingObjectMapping extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date_assigned")
    private ZonedDateTime dateAssigned;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @NotNull
    @JsonIgnoreProperties(value = { "organisation", "customer", "vendor" }, allowSetters = true)
    @JoinColumn(name = "user_vendor_mapping_id", nullable = true, foreignKey = @ForeignKey(name="fk_uvmom_uvm"))
    private UserVendorMapping userVendorMapping;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @NotNull
    @JsonIgnoreProperties(value = { "project" }, allowSetters = true)
    @JoinColumn(name = "object_id", nullable = true, foreignKey = @ForeignKey(name="fk_uvmom_object"))
    private Object object;

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
        return "UserVendorMappingObjectMapping{" +
            "id=" + getId() +
            ", dateAssigned='" + getDateAssigned() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
