package com.ainnotate.aidas.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ainnotate.aidas.domain.AidasVendor;
import com.ainnotate.aidas.repository.AidasVendorRepository;
import com.ainnotate.aidas.repository.search.AidasVendorSearchRepository;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ainnotate.aidas.domain.AidasVendor}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AidasVendorResource {

    private final Logger log = LoggerFactory.getLogger(AidasVendorResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasVendor";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AidasVendorRepository aidasVendorRepository;

    private final AidasVendorSearchRepository aidasVendorSearchRepository;

    public AidasVendorResource(AidasVendorRepository aidasVendorRepository, AidasVendorSearchRepository aidasVendorSearchRepository) {
        this.aidasVendorRepository = aidasVendorRepository;
        this.aidasVendorSearchRepository = aidasVendorSearchRepository;
    }

    /**
     * {@code POST  /aidas-vendors} : Create a new aidasVendor.
     *
     * @param aidasVendor the aidasVendor to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasVendor, or with status {@code 400 (Bad Request)} if the aidasVendor has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-vendors")
    public ResponseEntity<AidasVendor> createAidasVendor(@Valid @RequestBody AidasVendor aidasVendor) throws URISyntaxException {
        log.debug("REST request to save AidasVendor : {}", aidasVendor);
        if (aidasVendor.getId() != null) {
            throw new BadRequestAlertException("A new aidasVendor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AidasVendor result = aidasVendorRepository.save(aidasVendor);
        aidasVendorSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/aidas-vendors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /aidas-vendors/:id} : Updates an existing aidasVendor.
     *
     * @param id the id of the aidasVendor to save.
     * @param aidasVendor the aidasVendor to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasVendor,
     * or with status {@code 400 (Bad Request)} if the aidasVendor is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aidasVendor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-vendors/{id}")
    public ResponseEntity<AidasVendor> updateAidasVendor(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AidasVendor aidasVendor
    ) throws URISyntaxException {
        log.debug("REST request to update AidasVendor : {}, {}", id, aidasVendor);
        if (aidasVendor.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasVendor.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasVendorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AidasVendor result = aidasVendorRepository.save(aidasVendor);
        aidasVendorSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasVendor.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /aidas-vendors/:id} : Partial updates given fields of an existing aidasVendor, field will ignore if it is null
     *
     * @param id the id of the aidasVendor to save.
     * @param aidasVendor the aidasVendor to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasVendor,
     * or with status {@code 400 (Bad Request)} if the aidasVendor is not valid,
     * or with status {@code 404 (Not Found)} if the aidasVendor is not found,
     * or with status {@code 500 (Internal Server Error)} if the aidasVendor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-vendors/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AidasVendor> partialUpdateAidasVendor(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AidasVendor aidasVendor
    ) throws URISyntaxException {
        log.debug("REST request to partial update AidasVendor partially : {}, {}", id, aidasVendor);
        if (aidasVendor.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasVendor.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasVendorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AidasVendor> result = aidasVendorRepository
            .findById(aidasVendor.getId())
            .map(existingAidasVendor -> {
                if (aidasVendor.getName() != null) {
                    existingAidasVendor.setName(aidasVendor.getName());
                }
                if (aidasVendor.getDescription() != null) {
                    existingAidasVendor.setDescription(aidasVendor.getDescription());
                }

                return existingAidasVendor;
            })
            .map(aidasVendorRepository::save)
            .map(savedAidasVendor -> {
                aidasVendorSearchRepository.save(savedAidasVendor);

                return savedAidasVendor;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasVendor.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-vendors} : get all the aidasVendors.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasVendors in body.
     */
    @GetMapping("/aidas-vendors")
    public ResponseEntity<List<AidasVendor>> getAllAidasVendors(Pageable pageable) {
        log.debug("REST request to get a page of AidasVendors");
        Page<AidasVendor> page = aidasVendorRepository.findAllByIdGreaterThan(0l,pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-vendors/:id} : get the "id" aidasVendor.
     *
     * @param id the id of the aidasVendor to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasVendor, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-vendors/{id}")
    public ResponseEntity<AidasVendor> getAidasVendor(@PathVariable Long id) {
        log.debug("REST request to get AidasVendor : {}", id);
        Optional<AidasVendor> aidasVendor = aidasVendorRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(aidasVendor);
    }

    /**
     * {@code DELETE  /aidas-vendors/:id} : delete the "id" aidasVendor.
     *
     * @param id the id of the aidasVendor to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aidas-vendors/{id}")
    public ResponseEntity<Void> deleteAidasVendor(@PathVariable Long id) {
        log.debug("REST request to delete AidasVendor : {}", id);
        aidasVendorRepository.deleteById(id);
        aidasVendorSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-vendors?query=:query} : search for the aidasVendor corresponding
     * to the query.
     *
     * @param query the query of the aidasVendor search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/aidas-vendors")
    public ResponseEntity<List<AidasVendor>> searchAidasVendors(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasVendors for query {}", query);
        Page<AidasVendor> page = aidasVendorSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
