package com.ainnotate.aidas.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ainnotate.aidas.domain.Upload;
import com.ainnotate.aidas.domain.UploadMetaData;
import com.ainnotate.aidas.dto.UploadDTO;
import com.ainnotate.aidas.repository.UploadMetaDataRepository;
import com.ainnotate.aidas.repository.UploadRepository;

@Service
public class UploadMetaDataService implements Runnable{

	@Autowired
	UploadMetaDataRepository uploadMetaDataRepository;
	
	@Autowired
	UploadRepository uploadRepository;
	
	Long uploadId;
	com.ainnotate.aidas.domain.Object object;
	
	UploadDTO uploadDto;
	
	@Override
	public void run() {
		
	}

	@Async
	public void runAsync() {
		Upload u = null;
		while(u==null) {
			try {
			Thread.sleep(100);
			u = uploadRepository.getById(uploadId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(u!=null) {
			List<String> uploadMetaDataKeys = new ArrayList<>();
			for (Map.Entry<String, String> entry : uploadDto.getUploadMetadata().entrySet()) {
				uploadMetaDataKeys.add(entry.getKey());
			}
			List<UploadMetaData> metadatas = uploadMetaDataRepository.getAllUploadMetaDatasProjectProperty(uploadId,uploadMetaDataKeys); 
			metadatas.addAll(uploadMetaDataRepository.getAllUploadMetaDatasObjectProperty(uploadId,uploadMetaDataKeys)); 
			for(UploadMetaData umd:metadatas) {
				if(umd.getProjectProperty()!=null) {
					umd.setValue(uploadDto.getUploadMetadata().get(umd.getProjectProperty().getProperty().getName()));
				}
				if(umd.getObjectProperty()!=null) {
					umd.setValue(uploadDto.getUploadMetadata().get(umd.getObjectProperty().getProperty().getName()));
				}
			}
			uploadMetaDataRepository.saveAll(metadatas);
		}
	}
	public UploadMetaDataRepository getUploadMetaDataRepository() {
		return uploadMetaDataRepository;
	}

	public void setUploadMetaDataRepository(UploadMetaDataRepository uploadMetaDataRepository) {
		this.uploadMetaDataRepository = uploadMetaDataRepository;
	}

	
	
	public Long getUploadId() {
		return uploadId;
	}

	public void setUploadId(Long uploadId) {
		this.uploadId = uploadId;
	}

	public com.ainnotate.aidas.domain.Object getObject() {
		return object;
	}

	public void setObject(com.ainnotate.aidas.domain.Object object) {
		this.object = object;
	}

	public UploadDTO getUploadDto() {
		return uploadDto;
	}

	public void setUploadDto(UploadDTO uploadDto) {
		this.uploadDto = uploadDto;
	}

	
}
