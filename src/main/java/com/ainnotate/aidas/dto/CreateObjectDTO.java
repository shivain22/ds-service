package com.ainnotate.aidas.dto;

public class CreateObjectDTO {

	private String name;
	private String description;
	private String objetctDetailUrl;
	private Integer numberOfUploadsRequired;
	private Long projectId;
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getObjetctDetailUrl() {
		return objetctDetailUrl;
	}
	public void setObjetctDetailUrl(String objetctDetailUrl) {
		this.objetctDetailUrl = objetctDetailUrl;
	}
	public Integer getNumberOfUploadsRequired() {
		return numberOfUploadsRequired;
	}
	public void setNumberOfUploadsRequired(Integer numberOfUploadsRequired) {
		this.numberOfUploadsRequired = numberOfUploadsRequired;
	}
	
}
