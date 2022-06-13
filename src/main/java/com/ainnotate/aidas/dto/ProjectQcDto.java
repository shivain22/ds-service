package com.ainnotate.aidas.dto;

public class ProjectQcDto {

    private Long projectId;
    private Long userId;
    private Long qcLevel;
    private Long qcMappingId;

    public Long getQcMappingId() {
        return qcMappingId;
    }

    public void setQcMappingId(Long qcMappingId) {
        this.qcMappingId = qcMappingId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getQcLevel() {
        return qcLevel;
    }

    public void setQcLevel(Long qcLevel) {
        this.qcLevel = qcLevel;
    }
}
