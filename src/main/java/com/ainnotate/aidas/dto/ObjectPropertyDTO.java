package com.ainnotate.aidas.dto;

public class ObjectPropertyDTO {

	public ObjectPropertyDTO() {
		
	}
	public ObjectPropertyDTO(Long projectPropertyId, String name, Integer optional, String value) {
		this.objectPropertyId=projectPropertyId;
		this.name=name;
		this.optional=optional;
		this.value=value;
	}
    private Long objectPropertyId;
    private String name;
    private String value;

    public Long getObjectPropertyId() {
        return objectPropertyId;
    }

    public void setObjectPropertyId(Long objectPropertyId) {
        this.objectPropertyId = objectPropertyId;
    }

    private Integer systemProperty;
    private Integer propertyType;
    private Integer optional;
    private String description;
    private Integer defaultProp;
    private Long aidasObjectId;
    private String aidasObjectPropertyValue;
    private Long aidasPropertyId;
    private Integer isOptional;
    private Integer addToMetaData;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public Integer getIsOptional() {
        return isOptional;
    }

    public void setIsOptional(Integer isOptional) {
        this.isOptional = isOptional;
    }

    public Integer getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Integer propertyType) {
        this.propertyType = propertyType;
    }

    public Integer getAddToMetaData() {
        return addToMetaData;
    }

    public void setAddToMetaData(Integer addToMetaData) {
        this.addToMetaData = addToMetaData;
    }



    public Long getAidasObjectId() {
        return aidasObjectId;
    }

    public void setAidasObjectId(Long aidasObjectId) {
        this.aidasObjectId = aidasObjectId;
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

    public String getAidasObjectPropertyValue() {
        return aidasObjectPropertyValue;
    }

    public void setAidasObjectPropertyValue(String aidasObjectPropertyValue) {
        this.aidasObjectPropertyValue = aidasObjectPropertyValue;
    }

    public Long getAidasPropertyId() {
        return aidasPropertyId;
    }

    public void setAidasPropertyId(Long aidasPropertyId) {
        this.aidasPropertyId = aidasPropertyId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
