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
@Table(name = "uam_ucm_mapping",
    uniqueConstraints={
        @UniqueConstraint(name = "uk_uam_ucm",columnNames={"uam_id","ucm_id"})})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
@org.springframework.data.elasticsearch.annotations.Document(indexName = "uamucm")
public class UserAuthorityMappingUserCustomerMapping extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "uam_id", nullable = true, foreignKey = @ForeignKey(name="fk_uam_id"))
    private UserAuthorityMapping userAuthorityMapping;
    
    @ManyToOne
    @JoinColumn(name = "ucm_id", nullable = true, foreignKey = @ForeignKey(name="fk_ucm_id"))
    private UserCustomerMapping userCustomerMapping;

    

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


	public UserCustomerMapping getUserCustomerMapping() {
		return userCustomerMapping;
	}


	public void setUserCustomerMapping(UserCustomerMapping userCustomerMapping) {
		this.userCustomerMapping = userCustomerMapping;
	}


	@Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAuthorityMappingUserCustomerMapping)) {
            return false;
        }
        return Objects.equals(id, ((UserAuthorityMappingUserCustomerMapping) o).id);
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
