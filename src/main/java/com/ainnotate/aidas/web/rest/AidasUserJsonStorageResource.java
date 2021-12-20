package com.ainnotate.aidas.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ainnotate.aidas.domain.AidasUserJsonStorage;
import com.ainnotate.aidas.repository.AidasUserJsonStorageRepository;
import com.ainnotate.aidas.repository.search.AidasUserJsonStorageSearchRepository;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
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
 * REST controller for managing {@link AidasUserJsonStorage}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AidasUserJsonStorageResource {

    private final Logger log = LoggerFactory.getLogger(AidasUserJsonStorageResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasUserJsonStorage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AidasUserJsonStorageRepository aidasUserJsonStorageRepository;

    private final AidasUserJsonStorageSearchRepository aidasUserJsonStorageSearchRepository;

    public AidasUserJsonStorageResource(
        AidasUserJsonStorageRepository aidasUserJsonStorageRepository,
        AidasUserJsonStorageSearchRepository aidasUserJsonStorageSearchRepository
    ) {
        this.aidasUserJsonStorageRepository = aidasUserJsonStorageRepository;
        this.aidasUserJsonStorageSearchRepository = aidasUserJsonStorageSearchRepository;
    }

    /**
     * {@code POST  /aidas-user-json-storages} : Create a new aidasUserJsonStorage.
     *
     * @param aidasUserJsonStorage the aidasUserJsonStorage to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasUserJsonStorage, or with status {@code 400 (Bad Request)} if the aidasUserJsonStorage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-user-json-storages")
    public ResponseEntity<AidasUserJsonStorage> createAidasUserJsonStorage(@RequestBody AidasUserJsonStorage aidasUserJsonStorage)
        throws URISyntaxException {
        log.debug("REST request to save AidasUserJsonStorage : {}", aidasUserJsonStorage);
        if (aidasUserJsonStorage.getId() != null) {
            throw new BadRequestAlertException("A new aidasUserJsonStorage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AidasUserJsonStorage result = aidasUserJsonStorageRepository.save(aidasUserJsonStorage);
        aidasUserJsonStorageSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/aidas-user-json-storages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /aidas-user-json-storages/:id} : Updates an existing aidasUserJsonStorage.
     *
     * @param id the id of the aidasUserJsonStorage to save.
     * @param aidasUserJsonStorage the aidasUserJsonStorage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasUserJsonStorage,
     * or with status {@code 400 (Bad Request)} if the aidasUserJsonStorage is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aidasUserJsonStorage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-user-json-storages/{id}")
    public ResponseEntity<AidasUserJsonStorage> updateAidasUserJsonStorage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AidasUserJsonStorage aidasUserJsonStorage
    ) throws URISyntaxException {
        log.debug("REST request to update AidasUserJsonStorage : {}, {}", id, aidasUserJsonStorage);
        if (aidasUserJsonStorage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasUserJsonStorage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasUserJsonStorageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AidasUserJsonStorage result = aidasUserJsonStorageRepository.save(aidasUserJsonStorage);
        aidasUserJsonStorageSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasUserJsonStorage.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /aidas-user-json-storages/:id} : Partial updates given fields of an existing aidasUserJsonStorage, field will ignore if it is null
     *
     * @param id the id of the aidasUserJsonStorage to save.
     * @param aidasUserJsonStorage the aidasUserJsonStorage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasUserJsonStorage,
     * or with status {@code 400 (Bad Request)} if the aidasUserJsonStorage is not valid,
     * or with status {@code 404 (Not Found)} if the aidasUserJsonStorage is not found,
     * or with status {@code 500 (Internal Server Error)} if the aidasUserJsonStorage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-user-json-storages/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AidasUserJsonStorage> partialUpdateAidasUserJsonStorage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AidasUserJsonStorage aidasUserJsonStorage
    ) throws URISyntaxException {
        log.debug("REST request to partial update AidasUserJsonStorage partially : {}, {}", id, aidasUserJsonStorage);
        if (aidasUserJsonStorage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasUserJsonStorage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasUserJsonStorageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AidasUserJsonStorage> result = aidasUserJsonStorageRepository
            .findById(aidasUserJsonStorage.getId())
            .map(existingAidasUserJsonStorage -> {
                if (aidasUserJsonStorage.getJsonPayLoad() != null) {
                    existingAidasUserJsonStorage.setJsonPayLoad(aidasUserJsonStorage.getJsonPayLoad());
                }

                return existingAidasUserJsonStorage;
            })
            .map(aidasUserJsonStorageRepository::save)
            .map(savedAidasUserJsonStorage -> {
                aidasUserJsonStorageSearchRepository.save(savedAidasUserJsonStorage);

                return savedAidasUserJsonStorage;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasUserJsonStorage.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-user-json-storages} : get all the aidasUserJsonStorages.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasUserJsonStorages in body.
     */
    @GetMapping("/aidas-user-json-storages")
    public ResponseEntity<List<AidasUserJsonStorage>> getAllAidasUserJsonStorages(Pageable pageable) {
        log.debug("REST request to get a page of AidasUserJsonStorages");
        Page<AidasUserJsonStorage> page = aidasUserJsonStorageRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-user-json-storages/:id} : get the "id" aidasUserJsonStorage.
     *
     * @param id the id of the aidasUserJsonStorage to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasUserJsonStorage, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-user-json-storages/{id}")
    public ResponseEntity<AidasUserJsonStorage> getAidasUserJsonStorage(@PathVariable Long id) {
        log.debug("REST request to get AidasUserJsonStorage : {}", id);
        Optional<AidasUserJsonStorage> aidasUserJsonStorage = aidasUserJsonStorageRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(aidasUserJsonStorage);
    }

    /**
     * {@code DELETE  /aidas-user-json-storages/:id} : delete the "id" aidasUserJsonStorage.
     *
     * @param id the id of the aidasUserJsonStorage to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aidas-user-json-storages/{id}")
    public ResponseEntity<Void> deleteAidasUserJsonStorage(@PathVariable Long id) {
        log.debug("REST request to delete AidasUserJsonStorage : {}", id);
        aidasUserJsonStorageRepository.deleteById(id);
        aidasUserJsonStorageSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-user-json-storages?query=:query} : search for the aidasUserJsonStorage corresponding
     * to the query.
     *
     * @param query the query of the aidasUserJsonStorage search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/aidas-user-json-storages")
    public ResponseEntity<List<AidasUserJsonStorage>> searchAidasUserJsonStorages(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasUserJsonStorages for query {}", query);
        Page<AidasUserJsonStorage> page = aidasUserJsonStorageSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
