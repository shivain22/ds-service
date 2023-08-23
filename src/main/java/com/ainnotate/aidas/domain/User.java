package com.ainnotate.aidas.domain;

import com.ainnotate.aidas.dto.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Date;
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
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A AidasUser.
 */

@NamedNativeQuery(name = "User.findAllUsersOfVendorWithProject", query = "select  \n" + "u.first_name as firstName, \n"
		+ "u.last_name lastName,\n" + "u.login as login,\n" + "u.id as userId,\n" + "v.name as vendorName,\n"
		+ "uvm.id as userVendorMappingId,\n" + "v.id as vendorId,\n"
		+ "uvmpm.id as userVendorMappingProjectMappingId,\n" + "uvmpm.status as status\n"
		+ "from user_vendor_mapping_project_mapping uvmpm,\n" + "user_vendor_mapping uvm,\n" + "vendor v,\n"
		+ "user u \n" + "where\n" + "uvmpm.user_vendor_mapping_id=uvm.id\n" + "and uvm.user_id=u.id\n"
		+ "and uvm.vendor_id=v.id\n" + "and uvmpm.project_id=?1", resultSetMapping = "Mapping.UserDTO")
@SqlResultSetMapping(name = "Mapping.UserDTO", classes = @ConstructorResult(targetClass = UserDTO.class, columns = {
		@ColumnResult(name = "firstName", type = String.class), @ColumnResult(name = "lastName", type = String.class),
		@ColumnResult(name = "login", type = String.class), @ColumnResult(name = "vendorName", type = String.class),
		@ColumnResult(name = "userId", type = Long.class),
		@ColumnResult(name = "userVendorMappingId", type = Long.class),
		@ColumnResult(name = "vendorId", type = Long.class), @ColumnResult(name = "status", type = Integer.class),
		@ColumnResult(name = "userVendorMappingProjectMappingId", type = Long.class)

}))

@NamedNativeQuery(name = "User.findAllUsersOfVendorWithObject", query = "select  \n" + "u.first_name as firstName, \n"
		+ "u.last_name lastName,\n" + "u.login as login,\n" + "u.id as userId,\n" + "v.name as vendorName,\n"
		+ "uvm.id as userVendorMappingId,\n" + "v.id as vendorId,\n" + "uvmom.status as status\n"
		+ "FROM user_vendor_mapping_object_mapping uvmom,\n" + "user_vendor_mapping uvm,\n" + "vendor v,\n"
		+ "user u \n" + "where\n" + "uvmom.user_vendor_mapping_id=uvm.id\n" + "and uvm.user_id=u.id\n"
		+ "and uvm.vendor_id=v.id\n" + "and uvmom.object_id=?1", resultSetMapping = "Mapping.UserObjectMappingDTO")
@SqlResultSetMapping(name = "Mapping.UserObjectMappingDTO", classes = @ConstructorResult(targetClass = UserDTO.class, columns = {
		@ColumnResult(name = "firstName", type = String.class), @ColumnResult(name = "lastName", type = String.class),
		@ColumnResult(name = "login", type = String.class), @ColumnResult(name = "vendorName", type = String.class),
		@ColumnResult(name = "userId", type = Long.class),
		@ColumnResult(name = "userVendorMappingId", type = Long.class),
		@ColumnResult(name = "vendorId", type = Long.class), @ColumnResult(name = "status", type = Integer.class)

}))

@Entity
@Table(name = "user", indexes = { @Index(name = "idx_user_organisation", columnList = "organisation_id"),
		@Index(name = "idx_user_customer", columnList = "customer_id"),
		@Index(name = "idx_user_vendor", columnList = "vendor_id"),
		@Index(name = "idx_user_authority", columnList = "authority_id") }, uniqueConstraints = {
				@UniqueConstraint(name = "uk_user_email", columnNames = { "email" }),
				@UniqueConstraint(name = "uk_user_login", columnNames = { "login" }) })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "user")
