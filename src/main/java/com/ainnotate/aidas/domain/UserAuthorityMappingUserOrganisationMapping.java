package com.ainnotate.aidas.domain;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

/**
 * An authority (a security role) used by Spring Security.
 */
@Entity
@Table(name = "uam_uom_mapping",
    uniqueConstraints={
        @UniqueConstraint(name = "uk_uam_uom",columnNames={"uam_id","uom_id"})})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
@org.springframework.data.elasticsearch.annotations.Document(indexName = "uamucm")
public class UserAuthorityMappingUserOrganisationMapping extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "uam_id", nullable = true, foreignKey = @ForeignKey(name="fk_uam_id"))
    private UserAuthorityMapping userAuthorityMapping;
    
    @ManyToOne
    @JoinColumn(name = "uom_id", nullable = true, foreignKey = @ForeignKey(name="fk_uom_id"))
    private UserOrganisationMapping userOrganisationMapping;

    

    public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public UserAuthorityMapping getUserAuthorityMapping() {
		return userAuthorityMapping;
	}


	public void setUserAuthorityMapping(UserAuthorityMapping userAuthorityMapping) {
		this.userAuthorityMapping = userAuthorityMapping;
	}


	


	public UserOrganisationMapping getUserOrganisationMapping() {
		return userOrganisationMapping;
	}


	public void setUserOrganisationMapping(UserOrganisationMapping userOrganisationMapping) {
		this.userOrganisationMapping = userOrganisationMapping;
	}


	@Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAuthorityMappingUserOrganisationMapping)) {
            return false;
        }
        return Objects.equals(id, ((UserAuthorityMappingUserOrganisationMapping) o).id);
    }

   
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Authority{" +
            "name='" + id + '\'' +
            "}";
    }
}
