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
@Table(name = "uam_uvm_mapping",
    uniqueConstraints={
        @UniqueConstraint(name = "uk_uam_uvm",columnNames={"uam_id","uvm_id"})})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
@org.springframework.data.elasticsearch.annotations.Document(indexName = "uamucm")
public class UserAuthorityMappingUserVendorMapping extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "uam_id", nullable = true, foreignKey = @ForeignKey(name="fk_uam_id"))
    private UserAuthorityMapping userAuthorityMapping;
    
    @ManyToOne
    @JoinColumn(name = "uvm_id", nullable = true, foreignKey = @ForeignKey(name="fk_uvm_id"))
    private UserVendorMapping userVendorMapping;

    

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



	public UserVendorMapping getUserVendorMapping() {
		return userVendorMapping;
	}


	public void setUserVendorMapping(UserVendorMapping userVendorMapping) {
		this.userVendorMapping = userVendorMapping;
	}


	@Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAuthorityMappingUserVendorMapping)) {
            return false;
        }
        return Objects.equals(id, ((UserAuthorityMappingUserVendorMapping) o).id);
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
