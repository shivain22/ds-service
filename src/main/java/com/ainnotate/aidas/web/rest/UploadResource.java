package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.dto.*;
import com.ainnotate.aidas.repository.*;
import com.ainnotate.aidas.repository.search.UploadSearchRepository;
import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.security.SecurityUtils;
import com.ainnotate.aidas.service.DownloadUploadS3;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;


import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link Upload}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UploadResource {

    private final Logger log = LoggerFactory.getLogger(UploadResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasUpload";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private static final String PATTERN_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectRepository objectRepository;
    @Autowired
    private UserVendorMappingObjectMappingRepository userVendorMappingObjectMappingRepository;

    @Autowired
    private ProjectPropertyRepository projectPropertyRepository;

    @Autowired
    UserVendorMappingProjectMappingRepository userVendorMappingProjectMappingRepository;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private ObjectPropertyRepository objectPropertyRepository;

    private final UploadRepository uploadRepository;

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerQcProjectMappingRepository customerQcProjectMappingRepository;

    @Autowired
    private UploadCustomerQcProjectMappingBatchInfoRepository uploadCustomerQcProjectMappingBatchInfoRepository;

    private final UploadSearchRepository aidasUploadSearchRepository;

    @Autowired
    private UploadMetaDataRepository uploadMetaDataRepository;

    @Autowired
    private UploadRejectReasonRepository uploadRejectReasonRepository;

    @Autowired
    private UploadRejectReasonMappingRepository uploadRejectReasonMappingRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectQcLevelConfigurationsRepository projectQcLevelConfigurationsRepository;

    @Autowired
    private CustomerQcProjectMappingBatchMappingRepository customerQcProjectMappingBatchMappingRepository;

    public UploadResource(UploadRepository uploadRepository, UploadSearchRepository aidasUploadSearchRepository) {
        this.uploadRepository = uploadRepository;
        this.aidasUploadSearchRepository = aidasUploadSearchRepository;
    }

    Integer totalApproved = 0;
    Integer totalRejected = 0;
    Integer totalPending = 0;

    /**
     * {@code POST  /aidas-uploads} : Create a new upload.
     *
     * @param upload the upload to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new upload, or with status {@code 400 (Bad Request)} if the upload has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-uploads")
    public ResponseEntity<Upload> createAidasUpload(@Valid @RequestBody Upload upload) throws URISyntaxException {
        log.debug("REST request to save AidasUpload : {}", upload);
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Authority authority = user.getAuthority();
        if(authority.getName().equals(AidasConstants.ADMIN)){

        }
        if(authority.getName().equals(AidasConstants.ORG_ADMIN)){
            throw new BadRequestAlertException("You can not upload as ORG ADMIN", ENTITY_NAME, "idexists");
        }
        if(authority.getName().equals(AidasConstants.CUSTOMER_ADMIN)){
            throw new BadRequestAlertException("You can not upload as CUSTOMER ADMIN", ENTITY_NAME, "idexists");
        }
        if(authority.getName().equals(AidasConstants.VENDOR_ADMIN)){
            throw new BadRequestAlertException("You can not upload as VENDOR ADMIN", ENTITY_NAME, "idexists");
        }
        if(authority.getName().equals(AidasConstants.VENDOR_USER)){
            if (upload.getId() != null) {
                throw new BadRequestAlertException("A new upload cannot already have an ID", ENTITY_NAME, "idexists");
            }


            UserVendorMappingObjectMapping auaom = userVendorMappingObjectMappingRepository.getById(upload.getUserVendorMappingObjectMapping().getId());
            Object object = objectRepository.getByIdForUpload(auaom.getObject().getId());//auaom.getObject();
            Project project = object.getProject();
            Long mandatoryProjectProperties = projectPropertyRepository.countProjectPropertyByProjectAndOptional(project.getId(),AidasConstants.AIDAS_PROPERTY_REQUIRED);
            Long mandatoryObjectProperties = objectPropertyRepository.countObjectProperties(object.getId(),AidasConstants.AIDAS_PROPERTY_REQUIRED);

            if(mandatoryObjectProperties==0 && mandatoryProjectProperties==0){
                upload.setMetadataStatus(AidasConstants.AIDAS_UPLOAD_METADATA_REQUIRED);
            }else {
                upload.setMetadataStatus(AidasConstants.AIDAS_UPLOAD_METADATA_COMPLETED);
            }
            upload.setStatus(AidasConstants.AIDAS_UPLOAD_PENDING);
            upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
            upload.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_PENDING);
            Upload result = uploadRepository.save(upload);

            aidasUploadSearchRepository.save(result);
            return ResponseEntity
                .created(new URI("/api/aidas-uploads/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                .body(result);
        }
        throw new BadRequestAlertException("You can not upload as there was an error internally", ENTITY_NAME, "idexists");
    }


    /**
     * {@code POST  /aidas-uploads} : Create a new upload.
     *
     * @param uploadMetadataDTOS the list of uploadMetadata to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new upload, or with status {@code 400 (Bad Request)} if the upload has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-uploads/metadata")
    public ResponseEntity<List<UploadMetadataDTO>> submitMetadata(@RequestBody List<UploadMetadataDTO> uploadMetadataDTOS) throws URISyntaxException {
        log.debug("REST request to save AidasUpload : {}", uploadMetadataDTOS);
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Authority authority = user.getAuthority();
        if(authority.getName().equals(AidasConstants.VENDOR_USER)){
            for(UploadMetadataDTO umd:uploadMetadataDTOS){
                UploadDTO uploadDTO = new UploadDTO();
                Upload upload = uploadRepository.getById(umd.getUploadId());
                uploadDTO.setUploadId(upload.getId());
                uploadDTO.setName(upload.getUserVendorMappingObjectMapping().getObject().getName());
                uploadDTO.setObjectKey(upload.getUploadUrl());
                if(umd.getProjectProperty()!=null && umd.getProjectProperty() && umd.getProjectPropertyId()!=null){
                    UploadMetaData umdpp = uploadMetaDataRepository.getUploadMetaDataByProjectPropertyId(umd.getUploadId(),umd.getProjectPropertyId());
                    if(umdpp.getProjectProperty().getOptional().equals(0)){
                        if(umd.getValue()==null || (umd.getValue()!=null && umd.getValue().trim().length()==0)){
                            umd.setFailed(true);
                        }
                    }
                    umdpp.setValue(umd.getValue());
                    uploadMetaDataRepository.save(umdpp);
                }
                if(umd.getProjectProperty()!=null && !umd.getProjectProperty() && umd.getObjectPropertyId()!=null){
                    UploadMetaData umdop = uploadMetaDataRepository.getUploadMetaDataByObjectPropertyId(umd.getUploadId(),umd.getObjectPropertyId());
                    if(umdop.getObjectProperty().getOptional().equals(0)){
                        if(umd.getValue()==null || (umd.getValue()!=null && umd.getValue().trim().length()==0)){
                            umd.setFailed(true);
                        }
                    }
                    umdop.setValue(umd.getValue());
                    uploadMetaDataRepository.save(umdop);
                }
                umd.setUploadDTO(uploadDTO);
                upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
                uploadRepository.save(upload);
            }
            return ResponseEntity.ok().body(uploadMetadataDTOS);
        }
        throw new BadRequestAlertException("You can not upload as there was an error internally", ENTITY_NAME, "idexists");
    }

    /**
     * {@code POST  /aidas-uploads} : Create a new upload.
     *
     * @param uploadIds the list of uploadMetadata to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new upload, or with status {@code 400 (Bad Request)} if the upload has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-uploads/qc")
    public ResponseEntity<List<UploadMetadataDTO>> submitForQc(@Valid @RequestBody List<Long> uploadIds) throws URISyntaxException {
        log.debug("REST request to save AidasUpload : {}", uploadIds);
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Authority authority = user.getAuthority();
        List<UploadMetadataDTO> uploadMetadataDTOS = new ArrayList<>();
        List<Upload> successUploads = new ArrayList<>();
        if(authority.getName().equals(AidasConstants.VENDOR_USER)){
            for(Long upId:uploadIds){
                int i=0;
                Upload upload = uploadRepository.getUploadById(upId);
               List<UploadMetadataDTO> umds = uploadMetaDataRepository.getAllUploadMetaDataForUpload(upId);
                    for (UploadMetadataDTO umd : umds) {
                        UploadDTO uploadDTO = new UploadDTO();
                        uploadDTO.setUploadId(upload.getId());
                        uploadDTO.setName(upload.getUserVendorMappingObjectMapping().getObject().getName());
                        uploadDTO.setObjectKey(upload.getUploadUrl());
                        umd.setUploadId(upId);
                        if (umd.getProjectPropertyId() != null) {
                            UploadMetaData umdpp = uploadMetaDataRepository.getUploadMetaDataByProjectPropertyId(umd.getUploadId(), umd.getProjectPropertyId());
                            if (umdpp.getProjectProperty().getOptional().equals(0)) {
                                if (umd.getValue() == null || (umd.getValue() != null && umd.getValue().trim().length() == 0)) {
                                    umd.setFailed(true);
                                    i++;
                                } else {
                                    umd.setFailed(false);
                                }
                            }
                        } else if (umd.getObjectPropertyId() != null) {
                            UploadMetaData umdop = uploadMetaDataRepository.getUploadMetaDataByObjectPropertyId(umd.getUploadId(), umd.getObjectPropertyId());
                            if (umdop.getObjectProperty().getOptional().equals(0)) {
                                if (umd.getValue() == null || (umd.getValue() != null && umd.getValue().trim().length() == 0)) {
                                    umd.setFailed(true);
                                    i++;
                                } else {
                                    umd.setFailed(false);
                                }
                            }
                        }
                        umd.setUploadDTO(uploadDTO);
                        uploadMetadataDTOS.add(umd);
                    }
                    if(i==0){
                        successUploads.add(upload);
                    }
                }
            }
        for(Upload u:successUploads){
            uploadRepository.updateMetadataStatus(u.getId(),AidasConstants.AIDAS_UPLOAD_METADATA_COMPLETED);
        }
        //uploadRepository.saveAllAndFlush(successUploads);
            return ResponseEntity.ok().body(uploadMetadataDTOS);
    }


    @GetMapping("/aidas-objects/{id}/more-required")
    public ResponseEntity<Integer> getObjectStatus( @PathVariable(value = "id", required = false) final Long objectId) {
        log.debug("REST request to get a page of AidasObjects");
        Object object = objectRepository.getById(objectId);
        Integer moreRequired = object.getNumberOfUploadsRequired()-((object.getTotalApproved()+object.getTotalPending()));
        return ResponseEntity.ok().body(moreRequired);
    }

    /**
     * {@code POST  /aidas-uploads/dto} : Create a new upload.
     *
     * @param uploadDto the upload to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new upload, or with status {@code 400 (Bad Request)} if the upload has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-uploads/dto")
    public ResponseEntity<Upload> createAidasUploadFromDto( @RequestBody UploadDTO uploadDto) throws URISyntaxException {
        //throw new BadRequestAlertException("Total uploads exceeded the limit.... ", ENTITY_NAME, "idexists");
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to save AidasUpload : {}", uploadDto);
        Authority authority = user.getAuthority();
        if(authority.getName().equals(AidasConstants.VENDOR_USER)) {
            if (uploadDto.getUserId() == null) {
                throw new BadRequestAlertException("No User Id", ENTITY_NAME, "idexists");
            }
            if (uploadDto.getObjectId() == null) {
                throw new BadRequestAlertException("No Object Id", ENTITY_NAME, "idexists");
            }
            if (uploadDto.getUploadUrl() == null) {
                throw new BadRequestAlertException("No Upload URL Id", ENTITY_NAME, "idexists");
            }
            if (uploadDto.getEtag() == null) {
                throw new BadRequestAlertException("No Etag Id", ENTITY_NAME, "idexists");
            }
            Upload upload = new Upload();
            Object object = objectRepository.getByIdForUpload(uploadDto.getObjectId());
            Project project = object.getProject();
            UserVendorMappingObjectMapping uvmom = userVendorMappingObjectMappingRepository.findByUserObject(uploadDto.getUserId(), uploadDto.getObjectId());
            UserVendorMappingProjectMapping uvmpm = userVendorMappingProjectMappingRepository.findByUserVendorMappingIdProjectId(uvmom.getUserVendorMapping().getId(),project.getId());

            if (uvmom != null ) {
                System.out.println(user.getFirstName()+""+object.getTotalRequired());
                if(object.getTotalRequired() > 0){
                    upload.setUserVendorMappingObjectMapping(uvmom);
                    upload.setDateUploaded(Instant.now());
                    upload.setName(uploadDto.getObjectKey());
                    upload.setUploadUrl(uploadDto.getUploadUrl());
                    upload.setUploadEtag(uploadDto.getEtag());
                    upload.setObjectKey(uploadDto.getObjectKey());
                    upload.setCurrentQcLevel(1);
                    upload.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_PENDING);
                    upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
                    try {
                        Upload result = uploadRepository.save(upload);
                        Object o = result.getUserVendorMappingObjectMapping().getObject();
                        List<ProjectProperty> projectProperties = projectPropertyRepository.findAllProjectProperty(project.getId());
                        List<ObjectProperty> objectProperties = objectPropertyRepository.getAllObjectPropertyForObject(object.getId());
                        List<UploadMetaData> uploadMetaDataList = new ArrayList<>();
                        for (ProjectProperty pp : projectProperties) {
                            UploadMetaData umd = new UploadMetaData();
                            umd.setProjectProperty(pp);
                            umd.setUpload(upload);
                            if (uploadDto.getUploadMetadata().get(pp.getProperty().getName().trim()) != null) {
                                umd.setValue(uploadDto.getUploadMetadata().get(pp.getProperty().getName().trim()));
                            } else {
                                umd.setValue(" ");
                            }
                            umd.setStatus(1);
                            uploadMetaDataList.add(umd);
                        }
                        for (ObjectProperty op : objectProperties) {
                            UploadMetaData umd = new UploadMetaData();
                            umd.setObjectProperty(op);
                            umd.setUpload(upload);
                            if (uploadDto.getUploadMetadata().get(op.getProperty().getName().trim()) != null) {
                                umd.setValue(uploadDto.getUploadMetadata().get(op.getProperty().getName().trim()));
                            } else {
                                umd.setValue(" ");
                            }
                            umd.setStatus(1);
                            uploadMetaDataList.add(umd);
                        }
                        upload.setMetadataStatus(AidasConstants.AIDAS_UPLOAD_METADATA_REQUIRED);
                        upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
                        upload.setCurrentBatchNumber(0);
                        uploadRepository.save(upload);
                        uploadMetaDataRepository.saveAll(uploadMetaDataList);

                        uvmom.setTotalUploaded(uvmom.getTotalUploaded()+1);
                        uvmom.setTotalPending(uvmom.getTotalPending()+1);
                        object.setTotalRequired(object.getTotalRequired()-1);
                        object.setTotalPending(object.getTotalPending()+1);

                        uvmpm.setTotalUploaded(uvmom.getTotalUploaded()+1);
                        uvmpm.setTotalPending(uvmom.getTotalPending()+1);
                        project.setTotalRequired(project.getTotalRequired()-1);
                        project.setTotalPending(project.getTotalPending()+1);

                        if(project.getAutoCreateObjects()!=null && project.getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS)){
                            if(object.getNumberOfUploadsRequired().equals(object.getTotalUploaded())){
                                project.setNumberOfObjects(project.getNumberOfObjects()-1);
                            }
                        }
                        userVendorMappingObjectMappingRepository.save(uvmom);
                        userVendorMappingProjectMappingRepository.save(uvmpm);
                        objectRepository.save(object);
                        projectRepository.save(project);
                        return ResponseEntity
                            .created(new URI("/api/aidas-uploads/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new BadRequestAlertException("Internal error occured.  Contact administrator", ENTITY_NAME, "idexists");
                    }

                }else{

                    throw new BadRequestAlertException("Total uploads exceeded the limit.... ", ENTITY_NAME, "idexists");
                }
            }
            else{
                throw new BadRequestAlertException("Some issue with upload.... ", ENTITY_NAME, "idexists");
            }
        }
        throw new BadRequestAlertException("End of Upload...No way ", ENTITY_NAME, "idexists");
    }


    /**
     * {@code POST  /aidas-uploads/dtos} : Create a new upload.
     *
     * @param uploadDtos the upload to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new upload, or with status {@code 400 (Bad Request)} if the upload has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-uploads/dtos")
    public ResponseEntity<Upload> createAidasUploadFromDtos( @RequestBody List<UploadDTO> uploadDtos) throws URISyntaxException {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to save AidasUpload : {}", uploadDtos);
        Authority authority = user.getAuthority();
        if(authority.getName().equals(AidasConstants.VENDOR_USER)) {
            for(UploadDTO uploadDto: uploadDtos){

                if (uploadDto.getUserId() == null) {
                    throw new BadRequestAlertException("No User Id", ENTITY_NAME, "idexists");
                }
                if (uploadDto.getObjectId() == null) {
                    throw new BadRequestAlertException("No Object Id", ENTITY_NAME, "idexists");
                }
                if (uploadDto.getUploadUrl() == null) {
                    throw new BadRequestAlertException("No Upload URL Id", ENTITY_NAME, "idexists");
                }
                if (uploadDto.getEtag() == null) {
                    throw new BadRequestAlertException("No Etag Id", ENTITY_NAME, "idexists");
                }
                Upload upload = new Upload();
                UserVendorMappingObjectMapping uvmom = userVendorMappingObjectMappingRepository.findByUserObject(uploadDto.getUserId(), uploadDto.getObjectId());
                if(uvmom!=null && uvmom.getObject()!=null){
                    Object object = uvmom.getObject();
                    Project project = object.getProject();
                    Customer customer = project.getCustomer();
                    upload.setUserVendorMappingObjectMapping(uvmom);
                    upload.setDateUploaded(Instant.now());
                    upload.setName(uploadDto.getObjectKey());
                    upload.setUploadUrl(uploadDto.getUploadUrl());
                    upload.setUploadEtag(uploadDto.getEtag());
                    upload.setObjectKey(uploadDto.getObjectKey());
                    upload.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_PENDING);
                    upload.setCurrentQcLevel(1);
                    Long mandatoryProjectProperties = projectPropertyRepository.countProjectPropertyByProjectAndOptional(project.getId(), AidasConstants.AIDAS_PROPERTY_REQUIRED);
                    Long mandatoryObjectProperties = objectPropertyRepository.countObjectProperties(object.getId(), AidasConstants.AIDAS_PROPERTY_REQUIRED);
                    if (mandatoryObjectProperties == 0 && mandatoryProjectProperties == 0) {
                        upload.setMetadataStatus(AidasConstants.AIDAS_UPLOAD_METADATA_COMPLETED);
                    } else {
                        upload.setMetadataStatus(AidasConstants.AIDAS_UPLOAD_METADATA_REQUIRED);
                    }
                    upload.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_PENDING);
                    upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
                    try {
                        Upload result = uploadRepository.save(upload);
                        Object o = result.getUserVendorMappingObjectMapping().getObject();
                        Project p = o.getProject();
                        o.setTotalUploaded(o.getTotalUploaded() + 1);
                        o.setTotalPending(o.getTotalPending() + 1);
                        if (o.getTotalRequired() > 0) {
                            o.setTotalRequired(o.getTotalRequired() - 1);
                        }
                        p.setTotalUploaded(p.getTotalUploaded() + 1);
                        p.setTotalPending(p.getTotalPending() + 1);
                        if (p.getTotalRequired() > 0) {
                            p.setTotalRequired(p.getTotalRequired() - 1);
                        }
                        UserVendorMappingProjectMapping uvmpm = userVendorMappingProjectMappingRepository.findByUserVendorMappingIdProjectId(upload.getUserVendorMappingObjectMapping().getUserVendorMapping().getId(), p.getId());
                        uvmpm.setTotalUploaded(uvmpm.getTotalUploaded() + 1);
                        uvmpm.setTotalPending(uvmpm.getTotalPending() + 1);
                        objectRepository.save(o);
                        projectRepository.save(p);
                        uvmom.setTotalUploaded(uvmom.getTotalUploaded() + 1);
                        uvmom.setTotalPending(uvmom.getTotalPending() + 1);
                        userVendorMappingObjectMappingRepository.save(uvmom);
                        userVendorMappingProjectMappingRepository.save(uvmpm);
                        List<ProjectProperty> projectProperties = projectPropertyRepository.findAllProjectProperty(project.getId());
                        List<ObjectProperty> objectProperties = objectPropertyRepository.getAllObjectPropertyForObject(object.getId());
                        List<UploadMetaData> uploadMetaDataList = new ArrayList<>();
                        for (ProjectProperty pp : projectProperties) {
                            UploadMetaData umd = new UploadMetaData();
                            umd.setProjectProperty(pp);
                            umd.setUpload(upload);
                            umd.setValue(" ");
                            umd.setStatus(1);
                            uploadMetaDataList.add(umd);
                        }
                        for (ObjectProperty op : objectProperties) {
                            UploadMetaData umd = new UploadMetaData();
                            umd.setObjectProperty(op);
                            umd.setUpload(upload);
                            umd.setValue(" ");
                            umd.setStatus(1);
                            uploadMetaDataList.add(umd);
                        }
                        uploadMetaDataRepository.saveAll(uploadMetaDataList);
                        for (Map.Entry<String, String> entry : uploadDto.getUploadMetadata().entrySet()) {
                            Property property = propertyRepository.getByNameAndUserIdAndCategory(entry.getKey().trim(), customer.getId(), project.getCategory().getId());
                            UploadMetaData umdpp = uploadMetaDataRepository.getUploadMetaDataByProjectPropertyName(result.getId(), entry.getKey());
                            UploadMetaData umdop = uploadMetaDataRepository.getUploadMetaDataByObjectPropertyName(result.getId(), entry.getKey());
                            if (umdpp != null) {
                                umdpp.setValue(entry.getValue().toString());
                                uploadMetaDataRepository.save(umdpp);
                            }
                            if (umdop != null) {
                                umdop.setValue(entry.getValue().toString());
                                uploadMetaDataRepository.save(umdop);
                            }
                        }
                        Integer numberOfMandatoryProjectProperties = uploadMetaDataRepository.getUploadMetadataCountMandatoryProjectPropertyNotFilled(project.getId());
                        Integer numberOfMandatoryObjectProperties = uploadMetaDataRepository.getUploadMetadataCountMandatoryObjectPropertyNotFilled(object.getId());
                        if(numberOfMandatoryObjectProperties==0 && numberOfMandatoryProjectProperties==0){
                            upload.setMetadataStatus(AidasConstants.AIDAS_UPLOAD_METADATA_COMPLETED);
                            upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
                        }
                        upload.setCurrentBatchNumber(0);
                        uploadRepository.save(upload);
                        return ResponseEntity
                            .created(new URI("/api/aidas-uploads/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new BadRequestAlertException("Internal error occured.  Contact administrator", ENTITY_NAME, "idexists");
                    }
                }else{
                    throw new BadRequestAlertException("Some issue with upload.... ", ENTITY_NAME, "idexists");
                }

            }
        }
        throw new BadRequestAlertException("End of Upload...No way ", ENTITY_NAME, "idexists");
    }

    /**
     * {@code POST  /aidas-uploads/user-object-mapping-dto} : Create a new upload.
     *
     * @param uploadByUserObjectMappingDto the upload to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new upload, or with status {@code 400 (Bad Request)} if the upload has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-uploads/user-object-mapping-dto")
    public ResponseEntity<Upload> createAidasUploadFromUseObjectMappingDto(@Valid @RequestBody UploadByUserObjectMappingDto uploadByUserObjectMappingDto) throws URISyntaxException {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to save AidasUpload : {}", uploadByUserObjectMappingDto);
        Authority authority = user.getAuthority();
        if(authority.getName().equals(AidasConstants.ADMIN)){

        }
        if(authority.getName().equals(AidasConstants.ORG_ADMIN)){
            throw new BadRequestAlertException("You can not upload as ORG ADMIN", ENTITY_NAME, "idexists");
        }
        if(authority.getName().equals(AidasConstants.CUSTOMER_ADMIN)){
            throw new BadRequestAlertException("You can not upload as CUSTOMER ADMIN", ENTITY_NAME, "idexists");
        }
        if(authority.getName().equals(AidasConstants.VENDOR_ADMIN)){
            throw new BadRequestAlertException("You can not upload as VENDOR ADMIN", ENTITY_NAME, "idexists");
        }
        if(authority.getName().equals(AidasConstants.VENDOR_USER)){
            if (uploadByUserObjectMappingDto.getUserObjectMappingId() == null) {
                throw new BadRequestAlertException("No User Id", ENTITY_NAME, "idexists");
            }
            if (uploadByUserObjectMappingDto.getUploadUrl() == null) {
                throw new BadRequestAlertException("No Upload URL Id", ENTITY_NAME, "idexists");
            }
            if (uploadByUserObjectMappingDto.getEtag() == null) {
                throw new BadRequestAlertException("No Etag Id", ENTITY_NAME, "idexists");
            }

            Upload upload = new Upload();
            UserVendorMappingObjectMapping auaom = null;
            if(uploadByUserObjectMappingDto.getUserVendorMappingObjectMappingId()!=null){
                auaom =userVendorMappingObjectMappingRepository.getById(uploadByUserObjectMappingDto.getUserVendorMappingObjectMappingId());
            }else{
                auaom = userVendorMappingObjectMappingRepository.getById(uploadByUserObjectMappingDto.getUserObjectMappingId());
            }
            //UserVendorMappingObjectMapping auaom = userVendorMappingObjectMappingRepository.getById(uploadByUserObjectMappingDto.getUserObjectMappingId());
            upload.setUserVendorMappingObjectMapping(auaom);
            upload.setDateUploaded(Instant.now());
            upload.setName(uploadByUserObjectMappingDto.getUploadUrl());
            upload.setUploadUrl(uploadByUserObjectMappingDto.getUploadUrl());
            upload.setUploadEtag(uploadByUserObjectMappingDto.getEtag());
            upload.setObjectKey(uploadByUserObjectMappingDto.getObjectKey());

            Object object = auaom.getObject();
            Project project = object.getProject();
            Long mandatoryProjectProperties = projectPropertyRepository.countProjectPropertyByProjectAndOptional(project.getId(),AidasConstants.AIDAS_PROPERTY_REQUIRED);
            Long mandatoryObjectProperties = objectPropertyRepository.countObjectProperties(object.getId(),AidasConstants.AIDAS_PROPERTY_REQUIRED);

            if(mandatoryObjectProperties==0 && mandatoryProjectProperties==0){
                upload.setMetadataStatus(AidasConstants.AIDAS_UPLOAD_METADATA_COMPLETED);
            }else {
                upload.setMetadataStatus(AidasConstants.AIDAS_UPLOAD_METADATA_REQUIRED);
            }
            upload.setStatus(AidasConstants.AIDAS_UPLOAD_PENDING);
            upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);

            Upload result = uploadRepository.save(upload);
            //aidasUploadSearchRepository.save(result);
            return ResponseEntity
                .created(new URI("/api/aidas-uploads/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                .body(result);
        }
        throw new BadRequestAlertException("Internal error occured.  Contact administrator", ENTITY_NAME, "idexists");
    }


    /**
     * {@code PUT  /aidas-uploads/:id} : Updates an existing upload.
     *
     * @param id the id of the upload to save.
     * @param upload the upload to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated upload,
     * or with status {@code 400 (Bad Request)} if the upload is not valid,
     * or with status {@code 500 (Internal Server Error)} if the upload couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-uploads/{id}")
    public ResponseEntity<Upload> updateAidasUpload(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Upload upload
    ) throws URISyntaxException {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to update AidasUpload : {}, {}", id, upload);
        Authority authority = user.getAuthority();
        if(authority.getName().equals(AidasConstants.ADMIN)){

        }
        if(authority.getName().equals(AidasConstants.ORG_ADMIN)){

        }
        if(authority.getName().equals(AidasConstants.CUSTOMER_ADMIN)){

        }
        if(authority.getName().equals(AidasConstants.VENDOR_ADMIN)){

        }
        if(authority.getName().equals(AidasConstants.VENDOR_USER)){

        }
        if (upload.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, upload.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!uploadRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Upload result = uploadRepository.save(upload);
        aidasUploadSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, upload.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /aidas-uploads/:id} : Approve an existing upload.
     *
     * @param id the id of the upload to save.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated upload,
     * or with status {@code 400 (Bad Request)} if the upload is not valid,
     * or with status {@code 500 (Internal Server Error)} if the upload couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-uploads/approve/{id}/{customerQcProjectMappingId}")
    public ResponseEntity<Upload> approveAidasUpload(
        @PathVariable(value = "id", required = false) final Long id,@PathVariable(value = "customerQcProjectMappingId", required = false) final Long customerQcProjectMappingId
    ) throws URISyntaxException {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to approve AidasUpload : {}, {}", id);
        Authority authority = user.getAuthority();
        if (!uploadRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        Upload upload = uploadRepository.getById(id);
        UserVendorMappingObjectMapping uvmom = upload.getUserVendorMappingObjectMapping();
        Object object = uvmom.getObject();
        Project project = uvmom.getObject().getProject();
        UserVendorMappingProjectMapping uvmpm = userVendorMappingProjectMappingRepository.findByUserVendorMappingIdProjectId(uvmom.getUserVendorMapping().getId(),project.getId());
        CustomerQcProjectMapping cqpm = customerQcProjectMappingRepository.getById(customerQcProjectMappingId);
        ProjectQcLevelConfigurations pqlc = projectQcLevelConfigurationsRepository.findByProejctIdAndQcLevel(project.getId(),cqpm.getQcLevel());
        List<Long> allApprovedUploads = uploadCustomerQcProjectMappingBatchInfoRepository.getAllApprovedInBatch(customerQcProjectMappingId,cqpm.getCurrentQcBatchNo());
        List<Upload> remainingUploads =new ArrayList<>();
        Integer totalAvailableInBatch = uploadCustomerQcProjectMappingBatchInfoRepository.countUploadsByCustomerQcProjectMappingAndBatchNumber(customerQcProjectMappingId,cqpm.getCurrentQcBatchNo());
        Float countReqdBasedOnAcceptancePercent = (pqlc.getQcLevelAcceptancePercentage().floatValue() / 100f) * totalAvailableInBatch;
        if(allApprovedUploads.size()+1>=countReqdBasedOnAcceptancePercent.intValue()){
            List<Long> remainingUploadIds = uploadCustomerQcProjectMappingBatchInfoRepository.getRemainingUploadsInBatchIncludingCurrentUpload(customerQcProjectMappingId,cqpm.getCurrentQcBatchNo());
            remainingUploads = uploadRepository.getUploadsByIds(remainingUploadIds);
        }
        if(remainingUploads!=null){
            remainingUploads.add(upload);
        }
        boolean lastLevel = false;
        boolean finalApproval=false;
        if(cqpm!=null && project.getQcLevels().equals(cqpm.getQcLevel())) {
            lastLevel = true;
        }
        if((project.getQcLevels().equals(1) && cqpm.getQcLevel().equals(1)) || (lastLevel && (allApprovedUploads.size()+1)==countReqdBasedOnAcceptancePercent.intValue())){
                finalApproval=true;
        }
        for(Upload upload1:remainingUploads){
            UploadCustomerQcProjectMappingBatchInfo ucqpmbi =  uploadCustomerQcProjectMappingBatchInfoRepository.getUploadIdByCustomerQcProjectMappingAndBatchNumber(customerQcProjectMappingId,cqpm.getCurrentQcBatchNo(),upload1.getId());
            if(finalApproval){
                upload1.setStatus(AidasConstants.AIDAS_UPLOAD_APPROVED);
                upload1.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_APPROVED);

                uvmom.setTotalApproved(uvmom.getTotalApproved()+1);
                uvmom.setTotalPending(uvmom.getTotalPending()-1);

                uvmpm.setTotalApproved(uvmom.getTotalApproved()+1);
                uvmpm.setTotalPending(uvmom.getTotalPending()-1);

                object.setTotalApproved(object.getTotalApproved()+1);
                object.setTotalPending(object.getTotalPending()-1);
                if (object.getTotalRequired() > 0) {
                    object.setTotalRequired(object.getTotalRequired()-1);
                }
                project.setTotalApproved(project.getTotalApproved()+1);
                project.setTotalPending(project.getTotalPending()-1);
                if (project.getTotalRequired() > 0) {
                    project.setTotalRequired(project.getTotalRequired()-1);
                }
                userVendorMappingObjectMappingRepository.save(uvmom);
                userVendorMappingProjectMappingRepository.save(uvmpm);
                objectRepository.save(object);
                projectRepository.save(project);
                upload1.setCurrentBatchNumber(null);
            }
            upload1.setCurrentQcLevel(cqpm.getQcLevel()+1);
            upload1.setQcDoneBy(cqpm);
            upload1.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_APPROVED);
            upload1.setQcEndDate(Instant.now());
            ucqpmbi.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_APPROVED);
            uploadRepository.save(upload1);
            uploadCustomerQcProjectMappingBatchInfoRepository.save(ucqpmbi);
        }
        List<Long> remainingUploadIds = uploadCustomerQcProjectMappingBatchInfoRepository.getRemainingUploadsInBatchIncludingCurrentUpload(customerQcProjectMappingId,cqpm.getCurrentQcBatchNo());
        if(remainingUploadIds.size()==0){
            CustomerQcProjectMappingBatchMapping cqpmbm = customerQcProjectMappingBatchMappingRepository.findByCustomerQcProjectMappingIdAndBatchNumber(customerQcProjectMappingId,cqpm.getCurrentQcBatchNo());
            cqpmbm.setBatchCompletionStatus(AidasConstants.AIDAS_UPLOAD_QC_BATCH_APPROVED);
            customerQcProjectMappingBatchMappingRepository.save(cqpmbm);
        }
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, upload.getId().toString()))
            .body(upload);
    }

    /**
     * {@code PUT  /aidas-uploads/:id} : Reject an existing upload.
     *
     * @param id the id of the upload to save.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated upload,
     * or with status {@code 400 (Bad Request)} if the upload is not valid,
     * or with status {@code 500 (Internal Server Error)} if the upload couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-uploads/reject/{id}/{customerQcProjectMappingId}")
    public ResponseEntity<Upload> rejectAidasUpload(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody List<UploadRejectReason> uploadRejectReasons,@PathVariable(value = "customerQcProjectMappingId", required = false) final Long customerQcProjectMappingId
    ) throws URISyntaxException {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to reject AidasUpload : {}, {}", id);
        Authority authority = user.getAuthority();
        if (!uploadRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        Upload upload = uploadRepository.getById(id);
        HashMap<Long, Integer> oldQcStatus=new HashMap<>();
        oldQcStatus.put(upload.getId(),upload.getQcStatus());
        UserVendorMappingObjectMapping uvmom = upload.getUserVendorMappingObjectMapping();
        Object object = uvmom.getObject();
        Project project = uvmom.getObject().getProject();
        UserVendorMappingProjectMapping uvmpm = userVendorMappingProjectMappingRepository.findByUserVendorMappingIdProjectId(uvmom.getUserVendorMapping().getId(),project.getId());
        CustomerQcProjectMapping cqpm = customerQcProjectMappingRepository.getById(customerQcProjectMappingId);
        UploadCustomerQcProjectMappingBatchInfo ucqpmbi =  uploadCustomerQcProjectMappingBatchInfoRepository.getUploadIdByCustomerQcProjectMappingAndBatchNumber(customerQcProjectMappingId,cqpm.getCurrentQcBatchNo(),upload.getId());
        ucqpmbi.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_REJECTED);
        upload.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_REJECTED);
        upload.setQcEndDate(Instant.now());
        upload.setCurrentQcLevel(upload.getCurrentQcLevel()+1);
        upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_REJECTED);
        for(UploadRejectReason uploadRejectReason : uploadRejectReasons){
            UploadRejectReasonMapping uploadRejectReasonMapping = new UploadRejectReasonMapping();
            if(uploadRejectReason.getId()!=null){
                uploadRejectReasonMapping = uploadRejectReasonMappingRepository.getUploadRejectReasonMapping(upload.getId(),uploadRejectReason.getId());
                if(uploadRejectReasonMapping==null) {
                    uploadRejectReasonMapping = new UploadRejectReasonMapping();
                    uploadRejectReason = uploadRejectReasonRepository.getById(uploadRejectReason.getId());
                    uploadRejectReasonMapping.setUpload(upload);
                    uploadRejectReasonMapping.setUploadRejectReason(uploadRejectReason);
                    upload.getUploadRejectMappings().add(uploadRejectReasonMapping);
                }
            }else{
                uploadRejectReason = uploadRejectReasonRepository.save(uploadRejectReason);
                uploadRejectReasonMapping.setUpload(upload);
                uploadRejectReasonMapping.setUploadRejectReason(uploadRejectReason);
                upload.getUploadRejectMappings().add(uploadRejectReasonMapping);
            }
        }
        if(cqpm!=null) {
            upload.setQcDoneBy(cqpm);
            upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_REJECTED);
            upload.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_REJECTED);
        }
        if(ucqpmbi!=null) {
            ucqpmbi.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_REJECTED);
        }
        Upload result = uploadRepository.save(upload);
        List<Long> allInBatch = uploadCustomerQcProjectMappingBatchInfoRepository.getAllInBatchGrouped(customerQcProjectMappingId,result.getUserVendorMappingObjectMapping().getObject().getId(),cqpm.getCurrentQcBatchNo());
        uploadCustomerQcProjectMappingBatchInfoRepository.save(ucqpmbi);
        List<Upload> uploadsInBatch = uploadRepository.getUploadsByIds(allInBatch);
        if(project.getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS)){
            for(Upload upload1:uploadsInBatch){
                if(oldQcStatus.get(upload1.getId())!=null) {
                    oldQcStatus.put(upload1.getId(), upload1.getQcStatus());
                }
                if (oldQcStatus.get(upload1.getId())!=null && oldQcStatus.get(upload1.getId()).equals(AidasConstants.AIDAS_UPLOAD_QC_PENDING)) {
                    upload1.setPreviouQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
                    upload1.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_REJECTED);
                    upload1.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_QC_REJECTED);
                } else if (oldQcStatus.get(upload1.getId())!=null && oldQcStatus.get(upload1.getId()).equals(AidasConstants.AIDAS_UPLOAD_QC_APPROVED)) {
                    upload1.setPreviouQcStatus(AidasConstants.AIDAS_UPLOAD_QC_APPROVED);
                    upload1.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_REJECTED);
                    upload1.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_QC_REJECTED);
                }else{
                    upload1.setPreviouQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
                    upload1.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_REJECTED);
                    upload1.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_QC_REJECTED);
                }
                uvmom.setTotalApproved(uvmom.getTotalApproved()+1);
                uvmom.setTotalPending(uvmom.getTotalPending()-1);

                uvmpm.setTotalApproved(uvmom.getTotalApproved()+1);
                uvmpm.setTotalPending(uvmom.getTotalPending()-1);

                object.setTotalApproved(object.getTotalApproved()+1);
                object.setTotalPending(object.getTotalPending()-1);
                if (object.getTotalRequired() > 0) {
                    object.setTotalRequired(object.getTotalRequired()-1);
                }
                project.setTotalApproved(project.getTotalApproved()+1);
                project.setTotalPending(project.getTotalPending()-1);
                if (project.getTotalRequired() > 0) {
                    project.setTotalRequired(project.getTotalRequired()-1);
                }
                uploadRepository.save(upload1);
            }
            userVendorMappingObjectMappingRepository.save(uvmom);
            userVendorMappingProjectMappingRepository.save(uvmpm);
            objectRepository.save(object);
            projectRepository.save(project);
            List<Long> remainingUploadIds = uploadCustomerQcProjectMappingBatchInfoRepository.getRemainingUploadsInBatchIncludingCurrentUploadNew(customerQcProjectMappingId,cqpm.getCurrentQcBatchNo(),upload.getId());
            if(remainingUploadIds.size()==0){
                CustomerQcProjectMappingBatchMapping cqpmbm = customerQcProjectMappingBatchMappingRepository.findByCustomerQcProjectMappingIdAndBatchNumber(customerQcProjectMappingId,cqpm.getCurrentQcBatchNo());
                cqpmbm.setBatchCompletionStatus(AidasConstants.AIDAS_UPLOAD_QC_BATCH_REJECTED);
                customerQcProjectMappingBatchMappingRepository.save(cqpmbm);
            }
        }else{
            if (oldQcStatus.get(upload.getId()).equals(AidasConstants.AIDAS_UPLOAD_QC_PENDING)) {
                upload.setShowToQc(AidasConstants.AIDAS_UPLOAD_NO_SHOW_TO_QC);
                upload.setPreviouQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
                upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_REJECTED);
                upload.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_QC_REJECTED);
            } else if (oldQcStatus.get(upload.getId()).equals(AidasConstants.AIDAS_UPLOAD_QC_APPROVED)) {
                upload.setShowToQc(AidasConstants.AIDAS_UPLOAD_NO_SHOW_TO_QC);
                upload.setPreviouQcStatus(AidasConstants.AIDAS_UPLOAD_QC_APPROVED);
                upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_REJECTED);
                upload.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_QC_REJECTED);
            }
            uvmom.setTotalApproved(uvmom.getTotalApproved()+1);
            uvmom.setTotalPending(uvmom.getTotalPending()-1);

            uvmpm.setTotalApproved(uvmom.getTotalApproved()+1);
            uvmpm.setTotalPending(uvmom.getTotalPending()-1);

            object.setTotalApproved(object.getTotalApproved()+1);
            object.setTotalPending(object.getTotalPending()-1);
            if (object.getTotalRequired() > 0) {
                object.setTotalRequired(object.getTotalRequired()-1);
            }
            project.setTotalApproved(project.getTotalApproved()+1);
            project.setTotalPending(project.getTotalPending()-1);
            if (project.getTotalRequired() > 0) {
                project.setTotalRequired(project.getTotalRequired()-1);
            }
            uploadRepository.save(upload);
            userVendorMappingObjectMappingRepository.save(uvmom);
            userVendorMappingProjectMappingRepository.save(uvmpm);
            objectRepository.save(object);
            projectRepository.save(project);
            List<Long> remainingUploadIds = uploadCustomerQcProjectMappingBatchInfoRepository.getRemainingUploadsInBatchIncludingCurrentUpload(customerQcProjectMappingId,cqpm.getCurrentQcBatchNo());
            if(remainingUploadIds.size()==0){
                CustomerQcProjectMappingBatchMapping cqpmbm = customerQcProjectMappingBatchMappingRepository.findByCustomerQcProjectMappingIdAndBatchNumber(customerQcProjectMappingId,cqpm.getCurrentQcBatchNo());
                cqpmbm.setBatchCompletionStatus(AidasConstants.AIDAS_UPLOAD_QC_BATCH_COMPLETED);
                customerQcProjectMappingBatchMappingRepository.save(cqpmbm);
            }
        }
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, upload.getId().toString()))
            .body(result);
    }
    /**
     * {@code PATCH  /aidas-uploads/:id} : Partial updates given fields of an existing upload, field will ignore if it is null
     *
     * @param id the id of the upload to save.
     * @param upload the upload to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated upload,
     * or with status {@code 400 (Bad Request)} if the upload is not valid,
     * or with status {@code 404 (Not Found)} if the upload is not found,
     * or with status {@code 500 (Internal Server Error)} if the upload couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-uploads/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Upload> partialUpdateAidasUpload(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Upload upload
    ) throws URISyntaxException {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to partial update AidasUpload partially : {}, {}", id, upload);
        Authority authority = user.getAuthority();
        if(authority.getName().equals(AidasConstants.ADMIN)){

        }
        if(authority.getName().equals(AidasConstants.ORG_ADMIN)){

        }
        if(authority.getName().equals(AidasConstants.CUSTOMER_ADMIN)){

        }
        if(authority.getName().equals(AidasConstants.VENDOR_ADMIN)){

        }
        if(authority.getName().equals(AidasConstants.VENDOR_USER)){

        }
        if (upload.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, upload.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!uploadRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Upload> result = uploadRepository
            .findById(upload.getId())
            .map(existingAidasUpload -> {
                if (upload.getName() != null) {
                    existingAidasUpload.setName(upload.getName());
                }
                if (upload.getDateUploaded() != null) {
                    existingAidasUpload.setDateUploaded(upload.getDateUploaded());
                }
                if (upload.getStatus() != null) {
                    existingAidasUpload.setStatus(upload.getStatus());
                }
                if (upload.getStatusModifiedDate() != null) {
                    existingAidasUpload.setStatusModifiedDate(upload.getStatusModifiedDate());
                }


                return existingAidasUpload;
            })
            .map(uploadRepository::save)
            .map(savedAidasUpload -> {
                aidasUploadSearchRepository.save(savedAidasUpload);

                return savedAidasUpload;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, upload.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-uploads} : get all the aidasUploads.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasUploads in body.
     */
    @GetMapping("/aidas-uploads")
    public ResponseEntity<List<Upload>> getAllAidasUploads(Pageable pageable) {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to get a page of AidasUploads");
        Page<Upload> page = uploadRepository.findAll(pageable);
        Authority authority = user.getAuthority();
        if(authority.getName().equals(AidasConstants.ADMIN)){
            page = uploadRepository.findAll(pageable);
        }
        if(authority.getName().equals(AidasConstants.ORG_ADMIN)){
            page = uploadRepository.findAidasUploadByAidasOrganisation(user.getOrganisation().getId(),pageable);
        }
        if(authority.getName().equals(AidasConstants.CUSTOMER_ADMIN)){
            page = uploadRepository.findAidasUploadByAidasCustomer(user.getCustomer().getId(),pageable);
        }
        if(authority.getName().equals(AidasConstants.VENDOR_ADMIN)){
            page = uploadRepository.findAidasUploadByAidasVendor(user.getVendor().getId(),pageable);
        }
        if(authority.getName().equals(AidasConstants.VENDOR_USER)){
            page = uploadRepository.findAllByUser(user.getId(),pageable);
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-uploads/{id}/{type}/{status}} : get all the aidasUploads.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasUploads in body.
     */
    @GetMapping("/aidas-uploads/{id}/{type}/{status}")
    public ResponseEntity<List<Upload>> getAllAidasUploads(Pageable pageable, @PathVariable Long id, @PathVariable String type, @PathVariable String status) {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to get a page of AidasUploads");
        try {
            Page<Upload> page = null;//uploadRepository.findAll(pageable);
            Authority authority = user.getAuthority();
            if (authority.getName().equals(AidasConstants.ADMIN)) {
                page = uploadRepository.findAll(pageable);
            }
            if (authority.getName().equals(AidasConstants.ORG_ADMIN)) {
                page = uploadRepository.findAidasUploadByAidasOrganisation(user.getOrganisation().getId(), pageable);
            }
            if (authority.getName().equals(AidasConstants.CUSTOMER_ADMIN)) {
                page = uploadRepository.findAidasUploadByAidasCustomer(user.getCustomer().getId(), pageable);
            }
            if (authority.getName().equals(AidasConstants.VENDOR_ADMIN)) {
                page = uploadRepository.findAidasUploadByAidasVendor(user.getVendor().getId(), pageable);
            }
            if (authority.getName().equals(AidasConstants.VENDOR_USER)) {
                if (id != null && type != null && status != null) {
                    if (id != null && type.equalsIgnoreCase("o") && status.equalsIgnoreCase("a")) {
                        page = uploadRepository.findAllByUserAndObject(user.getId(), id, AidasConstants.AIDAS_UPLOAD_APPROVED, pageable);
                    }
                    if (id != null && type.equalsIgnoreCase("o") && status.equalsIgnoreCase("r")) {
                        page = uploadRepository.findAllByUserAndObject(user.getId(), id, AidasConstants.AIDAS_UPLOAD_REJECTED, pageable);
                    }
                    if (id != null && type.equalsIgnoreCase("o") && status.equalsIgnoreCase("p")) {
                        page = uploadRepository.findAllByUserAndObject(user.getId(), id, AidasConstants.AIDAS_UPLOAD_PENDING, pageable);
                    }
                    if (id != null && type.equalsIgnoreCase("o") && status.equalsIgnoreCase("all")) {
                        page = uploadRepository.findAllByUserAndObject(user.getId(), id, pageable);
                    }
                    if (id != null && type.equalsIgnoreCase("p") && status.equalsIgnoreCase("a")) {
                        page = uploadRepository.findAllByUserAndProject(user.getId(), id, AidasConstants.AIDAS_UPLOAD_APPROVED, pageable);
                    }
                    if (id != null && type.equalsIgnoreCase("p") && status.equalsIgnoreCase("r")) {
                        page = uploadRepository.findAllByUserAndProject(user.getId(), id, AidasConstants.AIDAS_UPLOAD_REJECTED, pageable);
                    }
                    if (id != null && type.equalsIgnoreCase("p") && status.equalsIgnoreCase("p")) {
                        page = uploadRepository.findAllByUserAndProject(user.getId(), id, AidasConstants.AIDAS_UPLOAD_PENDING, pageable);
                    }
                    if (id != null && type.equalsIgnoreCase("p") && status.equalsIgnoreCase("all")) {
                        page = uploadRepository.findAllByUserAndProject(user.getId(), id, pageable);
                    }
                }
                if (id == null && type == null && status != null) {
                    if (status.equalsIgnoreCase("a")) {
                        page = uploadRepository.findAllByUser(user.getId(), AidasConstants.AIDAS_UPLOAD_APPROVED, pageable);
                    }
                    if (status.equalsIgnoreCase("r")) {
                        page = uploadRepository.findAllByUser(user.getId(), AidasConstants.AIDAS_UPLOAD_REJECTED, pageable);
                    }
                    if (status.equalsIgnoreCase("p")) {
                        page = uploadRepository.findAllByUser(user.getId(), AidasConstants.AIDAS_UPLOAD_PENDING, pageable);
                    }
                    if (status.equalsIgnoreCase("all")) {
                        page = uploadRepository.findAllByUser(user.getId(), pageable);
                    }
                }
            }
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        }catch(Exception e){
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
    }




    /**
     * {@code GET  /aidas-uploads/{id}/{type}/{status}} : get all the aidasUploads.
     *
     * @param projectId the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasUploads in body.
     */
    @GetMapping("/aidas-uploads/metadata/{projectId}/{objectId}/{uploadId}")
    public ResponseEntity<List<UploadsMetadataDTO>> getAllAidasUploadsForMetadata(@PathVariable(required = true,name = "projectId") Long projectId,@PathVariable(name="objectId" ,required = false) Long objectId,@PathVariable(name="uploadId" ,required = false) Long uploadId) {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        List<UploadsMetadataDTO> uploadsMetadataDTOList = new ArrayList<>();
            List<Upload> uploads = null;
            if(uploadId!=null)
                uploads = uploadRepository.findAllByUserAndProjectAllForMetadataUploadWise(user.getId(),projectId);
            else if(objectId!=null)
                uploads = uploadRepository.findAllByUserAndProjectAllForMetadataObjectWise(user.getId(),projectId);
            else if(projectId!=null)
                uploads = uploadRepository.findAllByUserAndProjectAllForMetadataProjectWise(user.getId(),projectId);

            UploadsMetadataDTO uploadsMetadataDTO=new UploadsMetadataDTO();
            for(Upload au : uploads) {
                List<UploadMetadataDTO> uploadMetaDataList = uploadMetaDataRepository.getAllUploadMetaDataForUpload(au.getId());
                List<ProjectPropertyDTO> projectPropertyDTOS = new ArrayList<>();
                List<ObjectPropertyDTO> objectPropertyDTOS = new ArrayList<>();
                UploadDTO uploadDTO = new UploadDTO();
                uploadDTO.setUploadId(au.getId());
                uploadDTO.setName(au.getUserVendorMappingObjectMapping().getObject().getName());
                uploadDTO.setObjectKey(au.getUploadUrl());
                uploadsMetadataDTO.setUploadDTO(uploadDTO);
                for(UploadMetadataDTO umdt: uploadMetaDataList){
                    if(umdt.getProjectPropertyId()!=null){
                        ProjectPropertyDTO pp = new ProjectPropertyDTO();
                        ProjectProperty p = projectPropertyRepository.getById(umdt.getProjectPropertyId());
                        pp.setProjectPropertyId(p.getId());
                        pp.setName(p.getProperty().getName());
                        pp.setOptional(p.getOptional());
                        pp.setValue(umdt.getValue());
                        projectPropertyDTOS.add(pp);
                    }
                    uploadsMetadataDTO.setProjectProperties(projectPropertyDTOS);
                    if(umdt.getObjectPropertyId()!=null){
                        ObjectPropertyDTO oo = new ObjectPropertyDTO();
                        ObjectProperty o = objectPropertyRepository.getById(umdt.getObjectPropertyId());
                        oo.setObjectPropertyId(o.getId());
                        oo.setName(o.getProperty().getName());
                        oo.setOptional(o.getOptional());
                        oo.setValue(umdt.getValue());
                        objectPropertyDTOS.add(oo);
                    }
                    uploadsMetadataDTO.setObjectProperties(objectPropertyDTOS);
                }
                uploadsMetadataDTOList.add(uploadsMetadataDTO);
            }
            return ResponseEntity.ok().body(uploadsMetadataDTOList);
    }



    /**
     * {@code GET  /aidas-uploads/{id}/{type}/{status}} : get all the aidasUploads.
     *
     * @param projectId the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasUploads in body.
     */
    @GetMapping("/aidas-uploads/metadata/{projectId}")
    public ResponseEntity<List<UploadsMetadataDTO>> getAllAidasUploadsForMetadata(@PathVariable(required = true,name = "projectId") Long projectId) {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        List<UploadsMetadataDTO> uploadsMetadataDTOList = new ArrayList<>();
        List<Upload> uploads = null;
        if(projectId!=null) {
            uploads = uploadRepository.findAllByUserAndProjectAllForMetadataProjectWise(user.getId(), projectId);
        }
        UploadsMetadataDTO uploadsMetadataDTO=null;
        for(Upload au : uploads) {
            uploadsMetadataDTO =  new UploadsMetadataDTO();
            List<UploadMetadataDTO> uploadMetaDataList = uploadMetaDataRepository.getAllUploadMetaDataForUpload(au.getId());
            List<ProjectPropertyDTO> projectPropertyDTOS = new ArrayList<>();
            List<ObjectPropertyDTO> objectPropertyDTOS = new ArrayList<>();
            UploadDTO uploadDTO = new UploadDTO();
            uploadDTO.setUploadId(au.getId());
            uploadDTO.setName(au.getUserVendorMappingObjectMapping().getObject().getName());
            uploadDTO.setObjectKey(au.getUploadUrl());
            uploadsMetadataDTO.setUploadDTO(uploadDTO);
            for(UploadMetadataDTO umdt: uploadMetaDataList){
                if(umdt.getProjectPropertyId()!=null){
                    ProjectPropertyDTO pp = new ProjectPropertyDTO();
                    ProjectProperty p = projectPropertyRepository.getById(umdt.getProjectPropertyId());
                    pp.setProjectPropertyId(p.getId());
                    pp.setName(p.getProperty().getName());
                    pp.setOptional(p.getOptional());
                    pp.setValue(umdt.getValue());
                    projectPropertyDTOS.add(pp);
                }
                if(umdt.getObjectPropertyId()!=null){
                    ObjectPropertyDTO oo = new ObjectPropertyDTO();
                    ObjectProperty o = objectPropertyRepository.getById(umdt.getObjectPropertyId());
                    oo.setObjectPropertyId(o.getId());
                    oo.setName(o.getProperty().getName());
                    oo.setOptional(o.getOptional());
                    oo.setValue(umdt.getValue());
                    objectPropertyDTOS.add(oo);
                }
            }
            uploadsMetadataDTO.setProjectProperties(projectPropertyDTOS);
            uploadsMetadataDTO.setObjectProperties(objectPropertyDTOS);
            uploadsMetadataDTOList.add(uploadsMetadataDTO);
        }
        return ResponseEntity.ok().body(uploadsMetadataDTOList);
    }

    /**
     * {@code GET  /aidas-uploads/{id}/{type}/{status}} : get all the aidasUploads.
     *
     * @param projectId the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasUploads in body.
     */
    @GetMapping("/aidas-uploads/metadata/{projectId}/{objectId}")
    public ResponseEntity<List<UploadsMetadataDTO>> getAllAidasUploadsForMetadata(@PathVariable(required = true,name = "projectId") Long projectId,@PathVariable(name="objectId" ,required = false) Long objectId) {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        List<UploadsMetadataDTO> uploadsMetadataDTOList = new ArrayList<>();
        List<Upload> uploads = uploadRepository.findAllByUserAndProjectAllForMetadataObjectWise(user.getId(),objectId);
        UploadsMetadataDTO uploadsMetadataDTO=new UploadsMetadataDTO();
        for(Upload au : uploads) {
            uploadsMetadataDTO=new UploadsMetadataDTO();
            List<UploadMetadataDTO> uploadMetaDataList = uploadMetaDataRepository.getAllUploadMetaDataForUpload(au.getId());
            List<ProjectPropertyDTO> projectPropertyDTOS = new ArrayList<>();
            List<ObjectPropertyDTO> objectPropertyDTOS = new ArrayList<>();
            UploadDTO uploadDTO = new UploadDTO();
            uploadDTO.setUploadId(au.getId());
            uploadDTO.setName(au.getUserVendorMappingObjectMapping().getObject().getName());
            uploadDTO.setObjectKey(au.getUploadUrl());
            uploadsMetadataDTO.setUploadDTO(uploadDTO);
            for(UploadMetadataDTO umdt: uploadMetaDataList){
                if(umdt.getProjectPropertyId()!=null){
                    ProjectPropertyDTO pp = new ProjectPropertyDTO();
                    ProjectProperty p = projectPropertyRepository.getById(umdt.getProjectPropertyId());
                    pp.setProjectPropertyId(p.getId());
                    pp.setName(p.getProperty().getName());
                    pp.setOptional(p.getOptional());
                    pp.setValue(umdt.getValue());
                    projectPropertyDTOS.add(pp);
                }

                if(umdt.getObjectPropertyId()!=null){
                    ObjectPropertyDTO oo = new ObjectPropertyDTO();
                    ObjectProperty o = objectPropertyRepository.getById(umdt.getObjectPropertyId());
                    oo.setObjectPropertyId(o.getId());
                    oo.setName(o.getProperty().getName());
                    oo.setOptional(o.getOptional());
                    oo.setValue(umdt.getValue());
                    objectPropertyDTOS.add(oo);
                }
            }
            uploadsMetadataDTO.setProjectProperties(projectPropertyDTOS);
            uploadsMetadataDTO.setObjectProperties(objectPropertyDTOS);
            uploadsMetadataDTOList.add(uploadsMetadataDTO);
        }
        return ResponseEntity.ok().body(uploadsMetadataDTOList);
    }
    /**
     * {@code GET  /aidas-uploads/:id} : get the "id" upload.
     *
     * @param id the id of the upload to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the upload, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-uploads/{id}")
    public ResponseEntity<Upload> getUpload(@PathVariable Long id) {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to get AidasUpload : {}", id);
        Authority authority = user.getAuthority();
        if(authority.getName().equals(AidasConstants.ADMIN)){

        }
        if(authority.getName().equals(AidasConstants.ORG_ADMIN)){

        }
        if(authority.getName().equals(AidasConstants.CUSTOMER_ADMIN)){

        }
        if(authority.getName().equals(AidasConstants.VENDOR_ADMIN)){

        }
        if(authority.getName().equals(AidasConstants.VENDOR_USER)){

        }
        Optional<Upload> upload = uploadRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(upload);
    }

    /**
     * {@code GET  /aidas-uploads/next} : get the "id" upload.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the upload, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-uploads/next/{projectId}")
    public ResponseEntity<List<CustomerQcProjectMapping>> getNextAidasUpload(@PathVariable Long projectId) {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to get next AidasUpload : {}");
        Project project  = projectRepository.getById(projectId);
        List<CustomerQcProjectMapping> cqpms = customerQcProjectMappingRepository.getQcProjectMappingByProjectAndCustomerAndUser(projectId,project.getCustomer().getId(),user.getId());
        return ResponseEntity.ok().body(cqpms);
    }


    /**
     * {@code GET  /aidas-uploads/next} : get the "id" upload.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the upload, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-uploads/next-batch/{projectId}/{customerQcProjectMappingId}/{qcLevel}")
    public ResponseEntity<Map<String,List<Upload>> > getNextAidasUploadBatch(@PathVariable(name = "projectId",required = true) Long projectId,@PathVariable(value = "customerQcProjectMappingId",required = true) Long customerQcProjectMappingId,@PathVariable(value = "qcLevel",required = true) Integer qcLevel) {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to get next AidasUpload : {}");
        Map<String,List<Upload>>  uploads=new HashMap<>();
        Authority authority = user.getAuthority();
        Project project = projectRepository.getById(projectId);
        uploads = getUploadsOfProjectForQc(projectId, customerQcProjectMappingId, qcLevel);
        Map<String,List<Upload>>  responseUploads = new HashMap<>();

        for(Map.Entry<String,List<Upload>> entry : uploads.entrySet()){
            responseUploads.put(entry.getKey(),new ArrayList<>());
            List<Upload> uploadList = entry.getValue();
            List<Upload> responseUploadList = new ArrayList<>();
            for(Upload u : uploadList) {
                UploadMetadataDTO ud1 = new UploadMetadataDTO();
                if (u.getUserVendorMappingObjectMapping() != null) {
                    if (u.getUserVendorMappingObjectMapping().getObject() != null) {
                        ud1.setValue(u.getUserVendorMappingObjectMapping().getObject().getName());
                        ud1.setName("Object Name");
                        u.getUploadMetaDatas().add(ud1);
                    }
                }
                List<UploadMetadataDTO> uds = new LinkedList<>();
                List<UploadMetaData> umds = uploadMetaDataRepository.getAllUploadMetaDataForUploadForQc(u.getId());
                for (UploadMetaData umd : umds) {
                    if (umd.getProjectProperty() != null && umd.getProjectProperty().getProperty() != null && umd.getProjectProperty().getProperty().getName() != null && umd.getProjectProperty().getAddToMetadata() != null && umd.getProjectProperty().getAddToMetadata().equals(1)) {
                        UploadMetadataDTO ud = new UploadMetadataDTO();
                        ud.setName(umd.getProjectProperty().getProperty().getName());
                        ud.setPropertyType(umd.getProjectProperty().getProperty().getPropertyType());
                        ud.setProjectPropertyId(umd.getProjectProperty().getId());
                        if (umd.getValue() != null) {
                            ud.setValue(umd.getValue());
                        }
                        u.getUploadMetaDatas().add(ud);
                    } else if (umd.getObjectProperty() != null && umd.getObjectProperty().getProperty() != null && umd.getObjectProperty().getProperty().getName() != null && umd.getValue() != null && umd.getObjectProperty().getAddToMetadata() != null && umd.getObjectProperty().getAddToMetadata().equals(1)) {
                        UploadMetadataDTO ud = new UploadMetadataDTO();
                        ud.setName(umd.getObjectProperty().getProperty().getName());
                        ud.setObjectPropertyId(umd.getObjectProperty().getId());
                        ud.setPropertyType(umd.getObjectProperty().getProperty().getPropertyType());
                        if (umd.getValue() != null) {
                            ud.setValue(umd.getValue());
                        }
                        u.getUploadMetaDatas().add(ud);
                    }
                }
                HashMap<String, UploadMetadataDTO> singleMap = new HashMap<>();
                for (UploadMetadataDTO umdd : u.getUploadMetaDatas()) {
                    singleMap.put(umdd.getName(), umdd);
                }
                uds = new ArrayList<>();
                for (Map.Entry<String, UploadMetadataDTO> entry1 : singleMap.entrySet()) {
                    uds.add(entry1.getValue());
                }
                uds.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
                u.setUploadMetaDatas(uds);
                if(u.getShowToQc()!=null && u.getShowToQc().equals(1)){
                    responseUploadList.add(u);
                    responseUploads.get(entry.getKey()).add(u);
                }
            }
        }
        return ResponseEntity.ok().body(responseUploads);
    }

   

    private Map<String,List<Upload>> getUploadsOfProjectForQc(Long projectId, Long customerQcProjectMappingId, Integer qcLevel){
        Map<String,List<Upload>> uploads = new HashMap<>();
        ProjectQcLevelConfigurations pqlc = projectQcLevelConfigurationsRepository.findByProejctIdAndQcLevel(projectId,qcLevel);
        CustomerQcProjectMapping cqpm = customerQcProjectMappingRepository.getById(customerQcProjectMappingId);
		/*
		 * if(pqlc.getQcLevel().equals(1)){ uploads =
		 * getUploadsOfProjectForQcLevel1(projectId,customerQcProjectMappingId,qcLevel,
		 * pqlc,cqpm); }else{ uploads =
		 * getUploadsOfProjectForQcLevelGreaterThan1(projectId,
		 * customerQcProjectMappingId,qcLevel,pqlc,cqpm); }
		 */
        uploads = getUploads(projectId,customerQcProjectMappingId,qcLevel,pqlc,cqpm);
        return uploads;
    }
    
    private Map<String,List<Upload>> getUploads(Long projectId, Long customerQcProjectMappingId, Integer qcLevel,ProjectQcLevelConfigurations pqlc,CustomerQcProjectMapping cqpm) {
    	
    	CustomerQcProjectMappingBatchMapping cqpmbm = customerQcProjectMappingBatchMappingRepository.findByCustomerQcProjectMappingIdAndBatchNumber(cqpm.getId(), cqpm.getCurrentQcBatchNo());
    	Project project = projectRepository.getById(projectId);
    	List<Long> objectsQcNotStarted = new LinkedList<Long>();
    	List<Upload> uploads =  new LinkedList<>();
    	List<Long> uvmomIds = new LinkedList<Long>();
    	List<Upload> tempUploads = new LinkedList<>();
    	Map<String,List<Upload>> resultMap = new HashMap<>();
    	Long multFactorForSelectingNumOfUploads = 1l;
    	
		/*
		 * List<Long> allUploadsInBatch =
		 * uploadCustomerQcProjectMappingBatchInfoRepository.
		 * getAllPendingBatchInfoForProjectAndLevelForLoggedInUserPreviousThanLevel1(
		 * projectId,
		 * allCompletedBatchForProject.get(0).getCustomerQcProjectMapping().getId(),
		 * allCompletedBatchForProject.get(0).getBatchNo()); Float
		 * numOfUploadsNeedToBeShownBasedOnQcLevelAcceptancePercentage =
		 * (pqlc.getQcLevelAcceptancePercentage().floatValue() / 100f) *
		 * allUploadsInBatch.size(); multFactorForSelectingNumOfUploads =
		 * allUploadsInBatch.size() /
		 * numOfUploadsNeedToBeShownBasedOnQcLevelAcceptancePercentage.longValue();
		 */
        
    	if(project.getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS)) {
    		objectsQcNotStarted = uploadRepository.findAllObjectsQcNotStarted(projectId,cqpm.getQcLevel(),pqlc.getQcLevelBatchSize());
        	uvmomIds = uploadRepository.findAllUvmomsQcNotStarted(objectsQcNotStarted, cqpm.getQcLevel(), pqlc.getQcLevelBatchSize());
    	}else {
    		uvmomIds = uploadRepository.findAllUvmomsQcNotStarted(projectId, cqpm.getQcLevel(), pqlc.getQcLevelBatchSize());
    	}
    	if(cqpmbm==null) {
    		cqpmbm = new CustomerQcProjectMappingBatchMapping();
    		cqpmbm.setBatchNo(cqpm.getCurrentQcBatchNo()+1);
    		customerQcProjectMappingBatchMappingRepository.save(cqpmbm);
    		uploads = uploadRepository.findAllUploadIds(uvmomIds);
    		for(Upload u:uploads) {
    			UploadCustomerQcProjectMappingBatchInfo ucqpmbi = new UploadCustomerQcProjectMappingBatchInfo();
    			ucqpmbi.setBatchNumber(cqpmbm.getId());
    			ucqpmbi.setUploadId(u.getId());
    			ucqpmbi.setCustomerQcProjectMappingId(cqpm.getId());
    			uploadCustomerQcProjectMappingBatchInfoRepository.save(ucqpmbi);
    			if(resultMap.get(u.getUserVendorMappingObjectMapping().getObject().getId().toString()+"-"+u.getUserVendorMappingObjectMapping().getObject().getName()+"-"+cqpmbm.getBatchCompletionStatus())==null){
                    resultMap.put(u.getUserVendorMappingObjectMapping().getObject().getId().toString()+"-"+u.getUserVendorMappingObjectMapping().getObject().getName()+"-"+cqpmbm.getBatchCompletionStatus(),new ArrayList<>());
                }
                u.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
                u.setQcDoneBy(cqpm);
                u.setQcStartDate(Instant.now());
                u.setQcBatchInfo(String.valueOf(ucqpmbi.getId()));
                u.setCurrentBatchNumber(cqpmbm.getId().intValue());
                tempUploads.add(u);
                
                u.getUserVendorMappingObjectMapping().setCurrentQcLevel(cqpm.getQcLevel());
                u.getUserVendorMappingObjectMapping().setQcStartStatus(AidasConstants.AIDAS_UPLOAD_QC_STARTED);

                u.getUserVendorMappingObjectMapping().getObject().setCurrentQcLevel(cqpm.getQcLevel());
                u.getUserVendorMappingObjectMapping().getObject().setQcStartStatus(AidasConstants.AIDAS_UPLOAD_QC_STARTED);
                
                u.getUserVendorMappingObjectMapping().getObject().getProject().setCurrentQcLevel(cqpm.getQcLevel());
                u.getUserVendorMappingObjectMapping().getObject().getProject().setQcStartStatus(AidasConstants.AIDAS_UPLOAD_QC_STARTED);
                
                resultMap.get(u.getUserVendorMappingObjectMapping().getObject().getId().toString()+"-"+u.getUserVendorMappingObjectMapping().getObject().getName()+"-"+cqpmbm.getBatchCompletionStatus()).add(u);
    		}
    	}else {
    		uploads = uploadCustomerQcProjectMappingBatchInfoRepository.getUploadIdsInBatch(cqpmbm.getId());
    	}
    	uploadRepository.saveAll(tempUploads);
    	return resultMap;
    }

    private Map<String,List<Upload>> getUploadsOfProjectForQcLevel1(Long projectId, Long customerQcProjectMappingId, Integer qcLevel,ProjectQcLevelConfigurations pqlc,CustomerQcProjectMapping cqpm){
        List<Upload> uploads = new LinkedList<>();
        boolean batchChanged = false;
        List<Long[]> objectsWithUvmomNotStarted = uploadRepository.findObjectsWithUvmomQcNotStarted(projectId,cqpm.getQcLevel());
        CustomerQcProjectMappingBatchMapping cqpmbm = customerQcProjectMappingBatchMappingRepository.findByCustomerQcProjectMappingIdAndBatchNumber(cqpm.getId(), cqpm.getCurrentQcBatchNo());
        Integer newBatchNumber = cqpm.getCurrentQcBatchNo();
        if (pqlc.getAllocationStrategy().equals(AidasConstants.QC_LEVEL_FCFS)) {
            if(cqpmbm!=null && cqpmbm.getBatchCompletionStatus()==2){
                    List<Long> allUploadsInBatch = uploadCustomerQcProjectMappingBatchInfoRepository.getAllAllBatchInfoForProjectAndLevelForLoggedInUserLevel1(projectId, qcLevel, customerQcProjectMappingId, cqpm.getCurrentQcBatchNo());
                    uploads = uploadRepository.getUploadsByIds(allUploadsInBatch);
                    batchChanged= false;
            }else{
                if(objectsWithUvmomNotStarted!=null && objectsWithUvmomNotStarted.size()>0){
                    Project project = projectRepository.getById(projectId);
                    List<Long> oids=new ArrayList<>();
                    List<Long> uvmomids = new ArrayList<>();
                    if(project!=null && project.getAutoCreateObjects() !=null &&project.getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS)){
                        if(objectsWithUvmomNotStarted.size()>=pqlc.getQcLevelBatchSize()){
                            for(int i=0;i<pqlc.getQcLevelBatchSize();i++){
                                oids.add(objectsWithUvmomNotStarted.get(i)[0]);
                                uvmomids.add(objectsWithUvmomNotStarted.get(i)[1]);
                            }
                        }else{
                            for(int i=0;i<objectsWithUvmomNotStarted.size();i++){
                                oids.add(objectsWithUvmomNotStarted.get(i)[0]);
                                uvmomids.add(objectsWithUvmomNotStarted.get(i)[1]);
                            }
                        }
                    }else {
                        if(objectsWithUvmomNotStarted!=null && objectsWithUvmomNotStarted.size()>0){
                            oids.add(objectsWithUvmomNotStarted.get(0)[0]);
                            uvmomids.add(objectsWithUvmomNotStarted.get(0)[1]);
                        }
                    }
                    if(project.getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS)){
                        List<Long> subList = new ArrayList<>();
                        if(oids.size()> pqlc.getQcLevelBatchSize()){
                            subList = oids.subList(0, pqlc.getQcLevelBatchSize());
                        }else{
                            subList = oids;
                        }
                        uploads = uploadRepository.getUploadsForGroupedProjectToBeAssignedToQcLevel1FromUploads(projectId, subList);
                    }else {
                        uploads = uploadRepository.getUploadsForProjectToBeAssignedToQcLevel1FromUploads(projectId, oids, uvmomids, pqlc.getQcLevelBatchSize());
                    }
                    //uploads = uploadRepository.getUploadsForProjectToBeAssignedToQcLevel1FromUploads(projectId, objectsWithUvmomNotStarted.get(0)[0], objectsWithUvmomNotStarted.get(0)[1], pqlc.getQcLevelBatchSize());
                    batchChanged = true;
                    newBatchNumber = cqpm.getCurrentQcBatchNo() + 1;
                }else {
                        System.out.println("No objects are available for qc level 1");
                }
            }
        }else if (pqlc.getAllocationStrategy().equals(AidasConstants.QC_LEVEL_ED)) {

        }
        Map<String,List<Upload>> resultMap = new HashMap<>();
        if(uploads!=null && uploads.size()>0) {
            if (batchChanged) {
                //if (cqpmbm == null) {
                    cqpmbm = new CustomerQcProjectMappingBatchMapping();
                    cqpmbm.setCustomerQcProjectMapping(cqpm);
                    cqpmbm.setBatchNo(newBatchNumber);
                    cqpmbm.setBatchCompletionStatus(AidasConstants.AIDAS_UPLOAD_QC_BATCH_PENDING);
                //}
            }
            cqpm.setCurrentQcBatchNo(newBatchNumber);
            customerQcProjectMappingRepository.save(cqpm);
            cqpmbm.setCustomerQcProjectMapping(cqpm);
            cqpmbm.setBatchNo(newBatchNumber);
            customerQcProjectMappingBatchMappingRepository.save(cqpmbm);
        }
        for (Upload u : uploads) {
            if(resultMap.get(u.getUserVendorMappingObjectMapping().getObject().getId().toString()+"-"+u.getUserVendorMappingObjectMapping().getObject().getName()+"-"+cqpmbm.getBatchCompletionStatus())==null){
                resultMap.put(u.getUserVendorMappingObjectMapping().getObject().getId().toString()+"-"+u.getUserVendorMappingObjectMapping().getObject().getName()+"-"+cqpmbm.getBatchCompletionStatus(),new ArrayList<>());
            }
            if(batchChanged) {
                UploadCustomerQcProjectMappingBatchInfo ucqpmbinfo = new UploadCustomerQcProjectMappingBatchInfo();
                ucqpmbinfo.setUploadId(u.getId());
                ucqpmbinfo.setCustomerQcProjectMappingId(cqpm.getId());
                ucqpmbinfo.setBatchNumber(newBatchNumber.longValue());
                ucqpmbinfo.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
                u.setCurrentBatchNumber(newBatchNumber);
                uploadCustomerQcProjectMappingBatchInfoRepository.save(ucqpmbinfo);
                u.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
                u.setQcDoneBy(cqpm);
                u.setQcStartDate(Instant.now());
                uploadRepository.save(u);
            }
            resultMap.get(u.getUserVendorMappingObjectMapping().getObject().getId().toString()+"-"+u.getUserVendorMappingObjectMapping().getObject().getName()+"-"+cqpmbm.getBatchCompletionStatus()).add(u);
        }

        return resultMap;
    }

    private Map<String,List<Upload>>  getUploadsOfProjectForQcLevelGreaterThan1(Long projectId, Long customerQcProjectMappingId, Integer qcLevel,ProjectQcLevelConfigurations pqlc,CustomerQcProjectMapping cqpm) {

        List<Upload> batchUploads = new LinkedList<>();
        Integer newBatchNumber = cqpm.getCurrentQcBatchNo();
        Long multFactorForSelectingNumOfUploads = 1l;
        Project project = projectRepository.getById(projectId);
        List<CustomerQcProjectMappingBatchMapping> allCompletedBatchForProject = null;// customerQcProjectMappingBatchMappingRepository.getAllCompletedBatchNumberForQcLevel(projectId, qcLevel-1);
        CustomerQcProjectMappingBatchMapping cqpmbm = customerQcProjectMappingBatchMappingRepository.findByCustomerQcProjectMappingIdAndBatchNumber(cqpm.getId(), cqpm.getCurrentQcBatchNo());
        if (qcLevel - 1 == 1) {
            if(project.getAutoCreateObjects().equals(1)) {
                allCompletedBatchForProject = customerQcProjectMappingBatchMappingRepository.getAllCompletedBatchNumberForQcLevel(projectId, qcLevel - 1);
            }else if(project.getAutoCreateObjects().equals(0)){
                allCompletedBatchForProject = customerQcProjectMappingBatchMappingRepository.getAllCompletedBatchNumberForQcLevelNonGrouped(projectId, qcLevel - 1);
            }
        } else {
            if(project.getAutoCreateObjects().equals(1)) {
                allCompletedBatchForProject = customerQcProjectMappingBatchMappingRepository.getAllCompletedBatchNumberForQcLevelGreaterThan1(projectId, qcLevel - 1);
            }else if(project.getAutoCreateObjects().equals(0)){
                if(qcLevel>2){
                    allCompletedBatchForProject = customerQcProjectMappingBatchMappingRepository.getAllCompletedBatchNumberForQcLevelNonGroupedGreaterThanLevel2(projectId, qcLevel - 1);
                }else{
                    allCompletedBatchForProject = customerQcProjectMappingBatchMappingRepository.getAllCompletedBatchNumberForQcLevelNonGrouped(projectId, qcLevel - 1);
                }

            }
        }

        boolean batchChanged = false;
        if (pqlc.getAllocationStrategy().equals(AidasConstants.QC_LEVEL_FCFS)) {
            if(cqpmbm!=null && cqpmbm.getBatchCompletionStatus()==2 ){
                List<Long> uploadsPendingByLoggedInQc = uploadCustomerQcProjectMappingBatchInfoRepository.getAllPendingBatchInfoForProjectAndLevelForLoggedInUserGreaterThanLevel1(projectId, qcLevel, customerQcProjectMappingId, cqpm.getCurrentQcBatchNo());
                if (uploadsPendingByLoggedInQc != null && uploadsPendingByLoggedInQc.size() > 0) {
                    batchUploads = uploadRepository.getUploadsByIds(uploadsPendingByLoggedInQc);
                    batchChanged = false;
                }
            } else {
                if (allCompletedBatchForProject != null && allCompletedBatchForProject.size() > 0) {
                    List<Long> allUploadsInBatch = uploadCustomerQcProjectMappingBatchInfoRepository.getAllPendingBatchInfoForProjectAndLevelForLoggedInUserPreviousThanLevel1(projectId, allCompletedBatchForProject.get(0).getCustomerQcProjectMapping().getId(), allCompletedBatchForProject.get(0).getBatchNo());
                    Float numOfUploadsNeedToBeShownBasedOnQcLevelAcceptancePercentage = (pqlc.getQcLevelAcceptancePercentage().floatValue() / 100f) * allUploadsInBatch.size();
                    multFactorForSelectingNumOfUploads = allUploadsInBatch.size() / numOfUploadsNeedToBeShownBasedOnQcLevelAcceptancePercentage.longValue();
                    batchUploads = uploadRepository.getUploadsByIds(allUploadsInBatch);
                    batchChanged = true;
                    if (batchChanged || cqpm.getCurrentQcBatchNo().equals(0)) {
                        newBatchNumber = cqpm.getCurrentQcBatchNo() + 1;
                    }
                }
            }
        } else if (pqlc.getAllocationStrategy().equals(AidasConstants.QC_LEVEL_ED)) {

        }
        Map<String,List<Upload>> resultMap = new HashMap<>();
        int j = 0;
        if (batchUploads != null && batchUploads.size() > 0) {
            if(batchUploads!=null && batchUploads.size()>0) {
                if (batchChanged) {
                    cqpm.setCurrentQcBatchNo(newBatchNumber);
                    customerQcProjectMappingRepository.save(cqpm);
                    //if (cqpmbm == null) {
                        cqpmbm = new CustomerQcProjectMappingBatchMapping();
                        cqpmbm.setCustomerQcProjectMapping(cqpm);
                        cqpmbm.setBatchNo(newBatchNumber);
                        cqpmbm.setStatus(0);
                    //}
                    cqpmbm.setCustomerQcProjectMapping(cqpm);
                    cqpmbm.setBatchNo(newBatchNumber);
                    cqpmbm.setBatchCompletionStatus(AidasConstants.AIDAS_UPLOAD_QC_BATCH_PENDING);
                    cqpmbm.setPreviousLevelBatchNumber(allCompletedBatchForProject.get(0).getId());
                    customerQcProjectMappingBatchMappingRepository.save(cqpmbm);
                    allCompletedBatchForProject.get(0).setNextLevelBatchNumber(cqpmbm.getId());
                    customerQcProjectMappingBatchMappingRepository.save(allCompletedBatchForProject.get(0));
                }
            }
            for (Upload u : batchUploads) {
                if(resultMap.get(u.getUserVendorMappingObjectMapping().getObject().getId().toString()+"-"+u.getUserVendorMappingObjectMapping().getObject().getName()+"-"+cqpmbm.getBatchCompletionStatus())==null){
                    resultMap.put(u.getUserVendorMappingObjectMapping().getObject().getId().toString()+"-"+u.getUserVendorMappingObjectMapping().getObject().getName()+"-"+cqpmbm.getBatchCompletionStatus(),new ArrayList<>());
                }
                UploadCustomerQcProjectMappingBatchInfo ucqpmbinfo;
                if (batchChanged) {
                    ucqpmbinfo = new UploadCustomerQcProjectMappingBatchInfo();
                    ucqpmbinfo.setUploadId(u.getId());
                    ucqpmbinfo.setCustomerQcProjectMappingId(cqpm.getId());
                    ucqpmbinfo.setBatchNumber(newBatchNumber.longValue());
                    ucqpmbinfo.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
                    u.setCurrentBatchNumber(newBatchNumber);
                    if (j % multFactorForSelectingNumOfUploads == 0) {
                        ucqpmbinfo.setShowToQc(1);
                        u.setShowToQc(1);
                    } else {
                        ucqpmbinfo.setShowToQc(0);
                        u.setShowToQc(0);
                    }
                    uploadCustomerQcProjectMappingBatchInfoRepository.save(ucqpmbinfo);
                }
                u.setQcDoneBy(cqpm);
                u.setQcStartDate(Instant.now());
                u.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
                uploadRepository.save(u);
               // uploads.add(u);
                resultMap.get(u.getUserVendorMappingObjectMapping().getObject().getId().toString()+"-"+u.getUserVendorMappingObjectMapping().getObject().getName()+"-"+cqpmbm.getBatchCompletionStatus()).add(u);
                j++;
            }
        }

        return resultMap;
    }

    /**
     * {@code GET  /aidas-uploads/next} : get the "id" upload.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the upload, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-uploads/next/{projectId}/{qcLevel}")
    public ResponseEntity<List<Upload>> getNextAidasUpload(@PathVariable(name = "projectId") Long projectId,@PathVariable(name = "qcLevel") Integer qcLevel) {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to get next AidasUpload : {}");
        Authority authority = user.getAuthority();
        List<CustomerQcProjectMapping> cqpms = customerQcProjectMappingRepository.getQcProjectMappingByProjectAndCustomerAndUser(projectId,user.getCustomer().getId(),user.getId());
        List<Upload> uploads = uploadRepository.findTopByQcNotDoneYetForQcLevel(projectId,qcLevel);
        if(uploads !=null) {
            for (Upload upload : uploads){
                Customer customer = upload.getUserVendorMappingObjectMapping().getObject().getProject().getCustomer();
                Project project = upload.getUserVendorMappingObjectMapping().getObject().getProject();
                List<CustomerQcProjectMapping> qpms = customerQcProjectMappingRepository.getQcProjectMappingByProjectAndCustomerAndUser(project.getId(), customer.getId(), user.getId());
                if (qpms != null && qpms.size() > 0) {
                    upload.setQcDoneBy(qpms.get(0));
                    upload.setQcStartDate(Instant.now());
                    upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
                }
        }
        }else{
            uploads = new ArrayList<>();
            return ResponseEntity.ok().body(uploads);
        }
        uploadRepository.saveAll(uploads);
        return ResponseEntity.ok().body(uploads);
    }


    /**
     * {@code DELETE  /aidas-uploads/:id} : delete the "id" upload.
     *
     * @param id the id of the upload to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aidas-uploads/{id}")
    public ResponseEntity<Void> deleteAidasUpload(@PathVariable Long id) {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to delete AidasUpload : {}", id);
        Authority authority = user.getAuthority();
        Upload upload = uploadRepository.getById(id);
        if(upload !=null){
            upload.setStatus(0);
            uploadRepository.save(upload);
        }
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-uploads?query=:query} : search for the upload corresponding
     * to the query.
     *
     * @param query the query of the upload search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/aidas-uploads")
    public ResponseEntity<List<Upload>> searchAidasUploads(@RequestParam String query, Pageable pageable) {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to search for a page of AidasUploads for query {}", query);
        Authority authority = user.getAuthority();
        Page<Upload> page = aidasUploadSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @Autowired
    TaskExecutor taskExecutor;

    @Autowired
    DownloadUploadS3 downloadUploadS3;
    /**
     * {@code GET  /download/uploads/} : download objects with the "id" object and provided status.  User "all" for download both.
     *
     * @param uploadIds array of upload ids to download.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the object, or with status {@code 404 (Not Found)}.
     */
    @PostMapping("/download/uploads")
    public void downloadUploadedObjects(@RequestBody List<Long> uploadIds){
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        downloadUploadS3.setUser(user);
        downloadUploadS3.setUp(uploadIds);
        taskExecutor.execute(downloadUploadS3);
    }
}
