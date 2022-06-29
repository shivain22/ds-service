package com.ainnotate.aidas.service;

import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.domain.UserVendorMapping;
import com.ainnotate.aidas.domain.UserVendorMappingObjectMapping;
import com.ainnotate.aidas.repository.ObjectRepository;
import com.ainnotate.aidas.repository.UserRepository;
import com.ainnotate.aidas.repository.UserVendorMappingObjectMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserAddingTask implements  Runnable{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectRepository objectRepository;

    @Autowired
    private UserVendorMappingObjectMappingRepository userVendorMappingObjectMappingRepository;

    public UserVendorMapping getUserVendorMapping() {
        return userVendorMapping;
    }

    public void setUserVendorMapping(UserVendorMapping userVendorMapping) {
        this.userVendorMapping = userVendorMapping;
    }

    private UserVendorMapping userVendorMapping;

    @Override
    public void run() {
        List<UserVendorMappingObjectMapping> uvmoms = new ArrayList<>();
            if(userVendorMapping!=null){
                for(Object o:objectRepository.findAll()){
                    UserVendorMappingObjectMapping uvmom = new UserVendorMappingObjectMapping();
                    uvmom.setUserVendorMapping(userVendorMapping);
                    uvmom.setObject(o);
                    uvmom.setStatus(0);
                    uvmoms.add(uvmom);
                }
                userVendorMappingObjectMappingRepository.saveAll(uvmoms);
            }
        }
}

