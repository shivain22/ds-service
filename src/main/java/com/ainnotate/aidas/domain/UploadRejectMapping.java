package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A AidasUserAidasObjectMapping.
 */
@Entity
@Table(name = "upload_reject_reason_mapping",indexes = {
    @Index(name="idx_urm_upload",columnList = "upload_id"),
    @Index(name="idx_urm_reject_reason",columnList = "upload_reject_reason_id")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_urm_up_urr",columnNames={"upload_id", "upload_reject_reason_id"})
    })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "uploadrejectmapping")
@Audited
public class UploadRejectMapping extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "aidasUserAidasObjectMapping", "customer", "vendor" }, allowSetters = true)
    @JoinColumn(name = "upload_id", nullable = true, foreignKey = @ForeignKey(name="fk_urm_upload"))
    private Upload upload;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "project" }, allowSetters = true)
    @JoinColumn(name = "upload_reject_reason_id", nullable = true, foreignKey = @ForeignKey(name="fk_urm_urr"))
    private UploadRejectReason uploadRejectReason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Upload getUpload() {
        return upload;
    }

    public void setUpload(Upload upload) {
        this.upload = upload;
    }

    public UploadRejectReason getAidasUploadRejectReason() {
        return uploadRejectReason;
    }

    public void setAidasUploadRejectReason(UploadRejectReason uploadRejectReason) {
        this.uploadRejectReason = uploadRejectReason;
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AidasUserAidasObjectMapping{" +
            "id=" + getId() +
            "}";
    }
}
