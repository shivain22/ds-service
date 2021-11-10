package com.ainnotate.aidas.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ainnotate.aidas.domain.AidasOrganisation;
import com.ainnotate.aidas.repository.AidasOrganisationRepository;
import com.ainnotate.aidas.repository.search.AidasOrganisationSearchRepository;
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
 * REST controller for managing {@link com.ainnotate.aidas.domain.AidasOrganisation}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AidasOrganisationResource {

    private final Logger log = LoggerFactory.getLogger(AidasOrganisationResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasOrganisation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AidasOrganisationRepository aidasOrganisationRepository;

    private final AidasOrganisationSearchRepository aidasOrganisationSearchRepository;

    public AidasOrganisationResource(
        AidasOrganisationRepository aidasOrganisationRepository,
        AidasOrganisationSearchRepository aidasOrganisationSearchRepository
    ) {
        this.aidasOrganisationRepository = aidasOrganisationRepository;
        this.aidasOrganisationSearchRepository = aidasOrganisationSearchRepository;
    }

    /**
     * {@code POST  /aidas-organisations} : Create a new aidasOrganisation.
     *
     * @param aidasOrganisation the aidasOrganisation to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasOrganisation, or with status {@code 400 (Bad Request)} if the aidasOrganisation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-organisations")
    public ResponseEntity<AidasOrganisation> createAidasOrganisation(@Valid @RequestBody AidasOrganisation aidasOrganisation)
        throws URISyntaxException {
        log.debug("REST request to save AidasOrganisation : {}", aidasOrganisation);
        if (aidasOrganisation.getId() != null) {
            throw new BadRequestAlertException("A new aidasOrganisation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AidasOrganisation result = aidasOrganisationRepository.save(aidasOrganisation);
        aidasOrganisationSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/aidas-organisations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /aidas-organisations/:id} : Updates an existing aidasOrganisation.
     *
     * @param id the id of the aidasOrganisation to save.
     * @param aidasOrganisation the aidasOrganisation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasOrganisation,
     * or with status {@code 400 (Bad Request)} if the aidasOrganisation is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aidasOrganisation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-organisations/{id}")
    public ResponseEntity<AidasOrganisation> updateAidasOrganisation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AidasOrganisation aidasOrganisation
    ) throws URISyntaxException {
        log.debug("REST request to update AidasOrganisation : {}, {}", id, aidasOrganisation);
        if (aidasOrganisation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasOrganisation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasOrganisationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AidasOrganisation result = aidasOrganisationRepository.save(aidasOrganisation);
        aidasOrganisationSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasOrganisation.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /aidas-organisations/:id} : Partial updates given fields of an existing aidasOrganisation, field will ignore if it is null
     *
     * @param id the id of the aidasOrganisation to save.
     * @param aidasOrganisation the aidasOrganisation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasOrganisation,
     * or with status {@code 400 (Bad Request)} if the aidasOrganisation is not valid,
     * or with status {@code 404 (Not Found)} if the aidasOrganisation is not found,
     * or with status {@code 500 (Internal Server Error)} if the aidasOrganisation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-organisations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AidasOrganisation> partialUpdateAidasOrganisation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AidasOrganisation aidasOrganisation
    ) throws URISyntaxException {
        log.debug("REST request to partial update AidasOrganisation partially : {}, {}", id, aidasOrganisation);
        if (aidasOrganisation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasOrganisation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasOrganisationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AidasOrganisation> result = aidasOrganisationRepository
            .findById(aidasOrganisation.getId())
            .map(existingAidasOrganisation -> {
                if (aidasOrganisation.getName() != null) {
                    existingAidasOrganisation.setName(aidasOrganisation.getName());
                }
                if (aidasOrganisation.getDescription() != null) {
                    existingAidasOrganisation.setDescription(aidasOrganisation.getDescription());
                }

                return existingAidasOrganisation;
            })
            .map(aidasOrganisationRepository::save)
            .map(savedAidasOrganisation -> {
                aidasOrganisationSearchRepository.save(savedAidasOrganisation);

                return savedAidasOrganisation;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasOrganisation.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-organisations} : get all the aidasOrganisations.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasOrganisations in body.
     */
    @GetMapping("/aidas-organisations")
    public ResponseEntity<List<AidasOrganisation>> getAllAidasOrganisations(Pageable pageable) {
        log.debug("REST request to get a page of AidasOrganisations");
        Page<AidasOrganisation> page = aidasOrganisationRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-organisations/:id} : get the "id" aidasOrganisation.
     *
     * @param id the id of the aidasOrganisation to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasOrganisation, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-organisations/{id}")
    public ResponseEntity<AidasOrganisation> getAidasOrganisation(@PathVariable Long id) {
        log.debug("REST request to get AidasOrganisation : {}", id);
        Optional<AidasOrganisation> aidasOrganisation = aidasOrganisationRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(aidasOrganisation);
    }

    /**
     * {@code DELETE  /aidas-organisations/:id} : delete the "id" aidasOrganisation.
     *
     * @param id the id of the aidasOrganisation to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aidas-organisations/{id}")
    public ResponseEntity<Void> deleteAidasOrganisation(@PathVariable Long id) {
        log.debug("REST request to delete AidasOrganisation : {}", id);
        aidasOrganisationRepository.deleteById(id);
        aidasOrganisationSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-organisations?query=:query} : search for the aidasOrganisation corresponding
     * to the query.
     *
     * @param query the query of the aidasOrganisation search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/aidas-organisations")
    public ResponseEntity<List<AidasOrganisation>> searchAidasOrganisations(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasOrganisations for query {}", query);
        Page<AidasOrganisation> page = aidasOrganisationSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
