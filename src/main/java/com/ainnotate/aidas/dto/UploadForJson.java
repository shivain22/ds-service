package com.ainnotate.aidas.dto;

import java.net.URL;

public class UploadForJson {

	private Long id;
	private String fileName;
	private URL s3Url;
	private String md5;
	private Integer approvalStatus;
	
	public Integer getApprovalStatus() {
		return approvalStatus;
	}
	public void setApprovalStatus(Integer approvalStatus) {
		this.approvalStatus = approvalStatus;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public URL getS3Url() {
		return s3Url;
	}
	public void setS3Url(URL s3Url) {
		this.s3Url = s3Url;
	}
}
