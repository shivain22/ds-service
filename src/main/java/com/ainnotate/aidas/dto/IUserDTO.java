package com.ainnotate.aidas.dto;

public interface IUserDTO {

    public Long getUserId();
    public String getFirstName();
    public String getLastName();
    public String getLogin();
    public Long getUserVendorMappingId();
    public Long getUserCustomerMappingId();
    public Integer getStatus();
    public Long getQcLevel();

    public void setUserId(Long userId);
    public void setFirstName(String firstName);
    public void setLastName(String lastName);
    public void setLogin(String login);
    public void setUserVendorMappingId(Long userVendorMappingId);
    public void setUserCustomerMappingId(Long userCustomerMappingId);
    public void setStatus(Integer status);
    public void setQcLevel(Long qcLevel);
}
