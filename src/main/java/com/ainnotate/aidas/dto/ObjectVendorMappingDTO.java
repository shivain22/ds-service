package com.ainnotate.aidas.dto;

import java.util.List;

public class ObjectVendorMappingDTO {

    private Long aidasObjectId;
    private List<Long> aidasVendorIds;


    public Long getAidasObjectId() {
        return aidasObjectId;
    }

    public void setAidasObjectId(Long aidasObjectId) {
        this.aidasObjectId = aidasObjectId;
    }

    public List<Long> getAidasVendorIds() {
        return aidasVendorIds;
    }

    public void setAidasVendorIds(List<Long> aidasVendorIds) {
        this.aidasVendorIds = aidasVendorIds;
    }
}
