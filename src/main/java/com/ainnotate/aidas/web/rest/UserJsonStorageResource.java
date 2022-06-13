package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.UserJsonStorage;
import com.ainnotate.aidas.repository.UserJsonStorageRepository;
import com.ainnotate.aidas.repository.search.AidasUserJsonStorageSearchRepository;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
 * REST controller for managing {@link UserJsonStorage}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UserJsonStorageResource {

    private final Logger log = LoggerFactory.getLogger(UserJsonStorageResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasUserJsonStorage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserJsonStorageRepository userJsonStorageRepository;

    private final AidasUserJsonStorageSearchRepository aidasUserJsonStorageSearchRepository;

    public UserJsonStorageResource(
        UserJsonStorageRepository userJsonStorageRepository,
        AidasUserJsonStorageSearchRepository aidasUserJsonStorageSearchRepository
    ) {
        this.userJsonStorageRepository = userJsonStorageRepository;
        this.aidasUserJsonStorageSearchRepository = aidasUserJsonStorageSearchRepository;
    }

    /**
     * {@code POST  /aidas-user-json-storages} : Create a new aidasUserJsonStorage.
     *
     * @param userJsonStorage the aidasUserJsonStorage to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasUserJsonStorage, or with status {@code 400 (Bad Request)} if the aidasUserJsonStorage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-user-json-storages")
    public ResponseEntity<UserJsonStorage> createAidasUserJsonStorage(@RequestBody UserJsonStorage userJsonStorage)
        throws URISyntaxException {
        log.debug("REST request to save AidasUserJsonStorage : {}", userJsonStorage);
        if (userJsonStorage.getId() != null) {
            throw new BadRequestAlertException("A new aidasUserJsonStorage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserJsonStorage result = userJsonStorageRepository.save(userJsonStorage);
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
     * @param userJsonStorage the aidasUserJsonStorage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasUserJsonStorage,
     * or with status {@code 400 (Bad Request)} if the aidasUserJsonStorage is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aidasUserJsonStorage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-user-json-storages/{id}")
    public ResponseEntity<UserJsonStorage> updateAidasUserJsonStorage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserJsonStorage userJsonStorage
    ) throws URISyntaxException {
        log.debug("REST request to update AidasUserJsonStorage : {}, {}", id, userJsonStorage);
        if (userJsonStorage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userJsonStorage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userJsonStorageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserJsonStorage result = userJsonStorageRepository.save(userJsonStorage);
        aidasUserJsonStorageSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userJsonStorage.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /aidas-user-json-storages/:id} : Partial updates given fields of an existing aidasUserJsonStorage, field will ignore if it is null
     *
     * @param id the id of the aidasUserJsonStorage to save.
     * @param userJsonStorage the aidasUserJsonStorage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasUserJsonStorage,
     * or with status {@code 400 (Bad Request)} if the aidasUserJsonStorage is not valid,
     * or with status {@code 404 (Not Found)} if the aidasUserJsonStorage is not found,
     * or with status {@code 500 (Internal Server Error)} if the aidasUserJsonStorage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-user-json-storages/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserJsonStorage> partialUpdateAidasUserJsonStorage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserJsonStorage userJsonStorage
    ) throws URISyntaxException {
        log.debug("REST request to partial update AidasUserJsonStorage partially : {}, {}", id, userJsonStorage);
        if (userJsonStorage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userJsonStorage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userJsonStorageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserJsonStorage> result = userJsonStorageRepository
            .findById(userJsonStorage.getId())
            .map(existingAidasUserJsonStorage -> {
                if (userJsonStorage.getJsonPayLoad() != null) {
                    existingAidasUserJsonStorage.setJsonPayLoad(userJsonStorage.getJsonPayLoad());
                }

                return existingAidasUserJsonStorage;
            })
            .map(userJsonStorageRepository::save)
            .map(savedAidasUserJsonStorage -> {
                aidasUserJsonStorageSearchRepository.save(savedAidasUserJsonStorage);

                return savedAidasUserJsonStorage;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userJsonStorage.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-user-json-storages} : get all the aidasUserJsonStorages.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasUserJsonStorages in body.
     */
    @GetMapping("/aidas-user-json-storages")
    public ResponseEntity<List<UserJsonStorage>> getAllAidasUserJsonStorages(Pageable pageable) {
        log.debug("REST request to get a page of AidasUserJsonStorages");
        Page<UserJsonStorage> page = userJsonStorageRepository.findAll(pageable);
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
    public ResponseEntity<UserJsonStorage> getAidasUserJsonStorage(@PathVariable Long id) {
        log.debug("REST request to get AidasUserJsonStorage : {}", id);
        Optional<UserJsonStorage> aidasUserJsonStorage = userJsonStorageRepository.findById(id);
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
        userJsonStorageRepository.deleteById(id);
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
    public ResponseEntity<List<UserJsonStorage>> searchAidasUserJsonStorages(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasUserJsonStorages for query {}", query);
        Page<UserJsonStorage> page = aidasUserJsonStorageSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
