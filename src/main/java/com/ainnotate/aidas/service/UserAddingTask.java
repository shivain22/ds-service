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

    private boolean addProperty=false;

    private boolean addVendorMappingObjectMapping=false;

    private boolean addCustomerMappingQcProjectMapping=false;

    public boolean isAddCustomerMappingQcProjectMapping() {
        return addCustomerMappingQcProjectMapping;
    }

    public void setAddCustomerMappingQcProjectMapping(boolean addCustomerMappingQcProjectMapping) {
        this.addCustomerMappingQcProjectMapping = addCustomerMappingQcProjectMapping;
    }

    public boolean isAddVendorMappingObjectMapping() {
        return addVendorMappingObjectMapping;
    }

    public void setAddVendorMappingObjectMapping(boolean addVendorMappingObjectMapping) {
        this.addVendorMappingObjectMapping = addVendorMappingObjectMapping;
    }

    public boolean isAddProperty() {
        return addProperty;
    }

    public void setAddProperty(boolean addProperty) {
        this.addProperty = addProperty;
    }

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
        if(user!=null && addProperty) {
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
            if(userVendorMapping!=null && addVendorMappingObjectMapping){
                for(Project p: projectRepository.findAll()){
                    UserVendorMappingProjectMapping uvmpm = userVendorMappingProjectMappingRepository.findByUserVendorMappingIdProjectId(userVendorMapping.getId(),p.getId());
                    if(uvmpm==null) {
                        uvmpm = new UserVendorMappingProjectMapping();
                        uvmpm.setProject(p);
                        uvmpm.setStatus(0);
                        uvmpm.setUserVendorMapping(userVendorMapping);
                        userVendorMappingProjectMappingRepository.save(uvmpm);
                    }
                }
                for(Object o:objectRepository.findAll()){
                        List<Long> vendorWithUserStatusOne = userVendorMappingObjectMappingRepository.getVendorsWhoseUsersAreHavingStatusOne(o.getProject().getId());
                        UserVendorMappingObjectMapping uvmom = userVendorMappingObjectMappingRepository.findByUserVendorMappingObject(userVendorMapping.getId(),o.getId());
                        if(uvmom==null) {
                            uvmom = new UserVendorMappingObjectMapping();
                            uvmom.setUserVendorMapping(userVendorMapping);
                            uvmom.setObject(o);
                        }
                        if (vendorWithUserStatusOne.contains(userVendorMapping.getVendor().getId())) {
                            uvmom.setStatus(0);
                        } else {
                            uvmom.setStatus(0);
                        }
                        userVendorMappingObjectMappingRepository.save(uvmom);
                }

            }
            if(userCustomerMapping!=null && addCustomerMappingQcProjectMapping){
                List<Project> projects = projectRepository.findAllByAidasCustomer(userCustomerMapping.getCustomer().getId());
                for(Project p : projects){
                    if(p.getQcLevels()!=null) {
                        for( int i=1;i<=p.getQcLevels();i++) {
                            System.out.println("userCustomerMappingId="+userCustomerMapping.getId()+"projcetId="+p.getId()+"level="+i);
                            CustomerQcProjectMapping qpm = customerQcProjectMappingRepository.getQcProjectMappingByProjectAndCustomerAndUserAndLevel(p.getId(),userCustomerMapping.getId(),i);
                            if(qpm!=null){
                                System.out.println("userCustomerMappingId="+userCustomerMapping.getId()+"projcetId="+p.getId()+"level="+i+"qpmId="+qpm.getId());
                            }
                            if(qpm==null) {
                                qpm =  new CustomerQcProjectMapping();
                                qpm.setUserCustomerMapping(userCustomerMapping);
                                qpm.setProject(p);
                                qpm.setStatus(0);
                                qpm.setQcLevel(Long.valueOf(i));
                                customerQcProjectMappingRepository.save(qpm);
                            }
                        }
                    }
                }
            }
        }
}

