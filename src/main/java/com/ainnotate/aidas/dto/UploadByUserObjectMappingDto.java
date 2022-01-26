package com.ainnotate.aidas.dto;

public class UploadByUserObjectMappingDto {

    private Long userObjectMappingId;
    private String uploadUrl;
    private String etag;
    private String objectKey;
    private String uploadMetadata;

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public Long getUserObjectMappingId() {
        return userObjectMappingId;
    }

    public void setUserObjectMappingId(Long userObjectMappingId) {
        this.userObjectMappingId = userObjectMappingId;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getUploadMetadata() {
        return uploadMetadata;
    }

    public void setUploadMetadata(String uploadMetadata) {
        this.uploadMetadata = uploadMetadata;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }
}
