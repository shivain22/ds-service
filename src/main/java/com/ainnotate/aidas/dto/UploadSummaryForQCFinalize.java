package com.ainnotate.aidas.dto;

public class UploadSummaryForQCFinalize {

	private Long projectId;
	private Long objectId;
	private Long uvmpmId;
	private Long uvmomId;
	private Integer totalUploaded;
	private Integer totalApproved;
	private Integer totalRejected;
	private Integer totalPending;
	private Integer totalShowToQc;
	private Long uvmId;
	public Long getUvmId() {
		return uvmId;
	}

	public void setUvmId(Long uvmId) {
		this.uvmId = uvmId;
	}

	public UploadSummaryForQCFinalize(Long projectId, Long objectId, Long uvmpmId, Long uvmomId,Long uvmId, Integer totalUploaded,
			Integer totalApproved, Integer totalRejected, Integer totalPending, Integer totalShowToQc) {
		super();
		this.projectId = projectId;
		this.objectId = objectId;
		this.uvmpmId = uvmpmId;
		this.uvmomId = uvmomId;
		this.uvmId = uvmId;
		this.totalUploaded = totalUploaded;
		this.totalApproved = totalApproved;
		this.totalRejected = totalRejected;
		this.totalPending = totalPending;
		this.totalShowToQc = totalShowToQc;
	}
	
	public UploadSummaryForQCFinalize(Long uvmpmId, Long projectId,Integer totalUploaded,
			Integer totalApproved, Integer totalRejected, Integer totalPending) {
		super();
		this.projectId = projectId;
		this.uvmpmId = uvmpmId;
		this.totalUploaded = totalUploaded;
		this.totalApproved = totalApproved;
		this.totalRejected = totalRejected;
		this.totalPending = totalPending;
	}
	
	public UploadSummaryForQCFinalize(Long projectId,Integer totalUploaded,
			Integer totalApproved, Integer totalRejected, Integer totalPending) {
		super();
		this.projectId = projectId;
		this.totalUploaded = totalUploaded;
		this.totalApproved = totalApproved;
		this.totalRejected = totalRejected;
		this.totalPending = totalPending;
	}
	
	
	
	public UploadSummaryForQCFinalize(Integer totalUploaded,
			Integer totalApproved, Integer totalRejected, Integer totalPending) {
		super();
		this.totalUploaded = totalUploaded;
		this.totalApproved = totalApproved;
		this.totalRejected = totalRejected;
		this.totalPending = totalPending;
	}
	
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public Long getObjectId() {
		return objectId;
	}
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
	public Long getUvmpmId() {
		return uvmpmId;
	}
	public void setUvmpmId(Long uvmpmId) {
		this.uvmpmId = uvmpmId;
	}
	public Long getUvmomId() {
		return uvmomId;
	}
	public void setUvmomId(Long uvmomId) {
		this.uvmomId = uvmomId;
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
	public Integer getTotalShowToQc() {
		return totalShowToQc;
	}
	public void setTotalShowToQc(Integer totalShowToQc) {
		this.totalShowToQc = totalShowToQc;
	}
	}
