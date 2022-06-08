package com.ainnotate.aidas.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * An authority (a security role) used by Spring Security.
 */
@Entity
@Table(name = "aidas_qc_project_mapping")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
public class AidasQcProjectMapping extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    private AidasProject aidasProject;

    @ManyToOne
    private AidasUser aidasUser;

    @Column(name="qc_level")
    private Long qcLevel;

    public Long getQcLevel() {
        return qcLevel;
    }

    public void setQcLevel(Long qcLevel) {
        this.qcLevel = qcLevel;
    }

    public AidasProject getAidasProject() {
        return aidasProject;
    }

    public void setAidasProject(AidasProject aidasProject) {
        this.aidasProject = aidasProject;
    }

    public AidasUser getAidasUser() {
        return aidasUser;
    }

    public void setAidasUser(AidasUser aidasUser) {
        this.aidasUser = aidasUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AidasQcProjectMapping)) {
            return false;
        }
        return Objects.equals(id, ((AidasQcProjectMapping) o).id);
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
