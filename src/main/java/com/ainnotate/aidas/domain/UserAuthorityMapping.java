package com.ainnotate.aidas.domain;

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

    // prettier-ignore
    @Override
    public String toString() {
        return "AidasQcProjectMapping{" +
            "name='" + id + '\'' +
            "}";
    }
}
