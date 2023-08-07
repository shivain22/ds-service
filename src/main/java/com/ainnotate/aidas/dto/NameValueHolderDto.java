package com.ainnotate.aidas.dto;

public class NameValueHolderDto {

	private Long id;
	private String name;
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
	private boolean lastLoggedInRole;

    public boolean isLastLoggedInRole() {
		return lastLoggedInRole;
	}

	public void setLastLoggedInRole(boolean lastLoggedInRole) {
		this.lastLoggedInRole = lastLoggedInRole;
	}
}
