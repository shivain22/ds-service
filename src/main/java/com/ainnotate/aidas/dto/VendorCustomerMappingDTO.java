package com.ainnotate.aidas.dto;

public class VendorCustomerMappingDTO {

    private Long customerId;
    private Integer status;
    private String name;
    public VendorCustomerMappingDTO() {
    	
    }
    public VendorCustomerMappingDTO(Long id, String name,Integer status ) {
    	this.customerId=id;
    	this.name=name;
    	this.status=status;
    }
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
