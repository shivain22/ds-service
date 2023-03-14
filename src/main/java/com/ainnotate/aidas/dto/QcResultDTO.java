package com.ainnotate.aidas.dto;

public class QcResultDTO {

    private String firstName;
    private String lastName;
    private Integer qcStatus;
    private Integer qcSeenStatus=0;

    public Integer getQcSeenStatus() {
		return qcSeenStatus;
	}

	public void setQcSeenStatus(Integer qcSeenStatus) {
		this.qcSeenStatus = qcSeenStatus;
	}

	public QcResultDTO(String firstName, String lastName, Integer qcStatus,Integer qcSeenStatus) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.qcStatus = qcStatus;
        this.qcSeenStatus = qcSeenStatus;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getQcStatus() {
        return qcStatus;
    }

    public void setQcStatus(Integer qcStatus) {
        this.qcStatus = qcStatus;
    }
}
