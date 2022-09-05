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
@Table(name = "upload_cqpm_batch_info",
    uniqueConstraints={
        @UniqueConstraint(name = "uk_upload_cqpm_status",columnNames={"upload_id","customer_qc_project_mapping_id","batch_number","qc_status"})})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
@org.springframework.data.elasticsearch.annotations.Document(indexName = "uploadCqpmBatchInfo")
public class UploadCustomerQcProjectMappingBatchInfo extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name="upload_id")
    private Long uploadId;

    @Column(name="customer_qc_project_mapping_id")
    private Long customerQcMappingId;

    @Column(name="batch_number")
    private Integer batchNumber;

    @Column(name="qc_status")
    private Integer qcStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUploadId() {
        return uploadId;
    }

    public void setUploadId(Long uploadId) {
        this.uploadId = uploadId;
    }

    public Long getCustomerQcMappingId() {
        return customerQcMappingId;
    }

    public void setCustomerQcMappingId(Long customerQcMappingId) {
        this.customerQcMappingId = customerQcMappingId;
    }

    public Integer getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(Integer batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Integer getQcStatus() {
        return qcStatus;
    }

    public void setQcStatus(Integer qcStatus) {
        this.qcStatus = qcStatus;
    }
}
