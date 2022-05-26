package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.TaskDefinition;
import com.ainnotate.aidas.service.TaskDefinitionBean;
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
    private TaskDefinitionBean taskDefinitionBean;

    @PostMapping(path="/taskdef", consumes = "application/json", produces="application/json")
    public void scheduleATask(@RequestBody TaskDefinition taskDefinition) {
        taskDefinitionBean.setTaskDefinition(taskDefinition);
        UUID randomUUID = UUID.randomUUID();
        taskSchedulingService.scheduleATask(randomUUID.toString(), taskDefinitionBean, taskDefinition.getCronExpression());
    }

    @GetMapping(path="/remove/{jobid}")
    public void removeJob(@PathVariable String jobid) {
        taskSchedulingService.removeScheduledTask(jobid);
    }
}
