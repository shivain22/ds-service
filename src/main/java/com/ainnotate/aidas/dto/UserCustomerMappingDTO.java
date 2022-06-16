package com.ainnotate.aidas.dto;

public class UserCustomerMappingDTO {

    private Long customerId;
    private Integer status;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
