package com.ainnotate.aidas.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users_of_vendor")
public class QcUsersOfCustomer {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "login")
    private String login;
    @Column(name = "customer_id")
    private Long customerId;
    @Column(name = "user_customer_mapping_id")
    private Long userCustomerMappingId;
    @Column(name = "user_vendor_mapping_id")
    private Long userVendorMappingId;
    @Column(name = "user_organisation_mapping_id")
    private Long userOrganisationMappingId;
    @Column(name = "qc_level")
    private Integer qcLevel;
    @Column(name = "status")
    private Integer status;

    @Column(name = "purpose_id")
    private Integer purposeId;

    @Column(name = "organisation_id")
    private Integer organisationId;

    @Column(name = "vendor_id")
    private Integer vendorId;

    public Long getUserVendorMappingId() {
        return userVendorMappingId;
    }

    public void setUserVendorMappingId(Long userVendorMappingId) {
        this.userVendorMappingId = userVendorMappingId;
    }

    public Long getUserOrganisationMappingId() {
        return userOrganisationMappingId;
    }

    public void setUserOrganisationMappingId(Long userOrganisationMappingId) {
        this.userOrganisationMappingId = userOrganisationMappingId;
    }

    public Integer getPurposeId() {
        return purposeId;
    }

    public void setPurposeId(Integer purposeId) {
        this.purposeId = purposeId;
    }

    public Integer getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Integer organisationId) {
        this.organisationId = organisationId;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }


    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getUserCustomerMappingId() {
        return userCustomerMappingId;
    }

    public void setUserCustomerMappingId(Long userCustomerMappingId) {
        this.userCustomerMappingId = userCustomerMappingId;
    }

    public Integer getQcLevel() {
        return qcLevel;
    }

    public void setQcLevel(Integer qcLevel) {
        this.qcLevel = qcLevel;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }



}
