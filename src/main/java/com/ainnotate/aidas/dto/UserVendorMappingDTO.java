package com.ainnotate.aidas.dto;

public class UserVendorMappingDTO {

    private Long vendorId;
    private Integer status;

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
