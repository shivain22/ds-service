package com.ainnotate.aidas.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * An authority (a security role) used by Spring Security.
 */
@Entity
@Table(name = "qpm_batch_mapping",indexes = {
    @Index(name="idx_qpm_batch_mapping",columnList = "qpm_id,batch_no")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_qpm_qpm_id_batch_no",columnNames={"qpm_id","batch_no"})
    })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
public class QcProjectMappingBatchMapping extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties(value = {"user","customer"})
    @JoinColumn(name = "qpm_id", nullable = false,referencedColumnName = "id")
    private QcProjectMapping qcProjectMapping;


    @Column(name="batch_no")
    private Integer batchNo=0;


    @Column(name="batch_completion_status")
    private Integer batchCompletionStatus=2;

    
    @Column(name="current_page_number")
    private Integer currentPageNumber=0;
    
    public Integer getCurrentPageNumber() {
		return currentPageNumber;
	}

	public void setCurrentPageNumber(Integer currentPageNumber) {
		this.currentPageNumber = currentPageNumber;
	}

	@Column(name="previous_level_batch_number")
    private Long previousLevelBatchNumber;

    @Column(name="next_level_batch_number")
    private Long nextLevelBatchNumber;

    public Long getNextLevelBatchNumber() {
        return nextLevelBatchNumber;
    }

    public void setNextLevelBatchNumber(Long nextLevelBatchNumber) {
        this.nextLevelBatchNumber = nextLevelBatchNumber;
    }

    public Long getPreviousLevelBatchNumber() {
        return previousLevelBatchNumber;
    }

    public void setPreviousLevelBatchNumber(Long previousLevelBatchNumber) {
        this.previousLevelBatchNumber = previousLevelBatchNumber;
    }

    public Integer getBatchCompletionStatus() {
        return batchCompletionStatus;
    }

    public void setBatchCompletionStatus(Integer batchCompletionStatus) {
        this.batchCompletionStatus = batchCompletionStatus;
    }

    public QcProjectMapping getqcProjectMapping() {
        return qcProjectMapping;
    }

    public void setqcProjectMapping(QcProjectMapping qcProjectMapping) {
        this.qcProjectMapping = qcProjectMapping;
    }

    public Integer getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(Integer batchNo) {
        this.batchNo = batchNo;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QcProjectMappingBatchMapping)) {
            return false;
        }
        return Objects.equals(id, ((QcProjectMappingBatchMapping) o).id);
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
        return "qcProjectMappingBatchMapping{" +"id="+id+",qpm_id="+this.qcProjectMapping.getId()+",batch_no="+this.batchNo+"}";
    }
}
