package com.ainnotate.aidas.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ainnotate.aidas.domain.AidasObject;
import com.ainnotate.aidas.domain.AidasUser;
import com.ainnotate.aidas.domain.AidasUserAidasObjectMapping;
import com.ainnotate.aidas.dto.UserObjectMappingDto;
import com.ainnotate.aidas.repository.AidasObjectRepository;
import com.ainnotate.aidas.repository.AidasUserAidasObjectMappingRepository;
import com.ainnotate.aidas.repository.AidasUserRepository;
import com.ainnotate.aidas.repository.search.AidasUserAidasObjectMappingSearchRepository;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.ZonedDateTime;
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
 * REST controller for managing {@link AidasUserAidasObjectMapping}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AidasUserAidasObjectMappingResource {

    private final Logger log = LoggerFactory.getLogger(AidasUserAidasObjectMappingResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasUserAidasObjectMapping";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AidasUserAidasObjectMappingRepository aidasUserAidasObjectMappingRepository;

    private final AidasUserAidasObjectMappingSearchRepository aidasUserAidasObjectMappingSearchRepository;

    @Autowired
    private AidasUserRepository aidasUserRepository;

    @Autowired
    private AidasObjectRepository aidasObjectRepository;


    public AidasUserAidasObjectMappingResource(
        AidasUserAidasObjectMappingRepository aidasUserAidasObjectMappingRepository,
        AidasUserAidasObjectMappingSearchRepository aidasUserAidasObjectMappingSearchRepository
    ) {
        this.aidasUserAidasObjectMappingRepository = aidasUserAidasObjectMappingRepository;
        this.aidasUserAidasObjectMappingSearchRepository = aidasUserAidasObjectMappingSearchRepository;
    }

    /**
     * {@code POST  /aidas-user-aidas-object-mappings} : Create a new aidasUserAidasObjectMapping.
     *
     * @param aidasUserAidasObjectMapping the aidasUserAidasObjectMapping to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasUserAidasObjectMapping, or with status {@code 400 (Bad Request)} if the aidasUserAidasObjectMapping has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-user-aidas-object-mappings")
    public ResponseEntity<AidasUserAidasObjectMapping> createAidasUserAidasObjectMapping(
        @Valid @RequestBody AidasUserAidasObjectMapping aidasUserAidasObjectMapping
    ) throws URISyntaxException {
        log.debug("REST request to save AidasUserAidasObjectMapping : {}", aidasUserAidasObjectMapping);
        if (aidasUserAidasObjectMapping.getId() != null) {
            throw new BadRequestAlertException("A new aidasUserAidasObjectMapping cannot already have an ID", ENTITY_NAME, "idexists");
        }
        try {
            AidasUserAidasObjectMapping result = aidasUserAidasObjectMappingRepository.save(aidasUserAidasObjectMapping);
            aidasUserAidasObjectMappingSearchRepository.save(result);
            return ResponseEntity
                .created(new URI("/api/aidas-user-aidas-object-mappings/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                .body(result);
        }catch(DataIntegrityViolationException dive){
            throw new BadRequestAlertException("There is already a mapping available for selected user and object", ENTITY_NAME, "idexists");
        }
    }

    /**
     * {@code POST  /aidas-user-aidas-object-mappings/dto} : Create a new aidasUserAidasObjectMapping.
     *
     * @param userAidasObjectMappingDto the userObjectMappingDto to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasUserAidasObjectMapping, or with status {@code 400 (Bad Request)} if the aidasUserAidasObjectMapping has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-user-aidas-object-mappings/dto")
    public ResponseEntity<AidasUserAidasObjectMapping> createAidasUserAidasObjectMappingFromIds( @Valid @RequestBody UserObjectMappingDto userAidasObjectMappingDto ) throws URISyntaxException {
        log.debug("REST request to save AidasUserAidasObjectMapping : {}", userAidasObjectMappingDto);
        if (userAidasObjectMappingDto.getAidasUserId() == null) {
            throw new BadRequestAlertException("User id can not be null", ENTITY_NAME, "idexists");
        }
        if (userAidasObjectMappingDto.getAidasObjectId() == null) {
            throw new BadRequestAlertException("Object id can not be null", ENTITY_NAME, "idexists");
        }
        AidasUser aidasUser = aidasUserRepository.getById(userAidasObjectMappingDto.getAidasUserId());
        AidasObject aidasObject = aidasObjectRepository.getById(userAidasObjectMappingDto.getAidasObjectId());
        AidasUserAidasObjectMapping aidasUserAidasObjectMapping = new AidasUserAidasObjectMapping();
        aidasUserAidasObjectMapping.setAidasUser(aidasUser);
        aidasUserAidasObjectMapping.setAidasObject(aidasObject);
        aidasUserAidasObjectMapping.setDateAssigned(ZonedDateTime.now());
        AidasUserAidasObjectMapping result = aidasUserAidasObjectMappingRepository.save(aidasUserAidasObjectMapping);
        return ResponseEntity
            .created(new URI("/api/aidas-user-aidas-object-mappings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code POST  /aidas-user-aidas-object-mappings/dtos} : Create a new aidasUserAidasObjectMapping.
     *
     * @param userAidasObjectMappingDtos the userObjectMappingDtos to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasUserAidasObjectMapping, or with status {@code 400 (Bad Request)} if the aidasUserAidasObjectMapping has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-user-aidas-object-mappings/dtos")
    public ResponseEntity<String> createAidasUserAidasObjectMappings( @Valid @RequestBody List<UserObjectMappingDto> userAidasObjectMappingDtos ) throws URISyntaxException {
        log.debug("REST request to save AidasUserAidasObjectMapping : {}", userAidasObjectMappingDtos);
        for(UserObjectMappingDto userAidasObjectMappingDto:userAidasObjectMappingDtos) {
            if (userAidasObjectMappingDto.getAidasUserId() == null) {
                throw new BadRequestAlertException("User id can not be null", ENTITY_NAME, "idexists");
            }
            if (userAidasObjectMappingDto.getAidasObjectId() == null) {
                throw new BadRequestAlertException("Object id can not be null", ENTITY_NAME, "idexists");
            }
            AidasUser aidasUser = aidasUserRepository.getById(userAidasObjectMappingDto.getAidasUserId());
            AidasObject aidasObject = aidasObjectRepository.getById(userAidasObjectMappingDto.getAidasObjectId());
            AidasUserAidasObjectMapping aidasUserAidasObjectMapping = new AidasUserAidasObjectMapping();
            aidasUserAidasObjectMapping.setAidasUser(aidasUser);
            aidasUserAidasObjectMapping.setAidasObject(aidasObject);
            aidasUserAidasObjectMapping.setDateAssigned(ZonedDateTime.now());
            AidasUserAidasObjectMapping result = aidasUserAidasObjectMappingRepository.save(aidasUserAidasObjectMapping);
        }
        return ResponseEntity.ok().body("success");
    }

    /**
     * {@code PUT  /aidas-user-aidas-object-mappings/:id} : Updates an existing aidasUserAidasObjectMapping.
     *
     * @param id the id of the aidasUserAidasObjectMapping to save.
     * @param aidasUserAidasObjectMapping the aidasUserAidasObjectMapping to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasUserAidasObjectMapping,
     * or with status {@code 400 (Bad Request)} if the aidasUserAidasObjectMapping is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aidasUserAidasObjectMapping couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-user-aidas-object-mappings/{id}")
    public ResponseEntity<AidasUserAidasObjectMapping> updateAidasUserAidasObjectMapping(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AidasUserAidasObjectMapping aidasUserAidasObjectMapping
    ) throws URISyntaxException {
        log.debug("REST request to update AidasUserAidasObjectMapping : {}, {}", id, aidasUserAidasObjectMapping);
        if (aidasUserAidasObjectMapping.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasUserAidasObjectMapping.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasUserAidasObjectMappingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AidasUserAidasObjectMapping result = aidasUserAidasObjectMappingRepository.save(aidasUserAidasObjectMapping);
        aidasUserAidasObjectMappingSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasUserAidasObjectMapping.getId().toString())
            )
            .body(result);
    }


    /**
     * {@code PUT  /aidas-user-aidas-object-mappings/:id} : Updates an existing aidasUserAidasObjectMapping.
     *
     * @param id the id of the aidasUserAidasObjectMapping to save.
     * @param userAidasObjectMappingDto the aidasUserAidasObjectMapping to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasUserAidasObjectMapping,
     * or with status {@code 400 (Bad Request)} if the aidasUserAidasObjectMapping is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aidasUserAidasObjectMapping couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-user-aidas-object-mappings/dto/{id}")
    public ResponseEntity<AidasUserAidasObjectMapping> updateAidasUserAidasObjectMappingDto(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserObjectMappingDto userAidasObjectMappingDto
    ) throws URISyntaxException {
        log.debug("REST request to update AidasUserAidasObjectMapping : {}, {}", id, userAidasObjectMappingDto);
        if (userAidasObjectMappingDto.getAidasUserId() == null) {
            throw new BadRequestAlertException("User id can not be null", ENTITY_NAME, "idexists");
        }
        if (userAidasObjectMappingDto.getAidasObjectId() == null) {
            throw new BadRequestAlertException("Object id can not be null", ENTITY_NAME, "idexists");
        }

        if (!aidasUserAidasObjectMappingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        AidasUser aidasUser = aidasUserRepository.getById(userAidasObjectMappingDto.getAidasUserId());
        AidasObject aidasObject = aidasObjectRepository.getById(userAidasObjectMappingDto.getAidasObjectId());
        AidasUserAidasObjectMapping existing = aidasUserAidasObjectMappingRepository.getById(id);
        existing.setAidasUser(aidasUser);
        existing.setAidasObject(aidasObject);
        AidasUserAidasObjectMapping result = aidasUserAidasObjectMappingRepository.save(existing);
        aidasUserAidasObjectMappingSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString())
            )
            .body(result);
    }

    /**
     * {@code PATCH  /aidas-user-aidas-object-mappings/:id} : Partial updates given fields of an existing aidasUserAidasObjectMapping, field will ignore if it is null
     *
     * @param id the id of the aidasUserAidasObjectMapping to save.
     * @param aidasUserAidasObjectMapping the aidasUserAidasObjectMapping to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasUserAidasObjectMapping,
     * or with status {@code 400 (Bad Request)} if the aidasUserAidasObjectMapping is not valid,
     * or with status {@code 404 (Not Found)} if the aidasUserAidasObjectMapping is not found,
     * or with status {@code 500 (Internal Server Error)} if the aidasUserAidasObjectMapping couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-user-aidas-object-mappings/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AidasUserAidasObjectMapping> partialUpdateAidasUserAidasObjectMapping(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AidasUserAidasObjectMapping aidasUserAidasObjectMapping
    ) throws URISyntaxException {
        log.debug("REST request to partial update AidasUserAidasObjectMapping partially : {}, {}", id, aidasUserAidasObjectMapping);
        if (aidasUserAidasObjectMapping.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasUserAidasObjectMapping.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasUserAidasObjectMappingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AidasUserAidasObjectMapping> result = aidasUserAidasObjectMappingRepository
            .findById(aidasUserAidasObjectMapping.getId())
            .map(existingAidasUserAidasObjectMapping -> {
                if (aidasUserAidasObjectMapping.getDateAssigned() != null) {
                    existingAidasUserAidasObjectMapping.setDateAssigned(aidasUserAidasObjectMapping.getDateAssigned());
                }
                if (aidasUserAidasObjectMapping.getStatus() != null) {
                    existingAidasUserAidasObjectMapping.setStatus(aidasUserAidasObjectMapping.getStatus());
                }

                return existingAidasUserAidasObjectMapping;
            })
            .map(aidasUserAidasObjectMappingRepository::save)
            .map(savedAidasUserAidasObjectMapping -> {
                aidasUserAidasObjectMappingSearchRepository.save(savedAidasUserAidasObjectMapping);

                return savedAidasUserAidasObjectMapping;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasUserAidasObjectMapping.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-user-aidas-object-mappings} : get all the aidasUserAidasObjectMappings.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasUserAidasObjectMappings in body.
     */
    @GetMapping("/aidas-user-aidas-object-mappings")
    public ResponseEntity<List<AidasUserAidasObjectMapping>> getAllAidasUserAidasObjectMappings(Pageable pageable) {
        log.debug("REST request to get a page of AidasUserAidasObjectMappings");
        Page<AidasUserAidasObjectMapping> page = aidasUserAidasObjectMappingRepository.findAllMappings(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-user-aidas-object-mappings/:id} : get the "id" aidasUserAidasObjectMapping.
     *
     * @param id the id of the aidasUserAidasObjectMapping to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasUserAidasObjectMapping, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-user-aidas-object-mappings/{id}")
    public ResponseEntity<AidasUserAidasObjectMapping> getAidasUserAidasObjectMapping(@PathVariable Long id) {
        log.debug("REST request to get AidasUserAidasObjectMapping : {}", id);
        Optional<AidasUserAidasObjectMapping> aidasUserAidasObjectMapping = aidasUserAidasObjectMappingRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(aidasUserAidasObjectMapping);
    }

    /**
     * {@code DELETE  /aidas-user-aidas-object-mappings/:id} : delete the "id" aidasUserAidasObjectMapping.
     *
     * @param id the id of the aidasUserAidasObjectMapping to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aidas-user-aidas-object-mappings/{id}")
    public ResponseEntity<Void> deleteAidasUserAidasObjectMapping(@PathVariable Long id) {
        log.debug("REST request to delete AidasUserAidasObjectMapping : {}", id);
        aidasUserAidasObjectMappingRepository.deleteById(id);
        aidasUserAidasObjectMappingSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-user-aidas-object-mappings?query=:query} : search for the aidasUserAidasObjectMapping corresponding
     * to the query.
     *
     * @param query the query of the aidasUserAidasObjectMapping search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/aidas-user-aidas-object-mappings")
    public ResponseEntity<List<AidasUserAidasObjectMapping>> searchAidasUserAidasObjectMappings(
        @RequestParam String query,
        Pageable pageable
    ) {
        log.debug("REST request to search for a page of AidasUserAidasObjectMappings for query {}", query);
        Page<AidasUserAidasObjectMapping> page = aidasUserAidasObjectMappingSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
