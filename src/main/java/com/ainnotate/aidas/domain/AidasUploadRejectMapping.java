package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * A AidasUserAidasObjectMapping.
 */
@Entity
@Table(name = "aidas_upload_aidas_rej_reason")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "aidasupoadrejectmapping")
@Audited
public class AidasUploadRejectMapping extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "aidasUserAidasObjectMapping", "aidasCustomer", "aidasVendor" }, allowSetters = true)
    private AidasUpload aidasUpload;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "aidasProject" }, allowSetters = true)
    private AidasUploadRejectReason aidasUploadRejectReason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AidasUpload getAidasUpload() {
        return aidasUpload;
    }

    public void setAidasUpload(AidasUpload aidasUpload) {
        this.aidasUpload = aidasUpload;
    }

    public AidasUploadRejectReason getAidasUploadRejectReason() {
        return aidasUploadRejectReason;
    }

    public void setAidasUploadRejectReason(AidasUploadRejectReason aidasUploadRejectReason) {
        this.aidasUploadRejectReason = aidasUploadRejectReason;
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
