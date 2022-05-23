package com.ainnotate.aidas.domain;

public class AidasPropertiesAidasObjectPropertyDTO {


    private String name;
    private String value;
    private Boolean systemProperty;
    private Long propertyType;
    private Boolean optional;
    private String description;
    private Integer defaultProp;
    private Long aidasObjectId;
    private String aidasObjectPropertyValue;

    public String getAidasObjectPropertyValue() {
        return aidasObjectPropertyValue;
    }

    public void setAidasObjectPropertyValue(String aidasObjectPropertyValue) {
        this.aidasObjectPropertyValue = aidasObjectPropertyValue;
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

    public Long getAidasObjectId() {
        return aidasObjectId;
    }

    public void setAidasObjectId(Long aidasObjectId) {
        this.aidasObjectId = aidasObjectId;
    }
}
