package com.ainnotate.aidas.dto;

import java.util.List;

import com.ainnotate.aidas.domain.QcUser;

public class ProjectQcDTO {

    private Long projectId;
    private Long customerId;
    private String name;

    private List<QcUser> qcUsers;

    private List<QcUser> qcUsers1;

    public List<QcUser> getQcUsers1() {
        return qcUsers1;
    }

    public void setQcUsers1(List<QcUser> qcUsers1) {
        this.qcUsers1 = qcUsers1;
    }

    public List<QcUser> getQcUsers() {
        return qcUsers;
    }

    public void setQcUsers(List<QcUser> qcUsers) {
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
