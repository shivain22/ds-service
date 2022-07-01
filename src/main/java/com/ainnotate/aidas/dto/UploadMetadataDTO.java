package com.ainnotate.aidas.dto;

import java.util.HashMap;
import java.util.Map;

public class UploadMetadataDTO {
    private Long uploadId;
    private Long projectPropertyId;
    private String value;
    private Long objectPropertyId;
    Map<String,String> uploadMetaDatas = new HashMap<>();

    public Map<String, String> getUploadMetaDatas() {
        return uploadMetaDatas;
    }

    public void setUploadMetaDatas(Map<String, String> uploadMetaDatas) {
        this.uploadMetaDatas = uploadMetaDatas;
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
