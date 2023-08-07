package com.ainnotate.aidas.dto;

import java.util.ArrayList;
import java.util.List;

public class ObjectForJson {

	private Long id;
	private String name;
	private List<UploadForJson> uploads= new ArrayList<>();
	private String consentFormUrl;
	public String getConsentFormUrl() {
		return consentFormUrl;
	}
	public void setConsentFormUrl(String consentFormUrl) {
		this.consentFormUrl = consentFormUrl;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<UploadForJson> getUploads() {
		return uploads;
	}
	public void setUploads(List<UploadForJson> uploads) {
		this.uploads = uploads;
	}
}
