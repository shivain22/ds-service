package com.ainnotate.aidas.dto;

public class UserObjectMappingDto {

    Long aidasUserId;
    Long aidasObjectId;
    Long aidasVendorId;

    public Long getAidasUserId() {
        return aidasUserId;
    }

    public void setAidasUserId(Long aidasUserId) {
        this.aidasUserId = aidasUserId;
    }

    public Long getAidasObjectId() {
        return aidasObjectId;
    }

    public Long getAidasVendorId() {
        return aidasVendorId;
    }

    public void setAidasVendorId(Long aidasVendorId) {
        this.aidasVendorId = aidasVendorId;
    }

    public void setAidasObjectId(Long aidasObjectId) {
        this.aidasObjectId = aidasObjectId;
    }
}
