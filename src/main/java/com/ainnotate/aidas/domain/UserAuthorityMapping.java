package com.ainnotate.aidas.domain;

import com.ainnotate.aidas.dto.UserAuthorityMappingDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "user_authority_mapping",indexes = {
    @Index(name="idx_uam_user",columnList = "user_id"),
    @Index(name="idx_uam_authority",columnList = "authority_id")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_uam_user_authority",columnNames={"user_id", "authority_id"})
    })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited



@NamedNativeQuery(name = "UserAuthorityMapping.getAllAuthoritiesOfUser",
query="select uam.id, a.name, uam.status \n"
		+ "from\n"
		+ "authority a, user_authority_mapping uam\n"
		+ "where\n"
		+ "uam.authority_id=a.id \n"
		+ "and uam.user_id=?1\n"
		+ "union\n"
		+ "select -2 , a.name, 0 as status\n"
		+ "from\n"
		+ "authority a \n"
		+ "where\n"
		+ "a.id \n"
		+ "not in \n"
		+ "(select authority_id from user_authority_mapping uam where user_id=?1)",
		resultSetMapping = "Mapping.AuthorityMappingDTO")

@SqlResultSetMapping(
		name = "Mapping.AuthorityMappingDTO", 
		classes = @ConstructorResult(targetClass = UserAuthorityMappingDTO.class, 
		columns = {
				@ColumnResult(name = "id", type = Long.class), 
				@ColumnResult(name = "name", type = String.class),
				@ColumnResult(name = "status", type = Integer.class)
	}))



public class UserAuthorityMapping extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAuthorityMapping)) return false;
        UserAuthorityMapping that = (UserAuthorityMapping) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getAuthority(), that.getAuthority()) && Objects.equals(getUser(), that.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAuthority(), getUser());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "authority_id", nullable = true, foreignKey = @ForeignKey(name="fk_uam_authority"))
    private Authority authority;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = true, foreignKey = @ForeignKey(name="fk_uam_user"))
    private User user;

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    public Authority getAuthority() {
        return authority;
    }

    public void getAuthority(Authority authority) {
        this.authority = authority;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "UserAuthorityMapping{id="+id+",user_id="+this.user.getId()+",authority_id="+this.authority.getId()+"}";
    }
}
