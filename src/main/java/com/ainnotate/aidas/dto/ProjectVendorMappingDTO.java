package com.ainnotate.aidas.dto;

import java.util.List;

public class ProjectVendorMappingDTO {

    private Long aidasProjectId;
    private List<Long> aidasVendorIds;

    public Long getAidasProjectId() {
        return aidasProjectId;
    }

    public void setAidasProjectId(Long aidasProjectId) {
        this.aidasProjectId = aidasProjectId;
    }

    public List<Long> getAidasVendorIds() {
        return aidasVendorIds;
    }

    public void setAidasVendorIds(List<Long> aidasVendorIds) {
        this.aidasVendorIds = aidasVendorIds;
    }
}
