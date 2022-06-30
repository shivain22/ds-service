package com.ainnotate.aidas.dto;

public class ProjectPropertyDTO {

    private Long projectPropertyId;

    public Long getProjectPropertyId() {
        return projectPropertyId;
    }

    public void setProjectPropertyId(Long projectPropertyId) {
        this.projectPropertyId = projectPropertyId;
    }

    private String name;
    private String value;
    private Integer systemProperty;
    private Integer propertyType;
    private Integer optional;
    private String description;
    private Integer defaultProp;
    private Long aidasProjectId;
    private Long aidasPropertyId;
    private Integer addToMetadata;

    public Integer getAddToMetadata() {
        return addToMetadata;
    }

    public void setAddToMetadata(Integer addToMetadata) {
        this.addToMetadata = addToMetadata;
    }

    public Long getAidasPropertyId() {
        return aidasPropertyId;
    }

    public void setAidasPropertyId(Long aidasPropertyId) {
        this.aidasPropertyId = aidasPropertyId;
    }

    private String aidasProjectPropertyValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSystemProperty() {
        return systemProperty;
    }

    public void setSystemProperty(Integer systemProperty) {
        this.systemProperty = systemProperty;
    }

    public Integer getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Integer propertyType) {
        this.propertyType = propertyType;
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

    public String getAidasProjectPropertyValue() {
        return aidasProjectPropertyValue;
    }

    public void setAidasProjectPropertyValue(String aidasProjectPropertyValue) {
        this.aidasProjectPropertyValue = aidasProjectPropertyValue;
    }

    public Long getAidasProjectId() {
        return aidasProjectId;
    }

    public void setAidasProjectId(Long aidasProjectId) {
        this.aidasProjectId = aidasProjectId;
    }



    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
