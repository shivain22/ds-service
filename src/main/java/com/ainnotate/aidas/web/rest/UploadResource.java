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
import javax.validation.Valid;
import javax.validation.constraints.NotNull;



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

    public UploadResource(UploadRepository uploadRepository, UploadSearchRepository aidasUploadSearchRepository) {
        this.uploadRepository = uploadRepository;
        this.aidasUploadSearchRepository = aidasUploadSearchRepository;
    }

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
            Object object = auaom.getObject();
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
    public ResponseEntity<List<UploadMetadataDTO>> submitMetadata(@Valid @RequestBody List<UploadMetadataDTO> uploadMetadataDTOS) throws URISyntaxException {
        log.debug("REST request to save AidasUpload : {}", uploadMetadataDTOS);
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Authority authority = user.getAuthority();
        if(authority.getName().equals(AidasConstants.VENDOR_USER)){
            for(UploadMetadataDTO umd:uploadMetadataDTOS){
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
    public ResponseEntity<String> submitForQc(@Valid @RequestBody List<Long> uploadIds) throws URISyntaxException {
        log.debug("REST request to save AidasUpload : {}", uploadIds);
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Authority authority = user.getAuthority();
        if(authority.getName().equals(AidasConstants.VENDOR_USER)){
            for(Long upId:uploadIds){
                Upload upload = uploadRepository.getById(upId);
                //if(upload.getMetadataStatus().equals(AidasConstants.AIDAS_UPLOAD_METADATA_COMPLETED)) {
                    upload.setMetadataStatus(AidasConstants.AIDAS_UPLOAD_METADATA_COMPLETED);
                    upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
                //}
                uploadRepository.save(upload);
            }
            return ResponseEntity.ok().body("Success");
        }
        throw new BadRequestAlertException("You can not upload as there was an error internally", ENTITY_NAME, "idexists");
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
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to save AidasUpload : {}", uploadDto);
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
                    if (o.getTotalRequired() > 0)
                        o.setTotalRequired(o.getTotalRequired() - 1);

                    p.setTotalUploaded(p.getTotalUploaded() + 1);
                    p.setTotalPending(p.getTotalPending() + 1);
                    if (p.getTotalRequired() > 0)
                        p.setTotalRequired(p.getTotalRequired() - 1);

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
                        System.out.println(entry.getKey() + "==" + entry.getValue());
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
        throw new BadRequestAlertException("End of Upload...No way ", ENTITY_NAME, "idexists");
    }


    /**
     * {@code POST  /aidas-uploads/dto} : Create a new upload.
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
                o.setTotalRequired(o.getTotalRequired() - 1);

                p.setTotalUploaded(p.getTotalUploaded() + 1);
                p.setTotalPending(p.getTotalPending() + 1);
                p.setTotalRequired(p.getTotalRequired() - 1);

                UserVendorMappingProjectMapping uvmpm = userVendorMappingProjectMappingRepository.findByUserVendorMappingIdProjectId(upload.getUserVendorMappingObjectMapping().getUserVendorMapping().getId(), p.getId());
                uvmpm.setTotalUploaded(uvmpm.getTotalUploaded() + 1);
                uvmpm.setTotalPending(uvmpm.getTotalPending() + 1);

                objectRepository.save(o);
                projectRepository.save(p);

                uvmom.setTotalUploaded(uvmom.getTotalUploaded() + 1);
                uvmom.setTotalPending(uvmom.getTotalPending() + 1);

                userVendorMappingObjectMappingRepository.save(uvmom);
                userVendorMappingProjectMappingRepository.save(uvmpm);
                for (Map.Entry<String, String> entry : uploadDto.getUploadMetadata().entrySet()) {
                    Property property = propertyRepository.getByNameAndUserIdAndCategory(entry.getKey().trim(), customer.getId(),project.getId());
                    if (property != null) {
                        ProjectProperty projectProperty = projectPropertyRepository.findByProjectAndProperty(project.getId(), property.getId());
                        ObjectProperty objectProperty = objectPropertyRepository.findByAidasObject_IdAndAidasProperty_Id(object.getId(), property.getId());
                        if (projectProperty != null) {
                            UploadMetaData uploadMetaData = new UploadMetaData();
                            uploadMetaData.setUpload(upload);
                            uploadMetaData.setProjectProperty(projectProperty);
                            uploadMetaData.setValue(entry.getValue().toString());
                            uploadMetaDataRepository.save(uploadMetaData);
                        }
                        if (objectProperty != null) {
                            UploadMetaData uploadMetaData = new UploadMetaData();
                            uploadMetaData.setUpload(upload);
                            uploadMetaData.setObjectProperty(objectProperty);
                            uploadMetaData.setValue(entry.getValue().toString());
                            uploadMetaDataRepository.save(uploadMetaData);
                        }
                    }
                }
                return ResponseEntity
                    .created(new URI("/api/aidas-uploads/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BadRequestAlertException("Internal error occured.  Contact administrator", ENTITY_NAME, "idexists");
            }
        }
        }
        throw new BadRequestAlertException("End of Upload...No way ", ENTITY_NAME, "idexists");
    }

    /**
     * {@code POST  /aidas-uploads/dto} : Create a new upload.
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
        if (!uploadRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }


        Upload upload = uploadRepository.getById(id);
        UserVendorMappingObjectMapping uvmom = upload.getUserVendorMappingObjectMapping();
        Customer customer = uvmom.getObject().getProject().getCustomer();
        Project project = uvmom.getObject().getProject();
        CustomerQcProjectMapping cqpm = customerQcProjectMappingRepository.getById(customerQcProjectMappingId);
        UploadCustomerQcProjectMappingBatchInfo ucqpmbi =  uploadCustomerQcProjectMappingBatchInfoRepository.getUploadIdByCustomerQcProjectMappingAndBatchNumber(customerQcProjectMappingId,cqpm.getCurrentQcBatchNo(),upload.getId());
        Integer totalApprovedAndAvailableForCurrentLevel = uploadRepository.getTotalApprovedForLevel(project.getId(), cqpm.getQcLevel());
        Integer totalApprovedAndAvailableForNextLevel = uploadRepository.getTotalApprovedForLevel(project.getId(), cqpm.getQcLevel() + 1);
        Integer batchSize = 10;
        Integer currentQcLevelAcceptancePercentage = 50;
        Float currentQcLevelReviewRequired =0f;
        if(project.getQcLevels()!=null && project.getQcLevels()>1) {

            if (project != null && project.getQcLevelConfigurations() != null) {
                for (QCLevelConfiguration qclc : project.getQcLevelConfigurations()) {
                    if (cqpm != null && cqpm.getQcLevel() != null && qclc.getQcLevelName() != null && qclc.getQcLevelName().equals(cqpm.getQcLevel())) {
                        batchSize = qclc.getQcLevelBatchSize();
                        currentQcLevelAcceptancePercentage = qclc.getQcLevelAcceptancePercentage();
                    }
                }
            }
            currentQcLevelReviewRequired = (currentQcLevelAcceptancePercentage.floatValue() / 100f) * totalApprovedAndAvailableForCurrentLevel;
        }
        Set<Upload> remainingUploads =new HashSet<>();
        HashMap<Long, Integer> oldQcStatus=new HashMap<>();
        oldQcStatus.put(upload.getId(),upload.getQcStatus());
        if(cqpm!=null && project.getQcLevels().equals(cqpm.getQcLevel())) {
            upload.setStatus(AidasConstants.AIDAS_UPLOAD_APPROVED);
            upload.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_APPROVED);
            upload.setQcDoneBy(cqpm);
            upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_APPROVED);
            upload.setQcEndDate(Instant.now());
            if(project.getQcLevels()>1  && totalApprovedAndAvailableForNextLevel-currentQcLevelReviewRequired.intValue() <=0){
                if(project.getAutoCreateObjects().equals(1)){
                    remainingUploads = uploadRepository.getApprovedUploadForLevel(project.getId(),upload.getUserVendorMappingObjectMapping().getObject().getId(), cqpm.getQcLevel());
                }else {
                    remainingUploads = uploadRepository.getApprovedUploadForLevel(project.getId(), cqpm.getQcLevel());
                }
            }else{
                remainingUploads.add(upload);
            }
        }else if(cqpm!=null && cqpm.getQcLevel()<project.getQcLevels()){
            upload.setStatus(AidasConstants.AIDAS_UPLOAD_APPROVED);
            upload.setQcDoneBy(cqpm);
            upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_APPROVED);
            upload.setQcEndDate(Instant.now());
            uploadRepository.save(upload);
            uploadCustomerQcProjectMappingBatchInfoRepository.save(ucqpmbi);
            remainingUploads.add(upload);
        }
        for(Upload upload1:remainingUploads){
            if(oldQcStatus.get(upload1.getId())==null) {
                oldQcStatus.put(upload1.getId(), upload1.getQcStatus());
            }
            UploadCustomerQcProjectMappingBatchInfo ucqpmbi1 =  uploadCustomerQcProjectMappingBatchInfoRepository.getUploadIdByCustomerQcProjectMappingAndBatchNumber(customerQcProjectMappingId,cqpm.getCurrentQcBatchNo(),upload1.getId());
            upload1.setStatus(AidasConstants.AIDAS_UPLOAD_APPROVED);
            upload1.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_APPROVED);
            upload1.setQcDoneBy(cqpm);
            upload1.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_APPROVED);
            upload1.setQcEndDate(Instant.now());
            upload1.setCurrentQcLevel(cqpm.getQcLevel()+1);
            Object o = upload1.getUserVendorMappingObjectMapping().getObject();
            Project p = o.getProject();
            UserVendorMappingProjectMapping uvmpm = userVendorMappingProjectMappingRepository.findByUserVendorMappingIdProjectId(upload1.getUserVendorMappingObjectMapping().getUserVendorMapping().getId(), p.getId());
            uvmom = upload1.getUserVendorMappingObjectMapping();
            if(oldQcStatus.get(upload1.getId()).equals(AidasConstants.AIDAS_UPLOAD_QC_PENDING)) {
                o.setTotalApproved(o.getTotalApproved() + 1);
                p.setTotalApproved(p.getTotalApproved() + 1);
                uvmpm.setTotalApproved(uvmpm.getTotalApproved() + 1);
                uvmom.setTotalApproved(uvmom.getTotalApproved() + 1);
                uvmpm.setTotalPending(uvmpm.getTotalPending() - 1);
                uvmom.setTotalPending(uvmom.getTotalPending() - 1);
            }else if(oldQcStatus.get(upload1.getId()).equals(AidasConstants.AIDAS_UPLOAD_QC_REJECTED)){
                o.setTotalApproved(o.getTotalApproved() + 1);
                p.setTotalApproved(p.getTotalApproved()+1);
                o.setTotalRequired(o.getTotalRequired() - 1);
                p.setTotalRequired(p.getTotalRequired()-1);
                uvmpm.setTotalApproved(uvmpm.getTotalApproved() + 1);
                uvmom.setTotalApproved(uvmom.getTotalApproved() + 1);
                o.setTotalRejected(o.getTotalRejected()-1);
                p.setTotalRejected(p.getTotalRejected()-1);
                uvmpm.setTotalRejected(uvmpm.getTotalRejected() - 1);
                uvmom.setTotalRejected(uvmom.getTotalRejected() - 1);
            }
            if (ucqpmbi != null) {
                ucqpmbi.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_APPROVED);
            }
            objectRepository.save(o);
            projectRepository.save(p);
            userVendorMappingProjectMappingRepository.save(uvmpm);
            userVendorMappingObjectMappingRepository.save(uvmom);
            uploadRepository.save(upload1);
            if(ucqpmbi1!=null) {
                uploadCustomerQcProjectMappingBatchInfoRepository.save(ucqpmbi1);
            }
        }
        if(ucqpmbi!=null){
            uploadCustomerQcProjectMappingBatchInfoRepository.save(ucqpmbi);
        }
        if(remainingUploads.size()==0){
            remainingUploads.add(upload);
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
        if (!uploadRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        Upload upload = uploadRepository.getById(id);
        UserVendorMappingObjectMapping uvmom = upload.getUserVendorMappingObjectMapping();
        Customer customer = upload.getUserVendorMappingObjectMapping().getObject().getProject().getCustomer();
        Project project = upload.getUserVendorMappingObjectMapping().getObject().getProject();
        CustomerQcProjectMapping cqpm = customerQcProjectMappingRepository.getById(customerQcProjectMappingId);
        UploadCustomerQcProjectMappingBatchInfo ucqpmbi =  uploadCustomerQcProjectMappingBatchInfoRepository.getUploadIdByCustomerQcProjectMappingAndBatchNumber(customerQcProjectMappingId,cqpm.getCurrentQcBatchNo(),upload.getId());
        ucqpmbi.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_REJECTED);
        upload.setStatus(AidasConstants.AIDAS_UPLOAD_REJECTED);
        upload.setQcEndDate(Instant.now());
        HashMap<Long, Integer> oldQcStatus=new HashMap<>();
        oldQcStatus.put(upload.getId(),upload.getQcStatus());
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
        uploadCustomerQcProjectMappingBatchInfoRepository.save(ucqpmbi);
        Object o = result.getUserVendorMappingObjectMapping().getObject();
        Project p = o.getProject();
        UserVendorMappingProjectMapping uvmpm = userVendorMappingProjectMappingRepository.findByUserVendorMappingIdProjectId(upload.getUserVendorMappingObjectMapping().getUserVendorMapping().getId(),p.getId());
        if(oldQcStatus.get(upload.getId()).equals(AidasConstants.AIDAS_UPLOAD_QC_PENDING)) {
            o.setTotalRejected(o.getTotalRejected()+1);
            p.setTotalRejected(p.getTotalRejected()+1);
            o.setTotalRequired(o.getTotalRequired()+1);
            p.setTotalRequired(p.getTotalRequired()+1);
            uvmpm.setTotalRejected(uvmpm.getTotalRejected()+1);
            uvmom.setTotalRejected(uvmom.getTotalRejected()+1);
            o.setTotalPending(o.getTotalPending()-1);
            p.setTotalPending(p.getTotalPending()-1);
            uvmpm.setTotalPending(uvmpm.getTotalPending()-1);
            uvmom.setTotalPending(uvmom.getTotalPending()-1);

        }else if(oldQcStatus.get(upload.getId()).equals(AidasConstants.AIDAS_UPLOAD_QC_APPROVED)){
            o.setTotalRequired(o.getTotalRequired()+1);
            p.setTotalRequired(p.getTotalRequired()+1);
            o.setTotalRejected(o.getTotalRejected()+1);
            p.setTotalRejected(p.getTotalRejected()+1);
            uvmpm.setTotalRejected(uvmpm.getTotalRejected()+1);
            uvmom.setTotalRejected(uvmom.getTotalRejected()+1);
            o.setTotalApproved(o.getTotalApproved()-1);
            p.setTotalApproved(p.getTotalApproved()-1);
            uvmpm.setTotalApproved(uvmpm.getTotalApproved()-1);
            uvmom.setTotalApproved(uvmom.getTotalApproved()-1);
        }
        objectRepository.save(o);
        projectRepository.save(p);
        userVendorMappingProjectMappingRepository.save(uvmpm);
        userVendorMappingObjectMappingRepository.save(uvmom);
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
    @GetMapping("/aidas-uploads/metadata/{projectId}")
    public ResponseEntity<List<UploadsMetadataDTO>> getAllAidasUploadsForMetadata(@PathVariable Long projectId) {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        List<UploadsMetadataDTO> uploadsMetadataDTOList = new ArrayList<>();
            List<Upload> uploads = uploadRepository.findAllByUserAndProjectAllForMetadata(user.getId(),projectId);
            UploadsMetadataDTO uploadsMetadataDTO;
            List<ProjectProperty> projectProperties = projectPropertyRepository.findAllMetaDataToBeFilledByVendorUser(projectId);
            for(Upload au : uploads) {
                List<ObjectProperty> objectProperties = objectPropertyRepository.findAllMetaDataToBeFilledByVendorUser(au.getUserVendorMappingObjectMapping().getObject().getId());

                uploadsMetadataDTO = new UploadsMetadataDTO();
                UploadDTO uploadDTO = new UploadDTO();
                uploadDTO.setUploadId(au.getId());
                uploadDTO.setName(au.getUserVendorMappingObjectMapping().getObject().getName());
                uploadDTO.setObjectKey(au.getUploadUrl());
                uploadsMetadataDTO.setUploadDTO(uploadDTO);

                List<ProjectPropertyDTO> projectPropertyDTOS = new ArrayList<>();
                for(ProjectProperty p:projectProperties){
                    ProjectPropertyDTO pp = new ProjectPropertyDTO();
                    pp.setProjectPropertyId(p.getId());
                    pp.setName(p.getProperty().getName());
                    pp.setOptional(p.getOptional());
                    projectPropertyDTOS.add(pp);
                }
                uploadsMetadataDTO.setProjectProperties(projectPropertyDTOS);

                List<ObjectPropertyDTO> objectPropertyDTOS = new ArrayList<>();
                for(ObjectProperty o:objectProperties){
                    ObjectPropertyDTO oo = new ObjectPropertyDTO();
                    oo.setObjectPropertyId(o.getId());
                    oo.setName(o.getProperty().getName());
                    oo.setOptional(o.getOptional());
                    objectPropertyDTOS.add(oo);
                }
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
        List<CustomerQcProjectMapping> cqpms = customerQcProjectMappingRepository.getQcProjectMappingByProjectAndCustomerAndUser(projectId,user.getCustomer().getId(),user.getId());
        return ResponseEntity.ok().body(cqpms);
    }


    /**
     * {@code GET  /aidas-uploads/next} : get the "id" upload.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the upload, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-uploads/next-batch/{projectId}/{customerQcProjectMappingId}/{qcLevel}")
    public ResponseEntity<List<Upload>> getNextAidasUploadBatch(@PathVariable(name = "projectId") Long projectId,@PathVariable("customerQcProjectMappingId") Long customerQcProjectMappingId,@PathVariable("qcLevel") Integer qcLevel) {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to get next AidasUpload : {}");
        Authority authority = user.getAuthority();
        if (authority.getName().equals(AidasConstants.ADMIN)) {

        }
        if (authority.getName().equals(AidasConstants.ORG_ADMIN)) {

        }
        if (authority.getName().equals(AidasConstants.CUSTOMER_ADMIN)) {

        }
        if (authority.getName().equals(AidasConstants.VENDOR_ADMIN)) {

        }
        if (authority.getName().equals(AidasConstants.VENDOR_USER)) {

        }
        System.out.println("Customer Qc Project Mapping Id:-" + customerQcProjectMappingId);
        CustomerQcProjectMapping cqpm = customerQcProjectMappingRepository.getById(customerQcProjectMappingId);
        System.out.println(cqpm);
        Integer currentQcBatchNo = 1;
        if (cqpm.getCurrentQcBatchNo() != null) {
            currentQcBatchNo = cqpm.getCurrentQcBatchNo();
        }
        System.out.println("Current Batch No:-" + currentQcBatchNo);
        Integer pendingInBatch = uploadCustomerQcProjectMappingBatchInfoRepository.getQcPendingCount(customerQcProjectMappingId, currentQcBatchNo);
        System.out.println("Pending in Batch " + pendingInBatch);
        Project project = projectRepository.getById(projectId);

        Integer totalApprovedAndAvailableForCurrentLevel = uploadRepository.getTotalApprovedForLevel(projectId, qcLevel);
        Integer totalApprovedAndAvailableForNextLevel = uploadRepository.getTotalApprovedForLevel(projectId, qcLevel + 1);
        Integer batchSize = 10;
        Integer currentQcLevelAcceptancePercentage = 50;
        if (project != null && project.getQcLevelConfigurations() != null) {
            for (QCLevelConfiguration qclc : project.getQcLevelConfigurations()) {
                if (cqpm != null && cqpm.getQcLevel() != null && qclc.getQcLevelName() != null && qclc.getQcLevelName().equals(cqpm.getQcLevel())) {
                    batchSize = qclc.getQcLevelBatchSize();
                    currentQcLevelAcceptancePercentage = qclc.getQcLevelAcceptancePercentage();
                }
            }
        }

        Float currentQcLevelReviewRequired = (currentQcLevelAcceptancePercentage.floatValue() / 100f) * totalApprovedAndAvailableForCurrentLevel;

        List<Upload> uploads = new ArrayList<>();
        Long uniqueUploadId = 0l;
        if(qcLevel.equals(1) || currentQcLevelReviewRequired.intValue()>0){
            if (pendingInBatch != null && pendingInBatch > 0) {
                System.out.println("Inside pending in batch condition....");
                List<Long> uploadsByBatch = uploadCustomerQcProjectMappingBatchInfoRepository.getUploadIdByCustomerQcProjectMappingAndBatchNumber(customerQcProjectMappingId, currentQcBatchNo);
                uploads = uploadRepository.getUploadsByIds(uploadsByBatch);
                int i=0;
                if (cqpm != null) {
                    for (Upload upload : uploads) {
                        if(i==0){
                            uniqueUploadId = upload.getUserVendorMappingObjectMapping().getId();
                        }
                        upload.setQcDoneBy(cqpm);
                        upload.setQcStartDate(Instant.now());
                        uploadRepository.save(upload);
                        i++;
                    }
                    cqpm.setCurrentQcBatchNo(currentQcBatchNo);
                    customerQcProjectMappingRepository.save(cqpm);
                }
                System.out.println("Completed pending in batch condition....");
            } else {
                System.out.println("Inside getting new set of uploads for qc " + projectId + ",qc level =" + qcLevel);
                System.out.println("BatchSize: " + batchSize);
                if(cqpm.getQcLevel().equals(1)) {
                    uploads = uploadRepository.findTopByQcNotDoneYetForQcLevel(projectId, cqpm.getQcLevel(), batchSize);
                }else{
                    if(currentQcLevelReviewRequired.intValue()<batchSize)
                        uploads = uploadRepository.findTopByQcNotDoneYetForQcLevelGreaterThan1(projectId, cqpm.getQcLevel(), currentQcLevelReviewRequired.intValue());
                    else
                        uploads = uploadRepository.findTopByQcNotDoneYetForQcLevelGreaterThan1(projectId, cqpm.getQcLevel(), batchSize);
                }
                currentQcBatchNo = currentQcBatchNo + 1;

                if (cqpm != null) {
                    int i=0;
                    for (Upload upload : uploads) {
                        if(i==0){
                            uniqueUploadId = upload.getUserVendorMappingObjectMapping().getId();
                        }
                        if(upload.getUserVendorMappingObjectMapping().getId().equals(uniqueUploadId)) {
                            UploadCustomerQcProjectMappingBatchInfo ucqpmbinfo = new UploadCustomerQcProjectMappingBatchInfo();
                            ucqpmbinfo.setUploadId(upload.getId());
                            ucqpmbinfo.setCustomerQcMappingId(cqpm.getId());
                            ucqpmbinfo.setBatchNumber(currentQcBatchNo);
                            ucqpmbinfo.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
                            upload.setQcDoneBy(cqpm);
                            upload.setQcStartDate(Instant.now());
                            upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
                            uploadRepository.save(upload);
                            uploadCustomerQcProjectMappingBatchInfoRepository.save(ucqpmbinfo);
                        }
                        i++;
                    }
                    cqpm.setCurrentQcBatchNo(currentQcBatchNo);
                    customerQcProjectMappingRepository.save(cqpm);
                }
                System.out.println("Completed getting new set of uploads for qc " + projectId + ",qc level =" + qcLevel);
            }
        }else{
            System.out.println("Acceptance percentage is already achieved.  ");
        }
        List<Upload> responseUploads = new LinkedList<>();
        for(Upload u : uploads) {
            if(u.getUserVendorMappingObjectMapping().getId().equals(uniqueUploadId)) {
                UploadMetadataDTO ud1 = new UploadMetadataDTO();
                if (u.getUserVendorMappingObjectMapping() != null) {
                    if (u.getUserVendorMappingObjectMapping().getObject() != null) {
                        ud1.setValue(u.getUserVendorMappingObjectMapping().getObject().getName());
                        ud1.setName("Object Name");
                        u.getUploadMetaDatas().add(ud1);
                    }
                }
                List<UploadMetadataDTO> uds = new LinkedList<>();
                List<UploadMetaData> umds = uploadMetaDataRepository.getAllUploadMetaDataForUpload(u.getId());
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
                for (Map.Entry<String, UploadMetadataDTO> entry : singleMap.entrySet()) {
                    uds.add(entry.getValue());
                }
                uds.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
                u.setUploadMetaDatas(uds);
                responseUploads.add(u);
            }
        }
        return ResponseEntity.ok().body(responseUploads);
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
        List<CustomerQcProjectMapping> cqpms = customerQcProjectMappingRepository.getQcProjectMappingByProjectAndCustomerAndUser(projectId,user.getCustomer().getId(),user.getId());
        if(qcLevel!=null){

        }
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
        //aidasUploadRepository.deleteById(id);
        //aidasUploadSearchRepository.deleteById(id);
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

    /*@Scheduled(cron = "0 0/15 * * * *")
    public void clearQCPendingUploads(){
        List<AidasUpload> qcPendingUploadsForMoreThan10Mins = aidasUploadRepository.findUploadsHeldByQcForMoreThan10Mins();
        for(AidasUpload au:qcPendingUploadsForMoreThan10Mins){
            au.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
            au.setQcDoneBy(null);
            au.setQcStartDate(null);
            aidasUploadRepository.save(au);
        }
    }*/
}
