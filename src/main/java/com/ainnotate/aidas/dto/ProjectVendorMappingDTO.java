package com.ainnotate.aidas.dto;

import java.util.List;

public class ProjectVendorMappingDTO {

    private Long projectId;

    private List<VendorUserDTO> vendors;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<VendorUserDTO> getVendors() {
        return vendors;
    }

    public void setVendors(List<VendorUserDTO> vendors) {
        this.vendors = vendors;
    }
}
