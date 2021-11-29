package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.AidasAuthority;
import com.ainnotate.aidas.repository.AidasAuthorityRepository;
import com.ainnotate.aidas.repository.search.AidasAuthoritySearchRepository;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing {@link AidasAuthority}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AidasAuthorityResource {

    private final Logger log = LoggerFactory.getLogger(AidasAuthorityResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasAuthority";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AidasAuthorityRepository aidasAuthorityRepository;

    private final AidasAuthoritySearchRepository aidasAuthoritySearchRepository;

    public AidasAuthorityResource(
        AidasAuthorityRepository aidasAuthorityRepository,
        AidasAuthoritySearchRepository aidasAuthoritySearchRepository
    ) {
        this.aidasAuthorityRepository = aidasAuthorityRepository;
        this.aidasAuthoritySearchRepository = aidasAuthoritySearchRepository;
    }

    /**
     * {@code POST  /aidas-authorities} : Create a new aidasAuthority.
     *
     * @param aidasAuthority the aidasAuthority to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasAuthority, or with status {@code 400 (Bad Request)} if the aidasAuthority has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-authorities")
    public ResponseEntity<AidasAuthority> createAidasAuthority(@Valid @RequestBody AidasAuthority aidasAuthority) throws URISyntaxException {
        log.debug("REST request to save AidasAuthority : {}", aidasAuthority);
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        if (aidasAuthority.getId() != null) {
            throw new BadRequestAlertException("A new aidasAuthority cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AidasAuthority result = aidasAuthorityRepository.save(aidasAuthority);
        aidasAuthoritySearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/aidas-authorities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /aidas-authorities/:id} : Updates an existing aidasAuthority.
     *
     * @param id the id of the aidasAuthority to save.
     * @param aidasAuthority the aidasAuthority to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasAuthority,
     * or with status {@code 400 (Bad Request)} if the aidasAuthority is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aidasAuthority couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-authorities/{id}")
    public ResponseEntity<AidasAuthority> updateAidasAuthority(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AidasAuthority aidasAuthority
    ) throws URISyntaxException {
        log.debug("REST request to update AidasAuthority : {}, {}", id, aidasAuthority);
        if (aidasAuthority.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasAuthority.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasAuthorityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AidasAuthority result = aidasAuthorityRepository.save(aidasAuthority);
        aidasAuthoritySearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasAuthority.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /aidas-authorities/:id} : Partial updates given fields of an existing aidasAuthority, field will ignore if it is null
     *
     * @param id the id of the aidasAuthority to save.
     * @param aidasAuthority the aidasAuthority to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasAuthority,
     * or with status {@code 400 (Bad Request)} if the aidasAuthority is not valid,
     * or with status {@code 404 (Not Found)} if the aidasAuthority is not found,
     * or with status {@code 500 (Internal Server Error)} if the aidasAuthority couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-authorities/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AidasAuthority> partialUpdateAidasAuthority(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AidasAuthority aidasAuthority
    ) throws URISyntaxException {
        log.debug("REST request to partial update AidasAuthority partially : {}, {}", id, aidasAuthority);
        if (aidasAuthority.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasAuthority.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasAuthorityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AidasAuthority> result = aidasAuthorityRepository
            .findById(aidasAuthority.getId())
            .map(existingAidasAuthority -> {
                if (aidasAuthority.getName() != null) {
                    existingAidasAuthority.setName(aidasAuthority.getName());
                }
                if (aidasAuthority.getName() != null) {
                    existingAidasAuthority.setName(aidasAuthority.getName());
                }

                return existingAidasAuthority;
            })
            .map(aidasAuthorityRepository::save)
            .map(savedAidasAuthority -> {
                aidasAuthoritySearchRepository.save(savedAidasAuthority);

                return savedAidasAuthority;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasAuthority.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-authorities} : get all the aidasAuthoritys.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasAuthoritys in body.
     */
    @GetMapping("/aidas-authorities")
    public ResponseEntity<List<AidasAuthority>> getAllAidasAuthoritys(Pageable pageable) {
        log.debug("REST request to get a page of AidasAuthoritys");
        Page<AidasAuthority> page = aidasAuthorityRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-authorities/:id} : get the "id" aidasAuthority.
     *
     * @param id the id of the aidasAuthority to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasAuthority, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-authorities/{id}")
    public ResponseEntity<AidasAuthority> getAidasAuthority(@PathVariable Long id) {
        log.debug("REST request to get AidasAuthority : {}", id);
        Optional<AidasAuthority> aidasAuthority = aidasAuthorityRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(aidasAuthority);
    }

    /**
     * {@code DELETE  /aidas-authorities/:id} : delete the "id" aidasAuthority.
     *
     * @param id the id of the aidasAuthority to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aidas-authorities/{id}")
    public ResponseEntity<Void> deleteAidasAuthority(@PathVariable Long id) {
        log.debug("REST request to delete AidasAuthority : {}", id);
        aidasAuthorityRepository.deleteById(id);
        aidasAuthoritySearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-authorities?query=:query} : search for the aidasAuthority corresponding
     * to the query.
     *
     * @param query the query of the aidasAuthority search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/aidas-authorities")
    public ResponseEntity<List<AidasAuthority>> searchAidasAuthoritys(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasAuthoritys for query {}", query);
        Page<AidasAuthority> page = aidasAuthoritySearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
