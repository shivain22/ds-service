package com.ainnotate.aidas.domain;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

public class AidasPropertiesAidasProjectPropertyDTO {


    private String name;
    private String value;
    private Boolean systemProperty;
    private Long propertyType;
    private Boolean optional;
    private String description;
    private Integer defaultProp;
    private Long aidasProjectId;
    private String aidasProjectPropertyValue;

    public String getAidasProjectPropertyValue() {
        return aidasProjectPropertyValue;
    }

    public void setAidasProjectPropertyValue(String aidasProjectPropertyValue) {
        this.aidasProjectPropertyValue = aidasProjectPropertyValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getSystemProperty() {
        return systemProperty;
    }

    public void setSystemProperty(Boolean systemProperty) {
        this.systemProperty = systemProperty;
    }

    public Long getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Long propertyType) {
        this.propertyType = propertyType;
    }

    public Boolean getOptional() {
        return optional;
    }

    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDefaultProp() {
        return defaultProp;
    }

    public void setDefaultProp(Integer defaultProp) {
        this.defaultProp = defaultProp;
    }

    public Long getAidasProjectId() {
        return aidasProjectId;
    }

    public void setAidasProjectId(Long aidasProjectId) {
        this.aidasProjectId = aidasProjectId;
    }
}
