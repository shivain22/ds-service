package com.ainnotate.aidas.domain;

import com.ainnotate.aidas.constants.AidasConstants;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;


@Table(name = "project_qc_level_configurations")
@Entity
@Audited
public class ProjectQcLevelConfigurations extends AbstractAuditingEntity  implements Serializable {


    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @ManyToOne
    @JoinColumn(name="project_id", nullable=false)
    private Project project;

    @Column(name ="qc_level",columnDefinition = "integer default 1")
    private Integer qcLevel;

    @Column(name ="qc_level_acceptance_percentage",columnDefinition = "integer default 0")
    private Integer qcLevelAcceptancePercentage=0;

    @Column(name ="qc_level_batch_size",columnDefinition = "integer default 0")
    private Integer qcLevelBatchSize=10;

    @Column(name="allocation_strategy",columnDefinition = "integer default 0")
    private Integer allocationStrategy= AidasConstants.QC_LEVEL_FCFS;

    public Integer getAllocationStrategy() {
        return allocationStrategy;
    }

    public void setAllocationStrategy(Integer allocationStrategy) {
        this.allocationStrategy = allocationStrategy;
    }

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

    public Integer getQcLevel() {
        return qcLevel;
    }

    public void setQcLevel(Integer qcLevel) {
        this.qcLevel = qcLevel;
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

        ProjectQcLevelConfigurations that = (ProjectQcLevelConfigurations) o;

        return new EqualsBuilder().append(id, that.id)
            .append(project, that.project)
            .append(qcLevel, that.qcLevel)
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
            .append(qcLevel)
            .append(qcLevelAcceptancePercentage)
            .append(qcLevelBatchSize)
            .toHashCode();
    }

    @Override
    public String toString() {
        return "QCLevelConfiguration{" +
            "id=" + id +
            ", project=" + project +
            ", qcLevelName='" + qcLevel + '\'' +
            ", qcLevelAcceptancePercentage=" + qcLevelAcceptancePercentage +
            ", QC Batch Size=" + qcLevelBatchSize +
            '}';
    }
}
