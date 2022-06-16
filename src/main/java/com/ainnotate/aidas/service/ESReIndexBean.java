package com.ainnotate.aidas.service;


import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.domain.Customer;
import com.ainnotate.aidas.domain.Organisation;
import com.ainnotate.aidas.domain.TaskDefinition;
import com.ainnotate.aidas.domain.Upload;
import com.ainnotate.aidas.repository.CustomerRepository;
import com.ainnotate.aidas.repository.OrganisationRepository;
import com.ainnotate.aidas.repository.UploadRepository;
import com.ainnotate.aidas.repository.search.CustomerSearchRepository;
import com.ainnotate.aidas.repository.search.OrganisationSearchRepository;
import com.ainnotate.aidas.web.rest.UserResource;
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

    @Override
    public void run() {
        System.out.println("Running action: " + taskDefinition.getActionType());
        log.debug("Synchonizing organisations with es repo");
        List<Organisation> organisations = organisationRepository.findAll();
        for(Organisation organisation:organisations){
            organisationSearchRepository.save(organisation);
        }
        log.debug("Synchonizing customers with es repo");
        List<Customer> customers = customerRepository.findAll();
        for(Customer customer:customers){
            customerSearchRepository.save(customer);
        }
        System.out.println("With Data: " + taskDefinition.getData());
    }

    public TaskDefinition getTaskDefinition() {
        return taskDefinition;
    }

    public void setTaskDefinition(TaskDefinition taskDefinition) {
        this.taskDefinition = taskDefinition;
    }
}
