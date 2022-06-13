package com.ainnotate.aidas.dto;

public class PropertyObjectPropertyDTO {


    private String name;
    private String value;
    private Integer systemProperty;
    private Integer propertyType;
    private Integer optional;
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

    public Long getAidasObjectId() {
        return aidasObjectId;
    }

    public void setAidasObjectId(Long aidasObjectId) {
        this.aidasObjectId = aidasObjectId;
    }
}
