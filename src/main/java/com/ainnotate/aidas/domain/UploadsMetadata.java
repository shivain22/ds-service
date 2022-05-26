package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

public class UploadsMetadata {

    AidasUpload aidasUploads;
    @JsonIgnoreProperties(value = {"aidasProject"})
    List<AidasProjectProperty> aidasProjectProperties;
    @JsonIgnoreProperties(value = {"aidasObject"})
    List<AidasObjectProperty> aidasObjectProperties;

    public AidasUpload getAidasUploads() {
        return aidasUploads;
    }

    public void setAidasUploads(AidasUpload aidasUploads) {
        this.aidasUploads = aidasUploads;
    }

    public List<AidasProjectProperty> getAidasProjectProperties() {
        return aidasProjectProperties;
    }

    public void setAidasProjectProperties(List<AidasProjectProperty> aidasProjectProperties) {
        this.aidasProjectProperties = aidasProjectProperties;
    }

    public List<AidasObjectProperty> getAidasObjectProperties() {
        return aidasObjectProperties;
    }

    public void setAidasObjectProperties(List<AidasObjectProperty> aidasObjectProperties) {
        this.aidasObjectProperties = aidasObjectProperties;
    }
}
