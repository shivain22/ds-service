package com.ainnotate.aidas.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ainnotate.aidas.domain.AidasProperties;
import com.ainnotate.aidas.repository.AidasPropertiesRepository;
import com.ainnotate.aidas.repository.search.AidasPropertiesSearchRepository;
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
 * REST controller for managing {@link com.ainnotate.aidas.domain.AidasProperties}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AidasPropertiesResource {

    private final Logger log = LoggerFactory.getLogger(AidasPropertiesResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasProperties";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AidasPropertiesRepository aidasPropertiesRepository;

    private final AidasPropertiesSearchRepository aidasPropertiesSearchRepository;

    public AidasPropertiesResource(
        AidasPropertiesRepository aidasPropertiesRepository,
        AidasPropertiesSearchRepository aidasPropertiesSearchRepository
    ) {
        this.aidasPropertiesRepository = aidasPropertiesRepository;
        this.aidasPropertiesSearchRepository = aidasPropertiesSearchRepository;
    }

    /**
     * {@code POST  /aidas-properties} : Create a new aidasProperties.
     *
     * @param aidasProperties the aidasProperties to create. For propertyType use values "metadata" or "property"
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasProperties, or with status {@code 400 (Bad Request)} if the aidasProperties has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-properties")
    public ResponseEntity<AidasProperties> createAidasProperties(@Valid @RequestBody AidasProperties aidasProperties)
        throws URISyntaxException {
        log.debug("REST request to save AidasProperties : {}", aidasProperties);
        if (aidasProperties.getId() != null) {
            throw new BadRequestAlertException("A new aidasProperties cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AidasProperties result = aidasPropertiesRepository.save(aidasProperties);
        aidasPropertiesSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/aidas-properties/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /aidas-properties/:id} : Updates an existing aidasProperties.
     *
     * @param id the id of the aidasProperties to save.
     * @param aidasProperties the aidasProperties to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasProperties,
     * or with status {@code 400 (Bad Request)} if the aidasProperties is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aidasProperties couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-properties/{id}")
    public ResponseEntity<AidasProperties> updateAidasProperties(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AidasProperties aidasProperties
    ) throws URISyntaxException {
        log.debug("REST request to update AidasProperties : {}, {}", id, aidasProperties);
        if (aidasProperties.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasProperties.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasPropertiesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AidasProperties result = aidasPropertiesRepository.save(aidasProperties);
        aidasPropertiesSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasProperties.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /aidas-properties/:id} : Partial updates given fields of an existing aidasProperties, field will ignore if it is null
     *
     * @param id the id of the aidasProperties to save.
     * @param aidasProperties the aidasProperties to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasProperties,
     * or with status {@code 400 (Bad Request)} if the aidasProperties is not valid,
     * or with status {@code 404 (Not Found)} if the aidasProperties is not found,
     * or with status {@code 500 (Internal Server Error)} if the aidasProperties couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-properties/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AidasProperties> partialUpdateAidasProperties(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AidasProperties aidasProperties
    ) throws URISyntaxException {
        log.debug("REST request to partial update AidasProperties partially : {}, {}", id, aidasProperties);
        if (aidasProperties.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasProperties.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasPropertiesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AidasProperties> result = aidasPropertiesRepository
            .findById(aidasProperties.getId())
            .map(existingAidasProperties -> {
                if (aidasProperties.getName() != null) {
                    existingAidasProperties.setName(aidasProperties.getName());
                }
                if (aidasProperties.getValue() != null) {
                    existingAidasProperties.setValue(aidasProperties.getValue());
                }
                if (aidasProperties.getSystemProperty() != null) {
                    existingAidasProperties.setSystemProperty(aidasProperties.getSystemProperty());
                }
                if (aidasProperties.getOptional() != null) {
                    existingAidasProperties.setOptional(aidasProperties.getOptional());
                }
                if (aidasProperties.getDescription() != null) {
                    existingAidasProperties.setDescription(aidasProperties.getDescription());
                }

                return existingAidasProperties;
            })
            .map(aidasPropertiesRepository::save)
            .map(savedAidasProperties -> {
                aidasPropertiesSearchRepository.save(savedAidasProperties);

                return savedAidasProperties;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasProperties.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-properties} : get all the aidasProperties.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasProperties in body.
     */
    @GetMapping("/aidas-properties")
    public ResponseEntity<List<AidasProperties>> getAllAidasProperties(Pageable pageable) {
        log.debug("REST request to get a page of AidasProperties");
        Page<AidasProperties> page = aidasPropertiesRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-properties/:id} : get the "id" aidasProperties.
     *
     * @param id the id of the aidasProperties to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasProperties, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-properties/{id}")
    public ResponseEntity<AidasProperties> getAidasProperties(@PathVariable Long id) {
        log.debug("REST request to get AidasProperties : {}", id);
        Optional<AidasProperties> aidasProperties = aidasPropertiesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(aidasProperties);
    }

    /**
     * {@code DELETE  /aidas-properties/:id} : delete the "id" aidasProperties.
     *
     * @param id the id of the aidasProperties to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aidas-properties/{id}")
    public ResponseEntity<Void> deleteAidasProperties(@PathVariable Long id) {
        log.debug("REST request to delete AidasProperties : {}", id);
        aidasPropertiesRepository.deleteById(id);
        aidasPropertiesSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-properties?query=:query} : search for the aidasProperties corresponding
     * to the query.
     *
     * @param query the query of the aidasProperties search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/aidas-properties")
    public ResponseEntity<List<AidasProperties>> searchAidasProperties(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasProperties for query {}", query);
        Page<AidasProperties> page = aidasPropertiesSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
