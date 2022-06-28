package com.ainnotate.aidas.dto;

import java.util.HashMap;

public class UploadDTO {

    private Long userId;
    private Long objectId;
    private String uploadUrl;
    private String etag;
    private String objectKey;
    private HashMap<String,String> uploadMetadata;

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public HashMap<String, String> getUploadMetadata() {
        return uploadMetadata;
    }

    public void setUploadMetadata(HashMap<String, String> uploadMetadata) {
        this.uploadMetadata = uploadMetadata;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }
}
