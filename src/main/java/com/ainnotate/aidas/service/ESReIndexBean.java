package com.ainnotate.aidas.service;
import com.ainnotate.aidas.domain.TaskDefinition;
import com.ainnotate.aidas.repository.*;
import com.ainnotate.aidas.repository.search.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ESReIndexBean implements Runnable {

    private TaskDefinition taskDefinition;

    private final Logger log = LoggerFactory.getLogger(ESReIndexBean.class);

    @Autowired
    private OrganisationSearchRepository organisationSearchRepository;
    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private CustomerSearchRepository customerSearchRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProjectSearchRepository projectSearchRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ObjectSearchRepository objectSearchRepository;
    @Autowired
    private ObjectRepository objectRepository;

    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private VendorSearchRepository vendorSearchRepository;

    @Autowired
    private UserSearchRepository userSearchRepository;
    @Autowired
    private UserRepository userRepository;



    @Override
    public void run() {
        System.out.println("Running action: " + taskDefinition.getActionType());
        log.debug("Synchonizing organisations with es repo");
        organisationSearchRepository.saveAll(organisationRepository.findAll());
        log.debug("Synchonizing customers with es repo");
        customerSearchRepository.saveAll(customerRepository.findAll());
        log.debug("Synchonizing vendors with es repo");
        vendorSearchRepository.saveAll(vendorRepository.findAll());
        log.debug("Synchonizing projects with es repo");
        projectSearchRepository.saveAll(projectRepository.findAll());
        log.debug("Synchonizing objects with es repo");
        objectSearchRepository.saveAll(objectRepository.findAll());
        System.out.println("With Data: " + taskDefinition.getData());
    }

    public TaskDefinition getTaskDefinition() {
        return taskDefinition;
    }

    public void setTaskDefinition(TaskDefinition taskDefinition) {
        this.taskDefinition = taskDefinition;
    }
}
