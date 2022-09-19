package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.service.DataPopulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.bind.annotation.*;



/**
 * REST controller for managing {@link Authority}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SampleDataLoaderResource {

    private final Logger log = LoggerFactory.getLogger(SampleDataLoaderResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Autowired
    TaskExecutor taskExecutor;

    @Autowired
    DataPopulator dataPopulator;
    /**
     * {@code GET  /aidas-authorities/:id} : get the "id" authority.
     *
     * @param dataFileName the id of the authority to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the authority, or with status {@code 404 (Not Found)}.
     */
   @GetMapping("/aidas-sample/{dataFileName}")
    public ResponseEntity<String> loadSampleData(@PathVariable String  dataFileName) {
       TaskDefinition taskDefinition = new TaskDefinition();
       taskDefinition.setActionType(dataFileName);
       dataPopulator.setTaskDefinition(taskDefinition);
       taskExecutor.execute(dataPopulator);
       return ResponseEntity.ok().body("Success");
   }

}
