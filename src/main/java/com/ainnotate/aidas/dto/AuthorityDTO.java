package com.ainnotate.aidas.dto;

public class AuthorityDTO {

	
	private Long id;
	private Integer status;
	private String name;
	private boolean lastLoggedInRole=false;
	public AuthorityDTO() {
		// TODO Auto-generated constructor stub
	}
	
	public AuthorityDTO(Long id, Integer status, String name) {
		super();
		this.id = id;
		this.status = status;
		this.name = name;
	
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isLastLoggedInRole() {
		return lastLoggedInRole;
	}
	public void setLastLoggedInRole(boolean lastLoggedInRole) {
		this.lastLoggedInRole = lastLoggedInRole;
	}
}
