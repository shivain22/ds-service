package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.AppProperty;
import com.ainnotate.aidas.domain.Organisation;
import com.ainnotate.aidas.domain.User;
import com.ainnotate.aidas.repository.AppPropertyRepository;
import com.ainnotate.aidas.repository.OrganisationRepository;
import com.ainnotate.aidas.repository.UserRepository;
import com.ainnotate.aidas.repository.search.OrganisationSearchRepository;
import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.security.SecurityUtils;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Predicate;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link Organisation}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class OrganisationResource {

    private final Logger log = LoggerFactory.getLogger(OrganisationResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasOrganisation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrganisationRepository organisationRepository;

    @Autowired
    private AppPropertyRepository appPropertyRepository;


    private final OrganisationSearchRepository aidasOrganisationSearchRepository;

    @Autowired
    private UserRepository userRepository;

    public OrganisationResource(
        OrganisationRepository organisationRepository,
        OrganisationSearchRepository aidasOrganisationSearchRepository
    ) {
        this.organisationRepository = organisationRepository;
        this.aidasOrganisationSearchRepository = aidasOrganisationSearchRepository;
    }


    /**
     * {@code POST  /aidas-organisations} : Create a new organisation.
     *
     * @param organisation the organisation to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new organisation, or with status {@code 400 (Bad Request)} if the organisation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(summary = "Create a new Organisation (createAidasOrganisation())")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Created the Organisation successfully",
            content = { @Content(mediaType = "application/json",
                schema = @Schema(implementation = Organisation.class)) }),
        @ApiResponse(responseCode = "400", description = "A new Organisation can not contain an ID",
            content = @Content),
        @ApiResponse(responseCode = "500", description = "Unable to create a new Organisation.  Contact the administrator",
            content = @Content) })
    @Secured(AidasConstants.ADMIN)
    @PostMapping("/aidas-organisations")
    public ResponseEntity<Organisation> createAidasOrganisation(@Valid @RequestBody Organisation organisation)
        throws URISyntaxException {
        log.debug("REST request to save AidasOrganisation : {}", organisation);
        if (organisation.getId() != null) {
            throw new BadRequestAlertException("A new organisation cannot already have an ID", ENTITY_NAME, "idexists");
        }
            Organisation result = organisationRepository.save(organisation);
            Set<AppProperty> appProperties = appPropertyRepository.getAppPropertyOfOrganisation(-1l);
            result.setAppProperties(appProperties);
            return ResponseEntity
                .created(new URI("/api/aidas-organisations/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    /**
     * {@code PUT  /aidas-organisations/:id} : Updates an existing organisation.
     *
     * @param id the id of the organisation to save.
     * @param organisation the organisation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated organisation,
     * or with status {@code 400 (Bad Request)} if the organisation is not valid,
     * or with status {@code 500 (Internal Server Error)} if the organisation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN})
    @PutMapping("/aidas-organisations/{id}")
    public ResponseEntity<Organisation> updateAidasOrganisation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Organisation organisation
    ) throws URISyntaxException {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to update AidasOrganisation : {}, {}", id, organisation);
        if (organisation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, organisation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!organisationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        if( (user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null && !user.getOrganisation().equals(organisation) )){
            throw new BadRequestAlertException("Not Authorised", ENTITY_NAME, "idinvalid");
        }
        try {
            Organisation result = organisationRepository.save(organisation);
            aidasOrganisationSearchRepository.save(result);
            return ResponseEntity
                .ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, organisation.getId().toString()))
                .body(result);
        }catch(Exception e){
            throw new BadRequestAlertException("Error::"+e.getMessage(), ENTITY_NAME, "uqkey");
        }
    }

    /**
     * {@code PATCH  /aidas-organisations/:id} : Partial updates given fields of an existing organisation, field will ignore if it is null
     *
     * @param id the id of the organisation to save.
     * @param organisation the organisation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated organisation,
     * or with status {@code 400 (Bad Request)} if the organisation is not valid,
     * or with status {@code 404 (Not Found)} if the organisation is not found,
     * or with status {@code 500 (Internal Server Error)} if the organisation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN})
    @PatchMapping(value = "/aidas-organisations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Organisation> partialUpdateAidasOrganisation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Organisation organisation
    ) throws URISyntaxException {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to partial update AidasOrganisation partially : {}, {}", id, organisation);
        if (organisation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, organisation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!organisationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        if( user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null && user.getOrganisation().getId()!= organisation.getId() ){
            throw new BadRequestAlertException("Not Authorised", ENTITY_NAME, "idinvalid");
        }
        Optional<Organisation> result = organisationRepository
            .findById(organisation.getId())
            .map(existingAidasOrganisation -> {
                if (organisation.getName() != null) {
                    existingAidasOrganisation.setName(organisation.getName());
                }
                if (organisation.getDescription() != null) {
                    existingAidasOrganisation.setDescription(organisation.getDescription());
                }
                return existingAidasOrganisation;
            })
            .map(organisationRepository::save)
            .map(savedAidasOrganisation -> {
                aidasOrganisationSearchRepository.save(savedAidasOrganisation);
                return savedAidasOrganisation;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, organisation.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-organisations} : get all the aidasOrganisations.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasOrganisations in body.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @GetMapping("/aidas-organisations")
    public ResponseEntity<List<Organisation>> getAllAidasOrganisations(Pageable pageable) {
        log.debug("REST request to get a page of AidasOrganisations");
        Page<Organisation> page=null;
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        if(user.getAuthority().getName().equals(AidasConstants.ADMIN)) {
            page = organisationRepository.findAllByIdGreaterThan(-1l,pageable);
        }
        else{
            if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN)){
                if(user.getOrganisation()!=null){
                    page = organisationRepository.findAllById(user.getOrganisation().getId(),pageable);
                }
            }
            if(user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN)){
                if(user.getCustomer()!=null){
                    page = organisationRepository.findAllByCustomer(user.getCustomer().getOrganisation().getId(),pageable);
                }
            }
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-organisations} : get all the aidasOrganisations.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasOrganisations in body.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @GetMapping("/aidas-organisations/dropdown")
    public ResponseEntity<List<Organisation>> getAllOrganisationsForDropDown() {
        log.debug("REST request to get a page of AidasOrganisations");
        List<Organisation> organisations=new ArrayList<>();
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        if(user.getAuthority().getName().equals(AidasConstants.ADMIN)) {
            organisations = organisationRepository.findAllByIdGreaterThanForDropDown(-1l);
        }
        {
            if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN)){
                if(user.getOrganisation()!=null){
                    organisations.add(user.getOrganisation());
                }
            }
            if(user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN)){
                if(user.getCustomer()!=null){
                    organisations = organisationRepository.findOrgOfCustomer(user.getCustomer().getOrganisation().getId());
                }
            }
        }
        return ResponseEntity.ok().body(organisations);
    }

    /**
     * {@code GET  /aidas-organisations/:id} : get the "id" organisation.
     *
     * @param id the id of the organisation to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the organisation, or with status {@code 404 (Not Found)}.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN})
    @GetMapping("/aidas-organisations/{id}")
    public ResponseEntity<Organisation> getOrganisation(@PathVariable Long id) {
        log.debug("REST request to get AidasOrganisation : {}", id);
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Optional<Organisation> organisation =null;
        if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN)) {
            if (user.getOrganisation() != null && user.getOrganisation().getId() == id) {
                organisation = organisationRepository.findById(id);
            }
        }else if(user.getAuthority().getName().equals(AidasConstants.ADMIN)){
            organisation = organisationRepository.findById(id);
        }
        return ResponseUtil.wrapOrNotFound(organisation);
    }

    /**
     * {@code DELETE  /aidas-organisations/:id} : delete the "id" organisation.
     *
     * @param id the id of the organisation to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @Secured(AidasConstants.ADMIN)
    @DeleteMapping("/aidas-organisations/{id}")
    public ResponseEntity<Void> deleteAidasOrganisation(@PathVariable Long id) {
        log.debug("REST request to delete AidasOrganisation : {}", id);
        Organisation organisation = organisationRepository.getById(id);
        if(organisation !=null) {
            organisation.setStatus(0);
            organisationRepository.save(organisation);
        }
        //aidasOrganisationRepository.deleteById(id);
        //aidasOrganisationSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-organisations?query=:query} : search for the organisation corresponding
     * to the query.
     *
     * @param query the query of the organisation search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN})
    @GetMapping("/_search/aidas-organisations")
    public ResponseEntity<List<Organisation>> searchAidasOrganisations(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasOrganisations for query {}", query);
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Page<Organisation> page = aidasOrganisationSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
