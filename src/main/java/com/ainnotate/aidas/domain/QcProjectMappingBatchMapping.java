package com.ainnotate.aidas.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import com.ainnotate.aidas.dto.QbmDto;
import com.ainnotate.aidas.dto.UploadDTOForQC;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@NamedNativeQuery(
		name = "QcProjectMappingBatchMapping.getQcNotCompletedBatches", 
				query="select qbm.id as qbmId,\n"
			    		+ "count(case when ucbi.qc_status=1 then 1 end )as approved, \n"
			    		+ "count(case when ucbi.qc_status=0 then 1 end)as rejected,\n"
			    		+ "count(case when ucbi.qc_status=2 then 2 end)as pending \n"
			    		+ "from \n"
			    		+ "qpm_batch_mapping qbm,\n"
			    		+ "qc_project_mapping qpm,\n"
			    		+ "upload_qpm_batch_info ucbi \n"
			    		+ "where \n"
			    		+ "ucbi.batch_number=qbm.id and \n"
			    		+ "qpm.id=?3 and \n"
			    		+ "qbm.qpm_id=qpm.id and \n"
			    		+ "qpm.project_id=?1 and \n"
			    		+ "qpm.qc_level=?2 and \n"
			    		+ "qbm.batch_completion_status=2 group by qbm.id", 
		resultSetMapping = "Mapping.getQcNotCompletedBatches")

@SqlResultSetMapping(
	name = "Mapping.getQcNotCompletedBatches", 
	classes = @ConstructorResult(targetClass = QbmDto.class, 
	columns = {
			@ColumnResult(name = "qbmId", type = Long.class), 
			@ColumnResult(name = "approved", type = Integer.class),
			@ColumnResult(name = "rejected", type = Integer.class),
			@ColumnResult(name = "pending", type = Integer.class)
}))
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
