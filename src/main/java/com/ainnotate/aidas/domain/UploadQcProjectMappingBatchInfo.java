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
@Table(name = "upload_qpm_batch_info",
    uniqueConstraints={
        @UniqueConstraint(name = "uk_upload_qpm_status",columnNames={"upload_id","qc_project_mapping_id","batch_number","qc_status"})})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited

/*
 * @NamedNativeQuery( query = "select u.first_name firstName," +
 * "u.last_name as lastName," + "ucbi.qc_status as qcStatus," +
 * "ucbi.qc_seen_status as qcSeenStatus " + "from " +
 * "upload_qpm_batch_info ucbi,qc_project_mapping qpm, user_customer_mapping ucm, user u "
 * + "where ucbi.qc_project_mapping_id=qpm.id " + "and qpm.qc_level=?2 " +
 * "and ucbi.upload_id=?1 " + "and qpm.user_customer_mapping_id=ucm.id " +
 * "and ucm.user_id=u.id ", name =
 * "UploadQcProjectMappingBatchInfo.getQcLevelStatus",resultSetMapping =
 * "Mapping.QcResultDTO")
 */
@NamedNativeQuery(
	    query = "select \n"
	    		+ "u.first_name firstName,\n"
	    		+ "u.last_name as lastName,\n"
	    		+ "ucbi.qc_status as qcStatus,\n"
	    		+ "ucbi.qc_seen_status as qcSeenStatus \n"
	    		+ "from \n"
	    		+ "upload_qpm_batch_info ucbi,\n"
	    		+ "qc_project_mapping qpm, \n"
	    		+ "uam_uom_mapping uum,\n"
	    		+ "user_organisation_mapping uom, \n"
	    		+ "user u \n"
	    		+ "where \n"
	    		+ "ucbi.qc_project_mapping_id=qpm.id \n"
	    		+ "and qpm.qc_level=?2 \n"
	    		+ "and ucbi.upload_id=?1 \n"
	    		+ "and qpm.user_mapping_id=uum.id \n"
	    		+ "and qpm.entity_id=1\n"
	    		+ "and uum.uom_id=uom.id\n"
	    		+ "and uom.user_id=u.id\n"
	    		+ "\n"
	    		+ "union\n"
	    		+ "\n"
	    		+ "select \n"
	    		+ "u.first_name firstName,\n"
	    		+ "u.last_name as lastName,\n"
	    		+ "ucbi.qc_status as qcStatus,\n"
	    		+ "ucbi.qc_seen_status as qcSeenStatus \n"
	    		+ "from \n"
	    		+ "upload_qpm_batch_info ucbi,\n"
	    		+ "qc_project_mapping qpm, \n"
	    		+ "user_customer_mapping ucm, \n"
	    		+ "uam_ucm_mapping uum,\n"
	    		+ "user u \n"
	    		+ "where \n"
	    		+ "ucbi.qc_project_mapping_id=qpm.id \n"
	    		+ "and qpm.qc_level=?2 \n"
	    		+ "and ucbi.upload_id=?1 \n"
	    		+ "and qpm.user_mapping_id=uum.id \n"
	    		+ "and qpm.entity_id=2\n"
	    		+ "and uum.ucm_id=ucm.id\n"
	    		+ "and ucm.user_id=u.id\n"
	    		+ "\n"
	    		+ "union\n"
	    		+ "\n"
	    		+ "select \n"
	    		+ "u.first_name firstName,\n"
	    		+ "u.last_name as lastName,\n"
	    		+ "ucbi.qc_status as qcStatus,\n"
	    		+ "ucbi.qc_seen_status as qcSeenStatus \n"
	    		+ "from \n"
	    		+ "upload_qpm_batch_info ucbi,\n"
	    		+ "qc_project_mapping qpm, \n"
	    		+ "user_vendor_mapping uvm,\n"
	    		+ "uam_uvm_mapping uum, \n"
	    		+ "user u \n"
	    		+ "where \n"
	    		+ "ucbi.qc_project_mapping_id=qpm.id \n"
	    		+ "and qpm.qc_level=?2 \n"
	    		+ "and ucbi.upload_id=?1 \n"
	    		+ "and qpm.user_mapping_id=uum.id \n"
	    		+ "and qpm.entity_id=3\n"
	    		+ "and uum.uvm_id=uvm.id\n"
	    		+ "and uvm.user_id=u.id",
	    name = "UploadQcProjectMappingBatchInfo.getQcLevelStatus",resultSetMapping = "Mapping.QcResultDTO")






