package com.ainnotate.aidas.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.dto.UploadByUserObjectMappingDto;
import com.ainnotate.aidas.dto.UploadDto;
import com.ainnotate.aidas.repository.*;
import com.ainnotate.aidas.repository.search.AidasUploadSearchRepository;
import com.ainnotate.aidas.security.AidasAuthoritiesConstants;
import com.ainnotate.aidas.security.SecurityUtils;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link AidasUpload}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AidasUploadResource {

    private final Logger log = LoggerFactory.getLogger(AidasUploadResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasUpload";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Autowired
    private AidasUserRepository aidasUserRepository;
    @Autowired
    private AidasObjectRepository aidasObjectRepository;
    @Autowired
    private AidasUserAidasObjectMappingRepository aidasUserAidasObjectMappingRepository;

    @Autowired
    private AidasProjectPropertyRepository aidasProjectPropertyRepository;

    private final AidasUploadRepository aidasUploadRepository;

    private final AidasUploadSearchRepository aidasUploadSearchRepository;

    @Autowired
    private AidasUploadMetaDataRepository aidasUploadMetaDataRepository;

    public AidasUploadResource(AidasUploadRepository aidasUploadRepository, AidasUploadSearchRepository aidasUploadSearchRepository) {
        this.aidasUploadRepository = aidasUploadRepository;
        this.aidasUploadSearchRepository = aidasUploadSearchRepository;
    }

    /**
     * {@code POST  /aidas-uploads} : Create a new aidasUpload.
     *
     * @param aidasUpload the aidasUpload to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasUpload, or with status {@code 400 (Bad Request)} if the aidasUpload has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-uploads")
    public ResponseEntity<AidasUpload> createAidasUpload(@Valid @RequestBody AidasUpload aidasUpload) throws URISyntaxException {
        log.debug("REST request to save AidasUpload : {}", aidasUpload);
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        AidasAuthority aidasAuthority = aidasUser.getCurrentAidasAuthority();
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ORG_ADMIN)){
            throw new BadRequestAlertException("You can not upload as ORG ADMIN", ENTITY_NAME, "idexists");
        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){
            throw new BadRequestAlertException("You can not upload as CUSTOMER ADMIN", ENTITY_NAME, "idexists");
        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN)){
            throw new BadRequestAlertException("You can not upload as VENDOR ADMIN", ENTITY_NAME, "idexists");
        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){
            if (aidasUpload.getId() != null) {
                throw new BadRequestAlertException("A new aidasUpload cannot already have an ID", ENTITY_NAME, "idexists");
            }
            aidasUpload.setStatus(0);
            AidasUpload result = aidasUploadRepository.save(aidasUpload);
            aidasUploadSearchRepository.save(result);
            return ResponseEntity
                .created(new URI("/api/aidas-uploads/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                .body(result);
        }
        throw new BadRequestAlertException("You can not upload as there was an error internally", ENTITY_NAME, "idexists");
    }


    /**
     * {@code POST  /aidas-uploads} : Create a new aidasUpload.
     *
     * @param uploadMetadataDTOS the list of aidasUploadMetadata to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasUpload, or with status {@code 400 (Bad Request)} if the aidasUpload has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-uploads/metadata")
    public ResponseEntity<List<AidasUploadMetaData>> submitMetadata(@Valid @RequestBody List<UploadMetadataDTO> uploadMetadataDTOS) throws URISyntaxException {
        log.debug("REST request to save AidasUpload : {}", uploadMetadataDTOS);
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        AidasAuthority aidasAuthority = aidasUser.getCurrentAidasAuthority();
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){
            List<AidasUploadMetaData> success = new ArrayList<>();
            List<AidasUploadMetaData> failed = new ArrayList<>();
            for(UploadMetadataDTO umd:uploadMetadataDTOS){
                AidasUpload aidasUpload = aidasUploadRepository.getById(umd.getUploadId());
                AidasProjectProperty aidasProjectProperty = aidasProjectPropertyRepository.getById(umd.getProjectPropertyId());
                AidasUploadMetaData aum = new AidasUploadMetaData();
                aum.setAidasUpload(aidasUpload);
                aum.setAidasProjectProperty(aidasProjectProperty);
                aum.setValue(umd.getValue());
                if(!aidasProjectProperty.getAidasProperties().getOptional()){
                    if(umd.getUploadId()==null){
                        failed.add(aum);
                    }else if(umd.getValue().trim().length()==0){
                        failed.add(aum);
                    }else{
                        success.add(aum);
                    }
                }else{
                    success.add(aum);
                }
            }
            for(AidasUploadMetaData aum:success){
                aidasUploadMetaDataRepository.save(aum);
                AidasUpload aidasUpload = aum.getAidasUpload();
                aidasUpload.setStatus(3);
                aidasUploadRepository.save(aidasUpload);
            }
            return ResponseEntity.ok().body(failed);
        }
        throw new BadRequestAlertException("You can not upload as there was an error internally", ENTITY_NAME, "idexists");
    }

    /**
     * {@code POST  /aidas-uploads} : Create a new aidasUpload.
     *
     * @param uploadIds the list of aidasUploadMetadata to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasUpload, or with status {@code 400 (Bad Request)} if the aidasUpload has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-uploads/qc")
    public ResponseEntity<String> submitForQc(@Valid @RequestBody List<Long> uploadIds) throws URISyntaxException {
        log.debug("REST request to save AidasUpload : {}", uploadIds);
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        AidasAuthority aidasAuthority = aidasUser.getCurrentAidasAuthority();
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){
            for(Long upId:uploadIds){
                AidasUpload aidasUpload = aidasUploadRepository.getById(upId);
                if(aidasUpload.getStatus()==3) {
                    aidasUpload.setStatus(4);
                }
                aidasUploadRepository.save(aidasUpload);
            }
            return ResponseEntity.ok().body("Success");
        }
        throw new BadRequestAlertException("You can not upload as there was an error internally", ENTITY_NAME, "idexists");
    }

    /**
     * {@code POST  /aidas-uploads/dto} : Create a new aidasUpload.
     *
     * @param uploadDto the aidasUpload to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasUpload, or with status {@code 400 (Bad Request)} if the aidasUpload has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-uploads/dto")
    public ResponseEntity<AidasUpload> createAidasUploadFromDto(@Valid @RequestBody UploadDto uploadDto) throws URISyntaxException {
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to save AidasUpload : {}", uploadDto);
        AidasAuthority aidasAuthority = aidasUser.getCurrentAidasAuthority();
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ORG_ADMIN)){
            throw new BadRequestAlertException("You can not upload as ORG ADMIN", ENTITY_NAME, "idexists");
        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){
            throw new BadRequestAlertException("You can not upload as CUSTOMER ADMIN", ENTITY_NAME, "idexists");
        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN)){
            throw new BadRequestAlertException("You can not upload as VENDOR ADMIN", ENTITY_NAME, "idexists");
        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){
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

            AidasUpload aidasUpload = new AidasUpload();
            AidasUserAidasObjectMapping auaom = aidasUserAidasObjectMappingRepository.findByAidasUser_IdAndAidasObject_Id(uploadDto.getUserId(),uploadDto.getObjectId());
            aidasUpload.setAidasUserAidasObjectMapping(auaom);
            aidasUpload.setDateUploaded(Instant.now());
            aidasUpload.setName(uploadDto.getUploadUrl());
            aidasUpload.setUploadUrl(uploadDto.getUploadUrl());
            aidasUpload.setUploadEtag(uploadDto.getEtag());

            aidasUpload.setStatus(2);
            aidasUpload.setObjectKey(uploadDto.getObjectKey());
            try {
                AidasUpload result = aidasUploadRepository.save(aidasUpload);
                //aidasUploadSearchRepository.save(result);
                return ResponseEntity
                    .created(new URI("/api/aidas-uploads/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            }catch(Exception e){
                e.printStackTrace();
                throw new BadRequestAlertException("Internal error occured.  Contact administrator "+e.getMessage(), ENTITY_NAME, "idexists");
            }
        }
        throw new BadRequestAlertException("Internal error occured.  Contact administrator", ENTITY_NAME, "idexists");
    }

    /**
     * {@code POST  /aidas-uploads/dto} : Create a new aidasUpload.
     *
     * @param uploadByUserObjectMappingDto the aidasUpload to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasUpload, or with status {@code 400 (Bad Request)} if the aidasUpload has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-uploads/user-object-mapping-dto")
    public ResponseEntity<AidasUpload> createAidasUploadFromUseObjectMappingDto(@Valid @RequestBody UploadByUserObjectMappingDto uploadByUserObjectMappingDto) throws URISyntaxException {
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to save AidasUpload : {}", uploadByUserObjectMappingDto);
        AidasAuthority aidasAuthority = aidasUser.getCurrentAidasAuthority();
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ORG_ADMIN)){
            throw new BadRequestAlertException("You can not upload as ORG ADMIN", ENTITY_NAME, "idexists");
        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){
            throw new BadRequestAlertException("You can not upload as CUSTOMER ADMIN", ENTITY_NAME, "idexists");
        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN)){
            throw new BadRequestAlertException("You can not upload as VENDOR ADMIN", ENTITY_NAME, "idexists");
        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){
            if (uploadByUserObjectMappingDto.getUserObjectMappingId() == null) {
                throw new BadRequestAlertException("No User Id", ENTITY_NAME, "idexists");
            }
            if (uploadByUserObjectMappingDto.getUploadUrl() == null) {
                throw new BadRequestAlertException("No Upload URL Id", ENTITY_NAME, "idexists");
            }
            if (uploadByUserObjectMappingDto.getEtag() == null) {
                throw new BadRequestAlertException("No Etag Id", ENTITY_NAME, "idexists");
            }

            AidasUpload aidasUpload = new AidasUpload();
            AidasUserAidasObjectMapping auaom = aidasUserAidasObjectMappingRepository.getById(uploadByUserObjectMappingDto.getUserObjectMappingId());
            aidasUpload.setAidasUserAidasObjectMapping(auaom);
            aidasUpload.setDateUploaded(Instant.now());
            aidasUpload.setName(uploadByUserObjectMappingDto.getUploadUrl());
            aidasUpload.setUploadUrl(uploadByUserObjectMappingDto.getUploadUrl());
            aidasUpload.setUploadEtag(uploadByUserObjectMappingDto.getEtag());
            aidasUpload.setObjectKey(uploadByUserObjectMappingDto.getObjectKey());
            aidasUpload.setStatus(2);
            AidasUpload result = aidasUploadRepository.save(aidasUpload);
            //aidasUploadSearchRepository.save(result);
            return ResponseEntity
                .created(new URI("/api/aidas-uploads/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                .body(result);
        }
        throw new BadRequestAlertException("Internal error occured.  Contact administrator", ENTITY_NAME, "idexists");
    }


    /**
     * {@code PUT  /aidas-uploads/:id} : Updates an existing aidasUpload.
     *
     * @param id the id of the aidasUpload to save.
     * @param aidasUpload the aidasUpload to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasUpload,
     * or with status {@code 400 (Bad Request)} if the aidasUpload is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aidasUpload couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-uploads/{id}")
    public ResponseEntity<AidasUpload> updateAidasUpload(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AidasUpload aidasUpload
    ) throws URISyntaxException {
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to update AidasUpload : {}, {}", id, aidasUpload);
        AidasAuthority aidasAuthority = aidasUser.getCurrentAidasAuthority();
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ORG_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){

        }
        if (aidasUpload.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasUpload.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasUploadRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AidasUpload result = aidasUploadRepository.save(aidasUpload);
        aidasUploadSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasUpload.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /aidas-uploads/:id} : Approve an existing aidasUpload.
     *
     * @param id the id of the aidasUpload to save.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasUpload,
     * or with status {@code 400 (Bad Request)} if the aidasUpload is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aidasUpload couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-uploads/approve/{id}")
    public ResponseEntity<AidasUpload> approveAidasUpload(
        @PathVariable(value = "id", required = false) final Long id
    ) throws URISyntaxException {
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to approve AidasUpload : {}, {}", id);
        AidasAuthority aidasAuthority = aidasUser.getCurrentAidasAuthority();
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ORG_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){

        }
        if (!aidasUploadRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        AidasUpload aidasUpload = aidasUploadRepository.getById(id);
        aidasUpload.setStatus(1);
        AidasUpload result = aidasUploadRepository.save(aidasUpload);
        //aidasUploadSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasUpload.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /aidas-uploads/:id} : Reject an existing aidasUpload.
     *
     * @param id the id of the aidasUpload to save.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasUpload,
     * or with status {@code 400 (Bad Request)} if the aidasUpload is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aidasUpload couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-uploads/reject/{id}")
    public ResponseEntity<AidasUpload> rejectAidasUpload(
        @PathVariable(value = "id", required = false) final Long id
    ) throws URISyntaxException {
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to reject AidasUpload : {}, {}", id);
        AidasAuthority aidasAuthority = aidasUser.getCurrentAidasAuthority();
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ORG_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){

        }
        if (!aidasUploadRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        AidasUpload aidasUpload = aidasUploadRepository.getById(id);
        aidasUpload.setStatus(0);
        AidasUpload result = aidasUploadRepository.save(aidasUpload);
        //aidasUploadSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasUpload.getId().toString()))
            .body(result);
    }
    /**
     * {@code PATCH  /aidas-uploads/:id} : Partial updates given fields of an existing aidasUpload, field will ignore if it is null
     *
     * @param id the id of the aidasUpload to save.
     * @param aidasUpload the aidasUpload to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasUpload,
     * or with status {@code 400 (Bad Request)} if the aidasUpload is not valid,
     * or with status {@code 404 (Not Found)} if the aidasUpload is not found,
     * or with status {@code 500 (Internal Server Error)} if the aidasUpload couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-uploads/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AidasUpload> partialUpdateAidasUpload(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AidasUpload aidasUpload
    ) throws URISyntaxException {
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to partial update AidasUpload partially : {}, {}", id, aidasUpload);
        AidasAuthority aidasAuthority = aidasUser.getCurrentAidasAuthority();
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ORG_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){

        }
        if (aidasUpload.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasUpload.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasUploadRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AidasUpload> result = aidasUploadRepository
            .findById(aidasUpload.getId())
            .map(existingAidasUpload -> {
                if (aidasUpload.getName() != null) {
                    existingAidasUpload.setName(aidasUpload.getName());
                }
                if (aidasUpload.getDateUploaded() != null) {
                    existingAidasUpload.setDateUploaded(aidasUpload.getDateUploaded());
                }
                if (aidasUpload.getStatus() != null) {
                    existingAidasUpload.setStatus(aidasUpload.getStatus());
                }
                if (aidasUpload.getStatusModifiedDate() != null) {
                    existingAidasUpload.setStatusModifiedDate(aidasUpload.getStatusModifiedDate());
                }


                return existingAidasUpload;
            })
            .map(aidasUploadRepository::save)
            .map(savedAidasUpload -> {
                aidasUploadSearchRepository.save(savedAidasUpload);

                return savedAidasUpload;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasUpload.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-uploads} : get all the aidasUploads.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasUploads in body.
     */
    @GetMapping("/aidas-uploads")
    public ResponseEntity<List<AidasUpload>> getAllAidasUploads(Pageable pageable) {
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to get a page of AidasUploads");
        Page<AidasUpload> page = aidasUploadRepository.findAll(pageable);
        AidasAuthority aidasAuthority = aidasUser.getCurrentAidasAuthority();
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ADMIN)){
            page = aidasUploadRepository.findAll(pageable);
        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ORG_ADMIN)){
            page = aidasUploadRepository.findAidasUploadByAidasOrganisation(aidasUser.getAidasOrganisation().getId(),pageable);
        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){
            page = aidasUploadRepository.findAidasUploadByAidasCustomer(aidasUser.getAidasCustomer().getId(),pageable);
        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN)){
            page = aidasUploadRepository.findAidasUploadByAidasVendor(aidasUser.getAidasVendor().getId(),pageable);
        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){
            page = aidasUploadRepository.findAllByUserAll(aidasUser.getId(),pageable);
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
    public ResponseEntity<List<AidasUpload>> getAllAidasUploads(Pageable pageable,@PathVariable Long id,@PathVariable String type,@PathVariable String status) {
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to get a page of AidasUploads");
        Page<AidasUpload> page = aidasUploadRepository.findAll(pageable);
        AidasAuthority aidasAuthority = aidasUser.getCurrentAidasAuthority();
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ADMIN)){
            page = aidasUploadRepository.findAll(pageable);
        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ORG_ADMIN)){
            page = aidasUploadRepository.findAidasUploadByAidasOrganisation(aidasUser.getAidasOrganisation().getId(),pageable);
        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){
            page = aidasUploadRepository.findAidasUploadByAidasCustomer(aidasUser.getAidasCustomer().getId(),pageable);
        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN)){
            page = aidasUploadRepository.findAidasUploadByAidasVendor(aidasUser.getAidasVendor().getId(),pageable);
        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){
            if(id!=null && type!=null && status!=null){
                if(id!=null && type.equalsIgnoreCase("o") && status.equalsIgnoreCase("a") ){
                    page= aidasUploadRepository.findAllByUserAndObjectApproved(aidasUser.getId(),id,pageable);
                }
                if(id!=null && type.equalsIgnoreCase("o") && status.equalsIgnoreCase("r") ){
                    page= aidasUploadRepository.findAllByUserAndObjectRejected(aidasUser.getId(),id,pageable);
                }
                if(id!=null && type.equalsIgnoreCase("o") && status.equalsIgnoreCase("p") ){
                    page= aidasUploadRepository.findAllByUserAndObjectPending(aidasUser.getId(),id,pageable);
                }
                if(id!=null && type.equalsIgnoreCase("o") && status.equalsIgnoreCase("all") ){
                    page= aidasUploadRepository.findAllByUserAndObjectAll(aidasUser.getId(),id,pageable);
                }
                if(id!=null && type.equalsIgnoreCase("p") && status.equalsIgnoreCase("a") ){
                    page= aidasUploadRepository.findAllByUserAndProjectApproved(aidasUser.getId(),id,pageable);
                }
                if(id!=null && type.equalsIgnoreCase("p") && status.equalsIgnoreCase("r") ){
                    page= aidasUploadRepository.findAllByUserAndProjectRejected(aidasUser.getId(),id,pageable);
                }
                if(id!=null && type.equalsIgnoreCase("p") && status.equalsIgnoreCase("p") ){
                    page= aidasUploadRepository.findAllByUserAndProjectPending(aidasUser.getId(),id,pageable);
                }
                if(id!=null && type.equalsIgnoreCase("p") && status.equalsIgnoreCase("all") ) {
                    page= aidasUploadRepository.findAllByUserAndProjectAll(aidasUser.getId(),id,pageable);
                }
            }
            if(id==null && type==null && status!=null){
                if(status.equalsIgnoreCase("a")){
                    page= aidasUploadRepository.findAllByUserApproved(aidasUser.getId(),pageable);
                }
                if(status.equalsIgnoreCase("r")){
                    page= aidasUploadRepository.findAllByUserRejected(aidasUser.getId(),pageable);
                }
                if(status.equalsIgnoreCase("p")){
                    page= aidasUploadRepository.findAllByUserPending(aidasUser.getId(),pageable);
                }
                if(status.equalsIgnoreCase("all")){
                    page= aidasUploadRepository.findAllByUserAll(aidasUser.getId(),pageable);
                }
            }
            //page = aidasUploadRepository.findAllByUserAll(aidasUser.getId(),pageable);

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
    public ResponseEntity<UploadsMetadata> getAllAidasUploadsForMetadata(@PathVariable Long projectId) {
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        AidasAuthority aidasAuthority = aidasUser.getCurrentAidasAuthority();
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){
            List<AidasUpload> aidasUploads= aidasUploadRepository.findAllByUserAndProjectAllForMetadata(aidasUser.getId(),projectId);
            UploadsMetadata uploadsMetadata = new UploadsMetadata();
            uploadsMetadata.setAidasUploads(aidasUploads);
            List<AidasProjectProperty> aidasProjectProperties = aidasProjectPropertyRepository.findAllAidasProjectPropertyForMetadata(projectId);
            uploadsMetadata.setAidasProjectProperties(aidasProjectProperties);
            return ResponseEntity.ok().body(uploadsMetadata);
        }
        throw new BadRequestAlertException("VENDOR_USER only allowed", ENTITY_NAME, "notauthorised");
    }


    /**
     * {@code GET  /aidas-uploads/:id} : get the "id" aidasUpload.
     *
     * @param id the id of the aidasUpload to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasUpload, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-uploads/{id}")
    public ResponseEntity<AidasUpload> getAidasUpload(@PathVariable Long id) {
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to get AidasUpload : {}", id);
        AidasAuthority aidasAuthority = aidasUser.getCurrentAidasAuthority();
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ORG_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){

        }
        Optional<AidasUpload> aidasUpload = aidasUploadRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(aidasUpload);
    }

    /**
     * {@code GET  /aidas-uploads/:id} : get the "id" aidasUpload.
     *
     * @param id the id of the aidasUpload to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasUpload, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-uploads/next")
    public ResponseEntity<AidasUpload> getNextAidasUpload(@PathVariable Long id) {
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to get AidasUpload : {}", id);
        AidasAuthority aidasAuthority = aidasUser.getCurrentAidasAuthority();
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ORG_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){

        }
        AidasUpload aidasUpload = aidasUploadRepository.findTopByQcNotDoneYet();
        aidasUpload.setQcDoneBy(aidasUser.getId());
        aidasUpload.setQcStartDate(Instant.now().getEpochSecond());
        aidasUpload.setQcStatus(2);

        return ResponseEntity.ok().body(aidasUpload);
    }



    /**
     * {@code DELETE  /aidas-uploads/:id} : delete the "id" aidasUpload.
     *
     * @param id the id of the aidasUpload to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aidas-uploads/{id}")
    public ResponseEntity<Void> deleteAidasUpload(@PathVariable Long id) {
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to delete AidasUpload : {}", id);
        AidasAuthority aidasAuthority = aidasUser.getCurrentAidasAuthority();
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ORG_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){

        }
        aidasUploadRepository.deleteById(id);
        aidasUploadSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-uploads?query=:query} : search for the aidasUpload corresponding
     * to the query.
     *
     * @param query the query of the aidasUpload search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/aidas-uploads")
    public ResponseEntity<List<AidasUpload>> searchAidasUploads(@RequestParam String query, Pageable pageable) {
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to search for a page of AidasUploads for query {}", query);
        AidasAuthority aidasAuthority = aidasUser.getCurrentAidasAuthority();
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ORG_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN)){

        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){

        }
        Page<AidasUpload> page = aidasUploadSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @Autowired
    TaskExecutor uploadDownloadTaskExecutor;

    @Autowired
    DownloadUploadS3 downloadUploadS3;
    /**
     * {@code GET  /download/uploads/} : download objects with the "id" aidasObject and provided status.  User "all" for download both.
     *
     * @param uploadIds array of upload ids to download.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasObject, or with status {@code 404 (Not Found)}.
     */
    @PostMapping("/download/uploads")
    public void downloadUploadedObjects(@RequestBody List<Long> uploadIds){
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        downloadUploadS3.setAidasUser(aidasUser);
        downloadUploadS3.setUp(uploadIds);
        uploadDownloadTaskExecutor.execute(downloadUploadS3);
    }
}
