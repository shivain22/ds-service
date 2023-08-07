package com.ainnotate.aidas.domain;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;

import com.ainnotate.aidas.dto.UploadDTOForQC;
import com.ainnotate.aidas.dto.UploadMetadataDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@NamedNativeQuery(name="Upload.findAllUploadIdsGroupedNew",
				  query=
						  " select u.id as uploadId,"
									+ "u.upload_url as uploadUrl,"
									+ "uvmom.id as userVendorMappingObjectMappingId,"
									+ "p.id as projectId, "
									+ "o.id as objectId, "
									+ "p.name as projectName, "
									+ "o.name as objectName,"
									+ "-1 as ucbiId,"
									+ "-1 as batchNumber,"
									+ "u.name as fileName,"
									+ "2 as qcStatus,  "
									+ "uvmom.consent_form_url as consentFormUrl  "
									+ "from "
									+ "upload u,"
									+ "user_vendor_mapping_object_mapping uvmom,"
									+ "object o,"
									+ "project p "
									+ "where u.user_vendor_mapping_object_mapping_id=uvmom.id "
									+ "and uvmom.object_id=o.id "
									+ "and o.project_id=p.id "
									+ "and u.user_vendor_mapping_object_mapping_id in (?1) "
									+ "and u.qc_done_by_id is null \n" 
						            + "and u.qc_start_date is null  \n" 
						            + "and u.qc_end_date is null   \n" 
						            + "and (u.qc_status is null or u.qc_status=2)  and  u.metadata_status=1 " 
								    + "order by u.user_vendor_mapping_object_mapping_id,u.id"
				
				            , resultSetMapping = "Mapping.getUploadDTOForQCInBatch")


@NamedNativeQuery(name="Upload.findAllUploadIdsNonGroupedNew",
query=
		  " select u.id as uploadId,"
					+ "u.upload_url as uploadUrl,"
					+ "uvmom.id as userVendorMappingObjectMappingId,"
					+ "p.id as projectId, "
					+ "o.id as objectId, "
					+ "p.name as projectName, "
					+ "o.name as objectName,"
					+ "-1 as ucbiId,"
					+ "-1 as batchNumber,"
					+ "u.name as fileName,"
					+ "2 as qcStatus  "
					+ "from "
					+ "upload u,"
					+ "user_vendor_mapping_object_mapping uvmom,"
					+ "object o,"
					+ "project p "
					+ "where u.user_vendor_mapping_object_mapping_id=uvmom.id "
					+ "and uvmom.object_id=o.id "
					+ "and o.project_id=p.id "
					+ "and u.user_vendor_mapping_object_mapping_id in (?1) "
					+ "and u.qc_done_by_id is null \n" 
		            + "and u.qc_start_date is null  \n" 
		            + "and u.qc_end_date is null   \n" 
		            + "and (u.qc_status is null or u.qc_status=2)  and  u.metadata_status=1 " 
				    + "order by u.user_vendor_mapping_object_mapping_id,u.id limit ?2"

          , resultSetMapping = "Mapping.getUploadDTOForQCInBatch")


@NamedNativeQuery(
		name = "Upload.getUploadDTOForQCPendingInBatch", 
		query = " select u.id as uploadId,"
				+ "u.upload_url as uploadUrl,"
				+ "uvmom.id as userVendorMappingObjectMappingId,"
				+ "p.id as projectId, "
				+ "o.id as objectId, "
				+ "p.name as projectName, "
				+ "o.name as objectName,"
				+ "ucbi.id as ucbiId,"
				+ "ucbi.batch_number as batchNumber,"
				+ "u.name as fileName,"
				+ "ucbi.qc_status as qcStatus,  "
				+ "uvmom.consent_form_url as consentFormUrl  "
				+ "from upload_cqpm_batch_info ucbi,"
				+ "upload u,"
				+ "user_vendor_mapping_object_mapping uvmom,"
				+ "object o,"
				+ "project p "
				+ "where u.user_vendor_mapping_object_mapping_id=uvmom.id "
				+ "and uvmom.object_id=o.id "
				+ "and o.project_id=p.id "
				+ "and  ucbi.upload_id =u.id "
				+ "and ucbi.batch_number=?1 "
				+ "and ucbi.show_to_qc=1 "
				+ "order by u.id limit ?3 offset ?2", 
		resultSetMapping = "Mapping.getUploadDTOForQCInBatch")


