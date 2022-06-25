package com.ainnotate.aidas.dto;

public class UserDTO implements  IUserDTO {

    private Long userId;
    private String firstName;
    private String lastName;
    private String login;
    private Long userVendorMappingId;
    private Long userCustomerMappingId;
    private Integer status=0;
    private Long qcLevel;
    private Long userVendorMappingObjectMappingId;
    private Long vendorId;
    private String vendorName;

    @Override
    public Long getUserId() {
        return this.userId;
    }

    @Override
    public String getFirstName() {
        return this.firstName;
    }

    @Override
    public String getLastName() {
        return this.lastName;
    }

    @Override
    public String getLogin() {
        return this.login;
    }

    @Override
    public Long getUserVendorMappingId() {
        return this.userVendorMappingId;
    }

    @Override
    public Long getUserCustomerMappingId() {
        return this.userCustomerMappingId;
    }

    @Override
    public Integer getStatus() {
        return this.status;
    }

    @Override
    public Long getQcLevel() {
        return this.qcLevel;
    }

    @Override
    public Long getUserVendorMappingObjectMappingId() {
        return this.userVendorMappingObjectMappingId;
    }

    @Override
    public Long getVendorId() {
        return this.vendorId;
    }

    @Override
    public String getVendorName() {
        return this.vendorName;
    }

    @Override
    public void setUserId(Long userId) {
            this.userId=userId;
    }

    @Override
    public void setFirstName(String firstName) {
this.firstName = firstName;
    }

    @Override
    public void setLastName(String lastName) {
this.lastName= lastName;
    }

    @Override
    public void setLogin(String login) {
this.login = login;
    }

    @Override
    public void setUserVendorMappingId(Long userVendorMappingId) {
this.userVendorMappingId=userVendorMappingId;
    }

    @Override
    public void setUserCustomerMappingId(Long userCustomerMappingId) {
        this.userCustomerMappingId = userCustomerMappingId;
    }

    @Override
    public void setStatus(Integer status) {
this.status=status;
    }

    @Override
    public void setQcLevel(Long qcLevel) {
this.qcLevel = qcLevel;
    }

    @Override
    public void setUserVendorMappingObjectMappingId(Long userVendorMappingObjectMappingId) {
        this.userVendorMappingObjectMappingId= userVendorMappingObjectMappingId;
    }

    @Override
    public void setVendorName(String vendorName) {
        this.vendorName= vendorName;
    }

    @Override
    public void setVendorId(Long vendorId) {
        this.vendorId= vendorId;
    }
}
