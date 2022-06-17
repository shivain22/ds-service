package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.domain.User;
import com.ainnotate.aidas.domain.UserVendorMapping;
import com.ainnotate.aidas.domain.UserVendorMappingObjectMapping;
import com.ainnotate.aidas.dto.UserObjectMappingDto;
import com.ainnotate.aidas.repository.ObjectRepository;
import com.ainnotate.aidas.repository.UserVendorMappingObjectMappingRepository;
import com.ainnotate.aidas.repository.UserVendorMappingRepository;
import com.ainnotate.aidas.repository.UserRepository;
import com.ainnotate.aidas.repository.search.UserObjectMappingSearchRepository;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link UserVendorMappingObjectMapping}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UserVendorMappingObjectMappingResource {

    private final Logger log = LoggerFactory.getLogger(UserVendorMappingObjectMappingResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasUserAidasObjectMapping";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Autowired
    private UserVendorMappingObjectMappingRepository userVendorMappingObjectMappingRepository;

    private final UserObjectMappingSearchRepository aidasUserAidasObjectMappingSearchRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectRepository objectRepository;

    @Autowired
    private UserVendorMappingRepository userVendorMappingRepository;


    public UserVendorMappingObjectMappingResource(

        UserObjectMappingSearchRepository aidasUserAidasObjectMappingSearchRepository
    ) {

        this.aidasUserAidasObjectMappingSearchRepository = aidasUserAidasObjectMappingSearchRepository;
    }

    /**
     * {@code POST  /aidas-user-aidas-object-mappings} : Create a new aidasUserAidasObjectMapping.
     *
     * @param userVendorMappingObjectMapping the aidasUserAidasObjectMapping to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasUserAidasObjectMapping, or with status {@code 400 (Bad Request)} if the aidasUserAidasObjectMapping has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-user-aidas-object-mappings")
    public ResponseEntity<UserVendorMappingObjectMapping> createAidasUserAidasObjectMapping(
        @Valid @RequestBody UserVendorMappingObjectMapping userVendorMappingObjectMapping
    ) throws URISyntaxException {
        log.debug("REST request to save AidasUserAidasObjectMapping : {}", userVendorMappingObjectMapping);
        if (userVendorMappingObjectMapping.getId() != null) {
            throw new BadRequestAlertException("A new aidasUserAidasObjectMapping cannot already have an ID", ENTITY_NAME, "idexists");
        }
        try {
            UserVendorMappingObjectMapping result = userVendorMappingObjectMappingRepository.save(userVendorMappingObjectMapping);
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
    public ResponseEntity<UserVendorMappingObjectMapping> createAidasUserAidasObjectMappingFromIds(@Valid @RequestBody UserObjectMappingDto userAidasObjectMappingDto ) throws URISyntaxException {
        log.debug("REST request to save AidasUserAidasObjectMapping : {}", userAidasObjectMappingDto);
        if (userAidasObjectMappingDto.getAidasUserId() == null) {
            throw new BadRequestAlertException("User id can not be null", ENTITY_NAME, "idexists");
        }
        if (userAidasObjectMappingDto.getAidasObjectId() == null) {
            throw new BadRequestAlertException("Object id can not be null", ENTITY_NAME, "idexists");
        }
        User user = userRepository.getById(userAidasObjectMappingDto.getAidasUserId());
        Object object = objectRepository.getById(userAidasObjectMappingDto.getAidasObjectId());
        UserVendorMapping auavm = userVendorMappingRepository.findByUserAndVendor(userAidasObjectMappingDto.getAidasVendorId(),userAidasObjectMappingDto.getAidasUserId());
        UserVendorMappingObjectMapping userVendorMappingObjectMapping = new UserVendorMappingObjectMapping();
        userVendorMappingObjectMapping.setUserVendorMapping(auavm);
        userVendorMappingObjectMapping.setObject(object);
        userVendorMappingObjectMapping.setDateAssigned(ZonedDateTime.now());
        UserVendorMappingObjectMapping result = userVendorMappingObjectMappingRepository.save(userVendorMappingObjectMapping);
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
            User user = userRepository.getById(userAidasObjectMappingDto.getAidasUserId());
            Object object = objectRepository.getById(userAidasObjectMappingDto.getAidasObjectId());
            UserVendorMapping auavm = userVendorMappingRepository.findByUserAndVendor(userAidasObjectMappingDto.getAidasVendorId(),userAidasObjectMappingDto.getAidasUserId());
            UserVendorMappingObjectMapping userVendorMappingObjectMapping = new UserVendorMappingObjectMapping();
            userVendorMappingObjectMapping.setUserVendorMapping(auavm);
            userVendorMappingObjectMapping.setObject(object);
            userVendorMappingObjectMapping.setDateAssigned(ZonedDateTime.now());
            UserVendorMappingObjectMapping result = userVendorMappingObjectMappingRepository.save(userVendorMappingObjectMapping);
        }
        return ResponseEntity.ok().body("success");
    }

    /**
     * {@code PUT  /aidas-user-aidas-object-mappings/:id} : Updates an existing aidasUserAidasObjectMapping.
     *
     * @param id the id of the aidasUserAidasObjectMapping to save.
     * @param userVendorMappingObjectMapping the aidasUserAidasObjectMapping to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasUserAidasObjectMapping,
     * or with status {@code 400 (Bad Request)} if the aidasUserAidasObjectMapping is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aidasUserAidasObjectMapping couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-user-aidas-object-mappings/{id}")
    public ResponseEntity<UserVendorMappingObjectMapping> updateAidasUserAidasObjectMapping(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserVendorMappingObjectMapping userVendorMappingObjectMapping
    ) throws URISyntaxException {
        log.debug("REST request to update AidasUserAidasObjectMapping : {}, {}", id, userVendorMappingObjectMapping);
        if (userVendorMappingObjectMapping.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userVendorMappingObjectMapping.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userVendorMappingObjectMappingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserVendorMappingObjectMapping result = userVendorMappingObjectMappingRepository.save(userVendorMappingObjectMapping);
        aidasUserAidasObjectMappingSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userVendorMappingObjectMapping.getId().toString())
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
    public ResponseEntity<UserVendorMappingObjectMapping> updateAidasUserAidasObjectMappingDto(
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

        if (!userVendorMappingObjectMappingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        User user = userRepository.getById(userAidasObjectMappingDto.getAidasUserId());
        Object object = objectRepository.getById(userAidasObjectMappingDto.getAidasObjectId());
        UserVendorMappingObjectMapping existing = userVendorMappingObjectMappingRepository.getById(id);
        //existing.setUser(user);
        existing.setObject(object);
        UserVendorMappingObjectMapping result = userVendorMappingObjectMappingRepository.save(existing);
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
     * @param userVendorMappingObjectMapping the aidasUserAidasObjectMapping to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasUserAidasObjectMapping,
     * or with status {@code 400 (Bad Request)} if the aidasUserAidasObjectMapping is not valid,
     * or with status {@code 404 (Not Found)} if the aidasUserAidasObjectMapping is not found,
     * or with status {@code 500 (Internal Server Error)} if the aidasUserAidasObjectMapping couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-user-aidas-object-mappings/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserVendorMappingObjectMapping> partialUpdateAidasUserAidasObjectMapping(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserVendorMappingObjectMapping userVendorMappingObjectMapping
    ) throws URISyntaxException {
        log.debug("REST request to partial update AidasUserAidasObjectMapping partially : {}, {}", id, userVendorMappingObjectMapping);
        if (userVendorMappingObjectMapping.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userVendorMappingObjectMapping.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userVendorMappingObjectMappingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserVendorMappingObjectMapping> result = userVendorMappingObjectMappingRepository
            .findById(userVendorMappingObjectMapping.getId())
            .map(existingAidasUserAidasObjectMapping -> {
                if (userVendorMappingObjectMapping.getDateAssigned() != null) {
                    existingAidasUserAidasObjectMapping.setDateAssigned(userVendorMappingObjectMapping.getDateAssigned());
                }
                if (userVendorMappingObjectMapping.getStatus() != null) {
                    existingAidasUserAidasObjectMapping.setStatus(userVendorMappingObjectMapping.getStatus());
                }

                return existingAidasUserAidasObjectMapping;
            })
            .map(userVendorMappingObjectMappingRepository::save)
            .map(savedAidasUserAidasObjectMapping -> {
                aidasUserAidasObjectMappingSearchRepository.save(savedAidasUserAidasObjectMapping);

                return savedAidasUserAidasObjectMapping;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userVendorMappingObjectMapping.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-user-aidas-object-mappings} : get all the aidasUserAidasObjectMappings.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasUserAidasObjectMappings in body.
     */
    @GetMapping("/aidas-user-aidas-object-mappings")
    public ResponseEntity<List<UserVendorMappingObjectMapping>> getAllAidasUserAidasObjectMappings(Pageable pageable) {
        log.debug("REST request to get a page of AidasUserAidasObjectMappings");
        Page<UserVendorMappingObjectMapping> page = userVendorMappingObjectMappingRepository.findAllMappings(pageable);
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
    public ResponseEntity<UserVendorMappingObjectMapping> getUserVendorMappingObjectMapping(@PathVariable Long id) {
        log.debug("REST request to get AidasUserAidasObjectMapping : {}", id);
        Optional<UserVendorMappingObjectMapping> aidasUserAidasObjectMapping = userVendorMappingObjectMappingRepository.findById(id);
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
        //aidasUserAidasObjectMappingRepository.deleteById(id);
        //aidasUserAidasObjectMappingSearchRepository.deleteById(id);
        UserVendorMappingObjectMapping userVendorMappingObjectMapping = userVendorMappingObjectMappingRepository.getById(id);
        if(userVendorMappingObjectMapping !=null){
            userVendorMappingObjectMapping.setStatus(0);
            userVendorMappingObjectMappingRepository.save(userVendorMappingObjectMapping);
        }
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
    public ResponseEntity<List<UserVendorMappingObjectMapping>> searchAidasUserAidasObjectMappings(
        @RequestParam String query,
        Pageable pageable
    ) {
        log.debug("REST request to search for a page of AidasUserAidasObjectMappings for query {}", query);
        Page<UserVendorMappingObjectMapping> page = aidasUserAidasObjectMappingSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
