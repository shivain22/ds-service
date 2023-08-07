package com.ainnotate.aidas.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.repository.Query;

import com.ainnotate.aidas.dto.QcResultDTO;
import com.ainnotate.aidas.dto.UploadSummaryForQCFinalize;

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
        "ucbi.qc_status as qcStatus,"
        + "ucbi.qc_seen_status as qcSeenStatus " +
        "from " +
        "upload_cqpm_batch_info ucbi,customer_qc_project_mapping cqpm, user_customer_mapping ucm, user u " +
        "where ucbi.customer_qc_project_mapping_id=cqpm.id " +
        "and cqpm.qc_level=?2 " +
        "and ucbi.upload_id=?1 " +
        "and cqpm.user_customer_mapping_id=ucm.id " +
        "and ucm.user_id=u.id ",
    name = "UploadCustomerQcProjectMappingBatchInfo.getQcLevelStatus",resultSetMapping = "Mapping.QcResultDTO")






@SqlResultSetMappings(value={
	    
	    @SqlResultSetMapping(
	        name = "Mapping.UploadSummaryForQCFinalize",
	        classes = @ConstructorResult(targetClass = UploadSummaryForQCFinalize.class,
	            columns = {
	                @ColumnResult(name = "projectId",type = Long.class),
	                @ColumnResult(name = "objectId",type = Long.class),
	                @ColumnResult(name = "uvmpmId",type = Long.class),
	                @ColumnResult(name = "uvmomId",type = Long.class),
	                @ColumnResult(name = "totalUploaded",type = Integer.class),
	                @ColumnResult(name = "totalApproved",type = Integer.class),
	                @ColumnResult(name = "totalRejected",type = Integer.class),
	                @ColumnResult(name = "totalPending",type = Integer.class),
	                @ColumnResult(name = "totalShowToQc",type = Integer.class)
	            })),
	    @SqlResultSetMapping(name = "Mapping.QcResultDTO",
	    classes = @ConstructorResult(targetClass = QcResultDTO.class,
	        columns = {
	            @ColumnResult(name = "firstName",type = String.class),
	            @ColumnResult(name = "lastName",type = String.class),
	            @ColumnResult(name = "qcStatus",type = Integer.class),
	            @ColumnResult(name = "qcSeenStatus",type = Integer.class)
	        })),
	    @SqlResultSetMapping(name = "Mapping.BatchInfoMapping",
	    classes = @ConstructorResult(targetClass = UploadSummaryForQCFinalize.class,
	        columns = {
	        		@ColumnResult(name = "totalUploaded",type = Integer.class),
	                @ColumnResult(name = "totalApproved",type = Integer.class),
	                @ColumnResult(name = "totalRejected",type = Integer.class),
	                @ColumnResult(name = "totalPending",type = Integer.class)
	        }))
	})

@NamedNativeQuery(name="UploadCustomerQcProjectMappingBatchInfo.getUvmomObjectIdsOfBatch",
query="select "
		+ " p.id as projectId,"
		+ " uvmpm.id as uvmpmId,"
		+ " u.user_vendor_mapping_object_mapping_id as uvmomId,"
		+ " o.id as objectId,"
		+ " count(*) as totalUploaded, \n"
		+ " sum(case when ucbi.qc_status=1 then 1 else 0 end) as totalApproved, \n"
		+ " sum(case when ucbi.qc_status=0 then 1 else 0 end) as totalRejected, \n"
		+ " sum(case when ucbi.qc_status=2 then 1 else 0 end) as totalPending, \n"
		+ " sum(ucbi.show_to_qc) as totalShowToQc \n"
		+ " from  \n"
		+ " upload_cqpm_batch_info ucbi, \n"
		+ " upload u,\n"
		+ " user_vendor_mapping_object_mapping uvmom, \n"
		+ " user_vendor_mapping_project_mapping uvmpm, \n"
		+ " object o ,\n"
		+ " project p \n"
		+ " where \n"
		+ " ucbi.upload_id=u.id \n"
		+ " and  u.user_vendor_mapping_object_mapping_id = uvmom.id \n"
		+ " and uvmom.object_id=o.id \n"
		+ " and o.project_id=p.id \n"
		+ " and uvmpm.project_id=p.id \n"
		+ " and ucbi.batch_number=?2 \n"
		+ " and ucbi.customer_qc_project_mapping_id=?1 \n"
		+ " group by u.user_vendor_mapping_object_mapping_id,o.id,uvmpm.id, p.id"
    ,resultSetMapping = "Mapping.UploadSummaryForQCFinalize")

@NamedNativeQuery(name="UploadCustomerQcProjectMappingBatchInfo.countUploadsByCustomerQcProjectMappingAndBatchNumberForFinalize",
query="select count(ucbi.id) as totalUploaded,\n"
		+ "sum(case when ucbi.qc_status=1 then 1 else 0 end) as totalApproved, \n"
		+ "sum(case when ucbi.qc_status=0 then 1 else 0 end) as totalRejected,\n"
		+ "sum(case when ucbi.qc_status=2 then 1 else 0 end) as totalPending  \n"
		+ "from upload_cqpm_batch_info ucbi \n"
		+ "where ucbi.customer_qc_project_mapping_id=?1 \n"
		+ "and ucbi.batch_number=?2 "
    ,resultSetMapping = "Mapping.BatchInfoMapping")




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
    private Long batchNumber;

    @Column(name="qc_status")
    private Integer qcStatus=2;

    @Column(name="qc_status_other_than_level_1")
    private Integer qcStatusOtherThanLevel1;

    @Column(name="show_to_qc")
    private Integer showToQc=1;

    @Column(name="qc_seen_status")
    private Integer qcSeenStatus=0;

    public Integer getShowToQc() {
        return showToQc;
    }

    public void setShowToQc(Integer showToQc) {
        this.showToQc = showToQc;
    }

   
    public Integer getQcSeenStatus() {
		return qcSeenStatus;
	}

	public void setQcSeenStatus(Integer qcSeenStatus) {
		this.qcSeenStatus = qcSeenStatus;
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

    public Long getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(Long batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Integer getQcStatus() {
        return qcStatus;
    }

    public void setQcStatus(Integer qcStatus) {
        this.qcStatus = qcStatus;
    }
}
