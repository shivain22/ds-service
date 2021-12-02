package com.ainnotate.aidas.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ainnotate.aidas.domain.AidasCustomer;
import com.ainnotate.aidas.domain.AidasProject;
import com.ainnotate.aidas.domain.AidasUser;
import com.ainnotate.aidas.repository.AidasCustomerRepository;
import com.ainnotate.aidas.repository.AidasProjectRepository;
import com.ainnotate.aidas.repository.AidasUserRepository;
import com.ainnotate.aidas.repository.search.AidasProjectSearchRepository;
import com.ainnotate.aidas.security.AidasAuthoritiesConstants;
import com.ainnotate.aidas.security.SecurityUtils;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
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
import org.springframework.security.access.annotation.Secured;
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

    @Autowired
    private AidasUserRepository aidasUserRepository;

    @Autowired
    private AidasCustomerRepository aidasCustomerRepository;

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
    @Secured({AidasAuthoritiesConstants.ADMIN,AidasAuthoritiesConstants.ORG_ADMIN,AidasAuthoritiesConstants.CUSTOMER_ADMIN})
    @PostMapping("/aidas-projects")
    public ResponseEntity<AidasProject> createAidasProject(@Valid @RequestBody AidasProject aidasProject) throws URISyntaxException {
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to save AidasProject : {}", aidasProject);
        if (aidasProject.getId() != null) {
            throw new BadRequestAlertException("A new aidasProject cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            Optional<AidasCustomer> aidasCustomer = aidasCustomerRepository.findById(aidasProject.getAidasCustomer().getId());
            if(aidasCustomer.isPresent()){
                if(!aidasProject.getAidasCustomer().equals(aidasCustomer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN) ){
            if(aidasUser.getAidasCustomer()!=null && !aidasUser.getAidasCustomer().equals(aidasProject.getAidasCustomer())) {
                throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
            }
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
    @Secured({AidasAuthoritiesConstants.ADMIN,AidasAuthoritiesConstants.ORG_ADMIN,AidasAuthoritiesConstants.CUSTOMER_ADMIN})
    @PutMapping("/aidas-projects/{id}")
    public ResponseEntity<AidasProject> updateAidasProject(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AidasProject aidasProject
    ) throws URISyntaxException {
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
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

        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            Optional<AidasCustomer> aidasCustomer = aidasCustomerRepository.findById(aidasProject.getAidasCustomer().getId());
            if(aidasCustomer.isPresent()){
                if(!aidasProject.getAidasCustomer().equals(aidasCustomer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){
            if(aidasUser.getAidasCustomer()!=null && !aidasUser.getAidasCustomer().equals(aidasProject.getAidasCustomer())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
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
    @Secured({AidasAuthoritiesConstants.ADMIN,AidasAuthoritiesConstants.ORG_ADMIN,AidasAuthoritiesConstants.CUSTOMER_ADMIN})
    @PatchMapping(value = "/aidas-projects/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AidasProject> partialUpdateAidasProject(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AidasProject aidasProject
    ) throws URISyntaxException {
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
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

        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            Optional<AidasCustomer> aidasCustomer = aidasCustomerRepository.findById(aidasProject.getAidasCustomer().getId());
            if(aidasCustomer.isPresent()){
                if(!aidasProject.getAidasCustomer().equals(aidasCustomer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){
            if(aidasUser.getAidasCustomer()!=null && !aidasUser.getAidasCustomer().equals(aidasProject.getAidasCustomer())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
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
                if (aidasProject.getProjectType() != null) {
                    existingAidasProject.setProjectType(aidasProject.getProjectType());
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
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Page<AidasProject> page = aidasProjectRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            page = aidasProjectRepository.findAllByAidasCustomer_AidasOrganisation(pageable,aidasUser.getAidasOrganisation());
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN) && aidasUser.getAidasCustomer()!=null ){
            page = aidasProjectRepository.findAllByAidasCustomer(pageable,aidasUser.getAidasCustomer());
        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN) || aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){

        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.USER)){

        }
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
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Optional<AidasProject> aidasProject = aidasProjectRepository.findById(id);

        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){

        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN) && aidasUser.getAidasCustomer()!=null ){

        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN )|| aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){

        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.USER)){

        }
        return ResponseUtil.wrapOrNotFound(aidasProject);
    }

    /**
     * {@code DELETE  /aidas-projects/:id} : delete the "id" aidasProject.
     *
     * @param id the id of the aidasProject to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @Secured({AidasAuthoritiesConstants.ADMIN,AidasAuthoritiesConstants.ORG_ADMIN,AidasAuthoritiesConstants.CUSTOMER_ADMIN})
    @DeleteMapping("/aidas-projects/{id}")
    public ResponseEntity<Void> deleteAidasProject(@PathVariable Long id) {
        log.debug("REST request to delete AidasProject : {}", id);
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        AidasProject aidasProject = aidasProjectRepository.getById(id);
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            Optional<AidasCustomer> aidasCustomer = aidasCustomerRepository.findById(aidasProject.getAidasCustomer().getId());
            if(aidasCustomer.isPresent()){
                if(!aidasProject.getAidasCustomer().equals(aidasCustomer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){
            if(aidasUser.getAidasCustomer()!=null && !aidasUser.getAidasCustomer().equals(aidasProject.getAidasCustomer())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
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
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Page<AidasProject> page = aidasProjectSearchRepository.search(query, pageable);
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null) {
            Iterator<AidasProject> it = page.getContent().iterator();
            while(it.hasNext()){
                AidasProject aidasProject = it.next();
                if(!aidasProject.getAidasCustomer().getAidasOrganisation().equals(aidasUser.getAidasOrganisation())){
                    it.remove();
                }
            }
        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN) && aidasUser.getAidasCustomer()!=null) {
            Iterator<AidasProject> it = page.getContent().iterator();
            while(it.hasNext()){
                AidasProject aidasProject = it.next();
                if(!aidasProject.getAidasCustomer().equals(aidasUser.getAidasCustomer())){
                    it.remove();
                }
            }
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
