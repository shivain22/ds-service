package com.ainnotate.aidas.domain;

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
@Table(name = "aidas_user_aidas_org_mapping")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
public class AidasUserAidasOrganisationMapping extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    private AidasOrganisation aidasOrganisation;

    @ManyToOne
    private AidasUser aidasUser;



    public AidasOrganisation getAidasOrganisation() {
        return aidasOrganisation;
    }

    public void setAidasOrganisation(AidasOrganisation aidasOrganisation) {
        this.aidasOrganisation = aidasOrganisation;
    }

    public AidasUser getAidasUser() {
        return aidasUser;
    }

    public void setAidasUser(AidasUser aidasUser) {
        this.aidasUser = aidasUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AidasUserAidasOrganisationMapping)) {
            return false;
        }
        return Objects.equals(id, ((AidasUserAidasOrganisationMapping) o).id);
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

    // prettier-ignore
    @Override
    public String toString() {
        return "AidasQcProjectMapping{" +
            "name='" + id + '\'' +
            "}";
    }
}
