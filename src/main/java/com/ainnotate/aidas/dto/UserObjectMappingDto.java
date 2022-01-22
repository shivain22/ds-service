package com.ainnotate.aidas.dto;

public class UserObjectMappingDto {

    Long aidasUserId;
    Long aidasObjectId;

    public Long getAidasUserId() {
        return aidasUserId;
    }

    public void setAidasUserId(Long aidasUserId) {
        this.aidasUserId = aidasUserId;
    }

    public Long getAidasObjectId() {
        return aidasObjectId;
    }

    public void setAidasObjectId(Long aidasObjectId) {
        this.aidasObjectId = aidasObjectId;
    }
}
