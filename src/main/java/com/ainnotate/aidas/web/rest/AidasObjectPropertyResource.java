package com.ainnotate.aidas.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ainnotate.aidas.domain.AidasObject;
import com.ainnotate.aidas.domain.AidasObjectProperty;
import com.ainnotate.aidas.domain.AidasUser;
import com.ainnotate.aidas.repository.AidasObjectPropertyRepository;
import com.ainnotate.aidas.repository.AidasObjectRepository;
import com.ainnotate.aidas.repository.search.AidasObjectPropertySearchRepository;
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
import org.springframework.dao.DataIntegrityViolationException;
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
 * REST controller for managing {@link com.ainnotate.aidas.domain.AidasObjectProperty}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AidasObjectPropertyResource {

    private final Logger log = LoggerFactory.getLogger(AidasObjectPropertyResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasObjectProperty";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AidasObjectPropertyRepository aidasObjectPropertyRepository;

    @Autowired
    private AidasObjectRepository aidasObjectRepository;

    private final AidasObjectPropertySearchRepository aidasObjectPropertySearchRepository;

    public AidasObjectPropertyResource(
        AidasObjectPropertyRepository aidasObjectPropertyRepository,
        AidasObjectPropertySearchRepository aidasObjectPropertySearchRepository
    ) {
        this.aidasObjectPropertyRepository = aidasObjectPropertyRepository;
        this.aidasObjectPropertySearchRepository = aidasObjectPropertySearchRepository;
    }

    /**
     * {@code POST  /aidas-object-properties} : Create a new aidasObjectProperty.
     *
     * @param aidasObjectProperty the aidasObjectProperty to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasObjectProperty, or with status {@code 400 (Bad Request)} if the aidasObjectProperty has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-object-properties")
    public ResponseEntity<AidasObjectProperty> createAidasObjectProperty(@Valid @RequestBody AidasObjectProperty aidasObjectProperty)
        throws URISyntaxException {
        log.debug("REST request to save AidasObjectProperty : {}", aidasObjectProperty);
        if (aidasObjectProperty.getId() != null) {
            throw new BadRequestAlertException("A new aidasObjectProperty cannot already have an ID", ENTITY_NAME, "idexists");
        }
        try {
            AidasObjectProperty result = aidasObjectPropertyRepository.save(aidasObjectProperty);
            aidasObjectPropertySearchRepository.save(result);
            return ResponseEntity
                .created(new URI("/api/aidas-object-properties/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                .body(result);
        }catch(DataIntegrityViolationException dive){
            throw new BadRequestAlertException("The property is already added to the object.", ENTITY_NAME, "idexists");
        }
    }

    /**
     * {@code PUT  /aidas-object-properties/:id} : Updates an existing aidasObjectProperty.
     *
     * @param id the id of the aidasObjectProperty to save.
     * @param aidasObjectProperty the aidasObjectProperty to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasObjectProperty,
     * or with status {@code 400 (Bad Request)} if the aidasObjectProperty is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aidasObjectProperty couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-object-properties/{id}")
    public ResponseEntity<AidasObjectProperty> updateAidasObjectProperty(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AidasObjectProperty aidasObjectProperty
    ) throws URISyntaxException {
        log.debug("REST request to update AidasObjectProperty : {}, {}", id, aidasObjectProperty);
        if (aidasObjectProperty.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasObjectProperty.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasObjectPropertyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AidasObjectProperty result = aidasObjectPropertyRepository.save(aidasObjectProperty);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasObjectProperty.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /aidas-object-properties/:id} : Partial updates given fields of an existing aidasObjectProperty, field will ignore if it is null
     *
     * @param id the id of the aidasObjectProperty to save.
     * @param aidasObjectProperty the aidasObjectProperty to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasObjectProperty,
     * or with status {@code 400 (Bad Request)} if the aidasObjectProperty is not valid,
     * or with status {@code 404 (Not Found)} if the aidasObjectProperty is not found,
     * or with status {@code 500 (Internal Server Error)} if the aidasObjectProperty couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-object-properties/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AidasObjectProperty> partialUpdateAidasObjectProperty(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AidasObjectProperty aidasObjectProperty
    ) throws URISyntaxException {
        log.debug("REST request to partial update AidasObjectProperty partially : {}, {}", id, aidasObjectProperty);
        if (aidasObjectProperty.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasObjectProperty.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasObjectPropertyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AidasObjectProperty> result = aidasObjectPropertyRepository
            .findById(aidasObjectProperty.getId())
            .map(existingAidasObjectProperty -> {

                if (aidasObjectProperty.getValue() != null) {
                    existingAidasObjectProperty.setValue(aidasObjectProperty.getValue());
                }

                return existingAidasObjectProperty;
            })
            .map(aidasObjectPropertyRepository::save)
            .map(savedAidasObjectProperty -> {
                aidasObjectPropertySearchRepository.save(savedAidasObjectProperty);

                return savedAidasObjectProperty;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasObjectProperty.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-object-properties} : get all the aidasObjectProperties.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasObjectProperties in body.
     */
    @GetMapping("/aidas-object-properties")
    public ResponseEntity<List<AidasObjectProperty>> getAllAidasObjectProperties(Pageable pageable) {
        log.debug("REST request to get a page of AidasObjectProperties");
        Page<AidasObjectProperty> page = aidasObjectPropertyRepository.findAllByAidasObjectIdGreaterThan(pageable,-1l);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-object-properties/:id} : get the "id" aidasObjectProperty.
     *
     * @param id the id of the aidasObjectProperty to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasObjectProperty, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-object-properties/{id}")
    public ResponseEntity<AidasObjectProperty> getAidasObjectProperty(@PathVariable Long id) {
        log.debug("REST request to get AidasObjectProperty : {}", id);
        Optional<AidasObjectProperty> aidasObjectProperty = aidasObjectPropertyRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(aidasObjectProperty);
    }

    /**
     * {@code DELETE  /aidas-object-properties/:id} : delete the "id" aidasObjectProperty.
     *
     * @param id the id of the aidasObjectProperty to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aidas-object-properties/{id}")
    public ResponseEntity<Void> deleteAidasObjectProperty(@PathVariable Long id) {
        log.debug("REST request to delete AidasObjectProperty : {}", id);
        AidasObjectProperty aidasObjectProperty = aidasObjectPropertyRepository.getById(id);
        AidasObject aidasObject = aidasObjectProperty.getAidasObject();
        aidasObject.removeAidasObjectProperty(aidasObjectProperty);
        aidasObjectRepository.save(aidasObject);
        aidasObjectPropertyRepository.deleteById(id);
        //aidasObjectPropertySearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-object-properties?query=:query} : search for the aidasObjectProperty corresponding
     * to the query.
     *
     * @param query the query of the aidasObjectProperty search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/aidas-object-properties")
    public ResponseEntity<List<AidasObjectProperty>> searchAidasObjectProperties(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasObjectProperties for query {}", query);
        Page<AidasObjectProperty> page = aidasObjectPropertySearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
