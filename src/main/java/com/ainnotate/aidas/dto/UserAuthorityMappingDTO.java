package com.ainnotate.aidas.dto;

public class UserAuthorityMappingDTO {

    public Long getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Long authorityId) {
        this.authorityId = authorityId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    private Long authorityId;
    private Integer status;
}
