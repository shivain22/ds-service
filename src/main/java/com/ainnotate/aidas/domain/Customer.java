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

import com.ainnotate.aidas.dto.UserCustomerMappingDTO;
import com.ainnotate.aidas.dto.UserOrganisationMappingDTO;
import com.ainnotate.aidas.dto.VendorOrganisationMappingDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A AidasCustomer.
 */



@NamedNativeQuery(name = "Customer.getAllCustomersWithUamId",
query="select distinct c.id, c.name, uum.status \n"
		+ "from\n"
		+ "customer  c,  user_customer_mapping ucm,uam_ucm_mapping uum, user_authority_mapping uam \n"
		+ "where\n"
		+ "uum.ucm_id=ucm.id and ucm.customer_id=c.id \n"
		+ "and ucm.user_id=?1 and uum.uam_id=uam.id and uam.authority_id=?2 and uum.status=1 and ucm.status=1 and c.status=1\n"
		+ "union\n"
		+ "select c.id, c.name, 0 as status\n"
		+ "from\n"
		+ "customer  c \n"
		+ "where\n"
		+ " c.id \n"
		+ "not in \n"
		+ "(select c.id\n"
		+ "from\n"
		+ "customer  c,  user_customer_mapping ucm,uam_ucm_mapping uum ,user_authority_mapping uam \n"
		+ "where\n"
		+ "uum.ucm_id=ucm.id and ucm.customer_id=c.id\n"
		+ "and ucm.user_id=?1 and c.id>-1 and uum.uam_id=uam.id and uam.authority_id=?2 and uum.status=1 and uam.status=1 and ucm.status=1 and c.status=1 ) and c.status=1",
		resultSetMapping = "Mapping.AuthorityCustomerMappingDTO")

@NamedNativeQuery(name = "Customer.getAllCustomersWithoutUamId",
query="select c.id as id, c.name as name, 0 as status from customer c where c.id>0 and c.id not in (select ucm.customer_id from user_customer_mapping ucm where ucm.user_id=?1)",
		resultSetMapping = "Mapping.AuthorityCustomerMappingDTO")

@NamedNativeQuery(name = "Customer.getAllCustomersWithUamIdAndCustomerId",
query="select c.id, c.name, uum.status \n"
		+ "from\n"
		+ "customer  c, uam_ucm_mapping uum, user_customer_mapping ucm,user_authority_mapping uam \n"
		+ "where\n"
		+ "uum.ucm_id=ucm.id \n"
		+ "and uum.uam_id=uam.id and uam.authority_id=?1 and uum.status=1 and ucm.status=1 and uam.status=1 and c.status=1 \n"
		+ "union\n"
		+ "select c.id, c.name, 0 as status\n"
		+ "from\n"
		+ "customer  c \n"
		+ "where\n"
		+ "c.id \n"
		+ "not in \n"
		+ "(select ucm.customer_id from uam_ucm_mapping uum,user_customer_mapping ucm,user_authority_mapping uam where uum.ucm_id=ucm.id and uum.uam_id=uam.id and uam.authority_id=?1 and uum.status=1 and uam.status=1 and ucm.status=1 and c.status=1 ) and c.status=1",
		
		resultSetMapping = "Mapping.AuthorityCustomerMappingDTO")


@NamedNativeQuery(name = "Customer.getAllCustomersWithUamIdAndOrgId",
query="select c.id, c.name, uum.status \n"
		+ "from\n"
		+ "customer  c, uam_ucm_mapping uum, user_customer_mapping ucm,user_authority_mapping uam \n"
		+ "where\n"
		+ "uum.ucm_id=ucm.id \n"
		+ "and uum.uam_id=uam.id and uam.authority_id=?1 and uum.status=1 and ucm.status=1 and uam.status=1 and c.status=1 \n"
		+ "union\n"
		+ "select c.id, c.name, 0 as status\n"
		+ "from\n"
		+ "customer  c \n"
		+ "where\n"
		+ "c.id \n"
		+ "not in \n"
		+ "(select ucm.customer_id from uam_ucm_mapping uum,user_customer_mapping ucm,user_authority_mapping uam where uum.ucm_id=ucm.id and uum.uam_id=uam.id and uam.authority_id=?1 and uum.status=1 and ucm.status=1 and uam.status=1 and c.status=1) and c.status=1",
		resultSetMapping = "Mapping.AuthorityCustomerMappingDTO")

@SqlResultSetMapping(
		name = "Mapping.AuthorityCustomerMappingDTO", 
		classes = @ConstructorResult(targetClass = UserCustomerMappingDTO.class, 
		columns = {
				@ColumnResult(name = "id", type = Long.class), 
				@ColumnResult(name = "name", type = String.class),
				@ColumnResult(name = "status", type = Integer.class)
	}))




@Entity
@Table(name = "customer",indexes = {
    @Index(name="idx_customer_organisation",columnList = "organisation_id")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_customer_organisation_name",columnNames={"name", "organisation_id"})
    })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "aidascustomer")
@Audited
public class Customer extends AbstractAuditingEntity  implements Serializable {

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

    @ManyToOne(optional = false)
    @NotNull
    @JoinColumn(name = "organisation_id", nullable = true, foreignKey = @ForeignKey(name="fk_customer_organisation"))
    private Organisation organisation;
    
    @Transient
	@JsonProperty
	private transient List<VendorOrganisationMappingDTO> organisationDtos;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public List<VendorOrganisationMappingDTO> getOrganisationDtos() {
		return organisationDtos;
	}

	public void setOrganisationDtos(List<VendorOrganisationMappingDTO> organisationDtos) {
		this.organisationDtos = organisationDtos;
	}

	public Long getId() {
        return this.id;
    }

    public Customer id(Long id) {
        this.setId(id);
        return this;
    }
    @OneToMany
    Set<AppProperty> appProperties=new HashSet<>();

    public Set<AppProperty> getAppProperties() {
        return appProperties;
    }

    public void setAppProperties(Set<AppProperty> appProperties) {
        this.appProperties = appProperties;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Customer name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Customer description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Organisation getOrganisation() {
        return this.organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public Customer organisation(Organisation organisation) {
        this.setOrganisation(organisation);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer)) {
            return false;
        }
        return id != null && id.equals(((Customer) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Customer{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
