package com.ainnotate.aidas.dto;

import java.math.BigInteger;

public class UserDTO implements  IUserDTO {

    private Long userId;
    private String firstName;
    private String lastName;
    private String login;
    private Long userVendorMappingId;
    private Long userCustomerMappingId;
    private Integer status=0;
    private Integer qcLevel;
    private Long userVendorMappingObjectMappingId;
    private Long vendorId;
    private String vendorName;
    private Long userVendorMappingProjectMappingId;
    private Long userOrganisationMappingId;
    private Long organisationId;
    private Long customerId;
    private Long purposeId;

    public Long getUserOrganisationMappingId() {
        return userOrganisationMappingId;
    }

    public void setUserOrganisationMappingId(Long userOrganisationMappingId) {
        this.userOrganisationMappingId = userOrganisationMappingId;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getPurposeId() {
        return purposeId;
    }

    public void setPurposeId(Long purposeId) {
        this.purposeId = purposeId;
    }

    public UserDTO() {
    }

    public UserDTO( String firstName, String lastName,String login, String vendorName, Long userId, Long userVendorMappingId, Long vendorId, Integer status) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.userVendorMappingId = userVendorMappingId;
        this.status = status;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
    }

    public UserDTO( String firstName, String lastName,String login, String vendorName, Long userId, Long userVendorMappingId, Long vendorId, Integer status,Long userVendorMappingProjectMappingId) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.userVendorMappingId = userVendorMappingId;
        this.status = status;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.userVendorMappingProjectMappingId = userVendorMappingProjectMappingId;
    }

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
    public Integer getQcLevel() {
        return this.qcLevel;
    }

    @Override
    public Long getUserVendorMappingObjectMappingId() {
        return this.userVendorMappingObjectMappingId;
    }

    @Override
    public Long getUserVendorMappingProjectMappingId() {
        return this.userVendorMappingProjectMappingId;
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
    public void setQcLevel(Integer qcLevel) {
this.qcLevel = qcLevel;
    }

    @Override
    public void setUserVendorMappingObjectMappingId(Long userVendorMappingObjectMappingId) {
        this.userVendorMappingObjectMappingId= userVendorMappingObjectMappingId;
    }

    @Override
    public void setUserVendorMappingProjectMappingId(Long userVendorMappingProjectMappingId) {
        this.userVendorMappingProjectMappingId = userVendorMappingProjectMappingId;
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
