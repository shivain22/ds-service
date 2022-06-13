package com.ainnotate.aidas.domain;

public class Dashboard {

    private Long organisationCount;
    private Long customerCount;
    private Long vendorCount;
    private Long projectCount;
    private Long objectCount;
    private Long userCount;
    private Long uploadCount;
    private Long approvedUploadCount;
    private Long rejectedUploadCount;
    private Long pendingUploadCount;

    private Long orgUsersCount;
    private Long customerUsersCount;
    private Long vendorUsersCount;

    public Long getOrgUsersCount() {
        return orgUsersCount;
    }

    public void setOrgUsersCount(Long orgUsersCount) {
        this.orgUsersCount = orgUsersCount;
    }

    public Long getCustomerUsersCount() {
        return customerUsersCount;
    }

    public void setCustomerUsersCount(Long customerUsersCount) {
        this.customerUsersCount = customerUsersCount;
    }

    public Long getVendorUsersCount() {
        return vendorUsersCount;
    }

    public void setVendorUsersCount(Long vendorUsersCount) {
        this.vendorUsersCount = vendorUsersCount;
    }

    public Long getAllVendorUsersCount() {
        return allVendorUsersCount;
    }

    public void setAllVendorUsersCount(Long allVendorUsersCount) {
        this.allVendorUsersCount = allVendorUsersCount;
    }

    private Long allVendorUsersCount;


    public Long getPendingUploadCount() {
        return pendingUploadCount;
    }

    public void setPendingUploadCount(Long pendingUploadCount) {
        this.pendingUploadCount = pendingUploadCount;
    }

    public Long getOrganisationCount() {
        return organisationCount;
    }

    public void setOrganisationCount(Long organisationCount) {
        this.organisationCount = organisationCount;
    }

    public Long getCustomerCount() {
        return customerCount;
    }

    public void setCustomerCount(Long customerCount) {
        this.customerCount = customerCount;
    }

    public Long getVendorCount() {
        return vendorCount;
    }

    public void setVendorCount(Long vendorCount) {
        this.vendorCount = vendorCount;
    }

    public Long getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Long projectCount) {
        this.projectCount = projectCount;
    }

    public Long getObjectCount() {
        return objectCount;
    }

    public void setObjectCount(Long objectCount) {
        this.objectCount = objectCount;
    }

    public Long getUserCount() {
        return userCount;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }

    public Long getUploadCount() {
        return uploadCount;
    }

    public void setUploadCount(Long uploadCount) {
        this.uploadCount = uploadCount;
    }

    public Long getApprovedUploadCount() {
        return approvedUploadCount;
    }

    public void setApprovedUploadCount(Long approvedUploadCount) {
        this.approvedUploadCount = approvedUploadCount;
    }

    public Long getRejectedUploadCount() {
        return rejectedUploadCount;
    }

    public void setRejectedUploadCount(Long rejectedUploadCount) {
        this.rejectedUploadCount = rejectedUploadCount;
    }
}
