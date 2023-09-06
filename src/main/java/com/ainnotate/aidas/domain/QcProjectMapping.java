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
@Table(name = "qc_project_mapping",indexes = {
    @Index(name="idx_qpm_project",columnList = "project_id")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_qpm_ucmid_pid",columnNames={"user_mapping_id","project_id"})
    })
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
    @JoinColumn(name = "project_id", nullable = false, foreignKey = @ForeignKey(name="fk_qpm_project"))
    private Project project;




    @Column(name = "user_mapping_id")
    private Long userMappingId;


    @Column(name = "entity_id")
    private Integer entityId;

    public Long getUserMappingId() {
		return userMappingId;
	}

	public void setUserMappingId(Long userMappingId) {
		this.userMappingId = userMappingId;
	}

	public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	@Column(name="qc_level")
    private Integer qcLevel;

    @Column(name="current_qc_batch_no")
    private Integer currentQcBatchNo=0;

    public Integer getCurrentQcBatchNo() {
        return currentQcBatchNo;
    }

    public void setCurrentQcBatchNo(Integer currentQcBatchNo) {
        this.currentQcBatchNo = currentQcBatchNo;
    }

    public Integer getQcLevel() {
        return qcLevel;
    }

    public void setQcLevel(Integer qcLevel) {
        this.qcLevel = qcLevel;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

	/*
	 * public UserCustomerMapping getUserCustomerMapping() { return
	 * userCustomerMapping; }
	 *
	 * public void setUserCustomerMapping(UserCustomerMapping userCustomerMapping) {
	 * this.userCustomerMapping = userCustomerMapping; }
	 */

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

    @Override
    public String toString() {
        return "qcProjectMapping{" +"id="+id+"}";
    }
}
