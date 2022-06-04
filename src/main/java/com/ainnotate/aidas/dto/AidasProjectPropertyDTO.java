package com.ainnotate.aidas.dto;

public class AidasProjectPropertyDTO {

    private Long aidasProjectId;
    private Long aidasPropertiesId;
    private String value;

    public Long getAidasProjectId() {
        return aidasProjectId;
    }

    public void setAidasProjectId(Long aidasProjectId) {
        this.aidasProjectId = aidasProjectId;
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
