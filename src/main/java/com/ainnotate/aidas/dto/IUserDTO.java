package com.ainnotate.aidas.dto;

public interface IUserDTO {

    public Long getUserId();
    public String getFirstName();
    public String getLastName();
    public String getLogin();
    public Long getUserVendorMappingId();
    public Long getUserCustomerMappingId();
    public Integer getStatus();
    public Integer getQcLevel();
    public Long getUserVendorMappingObjectMappingId();
    public Long getUserVendorMappingProjectMappingId();
    public Long getVendorId();
    public String getVendorName();

    public void setUserId(Long userId);
    public void setFirstName(String firstName);
    public void setLastName(String lastName);
    public void setLogin(String login);
    public void setUserVendorMappingId(Long userVendorMappingId);
    public void setUserCustomerMappingId(Long userCustomerMappingId);
    public void setStatus(Integer status);
    public void setQcLevel(Integer qcLevel);
    public void setUserVendorMappingObjectMappingId(Long userVendorMappingObjectMappingId);
    public void setUserVendorMappingProjectMappingId(Long userVendorMappingProjectMappingId);
    public void setVendorName(String vendorName);
    public void setVendorId(Long vendorId);
}
