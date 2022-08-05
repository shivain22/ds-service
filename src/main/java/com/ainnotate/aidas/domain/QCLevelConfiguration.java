package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Table(name = "qclevel_configuration")
@Entity
@Audited
public class QCLevelConfiguration extends AbstractAuditingEntity  implements Serializable {


    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @ManyToOne(optional = false)
    @NotNull
    @JoinColumn(name = "project_id", nullable = false, foreignKey = @ForeignKey(name="fk_project_id"))
    private Project project;

    @Column(name ="qc_leval_name",columnDefinition = "varchar(50) default ' '")
    private String qcLevelName;

    @Column(name ="qc_level_acceptance_percentage",columnDefinition = "integer default 0")
    private Integer qcLevelAcceptancePercentage=0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {

        this.project = project;

    }

    public String getQcLevelName() {
        return qcLevelName;
    }

    public void setQcLevelName(String qcLevelName) {
        this.qcLevelName = qcLevelName;
    }

    public Integer getQcLevelAcceptancePercentage() {
        return qcLevelAcceptancePercentage;
    }

    public void setQcLevelAcceptancePercentage(Integer qcLevelAcceptancePercentage) {
        this.qcLevelAcceptancePercentage = qcLevelAcceptancePercentage;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        QCLevelConfiguration that = (QCLevelConfiguration) o;

        return new EqualsBuilder().append(id, that.id).append(project, that.project).append(qcLevelName, that.qcLevelName).append(qcLevelAcceptancePercentage, that.qcLevelAcceptancePercentage).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(project).append(qcLevelName).append(qcLevelAcceptancePercentage).toHashCode();
    }

    @Override
    public String toString() {
        return "QCLevelConfiguration{" +
            "id=" + id +
            ", project=" + project +
            ", qcLevelName='" + qcLevelName + '\'' +
            ", qcLevelAcceptancePercentage=" + qcLevelAcceptancePercentage +
            '}';
    }
}
