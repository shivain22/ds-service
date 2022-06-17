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
@Table(name = "qc_project_mapping")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
public class QcProjectMapping extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties(value = {"user","customer"})
    private Project project;

    @ManyToOne
    @JsonIgnoreProperties(value = {"user","customer"})
    private UserCustomerMapping userCustomerMapping;

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

    public UserCustomerMapping getUserCustomerMapping() {
        return userCustomerMapping;
    }

    public void setUserCustomerMapping(UserCustomerMapping userCustomerMapping) {
        this.userCustomerMapping = userCustomerMapping;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QcProjectMapping)) {
            return false;
        }
        return Objects.equals(id, ((QcProjectMapping) o).id);
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
