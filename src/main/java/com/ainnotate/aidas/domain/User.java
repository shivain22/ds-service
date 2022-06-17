package com.ainnotate.aidas.domain;

import com.ainnotate.aidas.dto.UserAuthorityMappingDTO;
import com.ainnotate.aidas.dto.UserCustomerMappingDTO;
import com.ainnotate.aidas.dto.UserOrganisationMappingDTO;
import com.ainnotate.aidas.dto.UserVendorMappingDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @OneToMany(fetch = FetchType.EAGER)
    Set<AppProperty> appProperties=new HashSet<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @NotNull
    @Size(min = 3, max = 100)
    @Column(name = "first_name",  nullable = true)
    private String firstName;
    @Column(name = "last_name" )
    private String lastName;
    @NotNull
    @Column(name = "email",  nullable = true, unique = false)
    private String email;


    @Column(name = "alt_email",  nullable = true, unique = false)
    private String altEmail;


    @Column(name = "cc_email",  nullable = true, unique = false)
    private String ccEmails;

    @Column(name = "keycloak_id", nullable = true, unique = false)
    private String keycloakId;
    @NotNull
    @Column(name = "locked", nullable = true,columnDefinition ="integer default 0" )
    private Integer locked;
    @Column(name = "deleted", nullable = true, columnDefinition ="integer default 0")
    private Integer deleted;
    @Column(name = "password", length = 20, nullable = true)
    private String password;
    @ManyToOne
    private Organisation organisation;
    @ManyToOne
    @JsonIgnoreProperties(value = { "organisation" }, allowSetters = true)
    private Customer customer;
    @ManyToOne
    private Vendor vendor;
    @NotNull
    @Column(nullable = true,columnDefinition = "integer default 1")
    private Integer activated = 1;

    @Column(name = "lang_key", length = 10)
    private String langKey;

    @Column(name = "image_url", length = 256)
    private String imageUrl;
    @ManyToOne(fetch = FetchType.EAGER)
    private Authority authority;

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

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

    private transient Set<Authority> authorities = new HashSet<>();
    private transient Set<Organisation> organisations = new HashSet<>();
    private transient Set<Customer> customers = new HashSet<>();
    private transient Set<Vendor> vendors = new HashSet<>();
    @Size(min = 1, max = 50)
    @Column(length = 50, unique = false, nullable = true)
    private String login;

    @Transient
    @JsonProperty
    private transient List<UserOrganisationMappingDTO> organisationIds;

    @Transient
    @JsonProperty
    private transient List<UserCustomerMappingDTO> customerIds;
    @Transient
    @JsonProperty
    private transient List<UserVendorMappingDTO> vendorIds;

    @Transient
    @JsonProperty
    private transient List<UserAuthorityMappingDTO> authorityIds;

    public List<UserOrganisationMappingDTO> getOrganisationIds() {
        return organisationIds;
    }

    public String getAltEmail() {
        return altEmail;
    }

    public void setAltEmail(String altEmail) {
        this.altEmail = altEmail;
    }

    public String getCcEmails() {
        return ccEmails;
    }

    public void setCcEmails(String ccEmails) {
        this.ccEmails = ccEmails;
    }

    public void setOrganisationIds(List<UserOrganisationMappingDTO> organisationIds) {
        this.organisationIds = organisationIds;
    }

    public List<UserCustomerMappingDTO> getCustomerIds() {
        return customerIds;
    }

    public void setCustomerIds(List<UserCustomerMappingDTO> customerIds) {
        this.customerIds = customerIds;
    }

    public List<UserVendorMappingDTO> getVendorIds() {
        return vendorIds;
    }

    public void setVendorIds(List<UserVendorMappingDTO> vendorIds) {
        this.vendorIds = vendorIds;
    }

    public List<UserAuthorityMappingDTO> getAuthorityIds() {
        return authorityIds;
    }

    public void setAuthorityIds(List<UserAuthorityMappingDTO> authorityIds) {
        this.authorityIds = authorityIds;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Set<AppProperty> getAppProperties() {
        return appProperties;
    }

    public void setAppProperties(Set<AppProperty> appProperties) {
        this.appProperties = appProperties;
    }

    public Authority getAuthority() {
        return authority;
    }

    public void getAuthority(Authority authority) {
        this.authority = authority;
    }

    public Set<UserAuthorityMapping> getUserAuthorityMappings() {
        return userAuthorityMappings;
    }

    public void setUserAuthorityMappings(Set<UserAuthorityMapping> userAuthorityMappings) {
        this.userAuthorityMappings = userAuthorityMappings;
    }

    public Set<UserOrganisationMapping> getUserOrganisationMappings() {
        return userOrganisationMappings;
    }

    public void setUserOrganisationMappings(Set<UserOrganisationMapping> userOrganisationMappings) {
        this.userOrganisationMappings = userOrganisationMappings;
    }

    public Set<UserCustomerMapping> getUserCustomerMappings() {
        return userCustomerMappings;
    }

    public void setUserCustomerMappings(Set<UserCustomerMapping> userCustomerMappings) {
        this.userCustomerMappings = userCustomerMappings;
    }

    public Set<UserVendorMapping> getUserVendorMappings() {
        return userVendorMappings;
    }

    public void setUserVendorMappings(Set<UserVendorMapping> userVendorMappings) {
        this.userVendorMappings = userVendorMappings;
    }

    public Set<Vendor> getVendors() {
        return vendors;
    }

    public Set<Organisation> getOrganisations() {
        if(userOrganisationMappings !=null && userOrganisationMappings.size()>0){
            for(UserOrganisationMapping auaom: userOrganisationMappings){
                if(auaom.getStatus().equals(1))
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
                if(auacm.getStatus().equals(1))
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
                if(auavm.getStatus().equals(1))
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



    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public Set<Authority> getAuthorities() {
        if(userAuthorityMappings !=null && userAuthorityMappings.size()>0){
            for(UserAuthorityMapping auaam: userAuthorityMappings){
                if(auaam.getStatus().equals(1))
                    authorities.add(auaam.getAuthority());
            }
        }
        return authorities;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
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


    public Integer getLocked() {
        return locked;
    }

    public void setLocked(Integer locked) {
        this.locked = locked;
    }

    public Integer getActivated() {
        return activated;
    }

    public void setActivated(Integer activated) {
        this.activated = activated;
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
