package com.ainnotate.aidas.dto;

import java.util.List;

public class ProjectQcDTO {

    private Long projectId;
    private Long customerId;
    private String name;

    private List<UserDTO> qcUsers;

    public List<UserDTO> getQcUsers() {
        return qcUsers;
    }

    public void setQcUsers(List<UserDTO> qcUsers) {
        this.qcUsers = qcUsers;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }


}
