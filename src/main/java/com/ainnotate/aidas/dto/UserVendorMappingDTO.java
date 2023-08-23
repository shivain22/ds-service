package com.ainnotate.aidas.dto;

public class UserVendorMappingDTO {

    private Long vendorId;
    private Integer status;
    private String name;
    public UserVendorMappingDTO(Long id, String name,Integer status ) {
    	this.vendorId=id;
    	this.name=name;
    	this.status=status;
    }
    public UserVendorMappingDTO() {
    	
    }
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
