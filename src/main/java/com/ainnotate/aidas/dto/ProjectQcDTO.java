package com.ainnotate.aidas.dto;

import com.ainnotate.aidas.domain.QcUsersOfCustomer;

import java.util.List;

public class ProjectQcDTO {

    private Long projectId;
    private Long customerId;
    private String name;

    private List<UserDTO> qcUsers;

    private List<QcUsersOfCustomer> qcUsers1;

    public List<QcUsersOfCustomer> getQcUsers1() {
        return qcUsers1;
    }

    public void setQcUsers1(List<QcUsersOfCustomer> qcUsers1) {
        this.qcUsers1 = qcUsers1;
    }

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
