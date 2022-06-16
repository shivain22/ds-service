package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.TaskDefinition;
import com.ainnotate.aidas.service.DataPopulatorBean;
import com.ainnotate.aidas.service.ESReIndexBean;
import com.ainnotate.aidas.service.QcCleanUpBean;
import com.ainnotate.aidas.service.TaskSchedulingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api")
public class JobSchedulingController {

    @Autowired
    private TaskSchedulingService taskSchedulingService;

    @Autowired
    private QcCleanUpBean qcCleanUpBean;

    @Autowired
    private ESReIndexBean esReIndexBean;

    @Autowired
    private DataPopulatorBean dataPopulatorBean;

    @PostMapping(path="/taskdef/qc-cleanup", consumes = "application/json", produces="application/json")
    public void scheduleQcCleanUpTask(@RequestBody TaskDefinition taskDefinition) {
        qcCleanUpBean.setTaskDefinition(taskDefinition);
        UUID randomUUID = UUID.randomUUID();
        taskSchedulingService.scheduleATask(randomUUID.toString(), qcCleanUpBean, taskDefinition.getCronExpression());
    }

    @PostMapping(path="/taskdef/es-reindex", consumes = "application/json", produces="application/json")
    public void scheduleESReindexTask(@RequestBody TaskDefinition taskDefinition) {
        esReIndexBean.setTaskDefinition(taskDefinition);
        UUID randomUUID = UUID.randomUUID();
        taskSchedulingService.scheduleATask(randomUUID.toString(), esReIndexBean, taskDefinition.getCronExpression());
    }

    @PostMapping(path="/taskdef/load-dummy-data", consumes = "application/json", produces="application/json")
    public void loadDummyData(@RequestBody TaskDefinition taskDefinition) {
        dataPopulatorBean.setTaskDefinition(taskDefinition);
        UUID randomUUID = UUID.randomUUID();
        taskSchedulingService.scheduleATask(randomUUID.toString(), dataPopulatorBean, taskDefinition.getCronExpression());
    }

    @GetMapping(path="/remove/{jobid}")
    public void removeJob(@PathVariable String jobid) {
        taskSchedulingService.removeScheduledTask(jobid);
    }
}