@NamedNativeQuery(
		name = "Upload.getUploadDTOForQCInBatch", 
		query = "select u.id as uploadId, \n"
				+ "u.upload_url as uploadUrl, \n"
				+ "uvmom.id as userVendorMappingObjectMappingId, \n"
				+ "p.id as projectId, \n"
				+ "o.id as objectId, \n"
				+ "p.name as projectName, \n"
				+ "o.name as objectName,  \n"
				+ "ucbi.id as ucbiId,\n"
				+ "ucbi.batch_number as batchNumber,\n"
				+ "u.name as fileName,\n"
				+ "ucbi.qc_status as qcStatus  \n"
				+ "uvmom.consent_form_url as consentFormUrl  \n"
				+ "from upload_cqpm_batch_info ucbi,\n"
				+ "upload u,\n"
				+ "user_vendor_mapping_object_mapping uvmom,\n"
				+ "object o,\n"
				+ "project p \n"
				+ "where u.user_vendor_mapping_object_mapping_id=uvmom.id \n"
				+ "and uvmom.object_id=o.id \n"
				+ "and o.project_id=p.id \n"
				+ "and  ucbi.upload_id =u.id \n"
				+ "and ucbi.batch_number=?1 \n"
				+ "order by u.id \n", 
		resultSetMapping = "Mapping.getUploadDTOForQCInBatch")

@SqlResultSetMapping(
	name = "Mapping.getUploadDTOForQCInBatch", 
	classes = @ConstructorResult(targetClass = UploadDTOForQC.class, 
	columns = {
			@ColumnResult(name = "uploadId", type = Long.class), 
			@ColumnResult(name = "uploadUrl", type = String.class),
			@ColumnResult(name = "userVendorMappingObjectMappingId", type = Long.class), 
			@ColumnResult(name = "projectId", type = Long.class), 
			@ColumnResult(name = "objectId", type = Long.class), 
			@ColumnResult(name = "projectName", type = String.class),
			@ColumnResult(name = "objectName", type = String.class),
			@ColumnResult(name = "ucbiId", type = Long.class), 
			@ColumnResult(name = "batchNumber", type = Long.class),
			@ColumnResult(name = "fileName", type = String.class),
			@ColumnResult(name = "qcStatus", type = Integer.class),
			@ColumnResult(name = "consentFormUrl", type = String.class)
}))


/**
 * A AidasUpload.
 */
@Entity
@Table(name = "upload", indexes = {
		@Index(name = "idx_upload_uvmom", columnList = "user_vendor_mapping_object_mapping_id"),
		@Index(name = "idx_upload_upload", columnList = "rework_upload_id"),
		@Index(name = "idx_upload_qc_done_by", columnList = "qc_done_by_id") }, uniqueConstraints = {
				@UniqueConstraint(name = "uk_upload_uvumom", columnNames = { "object_key",
						"user_vendor_mapping_object_mapping_id" }) })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "upload")
