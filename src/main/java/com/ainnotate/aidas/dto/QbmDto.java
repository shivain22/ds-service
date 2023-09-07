package com.ainnotate.aidas.dto;

public class QbmDto {

	private Long qbmId;
	private Integer approved;
	private Integer rejected;
	private Integer pending;
	
	public QbmDto(Long qbmId, Integer approved, Integer rejected, Integer pending) {
		super();
		this.qbmId = qbmId;
		this.approved = approved;
		this.rejected = rejected;
		this.pending = pending;
	}
	public Long getQbmId() {
		return qbmId;
	}
	public void setQbmId(Long qbmId) {
		this.qbmId = qbmId;
	}
	public Integer getApproved() {
		return approved;
	}
	public void setApproved(Integer approved) {
		this.approved = approved;
	}
	public Integer getRejected() {
		return rejected;
	}
	public void setRejected(Integer rejected) {
		this.rejected = rejected;
	}
	public Integer getPending() {
		return pending;
	}
	public void setPending(Integer pending) {
		this.pending = pending;
	}
	
}
