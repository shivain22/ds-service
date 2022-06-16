package com.ainnotate.aidas.dto;

import java.util.List;

public class ProjectQcDTO {

    private Long customerId;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private List<UserDTO> qcUsers;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<UserDTO> getQcUsers() {
        return qcUsers;
    }

    public void setQcUsers(List<UserDTO> qcUsers) {
        this.qcUsers = qcUsers;
    }
}
