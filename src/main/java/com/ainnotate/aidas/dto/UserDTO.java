package com.ainnotate.aidas.dto;

public class UserDTO implements  IUserDTO {

    private Long userId;
    private String firstName;
    private String lastName;
    private String login;
    private Long userVendorMappingId;
    private Long userCustomerMappingId;
    private Integer status=0;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getUserVendorMappingId() {
        return userVendorMappingId;
    }

    public void setUserVendorMappingId(Long userVendorMappingId) {
        this.userVendorMappingId = userVendorMappingId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Long getUserCustomerMappingId() {
        return userCustomerMappingId;
    }

    public void setUserCustomerMappingId(Long userCustomerMappingId) {
        this.userCustomerMappingId = userCustomerMappingId;
    }
}
