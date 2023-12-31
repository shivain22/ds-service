package com.ainnotate.aidas.service;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;


import java.util.ArrayList;
import java.util.List;


public class ObjectAddingTask {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectRepository objectRepository;

    @Autowired
    private UserVendorMappingObjectMappingRepository userVendorMappingObjectMappingRepository;

    @Autowired
    private UserVendorMappingProjectMappingRepository userVendorMappingProjectMappingRepository;

    @Autowired
    QcProjectMappingRepository qcProjectMappingRepository;

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

    private List<Object> dynamicObjects;

    public List<Object> getDynamicObjects() {
        return dynamicObjects;
    }

    public void setDynamicObjects(List<Object> dynamicObjects) {
        this.dynamicObjects = dynamicObjects;
    }

    @Async
    public void run() {


        List<Long> vendorWithUserStatusOne = userVendorMappingObjectMappingRepository.getVendorsWhoseUsersAreHavingStatusOne(object.getProject().getId());
        List<UserVendorMapping> userVendorMappings = userVendorMappingRepository.getAllUserVendorMappingsOfVendorUsers();
        List<UserVendorMappingObjectMapping> uvmoms=new ArrayList<>();
        List<UserVendorMappingProjectMapping> uvmpoms=new ArrayList<>();

        for(UserVendorMapping uvm:userVendorMappings){
            UserVendorMappingProjectMapping uvmpm = userVendorMappingProjectMappingRepository.findByUserVendorMappingIdProjectId(uvm.getId(),object.getProject().getId());
            List<UserVendorMappingProjectMapping> uvmpms =  new ArrayList<>();
            int batchSize = 5000;
            int count =0;
            if(uvmpm==null) {
                uvmpm = new UserVendorMappingProjectMapping();
                uvmpm.setProject(object.getProject());
                uvmpm.setUserVendorMapping(uvm);
                uvmpm.setStatus(0);
                uvmpms.add(uvmpm);
                if(count%batchSize==0){
                    userVendorMappingProjectMappingRepository.saveAll(uvmpms);
                    uvmpms = new ArrayList<>();
                }
                count++;
            }

            UserVendorMappingObjectMapping uvmom = userVendorMappingObjectMappingRepository.findByUserVendorMappingObject(uvm.getId(),object.getId());
            if(uvmom==null) {
                uvmom = new UserVendorMappingObjectMapping();
                uvmom.setUserVendorMapping(uvm);
                uvmom.setObject(object);
            }
            if(vendorWithUserStatusOne.contains(uvm.getVendor().getId())){
                uvmom.setStatus(1);
            }else{
                uvmom.setStatus(0);
            }
            userVendorMappingObjectMappingRepository.save(uvmom);
        }

        /*if(getDummy()) {
            List<UserCustomerMapping> qcUserCustomerMapping = userCustomerMappingRepository.getAllQcUserCustomerMapping(object.getProject().getCustomer().getId());
            List<qcProjectMapping> qpms = new ArrayList<>();
            for (UserCustomerMapping ucm : qcUserCustomerMapping) {
                if (object.getProject().getQcLevels() != null) {
                    for (int i = 0; i < object.getProject().getQcLevels(); i++) {
                        qcProjectMapping qpm = new qcProjectMapping();
                        qpm.setUserCustomerMapping(ucm);
                        qpm.setProject(object.getProject());
                        qpm.setStatus(0);
                        qpm.setQcLevel(i + 1);
                        qcProjectMappingRepository.save(qpm);
                    }
                }
            }
        }*/
    }

    @Async
    public void runBulkObjects() {
        List<UserCustomerMapping> qcUserCustomerMapping = userCustomerMappingRepository.getAllQcUserCustomerMapping(object.getProject().getCustomer().getId());
        List<QcProjectMapping> qpms = new ArrayList<>();
        for (UserCustomerMapping ucm : qcUserCustomerMapping) {
            if (object.getProject().getQcLevels() != null) {
                for (int i = 0; i < object.getProject().getQcLevels(); i++) {
                    QcProjectMapping qpm = new QcProjectMapping();
                    //qpm.setUserCustomerMapping(ucm);
                    qpm.setProject(object.getProject());
                    qpm.setStatus(0);
                    qpm.setQcLevel(i + 1);
                    qcProjectMappingRepository.save(qpm);
                }
            }
        }

        List<Long> vendorWithUserStatusOne = userVendorMappingObjectMappingRepository.getVendorsWhoseUsersAreHavingStatusOne(object.getProject().getId());
        List<UserVendorMapping> userVendorMappings = userVendorMappingRepository.getAllUserVendorMappingsOfVendorUsers();
        List<UserVendorMappingObjectMapping> uvmoms = new ArrayList<>();
        List<UserVendorMappingProjectMapping> uvmpoms = new ArrayList<>();

        for (Object o : dynamicObjects){
            for (UserVendorMapping uvm : userVendorMappings) {
                UserVendorMappingProjectMapping uvmpm = userVendorMappingProjectMappingRepository.findByUserVendorMappingIdProjectId(uvm.getId(), object.getProject().getId());
                if (uvmpm == null) {
                    uvmpm = new UserVendorMappingProjectMapping();
                    uvmpm.setProject(object.getProject());
                    uvmpm.setUserVendorMapping(uvm);
                    uvmpm.setStatus(0);
                    userVendorMappingProjectMappingRepository.save(uvmpm);
                }
                UserVendorMappingObjectMapping uvmom = userVendorMappingObjectMappingRepository.findByUserVendorMappingObject(uvm.getId(), o.getId());
                if (uvmom == null) {
                    uvmom = new UserVendorMappingObjectMapping();
                    uvmom.setUserVendorMapping(uvm);
                    uvmom.setObject(o);
                }
                if (vendorWithUserStatusOne.contains(uvm.getVendor().getId())) {
                    uvmom.setStatus(1);
                } else {
                    uvmom.setStatus(0);
                }
                userVendorMappingObjectMappingRepository.save(uvmom);
            }
        }
    }

    @Async
    public void runQcUserAdd(){
        List<UserCustomerMapping> qcUserCustomerMapping = userCustomerMappingRepository.getAllQcUserCustomerMapping(object.getProject().getCustomer().getId());
        List<QcProjectMapping> qpms = new ArrayList<>();
        for (UserCustomerMapping ucm : qcUserCustomerMapping) {
            if (object.getProject().getQcLevels() != null) {
                for (int i = 0; i < object.getProject().getQcLevels(); i++) {
                    QcProjectMapping qpm = new QcProjectMapping();
                    //qpm.setUserCustomerMapping(ucm);
                    qpm.setProject(object.getProject());
                    qpm.setStatus(0);
                    qpm.setQcLevel(i + 1);
                    qcProjectMappingRepository.save(qpm);
                }
            }
        }

    }
}

