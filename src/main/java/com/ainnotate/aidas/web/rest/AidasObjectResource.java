package com.ainnotate.aidas.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ainnotate.aidas.domain.AidasObject;
import com.ainnotate.aidas.repository.AidasObjectRepository;
import com.ainnotate.aidas.repository.search.AidasObjectSearchRepository;
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

    private final AidasObjectSearchRepository aidasObjectSearchRepository;

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
    @PostMapping("/aidas-objects")
    public ResponseEntity<AidasObject> createAidasObject(@Valid @RequestBody AidasObject aidasObject) throws URISyntaxException {
        log.debug("REST request to save AidasObject : {}", aidasObject);
        if (aidasObject.getId() != null) {
            throw new BadRequestAlertException("A new aidasObject cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AidasObject result = aidasObjectRepository.save(aidasObject);
        aidasObjectSearchRepository.save(result);
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
    @PutMapping("/aidas-objects/{id}")
    public ResponseEntity<AidasObject> updateAidasObject(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AidasObject aidasObject
    ) throws URISyntaxException {
        log.debug("REST request to update AidasObject : {}, {}", id, aidasObject);
        if (aidasObject.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasObject.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasObjectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AidasObject result = aidasObjectRepository.save(aidasObject);
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
        if (aidasObject.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasObject.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasObjectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

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
        Page<AidasObject> page = aidasObjectRepository.findAll(pageable);
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
        Optional<AidasObject> aidasObject = aidasObjectRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(aidasObject);
    }

    /**
     * {@code DELETE  /aidas-objects/:id} : delete the "id" aidasObject.
     *
     * @param id the id of the aidasObject to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aidas-objects/{id}")
    public ResponseEntity<Void> deleteAidasObject(@PathVariable Long id) {
        log.debug("REST request to delete AidasObject : {}", id);
        aidasObjectRepository.deleteById(id);
        aidasObjectSearchRepository.deleteById(id);
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
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
