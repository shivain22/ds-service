package com.ainnotate.aidas.dto;

import com.ainnotate.aidas.domain.UsersOfVendor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VendorUserDTO {

    private Long vendorId;
    private String name;
    private List<UsersOfVendorDTO> userDTOs=new ArrayList<>();
    

	public VendorUserDTO(Long vendorId, String name, List<UsersOfVendorDTO> userDTOs) {
        this.vendorId = vendorId;
        this.name = name;
        this.userDTOs = userDTOs;
    }

    public VendorUserDTO() {
    }

    public VendorUserDTO(Long vendorId) {
        this.vendorId = vendorId;
    }

    public VendorUserDTO(Long vendorId, String name) {
        this.vendorId = vendorId;
        this.name = name;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VendorUserDTO)) return false;
        VendorUserDTO that = (VendorUserDTO) o;
        return getVendorId().equals(that.getVendorId()) && getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVendorId(), getName());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public List<UsersOfVendorDTO> getUserDTOs() {
		return userDTOs;
	}

	public void setUserDTOs(List<UsersOfVendorDTO> userDTOs) {
		this.userDTOs = userDTOs;
	}

    
}
