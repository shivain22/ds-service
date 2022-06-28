package com.ainnotate.aidas.dto;


import com.ainnotate.aidas.domain.Project;

public interface IUploadDetail {

    public Long getProjectId();
    public Project getProject();
    public Integer getTotalUploaded();
    public Integer getTotalApproved() ;
    public Integer getTotalRejected() ;
    public Integer getTotalPending() ;
    public Long getObjectId();

}
