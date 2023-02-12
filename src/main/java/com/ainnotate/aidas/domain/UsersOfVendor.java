package com.ainnotate.aidas.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users_of_vendor")
public class UsersOfVendor {

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
    @Column(name = "vendor_name")
    private String vendorName;
    @Column(name = "user_vendor_mapping_id")
    private Long userVendorMappingId;
    @Column(name = "vendor_id")
    private Long vendorId;
    @Column(name = "user_vendor_mapping_project_mapping_id")
    private String userVendorMappingProjectMappingId;
    @Column(name = "status")
    private Integer status;

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

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public Long getUserVendorMappingId() {
        return userVendorMappingId;
    }

    public void setUserVendorMappingId(Long userVendorMappingId) {
        this.userVendorMappingId = userVendorMappingId;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String getUserVendorMappingProjectMappingId() {
        return userVendorMappingProjectMappingId;
    }

    public void setUserVendorMappingProjectMappingId(String userVendorMappingProjectMappingId) {
        this.userVendorMappingProjectMappingId = userVendorMappingProjectMappingId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }



}
