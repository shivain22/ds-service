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

public class ObjectDTO extends AbstractAuditingEntity implements Serializable, IObjectDTO {

    private static final long serialVersionUID = 1L;

    private Integer count;

    private String objectDescriptionLink;
    
    public String getObjectDescriptionLink() {
		return objectDescriptionLink;
	}

	public void setObjectDescriptionLink(String objectDescriptionLink) {
		this.objectDescriptionLink = objectDescriptionLink;
	}

	@Override
    public Integer getCount() {
        return count;
    }

    @Override
    public void setCount(Integer count) {
        this.count = count;
    }

    public ObjectDTO() {

    }

    public ObjectDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public ObjectDTO(Integer count) {
        this.count = count;
    }

   
    public ObjectDTO(
        Long id,
        Long userVendorMappingObjectMappingId,
        Long projectId,
        Long parentObjectId,
        Integer numberOfUploadsRequired,
        Integer numberOfBufferedUploadsRequired,
        Integer totalRequired,
        Integer totalUploaded,
        Integer totalApproved,
        Integer totalRejected,
        Integer totalPending,
        Integer bufferPercent,
        String name,
        String description,
        String imageType,
        String audioType,
        String videoType,
        String objectDescriptionLink
    ) {
        this.id = id;
        this.userVendorMappingObjectMappingId = userVendorMappingObjectMappingId;
        this.totalRequired = totalRequired;
        this.totalUploaded = totalUploaded;
        this.totalApproved = totalApproved;
        this.totalRejected = totalRejected;
        this.totalPending = totalPending;
        this.numberOfUploadsRequired = numberOfUploadsRequired;
        this.numberOfBufferedUploadsRequired = numberOfBufferedUploadsRequired;
        this.projectId = projectId;
        this.parentObjectId = parentObjectId;
        this.bufferPercent = bufferPercent;
        this.name = name;
        this.description = description;
        this.imageType = imageType;
        this.audioType = audioType;
        this.videoType = videoType;
        this.objectDescriptionLink = objectDescriptionLink;
    }

    private Long id;
    private String name;
    private Integer bufferPercent;
    private String description;
    private Integer numberOfUploadsRequired;
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
    private Integer numberOfBufferedUploadsRequired;
    private Long userVendorMappingObjectMappingId;

    @Override
    public Long getUserVendorMappingObjectMappingId() {
        return userVendorMappingObjectMappingId;
    }

    @Override
    public void setUserVendorMappingObjectMappingId(Long userVendorMappingObjectMappingId) {
        this.userVendorMappingObjectMappingId = userVendorMappingObjectMappingId;
    }

    @Override
    public Integer getNumberOfUploadsRequired() {
        return numberOfUploadsRequired;
    }

    @Override
    public void setNumberOfUploadsRequired(Integer numberOfUploadsRequired) {
        this.numberOfUploadsRequired = numberOfUploadsRequired;
    }

    @Override
    public Integer getNumberOfBufferedUploadsRequired() {
        return numberOfBufferedUploadsRequired;
    }

    @Override
    public void setNumberOfBufferedUploadsRequired(Integer numberOfBufferedUploadsRequired) {
        this.numberOfBufferedUploadsRequired = numberOfBufferedUploadsRequired;
    }

    @Override
    public Long getProjectId() {
        return projectId;
    }

    @Override
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public Long getParentObjectId() {
        return parentObjectId;
    }

    @Override
    public void setParentObjectId(Long parentObjectId) {
        this.parentObjectId = parentObjectId;
    }

    @Override
    public Integer getTotalRequired() {
        return totalRequired;
    }

    @Override
    public void setTotalRequired(Integer totalRequired) {
        this.totalRequired = totalRequired;
    }

    private Set<ObjectProperty> objectProperties = new HashSet<>();

    @Override
    public String getImageType() {
        return imageType;
    }

    @Override
    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    @Override
    public String getVideoType() {
        return videoType;
    }

    @Override
    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    @Override
    public String getAudioType() {
        return audioType;
    }

    @Override
    public void setAudioType(String audioType) {
        this.audioType = audioType;
    }

    @Override
    public Integer getTotalUploaded() {
        return totalUploaded;
    }

    @Override
    public void setTotalUploaded(Integer totalUploaded) {
        this.totalUploaded = totalUploaded;
    }

    @Override
    public Integer getTotalApproved() {
        return totalApproved;
    }

    @Override
    public void setTotalApproved(Integer totalApproved) {
        this.totalApproved = totalApproved;
    }

    @Override
    public Integer getTotalRejected() {
        return totalRejected;
    }

    @Override
    public void setTotalRejected(Integer totalRejected) {
        this.totalRejected = totalRejected;
    }

    @Override
    public Integer getTotalPending() {
        return totalPending;
    }

    @Override
    public void setTotalPending(Integer totalPending) {
        this.totalPending = totalPending;
    }

    @Override
    public Integer getUploadsCompleted() {
        return uploadsCompleted;
    }

    @Override
    public void setUploadsCompleted(Integer uploadsCompleted) {
        this.uploadsCompleted = uploadsCompleted;
    }

    @Override
    public Integer getUploadsRemaining() {
        return this.uploadsRemaining;
    }

    @Override
    public void setUploadsRemaining(Integer uploadsRemaining) {
        this.uploadsRemaining = uploadsRemaining;
    }

    @Override
    public Integer getBufferPercent() {
        return bufferPercent;
    }

    @Override
    public void setBufferPercent(Integer bufferPercent) {
        this.bufferPercent = bufferPercent;
    }

    @Override
    public Set<ObjectProperty> getObjectProperties() {
        return objectProperties;
    }

    @Override
    public void setObjectProperties(Set<ObjectProperty> objectProperties) {
        this.objectProperties = objectProperties;
    }

    @Override
    public void addAidasObjectProperty(ObjectProperty objectProperty) {
        this.objectProperties.add(objectProperty);
    }

    @Override
    public void removeAidasObjectProperty(ObjectProperty objectProperty) {
        this.objectProperties.remove(objectProperty);
    }

    @Override
    public Long getId() {
        return this.id;
    }


    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }


    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }


    @Override
    public void setDescription(String description) {
        this.description = description;
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
        return "ObjectDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", numberOfUploadReqd=" + getNumberOfUploadsRequired() +
            "}";
    }
}
