package com.ainnotate.aidas.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * An authority (a security role) used by Spring Security.
 */
@Entity
@Table(name = "customer_organisation_mapping")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited


public class CustomerOrganisationMapping extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerOrganisationMapping)) return false;
        CustomerOrganisationMapping that = (CustomerOrganisationMapping) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getCustomer(), that.getCustomer()) && Objects.equals(getOrganisation(), that.getOrganisation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCustomer(), getOrganisation());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = true, foreignKey = @ForeignKey(name="fk_com_vendor"))
    private Customer customer;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "organisation_id", nullable = true, foreignKey = @ForeignKey(name="fk_com_organisation"))
    private Organisation organisation;


    

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "VendorOrganisationMapping{vendor_id="+this.customer.getId()+",organisation_id="+this.organisation.getId()+"}";
    }
}
