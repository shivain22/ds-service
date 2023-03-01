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
@Table(name = "cqpm_batch_mapping",indexes = {
    @Index(name="idx_cqpm_batch_mapping",columnList = "cqpm_id,batch_no")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_cqpm_cqpm_id_batch_no",columnNames={"cqpm_id","batch_no"})
    })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
public class CustomerQcProjectMappingBatchMapping extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties(value = {"user","customer"})
    @JoinColumn(name = "cqpm_id", nullable = false, foreignKey = @ForeignKey(name="fk_cqpm_batch"))
    private CustomerQcProjectMapping customerQcProjectMapping;


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

    public CustomerQcProjectMapping getCustomerQcProjectMapping() {
        return customerQcProjectMapping;
    }

    public void setCustomerQcProjectMapping(CustomerQcProjectMapping customerQcProjectMapping) {
        this.customerQcProjectMapping = customerQcProjectMapping;
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
        if (!(o instanceof CustomerQcProjectMappingBatchMapping)) {
            return false;
        }
        return Objects.equals(id, ((CustomerQcProjectMappingBatchMapping) o).id);
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
        return "CustomerQcProjectMappingBatchMapping{" +"id="+id+",cqpm_id="+this.customerQcProjectMapping.getId()+",batch_no="+this.batchNo+"}";
    }
}
