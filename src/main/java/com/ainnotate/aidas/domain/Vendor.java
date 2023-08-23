package com.ainnotate.aidas.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.ainnotate.aidas.dto.UserCustomerMappingDTO;
import com.ainnotate.aidas.dto.UserVendorMappingDTO;
import com.ainnotate.aidas.dto.VendorCustomerMappingDTO;
import com.ainnotate.aidas.dto.VendorOrganisationMappingDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A AidasVendor.
 */
@Entity
@Table(name = "vendor")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "vendor")
@Audited

@NamedNativeQuery(name = "Vendor.getAllVendorsWithUamId",
query="select distinct v.id, v.name, uum.status \n"
		+ "from\n"
		+ "vendor  v,  user_vendor_mapping uvm,uam_uvm_mapping uum, user_authority_mapping uam \n"
		+ "where\n"
		+ "uum.uvm_id=uvm.id and uvm.vendor_id=v.id and v.id>-1\n"
		+ "and uvm.user_id=?1 and uum.uam_id=uam.id and uam.authority_id=?2 \n"
		+ "union\n"
		+ "select v.id, v.name, 0 as status\n"
		+ "from\n"
		+ "vendor  v \n"
		+ "where\n"
		+ "v.id>-1 and v.id \n"
		+ "not in \n"
		+ "(select v.id\n"
		+ "from\n"
		+ "vendor  v,  user_vendor_mapping uvm,uam_uvm_mapping uum ,user_authority_mapping uam \n"
		+ "where\n"
		+ "uum.uvm_id=uvm.id and uvm.vendor_id=v.id and uum.uam_id=uam.id and uam.authority_id=?2\n"
		+ "and uvm.user_id=?1)",
		resultSetMapping = "Mapping.AuthorityVendorMappingDTO")

@NamedNativeQuery(name = "Vendor.getAllVendorsWithoutUamId",
query="select v.id as id, v.name as name, 0 as status from vendor v where v.id>0 and v.id not in (select uvm.vendor_id from user_vendor_mapping uvm where uvm.user_id=?1)",
		resultSetMapping = "Mapping.AuthorityVendorMappingDTO")

@NamedNativeQuery(name = "Vendor.getAllVendorsOfOrganisation",
query="select  v.id, v.name, v.status \n"
		+ "from\n"
		+ "vendor  v,vendor_organisation_mapping vom where vom.vendor_id=v.id and vom.organisation_id=?1",
		resultSetMapping = "Mapping.AuthorityVendorMappingDTO")


@NamedNativeQuery(name = "Vendor.getAllVendorsOfCustomer",
query="select  v.id, v.name, v.status \n"
		+ "from\n"
		+ "vendor  v,vendor_customer_mapping vcm where vcm.vendor_id=v.id and vcm.customer_id=?1",
		resultSetMapping = "Mapping.AuthorityVendorMappingDTO")

@SqlResultSetMapping(
		name = "Mapping.AuthorityVendorMappingDTO", 
		classes = @ConstructorResult(targetClass = UserVendorMappingDTO.class, 
		columns = {
				@ColumnResult(name = "id", type = Long.class), 
				@ColumnResult(name = "name", type = String.class),
				@ColumnResult(name = "status", type = Integer.class)
	}))


@NamedNativeQuery(name = "Vendor.getAllCustomers",
query="select c.id,c.name,vcm.status from vendor_customer_mapping vcm, customer c where vcm.customer_id=c.id and vcm.vendor_id=?1",
		resultSetMapping = "Mapping.VendorCustomerMappingDTO")

@SqlResultSetMapping(
		name = "Mapping.VendorCustomerMappingDTO", 
		classes = @ConstructorResult(targetClass = VendorCustomerMappingDTO.class, 
		columns = {
				@ColumnResult(name = "id", type = Long.class), 
				@ColumnResult(name = "name", type = String.class),
				@ColumnResult(name = "status", type = Integer.class)
	}))

@NamedNativeQuery(name = "Vendor.getAllOrganisations",
query="select o.id,o.name,vom.status from vendor_organisation_mapping vom, organisation o where vom.organisation_id=o.id and vom.vendor_id=?1",
		resultSetMapping = "Mapping.VendorOrganisationMappingDTO")

@SqlResultSetMapping(
		name = "Mapping.VendorOrganisationMappingDTO", 
		classes = @ConstructorResult(targetClass = VendorOrganisationMappingDTO.class, 
		columns = {
				@ColumnResult(name = "id", type = Long.class), 
				@ColumnResult(name = "name", type = String.class),
				@ColumnResult(name = "status", type = Integer.class)
	}))



public class Vendor extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 500)
    @Column(name = "name", length = 500, nullable = true, unique = false)
    private String name;

    @Column(name = "description")
    private String description;
    @OneToMany
    Set<AppProperty> appProperties=new HashSet<>();

    @Transient
	@JsonProperty
	private transient List<VendorCustomerMappingDTO> customerDtos;
	@Transient
	@JsonProperty
	private transient List<VendorOrganisationMappingDTO> organisationDtos;
    
    public List<VendorOrganisationMappingDTO> getOrganisationDtos() {
		return organisationDtos;
	}

	public void setOrganisationDtos(List<VendorOrganisationMappingDTO> organisationDtos) {
		this.organisationDtos = organisationDtos;
	}

	public List<VendorCustomerMappingDTO> getCustomerDtos() {
		return customerDtos;
	}

	public void setCustomerDtos(List<VendorCustomerMappingDTO> customerDtos) {
		this.customerDtos = customerDtos;
	}

	
	@OneToMany
    Set<User> users=new HashSet<>();

    public Set<AppProperty> getAppProperties() {
        return appProperties;
    }

    public void setAppProperties(Set<AppProperty> appProperties) {
        this.appProperties = appProperties;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Long getId() {
        return this.id;
    }

    public Vendor id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Vendor name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Vendor description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vendor)) {
            return false;
        }
        return id != null && id.equals(((Vendor) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AidasVendor{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
