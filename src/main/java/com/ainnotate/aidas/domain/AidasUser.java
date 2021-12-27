package com.ainnotate.aidas.domain;

import com.ainnotate.aidas.config.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * A AidasUser.
 */
@Entity
@Table(name = "aidas_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "aidasuser")
public class AidasUser extends AbstractAuditingEntity implements Serializable {

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
    @Column(name = "email", length = 200, nullable = false, unique = true)
    private String email;


    @Size(min = 5, max = 100)
    @Column(name = "keycloak_id", length = 200, nullable = false, unique = true)
    private String keycloakId;

    @NotNull
    @Column(name = "locked", nullable = false)
    private Boolean locked;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Column(name = "password", length = 20, nullable = false)
    private String password;

    @ManyToOne
    private AidasOrganisation aidasOrganisation;

    @ManyToOne
    @JsonIgnoreProperties(value = { "aidasOrganisation" }, allowSetters = true)
    private AidasCustomer aidasCustomer;

    @ManyToOne
    private AidasVendor aidasVendor;

    @NotNull
    @Column(nullable = false)
    private boolean activated = false;

    @Size(min = 2, max = 10)
    @Column(name = "lang_key", length = 10)
    private String langKey;

    @Size(max = 256)
    @Column(name = "image_url", length = 256)
    private String imageUrl;



    @ManyToOne
    private AidasAuthority currentAidasAuthority;

    public AidasAuthority getCurrentAidasAuthority() {
        return currentAidasAuthority;
    }

    public void setCurrentAidasAuthority(AidasAuthority currentAidasAuthority) {
        this.currentAidasAuthority = currentAidasAuthority;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "aidas_user_aidas_authority",
        joinColumns = { @JoinColumn(name = "aidas_user_id", referencedColumnName = "id") },
        inverseJoinColumns = { @JoinColumn(name = "aidas_authority_id", referencedColumnName = "id") }
    )
    private Set<AidasAuthority> aidasAuthorities = new HashSet<>();



    @Size(min = 1, max = 50)
    @Column(length = 50, unique = true, nullable = false)
    private String login;

    public String getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }


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
    public String getLogin() {
        return login;
    }

    // Lowercase the login before saving it in database
    public void setLogin(String login) {
        this.login = StringUtils.lowerCase(login, Locale.ENGLISH);
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public Set<AidasAuthority> getAidasAuthorities() {
        return aidasAuthorities;
    }

    public void setAidasAuthorities(Set<AidasAuthority> aidasAuthorities) {
        this.aidasAuthorities = aidasAuthorities;
    }



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
