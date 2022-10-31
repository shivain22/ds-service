package com.ainnotate.aidas.domain;

import com.ainnotate.aidas.dto.ProjectDTO;
import com.ainnotate.aidas.dto.QcResultDTO;
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

@NamedNativeQuery(
    query = "select u.first_name firstName," +
        "u.last_name as lastName," +
        "ucbi.qc_status as qcStatus " +
        "from " +
        "upload_cqpm_batch_info ucbi,customer_qc_project_mapping cqpm, user_customer_mapping ucm, user u " +
        "where ucbi.customer_qc_project_mapping_id=cqpm.id " +
        "and cqpm.qc_level=?2 " +
        "and ucbi.upload_id=?1 " +
        "and cqpm.user_customer_mapping_id=ucm.id " +
        "and ucm.user_id=u.id ",
    name = "UploadCustomerQcProjectMappingBatchInfo.getQcLevelStatus",resultSetMapping = "Mapping.QcResultDTO")
@SqlResultSetMapping(name = "Mapping.QcResultDTO",
    classes = @ConstructorResult(targetClass = QcResultDTO.class,
        columns = {
            @ColumnResult(name = "firstName",type = String.class),
            @ColumnResult(name = "lastName",type = String.class),
            @ColumnResult(name = "qcStatus",type = Integer.class)
        }))
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
    private Long customerQcProjectMappingId;

    @Column(name="batch_number")
    private Integer batchNumber;

    @Column(name="qc_status")
    private Integer qcStatus;

    @Column(name="qc_status_other_than_level_1")
    private Integer qcStatusOtherThanLevel1;

    @Column(name="show_to_qc")
    private Integer showToQc=0;

    @Column(name="qc_seen_status")
    private Integer qc_seen_status=0;

    public Integer getShowToQc() {
        return showToQc;
    }

    public void setShowToQc(Integer showToQc) {
        this.showToQc = showToQc;
    }

    public Integer getQc_seen_status() {
        return qc_seen_status;
    }

    public void setQc_seen_status(Integer qc_seen_status) {
        this.qc_seen_status = qc_seen_status;
    }

    public Integer getQcStatusOtherThanLevel1() {
        return qcStatusOtherThanLevel1;
    }

    public void setQcStatusOtherThanLevel1(Integer qcStatusOtherThanLevel1) {
        this.qcStatusOtherThanLevel1 = qcStatusOtherThanLevel1;
    }

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

    public Long getCustomerQcProjectMappingId() {
        return customerQcProjectMappingId;
    }

    public void setCustomerQcProjectMappingId(Long customerQcProjectMappingId) {
        this.customerQcProjectMappingId = customerQcProjectMappingId;
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
