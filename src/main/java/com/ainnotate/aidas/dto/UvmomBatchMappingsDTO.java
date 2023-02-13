package com.ainnotate.aidas.dto;

import java.util.LinkedList;
import java.util.List;

public class UvmomBatchMappingsDTO {

    private Long userVendorMappingId;

    public Long getUserVendorMappingId() {
        return userVendorMappingId;
    }

    public void setUserVendorMappingId(Long userVendorMappingId) {
        this.userVendorMappingId = userVendorMappingId;
    }

    private List<UvmomBatchMappingDTO> uvmoms=new LinkedList<>();

    public List<UvmomBatchMappingDTO> getUvmoms() {
        return uvmoms;
    }

    public void setUvmoms(List<UvmomBatchMappingDTO> uvmoms) {
        this.uvmoms = uvmoms;
    }
}
