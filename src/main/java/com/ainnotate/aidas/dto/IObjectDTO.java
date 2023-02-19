package com.ainnotate.aidas.dto;

import com.ainnotate.aidas.domain.ObjectProperty;

import java.util.List;
import java.util.Set;

public interface IObjectDTO extends Cloneable{
    Integer getCount();

    void setCount(Integer count);

    Long getUserVendorMappingObjectMappingId();

    void setUserVendorMappingObjectMappingId(Long userVendorMappingObjectMappingId);

    Integer getNumberOfUploadsRequired();

    void setNumberOfUploadsRequired(Integer numberOfUploadsRequired);

    Integer getNumberOfBufferedUploadsRequired();

    void setNumberOfBufferedUploadsRequired(Integer numberOfBufferedUploadsRequired);

    Long getProjectId();

    void setProjectId(Long projectId);

    Long getParentObjectId();

    void setParentObjectId(Long parentObjectId);

    Integer getTotalRequired();

    void setTotalRequired(Integer totalRequired);

    String getImageType();

    void setImageType(String imageType);

    String getVideoType();

    void setVideoType(String videoType);

    String getAudioType();

    void setAudioType(String audioType);

    Integer getTotalUploaded();

    void setTotalUploaded(Integer totalUploaded);

    Integer getTotalApproved();

    void setTotalApproved(Integer totalApproved);

    Integer getTotalRejected();

    void setTotalRejected(Integer totalRejected);

    Integer getTotalPending();

    void setTotalPending(Integer totalPending);

    Integer getUploadsCompleted();

    void setUploadsCompleted(Integer uploadsCompleted);

    Integer getUploadsRemaining();

    void setUploadsRemaining(Integer uploadsRemaining);

    Integer getBufferPercent();

    void setBufferPercent(Integer bufferPercent);

    Set<ObjectProperty> getObjectProperties();

    void setObjectProperties(Set<ObjectProperty> objectProperties);

    void addAidasObjectProperty(ObjectProperty objectProperty);

    void removeAidasObjectProperty(ObjectProperty objectProperty);

    Long getId();

    void setId(Long id);

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);
}