@SqlResultSetMappings(value={
	    
	    @SqlResultSetMapping(
	        name = "Mapping.UploadSummaryForQCFinalize",
	        classes = @ConstructorResult(targetClass = UploadSummaryForQCFinalize.class,
	            columns = {
	                @ColumnResult(name = "projectId",type = Long.class),
	                @ColumnResult(name = "objectId",type = Long.class),
	                @ColumnResult(name = "uvmpmId",type = Long.class),
	                @ColumnResult(name = "uvmomId",type = Long.class),
	                @ColumnResult(name = "uvmId",type = Long.class),
	                @ColumnResult(name = "totalUploaded",type = Integer.class),
	                @ColumnResult(name = "totalApproved",type = Integer.class),
	                @ColumnResult(name = "totalRejected",type = Integer.class),
	                @ColumnResult(name = "totalPending",type = Integer.class),
	                @ColumnResult(name = "totalShowToQc",type = Integer.class)
	            })),
	    @SqlResultSetMapping(
		        name = "Mapping.UploadSummaryForQCFinalizeNonGrouped",
		        classes = @ConstructorResult(targetClass = UploadSummaryForQCFinalize.class,
		            columns = {
		            	@ColumnResult(name = "uvmpmId",type = Long.class),
		                @ColumnResult(name = "projectId",type = Long.class),
		                @ColumnResult(name = "totalUploaded",type = Integer.class),
		                @ColumnResult(name = "totalApproved",type = Integer.class),
		                @ColumnResult(name = "totalRejected",type = Integer.class),
		                @ColumnResult(name = "totalPending",type = Integer.class)
		            })),
	    @SqlResultSetMapping(
		        name = "Mapping.UploadSummaryForQCFinalizeNonGroupedForProject",
		        classes = @ConstructorResult(targetClass = UploadSummaryForQCFinalize.class,
		            columns = {
		                @ColumnResult(name = "projectId",type = Long.class),
		                @ColumnResult(name = "totalUploaded",type = Integer.class),
		                @ColumnResult(name = "totalApproved",type = Integer.class),
		                @ColumnResult(name = "totalRejected",type = Integer.class),
		                @ColumnResult(name = "totalPending",type = Integer.class)
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

@NamedNativeQuery(name="UploadQcProjectMappingBatchInfo.getUvmomObjectIdsOfBatch",
query="select "
		+ " p.id as projectId,"
		+ " uvmpm.id as uvmpmId,"
		+ " u.user_vendor_mapping_object_mapping_id as uvmomId,"
		+ " uvmom.user_vendor_mapping_id as uvmId,"
		+ " o.id as objectId,"
		+ " count(*) as totalUploaded, \n"
		+ " sum(case when ucbi.qc_status=1 then 1 else 0 end) as totalApproved, \n"
		+ " sum(case when ucbi.qc_status=0 then 1 else 0 end) as totalRejected, \n"
		+ " sum(case when ucbi.qc_status=2 then 1 else 0 end) as totalPending, \n"
		+ " sum(ucbi.show_to_qc) as totalShowToQc \n"
		+ " from  \n"
		+ " upload_qpm_batch_info ucbi, \n"
		+ " upload u,\n"
		+ " user_vendor_mapping_object_mapping uvmom, \n"
		+ " user_vendor_mapping_project_mapping uvmpm, \n"
		+ " user_vendor_mapping uvm, \n"
		+ " object o ,\n"
		+ " project p \n"
		+ " where \n"
		+ " ucbi.upload_id=u.id \n"
		+ " and  u.user_vendor_mapping_object_mapping_id = uvmom.id \n"
		+ " and uvmom.object_id=o.id \n"
		+ " and o.project_id=p.id \n"
		+ " and uvmom.user_vendor_mapping_id=uvm.id \n"
		+ " and uvmpm.user_vendor_mapping_id=uvm.id \n"
		+ " and uvmpm.project_id=p.id \n"
		+ " and ucbi.batch_number=?2 \n"
		+ " and ucbi.qc_project_mapping_id=?1 \n"
		+ " group by u.user_vendor_mapping_object_mapping_id,o.id,uvmpm.id, p.id"
    ,resultSetMapping = "Mapping.UploadSummaryForQCFinalize")


@NamedNativeQuery(name="UploadQcProjectMappingBatchInfo.getUvmomObjectIdsOfBatchNonGrouped",
query="SELECT \n"
		+ " uvmpm.id as uvmpmId,\n"
		+ " uvmpm.project_id as projectId,\n"
		+ " count(*) as totalUploaded, \n"
		+ " sum(case when u.approval_status=1 then 1 else 0 end) as totalApproved, \n"
		+ " sum(case when u.approval_status=0 then 1 else 0 end) as totalRejected, \n"
		+ " sum(case when u.approval_status=2 then 1 else 0 end) as totalPending\n"
		+ " FROM \n"
		+ " upload u, \n"
		+ " user_vendor_mapping_object_mapping uvmom, \n"
		+ " object o ,\n"
		+ " user_vendor_mapping_project_mapping uvmpm\n"
		+ " where u.user_vendor_mapping_object_mapping_id=uvmom.id \n"
		+ " and uvmpm.project_id=o.project_id\n"
		+ " and uvmpm.user_vendor_mapping_id = uvmom.user_vendor_mapping_id\n"
		+ " and uvmom.object_id=o.id \n"
		+ " and o.project_id=?1\n"
		+ "and uvmom.user_vendor_mapping_id=?2"
    ,resultSetMapping = "Mapping.UploadSummaryForQCFinalizeNonGrouped")


@NamedNativeQuery(name="UploadQcProjectMappingBatchInfo.getUvmomObjectIdsOfBatchNonGroupedForProject",
query="SELECT \n"
		+ "uvmpm.project_id as projectId,\n"
		+ "count(*) as totalUploaded, \n"
		+ "sum(case when u.approval_status=1 then 1 else 0 end) as totalApproved, \n"
		+ "sum(case when u.approval_status=0 then 1 else 0 end) as totalRejected, \n"
		+ "sum(case when u.approval_status=2 then 1 else 0 end) as totalPending\n"
		+ "FROM \n"
		+ "upload u, \n"
		+ "user_vendor_mapping_object_mapping uvmom, \n"
		+ "object o ,\n"
		+ "user_vendor_mapping_project_mapping uvmpm\n"
		+ "where u.user_vendor_mapping_object_mapping_id=uvmom.id \n"
		+ "and uvmpm.project_id=o.project_id\n"
		+ "and uvmpm.user_vendor_mapping_id = uvmom.user_vendor_mapping_id\n"
		+ "and uvmom.object_id=o.id \n"
		+ "and o.project_id=?1\n"
		+ "group by o.project_id"
    ,resultSetMapping = "Mapping.UploadSummaryForQCFinalizeNonGroupedForProject")

@NamedNativeQuery(name="UploadQcProjectMappingBatchInfo.countUploadsByqcProjectMappingAndBatchNumberForFinalize",
query="select count(ucbi.id) as totalUploaded,\n"
		+ "sum(case when ucbi.qc_status=1 then 1 else 0 end) as totalApproved, \n"
		+ "sum(case when ucbi.qc_status=0 then 1 else 0 end) as totalRejected,\n"
		+ "sum(case when ucbi.qc_status=2 then 1 else 0 end) as totalPending  \n"
		+ "from upload_qpm_batch_info ucbi \n"
		+ "where ucbi.qc_project_mapping_id=?1 \n"
		+ "and ucbi.batch_number=?2 "
    ,resultSetMapping = "Mapping.BatchInfoMapping")




@org.springframework.data.elasticsearch.annotations.Document(indexName = "uploadqpmBatchInfo")
public class UploadQcProjectMappingBatchInfo extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name="upload_id")
    private Long uploadId;

    @Column(name="qc_project_mapping_id")
    private Long qcProjectMappingId;

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

    public Long getqcProjectMappingId() {
        return qcProjectMappingId;
    }

    public void setqcProjectMappingId(Long qcProjectMappingId) {
        this.qcProjectMappingId = qcProjectMappingId;
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
