package com.ainnotate.aidas.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ainnotate.aidas.domain.AidasProject;
import com.ainnotate.aidas.domain.AidasProjectProperty;
import com.ainnotate.aidas.repository.AidasProjectPropertyRepository;
import com.ainnotate.aidas.repository.AidasProjectRepository;
import com.ainnotate.aidas.repository.search.AidasProjectPropertySearchRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
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
 * REST controller for managing {@link com.ainnotate.aidas.domain.AidasProjectProperty}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AidasProjectPropertyResource {

    private final Logger log = LoggerFactory.getLogger(AidasProjectPropertyResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasProjectProperty";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AidasProjectPropertyRepository aidasProjectPropertyRepository;

    @Autowired
    private AidasProjectRepository aidasProjectRepository;

    private final AidasProjectPropertySearchRepository aidasProjectPropertySearchRepository;

    public AidasProjectPropertyResource(
        AidasProjectPropertyRepository aidasProjectPropertyRepository,
        AidasProjectPropertySearchRepository aidasProjectPropertySearchRepository
    ) {
        this.aidasProjectPropertyRepository = aidasProjectPropertyRepository;
        this.aidasProjectPropertySearchRepository = aidasProjectPropertySearchRepository;
    }

    /**
     * {@code POST  /aidas-project-properties} : Create a new aidasProjectProperty.
     *
     * @param aidasProjectProperty the aidasProjectProperty to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasProjectProperty, or with status {@code 400 (Bad Request)} if the aidasProjectProperty has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-project-properties")
    public ResponseEntity<AidasProjectProperty> createAidasProjectProperty(@Valid @RequestBody AidasProjectProperty aidasProjectProperty)
        throws URISyntaxException {
        log.debug("REST request to save AidasProjectProperty : {}", aidasProjectProperty);
        if (aidasProjectProperty.getId() != null) {
            throw new BadRequestAlertException("A new aidasProjectProperty cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AidasProjectProperty result = aidasProjectPropertyRepository.save(aidasProjectProperty);
        aidasProjectPropertySearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/aidas-project-properties/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /aidas-project-properties/:id} : Updates an existing aidasProjectProperty.
     *
     * @param id the id of the aidasProjectProperty to save.
     * @param aidasProjectProperty the aidasProjectProperty to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasProjectProperty,
     * or with status {@code 400 (Bad Request)} if the aidasProjectProperty is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aidasProjectProperty couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-project-properties/{id}")
    public ResponseEntity<AidasProjectProperty> updateAidasProjectProperty(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AidasProjectProperty aidasProjectProperty
    ) throws URISyntaxException {
        log.debug("REST request to update AidasProjectProperty : {}, {}", id, aidasProjectProperty);
        if (aidasProjectProperty.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasProjectProperty.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasProjectPropertyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AidasProjectProperty result = aidasProjectPropertyRepository.save(aidasProjectProperty);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasProjectProperty.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /aidas-project-properties/:id} : Partial updates given fields of an existing aidasProjectProperty, field will ignore if it is null
     *
     * @param id the id of the aidasProjectProperty to save.
     * @param aidasProjectProperty the aidasProjectProperty to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasProjectProperty,
     * or with status {@code 400 (Bad Request)} if the aidasProjectProperty is not valid,
     * or with status {@code 404 (Not Found)} if the aidasProjectProperty is not found,
     * or with status {@code 500 (Internal Server Error)} if the aidasProjectProperty couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-project-properties/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AidasProjectProperty> partialUpdateAidasProjectProperty(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AidasProjectProperty aidasProjectProperty
    ) throws URISyntaxException {
        log.debug("REST request to partial update AidasProjectProperty partially : {}, {}", id, aidasProjectProperty);
        if (aidasProjectProperty.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasProjectProperty.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasProjectPropertyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AidasProjectProperty> result = aidasProjectPropertyRepository
            .findById(aidasProjectProperty.getId())
            .map(existingAidasProjectProperty -> {

                if (aidasProjectProperty.getValue() != null) {
                    existingAidasProjectProperty.setValue(aidasProjectProperty.getValue());
                }

                return existingAidasProjectProperty;
            })
            .map(aidasProjectPropertyRepository::save)
            .map(savedAidasProjectProperty -> {
                aidasProjectPropertySearchRepository.save(savedAidasProjectProperty);

                return savedAidasProjectProperty;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasProjectProperty.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-project-properties} : get all the aidasProjectProperties.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasProjectProperties in body.
     */
    @GetMapping("/aidas-project-properties")
    public ResponseEntity<List<AidasProjectProperty>> getAllAidasProjectProperties(Pageable pageable) {
        log.debug("REST request to get a page of AidasProjectProperties");
        Page<AidasProjectProperty> page = aidasProjectPropertyRepository.findAllByAidasProjectIdGreaterThan(pageable,-1l);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-project-properties/:id} : get the "id" aidasProjectProperty.
     *
     * @param id the id of the aidasProjectProperty to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasProjectProperty, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-project-properties/{id}")
    public ResponseEntity<AidasProjectProperty> getAidasProjectProperty(@PathVariable Long id) {
        log.debug("REST request to get AidasProjectProperty : {}", id);
        Optional<AidasProjectProperty> aidasProjectProperty = aidasProjectPropertyRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(aidasProjectProperty);
    }

    /**
     * {@code DELETE  /aidas-project-properties/:id} : delete the "id" aidasProjectProperty.
     *
     * @param id the id of the aidasProjectProperty to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aidas-project-properties/{id}")
    public ResponseEntity<Void> deleteAidasProjectProperty(@PathVariable Long id) {
        log.debug("REST request to delete AidasProjectProperty : {}", id);
        AidasProjectProperty aidasProjectProperty = aidasProjectPropertyRepository.getById(id);
        AidasProject aidasProject = aidasProjectProperty.getAidasProject();
        aidasProject.removeAidasProjectProperty(aidasProjectProperty);
        aidasProjectRepository.save(aidasProject);
        aidasProjectPropertyRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-project-properties?query=:query} : search for the aidasProjectProperty corresponding
     * to the query.
     *
     * @param query the query of the aidasProjectProperty search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/aidas-project-properties")
    public ResponseEntity<List<AidasProjectProperty>> searchAidasProjectProperties(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasProjectProperties for query {}", query);
        Page<AidasProjectProperty> page = aidasProjectPropertySearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
