package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AidasUser.
 */
@Entity
@Table(name = "aidas_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "aidasuser")
public class AidasUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 100)
    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Size(min = 3, max = 100)
    @Column(name = "last_name", length = 100)
    private String lastName;

    @NotNull
    @Size(min = 5, max = 100)
    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(name = "locked", nullable = false)
    private Boolean locked;

    @NotNull
    @Size(min = 5, max = 20)
    @Column(name = "password", length = 20, nullable = false)
    private String password;

    @ManyToOne
    private AidasOrganisation aidasOrganisation;

    @ManyToOne
    @JsonIgnoreProperties(value = { "aidasOrganisation" }, allowSetters = true)
    private AidasCustomer aidasCustomer;

    @ManyToOne
    private AidasVendor aidasVendor;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AidasUser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public AidasUser firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public AidasUser lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public AidasUser email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getLocked() {
        return this.locked;
    }

    public AidasUser locked(Boolean locked) {
        this.setLocked(locked);
        return this;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public String getPassword() {
        return this.password;
    }

    public AidasUser password(String password) {
        this.setPassword(password);
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AidasOrganisation getAidasOrganisation() {
        return this.aidasOrganisation;
    }

    public void setAidasOrganisation(AidasOrganisation aidasOrganisation) {
        this.aidasOrganisation = aidasOrganisation;
    }

    public AidasUser aidasOrganisation(AidasOrganisation aidasOrganisation) {
        this.setAidasOrganisation(aidasOrganisation);
        return this;
    }

    public AidasCustomer getAidasCustomer() {
        return this.aidasCustomer;
    }

    public void setAidasCustomer(AidasCustomer aidasCustomer) {
        this.aidasCustomer = aidasCustomer;
    }

    public AidasUser aidasCustomer(AidasCustomer aidasCustomer) {
        this.setAidasCustomer(aidasCustomer);
        return this;
    }

    public AidasVendor getAidasVendor() {
        return this.aidasVendor;
    }

    public void setAidasVendor(AidasVendor aidasVendor) {
        this.aidasVendor = aidasVendor;
    }

    public AidasUser aidasVendor(AidasVendor aidasVendor) {
        this.setAidasVendor(aidasVendor);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AidasUser)) {
            return false;
        }
        return id != null && id.equals(((AidasUser) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AidasUser{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", locked='" + getLocked() + "'" +
            ", password='" + getPassword() + "'" +
            "}";
    }
}
