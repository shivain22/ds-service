package com.ainnotate.aidas.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ainnotate.aidas.domain.AidasUploadMetaData;
import com.ainnotate.aidas.repository.AidasUploadMetaDataRepository;
import com.ainnotate.aidas.repository.search.AidasUploadMetaDataSearchRepository;
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
 * REST controller for managing {@link com.ainnotate.aidas.domain.AidasUploadMetaData}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AidasUploadMetaDataResource {

    private final Logger log = LoggerFactory.getLogger(AidasUploadMetaDataResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasUploadMetaData";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AidasUploadMetaDataRepository aidasUploadMetaDataRepository;

    private final AidasUploadMetaDataSearchRepository aidasUploadMetaDataSearchRepository;

    public AidasUploadMetaDataResource(
        AidasUploadMetaDataRepository aidasUploadMetaDataRepository,
        AidasUploadMetaDataSearchRepository aidasUploadMetaDataSearchRepository
    ) {
        this.aidasUploadMetaDataRepository = aidasUploadMetaDataRepository;
        this.aidasUploadMetaDataSearchRepository = aidasUploadMetaDataSearchRepository;
    }

    /**
     * {@code POST  /aidas-upload-meta-data} : Create a new aidasUploadMetaData.
     *
     * @param aidasUploadMetaData the aidasUploadMetaData to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasUploadMetaData, or with status {@code 400 (Bad Request)} if the aidasUploadMetaData has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-upload-meta-data")
    public ResponseEntity<AidasUploadMetaData> createAidasUploadMetaData(@RequestBody AidasUploadMetaData aidasUploadMetaData)
        throws URISyntaxException {
        log.debug("REST request to save AidasUploadMetaData : {}", aidasUploadMetaData);
        if (aidasUploadMetaData.getId() != null) {
            throw new BadRequestAlertException("A new aidasUploadMetaData cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AidasUploadMetaData result = aidasUploadMetaDataRepository.save(aidasUploadMetaData);
        aidasUploadMetaDataSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/aidas-upload-meta-data/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /aidas-upload-meta-data/:id} : Updates an existing aidasUploadMetaData.
     *
     * @param id the id of the aidasUploadMetaData to save.
     * @param aidasUploadMetaData the aidasUploadMetaData to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasUploadMetaData,
     * or with status {@code 400 (Bad Request)} if the aidasUploadMetaData is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aidasUploadMetaData couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-upload-meta-data/{id}")
    public ResponseEntity<AidasUploadMetaData> updateAidasUploadMetaData(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AidasUploadMetaData aidasUploadMetaData
    ) throws URISyntaxException {
        log.debug("REST request to update AidasUploadMetaData : {}, {}", id, aidasUploadMetaData);
        if (aidasUploadMetaData.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasUploadMetaData.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasUploadMetaDataRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AidasUploadMetaData result = aidasUploadMetaDataRepository.save(aidasUploadMetaData);
        aidasUploadMetaDataSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasUploadMetaData.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /aidas-upload-meta-data/:id} : Partial updates given fields of an existing aidasUploadMetaData, field will ignore if it is null
     *
     * @param id the id of the aidasUploadMetaData to save.
     * @param aidasUploadMetaData the aidasUploadMetaData to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasUploadMetaData,
     * or with status {@code 400 (Bad Request)} if the aidasUploadMetaData is not valid,
     * or with status {@code 404 (Not Found)} if the aidasUploadMetaData is not found,
     * or with status {@code 500 (Internal Server Error)} if the aidasUploadMetaData couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-upload-meta-data/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AidasUploadMetaData> partialUpdateAidasUploadMetaData(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AidasUploadMetaData aidasUploadMetaData
    ) throws URISyntaxException {
        log.debug("REST request to partial update AidasUploadMetaData partially : {}, {}", id, aidasUploadMetaData);
        if (aidasUploadMetaData.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasUploadMetaData.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasUploadMetaDataRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AidasUploadMetaData> result = aidasUploadMetaDataRepository
            .findById(aidasUploadMetaData.getId())
            .map(existingAidasUploadMetaData -> {
                if (aidasUploadMetaData.getName() != null) {
                    existingAidasUploadMetaData.setName(aidasUploadMetaData.getName());
                }
                if (aidasUploadMetaData.getValue() != null) {
                    existingAidasUploadMetaData.setValue(aidasUploadMetaData.getValue());
                }

                return existingAidasUploadMetaData;
            })
            .map(aidasUploadMetaDataRepository::save)
            .map(savedAidasUploadMetaData -> {
                aidasUploadMetaDataSearchRepository.save(savedAidasUploadMetaData);

                return savedAidasUploadMetaData;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasUploadMetaData.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-upload-meta-data} : get all the aidasUploadMetaData.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasUploadMetaData in body.
     */
    @GetMapping("/aidas-upload-meta-data")
    public ResponseEntity<List<AidasUploadMetaData>> getAllAidasUploadMetaData(Pageable pageable) {
        log.debug("REST request to get a page of AidasUploadMetaData");
        Page<AidasUploadMetaData> page = aidasUploadMetaDataRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-upload-meta-data/:id} : get the "id" aidasUploadMetaData.
     *
     * @param id the id of the aidasUploadMetaData to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasUploadMetaData, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-upload-meta-data/{id}")
    public ResponseEntity<AidasUploadMetaData> getAidasUploadMetaData(@PathVariable Long id) {
        log.debug("REST request to get AidasUploadMetaData : {}", id);
        Optional<AidasUploadMetaData> aidasUploadMetaData = aidasUploadMetaDataRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(aidasUploadMetaData);
    }

    /**
     * {@code DELETE  /aidas-upload-meta-data/:id} : delete the "id" aidasUploadMetaData.
     *
     * @param id the id of the aidasUploadMetaData to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aidas-upload-meta-data/{id}")
    public ResponseEntity<Void> deleteAidasUploadMetaData(@PathVariable Long id) {
        log.debug("REST request to delete AidasUploadMetaData : {}", id);
        aidasUploadMetaDataRepository.deleteById(id);
        aidasUploadMetaDataSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-upload-meta-data?query=:query} : search for the aidasUploadMetaData corresponding
     * to the query.
     *
     * @param query the query of the aidasUploadMetaData search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/aidas-upload-meta-data")
    public ResponseEntity<List<AidasUploadMetaData>> searchAidasUploadMetaData(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasUploadMetaData for query {}", query);
        Page<AidasUploadMetaData> page = aidasUploadMetaDataSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
