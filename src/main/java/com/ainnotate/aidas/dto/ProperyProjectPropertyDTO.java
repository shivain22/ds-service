package com.ainnotate.aidas.dto;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

public class ProperyProjectPropertyDTO {


    private String name;
    private String value;
    private Integer systemProperty;
    private Integer propertyType;
    private Integer optional;
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

    public Integer getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Integer propertyType) {
        this.propertyType = propertyType;
    }

    public Integer getSystemProperty() {
        return systemProperty;
    }

    public void setSystemProperty(Integer systemProperty) {
        this.systemProperty = systemProperty;
    }

    public Integer getOptional() {
        return optional;
    }

    public void setOptional(Integer optional) {
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
