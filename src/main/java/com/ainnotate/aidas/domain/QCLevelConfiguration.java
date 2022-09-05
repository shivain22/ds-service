package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;


@Table(name = "project_qc_level_configurations")
@Entity
@Audited
public class QCLevelConfiguration extends AbstractAuditingEntity  implements Serializable {


    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @ManyToOne
    @JoinColumn(name="project_id", nullable=false)
    private Project project;

    @Column(name ="qc_level_name",columnDefinition = "varchar(50) default ' '")
    private Integer qcLevelName;

    @Column(name ="qc_level_acceptance_percentage",columnDefinition = "integer default 0")
    private Integer qcLevelAcceptancePercentage=0;

    @Column(name ="qc_level_batch_size",columnDefinition = "integer default 0")
    private Integer qcLevelBatchSize=10;

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

    public Integer getQcLevelName() {
        return qcLevelName;
    }

    public void setQcLevelName(Integer qcLevelName) {
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

        return new EqualsBuilder().append(id, that.id)
            .append(project, that.project)
            .append(qcLevelName, that.qcLevelName)
            .append(qcLevelAcceptancePercentage, that.qcLevelAcceptancePercentage)
            .append(qcLevelBatchSize, that.qcLevelBatchSize)
            .isEquals();
    }

    public Integer getQcLevelBatchSize() {
        return qcLevelBatchSize;
    }

    public void setQcLevelBatchSize(Integer batchSize) {
        this.qcLevelBatchSize = batchSize;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).
            append(id)
            .append(project)
            .append(qcLevelName)
            .append(qcLevelAcceptancePercentage)
            .append(qcLevelBatchSize)
            .toHashCode();
    }

    @Override
    public String toString() {
        return "QCLevelConfiguration{" +
            "id=" + id +
            ", project=" + project +
            ", qcLevelName='" + qcLevelName + '\'' +
            ", qcLevelAcceptancePercentage=" + qcLevelAcceptancePercentage +
            ", QC Batch Size=" + qcLevelBatchSize +
            '}';
    }
}
