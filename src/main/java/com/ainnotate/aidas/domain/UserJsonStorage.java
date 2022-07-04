package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

/**
 * A AidasUserJsonStorage.
 */
@Entity
@Table(name = "user_json_storage",indexes = {
    @Index(name="idx_ujs_user",columnList = "user_id")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_ujs_user",columnNames={"user_id"})
    })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "userjsonstorage")
@Audited
public class UserJsonStorage extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "json_pay_load")
    private String jsonPayLoad;

    @ManyToOne
    @JsonIgnoreProperties(value = { "organisation", "customer", "vendor" }, allowSetters = true)
    @JoinColumn(name = "user_id", nullable = true, foreignKey = @ForeignKey(name="fk_ujs_user"))
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserJsonStorage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJsonPayLoad() {
        return this.jsonPayLoad;
    }

    public UserJsonStorage jsonPayLoad(String jsonPayLoad) {
        this.setJsonPayLoad(jsonPayLoad);
        return this;
    }

    public void setJsonPayLoad(String jsonPayLoad) {
        this.jsonPayLoad = jsonPayLoad;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserJsonStorage user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserJsonStorage)) {
            return false;
        }
        return id != null && id.equals(((UserJsonStorage) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AidasUserJsonStorage{" +
            "id=" + getId() +
            ", jsonPayLoad='" + getJsonPayLoad() + "'" +
            "}";
    }
}
