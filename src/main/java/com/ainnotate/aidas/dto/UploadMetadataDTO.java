package com.ainnotate.aidas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

public class UploadMetadataDTO {
	
    @JsonIgnoreProperties(value = {"userVendorMappingObjecMapping","uploadRejectReasonMappings","uploadMetaDataSet"})
    UploadDTO uploadDTO;
    private Long uploadId;
    private Long projectPropertyId;
    private String value;
    private String name;
    private Long objectPropertyId;
    private Boolean projectProperty;
    private Integer propertyType;
    private Boolean failed=false;
    
    private String projectPropertyName;
    private String objectPropertyName;
    private String objectKey;
    private Long uploadMetaDataId;
    
    private Integer optional;
    
    public Integer getOptional() {
		return optional;
	}

	public void setOptional(Integer optional) {
		this.optional = optional;
	}

	public String getProjectPropertyName() {
		return projectPropertyName;
	}

	public void setProjectPropertyName(String projectPropertyName) {
		this.projectPropertyName = projectPropertyName;
	}

	public String getObjectPropertyName() {
		return objectPropertyName;
	}

	public void setObjectPropertyName(String objectPropertyName) {
		this.objectPropertyName = objectPropertyName;
	}

	public String getObjectKey() {
		return objectKey;
	}

	public void setObjectKey(String objectKey) {
		this.objectKey = objectKey;
	}

	public Long getUploadMetaDataId() {
		return uploadMetaDataId;
	}

	public void setUploadMetaDataId(Long uploadMetaDataId) {
		this.uploadMetaDataId = uploadMetaDataId;
	}

	public UploadMetadataDTO(String projectName, String objectName, Long uploadId, String objectKey, Long propertyId, String propertyName, Long uploadMetaDataId, String value,Integer optional, Integer isProjectProperty) {
    	this.projectName= projectName;
    	this.objectName = objectName;
    	this.uploadId = uploadId;
    	this.objectKey = objectKey;
    	if(isProjectProperty.equals(1)) {
	    	this.projectPropertyId = propertyId;
	    	this.projectPropertyName = propertyName;
    	}else if(isProjectProperty.equals(2)) {
    		this.objectPropertyId = propertyId;
	    	this.objectPropertyName = propertyName;
    	}
    	this.uploadMetaDataId = uploadMetaDataId;
    	this.value = value;
    	this.optional=optional;
    }
	
	

    private String projectName;
    private String objectName;

    public UploadDTO getUploadDTO() {
        return uploadDTO;
    }

    public void setUploadDTO(UploadDTO uploadDTO) {
        this.uploadDTO = uploadDTO;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public UploadMetadataDTO(){

    }


    public UploadMetadataDTO(Long id,String projectName, String objectName, Long uploadId, String value, Long projectPropertyId, Long objectPropertyId){
            this.projectName = projectName;
            this.objectName = objectName;
            this.uploadId  = uploadId;
            this.value = value;
            this.projectPropertyId = projectPropertyId;
            this.objectPropertyId = objectPropertyId;
    }
    
    public UploadMetadataDTO(Long id,String projectName, String objectName, Long uploadId, String value, Long projectPropertyId, Long objectPropertyId,String projectPropertyName, String objectPropertyName,Integer propertyType){
        this.projectName = projectName;
        this.objectName = objectName;
        this.uploadId  = uploadId;
        this.value = value;
        this.projectPropertyId = projectPropertyId;
        this.objectPropertyId = objectPropertyId;
        this.projectPropertyName = projectPropertyName;
        this.objectPropertyName = objectPropertyName;
        this.propertyType=propertyType;
}

    public Boolean getFailed() {
        return failed;
    }

    public void setFailed(Boolean failed) {
        this.failed = failed;
    }

    public Integer getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Integer propertyType) {
        this.propertyType = propertyType;
    }

    public Boolean getProjectProperty() {
        return projectProperty;
    }

    public void setProjectProperty(Boolean projectProperty) {
        this.projectProperty = projectProperty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getObjectPropertyId() {
        return objectPropertyId;
    }

    public void setObjectPropertyId(Long objectPropertyId) {
        this.objectPropertyId = objectPropertyId;
    }

    public Long getUploadId() {
        return uploadId;
    }

    public void setUploadId(Long uploadId) {
        this.uploadId = uploadId;
    }

    public Long getProjectPropertyId() {
        return projectPropertyId;
    }

    public void setProjectPropertyId(Long projectPropertyId) {
        this.projectPropertyId = projectPropertyId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
