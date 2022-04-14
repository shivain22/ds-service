package com.ainnotate.aidas.domain;

import java.util.List;

public class UploadsMetadata {
    List<AidasUpload> aidasUploads;
    List<AidasProjectProperty> aidasProjectProperties;

    public List<AidasUpload> getAidasUploads() {
        return aidasUploads;
    }

    public void setAidasUploads(List<AidasUpload> aidasUploads) {
        this.aidasUploads = aidasUploads;
    }

    public List<AidasProjectProperty> getAidasProjectProperties() {
        return aidasProjectProperties;
    }

    public void setAidasProjectProperties(List<AidasProjectProperty> aidasProjectProperties) {
        this.aidasProjectProperties = aidasProjectProperties;
    }
}
