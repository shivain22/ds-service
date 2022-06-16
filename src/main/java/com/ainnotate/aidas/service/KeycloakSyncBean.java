package com.ainnotate.aidas.service;


import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.domain.TaskDefinition;
import com.ainnotate.aidas.domain.Upload;
import com.ainnotate.aidas.repository.UploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeycloakSyncBean implements Runnable {

    private TaskDefinition taskDefinition;

    @Autowired
    private UploadRepository uploadRepository;

    @Override
    public void run() {
        System.out.println("Running action: " + taskDefinition.getActionType());

        System.out.println("With Data: " + taskDefinition.getData());
    }

    public TaskDefinition getTaskDefinition() {
        return taskDefinition;
    }

    public void setTaskDefinition(TaskDefinition taskDefinition) {
        this.taskDefinition = taskDefinition;
    }
}
