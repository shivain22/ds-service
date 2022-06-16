package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.UploadRejectReason;
import com.ainnotate.aidas.repository.UploadRejectReasonRepository;
import com.ainnotate.aidas.repository.UserRepository;
import com.ainnotate.aidas.repository.search.UploadRejectReasonSearchRepository;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link UploadRejectReason}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UploadRejectReasonResource {

    private final Logger log = LoggerFactory.getLogger(UploadRejectReasonResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasUploadRejectReason";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Autowired
    private UserRepository userRepository;

    private final UploadRejectReasonRepository uploadRejectReasonRepository;

    private final UploadRejectReasonSearchRepository aidasUploadRejectReasonSearchRepository;

    public UploadRejectReasonResource(
        UploadRejectReasonRepository uploadRejectReasonRepository,
        UploadRejectReasonSearchRepository aidasUploadRejectReasonSearchRepository
    ) {
        this.uploadRejectReasonRepository = uploadRejectReasonRepository;
        this.aidasUploadRejectReasonSearchRepository = aidasUploadRejectReasonSearchRepository;
    }

    /**
     * {@code POST  /aidas-upload-reject-reason} : Create a new aidasUploadRejectReason.
     *
     * @param uploadRejectReason the aidasUploadRejectReason to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasUploadRejectReason, or with status {@code 400 (Bad Request)} if the aidasUploadRejectReason has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-upload-reject-reason")
    public ResponseEntity<UploadRejectReason> createAidasUploadRejectReason(@Valid @RequestBody UploadRejectReason uploadRejectReason) throws URISyntaxException {
        log.debug("REST request to save AidasUploadRejectReason : {}", uploadRejectReason);
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        if (uploadRejectReason.getId() != null) {
            throw new BadRequestAlertException("A new aidasUploadRejectReason cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UploadRejectReason result = uploadRejectReasonRepository.save(uploadRejectReason);
        //aidasUploadRejectReasonSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/aidas-upload-reject-reason/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /aidas-upload-reject-reason/:id} : Updates an existing aidasUploadRejectReason.
     *
     * @param id the id of the aidasUploadRejectReason to save.
     * @param uploadRejectReason the aidasUploadRejectReason to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasUploadRejectReason,
     * or with status {@code 400 (Bad Request)} if the aidasUploadRejectReason is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aidasUploadRejectReason couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-upload-reject-reason/{id}")
    public ResponseEntity<UploadRejectReason> updateUploadRejectReason(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UploadRejectReason uploadRejectReason
    ) throws URISyntaxException {
        log.debug("REST request to update AidasUploadRejectReason : {}, {}", id, uploadRejectReason);
        if (uploadRejectReason.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uploadRejectReason.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!uploadRejectReasonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UploadRejectReason result = uploadRejectReasonRepository.save(uploadRejectReason);
        aidasUploadRejectReasonSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, uploadRejectReason.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /aidas-upload-reject-reason/:id} : Partial updates given fields of an existing aidasUploadRejectReason, field will ignore if it is null
     *
     * @param id the id of the aidasUploadRejectReason to save.
     * @param uploadRejectReason the aidasUploadRejectReason to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasUploadRejectReason,
     * or with status {@code 400 (Bad Request)} if the aidasUploadRejectReason is not valid,
     * or with status {@code 404 (Not Found)} if the aidasUploadRejectReason is not found,
     * or with status {@code 500 (Internal Server Error)} if the aidasUploadRejectReason couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-upload-reject-reason/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UploadRejectReason> partialUpdateAidasUploadRejectReason(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UploadRejectReason uploadRejectReason
    ) throws URISyntaxException {
        log.debug("REST request to partial update AidasUploadRejectReason partially : {}, {}", id, uploadRejectReason);
        if (uploadRejectReason.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uploadRejectReason.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!uploadRejectReasonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UploadRejectReason> result = uploadRejectReasonRepository
            .findById(uploadRejectReason.getId())
            .map(existingAidasAuthority -> {
                if (uploadRejectReason.getReason() != null) {
                    existingAidasAuthority.setReason(uploadRejectReason.getReason());
                }
                if (uploadRejectReason.getReason() != null) {
                    existingAidasAuthority.setReason(uploadRejectReason.getReason());
                }

                return existingAidasAuthority;
            })
            .map(uploadRejectReasonRepository::save)
            .map(savedAidasAuthority -> {
                aidasUploadRejectReasonSearchRepository.save(savedAidasAuthority);

                return savedAidasAuthority;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, uploadRejectReason.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-upload-reject-reason} : get all the aidasAuthoritys.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasAuthoritys in body.
     */
    @GetMapping("/aidas-upload-reject-reason")
    public ResponseEntity<List<UploadRejectReason>> getAllAidasUploadRejectReason(Pageable pageable) {
        log.debug("REST request to get a page of AidasAuthoritys");
        Page<UploadRejectReason> page = uploadRejectReasonRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-upload-reject-reason/:id} : get the "id" aidasUploadRejectReason.
     *
     * @param id the id of the aidasUploadRejectReason to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasUploadRejectReason, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-upload-reject-reason/{id}")
    public ResponseEntity<UploadRejectReason> getAidasUploadRejectReason(@PathVariable Long id) {
        log.debug("REST request to get AidasUploadRejectReason : {}", id);
        Optional<UploadRejectReason> aidasUploadRejectReason = uploadRejectReasonRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(aidasUploadRejectReason);
    }

    /**
     * {@code DELETE  /aidas-upload-reject-reason/:id} : delete the "id" aidasUploadRejectReason.
     *
     * @param id the id of the aidasUploadRejectReason to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aidas-upload-reject-reason/{id}")
    public ResponseEntity<Void> deleteAidasUploadRejectReason(@PathVariable Long id) {
        log.debug("REST request to delete AidasUploadRejectReason : {}", id);
        //aidasUploadRejectReasonRepository.deleteById(id);
        //aidasUploadRejectReasonSearchRepository.deleteById(id);
        UploadRejectReason uploadRejectReason = uploadRejectReasonRepository.getById(id);
        if(uploadRejectReason !=null){
            uploadRejectReason.setStatus(0);
            uploadRejectReasonRepository.save(uploadRejectReason);
        }
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-upload-reject-reason?query=:query} : search for the aidasUploadRejectReason corresponding
     * to the query.
     *
     * @param query the query of the aidasUploadRejectReason search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/aidas-upload-reject-reason")
    public ResponseEntity<List<UploadRejectReason>> searchAidasUploadRejectReason(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasAuthoritys for query {}", query);
        Page<UploadRejectReason> page = aidasUploadRejectReasonSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
