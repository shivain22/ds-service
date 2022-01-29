package com.ainnotate.aidas.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ainnotate.aidas.domain.AidasObject;
import com.ainnotate.aidas.domain.AidasUpload;
import com.ainnotate.aidas.domain.AidasUser;
import com.ainnotate.aidas.domain.AidasUserAidasObjectMapping;
import com.ainnotate.aidas.dto.UploadByUserObjectMappingDto;
import com.ainnotate.aidas.dto.UploadDto;
import com.ainnotate.aidas.repository.*;
import com.ainnotate.aidas.repository.search.AidasUploadSearchRepository;
import com.ainnotate.aidas.security.SecurityUtils;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
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

    private final AidasUploadRepository aidasUploadRepository;

    private final AidasUploadSearchRepository aidasUploadSearchRepository;

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
        if (aidasUpload.getId() != null) {
            throw new BadRequestAlertException("A new aidasUpload cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AidasUpload result = aidasUploadRepository.save(aidasUpload);
        aidasUploadSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/aidas-uploads/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
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
        log.debug("REST request to save AidasUpload : {}", uploadDto);
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
        AidasUpload result = aidasUploadRepository.save(aidasUpload);
        //aidasUploadSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/aidas-uploads/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
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
        log.debug("REST request to save AidasUpload : {}", uploadByUserObjectMappingDto);
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
        aidasUpload.setUploadObjectMeta(uploadByUserObjectMappingDto.getUploadMetadata());
        AidasUpload result = aidasUploadRepository.save(aidasUpload);
        //aidasUploadSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/aidas-uploads/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
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
        log.debug("REST request to update AidasUpload : {}, {}", id, aidasUpload);
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
        log.debug("REST request to approve AidasUpload : {}, {}", id);
        if (!aidasUploadRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        AidasUpload aidasUpload = aidasUploadRepository.getById(id);
        aidasUpload.setStatus(true);
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
        log.debug("REST request to reject AidasUpload : {}, {}", id);
        if (!aidasUploadRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        AidasUpload aidasUpload = aidasUploadRepository.getById(id);
        aidasUpload.setStatus(true);
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
        log.debug("REST request to partial update AidasUpload partially : {}, {}", id, aidasUpload);
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
                if (aidasUpload.getRejectReason() != null) {
                    existingAidasUpload.setRejectReason(aidasUpload.getRejectReason());
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
        log.debug("REST request to get a page of AidasUploads");
        Page<AidasUpload> page = aidasUploadRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-uploads/:id} : get the "id" aidasUpload.
     *
     * @param id the id of the aidasUpload to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasUpload, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-uploads/{id}")
    public ResponseEntity<AidasUpload> getAidasUpload(@PathVariable Long id) {
        log.debug("REST request to get AidasUpload : {}", id);
        Optional<AidasUpload> aidasUpload = aidasUploadRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(aidasUpload);
    }

    /**
     * {@code DELETE  /aidas-uploads/:id} : delete the "id" aidasUpload.
     *
     * @param id the id of the aidasUpload to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aidas-uploads/{id}")
    public ResponseEntity<Void> deleteAidasUpload(@PathVariable Long id) {
        log.debug("REST request to delete AidasUpload : {}", id);
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
        log.debug("REST request to search for a page of AidasUploads for query {}", query);
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
