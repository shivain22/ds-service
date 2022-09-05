package com.ainnotate.aidas.domain;

import com.ainnotate.aidas.dto.UploadMetadataDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;

/**
 * A AidasUpload.
 */
@Entity
@Table(name = "upload",indexes = {
    @Index(name="idx_upload_uvmom",columnList = "user_vendor_mapping_object_mapping_id"),
    @Index(name="idx_upload_upload",columnList = "rework_upload_id"),
    @Index(name="idx_upload_qc_done_by",columnList = "qc_done_by_id")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_upload_uvumom",columnNames={"object_key", "user_vendor_mapping_object_mapping_id"})
    })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "upload")
@Audited
public class Upload extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @Column(name = "name", length = 500, nullable = true)
    private String name;

    @Column(name = "upload_url",  nullable = true)
    private String uploadUrl;

    @Column(name = "qc_batch_info",  nullable = true)
    private String qcBatchInfo;

    @Column(name = "upload_etag",  nullable = true)
    private String uploadEtag;

    @Column(name = "date_uploaded")
    private Instant dateUploaded;

    @ManyToOne(optional = true)
    @JsonIgnoreProperties(value = { "object" }, allowSetters = true)
    @JoinColumn(name = "rework_upload_id", nullable = true, foreignKey = @ForeignKey(name="fk_upload_rework_upload"))
    private Upload reworkUpload;
    @Column(name = "status_modified_date")
    private ZonedDateTime statusModifiedDate;

    @OneToMany(mappedBy = "upload",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "upload" }, allowSetters = true)
    private Set<UploadRejectReasonMapping> uploadRejectReasonMappings = new HashSet<>();

    @Column(name = "object_key",  nullable = true)
    private String objectKey;
    @OneToMany(mappedBy = "upload",fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "upload","project","object" }, allowSetters = true)
    private Set<UploadMetaData> uploadMetaDataSet = new HashSet<>();
    @Column(name="status", nullable=false)
    private Integer status;
    @Column(name="approval_status",nullable = true)
    private Integer approvalStatus;
    @Column(name="qc_status", nullable=true)
    private Integer qcStatus;
    @Column(name="metadata_status", nullable=true)
    private Integer metadataStatus;
    @ManyToOne
    @JsonIgnoreProperties(value={"user","project"})
    @JsonIgnore
    @JoinColumn(name = "qc_done_by_id", nullable = true, foreignKey = @ForeignKey(name="fk_upload_qc_done_by"))
    private CustomerQcProjectMapping qcDoneBy;
    @Column(name="qc_start_date", nullable=true)
    private Instant qcStartDate;
    @Column(name="qc_end_date", nullable=true)
    private Instant qcEndDate;
    @Column(name="external_dataset_status")
    @JsonProperty
    private Integer externalDatasetStatus;
    @JsonProperty
    private transient String uploadMetaData;
    @Transient
    private Integer reworkCount;
    @ManyToOne(optional = false,fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "user", "object", "aidasUploads" }, allowSetters = true)
    @JsonIgnore
    @JoinColumn(name = "user_vendor_mapping_object_mapping_id", nullable = true, foreignKey = @ForeignKey(name="fk_upload_uvmom"))
    private UserVendorMappingObjectMapping userVendorMappingObjectMapping;
    @Column(name ="current_qc_level",columnDefinition = "integer default 1")
    private Integer currentQcLevel;

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

    public List<UploadMetadataDTO> getUploadMetaDatas(){
        List<UploadMetadataDTO> uds = new LinkedList<>();
        if(this.uploadMetaDataSet!=null){
            UploadMetadataDTO ud1  = new UploadMetadataDTO();
            if(this.userVendorMappingObjectMapping!=null) {
                if(this.getUserVendorMappingObjectMapping().getObject()!=null) {
                    ud1.setValue(this.userVendorMappingObjectMapping.getObject().getName());
                    ud1.setName("Object Name");
                    uds.add(ud1);
                }
            }
            for(UploadMetaData u:uploadMetaDataSet){
                if(u.getProjectProperty()!=null && u.getProjectProperty().getProperty()!=null && u.getProjectProperty().getProperty().getName()!=null ){
                    UploadMetadataDTO ud = new UploadMetadataDTO();
                    ud.setName(u.getProjectProperty().getProperty().getName());
                    ud.setPropertyType(u.getProjectProperty().getProperty().getPropertyType());
                    ud.setProjectPropertyId(u.getProjectProperty().getId());
                    if(u.getValue()!=null) {
                        ud.setValue(u.getValue());
                    }
                    uds.add(ud);
                }else if(u.getObjectProperty()!=null && u.getObjectProperty().getProperty()!=null && u.getObjectProperty().getProperty().getName()!=null  && u.getValue()!=null){
                    UploadMetadataDTO ud = new UploadMetadataDTO();
                    ud.setName(u.getObjectProperty().getProperty().getName());
                    ud.setObjectPropertyId(u.getObjectProperty().getId());
                    ud.setPropertyType(u.getObjectProperty().getProperty().getPropertyType());
                    if(u.getValue()!=null) {
                        ud.setValue(u.getValue());
                    }
                    uds.add(ud);
                }
            }
            HashMap<String,UploadMetadataDTO> singleMap = new HashMap<>();
            for(UploadMetadataDTO umdd:uds){
                singleMap.put(umdd.getName(),umdd);
            }
            uds = new ArrayList<>();
            for(Map.Entry<String,UploadMetadataDTO> entry:singleMap.entrySet()){
                uds.add(entry.getValue());
            }
            uds.sort((o1, o2)-> o1.getName().compareTo(o2.getName()));
        }
        return uds;
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
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Upload{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", object_key='" + getObjectKey() + "'" +
            ", dateUploaded='" + getDateUploaded() + "'" +
            ", status='" + getStatus() + "'" +
            ", statusModifiedDate='" + getStatusModifiedDate() + "'" +
            "}";
    }
}
