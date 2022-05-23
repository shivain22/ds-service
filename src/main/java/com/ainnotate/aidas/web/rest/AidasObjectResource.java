package com.ainnotate.aidas.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.repository.*;
import com.ainnotate.aidas.repository.search.AidasObjectSearchRepository;
import com.ainnotate.aidas.security.AidasAuthoritiesConstants;
import com.ainnotate.aidas.security.SecurityUtils;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.netflix.discovery.converters.Auto;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ainnotate.aidas.domain.AidasObject}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AidasObjectResource {

    private final Logger log = LoggerFactory.getLogger(AidasObjectResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasObject";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AidasObjectRepository aidasObjectRepository;

    @Autowired
    private AidasUploadRepository aidasUploadRepository;

    private final AidasObjectSearchRepository aidasObjectSearchRepository;

    @Autowired
    private AidasUserRepository aidasUserRepository;

    @Autowired
    private AidasPropertiesRepository aidasPropertiesRepository;

    @Autowired
    private AidasCustomerRepository aidasCustomerRepository;

    @Autowired
    private AidasObjectPropertyRepository aidasObjectPropertyRepository;

    @Autowired
    private AidasUserAidasObjectMappingRepository aidasUserAidasObjectMappingRepository;

    public AidasObjectResource(AidasObjectRepository aidasObjectRepository, AidasObjectSearchRepository aidasObjectSearchRepository) {
        this.aidasObjectRepository = aidasObjectRepository;
        this.aidasObjectSearchRepository = aidasObjectSearchRepository;
    }

    /**
     * {@code POST  /aidas-objects} : Create a new aidasObject.
     *
     * @param aidasObject the aidasObject to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasObject, or with status {@code 400 (Bad Request)} if the aidasObject has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasAuthoritiesConstants.ADMIN,AidasAuthoritiesConstants.ORG_ADMIN,AidasAuthoritiesConstants.CUSTOMER_ADMIN})
    @PostMapping("/aidas-objects")
    public ResponseEntity<AidasObject> createAidasObject(@Valid @RequestBody AidasObject aidasObject) throws URISyntaxException {
        log.debug("REST request to save AidasObject : {}", aidasObject);
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        if (aidasObject.getId() != null) {
            throw new BadRequestAlertException("A new aidasObject cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            Optional<AidasCustomer> aidasCustomer = aidasCustomerRepository.findById(aidasObject.getAidasProject().getAidasCustomer().getId());
            if(aidasCustomer.isPresent()){
                if(!aidasObject.getAidasProject().getAidasCustomer().equals(aidasCustomer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){
            if(aidasUser.getAidasCustomer()!=null && !aidasUser.getAidasCustomer().equals(aidasObject.getAidasProject().getAidasCustomer())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        List<AidasProperties> aidasProperties = aidasPropertiesRepository.findAll();
        for(AidasProperties ap:aidasProperties){
            AidasObjectProperty app = new AidasObjectProperty();
            app.setAidasObject(aidasObject);
            app.setAidasProperties(ap);
            app.setValue(ap.getValue());
            aidasObject.addAidasObjectProperty(app);
        }
        if(aidasObject.getBufferPercent()==null){
            aidasObject.setBufferPercent(20);
        }
        AidasObject result = aidasObjectRepository.save(aidasObject);

        //aidasObjectSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/aidas-objects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }




    /**
     * {@code POST  /aidas-objects/{id}} : Update aidas Object properties to default value.
     *
     * @param id the aidasObject id to update object properties to default value.
     * @return the {@link ResponseEntity} with status {@code 201 (Updated)} and with body the new aidasObject, or with status {@code 400 (Bad Request)} if the aidasObject has no ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasAuthoritiesConstants.ADMIN,AidasAuthoritiesConstants.ORG_ADMIN,AidasAuthoritiesConstants.CUSTOMER_ADMIN})
    @PostMapping("/aidas-objects/{id}")
    public ResponseEntity<AidasObject> resetObjectPropertiesToDefaultValues(@PathVariable(value = "id", required = false) final Long id) throws URISyntaxException {
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();

        log.debug("REST request to save AidasProjectProperties to default value : {}", id);
        if (id == null) {
            throw new BadRequestAlertException("A new aidasProject cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AidasObject aidasObject = aidasObjectRepository.getById(id);
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            Optional<AidasCustomer> aidasCustomer = aidasCustomerRepository.findById(aidasObject.getAidasProject().getAidasCustomer().getId());
            if(aidasCustomer.isPresent()){
                if(!aidasObject.getAidasProject().getAidasCustomer().equals(aidasCustomer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){
            if(aidasUser.getAidasCustomer()!=null && !aidasUser.getAidasCustomer().equals(aidasObject.getAidasProject().getAidasCustomer())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        List<AidasProperties> aidasProperties = aidasPropertiesRepository.findAll();
        for(AidasProperties ap:aidasProperties){
            for(AidasObjectProperty app1:aidasObject.getAidasObjectProperties()){
                if(app1.getAidasProperties().getId().equals(ap.getId())){
                    app1.setValue(ap.getValue());
                }
            }
        }
        AidasObject result = aidasObjectRepository.save(aidasObject);
        //aidasProjectSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/aidas-objects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code POST  /aidas-objects/add-all-new-added-properties/{id}} : Update aidas Object properties to default value.
     *
     * @param id the aidasObject id to add new properties to  object properties.
     * @return the {@link ResponseEntity} with status {@code 201 (Updated)} and with body the new aidasObject, or with status {@code 400 (Bad Request)} if the aidasObject has no ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasAuthoritiesConstants.ADMIN,AidasAuthoritiesConstants.ORG_ADMIN,AidasAuthoritiesConstants.CUSTOMER_ADMIN})
    @PostMapping("/aidas-objects/add-all-new-added-properties/{id}")
    public ResponseEntity<AidasObject> addAllNewlyAddedProperties(@PathVariable(value = "id", required = false) final Long id) throws URISyntaxException {
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();

        log.debug("REST request to save AidasProjectProperties to default value : {}", id);
        if (id == null) {
            throw new BadRequestAlertException("A new aidasProject cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AidasObject aidasObject = aidasObjectRepository.getById(id);
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            Optional<AidasCustomer> aidasCustomer = aidasCustomerRepository.findById(aidasObject.getAidasProject().getAidasCustomer().getId());
            if(aidasCustomer.isPresent()){
                if(!aidasObject.getAidasProject().getAidasCustomer().equals(aidasCustomer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){
            if(aidasUser.getAidasCustomer()!=null && !aidasUser.getAidasCustomer().equals(aidasObject.getAidasProject().getAidasCustomer())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        List<AidasProperties> aidasProperties = aidasPropertiesRepository.findAll();
        List<AidasProperties> addedAidasProperties = new ArrayList();
        for(AidasObjectProperty app1:aidasObject.getAidasObjectProperties()){
            addedAidasProperties.add(app1.getAidasProperties());
        }
        aidasProperties.removeAll(addedAidasProperties);
        for(AidasProperties ap:aidasProperties){
            AidasObjectProperty app = new AidasObjectProperty();
            app.setAidasObject(aidasObject);
            app.setAidasProperties(ap);
            app.setValue(ap.getValue());
            aidasObject.addAidasObjectProperty(app);
        }
        AidasObject result = aidasObjectRepository.save(aidasObject);
        //aidasProjectSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/aidas-objects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /aidas-objects/:id} : Updates an existing aidasObject.
     *
     * @param id the id of the aidasObject to save.
     * @param aidasObject the aidasObject to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasObject,
     * or with status {@code 400 (Bad Request)} if the aidasObject is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aidasObject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasAuthoritiesConstants.ADMIN,AidasAuthoritiesConstants.ORG_ADMIN,AidasAuthoritiesConstants.CUSTOMER_ADMIN})
    @PutMapping("/aidas-objects/{id}")
    public ResponseEntity<AidasObject> updateAidasObject(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AidasObject aidasObject
    ) throws URISyntaxException {
        log.debug("REST request to update AidasObject : {}, {}", id, aidasObject);
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        if (aidasObject.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasObject.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasObjectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            Optional<AidasCustomer> aidasCustomer = aidasCustomerRepository.findById(aidasObject.getAidasProject().getAidasCustomer().getId());
            if(aidasCustomer.isPresent()){
                if(!aidasObject.getAidasProject().getAidasCustomer().equals(aidasCustomer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){
            if(aidasUser.getAidasCustomer()!=null && !aidasUser.getAidasCustomer().equals(aidasObject.getAidasProject().getAidasCustomer())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }

        AidasObject existingAidasObject = aidasObjectRepository.getById(aidasObject.getId());
        existingAidasObject.setName(aidasObject.getName());
        existingAidasObject.setDescription(aidasObject.getDescription());
        existingAidasObject.setAidasProject(aidasObject.getAidasProject());
        existingAidasObject.setBufferPercent(aidasObject.getBufferPercent());
        existingAidasObject.setNumberOfUploadReqd(aidasObject.getNumberOfUploadReqd());
        AidasObject result = aidasObjectRepository.save(existingAidasObject);
        aidasObjectSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasObject.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /aidas-objects/:id} : Partial updates given fields of an existing aidasObject, field will ignore if it is null
     *
     * @param id the id of the aidasObject to save.
     * @param aidasObject the aidasObject to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasObject,
     * or with status {@code 400 (Bad Request)} if the aidasObject is not valid,
     * or with status {@code 404 (Not Found)} if the aidasObject is not found,
     * or with status {@code 500 (Internal Server Error)} if the aidasObject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-objects/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AidasObject> partialUpdateAidasObject(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AidasObject aidasObject
    ) throws URISyntaxException {
        log.debug("REST request to partial update AidasObject partially : {}, {}", id, aidasObject);
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        if (aidasObject.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasObject.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasObjectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        /*if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            Optional<AidasCustomer> aidasCustomer = aidasCustomerRepository.findById(aidasObject.getAidasProject().getAidasCustomer().getId());
            if(aidasCustomer.isPresent()){
                if(!aidasObject.getAidasProject().getAidasCustomer().equals(aidasCustomer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){
            if(aidasUser.getAidasCustomer()!=null && !aidasUser.getAidasCustomer().equals(aidasObject.getAidasProject().getAidasCustomer())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }*/
        Optional<AidasObject> result = aidasObjectRepository
            .findById(aidasObject.getId())
            .map(existingAidasObject -> {
                if (aidasObject.getName() != null) {
                    existingAidasObject.setName(aidasObject.getName());
                }
                if (aidasObject.getDescription() != null) {
                    existingAidasObject.setDescription(aidasObject.getDescription());
                }
                if (aidasObject.getNumberOfUploadReqd() != null) {
                    existingAidasObject.setNumberOfUploadReqd(aidasObject.getNumberOfUploadReqd());
                }

                return existingAidasObject;
            })
            .map(aidasObjectRepository::save)
            .map(savedAidasObject -> {
                aidasObjectSearchRepository.save(savedAidasObject);

                return savedAidasObject;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasObject.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-objects} : get all the aidasObjects.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasObjects in body.
     */

    @GetMapping("/aidas-objects")
    public ResponseEntity<List<AidasObject>> getAllAidasObjects(Pageable pageable) {
        log.debug("REST request to get a page of AidasObjects");
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Page<AidasObject> page =null;
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ADMIN)){
            page = aidasObjectRepository.findAllByIdGreaterThan(0l,pageable);
        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            page = aidasObjectRepository.findAllByAidasProject_AidasCustomer_AidasOrganisation(pageable,aidasUser.getAidasOrganisation());
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN) && aidasUser.getAidasCustomer()!=null ){
            page = aidasObjectRepository.findAllByAidasProject_AidasCustomer(pageable,aidasUser.getAidasCustomer());
        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN) || aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){
            page = aidasObjectRepository.findAllObjectsByVendorAdmin(pageable,aidasUser.getAidasVendor());
        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.USER)){
            page = aidasObjectRepository.findAllObjectsByVendorUser(pageable,aidasUser);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/aidas-projects/{id}/aidas-objects")
    public ResponseEntity<List<AidasObject>> getAllAidasObjectsOfProject(Pageable pageable,@PathVariable(value = "id", required = false) final Long projectId) {
        log.debug("REST request to get a page of AidasObjects");
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Page<AidasObject> page =null;
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ADMIN)){
            page = aidasObjectRepository.findAllByIdGreaterThanAndAidasProject_Id(0l,projectId,pageable);
            if(page!=null && page.getContent()!=null && page.getContent().size()>0){
                for(AidasObject aidasObject: page.getContent()){
                    Integer uploadsCompleted = aidasUploadRepository.countAidasUploadByAidasUserAidasObjectMapping_AidasObject(aidasObject);
                    aidasObject.setUploadsCompleted(uploadsCompleted);
                    Integer uploadsRemaining = (aidasObject.getNumberOfUploadReqd()+((aidasObject.getNumberOfUploadReqd()*(aidasObject.getBufferPercent())/100))-uploadsCompleted);
                    aidasObject.setUploadsRemaining(uploadsRemaining);
                    UploadDetail ud = aidasObjectRepository.countUploadsByObjectAndAidasAdmin(aidasObject.getId());
                    aidasObject.setTotalUploaded(ud.getTotalUploaded());
                    aidasObject.setTotalApproved(ud.getTotalApproved());
                    aidasObject.setTotalRejected(ud.getTotalRejected());
                    aidasObject.setTotalPending(ud.getTotalPending());
                }
            }
        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            page = aidasObjectRepository.findAllByAidasProject_AidasCustomer_AidasOrganisationAndAidasProject_Id(pageable,aidasUser.getAidasOrganisation(),projectId);
            if(page!=null && page.getContent()!=null && page.getContent().size()>0){
                for(AidasObject aidasObject: page.getContent()){
                    Integer uploadsCompleted = aidasUploadRepository.countAidasUploadByAidasUserAidasObjectMapping_AidasObject(aidasObject);
                    aidasObject.setUploadsCompleted(uploadsCompleted);
                    Integer uploadsRemaining = (aidasObject.getNumberOfUploadReqd()+((aidasObject.getNumberOfUploadReqd()*(aidasObject.getBufferPercent())/100))-uploadsCompleted);
                    aidasObject.setUploadsRemaining(uploadsRemaining);
                    UploadDetail ud = aidasObjectRepository.countUploadsByObjectAndAidasOrganisation(aidasUser.getAidasOrganisation().getId(),aidasObject.getId());
                    aidasObject.setTotalUploaded(ud.getTotalUploaded());
                    aidasObject.setTotalApproved(ud.getTotalApproved());
                    aidasObject.setTotalRejected(ud.getTotalRejected());
                    aidasObject.setTotalPending(ud.getTotalPending());
                }
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN) && aidasUser.getAidasCustomer()!=null ){
            page = aidasObjectRepository.findAllByAidasProject_AidasCustomerAndAidasProject_Id(pageable,aidasUser.getAidasCustomer(),projectId);
            if(page!=null && page.getContent()!=null && page.getContent().size()>0){
                for(AidasObject aidasObject: page.getContent()){
                    Integer uploadsCompleted = aidasUploadRepository.countAidasUploadByAidasUserAidasObjectMapping_AidasObject(aidasObject);
                    aidasObject.setUploadsCompleted(uploadsCompleted);
                    Integer uploadsRemaining = (aidasObject.getNumberOfUploadReqd()+((aidasObject.getNumberOfUploadReqd()*(aidasObject.getBufferPercent())/100))-uploadsCompleted);
                    aidasObject.setUploadsRemaining(uploadsRemaining);
                    UploadDetail ud = aidasObjectRepository.countUploadsByObjectAndAidasCustomer(aidasUser.getAidasCustomer().getId(),aidasObject.getId());
                    aidasObject.setTotalUploaded(ud.getTotalUploaded());
                    aidasObject.setTotalApproved(ud.getTotalApproved());
                    aidasObject.setTotalRejected(ud.getTotalRejected());
                    aidasObject.setTotalPending(ud.getTotalPending());
                }
            }
        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN) ){
            page = aidasObjectRepository.findAllObjectsByVendorAdminProject(pageable,aidasUser.getAidasVendor(),projectId);
            if(page!=null && page.getContent()!=null && page.getContent().size()>0){
                for(AidasObject aidasObject: page.getContent()){
                    Integer uploadsCompleted = aidasUploadRepository.countAidasUploadByAidasUserAidasObjectMapping_AidasObject(aidasObject);
                    aidasObject.setUploadsCompleted(uploadsCompleted);
                    Integer uploadsRemaining = (aidasObject.getNumberOfUploadReqd()+((aidasObject.getNumberOfUploadReqd()*(aidasObject.getBufferPercent())/100))-uploadsCompleted);
                    aidasObject.setUploadsRemaining(uploadsRemaining);
                    UploadDetail ud = aidasObjectRepository.countUploadsByObjectAndAidasVendor(aidasUser.getAidasVendor().getId(),aidasObject.getId());
                    aidasObject.setTotalUploaded(ud.getTotalUploaded());
                    aidasObject.setTotalApproved(ud.getTotalApproved());
                    aidasObject.setTotalRejected(ud.getTotalRejected());
                    aidasObject.setTotalPending(ud.getTotalPending());
                }
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){
            page = aidasObjectRepository.findAllObjectsByVendorAdminProject(pageable,aidasUser.getAidasVendor(),projectId);
            if(page!=null && page.getContent()!=null && page.getContent().size()>0){
                for(AidasObject aidasObject: page.getContent()){
                    Integer uploadsCompleted = aidasUploadRepository.countAidasUploadByAidasUserAidasObjectMapping_AidasObject(aidasObject);
                    aidasObject.setUploadsCompleted(uploadsCompleted);
                    Integer uploadsRemaining = (aidasObject.getNumberOfUploadReqd()+((aidasObject.getNumberOfUploadReqd()*(aidasObject.getBufferPercent())/100))-uploadsCompleted);
                    aidasObject.setUploadsRemaining(uploadsRemaining);
                    UploadDetail ud = aidasObjectRepository.countUploadsByObjectAndUser(aidasUser.getId(),aidasObject.getId());
                    aidasObject.setTotalUploaded(ud.getTotalUploaded());
                    aidasObject.setTotalApproved(ud.getTotalApproved());
                    aidasObject.setTotalRejected(ud.getTotalRejected());
                    aidasObject.setTotalPending(ud.getTotalPending());
                }
            }
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-objects/:id} : get the "id" aidasObject.
     *
     * @param id the id of the aidasObject to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasObject, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-objects/{id}")
    public ResponseEntity<AidasObject> getAidasObject(@PathVariable Long id) {
        log.debug("REST request to get AidasObject : {}", id);
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Optional<AidasObject> aidasObject = aidasObjectRepository.findById(id);
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ADMIN) ){
            return ResponseUtil.wrapOrNotFound(aidasObject);
        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            if(aidasObject.isPresent()){
                if(aidasObject.get().getAidasProject().getAidasCustomer().getAidasOrganisation().equals(aidasUser.getAidasOrganisation())){
                    return ResponseUtil.wrapOrNotFound(aidasObject);
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN) && aidasUser.getAidasCustomer()!=null ){
            if(aidasObject.isPresent()){
                if(aidasObject.get().getAidasProject().getAidasCustomer().equals(aidasUser.getAidasCustomer())){
                    return ResponseUtil.wrapOrNotFound(aidasObject);
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN)){
            Integer count = aidasUserAidasObjectMappingRepository.getCountOfAidasObjectMappingForVendorAdmin(aidasUser.getAidasVendor().getId(),id);
            if(count>0){
                if(aidasObject.isPresent()){
                    return ResponseUtil.wrapOrNotFound(aidasObject);
                }else{
                    throw new BadRequestAlertException("The object is not assigned to this vendor admin", ENTITY_NAME, "idexists");
                }
            }
            throw new BadRequestAlertException("The object is not assigned to this vendor admin", ENTITY_NAME, "idexists");
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){
            AidasUserAidasObjectMapping auao =  aidasUserAidasObjectMappingRepository.findByAidasUser_IdAndAidasObject_Id(aidasUser.getId(),id);
            if(auao!=null){
                if(aidasObject.isPresent()){
                        return ResponseUtil.wrapOrNotFound(aidasObject);
                }else{
                    throw new BadRequestAlertException("The object is not assigned to this vendor user", ENTITY_NAME, "idexists");
                }
            }
            throw new BadRequestAlertException("The object is not assigned to this vendor user", ENTITY_NAME, "idexists");
        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.USER)){
            throw new BadRequestAlertException("The object is not assigned to user", ENTITY_NAME, "idexists");
        }
        throw new BadRequestAlertException("The object is not assigned  user", ENTITY_NAME, "idexists");

    }


    /**
     * {@code GET  /aidas-objects/:id} : get the "id" aidasObject.
     *
     * @param objectId the id of the aidasObject to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasObject, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-objects/assinged-users/{objectId}")
    public ResponseEntity<List<AidasUser>> getAssignedAidasUser(@PathVariable Long objectId) {
        log.debug("REST request to get assigned users for AidasObject : {}", objectId);
        List<AidasUser> assignedAidasUsers = aidasUserRepository.getUsersByAssignedToObject(objectId);
        return ResponseEntity.ok().body(assignedAidasUsers);
    }

    /**
     * {@code GET  /aidas-objects/:id} : get the "id" aidasObject.
     *
     * @param objectId the id of the aidasObject to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasObject, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-objects/unassinged-users/{objectId}")
    public ResponseEntity<List<AidasUser>> getUnAssignedAidasUser(@PathVariable Long objectId) {
        log.debug("REST request to get unassigned users for AidasObject : {}", objectId);
        List<AidasUser> unAssignedAidasUsers = aidasUserRepository.getUsersByNotAssignedToObject(objectId);
        return ResponseEntity.ok().body(unAssignedAidasUsers);
    }


    /**
     * {@code DELETE  /aidas-objects/:id} : delete the "id" aidasObject.
     *
     * @param id the id of the aidasObject to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @Secured({AidasAuthoritiesConstants.ADMIN,AidasAuthoritiesConstants.ORG_ADMIN,AidasAuthoritiesConstants.CUSTOMER_ADMIN})
    @DeleteMapping("/aidas-objects/{id}")
    public ResponseEntity<Void> deleteAidasObject(@PathVariable Long id) {
        log.debug("REST request to delete AidasObject : {}", id);
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        AidasObject aidasObject = aidasObjectRepository.getById(id);
        aidasObjectRepository.deleteById(id);
        aidasObjectSearchRepository.deleteById(id);
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            Optional<AidasCustomer> aidasCustomer = aidasCustomerRepository.findById(aidasObject.getAidasProject().getAidasCustomer().getId());
            if(aidasCustomer.isPresent()){
                if(!aidasObject.getAidasProject().getAidasCustomer().equals(aidasCustomer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){
            if(aidasUser.getAidasCustomer()!=null && !aidasUser.getAidasCustomer().equals(aidasObject.getAidasProject().getAidasCustomer())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-objects?query=:query} : search for the aidasObject corresponding
     * to the query.
     *
     * @param query the query of the aidasObject search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/aidas-objects")
    public ResponseEntity<List<AidasObject>> searchAidasObjects(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasObjects for query {}", query);
        Page<AidasObject> page = aidasObjectSearchRepository.search(query, pageable);
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null) {
            Iterator<AidasObject> it = page.getContent().iterator();
            while(it.hasNext()){
                AidasObject aidasObject = it.next();
                if(!aidasObject.getAidasProject().getAidasCustomer().getAidasOrganisation().equals(aidasUser.getAidasOrganisation())){
                    it.remove();
                }
            }
        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN) && aidasUser.getAidasCustomer()!=null) {
            Iterator<AidasObject> it = page.getContent().iterator();
            while(it.hasNext()){
                AidasObject aidasObject = it.next();
                if(!aidasObject.getAidasProject().getAidasCustomer().equals(aidasUser.getAidasCustomer())){
                    it.remove();
                }
            }
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @Autowired
    TaskExecutor uploadDownloadTaskExecutor;

    @Autowired
    DownloadUploadS3 downloadUploadS3;
    /**
     * {@code GET  /download/:id/:status} : download objects with the "id" aidasObject and provided status.  User "all" for download both.
     *
     * @param id the id of the aidasObject to retrieve.
     * @param status the id of the upload objects to retrieve and download.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasObject, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/download/object/{id}/{status}")
    public void downloadUploadedObjectsOfObject(@PathVariable("id") Long id,@PathVariable("status") String status){
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        downloadUploadS3.setAidasUser(aidasUser);
        AidasObject aidasObject = aidasObjectRepository.getById(id);
        downloadUploadS3.setUp(aidasObject,status);
        uploadDownloadTaskExecutor.execute(downloadUploadS3);
    }
}
