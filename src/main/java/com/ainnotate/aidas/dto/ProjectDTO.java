package com.ainnotate.aidas.dto;

import com.ainnotate.aidas.domain.AbstractAuditingEntity;
import com.ainnotate.aidas.domain.Customer;
import com.ainnotate.aidas.domain.ProjectProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ProjectDTO extends AbstractAuditingEntity implements Serializable {


    public ProjectDTO(){

    }

    public ProjectDTO(Long id,
                      Integer totalRequired,
                      Integer totalUploaded,
                      Integer totalApproved,
                      Integer totalRejected,
                      Integer totalPending,
                      Integer status ,
                      String audioType ,
                      Integer autoCreateObjects ,
                      Integer bufferPercent ,
                      String description ,
                      Integer externalDatasetStatus ,
                      String imageType ,
                      String name ,
                      Integer numOfObjects ,
                      Integer numOfUploadsReqd ,
                      String objectPrefix ,
                      String objectSuffix ,
                      String projectType ,
                      Integer qcLevels ,
                      Integer reworkStatus ,
                      String videoType  ){
        this.id = id;
        this.totalRequired = totalRequired;
        this.totalUploaded = totalUploaded;
        this.totalApproved = totalApproved;
        this.totalRejected = totalRejected;
        this.totalPending = totalPending;
        this.setStatus(status);
        this.audioType = audioType ;
        this.autoCreateObjects = autoCreateObjects ;
        this.bufferPercent = bufferPercent ;
        this.description = description ;
        this.externalDatasetStatus = externalDatasetStatus ;
        this.imageType = imageType ;
        this.name = name ;
        this.numOfObjects = numOfObjects ;
        this.numOfUploadsReqd = numOfUploadsReqd ;
        this.objectPrefix = objectPrefix ;
        this.objectSuffix = objectSuffix ;
        this.projectType = projectType ;
        this.qcLevels = qcLevels ;
        this.reworkStatus = reworkStatus ;
        this.videoType = videoType ;


    }
    private static final long serialVersionUID = 1L;

    private Long id;

    private Integer totalRequired;

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTotalRequired() {
        return totalRequired;
    }

    public void setTotalRequired(Integer totalRequired) {
        this.totalRequired = totalRequired;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public void setReworkStatus(Integer reworkStatus) {
        this.reworkStatus = reworkStatus;
    }

    public void setQcLevels(Integer qcLevels) {
        this.qcLevels = qcLevels;
    }

    public void setTotalUploaded(Integer totalUploaded) {
        this.totalUploaded = totalUploaded;
    }

    public void setTotalApproved(Integer totalApproved) {
        this.totalApproved = totalApproved;
    }

    public void setTotalRejected(Integer totalRejected) {
        this.totalRejected = totalRejected;
    }

    public void setTotalPending(Integer totalPending) {
        this.totalPending = totalPending;
    }

    public void setAutoCreateObjects(Integer autoCreateObjects) {
        this.autoCreateObjects = autoCreateObjects;
    }

    public void setNumOfObjects(Integer numOfObjects) {
        this.numOfObjects = numOfObjects;
    }

    public void setObjectPrefix(String objectPrefix) {
        this.objectPrefix = objectPrefix;
    }

    public void setObjectSuffix(String objectSuffix) {
        this.objectSuffix = objectSuffix;
    }

    public void setExternalDatasetStatus(Integer externalDatasetStatus) {
        this.externalDatasetStatus = externalDatasetStatus;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public void setAudioType(String audioType) {
        this.audioType = audioType;
    }

    public void setNumOfUploadsReqd(Integer numOfUploadsReqd) {
        this.numOfUploadsReqd = numOfUploadsReqd;
    }

    public void setBufferPercent(Integer bufferPercent) {
        this.bufferPercent = bufferPercent;
    }

    private String name;

    private String description;

    private String projectType;

    private Integer reworkStatus=0;

    private Integer qcLevels;

    private Integer totalUploaded;

    private Integer totalApproved;

    private Integer totalRejected;

    private Integer totalPending;

    private Integer autoCreateObjects=0;

    private Integer numOfObjects;

    private String objectPrefix;

    private String objectSuffix;

    private Integer externalDatasetStatus;

    private String imageType;

    private String videoType;

    private String audioType;

    private Integer numOfUploadsReqd;

    private Integer bufferPercent;

    private List<ProjectProperty> aidasProjectProperties;

    public List<ProjectProperty> getAidasProjectProperties() {
        return aidasProjectProperties;
    }

    public void setAidasProjectProperties(List<ProjectProperty> aidasProjectProperties) {
        this.aidasProjectProperties = aidasProjectProperties;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getProjectType() {
        return projectType;
    }

    public Integer getReworkStatus() {
        return reworkStatus;
    }

    public Integer getQcLevels() {
        return qcLevels;
    }

    public Integer getTotalUploaded() {
        return totalUploaded;
    }

    public Integer getTotalApproved() {
        return totalApproved;
    }

    public Integer getTotalRejected() {
        return totalRejected;
    }

    public Integer getTotalPending() {
        return totalPending;
    }

    public Integer getAutoCreateObjects() {
        return autoCreateObjects;
    }

    public Integer getNumOfObjects() {
        return numOfObjects;
    }

    public String getObjectPrefix() {
        return objectPrefix;
    }

    public String getObjectSuffix() {
        return objectSuffix;
    }

    public Integer getExternalDatasetStatus() {
        return externalDatasetStatus;
    }

    public String getImageType() {
        return imageType;
    }

    public String getVideoType() {
        return videoType;
    }

    public String getAudioType() {
        return audioType;
    }

    public Integer getNumOfUploadsReqd() {
        return numOfUploadsReqd;
    }

    public Integer getBufferPercent() {
        return bufferPercent;
    }
}
