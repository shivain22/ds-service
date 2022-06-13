package com.ainnotate.aidas.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

public class UploadsMetadata {

    Upload uploads;
    @JsonIgnoreProperties(value = {"project"})
    List<ProjectProperty> projectProperties;
    @JsonIgnoreProperties(value = {"object"})
    List<ObjectProperty> objectProperties;

    public Upload getAidasUploads() {
        return uploads;
    }

    public void setAidasUploads(Upload uploads) {
        this.uploads = uploads;
    }

    public List<ProjectProperty> getProjectProperties() {
        return projectProperties;
    }

    public void setProjectProperties(List<ProjectProperty> projectProperties) {
        this.projectProperties = projectProperties;
    }

    public List<ObjectProperty> getObjectProperties() {
        return objectProperties;
    }

    public void setObjectProperties(List<ObjectProperty> objectProperties) {
        this.objectProperties = objectProperties;
    }
}
