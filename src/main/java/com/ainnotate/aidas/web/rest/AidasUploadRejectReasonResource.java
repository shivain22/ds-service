package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.AidasUploadRejectReason;
import com.ainnotate.aidas.domain.AidasUser;
import com.ainnotate.aidas.repository.AidasUploadRejectReasonRepository;
import com.ainnotate.aidas.repository.AidasUserRepository;
import com.ainnotate.aidas.repository.search.AidasAuthoritySearchRepository;
import com.ainnotate.aidas.repository.search.AidasUploadRejectReasonSearchRepository;
import com.ainnotate.aidas.security.SecurityUtils;
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
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link AidasUploadRejectReason}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AidasUploadRejectReasonResource {

    private final Logger log = LoggerFactory.getLogger(AidasUploadRejectReasonResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasUploadRejectReason";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Autowired
    private AidasUserRepository aidasUserRepository;

    private final AidasUploadRejectReasonRepository aidasUploadRejectReasonRepository;

    private final AidasUploadRejectReasonSearchRepository aidasUploadRejectReasonSearchRepository;

    public AidasUploadRejectReasonResource(
        AidasUploadRejectReasonRepository aidasUploadRejectReasonRepository,
        AidasUploadRejectReasonSearchRepository aidasUploadRejectReasonSearchRepository
    ) {
        this.aidasUploadRejectReasonRepository = aidasUploadRejectReasonRepository;
        this.aidasUploadRejectReasonSearchRepository = aidasUploadRejectReasonSearchRepository;
    }

    /**
     * {@code POST  /aidas-upload-reject-reason} : Create a new aidasUploadRejectReason.
     *
     * @param aidasUploadRejectReason the aidasUploadRejectReason to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasUploadRejectReason, or with status {@code 400 (Bad Request)} if the aidasUploadRejectReason has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-upload-reject-reason")
    public ResponseEntity<AidasUploadRejectReason> createAidasUploadRejectReason(@Valid @RequestBody AidasUploadRejectReason aidasUploadRejectReason) throws URISyntaxException {
        log.debug("REST request to save AidasUploadRejectReason : {}", aidasUploadRejectReason);
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        if (aidasUploadRejectReason.getId() != null) {
            throw new BadRequestAlertException("A new aidasUploadRejectReason cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AidasUploadRejectReason result = aidasUploadRejectReasonRepository.save(aidasUploadRejectReason);
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
     * @param aidasUploadRejectReason the aidasUploadRejectReason to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasUploadRejectReason,
     * or with status {@code 400 (Bad Request)} if the aidasUploadRejectReason is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aidasUploadRejectReason couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-upload-reject-reason/{id}")
    public ResponseEntity<AidasUploadRejectReason> updateUploadRejectReason(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AidasUploadRejectReason aidasUploadRejectReason
    ) throws URISyntaxException {
        log.debug("REST request to update AidasUploadRejectReason : {}, {}", id, aidasUploadRejectReason);
        if (aidasUploadRejectReason.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasUploadRejectReason.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasUploadRejectReasonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AidasUploadRejectReason result = aidasUploadRejectReasonRepository.save(aidasUploadRejectReason);
        aidasUploadRejectReasonSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasUploadRejectReason.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /aidas-upload-reject-reason/:id} : Partial updates given fields of an existing aidasUploadRejectReason, field will ignore if it is null
     *
     * @param id the id of the aidasUploadRejectReason to save.
     * @param aidasUploadRejectReason the aidasUploadRejectReason to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasUploadRejectReason,
     * or with status {@code 400 (Bad Request)} if the aidasUploadRejectReason is not valid,
     * or with status {@code 404 (Not Found)} if the aidasUploadRejectReason is not found,
     * or with status {@code 500 (Internal Server Error)} if the aidasUploadRejectReason couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-upload-reject-reason/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AidasUploadRejectReason> partialUpdateAidasUploadRejectReason(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AidasUploadRejectReason aidasUploadRejectReason
    ) throws URISyntaxException {
        log.debug("REST request to partial update AidasUploadRejectReason partially : {}, {}", id, aidasUploadRejectReason);
        if (aidasUploadRejectReason.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasUploadRejectReason.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasUploadRejectReasonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AidasUploadRejectReason> result = aidasUploadRejectReasonRepository
            .findById(aidasUploadRejectReason.getId())
            .map(existingAidasAuthority -> {
                if (aidasUploadRejectReason.getReason() != null) {
                    existingAidasAuthority.setReason(aidasUploadRejectReason.getReason());
                }
                if (aidasUploadRejectReason.getReason() != null) {
                    existingAidasAuthority.setReason(aidasUploadRejectReason.getReason());
                }

                return existingAidasAuthority;
            })
            .map(aidasUploadRejectReasonRepository::save)
            .map(savedAidasAuthority -> {
                aidasUploadRejectReasonSearchRepository.save(savedAidasAuthority);

                return savedAidasAuthority;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasUploadRejectReason.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-upload-reject-reason} : get all the aidasAuthoritys.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasAuthoritys in body.
     */
    @GetMapping("/aidas-upload-reject-reason")
    public ResponseEntity<List<AidasUploadRejectReason>> getAllAidasUploadRejectReason(Pageable pageable) {
        log.debug("REST request to get a page of AidasAuthoritys");
        Page<AidasUploadRejectReason> page = aidasUploadRejectReasonRepository.findAll(pageable);
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
    public ResponseEntity<AidasUploadRejectReason> getAidasUploadRejectReason(@PathVariable Long id) {
        log.debug("REST request to get AidasUploadRejectReason : {}", id);
        Optional<AidasUploadRejectReason> aidasUploadRejectReason = aidasUploadRejectReasonRepository.findById(id);
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
        AidasUploadRejectReason aidasUploadRejectReason = aidasUploadRejectReasonRepository.getById(id);
        if(aidasUploadRejectReason!=null){
            aidasUploadRejectReason.setStatus(0);
            aidasUploadRejectReasonRepository.save(aidasUploadRejectReason);
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
    public ResponseEntity<List<AidasUploadRejectReason>> searchAidasUploadRejectReason(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasAuthoritys for query {}", query);
        Page<AidasUploadRejectReason> page = aidasUploadRejectReasonSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
