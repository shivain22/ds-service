package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

/**
 * A AidasUser.
 */
@Entity
@Table(name = "user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "aidasuser")
@Audited
public class User extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @OneToMany
    Set<AppProperty> aidasAppProperties=new HashSet<>();
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
    @Column(name = "password", length = 20, nullable = false)
    private String password;
    @ManyToOne
    private Organisation organisation;
    @ManyToOne
    @JsonIgnoreProperties(value = { "organisation" }, allowSetters = true)
    private Customer customer;
    @ManyToOne
    private Vendor vendor;
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
    private Authority currentAuthority;
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Set<UserAuthorityMapping> userAuthorityMappings = new HashSet<>();
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Set<UserOrganisationMapping> userOrganisationMappings = new HashSet<>();
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Set<UserCustomerMapping> userCustomerMappings = new HashSet<>();
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Set<UserVendorMapping> userVendorMappings = new HashSet<>();
    private transient Set<Authority> aidasAuthorities = new HashSet<>();
    private transient Set<Organisation> organisations = new HashSet<>();
    private transient Set<Customer> customers = new HashSet<>();
    private transient Set<Vendor> vendors = new HashSet<>();
    @Size(min = 1, max = 50)
    @Column(length = 50, unique = true, nullable = false)
    private String login;

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Set<AppProperty> getAppProperty() {
        return aidasAppProperties;
    }

    public void setAppProperty(Set<AppProperty> aidasAppProperties) {
        this.aidasAppProperties = aidasAppProperties;
    }

    public Authority getCurrentAidasAuthority() {
        return currentAuthority;
    }

    public void setCurrentAidasAuthority(Authority currentAuthority) {
        this.currentAuthority = currentAuthority;
    }

    public Set<UserAuthorityMapping> getAidasUserAidasAuthorityMappings() {
        return userAuthorityMappings;
    }

    public void setAidasUserAidasAuthorityMappings(Set<UserAuthorityMapping> userAuthorityMappings) {
        this.userAuthorityMappings = userAuthorityMappings;
    }

    public Set<UserOrganisationMapping> getAidasUserAidasOrganisationMappings() {
        return userOrganisationMappings;
    }

    public void setAidasUserAidasOrganisationMappings(Set<UserOrganisationMapping> userOrganisationMappings) {
        this.userOrganisationMappings = userOrganisationMappings;
    }

    public Set<UserCustomerMapping> getAidasUserAidasCustomerMappings() {
        return userCustomerMappings;
    }

    public void setAidasUserAidasCustomerMappings(Set<UserCustomerMapping> userCustomerMappings) {
        this.userCustomerMappings = userCustomerMappings;
    }

    public Set<UserVendorMapping> getAidasUserAidasVendorMappings() {
        return userVendorMappings;
    }

    public void setAidasUserAidasVendorMappings(Set<UserVendorMapping> userVendorMappings) {
        this.userVendorMappings = userVendorMappings;
    }

    public Set<Organisation> getOrganisations() {
        if(userOrganisationMappings !=null && userOrganisationMappings.size()>0){
            for(UserOrganisationMapping auaom: userOrganisationMappings){
                organisations.add(auaom.getOrganisation());
            }
        }
        return organisations;
    }

    public void setOrganisations(Set<Organisation> organisations) {
        this.organisations = organisations;
    }

    public Set<Customer> getCustomers() {
        if(userCustomerMappings !=null && userCustomerMappings.size()>0){
            for(UserCustomerMapping auacm: userCustomerMappings){
                customers.add(auacm.getCustomer());
            }
        }
        return customers;
    }

    public void setCustomers(Set<Customer> customers) {
        this.customers = customers;
    }

    public Set<Vendor> getVednors() {
        if(userVendorMappings !=null && userVendorMappings.size()>0){
            for(UserVendorMapping auavm: userVendorMappings){
                vendors.add(auavm.getVendor());
            }
        }
        return vendors;
    }

    public void setVendors(Set<Vendor> vendors) {
        this.vendors = vendors;
    }

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

    public void setId(Long id) {
        this.id = id;
    }

    public User id(Long id) {
        this.setId(id);
        return this;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public User firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public User lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User email(String email) {
        this.setEmail(email);
        return this;
    }

    public Boolean getLocked() {
        return this.locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public User locked(Boolean locked) {
        this.setLocked(locked);
        return this;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User password(String password) {
        this.setPassword(password);
        return this;
    }

    public Organisation getOrganisation() {
        return this.organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public User organisation(Organisation organisation) {
        this.setOrganisation(organisation);
        return this;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public User customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public Vendor getVendor() {
        return this.vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public User vendor(Vendor vendor) {
        this.setVendor(vendor);
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

    public Set<Authority> getAidasAuthorities() {
        if(userAuthorityMappings !=null && userAuthorityMappings.size()>0){
            for(UserAuthorityMapping auaam: userAuthorityMappings){
                aidasAuthorities.add(auaam.getAidasAuthority());
            }
        }
        return aidasAuthorities;
    }

    public void setAidasAuthorities(Set<Authority> aidasAuthorities) {
        this.aidasAuthorities = aidasAuthorities;
    }



    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        return id != null && id.equals(((User) o).id);
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
