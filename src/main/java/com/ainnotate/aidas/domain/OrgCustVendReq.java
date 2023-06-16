package com.ainnotate.aidas.domain;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

/**
 * An authority (a security role) used by Spring Security.
 */
@Entity
@Table(name = "org_cust_vend_req",
    uniqueConstraints={
        @UniqueConstraint(name = "uk_org_cust_vend",columnNames={"email"})})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
@org.springframework.data.elasticsearch.annotations.Document(indexName = "orgcustvendreq")
public class OrgCustVendReq extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(length = 50)
    private String firstName;
    
    @NotNull
    @Size(max = 50)
    @Column(length = 50)
    private String lastName;
    
    @NotNull
    @Column
    private String email;
    
    @NotNull
    @Column
    private String companyName;
    
    @NotNull
    @Size(max = 50)
    @Column(length = 50)
    private String mobileNumber;
    
    @NotNull
    @Column
    private String message;

    

    public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrgCustVendReq)) {
            return false;
        }
        return Objects.equals(email, ((OrgCustVendReq) o).email);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }

    @Override
    public String toString() {
        return "OrgCustVendReq{" +
            "name='" + email + '\'' +
            "}";
    }
}
