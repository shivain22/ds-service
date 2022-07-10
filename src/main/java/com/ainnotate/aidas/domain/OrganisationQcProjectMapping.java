package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * An authority (a security role) used by Spring Security.
 */
@Entity
@Table(name = "organisation_qc_project_mapping",indexes = {
    @Index(name="idx_oqpm_project",columnList = "project_id"),
    @Index(name="idx_oqpm_uom",columnList = "user_organisation_mapping_id")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_oqpm_uomid_pid",columnNames={"user_organisation_mapping_id","project_id"})
    })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
public class OrganisationQcProjectMapping extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties(value = {"user","customer"})
    @JoinColumn(name = "project_id", nullable = false, foreignKey = @ForeignKey(name="fk_oqpm_project"))
    private Project project;

    @ManyToOne
    @JsonIgnoreProperties(value = {"user","organisation"})
    @JoinColumn(name = "user_organisation_mapping_id", nullable = false, foreignKey = @ForeignKey(name="fk_oqpm_uom"))
    private UserOrganisationMapping userOrganisationMapping;

    @Column(name="qc_level")
    private Long qcLevel;

    public Long getQcLevel() {
        return qcLevel;
    }

    public void setQcLevel(Long qcLevel) {
        this.qcLevel = qcLevel;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public UserOrganisationMapping getUserOrganisationMapping() {
        return userOrganisationMapping;
    }

    public void setUserOrganisationMapping(UserOrganisationMapping userOrganisationMapping) {
        this.userOrganisationMapping = userOrganisationMapping;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrganisationQcProjectMapping)) {
            return false;
        }
        return Objects.equals(id, ((OrganisationQcProjectMapping) o).id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AidasQcProjectMapping{" +
            "name='" + id + '\'' +
            "}";
    }
}
