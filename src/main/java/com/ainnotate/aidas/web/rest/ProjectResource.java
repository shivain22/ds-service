package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.dto.*;
import com.ainnotate.aidas.repository.*;
import com.ainnotate.aidas.repository.search.ProjectSearchRepository;
import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.security.SecurityUtils;
import com.ainnotate.aidas.service.DownloadUploadS3;
import com.ainnotate.aidas.service.ObjectAddingTask;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.support.PagedListHolder;
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
 * REST controller for managing {@link Project}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ProjectResource {

    private final Logger log = LoggerFactory.getLogger(ProjectResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasProject";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectRepository projectRepository;

    @Autowired
    private ProjectPropertyRepository projectPropertyRepository;

    @Autowired
    private QcProjectMappingRepository qcProjectMappingRepository;

    private final ProjectSearchRepository aidasProjectSearchRepository;

    @Autowired
    private UserCustomerMappingRepository userCustomerMappingRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectRepository objectRepository;

    @Autowired
    private UserVendorMappingObjectMappingRepository userVendorMappingObjectMappingRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private UserVendorMappingRepository userVendorMappingRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UploadRepository uploadRepository;

    @Autowired
    private TaskExecutor objectMappingTaskExecutor;

    @Autowired
    private ObjectAddingTask objectAddingTask;



    public ProjectResource(ProjectRepository projectRepository, ProjectSearchRepository aidasProjectSearchRepository) {
        this.projectRepository = projectRepository;
        this.aidasProjectSearchRepository = aidasProjectSearchRepository;
    }

    /**
     * {@code POST  /aidas-projects} : Create a new project.
     *
     * @param project the project to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new project, or with status {@code 400 (Bad Request)} if the project has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @PostMapping("/aidas-projects")
    public ResponseEntity<Project> createAidasProject(@Valid @RequestBody Project project) throws URISyntaxException {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to save AidasProject : {}", project);
        if (project.getId() != null) {
            throw new BadRequestAlertException("A new project cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null ){
            Optional<Customer> customer = customerRepository.findById(project.getCustomer().getId());
            if(customer.isPresent()){
                if(!project.getCustomer().equals(customer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) ){
            if(user.getCustomer()!=null && !user.getCustomer().equals(project.getCustomer())) {
                throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
            }
        }
        if(project.getProjectProperties()!=null){
            Property ap=null;
            for(ProjectProperty app: project.getProjectProperties()){
                if(app.getProperty()!=null && app.getProperty().getId()!=null){
                    ap = propertyRepository.getById(app.getId());
                    app.setProperty(ap);
                    app.setProject(project);
                }else{
                    ap = propertyRepository.save(app.getProperty());
                    app.setProperty(ap);
                    app.setProject(project);
                }
            }
        }

        List<Property> aidasProperties = propertyRepository.findAllDefaultPropsOfCustomer(project.getCustomer().getId());
        for(Property ap:aidasProperties){
            ProjectProperty app = new ProjectProperty();
            app.setProject(project);
            app.setProperty(ap);
            app.setValue(ap.getValue());
            app.setOptional(ap.getOptional());
            app.setAddToMetadata(1);
            app.setPassedFromApp(1);
            app.setStatus(1);
            project.addAidasProjectProperty(app);
        }
        Project result = projectRepository.save(project);
        {
            Object obj = new Object();
            obj.setName(result.getName()+" - Dummy Object");
            if(result.getNumOfUploadsReqd()!=null){
                obj.setNumberOfUploadReqd(result.getNumOfUploadsReqd());
            }else{
                obj.setNumberOfUploadReqd(0);
            }
            obj.setDescription("Dummy object for project "+result.getName());
            obj.setProject(result);
            obj.setBufferPercent(0);
            obj.setDummy(1);
            obj.setStatus(0);
            for(Property ap:aidasProperties){
                ObjectProperty opp = new ObjectProperty();
                opp.setObject(obj);
                opp.setProperty(ap);
                opp.setValue(ap.getValue());
                opp.setOptional(ap.getOptional());
                opp.setAddToMetadata(1);
                opp.setPassedFromApp(1);
                opp.setStatus(1);
                obj.addAidasObjectProperty(opp);
            }
            objectRepository.save(obj);
            objectAddingTask.setDummy(true);
            objectAddingTask.setObject(obj);
            objectMappingTaskExecutor.execute(objectAddingTask);
        }
        if(result.getAutoCreateObjects()!=null && result.getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS)){
            for(int i=0;i<result.getNumOfObjects();i++){
                Object obj = new Object();
                String objName ="";
                if(result.getObjectPrefix()!=null){
                    objName=result.getObjectPrefix();
                }
                objName+=String.valueOf(i);
                if(result.getObjectSuffix()!=null){
                    objName=result.getObjectSuffix();
                }
                obj.setName(result.getObjectPrefix()+"_"+i+"_"+result.getObjectSuffix());
                obj.setNumberOfUploadReqd(result.getNumOfUploadsReqd());
                obj.setDescription(result.getObjectPrefix()+"_"+i+"_"+result.getObjectSuffix());
                obj.setProject(result);
                obj.setBufferPercent(result.getBufferPercent());
                obj.setDummy(0);
                objectRepository.save(obj);
                objectAddingTask.setObject(obj);
                objectMappingTaskExecutor.execute(objectAddingTask);
            }
        }
        //aidasProjectSearchRepository.save(result.getId());
        return ResponseEntity
            .created(new URI("/api/aidas-projects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }


    /**
     * {@code POST  /aidas-projects/qc/add-remove} : Add QC to project.
     *
     * @param projectQcDTO the project to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new project, or with status {@code 400 (Bad Request)} if the project has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @PostMapping("/aidas-projects/qc/add-remove")
    public ResponseEntity<String> addQcToProject(@Valid @RequestBody ProjectQcDTO projectQcDTO) throws URISyntaxException {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to add AidasQcUsers : {}", projectQcDTO);
        for(UserDTO userDTO: projectQcDTO.getQcUsers()){
            //the uservendormappingid coming from the frontend is actually qpc.id -- check the method which fetch list of qc users for project.
            QcProjectMapping qpc = qcProjectMappingRepository.getById(userDTO.getUserCustomerMappingId());
            qpc.setStatus(userDTO.getStatus());
            qpc.setQcLevel(userDTO.getQcLevel());
            qcProjectMappingRepository.save(qpc);
        }
        return ResponseEntity.ok().body("Successfully added project qc level");
    }

    /**
     * {@code POST  /aidas-projects/vendormapping/add-remove} : Create a new project.
     *
     * @param projectVendorMappingDTO the projectVendorMappings to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new project, or with status {@code 400 (Bad Request)} if the project has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @PostMapping("/aidas-projects/vendormapping/add-remove")
    public ResponseEntity<String> addRemoveVendorUsersMapping(@Valid @RequestBody ProjectVendorMappingDTO projectVendorMappingDTO) throws URISyntaxException {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to map AidasProject to AidasVendor: {}", projectVendorMappingDTO);
        if (projectVendorMappingDTO.getProjectId() == null) {
            throw new BadRequestAlertException("A new project cannot already have an ID", ENTITY_NAME, "idexists");
        }
        List<Long>userVendorMappingIds = new ArrayList<>();
        Map<Long,Integer>userVendorMappingStatusMap = new HashMap<>();
        for(VendorUserDTO vendorUserDTO:projectVendorMappingDTO.getVendors()){
            for(UserDTO userDTO: vendorUserDTO.getUserDTOs()){
                userVendorMappingIds.add(userDTO.getUserVendorMappingId());
                userVendorMappingStatusMap.put(userDTO.getUserVendorMappingId(),userDTO.getStatus());
            }
        }
        List<UserVendorMappingObjectMapping> uvmoms = userVendorMappingObjectMappingRepository.getAllUserVendorMappingObjectMappingByUserVendorMappingIdsAndObjectId(projectVendorMappingDTO.getProjectId());
        for(UserVendorMappingObjectMapping uvmom:uvmoms){
            uvmom.setStatus(userVendorMappingStatusMap.get(uvmom.getUserVendorMapping().getId()));
        }
        userVendorMappingObjectMappingRepository.saveAll(uvmoms);
        return ResponseEntity.ok().body("Successfully mapped vendors to project");
    }




    /**
     * {@code POST  /aidas-projects/{id}} : Update aidas Project property to default value.
     *
     * @param id the project id to update project property to default value.
     * @return the {@link ResponseEntity} with status {@code 201 (Updated)} and with body the new project, or with status {@code 400 (Bad Request)} if the project has no ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @PostMapping("/aidas-projects/{id}")
    public ResponseEntity<Project> resetProjectPropertiesToDefaultValues(@PathVariable(value = "id", required = false) final Long id) throws URISyntaxException {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();

        log.debug("REST request to save AidasProjectProperties to default value : {}", id);
        if (id == null) {
            throw new BadRequestAlertException("A new project cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Project project = projectRepository.getById(id);
        if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null ){
            Optional<Customer> customer = customerRepository.findById(project.getCustomer().getId());
            if(customer.isPresent()){
                if(!project.getCustomer().equals(customer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) ){
            if(user.getCustomer()!=null && !user.getCustomer().equals(project.getCustomer())) {
                throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
            }
        }
        List<Property> aidasProperties = propertyRepository.findAll();
        for(Property ap:aidasProperties){
            for(ProjectProperty app1: project.getProjectProperties()){
                if(app1.getProperty().getId().equals(ap.getId())){
                    app1.setValue(ap.getValue());
                }
            }
        }
        Project result = projectRepository.save(project);
        //aidasProjectSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/aidas-projects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }


    /**
     * {@code POST  /aidas-projects/add-all-new-added-property/{id}} : Update aidas Project property to add new property.
     *
     * @param id the project id to add new project property .
     * @return the {@link ResponseEntity} with status {@code 201 (Updated)} and with body the new project, or with status {@code 400 (Bad Request)} if the project has no ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @PostMapping("/aidas-projects/add-all-new-added-property/{id}")
    public ResponseEntity<Project> addAllNewlyAddedProperties(@PathVariable(value = "id", required = false) final Long id) throws URISyntaxException {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();

        log.debug("REST request to save AidasProjectProperties to default value : {}", id);
        if (id == null) {
            throw new BadRequestAlertException("A new project cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Project project = projectRepository.getById(id);
        if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null ){
            Optional<Customer> customer = customerRepository.findById(project.getCustomer().getId());
            if(customer.isPresent()){
                if(!project.getCustomer().equals(customer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) ){
            if(user.getCustomer()!=null && !user.getCustomer().equals(project.getCustomer())) {
                throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
            }
        }
        List<Property> aidasProperties = propertyRepository.findAll();
        List<Property> addedAidasProperties = new ArrayList();
        for(ProjectProperty app1: project.getProjectProperties()){
                addedAidasProperties.add(app1.getProperty());
        }
        aidasProperties.removeAll(addedAidasProperties);
        for(Property ap:aidasProperties){
            for(ProjectProperty app1: project.getProjectProperties()){
                if(app1.getProperty().getId().equals(ap.getId())){
                    app1.setValue(ap.getValue());
                }
            }
        }
        Project result = projectRepository.save(project);
        //aidasProjectSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/aidas-projects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /aidas-projects/:id} : Updates an existing project.
     *
     * @param id the id of the project to save.
     * @param project the project to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated project,
     * or with status {@code 400 (Bad Request)} if the project is not valid,
     * or with status {@code 500 (Internal Server Error)} if the project couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @PutMapping("/aidas-projects/{id}")
    public ResponseEntity<Project> updateAidasProject(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Project project
    ) throws URISyntaxException {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to update AidasProject : {}, {}", id, project);
        if (project.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, project.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null ){
            Optional<Customer> customer = customerRepository.findById(project.getCustomer().getId());
            if(customer.isPresent()){
                if(!project.getCustomer().equals(customer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN)){
            if(user.getCustomer()!=null && !user.getCustomer().equals(project.getCustomer())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }

        Project existingProject = projectRepository.getById(project.getId());
        existingProject.setName(project.getName());
        existingProject.setProjectType(project.getProjectType());
        existingProject.setDescription(project.getDescription());
        existingProject.setCustomer(project.getCustomer());
        existingProject.setAutoCreateObjects(project.getAutoCreateObjects());
        existingProject.setReworkStatus(project.getReworkStatus());
        existingProject.setQcLevels(project.getQcLevels());
        existingProject.setExternalDatasetStatus(project.getExternalDatasetStatus());
        existingProject.setAudioType(project.getAudioType());
        existingProject.setVideoType(project.getVideoType());
        existingProject.setImageType(project.getImageType());
        //existingProject.setProjectProperties(project.getProjectProperties());
        Project result = projectRepository.save(existingProject);
        Project projectForSearch = new Project();
        projectForSearch.setId(result.getId());
        //aidasProjectSearchRepository.save(projectForSearch);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, project.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /aidas-projects/:id} : Partial updates given fields of an existing project, field will ignore if it is null
     *
     * @param id the id of the project to save.
     * @param project the project to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated project,
     * or with status {@code 400 (Bad Request)} if the project is not valid,
     * or with status {@code 404 (Not Found)} if the project is not found,
     * or with status {@code 500 (Internal Server Error)} if the project couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @PatchMapping(value = "/aidas-projects/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Project> partialUpdateAidasProject(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Project project
    ) throws URISyntaxException {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to partial update AidasProject partially : {}, {}", id, project);
        if (project.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, project.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null ){
            Optional<Customer> customer = customerRepository.findById(project.getCustomer().getId());
            if(customer.isPresent()){
                if(!project.getCustomer().equals(customer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN)){
            if(user.getCustomer()!=null && !user.getCustomer().equals(project.getCustomer())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        Optional<Project> result = projectRepository
            .findById(project.getId())
            .map(existingAidasProject -> {
                if (project.getName() != null) {
                    existingAidasProject.setName(project.getName());
                }
                if (project.getDescription() != null) {
                    existingAidasProject.setDescription(project.getDescription());
                }
                if (project.getProjectType() != null) {
                    existingAidasProject.setProjectType(project.getProjectType());
                }

                return existingAidasProject;
            })
            .map(projectRepository::save)
            .map(savedAidasProject -> {
                aidasProjectSearchRepository.save(savedAidasProject);

                return savedAidasProject;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, project.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-projects} : get all the aidasProjects.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasProjects in body.
     */

    @GetMapping("/aidas-projects")
    public ResponseEntity<List<Project>> getAllAidasProjects(Pageable pageable) {
        log.debug("REST request to get a page of AidasProjects");
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Page<Project> page = null;
        if(user.getAuthority().getName().equals(AidasConstants.ADMIN)){
            page = projectRepository.findAllByIdGreaterThan(0l,pageable);
        }
        if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null ){
            page = projectRepository.findAllByAidasCustomer_AidasOrganisation(pageable, user.getOrganisation().getId());
        }
        if( user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) && user.getCustomer()!=null ){
            page = projectRepository.findAllByAidasCustomer(pageable, user.getCustomer().getId());
        }
        if(user.getAuthority().getName().equals(AidasConstants.VENDOR_ADMIN)){
            page =  projectRepository.findAllProjectsByVendorAdmin(pageable, user.getVendor().getId());
        }
        if(user.getAuthority().getName().equals(AidasConstants.VENDOR_USER)){
            throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
        }
        if(user.getAuthority().getName().equals(AidasConstants.QC_USER)){
            List<Project>qcProjects = projectRepository.findProjectsForQC(user.getId());
            return ResponseEntity.ok().body(qcProjects);
        }
        if(page!=null) {
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        }else{
            throw new BadRequestAlertException("Not Authorised", ENTITY_NAME, "idexists");
        }
    }

    /**
     * {@code GET  /aidas-projects} : get all the aidasProjects.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasProjects in body.
     */

    @GetMapping("/aidas-projects/dropdown")
    public ResponseEntity<List<Project>> getAllAidasProjectsForDropDown() {
        log.debug("REST request to get a page of AidasProjects");
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        List<Project> page = new ArrayList();
        if(user.getAuthority().getName().equals(AidasConstants.ADMIN)){
            page = projectRepository.findAllByIdGreaterThanForDropDown(0l);
        }
        if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null ){
            page = projectRepository.findAllByAidasCustomer_AidasOrganisationForDropDown(user.getOrganisation().getId());
        }
        if( user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) && user.getCustomer()!=null ){
            page = projectRepository.findAllByAidasCustomerForDropDown( user.getCustomer().getId());
        }
        if(user.getAuthority().getName().equals(AidasConstants.VENDOR_ADMIN)){
            page =  projectRepository.findAllProjectsByVendorAdminDropDown(user.getVendor().getId());
        }
        if(user.getAuthority().getName().equals(AidasConstants.VENDOR_USER)){
            List<ProjectDTO> projects =  projectRepository.findProjectWithUploadCountByUserForDropDown(user.getId());
            for(ProjectDTO pdto:projects){
                Project p =  new Project();
                p.setId(pdto.getId());
                p.setName(pdto.getName());
                page.add(p);
            }
        }
        if(user.getAuthority().getName().equals(AidasConstants.QC_USER)){
            List<Project>qcProjects = projectRepository.findProjectsForQC(user.getId());
            return ResponseEntity.ok().body(qcProjects);
        }
        if(page!=null) {
            return ResponseEntity.ok().body(page);
        }else{
            throw new BadRequestAlertException("Not Authorised", ENTITY_NAME, "idexists");
        }
    }



    /**
     * {@code GET  /aidas-projects} : get all the aidasProjects.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasProjects in body.
     */

    @GetMapping("/aidas-projects/details")
    public ResponseEntity<List<ProjectDTO>> getAllAidasProjectDetails(Pageable pageable) {
        log.debug("REST request to get a page of AidasProjects");
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Page<ProjectDTO> page = null;
        if(user.getAuthority().getName().equals(AidasConstants.VENDOR_USER)){
            List<ProjectDTO> projects =  projectRepository.findProjectWithUploadCountByUser(pageable,user.getId());
            for(ProjectDTO p:projects){
                Integer totalApprovedUploads = projectRepository.countUploadsByProject(p.getId());
                List<ProjectProperty> projectProperties = projectPropertyRepository.findAllProjectProperty(p.getId());
                p.setAidasProjectProperties(projectProperties);
                if(totalApprovedUploads!=null) {
                    p.setTotalRequired(p.getTotalRequired() - totalApprovedUploads);
                }else{
                    p.setTotalRequired(p.getTotalRequired());
                }
            }
            PagedListHolder<ProjectDTO> pages = new PagedListHolder(projects);
            pages.setPage(pageable.getPageNumber()); //set current page number
            pages.setPageSize(pageable.getPageSize()); // set the size of page
            if(pages.getPageList()!=null) {
                return ResponseEntity.ok().body(pages.getPageList());
            }else{
                throw new BadRequestAlertException("Not Authorised", ENTITY_NAME, "idexists");
            }
        }
        throw new BadRequestAlertException("Not Authorised", ENTITY_NAME, "idexists");
    }



    /**
     * {@code GET  /aidas-projects/:id} : get the "id" project.
     *
     * @param id the id of the project to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the project, or with status {@code 404 (Not Found)}.
     */

    @GetMapping("/aidas-projects/{id}")
    public ResponseEntity<Project> getProject(@PathVariable Long id) {
        log.debug("REST request to get AidasProject : {}", id);
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Optional<Project> project =null;
        if(user.getAuthority().getName().equals(AidasConstants.ADMIN)){
            project = projectRepository.findById(id);
        }
        if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null ){
            project = projectRepository.findById(id);
        }
        if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null ){
            project = projectRepository.findAllProjectsByOrgAdminProject(user.getOrganisation().getId(),id);
        }
        if( user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) && user.getCustomer()!=null ){
            project = projectRepository.findAllProjectsByCustomerAdminProject(user.getCustomer().getId(),id);
        }
        if(user.getAuthority().getName().equals(AidasConstants.VENDOR_ADMIN )|| user.getAuthority().getName().equals(AidasConstants.VENDOR_USER)){
            project = projectRepository.findAllProjectsByVendorAdminProject(user.getVendor().getId(),id);
        }

        if(project.isPresent()) {
            return ResponseUtil.wrapOrNotFound(project);
        }else{
            throw new BadRequestAlertException("Not Authorised", ENTITY_NAME, "idexists");
        }
    }

    /**
     * {@code DELETE  /aidas-projects/:id} : delete the "id" project.
     *
     * @param id the id of the project to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @DeleteMapping("/aidas-projects/{id}")
    public ResponseEntity<Void> deleteAidasProject(@PathVariable Long id) {
        log.debug("REST request to delete AidasProject : {}", id);
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Project project = projectRepository.getById(id);
        if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null ){
            Optional<Customer> customer = customerRepository.findById(project.getCustomer().getId());
            if(customer.isPresent()){
                if(!project.getCustomer().equals(customer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN)){
            if(user.getCustomer()!=null && !user.getCustomer().equals(project.getCustomer())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        //aidasProjectRepository.deleteById(id);
        //aidasProjectSearchRepository.deleteById(id);
        if(project !=null){
            project.setStatus(0);
            projectRepository.save(project);
        }
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-projects?query=:query} : search for the project corresponding
     * to the query.
     *
     * @param query the query of the project search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */

    @GetMapping("/_search/aidas-projects")
    public ResponseEntity<List<Project>> searchAidasProjects(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasProjects for query {}", query);
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Page<Project> page = aidasProjectSearchRepository.search(query, pageable);
        if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null) {
            Iterator<Project> it = page.getContent().iterator();
            while(it.hasNext()){
                Project project = it.next();
                if(!project.getCustomer().getOrganisation().equals(user.getOrganisation())){
                    it.remove();
                }
            }
        }
        if(user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) && user.getCustomer()!=null) {
            Iterator<Project> it = page.getContent().iterator();
            while(it.hasNext()){
                Project project = it.next();
                if(!project.getCustomer().equals(user.getCustomer())){
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
     * {@code GET  /download/:id/:status} : download objects with the "id" object and provided status.  User "all" for download both.
     *
     * @param aidasProjectId the id of the object to retrieve.
     * @param status the id of the upload objects to retrieve and download.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the object, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/download/project/{id}/{status}")
    public void downloadUploadedObjectsOfProject(@PathVariable("id") Long aidasProjectId,@PathVariable("status") String status){
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        downloadUploadS3.setUser(user);
        Project project = projectRepository.getById(aidasProjectId);
        downloadUploadS3.setUp(project,status);
        uploadDownloadTaskExecutor.execute(downloadUploadS3);
    }




}