@Audited
public class Upload extends AbstractAuditingEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name", length = 500, nullable = true)
	private String name;

	@Column(name = "upload_url", nullable = true)
	private String uploadUrl;

	@Column(name = "qc_batch_info", nullable = true)
	private String qcBatchInfo;

	@Column(name = "upload_etag", nullable = true)
	private String uploadEtag;

	@Column(name = "date_uploaded")
	private Instant dateUploaded;

	@ManyToOne(optional = true)
	@JsonIgnoreProperties(value = { "object" }, allowSetters = true)
	@JoinColumn(name = "rework_upload_id", nullable = true, foreignKey = @ForeignKey(name = "fk_upload_rework_upload"))
	private Upload reworkUpload;
	@Column(name = "status_modified_date")
	private ZonedDateTime statusModifiedDate;

	@OneToMany(mappedBy = "upload", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@Fetch(FetchMode.SUBSELECT)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JsonIgnoreProperties(value = { "upload" }, allowSetters = true)
	private Set<UploadRejectReasonMapping> uploadRejectReasonMappings = new HashSet<>();

	@Column(name = "object_key", nullable = true)
	private String objectKey;
	@OneToMany(mappedBy = "upload", cascade = CascadeType.ALL)
	@Fetch(FetchMode.SUBSELECT)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JsonIgnoreProperties(value = { "upload", "project", "object" }, allowSetters = true)
	private Set<UploadMetaData> uploadMetaDataSet = new HashSet<>();
	@Column(name = "status", nullable = false)
	private Integer status = 1;
	@Column(name = "approval_status", nullable = true)
	private Integer approvalStatus = 2;
	@Column(name = "qc_status", nullable = true)
	private Integer qcStatus = 2;
	@Column(name = "metadata_status", nullable = true)
	private Integer metadataStatus;
	@ManyToOne
	@JsonIgnoreProperties(value = { "user", "project" })
	@JsonIgnore
	@JoinColumn(name = "qc_done_by_id", nullable = true, foreignKey = @ForeignKey(name = "fk_upload_qc_done_by"))
	private CustomerQcProjectMapping qcDoneBy;
	@Column(name = "qc_start_date", nullable = true)
	private Instant qcStartDate;
	@Column(name = "qc_end_date", nullable = true)
	private Instant qcEndDate;
	@Column(name = "external_dataset_status")
	@JsonProperty
	private Integer externalDatasetStatus;
	@JsonProperty
	private transient String uploadMetaData;
	@Transient
	private Integer reworkCount;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "user_vendor_mapping_object_mapping_id", nullable = true, foreignKey = @ForeignKey(name = "fk_upload_uvmom"))
	private UserVendorMappingObjectMapping userVendorMappingObjectMapping;

	@Column(name = "current_qc_level", columnDefinition = "integer default 1")
	private Integer currentQcLevel = 1;
	@Column(name = "previous_qc_status", columnDefinition = "integer default 1")
	private Integer previouQcStatus = 2;

	public Integer getPreviouQcStatus() {
		return previouQcStatus;
	}

	public void setPreviouQcStatus(Integer previouQcStatus) {
		this.previouQcStatus = previouQcStatus;
	}

	@Column(name = "show_to_qc")
	private Integer showToQc = 1;

	public Integer getShowToQc() {
		return showToQc;
	}

	public void setShowToQc(Integer showToQc) {
		this.showToQc = showToQc;
	}

	@Column(name = "current_batch_number", columnDefinition = "integer default 1")
	private Integer currentBatchNumber = 0;

	public Integer getCurrentBatchNumber() {
		return currentBatchNumber;
	}

	public void setCurrentBatchNumber(Integer currentBatchNumber) {
		this.currentBatchNumber = currentBatchNumber;
	}

	public Integer getCurrentQcLevel() {
		return currentQcLevel;
	}

	public void setCurrentQcLevel(Integer currentQcLevel) {
		this.currentQcLevel = currentQcLevel;
	}

	public String getQcBatchInfo() {
		return qcBatchInfo;
	}

	public void setQcBatchInfo(String qcBatchInfo) {
		this.qcBatchInfo = qcBatchInfo;
	}

	@Transient
	@JsonProperty
	public List<UploadMetadataDTO> uploadMetaDatas = new ArrayList<>();

	public void setUploadMetaDatas(List<UploadMetadataDTO> uploadMetadatas) {
		this.uploadMetaDatas = uploadMetadatas;
	}

	public List<UploadMetadataDTO> getUploadMetaDatas() {
		return this.uploadMetaDatas;
	}

	public Upload getReworkAidasUpload() {
		return reworkUpload;
	}

	public void setReworkAidasUpload(Upload reworkUpload) {
		this.reworkUpload = reworkUpload;
	}

	public Integer getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(Integer approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public String getUploadMetaData() {
		return uploadMetaData;
	}

	public void setUploadMetaData(String uploadMetaData) {
		this.uploadMetaData = uploadMetaData;
	}

	public Integer getExternalDatasetStatus() {
		return externalDatasetStatus;
	}

	public void setExternalDatasetStatus(Integer externalDatasetStatus) {
		this.externalDatasetStatus = externalDatasetStatus;
	}

	public Upload getReworkUpload() {
		return reworkUpload;
	}

	public void setReworkUpload(Upload reworkUpload) {
		this.reworkUpload = reworkUpload;
	}

	public Set<UploadRejectReasonMapping> getUploadRejectMappings() {
		return uploadRejectReasonMappings;
	}

	public void setUploadRejectMappings(Set<UploadRejectReasonMapping> uploadRejectReasonMappings) {
		this.uploadRejectReasonMappings = uploadRejectReasonMappings;
	}

	public Set<UploadMetaData> getUploadMetaDataSet() {
		return uploadMetaDataSet;
	}

	public void setUploadMetaDataSet(Set<UploadMetaData> uploadMetaDataSet) {
		this.uploadMetaDataSet = uploadMetaDataSet;
	}

	public Integer getReworkCount() {
		return reworkCount;
	}

	public void setReworkCount(Integer reworkCount) {
		this.reworkCount = reworkCount;
	}

	public UserVendorMappingObjectMapping getUserVendorMappingObjectMapping() {
		return userVendorMappingObjectMapping;
	}

	public void setUserVendorMappingObjectMapping(UserVendorMappingObjectMapping userVendorMappingObjectMapping) {
		this.userVendorMappingObjectMapping = userVendorMappingObjectMapping;
	}

	public Integer getMetadataStatus() {
		return metadataStatus;
	}

	public void setMetadataStatus(Integer metadataStatus) {
		this.metadataStatus = metadataStatus;
	}

	@Override
	public Integer getStatus() {
		return status;
	}

	@Override
	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getQcStatus() {
		return qcStatus;
	}

	public void setQcStatus(Integer qcStatus) {
		this.qcStatus = qcStatus;
	}

	public CustomerQcProjectMapping getQcDoneBy() {
		return qcDoneBy;
	}

	public void setQcDoneBy(CustomerQcProjectMapping qcDoneBy) {
		this.qcDoneBy = qcDoneBy;
	}

	public Instant getQcStartDate() {
		return qcStartDate;
	}

	public void setQcStartDate(Instant qcStartDate) {
		this.qcStartDate = qcStartDate;
	}

	public Instant getQcEndDate() {
		return qcEndDate;
	}

	public void setQcEndDate(Instant qcEndDate) {
		this.qcEndDate = qcEndDate;
	}

	public String getObjectKey() {
		return objectKey;
	}

	public void setObjectKey(String objectKey) {
		this.objectKey = objectKey;
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
	}

	public String getUploadEtag() {
		return uploadEtag;
	}

	public void setUploadEtag(String uploadEtag) {
		this.uploadEtag = uploadEtag;
	}

// jhipster-needle-entity-add-field - JHipster will add fields here

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Upload id(Long id) {
		this.setId(id);
		return this;
	}

	public Upload status(Integer status) {
		this.setStatus(status);
		return this;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Upload name(String name) {
		this.setName(name);
		return this;
	}

	public Instant getDateUploaded() {
		return this.dateUploaded;
	}

	public void setDateUploaded(Instant dateUploaded) {
		this.dateUploaded = dateUploaded;
	}

	public Upload dateUploaded(Instant dateUploaded) {
		this.setDateUploaded(dateUploaded);
		return this;
	}

	public ZonedDateTime getStatusModifiedDate() {
		return this.statusModifiedDate;
	}

	public void setStatusModifiedDate(ZonedDateTime statusModifiedDate) {
		this.statusModifiedDate = statusModifiedDate;
	}

	public Upload statusModifiedDate(ZonedDateTime statusModifiedDate) {
		this.setStatusModifiedDate(statusModifiedDate);
		return this;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Upload)) {
			return false;
		}
		return id != null && id.equals(((Upload) o).id);
	}

	@Override
	public int hashCode() {
		// see
		// https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
		return getClass().hashCode();
	}

	// prettier-ignore
	@Override
	public String toString() {
		return "Upload{" + "id=" + getId() + ", name='" + getName() + "'" + ", object_key='" + getObjectKey() + "'"
				+ ", dateUploaded='" + getDateUploaded() + "'" + ", status='" + getStatus() + "'"
				+ ", statusModifiedDate='" + getStatusModifiedDate() + "'" + "}";
	}
}
