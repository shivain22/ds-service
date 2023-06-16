package com.ainnotate.aidas.dto;

public class UserLanguageMappingDTO {

    private Long languageId;
    private Integer status;

    

    public Long getLanguageId() {
		return languageId;
	}

	public void setLanguageId(Long languageId) {
		this.languageId = languageId;
	}

	public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
