package com.ainnotate.aidas.dto;

public class UploadDTOForQC {

	private Long uploadId;
	private String uploadUrl;
	private String projectName;
	private String objectName;
	private Long userVendorMappingObjectMappingId;
	private Long projectId;
	private Long objectId;
	private Long ucbiId;
	private Long batchNumber;
	private String fileName;
	private Integer qcStatus;
	
	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public UploadDTOForQC(Long uploadId,String uploadUrl,Long userVendorMappingObjectMappingId, Long projectId, Long objectId, String projectName, String objectName,Long ucbiId, Long batchNumber,String fileName,Integer qcStatus) {
		this.uploadId=uploadId;
		this.uploadUrl=uploadUrl;
		this.userVendorMappingObjectMappingId=userVendorMappingObjectMappingId;
		this.projectId=projectId;
		this.objectId=objectId;
		this.projectName=projectName;
		this.objectName=objectName;
		this.ucbiId=ucbiId;
		this.batchNumber=batchNumber;
		this.fileName=fileName;
		this.qcStatus=qcStatus;
	}
	
	
	public Integer getQcStatus() {
		return qcStatus;
	}


	public void setQcStatus(Integer qcStatus) {
		this.qcStatus = qcStatus;
	}


	public Long getUcbiId() {
		return ucbiId;
	}


	public void setUcbiId(Long ucbiId) {
		this.ucbiId = ucbiId;
	}


	public Long getBatchNumber() {
		return batchNumber;
	}


	public void setBatchNumber(Long batchNumber) {
		this.batchNumber = batchNumber;
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


	public Long getUserVendorMappingObjectMappingId() {
		return userVendorMappingObjectMappingId;
	}


	public void setUserVendorMappingObjectMappingId(Long userVendorMappingObjectMappingId) {
		this.userVendorMappingObjectMappingId = userVendorMappingObjectMappingId;
	}


	public Long getProjectId() {
		return projectId;
	}


	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}


	public Long getObjectId() {
		return objectId;
	}


	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}


	public Long getUploadId() {
		return uploadId;
	}
	public void setUploadId(Long uploadId) {
		this.uploadId = uploadId;
	}
	public String getUploadUrl() {
		return uploadUrl;
	}
	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
	}
	
}
