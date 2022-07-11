package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * An authority (a security role) used by Spring Security.
 */
@Entity
@Table(name = "user_vendor_mapping",indexes = {
    @Index(name="idx_uvm_vendor",columnList = "vendor_id"),
    @Index(name="idx_uvm_user",columnList = "user_id")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_ap_oid_cid_vid_uid",columnNames={"vendor_id","user_id"})
    })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
public class UserVendorMapping extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (!(o instanceof UserVendorMapping)) return false;
        UserVendorMapping that = (UserVendorMapping) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getVendor(), that.getVendor()) && Objects.equals(getUser(), that.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getVendor(), getUser());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = true, foreignKey = @ForeignKey(name="fk_uvm_vendor"))
    private Vendor vendor;

    @ManyToOne
    @JsonIgnoreProperties(value = {"vendor","customer","organisation","userAuthorityMappings","userVendorMappings","userCustomerMappings","userOrganisationMappings"})
    @JoinColumn(name = "user_id", nullable = true, foreignKey = @ForeignKey(name="fk_uvm_user"))
    private User user;



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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserVendorMapping{user_id="+id+",vendor_id="+this.vendor.getId()+"}";
    }
}
