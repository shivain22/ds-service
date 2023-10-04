package com.ainnotate.aidas.dto;

public class UsersOfVendorDTO {

    
    private String firstName;
    private String lastName;
    private Long userId;
    private String login;
    private String vendorName;
    private Long userVendorMappingId;
    private Long vendorId;
    private Long userVendorMappingProjectMappingId;
    private Integer status;
    
    public UsersOfVendorDTO() {
    	
    }
	
	public UsersOfVendorDTO(String firstName, String lastName, Long userId, String login, String vendorName,
			Long userVendorMappingId, Long vendorId, Long userVendorMappingProjectMappingId, Integer status) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.userId = userId;
		this.login = login;
		this.vendorName = vendorName;
		this.userVendorMappingId = userVendorMappingId;
		this.vendorId = vendorId;
		this.userVendorMappingProjectMappingId = userVendorMappingProjectMappingId;
		this.status = status;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public Long getUserVendorMappingId() {
		return userVendorMappingId;
	}
	public void setUserVendorMappingId(Long userVendorMappingId) {
		this.userVendorMappingId = userVendorMappingId;
	}
	public Long getVendorId() {
		return vendorId;
	}
	public void setVendorId(Long vendorId) {
		this.vendorId = vendorId;
	}
	public Long getUserVendorMappingProjectMappingId() {
		return userVendorMappingProjectMappingId;
	}
	public void setUserVendorMappingProjectMappingId(Long userVendorMappingProjectMappingId) {
		this.userVendorMappingProjectMappingId = userVendorMappingProjectMappingId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
}
