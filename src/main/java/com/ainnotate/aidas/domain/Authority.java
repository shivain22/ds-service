package com.ainnotate.aidas.domain;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import com.ainnotate.aidas.dto.AuthorityDTO;
import com.ainnotate.aidas.dto.UserAuthorityMappingDTO;
import com.ainnotate.aidas.dto.UserCustomerMappingDTO;

/**
 * An authority (a security role) used by Spring Security.
 */
@Entity
@Table(name = "authority",indexes = {
    @Index(name="idx_authority_name",columnList = "name")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_authority_name",columnNames={"name"})})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited

@NamedNativeQuery(name = "Authority.getAllAuthorityForRoleAssignment",
query=" select a.id as id, a.name as name, a.status as status from authority a where a.entity_id>=?1 ",
		resultSetMapping = "Mapping.AuthorityDTO")

@SqlResultSetMapping(
		name = "Mapping.AuthorityDTO", 
		classes = @ConstructorResult(targetClass = AuthorityDTO.class, 
		columns = {
				@ColumnResult(name = "id", type = Long.class), 
				@ColumnResult(name = "status", type = Integer.class),
				@ColumnResult(name = "name", type = String.class)
	}))



@org.springframework.data.elasticsearch.annotations.Document(indexName = "authority")
public class Authority extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(length = 50)
    private String name;
    
    @Column(name="entity_id")
    private Integer entityId;
    
    public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	private transient boolean lastLoggedInRole;

    public boolean isLastLoggedInRole() {
		return lastLoggedInRole;
	}

	public void setLastLoggedInRole(boolean lastLoggedInRole) {
		this.lastLoggedInRole = lastLoggedInRole;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Authority)) {
            return false;
        }
        return Objects.equals(name, ((Authority) o).name);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public String toString() {
        return "Authority{" +
            "name='" + name + '\'' +
            "}";
    }
}
