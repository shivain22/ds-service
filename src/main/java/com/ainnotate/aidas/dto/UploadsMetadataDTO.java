package com.ainnotate.aidas.dto;

import com.ainnotate.aidas.domain.ObjectProperty;
import com.ainnotate.aidas.domain.ProjectProperty;
import com.ainnotate.aidas.domain.Upload;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

public class UploadsMetadataDTO {

    @JsonIgnoreProperties(value = {"userVendorMappingObjecMapping","uploadRejectMappings","uploadMetaDataSet"})
    UploadDTO uploadDTO;
    @JsonIgnoreProperties(value = {"project","customer"})
    List<ProjectPropertyDTO> projectProperties;
    @JsonIgnoreProperties(value = {"object","project"})
    List<ObjectPropertyDTO> objectProperties;

    public UploadDTO getUploadDTO() {
        return uploadDTO;
    }

    public void setUploadDTO(UploadDTO uploadDTO) {
        this.uploadDTO = uploadDTO;
    }

    public List<ProjectPropertyDTO> getProjectProperties() {
        return projectProperties;
    }

    public void setProjectProperties(List<ProjectPropertyDTO> projectProperties) {
        this.projectProperties = projectProperties;
    }

    public List<ObjectPropertyDTO> getObjectProperties() {
        return objectProperties;
    }

    public void setObjectProperties(List<ObjectPropertyDTO> objectProperties) {
        this.objectProperties = objectProperties;
    }
}
