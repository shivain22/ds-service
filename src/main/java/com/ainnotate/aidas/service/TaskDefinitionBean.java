package com.ainnotate.aidas.service;


import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.domain.AidasUpload;
import com.ainnotate.aidas.domain.TaskDefinition;
import com.ainnotate.aidas.repository.AidasUploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskDefinitionBean implements Runnable {

    private TaskDefinition taskDefinition;

    @Autowired
    private AidasUploadRepository aidasUploadRepository;

    @Override
    public void run() {
        System.out.println("Running action: " + taskDefinition.getActionType());
        List<AidasUpload> qcPendingUploadsForMoreThan10Mins = aidasUploadRepository.findUploadsHeldByQcForMoreThan10Mins();
        for(AidasUpload au:qcPendingUploadsForMoreThan10Mins){
            au.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
            au.setQcDoneBy(null);
            au.setQcStartDate(null);
            aidasUploadRepository.save(au);
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
