package com.ainnotate.aidas.dto;

import java.util.ArrayList;
import java.util.List;

public class VendorUserDTO {

    private Long vendorId;
    private String name;
    private List<UserDTO> userDTOs=new ArrayList<>();

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserDTO> getUserDTOs() {
        return userDTOs;
    }

    public void setUserDTOs(List<UserDTO> userDTOs) {
        this.userDTOs = userDTOs;
    }
}