@Audited
public class User extends AbstractAuditingEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	@OneToMany
	Set<AppProperty> appProperties = new HashSet<>();
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@NotNull
	@Size(min = 3, max = 100)
	@Column(name = "first_name", nullable = true)
	private String firstName;
	@Column(name = "last_name")
	private String lastName;
	@NotNull
	@Email
	@Column(name = "email", nullable = true, unique = false)
	private String email;
	@OneToMany
	Set<UserLanguageMapping> userLanguages = new HashSet<>();
	@Column(name = "alt_email", nullable = true, unique = false)
	private String altEmail;
	@Column(name = "gender", nullable = true, unique = false)
	private String gender;
	@Column(name = "cc_email", nullable = true, unique = false)
	private String ccEmails;
	@Column(name = "dob", nullable = true, unique = false)
	private Date dob;
	@Column(name = "keycloak_id", nullable = true, unique = false)
	private String keycloakId;
	@NotNull
	@Column(name = "locked", nullable = true, columnDefinition = "integer default 0")
	private Integer locked;
	@Column(name = "deleted", nullable = true, columnDefinition = "integer default 0")
	private Integer deleted;
	@Column(name = "password", length = 20, nullable = true)
	private String password;
	@ManyToOne
	@JoinColumn(name = "organisation_id", nullable = true, foreignKey = @ForeignKey(name = "fk_user_organisation"))
	private Organisation organisation;
	@ManyToOne
	@Field(type = FieldType.Nested)
	@JsonIgnoreProperties(value = { "organisation" }, allowSetters = true)
	@JoinColumn(name = "customer_id", nullable = true, foreignKey = @ForeignKey(name = "fk_user_customer"))
	private Customer customer;
	@ManyToOne
	@Field(type = FieldType.Nested)
	@JoinColumn(name = "vendor_id", nullable = true, foreignKey = @ForeignKey(name = "fk_user_vendor"))
	private Vendor vendor;
	@NotNull
	@Column(nullable = true, columnDefinition = "integer default 1")
	private Integer activated = 1;
	@Column(name = "lang_key", length = 100)
	private String langKey;
	@Column(name = "country", length = 100)
	private String country;
	@Column(name = "state", length = 100)
	private String state;
	@Column(name = "time_zone", length = 100)
	private String time_zone;
	@Column(name = "mobile_number", length = 100)
	private String mobileNumber;
	
	
	
	@ManyToOne
	@JoinColumn(name = "parent_organisation_id", nullable = true, foreignKey = @ForeignKey(name = "fk1_user_organisation"))
	private Organisation parentOrganisation;
	@ManyToOne
	@Field(type = FieldType.Nested)
	@JsonIgnoreProperties(value = { "organisation" }, allowSetters = true)
	@JoinColumn(name = "parent_customer_id", nullable = true, foreignKey = @ForeignKey(name = "fk1_user_customer"))
	private Customer parentCustomer;
	@ManyToOne
	@Field(type = FieldType.Nested)
	@JoinColumn(name = "parent_vendor_id", nullable = true, foreignKey = @ForeignKey(name = "fk1_user_vendor"))
	private Vendor parentVendor;
	
	@Column(name = "show_to_all")
	private Integer showToAll;
	
	public Integer getShowToAll() {
		return showToAll;
	}

	public void setShowToAll(Integer showToAll) {
		this.showToAll = showToAll;
	}

	public Organisation getParentOrganisation() {
		return parentOrganisation;
	}

	public void setParentOrganisation(Organisation parentOrganisation) {
		this.parentOrganisation = parentOrganisation;
	}

	public Customer getParentCustomer() {
		return parentCustomer;
	}

	public void setParentCustomer(Customer parentCustomer) {
		this.parentCustomer = parentCustomer;
	}

	public Vendor getParentVendor() {
		return parentVendor;
	}

	public void setParentVendor(Vendor parentVendor) {
		this.parentVendor = parentVendor;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTime_zone() {
		return time_zone;
	}

	public void setTime_zone(String time_zone) {
		this.time_zone = time_zone;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	@Column(name = "image_url", length = 256)
	private String imageUrl;
	@ManyToOne
	@Field(type = FieldType.Nested)
	@JoinColumn(name = "authority_id", nullable = true, foreignKey = @ForeignKey(name = "fk_user_authority"))
	private Authority authority;
	
	@Size(min = 1, max = 50)
	@Column(length = 50, unique = false, nullable = true)
	private String login;
	@Transient
	@JsonProperty
	private transient List<UserLanguageMappingDTO> languageIds;

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public List<UserLanguageMappingDTO> getLanguageIds() {
		return languageIds;
	}

	public void setLanguageIds(List<UserLanguageMappingDTO> languageIds) {
		this.languageIds = languageIds;
	}

	@Transient
	@JsonProperty
	private transient List<UserCustomerMappingDTO> adminCustomerDtos;
	@Transient
	@JsonProperty
	private transient List<UserCustomerMappingDTO> qcCustomerDtos;
	@Transient
	@JsonProperty
	private transient List<UserVendorMappingDTO> adminVendorDtos;

	@Transient
	@JsonProperty
	private transient List<UserVendorMappingDTO> qcVendorDtos;

	@Transient
	@JsonProperty
	private transient List<UserVendorMappingDTO> userVendorDtos;
	@Transient
	@JsonProperty
	private transient List<UserAuthorityMappingDTO> authorityDtos;

	@Transient
	@JsonProperty
	private transient List<UserOrganisationMappingDTO> adminOrgDtos;

	@Transient
	@JsonProperty
	private transient List<UserOrganisationMappingDTO> qcOrgDtos;

	@Transient
	@JsonProperty
	private transient List<UserOrganisationMappingDTO> qcAdminDtos;

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

	public Set<UserLanguageMapping> getUserLanguages() {
		return userLanguages;
	}

	public void setUserLanguages(Set<UserLanguageMapping> userLanguages) {
		this.userLanguages = userLanguages;
	}

	public String getKeycloakId() {
		return keycloakId;
	}

	public void setKeycloakId(String keycloakId) {
		this.keycloakId = keycloakId;
	}

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

	public void setLogin(String login) {
		this.login = StringUtils.lowerCase(login, Locale.ENGLISH);
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public List<UserCustomerMappingDTO> getAdminCustomerDtos() {
		return adminCustomerDtos;
	}

	public void setAdminCustomerDtos(List<UserCustomerMappingDTO> adminCustomerDtos) {
		this.adminCustomerDtos = adminCustomerDtos;
	}

	public List<UserCustomerMappingDTO> getQcCustomerDtos() {
		return qcCustomerDtos;
	}

	public void setQcCustomerDtos(List<UserCustomerMappingDTO> qcCustomerDtos) {
		this.qcCustomerDtos = qcCustomerDtos;
	}

	public List<UserVendorMappingDTO> getAdminVendorDtos() {
		return adminVendorDtos;
	}

	public void setAdminVendorDtos(List<UserVendorMappingDTO> adminVendorDtos) {
		this.adminVendorDtos = adminVendorDtos;
	}

	public List<UserVendorMappingDTO> getQcVendorDtos() {
		return qcVendorDtos;
	}

	public void setQcVendorDtos(List<UserVendorMappingDTO> qcVendorDtos) {
		this.qcVendorDtos = qcVendorDtos;
	}

	public List<UserVendorMappingDTO> getUserVendorDtos() {
		return userVendorDtos;
	}

	public void setUserVendorDtos(List<UserVendorMappingDTO> userVendorDtos) {
		this.userVendorDtos = userVendorDtos;
	}

	public List<UserAuthorityMappingDTO> getAuthorityDtos() {
		return authorityDtos;
	}

	public void setAuthorityDtos(List<UserAuthorityMappingDTO> authorityDtos) {
		this.authorityDtos = authorityDtos;
	}

	public List<UserOrganisationMappingDTO> getAdminOrgDtos() {
		return adminOrgDtos;
	}

	public void setAdminOrgDtos(List<UserOrganisationMappingDTO> adminOrgDtos) {
		this.adminOrgDtos = adminOrgDtos;
	}

	public List<UserOrganisationMappingDTO> getQcOrgDtos() {
		return qcOrgDtos;
	}

	public void setQcOrgDtos(List<UserOrganisationMappingDTO> qcOrgDtos) {
		this.qcOrgDtos = qcOrgDtos;
	}

	public List<UserOrganisationMappingDTO> getQcAdminDtos() {
		return qcAdminDtos;
	}

	public void setQcAdminDtos(List<UserOrganisationMappingDTO> qcAdminDtos) {
		this.qcAdminDtos = qcAdminDtos;
	}

	public String getLangKey() {
		return langKey;
	}

	public void setLangKey(String langKey) {
		this.langKey = langKey;
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
		return "User{" + "id=" + getId() + ", firstName='" + getFirstName() + "'" + ", lastName='" + getLastName() + "'"
				+ ", email='" + getEmail() + "'" + ", locked='" + getLocked() + "'" + ", password='" + getPassword()
				+ "'" + "}";
	}
}
