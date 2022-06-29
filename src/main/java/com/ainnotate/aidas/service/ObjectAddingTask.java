package com.ainnotate.aidas.service;

import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.domain.UserVendorMapping;
import com.ainnotate.aidas.domain.UserVendorMappingObjectMapping;
import com.ainnotate.aidas.repository.ObjectRepository;
import com.ainnotate.aidas.repository.UserRepository;
import com.ainnotate.aidas.repository.UserVendorMappingObjectMappingRepository;
import com.ainnotate.aidas.repository.UserVendorMappingRepository;
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

