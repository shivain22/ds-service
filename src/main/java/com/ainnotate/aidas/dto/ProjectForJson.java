package com.ainnotate.aidas.dto;

import java.util.ArrayList;
import java.util.List;

public class ProjectForJson {

	private Long id;
	private String name;
	private Integer groupingProject;
	private Integer consentFormStatus;
	public Integer getConsentFormStatus() {
		return consentFormStatus;
	}

	public void setConsentFormStatus(Integer consentFormStatus) {
		this.consentFormStatus = consentFormStatus;
	}

	public Integer getGroupingProject() {
		return groupingProject;
	}

	public void setGroupingProject(Integer groupingProject) {
		this.groupingProject = groupingProject;
	}

	List<ObjectForJson> objects=new ArrayList<>();

	public List<ObjectForJson> getObjects() {
		return objects;
	}

	public void setObjects(List<ObjectForJson> objects) {
		this.objects = objects;
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

	
}
