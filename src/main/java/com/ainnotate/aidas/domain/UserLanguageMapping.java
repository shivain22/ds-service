package com.ainnotate.aidas.domain;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * An authority (a security role) used by Spring Security.
 */
@Entity
@Table(name = "user_language_mapping",
    uniqueConstraints={
        @UniqueConstraint(name = "uk_user_language",columnNames={"language_id","user_id"})})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
@org.springframework.data.elasticsearch.annotations.Document(indexName = "userLanguageMapping")
public class UserLanguageMapping extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "language_id", nullable = true, foreignKey = @ForeignKey(name="fk_ulm_language"))
    private Language language;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = true, foreignKey = @ForeignKey(name="fk_ulm_user"))
    private User user;

    public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserLanguageMapping)) {
            return false;
        }
        return Objects.equals(id, ((UserLanguageMapping) o).id);
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

    @Override
    public String toString() {
        return "Language{" +
            "name='" + language.getId()+" - "+language.getName() + '\'' +
            "}";
    }
}
