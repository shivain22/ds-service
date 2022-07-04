package com.ainnotate.aidas.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * An authority (a security role) used by Spring Security.
 */
@Entity
@Table(name = "app_property",indexes = {
        @Index(name="idx_ap_organisation",columnList = "organisation_id"),
        @Index(name="idx_ap_customer",columnList = "customer_id"),
        @Index(name="idx_ap_vendor",columnList = "vendor_id"),
        @Index(name="idx_ap_user",columnList = "user_id")
    },
    uniqueConstraints={
        @UniqueConstraint(name = "uk_ap_oid_cid_vid_uid",columnNames={"name", "organisation_id","customer_id","vendor_id","user_id"})
})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "app_property")
@Audited
public class AppProperty extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @ManyToOne
    @JoinColumn(name = "organisation_id", nullable = true, foreignKey = @ForeignKey(name="fk_ap_organisation"))
    Organisation organisation;
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = true, foreignKey = @ForeignKey(name="fk_ap_customer"))
    Customer customer;
    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = true, foreignKey = @ForeignKey(name="fk_ap_vendor"))
    Vendor vendor;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true, foreignKey = @ForeignKey(name="fk_ap_user"))
    User user;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @NotNull
    @Column(length = 50)
    private String name;
    @NotNull
    @Lob
    @Column
    private String value;

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (!(o instanceof AppProperty)) return false;
        AppProperty that = (AppProperty) o;
        return getId().equals(that.getId()) && getName().equals(that.getName()) && getValue().equals(that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getValue());
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



}
