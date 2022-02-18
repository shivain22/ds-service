package com.ainnotate.aidas.domain;



public interface UploadDetail {

    public Long getProjectId();
    public Integer getTotalUploaded();
    public Integer getTotalApproved() ;
    public Integer getTotalRejected() ;
    public Integer getTotalPending() ;
    public Long getObjectId();

}
