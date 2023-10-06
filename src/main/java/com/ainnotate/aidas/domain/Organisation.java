package com.ainnotate.aidas.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import com.ainnotate.aidas.dto.UploadMetadataDTO;
import com.ainnotate.aidas.dto.UserOrganisationMappingDTO;

/**
 * A AidasOrganisation.
 */
@Entity
@Table(name = "organisation"
    ,
    uniqueConstraints={
        @UniqueConstraint(name = "uk_organisation_name",columnNames={"name"})
    })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)


@NamedNativeQuery(name = "Organisation.getAllOrganisationsWithUamId",
query="select distinct o.id, o.name, uum.status \n"
		+ "from\n"
		+ "organisation  o,  user_organisation_mapping uom,uam_uom_mapping uum, user_authority_mapping uam\n"
		+ "where\n"
		+ "uum.uom_id=uom.id and uom.organisation_id=o.id\n"
		+ "and uom.user_id=?1 and uum.uam_id=uam.id and uam.authority_id=?2 and o.id>-1 and o.status=1\n"
		+ "union\n"
		+ "select o.id, o.name, 0 as status\n"
		+ "from\n"
		+ "organisation  o \n"
		+ "where\n"
		+ " o.id \n"
		+ "not in \n"
		+ "(select o.id\n"
		+ "from\n"
		+ "organisation o,  user_organisation_mapping uom,uam_uom_mapping uum,user_authority_mapping uam \n"
		+ "where\n"
		+ "uum.uom_id=uom.id and uom.organisation_id=o.id\n"
		+ "and uom.user_id=?1 and uum.uam_id=uam.id and uam.authority_id=?2 and o.status=1) and o.status=1",
		resultSetMapping = "Mapping.AuthorityOrganisationMappingDTO")

@NamedNativeQuery(name = "Organisation.getAllOrganisationsWithoutUamId",
query="select o.id as id, o.name as name, 0 as status from organisation o where o.id>0 and o.id not in (select uom.organisation_id from user_organisation_mapping uom where uom.user_id=?1)",
		resultSetMapping = "Mapping.AuthorityOrganisationMappingDTO")


@NamedNativeQuery(name = "Organisation.getAllOrganisationsWithUamIdAndOrgId",
query="select distinct o.id, o.name, uum.status \n"
		+ "from\n"
		+ "organisation  o,  user_organisation_mapping uom,uam_uom_mapping uum\n"
		+ "where\n"
		+ "uum.uom_id=uom.id and uom.organisation_id=o.id\n"
		+ "and uum.uam_id=?1 and o.id>-1 and o.id=?2 and uom.status=1 and uum.status=1 \n"
		+ "union\n"
		+ "select o.id, o.name, 0 as status\n"
		+ "from\n"
		+ "organisation  o \n"
		+ "where\n"
		+ "o.id>-1 and o.id=?2 and o.id \n"
		+ "not in \n"
		+ "(select o.id\n"
		+ "from\n"
		+ "organisation o,  user_organisation_mapping uom,uam_uom_mapping uum\n"
		+ "where\n"
		+ "uum.uom_id=uom.id and uom.organisation_id=o.id\n"
		+ "and uum.uam_id=?1 and o.id=?2 and o.id>-1 and uum.status=1 and uom.status=1) and o.status=1",
		resultSetMapping = "Mapping.AuthorityOrganisationMappingDTO")


@SqlResultSetMapping(
		name = "Mapping.AuthorityOrganisationMappingDTO", 
		classes = @ConstructorResult(targetClass = UserOrganisationMappingDTO.class, 
		columns = {
				@ColumnResult(name = "id", type = Long.class), 
				@ColumnResult(name = "name", type = String.class),
				@ColumnResult(name = "status", type = Integer.class)
	}))


@org.springframework.data.elasticsearch.annotations.Document(indexName = "organisation")
@Audited
public class Organisation extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;
    @OneToMany
    Set<AppProperty> appProperties=new HashSet<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @NotNull
    @Size(min = 3, max = 500)
    @Column(name = "name", length = 500, nullable = true, unique = false)
    private String name;
    @Column(name = "description")
    private String description;

    public Set<AppProperty> getAppProperties() {
        return appProperties;
    }

    public void setAppProperties(Set<AppProperty> appProperties) {
        this.appProperties = appProperties;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Organisation id(Long id) {
        this.setId(id);
        return this;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Organisation name(String name) {
        this.setName(name);
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Organisation description(String description) {
        this.setDescription(description);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Organisation)) {
            return false;
        }
        return id != null && id.equals(((Organisation) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Organisation{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
