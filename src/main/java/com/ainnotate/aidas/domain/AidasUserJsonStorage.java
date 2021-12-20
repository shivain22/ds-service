package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AidasUserJsonStorage.
 */
@Entity
@Table(name = "aidas_user_json_storage")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "aidasuserjsonstorage")
public class AidasUserJsonStorage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "json_pay_load")
    private String jsonPayLoad;

    @ManyToOne
    @JsonIgnoreProperties(value = { "aidasOrganisation", "aidasCustomer", "aidasVendor" }, allowSetters = true)
    private AidasUser aidasUser;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AidasUserJsonStorage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJsonPayLoad() {
        return this.jsonPayLoad;
    }

    public AidasUserJsonStorage jsonPayLoad(String jsonPayLoad) {
        this.setJsonPayLoad(jsonPayLoad);
        return this;
    }

    public void setJsonPayLoad(String jsonPayLoad) {
        this.jsonPayLoad = jsonPayLoad;
    }

    public AidasUser getAidasUser() {
        return this.aidasUser;
    }

    public void setAidasUser(AidasUser aidasUser) {
        this.aidasUser = aidasUser;
    }

    public AidasUserJsonStorage aidasUser(AidasUser aidasUser) {
        this.setAidasUser(aidasUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AidasUserJsonStorage)) {
            return false;
        }
        return id != null && id.equals(((AidasUserJsonStorage) o).id);
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
