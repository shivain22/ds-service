package com.ainnotate.aidas.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ainnotate.aidas.domain.AidasCustomer;
import com.ainnotate.aidas.repository.AidasCustomerRepository;
import com.ainnotate.aidas.repository.search.AidasCustomerSearchRepository;
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
 * REST controller for managing {@link com.ainnotate.aidas.domain.AidasCustomer}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AidasCustomerResource {

    private final Logger log = LoggerFactory.getLogger(AidasCustomerResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasCustomer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AidasCustomerRepository aidasCustomerRepository;

    private final AidasCustomerSearchRepository aidasCustomerSearchRepository;

    public AidasCustomerResource(
        AidasCustomerRepository aidasCustomerRepository,
        AidasCustomerSearchRepository aidasCustomerSearchRepository
    ) {
        this.aidasCustomerRepository = aidasCustomerRepository;
        this.aidasCustomerSearchRepository = aidasCustomerSearchRepository;
    }

    /**
     * {@code POST  /aidas-customers} : Create a new aidasCustomer.
     *
     * @param aidasCustomer the aidasCustomer to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasCustomer, or with status {@code 400 (Bad Request)} if the aidasCustomer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-customers")
    public ResponseEntity<AidasCustomer> createAidasCustomer(@Valid @RequestBody AidasCustomer aidasCustomer) throws URISyntaxException {
        log.debug("REST request to save AidasCustomer : {}", aidasCustomer);
        if (aidasCustomer.getId() != null) {
            throw new BadRequestAlertException("A new aidasCustomer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AidasCustomer result = aidasCustomerRepository.save(aidasCustomer);
        aidasCustomerSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/aidas-customers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /aidas-customers/:id} : Updates an existing aidasCustomer.
     *
     * @param id the id of the aidasCustomer to save.
     * @param aidasCustomer the aidasCustomer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasCustomer,
     * or with status {@code 400 (Bad Request)} if the aidasCustomer is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aidasCustomer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-customers/{id}")
    public ResponseEntity<AidasCustomer> updateAidasCustomer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AidasCustomer aidasCustomer
    ) throws URISyntaxException {
        log.debug("REST request to update AidasCustomer : {}, {}", id, aidasCustomer);
        if (aidasCustomer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasCustomer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasCustomerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AidasCustomer result = aidasCustomerRepository.save(aidasCustomer);
        aidasCustomerSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasCustomer.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /aidas-customers/:id} : Partial updates given fields of an existing aidasCustomer, field will ignore if it is null
     *
     * @param id the id of the aidasCustomer to save.
     * @param aidasCustomer the aidasCustomer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasCustomer,
     * or with status {@code 400 (Bad Request)} if the aidasCustomer is not valid,
     * or with status {@code 404 (Not Found)} if the aidasCustomer is not found,
     * or with status {@code 500 (Internal Server Error)} if the aidasCustomer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-customers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AidasCustomer> partialUpdateAidasCustomer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AidasCustomer aidasCustomer
    ) throws URISyntaxException {
        log.debug("REST request to partial update AidasCustomer partially : {}, {}", id, aidasCustomer);
        if (aidasCustomer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasCustomer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasCustomerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AidasCustomer> result = aidasCustomerRepository
            .findById(aidasCustomer.getId())
            .map(existingAidasCustomer -> {
                if (aidasCustomer.getName() != null) {
                    existingAidasCustomer.setName(aidasCustomer.getName());
                }
                if (aidasCustomer.getDescription() != null) {
                    existingAidasCustomer.setDescription(aidasCustomer.getDescription());
                }

                return existingAidasCustomer;
            })
            .map(aidasCustomerRepository::save)
            .map(savedAidasCustomer -> {
                aidasCustomerSearchRepository.save(savedAidasCustomer);

                return savedAidasCustomer;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasCustomer.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-customers} : get all the aidasCustomers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasCustomers in body.
     */
    @GetMapping("/aidas-customers")
    public ResponseEntity<List<AidasCustomer>> getAllAidasCustomers(Pageable pageable) {
        log.debug("REST request to get a page of AidasCustomers");
        Page<AidasCustomer> page = aidasCustomerRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-customers/:id} : get the "id" aidasCustomer.
     *
     * @param id the id of the aidasCustomer to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasCustomer, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-customers/{id}")
    public ResponseEntity<AidasCustomer> getAidasCustomer(@PathVariable Long id) {
        log.debug("REST request to get AidasCustomer : {}", id);
        Optional<AidasCustomer> aidasCustomer = aidasCustomerRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(aidasCustomer);
    }

    /**
     * {@code DELETE  /aidas-customers/:id} : delete the "id" aidasCustomer.
     *
     * @param id the id of the aidasCustomer to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aidas-customers/{id}")
    public ResponseEntity<Void> deleteAidasCustomer(@PathVariable Long id) {
        log.debug("REST request to delete AidasCustomer : {}", id);
        aidasCustomerRepository.deleteById(id);
        aidasCustomerSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-customers?query=:query} : search for the aidasCustomer corresponding
     * to the query.
     *
     * @param query the query of the aidasCustomer search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/aidas-customers")
    public ResponseEntity<List<AidasCustomer>> searchAidasCustomers(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasCustomers for query {}", query);
        Page<AidasCustomer> page = aidasCustomerSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
