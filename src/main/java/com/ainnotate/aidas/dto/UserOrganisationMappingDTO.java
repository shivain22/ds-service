package com.ainnotate.aidas.dto;

public class UserOrganisationMappingDTO {

    private Long organisationId;
    private Integer status;

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
