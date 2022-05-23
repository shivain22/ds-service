package com.ainnotate.aidas.domain;

public class AidasObjectPropertyDTO {

    private Long aidasObjectId;
    private Long aidasPropertiesId;
    private String value;

    public Long getAidasObjectId() {
        return aidasObjectId;
    }

    public void setAidasObjectId(Long aidasObjectId) {
        this.aidasObjectId = aidasObjectId;
    }

    public Long getAidasPropertiesId() {
        return aidasPropertiesId;
    }

    public void setAidasPropertiesId(Long aidasPropertiesId) {
        this.aidasPropertiesId = aidasPropertiesId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
