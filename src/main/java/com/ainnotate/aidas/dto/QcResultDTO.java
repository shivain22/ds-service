package com.ainnotate.aidas.dto;

public class QcResultDTO {

    private String firstName;
    private String lastName;
    private Integer qcStatus;

    public QcResultDTO(String firstName, String lastName, Integer qcStatus) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.qcStatus = qcStatus;
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
