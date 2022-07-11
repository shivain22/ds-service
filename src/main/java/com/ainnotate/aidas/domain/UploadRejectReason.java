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
@Table(name = "upload_reject_reason",indexes = {
    @Index(name="idx_urr_reason",columnList = "reason")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_urr_reason",columnNames={"reason"})
    })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
public class UploadRejectReason extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 500)
    @Column(length = 500)
    private String reason;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UploadRejectReason)) {
            return false;
        }
        return Objects.equals(reason, ((UploadRejectReason) o).reason);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(reason);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UploadRejectReason{id="+id+",reason="+this.reason+"}";
    }
}
