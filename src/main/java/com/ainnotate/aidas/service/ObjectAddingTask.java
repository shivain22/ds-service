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
        List<UserVendorMapping> userVendorMappings = userVendorMappingRepository.findAll();
        List<UserVendorMappingObjectMapping> uvmoms=new ArrayList<>();
        for(UserVendorMapping uvm:userVendorMappings){
            UserVendorMappingObjectMapping uvmom = new UserVendorMappingObjectMapping();
            uvmom.setUserVendorMapping(uvm);
            uvmom.setObject(object);
            uvmom.setStatus(0);
            uvmoms.add(uvmom);
        }
        userVendorMappingObjectMappingRepository.saveAll(uvmoms);
        if(getDummy()) {
            List<UserCustomerMapping> qcUserCustomerMapping = userCustomerMappingRepository.getAllQcUserCustomerMapping(object.getProject().getCustomer().getId());
            List<QcProjectMapping> qpms = new ArrayList<>();
            for (UserCustomerMapping ucm : qcUserCustomerMapping) {
                if (object.getProject().getQcLevels() != null) {
                    for (int i = 0; i < object.getProject().getQcLevels(); i++) {
                        QcProjectMapping qpm = new QcProjectMapping();
                        qpm.setUserCustomerMapping(ucm);
                        qpm.setProject(object.getProject());
                        qpm.setStatus(0);
                        qpm.setQcLevel(Long.valueOf(i + 1));
                        qpms.add(qpm);
                    }
                }
            }
            qcProjectMappingRepository.saveAll(qpms);
        }
    }
}

