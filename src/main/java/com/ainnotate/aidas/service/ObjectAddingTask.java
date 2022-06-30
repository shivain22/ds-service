package com.ainnotate.aidas.service;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.repository.*;
import liquibase.pro.packaged.Q;
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

    @Override
    public void run() {
            if(getDummy()){
                List<UserVendorMapping> userVendorMappings = userVendorMappingRepository.findAll();
                List<UserVendorMappingObjectMapping> userVendorMappingObjectMappings=new ArrayList<>();
                for(UserVendorMapping uvm:userVendorMappings){
                    UserVendorMappingObjectMapping uvmom1 = new UserVendorMappingObjectMapping();
                    uvmom1.setUserVendorMapping(uvm);
                    uvmom1.setObject(object);
                    uvmom1.setStatus(0);
                    userVendorMappingObjectMappings.add(uvmom1);
                }
                userVendorMappingObjectMappingRepository.saveAll(userVendorMappingObjectMappings);
                List<UserCustomerMapping> qcUserCustomerMapping = userCustomerMappingRepository.getAllQcUserCustomerMapping(object.getProject().getCustomer().getId());
                List<QcProjectMapping> qpms = new ArrayList<>();
                for(UserCustomerMapping ucm : qcUserCustomerMapping){
                    if(object.getProject().getQcLevels()!=null) {
                        for( int i=0;i<object.getProject().getQcLevels();i++) {
                            QcProjectMapping qpm = new QcProjectMapping();
                            qpm.setUserCustomerMapping(ucm);
                            qpm.setProject(object.getProject());
                            qpm.setStatus(0);
                            qpm.setQcLevel(Long.valueOf(i+1));
                            qpms.add(qpm);
                        }
                    }
                }
                qcProjectMappingRepository.saveAll(qpms);
            }else{
                Object dummyObject = objectRepository.getDummyObjectOfProject(object.getProject().getId());
                List<UserVendorMappingObjectMapping> uvmoms = userVendorMappingObjectMappingRepository.getAllUserVendorMappingObjectMappingsByObjectId(dummyObject.getId());
                List<UserVendorMappingObjectMapping> userVendorMappingObjectMappings=new ArrayList<>();
                for(UserVendorMappingObjectMapping uvmom:uvmoms){
                    UserVendorMappingObjectMapping uvmom1 = new UserVendorMappingObjectMapping();
                    uvmom1.setUserVendorMapping(uvmom.getUserVendorMapping());
                    uvmom1.setObject(object);
                    uvmom1.setStatus(uvmom.getStatus());
                    userVendorMappingObjectMappings.add(uvmom1);
                }
                userVendorMappingObjectMappingRepository.saveAll(userVendorMappingObjectMappings);
            }
        }

}

