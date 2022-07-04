package com.ainnotate.aidas.service;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.repository.*;
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
    private ProjectRepository projectRepository;

    @Autowired
    private UserVendorMappingObjectMappingRepository userVendorMappingObjectMappingRepository;

    public UserVendorMapping getUserVendorMapping() {
        return userVendorMapping;
    }

    public void setUserVendorMapping(UserVendorMapping userVendorMapping) {
        this.userVendorMapping = userVendorMapping;
    }

    private UserVendorMapping userVendorMapping;

    private UserCustomerMapping userCustomerMapping;

    public UserCustomerMapping getUserCustomerMapping() {
        return userCustomerMapping;
    }

    public void setUserCustomerMapping(UserCustomerMapping userCustomerMapping) {
        this.userCustomerMapping = userCustomerMapping;
    }

    @Autowired
    QcProjectMappingRepository qcProjectMappingRepository;
    @Autowired
    UserCustomerMappingRepository userCustomerMappingRepository;

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
            if(userCustomerMapping!=null){
                List<Project> projects = projectRepository.findAllByAidasCustomer(userCustomerMapping.getCustomer().getId());
                List<QcProjectMapping> qpms = new ArrayList<>();
                for(Project p : projects){
                    if(p.getQcLevels()!=null) {
                        for( int i=0;i<p.getQcLevels();i++) {
                            QcProjectMapping qpm = new QcProjectMapping();
                            qpm.setUserCustomerMapping(userCustomerMapping);
                            qpm.setProject(p);
                            qpm.setStatus(0);
                            qpm.setQcLevel(Long.valueOf(i+1));
                            qpms.add(qpm);
                        }
                    }
                }
                qcProjectMappingRepository.saveAll(qpms);
            }
        }
}

