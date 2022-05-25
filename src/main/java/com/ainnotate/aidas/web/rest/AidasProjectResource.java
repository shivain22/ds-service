package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.repository.*;
import com.ainnotate.aidas.repository.search.AidasProjectSearchRepository;
import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.security.SecurityUtils;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
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
    private AidasPropertiesRepository aidasPropertiesRepository;

    @Autowired
    private AidasCustomerRepository aidasCustomerRepository;

    @Autowired
    private AidasUploadRepository aidasUploadRepository;

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
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @PostMapping("/aidas-projects")
    public ResponseEntity<AidasProject> createAidasProject(@Valid @RequestBody AidasProject aidasProject) throws URISyntaxException {
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to save AidasProject : {}", aidasProject);
        if (aidasProject.getId() != null) {
            throw new BadRequestAlertException("A new aidasProject cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            Optional<AidasCustomer> aidasCustomer = aidasCustomerRepository.findById(aidasProject.getAidasCustomer().getId());
            if(aidasCustomer.isPresent()){
                if(!aidasProject.getAidasCustomer().equals(aidasCustomer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) ){
            if(aidasUser.getAidasCustomer()!=null && !aidasUser.getAidasCustomer().equals(aidasProject.getAidasCustomer())) {
                throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
            }
        }
        List<AidasProperties> aidasProperties = aidasPropertiesRepository.findAll();
        for(AidasProperties ap:aidasProperties){
            AidasProjectProperty app = new AidasProjectProperty();
            app.setAidasProject(aidasProject);
            app.setAidasProperties(ap);
            app.setValue(ap.getValue());
            aidasProject.addAidasProjectProperty(app);
        }
        AidasProject result = aidasProjectRepository.save(aidasProject);
        //aidasProjectSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/aidas-projects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code POST  /aidas-projects/{id}} : Update aidas Project properties to default value.
     *
     * @param id the aidasProject id to update project properties to default value.
     * @return the {@link ResponseEntity} with status {@code 201 (Updated)} and with body the new aidasProject, or with status {@code 400 (Bad Request)} if the aidasProject has no ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @PostMapping("/aidas-projects/{id}")
    public ResponseEntity<AidasProject> resetProjectPropertiesToDefaultValues(@PathVariable(value = "id", required = false) final Long id) throws URISyntaxException {
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();

        log.debug("REST request to save AidasProjectProperties to default value : {}", id);
        if (id == null) {
            throw new BadRequestAlertException("A new aidasProject cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AidasProject aidasProject = aidasProjectRepository.getById(id);
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            Optional<AidasCustomer> aidasCustomer = aidasCustomerRepository.findById(aidasProject.getAidasCustomer().getId());
            if(aidasCustomer.isPresent()){
                if(!aidasProject.getAidasCustomer().equals(aidasCustomer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) ){
            if(aidasUser.getAidasCustomer()!=null && !aidasUser.getAidasCustomer().equals(aidasProject.getAidasCustomer())) {
                throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
            }
        }
        List<AidasProperties> aidasProperties = aidasPropertiesRepository.findAll();
        for(AidasProperties ap:aidasProperties){
            for(AidasProjectProperty app1:aidasProject.getAidasProjectProperties()){
                if(app1.getAidasProperties().getId().equals(ap.getId())){
                    app1.setValue(ap.getValue());
                }
            }
        }
        AidasProject result = aidasProjectRepository.save(aidasProject);
        //aidasProjectSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/aidas-projects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }


    /**
     * {@code POST  /aidas-projects/add-all-new-added-properties/{id}} : Update aidas Project properties to add new properties.
     *
     * @param id the aidasProject id to add new project properties .
     * @return the {@link ResponseEntity} with status {@code 201 (Updated)} and with body the new aidasProject, or with status {@code 400 (Bad Request)} if the aidasProject has no ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @PostMapping("/aidas-projects/add-all-new-added-properties/{id}")
    public ResponseEntity<AidasProject> addAllNewlyAddedProperties(@PathVariable(value = "id", required = false) final Long id) throws URISyntaxException {
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();

        log.debug("REST request to save AidasProjectProperties to default value : {}", id);
        if (id == null) {
            throw new BadRequestAlertException("A new aidasProject cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AidasProject aidasProject = aidasProjectRepository.getById(id);
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            Optional<AidasCustomer> aidasCustomer = aidasCustomerRepository.findById(aidasProject.getAidasCustomer().getId());
            if(aidasCustomer.isPresent()){
                if(!aidasProject.getAidasCustomer().equals(aidasCustomer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) ){
            if(aidasUser.getAidasCustomer()!=null && !aidasUser.getAidasCustomer().equals(aidasProject.getAidasCustomer())) {
                throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
            }
        }
        List<AidasProperties> aidasProperties = aidasPropertiesRepository.findAll();
        List<AidasProperties> addedAidasProperties = new ArrayList();
        for(AidasProjectProperty app1:aidasProject.getAidasProjectProperties()){
                addedAidasProperties.add(app1.getAidasProperties());
        }
        aidasProperties.removeAll(addedAidasProperties);
        for(AidasProperties ap:aidasProperties){
            for(AidasProjectProperty app1:aidasProject.getAidasProjectProperties()){
                if(app1.getAidasProperties().getId().equals(ap.getId())){
                    app1.setValue(ap.getValue());
                }
            }
        }
        AidasProject result = aidasProjectRepository.save(aidasProject);
        //aidasProjectSearchRepository.save(result);
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
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
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

        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            Optional<AidasCustomer> aidasCustomer = aidasCustomerRepository.findById(aidasProject.getAidasCustomer().getId());
            if(aidasCustomer.isPresent()){
                if(!aidasProject.getAidasCustomer().equals(aidasCustomer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN)){
            if(aidasUser.getAidasCustomer()!=null && !aidasUser.getAidasCustomer().equals(aidasProject.getAidasCustomer())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }

        AidasProject existingAidasProject =aidasProjectRepository.getById(aidasProject.getId());
        existingAidasProject.setName(aidasProject.getName());
        existingAidasProject.setProjectType(aidasProject.getProjectType());
        existingAidasProject.setDescription(aidasProject.getDescription());
        existingAidasProject.setAidasCustomer(aidasProject.getAidasCustomer());
        AidasProject result = aidasProjectRepository.save(existingAidasProject);
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
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
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

        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            Optional<AidasCustomer> aidasCustomer = aidasCustomerRepository.findById(aidasProject.getAidasCustomer().getId());
            if(aidasCustomer.isPresent()){
                if(!aidasProject.getAidasCustomer().equals(aidasCustomer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN)){
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
        Page<AidasProject> page = null;
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.ADMIN)){
            page = aidasProjectRepository.findAllByIdGreaterThan(0l,pageable);
        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            page = aidasProjectRepository.findAllByAidasCustomer_AidasOrganisation(pageable,aidasUser.getAidasOrganisation());
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) && aidasUser.getAidasCustomer()!=null ){
            page = aidasProjectRepository.findAllByAidasCustomer(pageable,aidasUser.getAidasCustomer());
        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.VENDOR_ADMIN)){
            page =  aidasProjectRepository.findAllProjectsByVendorAdmin(pageable,aidasUser.getAidasVendor());
        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.VENDOR_USER)){
            page =  aidasProjectRepository.findAllProjectsByVendorUser(pageable,aidasUser);
            for(AidasProject ap: page.getContent()){
                UploadDetail pu = aidasProjectRepository.countUploadsByProjectAndUser(ap.getId(),aidasUser.getId());
                ap.setTotalUploaded(pu.getTotalUploaded());
                ap.setTotalApproved(pu.getTotalApproved());
                ap.setTotalRejected(pu.getTotalRejected());
                ap.setTotalPending(pu.getTotalPending());
            }
        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.USER)){
            page =  aidasProjectRepository.findAllProjectsByVendorUser(pageable,aidasUser);
        }
        if(page!=null) {
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        }else{
            throw new BadRequestAlertException("Not Authorised", ENTITY_NAME, "idexists");
        }
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
        Optional<AidasProject> aidasProject =null;
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.ADMIN)){
            aidasProject = aidasProjectRepository.findById(id);
        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            aidasProject = aidasProjectRepository.findById(id);
        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            aidasProject = aidasProjectRepository.findAllProjectsByOrgAdminProject(aidasUser.getAidasOrganisation(),id);
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) && aidasUser.getAidasCustomer()!=null ){
            aidasProject = aidasProjectRepository.findAllProjectsByCustomerAdminProject(aidasUser.getAidasCustomer().getId(),id);
        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.VENDOR_ADMIN )|| aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.VENDOR_USER)){
            aidasProject = aidasProjectRepository.findAllProjectsByVendorAdminProject(aidasUser.getAidasVendor(),id);
        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.USER)){
            aidasProject = aidasProjectRepository.findAllProjectsByVendorUserProject(aidasUser,id);
        }
        if(aidasProject.isPresent()) {
            return ResponseUtil.wrapOrNotFound(aidasProject);
        }else{
            throw new BadRequestAlertException("Not Authorised", ENTITY_NAME, "idexists");
        }
    }

    /**
     * {@code DELETE  /aidas-projects/:id} : delete the "id" aidasProject.
     *
     * @param id the id of the aidasProject to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @DeleteMapping("/aidas-projects/{id}")
    public ResponseEntity<Void> deleteAidasProject(@PathVariable Long id) {
        log.debug("REST request to delete AidasProject : {}", id);
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        AidasProject aidasProject = aidasProjectRepository.getById(id);
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            Optional<AidasCustomer> aidasCustomer = aidasCustomerRepository.findById(aidasProject.getAidasCustomer().getId());
            if(aidasCustomer.isPresent()){
                if(!aidasProject.getAidasCustomer().equals(aidasCustomer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN)){
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
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null) {
            Iterator<AidasProject> it = page.getContent().iterator();
            while(it.hasNext()){
                AidasProject aidasProject = it.next();
                if(!aidasProject.getAidasCustomer().getAidasOrganisation().equals(aidasUser.getAidasOrganisation())){
                    it.remove();
                }
            }
        }
        if(aidasUser.getCurrentAidasAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) && aidasUser.getAidasCustomer()!=null) {
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

    @Autowired
    TaskExecutor uploadDownloadTaskExecutor;

    @Autowired
    DownloadUploadS3 downloadUploadS3;
    /**
     * {@code GET  /download/:id/:status} : download objects with the "id" aidasObject and provided status.  User "all" for download both.
     *
     * @param id the id of the aidasObject to retrieve.
     * @param status the id of the upload objects to retrieve and download.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasObject, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/download/project/{id}/{status}")
    public void downloadUploadedObjectsOfProject(@PathVariable("id") Long id,@PathVariable("status") String status){
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        downloadUploadS3.setAidasUser(aidasUser);
        AidasProject aidasProject = aidasProjectRepository.getById(id);
        downloadUploadS3.setUp(aidasProject,status);
        uploadDownloadTaskExecutor.execute(downloadUploadS3);
    }




}
