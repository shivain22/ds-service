package com.ainnotate.aidas.service;


import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.domain.Upload;
import com.ainnotate.aidas.domain.TaskDefinition;
import com.ainnotate.aidas.repository.UploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskDefinitionBean implements Runnable {

    private TaskDefinition taskDefinition;

    @Autowired
    private UploadRepository uploadRepository;

    @Override
    public void run() {
        System.out.println("Running action: " + taskDefinition.getActionType());
        List<Upload> qcPendingUploadsForMoreThan10Mins = uploadRepository.findUploadsHeldByQcForMoreThan10Mins();
        for(Upload au:qcPendingUploadsForMoreThan10Mins){
            au.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
            au.setQcDoneBy(null);
            au.setQcStartDate(null);
            uploadRepository.save(au);
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
