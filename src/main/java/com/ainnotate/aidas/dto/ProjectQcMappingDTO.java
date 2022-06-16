package com.ainnotate.aidas.dto;

import java.util.List;

public class ProjectQcMappingDTO {

    private Long projectId;

    private List<ProjectQcDTO> customers;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<ProjectQcDTO> getCustomers() {
        return customers;
    }

    public void setCustomers(List<ProjectQcDTO> customers) {
        this.customers = customers;
    }
}
