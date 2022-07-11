package com.ainnotate.aidas.service;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ObjectAddingTask implements  Runnable{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectRepository objectRepository;

    @Autowired
    private UserVendorMappingObjectMappingRepository userVendorMappingObjectMappingRepository;

    @Autowired
    private UserVendorMappingProjectMappingRepository userVendorMappingProjectMappingRepository;

    @Autowired
    CustomerQcProjectMappingRepository customerQcProjectMappingRepository;

    @Autowired
    UserCustomerMappingRepository userCustomerMappingRepository;

    @Autowired
    private UserVendorMappingRepository userVendorMappingRepository;

    private Boolean dummy;

    public Boolean getDummy() {
        return dummy;
    }

    public void setDummy(Boolean dummy) {
        this.dummy = dummy;
    }
    private Object object;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public void run() {

        List<Long> vendorWithUserStatusOne = userVendorMappingObjectMappingRepository.getVendorsWhoseUsersAreHavingStatusOne(object.getProject().getId());
        List<UserVendorMapping> userVendorMappings = userVendorMappingRepository.findAll();
        List<UserVendorMappingObjectMapping> uvmoms=new ArrayList<>();
        List<UserVendorMappingProjectMapping> uvmpoms=new ArrayList<>();
        for(UserVendorMapping uvm:userVendorMappings){
            if(object.getProject().getUserAddedStatus().equals(0)){
                UserVendorMappingProjectMapping uvmpm = new UserVendorMappingProjectMapping();
                uvmpm.setProject(object.getProject());
                uvmpm.setUserVendorMapping(uvm);
                uvmpoms.add(uvmpm);
            }
            UserVendorMappingObjectMapping uvmom = new UserVendorMappingObjectMapping();
            uvmom.setUserVendorMapping(uvm);
            uvmom.setObject(object);
            if(vendorWithUserStatusOne.contains(uvm.getVendor().getId())){
                uvmom.setStatus(1);
            }else{
                uvmom.setStatus(0);
            }
            uvmoms.add(uvmom);
        }
        if(!uvmpoms.isEmpty()){
            userVendorMappingProjectMappingRepository.saveAll(uvmpoms);
        }
        userVendorMappingObjectMappingRepository.saveAll(uvmoms);
        if(getDummy()) {
            List<UserCustomerMapping> qcUserCustomerMapping = userCustomerMappingRepository.getAllQcUserCustomerMapping(object.getProject().getCustomer().getId());
            List<CustomerQcProjectMapping> qpms = new ArrayList<>();
            for (UserCustomerMapping ucm : qcUserCustomerMapping) {
                if (object.getProject().getQcLevels() != null) {
                    for (int i = 0; i < object.getProject().getQcLevels(); i++) {
                        CustomerQcProjectMapping qpm = new CustomerQcProjectMapping();
                        qpm.setUserCustomerMapping(ucm);
                        qpm.setProject(object.getProject());
                        qpm.setStatus(0);
                        qpm.setQcLevel(Long.valueOf(i + 1));
                        qpms.add(qpm);
                    }
                }
            }
            customerQcProjectMappingRepository.saveAll(qpms);
        }
    }
}

