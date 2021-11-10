package com.ainnotate.aidas.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ainnotate.aidas.domain.AidasProject;
import com.ainnotate.aidas.repository.AidasProjectRepository;
import com.ainnotate.aidas.repository.search.AidasProjectSearchRepository;
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
 * REST controller for managing {@link com.ainnotate.aidas.domain.AidasProject}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AidasProjectResource {

    private final Logger log = LoggerFactory.getLogger(AidasProjectResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasProject";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AidasProjectRepository aidasProjectRepository;

    private final AidasProjectSearchRepository aidasProjectSearchRepository;

    public AidasProjectResource(AidasProjectRepository aidasProjectRepository, AidasProjectSearchRepository aidasProjectSearchRepository) {
        this.aidasProjectRepository = aidasProjectRepository;
        this.aidasProjectSearchRepository = aidasProjectSearchRepository;
    }

    /**
     * {@code POST  /aidas-projects} : Create a new aidasProject.
     *
     * @param aidasProject the aidasProject to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasProject, or with status {@code 400 (Bad Request)} if the aidasProject has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-projects")
    public ResponseEntity<AidasProject> createAidasProject(@Valid @RequestBody AidasProject aidasProject) throws URISyntaxException {
        log.debug("REST request to save AidasProject : {}", aidasProject);
        if (aidasProject.getId() != null) {
            throw new BadRequestAlertException("A new aidasProject cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AidasProject result = aidasProjectRepository.save(aidasProject);
        aidasProjectSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/aidas-projects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /aidas-projects/:id} : Updates an existing aidasProject.
     *
     * @param id the id of the aidasProject to save.
     * @param aidasProject the aidasProject to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasProject,
     * or with status {@code 400 (Bad Request)} if the aidasProject is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aidasProject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-projects/{id}")
    public ResponseEntity<AidasProject> updateAidasProject(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AidasProject aidasProject
    ) throws URISyntaxException {
        log.debug("REST request to update AidasProject : {}, {}", id, aidasProject);
        if (aidasProject.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasProject.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasProjectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AidasProject result = aidasProjectRepository.save(aidasProject);
        aidasProjectSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasProject.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /aidas-projects/:id} : Partial updates given fields of an existing aidasProject, field will ignore if it is null
     *
     * @param id the id of the aidasProject to save.
     * @param aidasProject the aidasProject to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasProject,
     * or with status {@code 400 (Bad Request)} if the aidasProject is not valid,
     * or with status {@code 404 (Not Found)} if the aidasProject is not found,
     * or with status {@code 500 (Internal Server Error)} if the aidasProject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-projects/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AidasProject> partialUpdateAidasProject(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AidasProject aidasProject
    ) throws URISyntaxException {
        log.debug("REST request to partial update AidasProject partially : {}, {}", id, aidasProject);
        if (aidasProject.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasProject.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasProjectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AidasProject> result = aidasProjectRepository
            .findById(aidasProject.getId())
            .map(existingAidasProject -> {
                if (aidasProject.getName() != null) {
                    existingAidasProject.setName(aidasProject.getName());
                }
                if (aidasProject.getDescription() != null) {
                    existingAidasProject.setDescription(aidasProject.getDescription());
                }

                return existingAidasProject;
            })
            .map(aidasProjectRepository::save)
            .map(savedAidasProject -> {
                aidasProjectSearchRepository.save(savedAidasProject);

                return savedAidasProject;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasProject.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-projects} : get all the aidasProjects.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasProjects in body.
     */
    @GetMapping("/aidas-projects")
    public ResponseEntity<List<AidasProject>> getAllAidasProjects(Pageable pageable) {
        log.debug("REST request to get a page of AidasProjects");
        Page<AidasProject> page = aidasProjectRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-projects/:id} : get the "id" aidasProject.
     *
     * @param id the id of the aidasProject to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasProject, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-projects/{id}")
    public ResponseEntity<AidasProject> getAidasProject(@PathVariable Long id) {
        log.debug("REST request to get AidasProject : {}", id);
        Optional<AidasProject> aidasProject = aidasProjectRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(aidasProject);
    }

    /**
     * {@code DELETE  /aidas-projects/:id} : delete the "id" aidasProject.
     *
     * @param id the id of the aidasProject to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aidas-projects/{id}")
    public ResponseEntity<Void> deleteAidasProject(@PathVariable Long id) {
        log.debug("REST request to delete AidasProject : {}", id);
        aidasProjectRepository.deleteById(id);
        aidasProjectSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-projects?query=:query} : search for the aidasProject corresponding
     * to the query.
     *
     * @param query the query of the aidasProject search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/aidas-projects")
    public ResponseEntity<List<AidasProject>> searchAidasProjects(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasProjects for query {}", query);
        Page<AidasProject> page = aidasProjectSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
