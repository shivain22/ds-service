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
    private PropertyRepository propertyRepository;
    @Autowired
    private ObjectPropertyRepository objectPropertyRepository;

    private final UploadRepository uploadRepository;

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private QcProjectMappingRepository qcProjectMappingRepository;

    private final UploadSearchRepository aidasUploadSearchRepository;

    @Autowired
    private UploadMetaDataRepository uploadMetaDataRepository;

    @Autowired
    private UploadRejectReasonRepository uploadRejectReasonRepository;

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
    public ResponseEntity<List<UploadMetaData>> submitMetadata(@Valid @RequestBody List<UploadMetadataDTO> uploadMetadataDTOS) throws URISyntaxException {
        log.debug("REST request to save AidasUpload : {}", uploadMetadataDTOS);
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Authority authority = user.getAuthority();
        if(authority.getName().equals(AidasConstants.VENDOR_USER)){
            List<UploadMetaData> success = new ArrayList<>();
            List<UploadMetaData> failed = new ArrayList<>();
            Map<Long,Long> projectPropertiesCount = new HashMap<>();
            Map<Long,Long> objectPropertiesCount = new HashMap<>();
            Map<Upload,Long> uploadPropertiesCount = new HashMap<>();
            for(UploadMetadataDTO umd:uploadMetadataDTOS){
                Upload upload = uploadRepository.getById(umd.getUploadId());
                uploadPropertiesCount.put(upload,0l);
                Project project = upload.getUserVendorMappingObjectMapping().getObject().getProject();
                Object object = upload.getUserVendorMappingObjectMapping().getObject();

                Long mandatoryProjectProperties = projectPropertyRepository.countProjectPropertyByProjectAndOptional(project.getId(),AidasConstants.AIDAS_PROPERTY_REQUIRED);
                Long mandatoryObjectProperties = objectPropertyRepository.findAllUncommonMandatoryAidasObjectPropertyForMetadata(object.getId(), project.getId());

                projectPropertiesCount.put(project.getId(),mandatoryProjectProperties);
                objectPropertiesCount.put(object.getId(),mandatoryObjectProperties);
                UploadMetaData aum = new UploadMetaData();
                aum.setUpload(upload);
                if(umd.getProjectProperty()!=null && umd.getProjectProperty() && umd.getProjectPropertyId()!=null){
                    ProjectProperty projectProperty = projectPropertyRepository.getById(umd.getProjectPropertyId());
                    aum.setProjectProperty(projectProperty);
                    if(projectProperty.getOptional()!=null && projectProperty.getOptional().equals(AidasConstants.AIDAS_PROPERTY_REQUIRED)){
                        if(umd.getUploadId()==null){
                            failed.add(aum);
                        }else if(umd.getValue().trim().length()==0){
                            failed.add(aum);
                        }else{
                            uploadPropertiesCount.put(upload,uploadPropertiesCount.get(upload)+1);
                            success.add(aum);
                        }
                    }else{
                        success.add(aum);
                    }
                }
                else if(umd.getProjectProperty()!=null && !umd.getProjectProperty() && umd.getObjectPropertyId()!=null){
                    ObjectProperty objectProperty = objectPropertyRepository.getById(umd.getObjectPropertyId());
                    aum.setObjectProperty(objectProperty);
                    if(objectProperty.getOptional().equals(AidasConstants.AIDAS_PROPERTY_REQUIRED)){
                        if(umd.getUploadId()==null){
                            failed.add(aum);
                        }else if(umd.getValue().trim().length()==0){
                            failed.add(aum);
                        }else{
                            uploadPropertiesCount.put(upload,uploadPropertiesCount.get(upload)+1);
                            success.add(aum);
                        }
                    }else{
                        success.add(aum);
                    }
                }
                aum.setValue(umd.getValue());
            }
            for(UploadMetaData aum:success){
                uploadMetaDataRepository.save(aum);
            }
            for (Map.Entry<Upload,Long> entry : uploadPropertiesCount.entrySet()){
                ///Project project = entry.getKey().getUserVendorMappingObjectMapping().getObject().getProject();
                //Object object = entry.getKey().getUserVendorMappingObjectMapping().getObject();
                //Long totalMandatoryProperties = projectPropertiesCount.get(project.getId()) + objectPropertiesCount.get(object.getId());
                //if(entry.getValue().equals(totalMandatoryProperties)){
                    //entry.getKey().setMetadataStatus(AidasConstants.AIDAS_UPLOAD_METADATA_COMPLETED);
                //}else{
                    entry.getKey().setMetadataStatus(AidasConstants.AIDAS_UPLOAD_METADATA_REQUIRED);
                //}
                uploadRepository.save(entry.getKey());
            }
                return ResponseEntity.ok().body(failed);
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
        if(authority.getName().equals(AidasConstants.VENDOR_USER)){
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
            UserVendorMappingObjectMapping uvmom = userVendorMappingObjectMappingRepository.findByUserObject(uploadDto.getUserId(),uploadDto.getObjectId());
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
            Long mandatoryProjectProperties = projectPropertyRepository.countProjectPropertyByProjectAndOptional(project.getId(),AidasConstants.AIDAS_PROPERTY_REQUIRED);
            Long mandatoryObjectProperties = objectPropertyRepository.countObjectProperties(object.getId(),AidasConstants.AIDAS_PROPERTY_REQUIRED);
            if(mandatoryObjectProperties==0 && mandatoryProjectProperties==0){
                upload.setMetadataStatus(AidasConstants.AIDAS_UPLOAD_METADATA_COMPLETED);
            }else {
                upload.setMetadataStatus(AidasConstants.AIDAS_UPLOAD_METADATA_REQUIRED);
            }
            upload.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_PENDING);
            upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
            try {
                Upload result = uploadRepository.save(upload);
                System.out.println("My Test::"+uploadDto.getUploadMetadata());

                for (Map.Entry < String, String> entry:uploadDto.getUploadMetadata().entrySet() ) {
                    Property property = propertyRepository.getByNameAndUserId(entry.getKey().trim(),customer.getId());
                    if(property!=null) {
                        ProjectProperty projectProperty = projectPropertyRepository.findByProjectAndProperty(project.getId(), property.getId());
                        System.out.println(projectProperty);
                        ObjectProperty objectProperty = objectPropertyRepository.findByAidasObject_IdAndAidasProperty_Id(object.getId(), property.getId());
                        System.out.println(objectProperty);
                        if(projectProperty!=null){
                            UploadMetaData uploadMetaData = new UploadMetaData();
                            uploadMetaData.setUpload(upload);
                            uploadMetaData.setProjectProperty(projectProperty);
                            uploadMetaData.setValue(entry.getValue().toString());
                            uploadMetaDataRepository.save(uploadMetaData);
                        }
                        if(objectProperty!=null){
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
            }catch(Exception e){
                e.printStackTrace();
                throw new BadRequestAlertException("Internal error occured.  Contact administrator", ENTITY_NAME, "idexists");
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
    @PostMapping("/aidas-uploads/approve/{id}")
    public ResponseEntity<Upload> approveAidasUpload(
        @PathVariable(value = "id", required = false) final Long id
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
        Customer customer = upload.getUserVendorMappingObjectMapping().getObject().getProject().getCustomer();
        Project project = upload.getUserVendorMappingObjectMapping().getObject().getProject();
        List<QcProjectMapping> qpms = qcProjectMappingRepository.getQcProjectMappingByProjectAndCustomerAndUser(project.getId(),customer.getId(),user.getId());
        upload.setStatus(AidasConstants.AIDAS_UPLOAD_APPROVED);
        upload.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_APPROVED);
        upload.setQcEndDate(Instant.now());
        if(qpms!=null && qpms.size()>0) {
            upload.setQcDoneBy(qpms.get(0));
            upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_COMPLETED);
        }
        Upload result = uploadRepository.save(upload);
        //aidasUploadSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, upload.getId().toString()))
            .body(result);
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
    @PostMapping("/aidas-uploads/reject/{id}")
    public ResponseEntity<Upload> rejectAidasUpload(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody List<UploadRejectReason> uploadRejectReasons
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
        Customer customer = upload.getUserVendorMappingObjectMapping().getObject().getProject().getCustomer();
        Project project = upload.getUserVendorMappingObjectMapping().getObject().getProject();
        List<QcProjectMapping> qpms = qcProjectMappingRepository.getQcProjectMappingByProjectAndCustomerAndUser(project.getId(),customer.getId(),user.getId());
        upload.setStatus(AidasConstants.AIDAS_UPLOAD_REJECTED);
        Upload result = uploadRepository.save(upload);
        for(UploadRejectReason uploadRejectReason : uploadRejectReasons){
            UploadRejectMapping uploadRejectMapping = new UploadRejectMapping();
            if(uploadRejectReason.getId()!=null){
                uploadRejectReason = uploadRejectReasonRepository.getById(uploadRejectReason.getId());
                uploadRejectMapping.setUpload(upload);
                uploadRejectMapping.setAidasUploadRejectReason(uploadRejectReason);
                upload.getAidasUploadRejectMappings().add(uploadRejectMapping);
            }else{
                uploadRejectReason = uploadRejectReasonRepository.save(uploadRejectReason);
                uploadRejectMapping.setUpload(upload);
                uploadRejectMapping.setAidasUploadRejectReason(uploadRejectReason);
                upload.getAidasUploadRejectMappings().add(uploadRejectMapping);
            }
        }
        upload.setQcEndDate(Instant.now());
        if(qpms!=null && qpms.size()>0) {
            upload.setQcDoneBy(qpms.get(0));
            upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_COMPLETED);
            upload.setApprovalStatus(AidasConstants.AIDAS_UPLOAD_REJECTED);
        }
        uploadRepository.save(upload);
        //aidasUploadSearchRepository.save(result);
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
            if(id!=null && type!=null && status!=null){
                if(id!=null && type.equalsIgnoreCase("o") && status.equalsIgnoreCase("a") ){
                    page= uploadRepository.findAllByUserAndObject(user.getId(),id,AidasConstants.AIDAS_UPLOAD_APPROVED,pageable);
                }
                if(id!=null && type.equalsIgnoreCase("o") && status.equalsIgnoreCase("r") ){
                    page= uploadRepository.findAllByUserAndObject(user.getId(),id,AidasConstants.AIDAS_UPLOAD_REJECTED,pageable);
                }
                if(id!=null && type.equalsIgnoreCase("o") && status.equalsIgnoreCase("p") ){
                    page= uploadRepository.findAllByUserAndObject(user.getId(),id,AidasConstants.AIDAS_UPLOAD_PENDING,pageable);
                }
                if(id!=null && type.equalsIgnoreCase("o") && status.equalsIgnoreCase("all") ){
                    page= uploadRepository.findAllByUserAndObject(user.getId(),id,pageable);
                }
                if(id!=null && type.equalsIgnoreCase("p") && status.equalsIgnoreCase("a") ){
                    page= uploadRepository.findAllByUserAndProject(user.getId(),id,AidasConstants.AIDAS_UPLOAD_APPROVED,pageable);
                }
                if(id!=null && type.equalsIgnoreCase("p") && status.equalsIgnoreCase("r") ){
                    page= uploadRepository.findAllByUserAndProject(user.getId(),id,AidasConstants.AIDAS_UPLOAD_REJECTED,pageable);
                }
                if(id!=null && type.equalsIgnoreCase("p") && status.equalsIgnoreCase("p") ){
                    page= uploadRepository.findAllByUserAndProject(user.getId(),id,AidasConstants.AIDAS_UPLOAD_PENDING,pageable);
                }
                if(id!=null && type.equalsIgnoreCase("p") && status.equalsIgnoreCase("all") ) {
                    page= uploadRepository.findAllByUserAndProject(user.getId(),id,pageable);
                }
            }
            if(id==null && type==null && status!=null){
                if(status.equalsIgnoreCase("a")){
                    page= uploadRepository.findAllByUser(user.getId(),AidasConstants.AIDAS_UPLOAD_APPROVED,pageable);
                }
                if(status.equalsIgnoreCase("r")){
                    page= uploadRepository.findAllByUser(user.getId(),AidasConstants.AIDAS_UPLOAD_REJECTED,pageable);
                }
                if(status.equalsIgnoreCase("p")){
                    page= uploadRepository.findAllByUser(user.getId(),AidasConstants.AIDAS_UPLOAD_PENDING,pageable);
                }
                if(status.equalsIgnoreCase("all")){
                    page= uploadRepository.findAllByUser(user.getId(),pageable);
                }
            }
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
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
        Authority authority = user.getAuthority();
        Project project = projectRepository.getById(projectId);
        List<UploadsMetadataDTO> uploadsMetadataDTOList = new ArrayList<>();
        //if(authority.getName().equals(AidasConstants.VENDOR_USER)){
            List<Upload> uploads = uploadRepository.findAllByUserAndProjectAllForMetadata(user.getId(),projectId);
            UploadsMetadataDTO uploadsMetadataDTO = new UploadsMetadataDTO();
            List<ProjectProperty> projectProperties = projectPropertyRepository.findAllAidasProjectPropertyForMetadata(projectId);
            for(Upload au : uploads) {
                List<UploadMetaData> uploadMetaDatas = uploadMetaDataRepository.getAllUploadMetaDataForUpload(au.getId());
                List<ObjectProperty> objectProperties = objectPropertyRepository.findAllUncommonAidasObjectPropertyForMetadata(au.getUserVendorMappingObjectMapping().getObject().getId(),projectId);
                uploadsMetadataDTO = new UploadsMetadataDTO();
                UploadDTO uploadDTO = new UploadDTO();
                uploadDTO.setUploadId(au.getId());
                uploadDTO.setObjectKey(au.getUploadUrl());
                uploadsMetadataDTO.setUploadDTO(uploadDTO);
                List<ProjectPropertyDTO> projectPropertyDTOS = new ArrayList<>();
                for(ProjectProperty p:projectProperties){
                    ProjectPropertyDTO pp = new ProjectPropertyDTO();
                    pp.setProjectPropertyId(p.getId());
                    pp.setName(p.getProperty().getName());
                    projectPropertyDTOS.add(pp);
                    for(UploadMetaData um:uploadMetaDatas){
                        if(um.getProjectProperty()!=null && um.getProjectProperty().getId().equals(p.getId())){
                            pp.setValue(um.getValue());
                        }
                    }
                }
                uploadsMetadataDTO.setProjectProperties(projectPropertyDTOS);
                List<ObjectPropertyDTO> objectPropertyDTOS = new ArrayList<>();
                for(ObjectProperty o:objectProperties){
                    ObjectPropertyDTO oo = new ObjectPropertyDTO();
                    oo.setObjectPropertyId(o.getId());
                    oo.setName(o.getProperty().getName());
                    objectPropertyDTOS.add(oo);
                    for(UploadMetaData um:uploadMetaDatas){
                        if(um.getObjectProperty()!=null && um.getObjectProperty().getId().equals(o.getId())){
                            oo.setValue(um.getValue());
                        }
                    }
                }
                uploadsMetadataDTO.setObjectProperties(objectPropertyDTOS);
                uploadsMetadataDTOList.add(uploadsMetadataDTO);
            }
            return ResponseEntity.ok().body(uploadsMetadataDTOList);
        //}
        //throw new BadRequestAlertException("VENDOR_USER only allowed", ENTITY_NAME, "notauthorised");
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
    public ResponseEntity<Upload> getNextAidasUpload(@PathVariable Long projectId) {
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
        Upload upload = uploadRepository.findTopByQcNotDoneYet(projectId);
        if(upload !=null) {
            Customer customer = upload.getUserVendorMappingObjectMapping().getObject().getProject().getCustomer();
            Project project = upload.getUserVendorMappingObjectMapping().getObject().getProject();
            List<QcProjectMapping> qpms = qcProjectMappingRepository.getQcProjectMappingByProjectAndCustomerAndUser(project.getId(),customer.getId(),user.getId());
            if(qpms!=null && qpms.size()>0) {
                upload.setQcDoneBy(qpms.get(0));
                upload.setQcStartDate(Instant.now());
                upload.setQcStatus(AidasConstants.AIDAS_UPLOAD_QC_PENDING);
            }
        }else{
            upload = new Upload();
            return ResponseEntity.ok().body(upload);
        }
        uploadRepository.save(upload);

        return ResponseEntity.ok().body(upload);
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
    TaskExecutor uploadDownloadTaskExecutor;

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
        uploadDownloadTaskExecutor.execute(downloadUploadS3);
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
