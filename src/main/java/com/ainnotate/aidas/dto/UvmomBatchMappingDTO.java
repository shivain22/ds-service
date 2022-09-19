package com.ainnotate.aidas.dto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UvmomBatchMappingDTO implements Comparable<UvmomBatchMappingDTO> {

    private Integer batchNumber;
    private List<Long> userVendorMappingObjectMappingIds = new ArrayList<>();

    public Integer getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(Integer batchNumber) {
        this.batchNumber = batchNumber;
    }

    public List<Long> getUserVendorMappingObjectMappingIds() {
        return userVendorMappingObjectMappingIds;
    }

    public void setUserVendorMappingObjectMappingIds(List<Long> userVendorMappingObjectMappingIds) {
        this.userVendorMappingObjectMappingIds = userVendorMappingObjectMappingIds;
    }

    @Override
    public int compareTo(UvmomBatchMappingDTO u) {
        if (getBatchNumber() == null || u.getBatchNumber() == null) {
            return 0;
        }
        return getBatchNumber().compareTo(u.getBatchNumber());
    }
}
