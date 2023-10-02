package com.ainnotate.aidas.dto;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

public class UploadDTO {

    private Long uploadId;
    
	/*
	 * private File uploadFile;
	 * 
	 * public File getUploadFile() { return uploadFile; }
	 * 
	 * public void setUploadFile(File uploadFile) { this.uploadFile = uploadFile; }
	 */

	public Long getUploadId() {
        return uploadId;
    }

    public void setUploadId(Long uploadId) {
        this.uploadId = uploadId;
    }

    private Long userId;
    private Long objectId;
    private String name;
    private Long userVendorMappingObjectMappingId;

    public Long getUserVendorMappingObjectMappingId() {
		return userVendorMappingObjectMappingId;
	}

	public void setUserVendorMappingObjectMappingId(Long userVendorMappingObjectMappingId) {
		this.userVendorMappingObjectMappingId = userVendorMappingObjectMappingId;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String uploadUrl;
    private String etag;
    private String objectKey;
    private HashMap<String,String> uploadMetadata;

    private String consentFormUrl;
    public String getConsentFormUrl() {
		return consentFormUrl;
	}

	public void setConsentFormUrl(String consentFormUrl) {
		this.consentFormUrl = consentFormUrl;
	}

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

    @Override
	public int hashCode() {
		return Objects.hash(name, objectKey, uploadId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UploadDTO other = (UploadDTO) obj;
		return Objects.equals(name, other.name) && Objects.equals(objectKey, other.objectKey)
				&& Objects.equals(uploadId, other.uploadId);
	}

	public void setEtag(String etag) {
        this.etag = etag;
    }
}
