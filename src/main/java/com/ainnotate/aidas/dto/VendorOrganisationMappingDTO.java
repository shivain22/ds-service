package com.ainnotate.aidas.dto;

public class VendorOrganisationMappingDTO {

    private Long organisationId;
    private Integer status;
    private String name;
    public VendorOrganisationMappingDTO() {
    	
    }
    public VendorOrganisationMappingDTO(Long id, String name,Integer status ) {
    	this.organisationId=id;
    	this.name=name;
    	this.status=status;
    }
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

    public Long getOrganisationId() {
		return organisationId;
	}
	public void setOrganisationId(Long organisationId) {
		this.organisationId = organisationId;
	}
	public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
