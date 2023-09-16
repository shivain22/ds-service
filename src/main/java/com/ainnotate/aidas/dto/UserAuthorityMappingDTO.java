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
    private String name;
    public UserAuthorityMappingDTO( ) {
    	
    }
    public UserAuthorityMappingDTO(Long id, String name,Integer status ) {
    	this.authorityId=id;
    	this.name=name;
    	this.status=status;
    }
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "UserAuthorityMappingDTO [authorityId=" + authorityId + ", status=" + status + ", name=" + name + "]";
	}
	
}
