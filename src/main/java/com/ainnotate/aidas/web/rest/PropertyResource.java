package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.AppProperty;
import com.ainnotate.aidas.domain.Property;
import com.ainnotate.aidas.repository.AppPropertyRepository;
import com.ainnotate.aidas.repository.PropertyRepository;
import com.ainnotate.aidas.repository.search.PropertySearchRepository;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link Property}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PropertyResource {

    private final Logger log = LoggerFactory.getLogger(PropertyResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasProperties";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PropertyRepository propertyRepository;

    @Autowired
    private AppPropertyRepository appPropertyRepository;
    private final PropertySearchRepository aidasPropertiesSearchRepository;

    public PropertyResource(
        PropertyRepository propertyRepository,
        PropertySearchRepository aidasPropertiesSearchRepository
    ) {
        this.propertyRepository = propertyRepository;
        this.aidasPropertiesSearchRepository = aidasPropertiesSearchRepository;
    }

    /**
     * {@code POST  /aidas-property} : Create a new aidasProperties.
     *
     * @param property the aidasProperties to create. For propertyType use values "metadata" or "property"
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasProperties, or with status {@code 400 (Bad Request)} if the aidasProperties has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-property")
    public ResponseEntity<Property> createAidasProperties(@Valid @RequestBody Property property)
        throws URISyntaxException {
        log.debug("REST request to save AidasProperties : {}", property);
        if (property.getId() != null) {
            throw new BadRequestAlertException("A new aidasProperties cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Property result = propertyRepository.save(property);
        aidasPropertiesSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/aidas-property/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /aidas-property/:id} : Updates an existing aidasProperties.
     *
     * @param id the id of the aidasProperties to save.
     * @param property the aidasProperties to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasProperties,
     * or with status {@code 400 (Bad Request)} if the aidasProperties is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aidasProperties couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-property/{id}")
    public ResponseEntity<Property> updateAidasProperties(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Property property
    ) throws URISyntaxException {
        log.debug("REST request to update AidasProperties : {}, {}", id, property);
        if (property.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, property.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!propertyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Property result = propertyRepository.save(property);
        aidasPropertiesSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, property.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /aidas-property/:id} : Partial updates given fields of an existing aidasProperties, field will ignore if it is null
     *
     * @param id the id of the aidasProperties to save.
     * @param property the aidasProperties to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasProperties,
     * or with status {@code 400 (Bad Request)} if the aidasProperties is not valid,
     * or with status {@code 404 (Not Found)} if the aidasProperties is not found,
     * or with status {@code 500 (Internal Server Error)} if the aidasProperties couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-property/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Property> partialUpdateAidasProperties(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Property property
    ) throws URISyntaxException {
        log.debug("REST request to partial update AidasProperties partially : {}, {}", id, property);
        if (property.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, property.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!propertyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Property> result = propertyRepository
            .findById(property.getId())
            .map(existingAidasProperties -> {
                if (property.getName() != null) {
                    existingAidasProperties.setName(property.getName());
                }
                if (property.getValue() != null) {
                    existingAidasProperties.setValue(property.getValue());
                }
                if (property.getSystemProperty() != null) {
                    existingAidasProperties.setSystemProperty(property.getSystemProperty());
                }
                if (property.getOptional() != null) {
                    existingAidasProperties.setOptional(property.getOptional());
                }
                if (property.getDescription() != null) {
                    existingAidasProperties.setDescription(property.getDescription());
                }

                return existingAidasProperties;
            })
            .map(propertyRepository::save)
            .map(savedAidasProperties -> {
                aidasPropertiesSearchRepository.save(savedAidasProperties);

                return savedAidasProperties;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, property.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-property} : get all the aidasProperties.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasProperties in body.
     */
    @GetMapping("/aidas-property")
    public ResponseEntity<List<Property>> getAllProperties(Pageable pageable) {
        log.debug("REST request to get a page of AidasProperties");
        Page<Property> page = propertyRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-property} : get all the aidasProperties.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasProperties in body.
     */
    @GetMapping("/aidas-property/all")
    public ResponseEntity<List<Property>> getAllProperties() {
        log.debug("REST request to get a page of AidasProperties");
        List<Property> aidasProperties = propertyRepository.findAll();
        return ResponseEntity.ok().body(aidasProperties);
    }

    /**
     * {@code GET  /aidas-property} : get all the aidasProperties with specific type.
     *
     * @param pageable the pagination information.
     * @param propertyType the type of property required (1=property , 2=metadata)
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasProperties in body.
     */
    @GetMapping("/aidas-property/type/{propertyType}")
    public ResponseEntity<List<Property>> getAllPropertiesByType(Pageable pageable, @PathVariable(value = "propertyType", required = false) final Long propertyType) {
        log.debug("REST request to get a page of AidasProperties");

        Page<Property> page = propertyRepository.findAllByPropertyTypeEquals(pageable,propertyType);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-app-property/option/{projectType}} : get all the options for given project option (image, view, both).
     *
     * @param projectType projectType for which opttions required
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasProperties in body.
     */
    @GetMapping("/aidas-app-property/option/{option}")
    public ResponseEntity<List<AppProperty>> getProjectTypeOptions(@PathVariable(value = "projectType", required = false) final String projectType) {
        log.debug("REST request to get a page of AidasAppProperties");
        List<AppProperty> appProperties = appPropertyRepository.getAppPropertyLike(-1l,projectType);
        return ResponseEntity.ok().body(appProperties);
    }

    /**
     * {@code GET  /aidas-property/:id} : get the "id" aidasProperties.
     *
     * @param id the id of the aidasProperties to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasProperties, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-property/{id}")
    public ResponseEntity<Property> getProperties(@PathVariable Long id) {
        log.debug("REST request to get AidasProperties : {}", id);
        Optional<Property> aidasProperties = propertyRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(aidasProperties);
    }

    /**
     * {@code DELETE  /aidas-property/:id} : delete the "id" aidasProperties.
     *
     * @param id the id of the aidasProperties to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aidas-property/{id}")
    public ResponseEntity<Void> deleteProperties(@PathVariable Long id) {
        log.debug("REST request to delete AidasProperties : {}", id);
        //aidasPropertiesRepository.deleteById(id);
        //aidasPropertiesSearchRepository.deleteById(id);
        Property property = propertyRepository.getById(id);
        if(property !=null){
            property.setStatus(0);
            propertyRepository.save(property);
        }
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-property?query=:query} : search for the aidasProperties corresponding
     * to the query.
     *
     * @param query the query of the aidasProperties search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/aidas-property")
    public ResponseEntity<List<Property>> searchProperties(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasProperties for query {}", query);
        Page<Property> page = aidasPropertiesSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
