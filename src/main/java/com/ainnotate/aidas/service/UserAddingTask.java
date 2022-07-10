package com.ainnotate.aidas.service;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    QcProjectMappingRepository qcProjectMappingRepository;
    @Autowired
    UserCustomerMappingRepository userCustomerMappingRepository;

    @Autowired
    private AppPropertyRepository appPropertyRepository;

    @Override
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
        List<UserVendorMappingObjectMapping> uvmoms = new ArrayList<>();
            if(userVendorMapping!=null){
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
                        uvmoms.add(uvmom);
                }
                userVendorMappingObjectMappingRepository.saveAll(uvmoms);
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
                qcProjectMappingRepository.saveAll(qpms);
            }
        }
}

