package com.ainnotate.aidas.dto;

import java.util.List;

public class ObjectVendorMappingDTO {

    private Long objectId;
    private List<VendorUserDTO> vendorDTOs;

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public List<VendorUserDTO> getVendorDTOs() {
        return vendorDTOs;
    }

    public void setVendorDTOs(List<VendorUserDTO> vendorDTOs) {
        this.vendorDTOs = vendorDTOs;
    }
}
