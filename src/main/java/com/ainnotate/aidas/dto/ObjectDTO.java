package com.ainnotate.aidas.dto;

import com.ainnotate.aidas.domain.AbstractAuditingEntity;
import com.ainnotate.aidas.domain.ObjectProperty;
import com.ainnotate.aidas.domain.Project;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Filter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A AidasObject.
 */

public class ObjectDTO extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    public ObjectDTO(
       Long id,
       Integer totalRequired,
       Integer totalUploaded,
       Integer totalApproved,
       Integer totalRejected,
       Integer totalPending,
       Long projectId,
       Long parentObjectId,
       Integer bufferPercent,
       String name,
       String description,
       String imageType,
       String audioType,
       String videoType
    ){
            this.id=id;
            this.totalRequired=totalRequired;
            this.totalUploaded=totalUploaded;
            this.totalApproved=totalApproved;
            this.totalRejected=totalRejected;
            this.totalPending=totalPending;
            this.projectId=projectId;
            this.parentObjectId=parentObjectId;
            this.bufferPercent=bufferPercent;
            this.name=name;
            this.description=description;
            this.imageType=imageType;
            this.audioType=audioType;
            this.videoType=videoType;
    }
    private Long id;
    private String name;
    private Integer bufferPercent;
    private String description;
    private Integer numberOfUploadReqd;
    private Long projectId;
    private Long parentObjectId;
    private Integer uploadsCompleted;
    private Integer uploadsRemaining;
    private Integer totalUploaded;
    private Integer totalApproved;
    private Integer totalRejected;
    private Integer totalPending;
    private String imageType;
    private String videoType;
    private String audioType;
    private Integer totalRequired;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getParentObjectId() {
        return parentObjectId;
    }

    public void setParentObjectId(Long parentObjectId) {
        this.parentObjectId = parentObjectId;
    }

    public Integer getTotalRequired() {
        return totalRequired;
    }

    public void setTotalRequired(Integer totalRequired) {
        this.totalRequired = totalRequired;
    }

    private List<ObjectProperty> objectProperties = new ArrayList<>();

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getAudioType() {
        return audioType;
    }

    public void setAudioType(String audioType) {
        this.audioType = audioType;
    }

    public Integer getTotalUploaded() {
        return totalUploaded;
    }

    public void setTotalUploaded(Integer totalUploaded) {
        this.totalUploaded = totalUploaded;
    }

    public Integer getTotalApproved() {
        return totalApproved;
    }

    public void setTotalApproved(Integer totalApproved) {
        this.totalApproved = totalApproved;
    }

    public Integer getTotalRejected() {
        return totalRejected;
    }

    public void setTotalRejected(Integer totalRejected) {
        this.totalRejected = totalRejected;
    }

    public Integer getTotalPending() {
        return totalPending;
    }

    public void setTotalPending(Integer totalPending) {
        this.totalPending = totalPending;
    }

    public Integer getUploadsCompleted() {
        return uploadsCompleted;
    }

    public void setUploadsCompleted(Integer uploadsCompleted) {
        this.uploadsCompleted = uploadsCompleted;
    }

    public Integer getUploadsRemaining() {
        return uploadsRemaining;
    }

    public void setUploadsRemaining(Integer uploadsRemaining) {
        this.uploadsRemaining = uploadsRemaining;
    }

    public Integer getBufferPercent() {
        return bufferPercent;
    }

    public void setBufferPercent(Integer bufferPercent) {
        this.bufferPercent = bufferPercent;
    }

    public List<ObjectProperty> getObjectProperties() {
        return objectProperties;
    }

    public void setObjectProperties(List<ObjectProperty> objectProperties) {
        this.objectProperties = objectProperties;
    }

    public void addAidasObjectProperty(ObjectProperty objectProperty){
        this.objectProperties.add(objectProperty);
    }

    public void removeAidasObjectProperty(ObjectProperty objectProperty){
        this.objectProperties.remove(objectProperty);
    }

    public Long getId() {
        return this.id;
    }



    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }



    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }



    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNumberOfUploadReqd() {
        return this.numberOfUploadReqd;
    }



    public void setNumberOfUploadReqd(Integer numberOfUploadReqd) {
        this.numberOfUploadReqd = numberOfUploadReqd;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ObjectDTO)) {
            return false;
        }
        return id != null && id.equals(((ObjectDTO) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AidasObject{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", numberOfUploadReqd=" + getNumberOfUploadReqd() +
            "}";
    }
}
