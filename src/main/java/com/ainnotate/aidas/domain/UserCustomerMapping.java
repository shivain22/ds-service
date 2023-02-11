package com.ainnotate.aidas.domain;

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
@Table(name = "user_customer_mapping",indexes = {
    @Index(name="idx_ucm_user",columnList = "user_id"),
    @Index(name="idx_ucm_customer",columnList = "customer_id")
},
    uniqueConstraints={
        @UniqueConstraint(name = "uk_ucm_user_customer",columnNames={"user_id", "customer_id"})
    })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
public class UserCustomerMapping extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (!(o instanceof UserCustomerMapping)) return false;
        UserCustomerMapping that = (UserCustomerMapping) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getCustomer(), that.getCustomer()) && Objects.equals(getUser(), that.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCustomer(), getUser());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = true, foreignKey = @ForeignKey(name="fk_ucm_customer"))
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true, foreignKey = @ForeignKey(name="fk_ucm_user"))
    private User user;

    @Column(name = "purpose_id")
    private Long purpose=0l;

    public Long getPurpose() {
        return purpose;
    }

    public void setPurpose(Long purpose) {
        this.purpose = purpose;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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
        return "UserCustomerMapping{user_id="+id+",customer_id="+this.customer.getId()+"}";
    }
}
