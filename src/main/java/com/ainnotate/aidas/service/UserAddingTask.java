package com.ainnotate.aidas.service;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class UserAddingTask {

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

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserCustomerMapping getUserCustomerMapping() {
        return userCustomerMapping;
    }

    public void setUserCustomerMapping(UserCustomerMapping userCustomerMapping) {
        this.userCustomerMapping = userCustomerMapping;
    }

    @Autowired
    CustomerQcProjectMappingRepository customerQcProjectMappingRepository;
    @Autowired
    UserCustomerMappingRepository userCustomerMappingRepository;

    @Autowired
    UserVendorMappingProjectMappingRepository userVendorMappingProjectMappingRepository;
    @Autowired
    private AppPropertyRepository appPropertyRepository;

    @Async
    public void run() {
        if(user!=null) {
            Set<AppProperty> appProperties = appPropertyRepository.getAppPropertyOfUser(-1l);
            List<AppProperty> appProperties1 = new ArrayList<>();
            for (AppProperty ap : appProperties) {
                AppProperty app = new AppProperty();
                app.setName(ap.getName());
                app.setValue(ap.getValue());
                app.setUser(user);
                appProperties1.add(app);
            }
            appPropertyRepository.saveAll(appProperties1);
        }
            if(userVendorMapping!=null){
                for(Project p: projectRepository.findAll()){
                    UserVendorMappingProjectMapping uvpom = new UserVendorMappingProjectMapping();
                    uvpom.setProject(p);
                    uvpom.setUserVendorMapping(userVendorMapping);
                    userVendorMappingProjectMappingRepository.save(uvpom);
                }
                for(Object o:objectRepository.findAll()){
                        List<Long> vendorWithUserStatusOne = userVendorMappingObjectMappingRepository.getVendorsWhoseUsersAreHavingStatusOne(o.getProject().getId());
                        UserVendorMappingObjectMapping uvmom = new UserVendorMappingObjectMapping();
                        uvmom.setUserVendorMapping(userVendorMapping);
                        uvmom.setObject(o);
                        if(vendorWithUserStatusOne.contains(userVendorMapping.getVendor().getId())){
                            uvmom.setStatus(0);
                        }else{
                            uvmom.setStatus(0);
                        }
                        userVendorMappingObjectMappingRepository.save(uvmom);
                }

            }
            if(userCustomerMapping!=null){
                List<Project> projects = projectRepository.findAllByAidasCustomer(userCustomerMapping.getCustomer().getId());
                List<CustomerQcProjectMapping> qpms = new ArrayList<>();
                for(Project p : projects){
                    if(p.getQcLevels()!=null) {
                        for( int i=0;i<p.getQcLevels();i++) {
                            CustomerQcProjectMapping qpm = new CustomerQcProjectMapping();
                            qpm.setUserCustomerMapping(userCustomerMapping);
                            qpm.setProject(p);
                            qpm.setStatus(0);
                            qpm.setQcLevel(Long.valueOf(i+1));
                            qpms.add(qpm);
                        }
                    }
                }
                customerQcProjectMappingRepository.saveAll(qpms);
            }
        }
}

