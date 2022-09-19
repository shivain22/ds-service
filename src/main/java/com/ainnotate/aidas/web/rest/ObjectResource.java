package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.dto.*;
import com.ainnotate.aidas.repository.*;
import com.ainnotate.aidas.repository.search.ObjectSearchRepository;
import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.security.SecurityUtils;
import com.ainnotate.aidas.service.DownloadUploadS3;
import com.ainnotate.aidas.service.ObjectAddingTask;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
 * REST controller for managing {@link Object}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ObjectResource {

    private final Logger log = LoggerFactory.getLogger(ObjectResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasObject";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ObjectRepository objectRepository;

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private UploadRepository uploadRepository;

    private final ObjectSearchRepository aidasObjectSearchRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ObjectPropertyRepository objectPropertyRepository;

    @Autowired
    private UserVendorMappingObjectMappingRepository userVendorMappingObjectMappingRepository;

    @Autowired
    private UserVendorMappingProjectMappingRepository userVendorMappingProjectMappingRepository;

    @Autowired
    private UserVendorMappingRepository userVendorMappingRepository;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ObjectAddingTask objectAddingTask;

    public ObjectResource(ObjectRepository objectRepository, ObjectSearchRepository aidasObjectSearchRepository) {
        this.objectRepository = objectRepository;
        this.aidasObjectSearchRepository = aidasObjectSearchRepository;
    }

    /**
     * {@code POST  /aidas-objects} : Create a new object.
     *
     * @param object the object to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new object, or with status {@code 400 (Bad Request)} if the object has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @PostMapping("/aidas-objects")
    public ResponseEntity<Object> createAidasObject(@Valid @RequestBody Object object) throws URISyntaxException {
        log.debug("REST request to save AidasObject : {}", object);
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        if (object.getId() != null) {
            throw new BadRequestAlertException("A new object cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null ){
            Optional<Customer> customer = customerRepository.findById(object.getProject().getCustomer().getId());
            if(customer.isPresent()){
                if(!object.getProject().getCustomer().equals(customer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN)){
            if(user.getCustomer()!=null && !user.getCustomer().equals(object.getProject().getCustomer())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        object.setDummy(0);
        object.setStatus(1);

        if(object.getBufferPercent()==null){
            object.setBufferPercent(20);
        }
        object.setNumberOfBufferedUploadsRequired(object.getNumberOfUploadsRequired()+(object.getNumberOfUploadsRequired()*(object.getBufferPercent()/100)));
        object.setTotalRequired(object.getNumberOfBufferedUploadsRequired());
        Object result = objectRepository.save(object);
        List<Property> commonProperties = propertyRepository.findAllDefaultPropsOfCustomerAndCategory(object.getProject().getCustomer().getId(), 1l);
        List<Property> categorySpecificProperties = propertyRepository.findAllDefaultPropsOfCustomerAndCategory(object.getProject().getCustomer().getId(), object.getProject().getCategory().getId());
        commonProperties.addAll(categorySpecificProperties);
        for(Property p:commonProperties){
            ObjectProperty op = new ObjectProperty();
            op.setObject(object);
            op.setProperty(p);
            op.setValue(p.getValue());
            op.setOptional(p.getOptional());
            object.addAidasObjectProperty(op);
        }
        result = objectRepository.save(object);
        Project p = projectRepository.getById(result.getProject().getId());
        p.setNumberOfBufferedUploadsdRequired(result.getNumberOfBufferedUploadsRequired());
        p.setNumberOfUploadsRequired(p.getNumberOfUploadsRequired()+result.getNumberOfUploadsRequired());
        p.setTotalRequired(p.getTotalRequired()+result.getNumberOfUploadsRequired());
        projectRepository.save(p);
        objectAddingTask.setObject(result);
        objectAddingTask.setDummy(false);
        objectAddingTask.run();
        return ResponseEntity
            .created(new URI("/api/aidas-objects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }


    /**
     * {@code POST  /aidas-objects/vendormapping/add} : Create a new project.
     *
     * @param objectVendorMappingDTO the objectVendorMappings to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new object, or with status {@code 400 (Bad Request)} if the project has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @PostMapping("/aidas-objects/vendormapping/add")
    public ResponseEntity<String> createAidasObjectAidasVendorMapping(@Valid @RequestBody ObjectVendorMappingDTO objectVendorMappingDTO) throws URISyntaxException {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to map AidasObject to AidasVendor: {}", objectVendorMappingDTO);
        if (objectVendorMappingDTO.getObjectId() == null) {
            throw new BadRequestAlertException("A new project cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Object object = objectRepository.getById(objectVendorMappingDTO.getObjectId());
        for(VendorUserDTO vendorUserDTO:objectVendorMappingDTO.getVendorDTOs()){
            Vendor v = vendorRepository.getById(vendorUserDTO.getVendorId());
            for(UserDTO userDTO: vendorUserDTO.getUserDTOs()){
                User u = userRepository.getById(userDTO.getUserId());
                UserVendorMappingObjectMapping uvmom = userVendorMappingObjectMappingRepository.findByUserObject(userDTO.getUserId(),objectVendorMappingDTO.getObjectId());
                if(uvmom!=null){
                    uvmom.setStatus(userDTO.getStatus());
                }else{
                    uvmom = new UserVendorMappingObjectMapping();
                    UserVendorMapping uvm = new UserVendorMapping();
                    uvm.setVendor(v);
                    uvm.setUser(u);
                    uvm.setStatus(1);
                    uvm = userVendorMappingRepository.save(uvm);
                    uvmom.setUserVendorMapping(uvm);
                    uvmom.setObject(object);
                    uvmom.setStatus(userDTO.getStatus());
                }
                userVendorMappingObjectMappingRepository.save(uvmom);
            }
        }
        return ResponseEntity.ok().body("Successfully mapped vendors to project");
    }




    /**
     * {@code POST  /aidas-objects/{id}} : Update aidas Object property to default value.
     *
     * @param id the object id to update object property to default value.
     * @return the {@link ResponseEntity} with status {@code 201 (Updated)} and with body the new object, or with status {@code 400 (Bad Request)} if the object has no ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @PostMapping("/aidas-objects/{id}")
    public ResponseEntity<Object> resetObjectPropertiesToDefaultValues(@PathVariable(value = "id", required = false) final Long id) throws URISyntaxException {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();

        log.debug("REST request to save AidasProjectProperties to default value : {}", id);
        if (id == null) {
            throw new BadRequestAlertException("A new project cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Object object = objectRepository.getById(id);
        if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null ){
            Optional<Customer> customer = customerRepository.findById(object.getProject().getCustomer().getId());
            if(customer.isPresent()){
                if(!object.getProject().getCustomer().equals(customer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN)){
            if(user.getCustomer()!=null && !user.getCustomer().equals(object.getProject().getCustomer())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        List<Property> aidasProperties = propertyRepository.findAll();
        for(Property ap:aidasProperties){
            for(ObjectProperty app1: object.getObjectProperties()){
                if(app1.getProperty().getId().equals(ap.getId())){
                    app1.setValue(ap.getValue());
                }
            }
        }
        Object result = objectRepository.save(object);
        //aidasProjectSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/aidas-objects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code POST  /aidas-objects/add-all-new-added-property/{id}} : Update aidas Object property to default value.
     *
     * @param id the object id to add new property to  object property.
     * @return the {@link ResponseEntity} with status {@code 201 (Updated)} and with body the new object, or with status {@code 400 (Bad Request)} if the object has no ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @PostMapping("/aidas-objects/add-all-new-added-property/{id}")
    public ResponseEntity<Object> addAllNewlyAddedProperties(@PathVariable(value = "id", required = false) final Long id) throws URISyntaxException {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();

        log.debug("REST request to save AidasProjectProperties to default value : {}", id);
        if (id == null) {
            throw new BadRequestAlertException("A new project cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Object object = objectRepository.getById(id);
        if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null ){
            Optional<Customer> customer = customerRepository.findById(object.getProject().getCustomer().getId());
            if(customer.isPresent()){
                if(!object.getProject().getCustomer().equals(customer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN)){
            if(user.getCustomer()!=null && !user.getCustomer().equals(object.getProject().getCustomer())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        List<Property> aidasProperties = propertyRepository.findAll();
        List<Property> addedAidasProperties = new ArrayList();
        for(ObjectProperty app1: object.getObjectProperties()){
            addedAidasProperties.add(app1.getProperty());
        }
        aidasProperties.removeAll(addedAidasProperties);
        for(Property ap:aidasProperties){
            ObjectProperty app = new ObjectProperty();
            app.setObject(object);
            app.setProperty(ap);
            app.setValue(ap.getValue());
            object.addAidasObjectProperty(app);
        }
        Object result = objectRepository.save(object);
        //aidasProjectSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/aidas-objects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /aidas-objects/:id} : Updates an existing object.
     *
     * @param id the id of the object to save.
     * @param object the object to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated object,
     * or with status {@code 400 (Bad Request)} if the object is not valid,
     * or with status {@code 500 (Internal Server Error)} if the object couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @PutMapping("/aidas-objects/{id}")
    public ResponseEntity<Object> updateAidasObject(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Object object
    ) throws URISyntaxException {
        log.debug("REST request to update AidasObject : {}, {}", id, object);
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        if (object.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, object.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!objectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null ){
            Customer customer = customerRepository.getById(object.getProject().getCustomer().getId());
            //if(customer.isPresent()){
                if(!object.getProject().getCustomer().getId().equals(customer.getId())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
           // }else{
               // throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            //}
        }
        if( user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN)){
            if(user.getCustomer()!=null && !user.getCustomer().equals(object.getProject().getCustomer())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }

        Object existingObject = objectRepository.getById(object.getId());
        existingObject.setName(object.getName());
        existingObject.setObjectProperties(object.getObjectProperties());
        existingObject.setDescription(object.getDescription());
        existingObject.setProject(object.getProject());
        existingObject.setBufferPercent(object.getBufferPercent());
        existingObject.setNumberOfUploadsRequired(object.getNumberOfUploadsRequired());
        existingObject.setAudioType(object.getAudioType());
        existingObject.setVideoType(object.getVideoType());
        existingObject.setImageType(object.getImageType());
        Object result = objectRepository.save(existingObject);
        aidasObjectSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, object.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /aidas-objects/:id} : Partial updates given fields of an existing object, field will ignore if it is null
     *
     * @param id the id of the object to save.
     * @param object the object to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated object,
     * or with status {@code 400 (Bad Request)} if the object is not valid,
     * or with status {@code 404 (Not Found)} if the object is not found,
     * or with status {@code 500 (Internal Server Error)} if the object couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-objects/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Object> partialUpdateAidasObject(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Object object
    ) throws URISyntaxException {
        log.debug("REST request to partial update AidasObject partially : {}, {}", id, object);
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        if (object.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, object.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!objectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        /*if(user.getAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && user.getOrganisation()!=null ){
            Optional<AidasCustomer> customer = aidasCustomerRepository.findById(object.getProject().getCustomer().getId());
            if(customer.isPresent()){
                if(!object.getProject().getCustomer().equals(customer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( user.getAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){
            if(user.getCustomer()!=null && !user.getCustomer().equals(object.getProject().getCustomer())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }*/
        Optional<Object> result = objectRepository
            .findById(object.getId())
            .map(existingAidasObject -> {
                if (object.getName() != null) {
                    existingAidasObject.setName(object.getName());
                }
                if (object.getDescription() != null) {
                    existingAidasObject.setDescription(object.getDescription());
                }
                if (object.getNumberOfUploadsRequired() != null) {
                    existingAidasObject.setNumberOfUploadsRequired(object.getNumberOfUploadsRequired());
                }

                return existingAidasObject;
            })
            .map(objectRepository::save)
            .map(savedAidasObject -> {
                aidasObjectSearchRepository.save(savedAidasObject);

                return savedAidasObject;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, object.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-objects} : get all the aidasObjects.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasObjects in body.
     */

    @GetMapping("/aidas-objects")
    public ResponseEntity<List<Object>> getAllAidasObjects(Pageable pageable) {
        log.debug("REST request to get a page of AidasObjects");
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Page<Object> page =null;
        if(user.getAuthority().getName().equals(AidasConstants.ADMIN)){
            page = objectRepository.findAllByIdGreaterThan(0l,pageable);
        }
        if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null ){
            page = objectRepository.findAllByAidasProject_AidasCustomer_AidasOrganisation(pageable, user.getOrganisation().getId());
        }
        if( user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) && user.getCustomer()!=null ){
            page = objectRepository.findAllByAidasProject_AidasCustomer(pageable, user.getCustomer().getId());
        }
        if(user.getAuthority().getName().equals(AidasConstants.VENDOR_ADMIN) || user.getAuthority().getName().equals(AidasConstants.VENDOR_USER)){
            page = objectRepository.findAllObjectsByVendorAdmin(pageable, user.getVendor());
        }
        if(user.getAuthority().getName().equals(AidasConstants.VENDOR_USER)){
            page = objectRepository.findAllObjectsByVendorUser(pageable, user);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/aidas-projects/{id}/aidas-objects/dropdown")
    public ResponseEntity<List<ObjectDTO>> getAllAidasObjectsOfProjectForDropdown( @PathVariable(value = "id", required = false) final Long projectId) {
        log.debug("REST request to get a page of AidasObjects");
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        List<ObjectDTO> objects =objectRepository.getAllObjectDTOsOfProject(projectId);
        return ResponseEntity.ok().body(objects);
    }


    @GetMapping("/aidas-projects/{id}/aidas-objects")
    public ResponseEntity<List<Object>> getAllAidasObjectsOfProject(Pageable pageable, @PathVariable(value = "id", required = false) final Long projectId) {
        log.debug("REST request to get a page of AidasObjects");
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        List<Object> objects =null;
        if(user.getAuthority().getName().equals(AidasConstants.ADMIN)){
            objects = objectRepository.getAllObjectsOfProject(projectId);
            if(objects!=null && objects.size()>0){
                for(Object object : objects){
                    IUploadDetail ud = objectRepository.countUploadsByObject(object.getId());
                    object.setTotalUploaded(ud.getTotalUploaded());
                    object.setTotalApproved(ud.getTotalApproved());
                    object.setTotalRejected(ud.getTotalRejected());
                    object.setTotalPending(ud.getTotalPending());
                }
            }
        }
        if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null ){
            objects = objectRepository.getAllObjectsOfProject(projectId);
            if(objects!=null && objects.size()>0){
                for(Object object : objects){
                    IUploadDetail ud = objectRepository.countUploadsByObject(object.getId());
                    if(ud.getTotalUploaded()!=null)
                        object.setTotalUploaded(ud.getTotalUploaded());
                    if(ud.getTotalApproved()!=null)
                        object.setTotalApproved(ud.getTotalApproved());
                    if(ud.getTotalRejected()!=null)
                        object.setTotalRejected(ud.getTotalRejected());
                    if(ud.getTotalPending()!=null)
                        object.setTotalPending(ud.getTotalPending());
                }
            }
        }
        if( user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) && user.getCustomer()!=null ){
            objects = objectRepository.getAllByAidasProject_AidasCustomerAndAidasProject_Id(user.getCustomer().getId(),projectId);
            if(objects!=null && objects.size()>0){
                for(Object object : objects){
                    IUploadDetail ud = objectRepository.countUploadsByObject(object.getId());
                    object.setTotalUploaded(ud.getTotalUploaded());
                    object.setTotalApproved(ud.getTotalApproved());
                    object.setTotalRejected(ud.getTotalRejected());
                    object.setTotalPending(ud.getTotalPending());
                }
            }
        }
        if(user.getAuthority().getName().equals(AidasConstants.VENDOR_ADMIN) ){
            objects = objectRepository.getAllObjectsByVendorAdminProject(user.getVendor().getId());
            if(objects!=null && objects.size()>0){
                for(Object object : objects){
                    IUploadDetail ud = objectRepository.countUploadsByObject( object.getId());
                    object.setTotalUploaded(ud.getTotalUploaded());
                    object.setTotalApproved(ud.getTotalApproved());
                    object.setTotalRejected(ud.getTotalRejected());
                    object.setTotalPending(ud.getTotalPending());
                }
            }
        }
        if( user.getAuthority().getName().equals(AidasConstants.VENDOR_USER)){
            /*objects = objectRepository.getAllObjectsByVendorUserProject(pageable,user.getId());
            if(objects!=null && objects.size()>0){
                for(Object object : objects){
                    Integer uploadsCompleted = uploadRepository.countAidasUploadByAidasUserAidasObjectMapping_AidasObject(object.getId());
                    object.setUploadsCompleted(uploadsCompleted);
                    Integer uploadsRemaining = (object.getNumberOfUploadReqd()+((object.getNumberOfUploadReqd()*(object.getBufferPercent())/100))-uploadsCompleted);
                    object.setUploadsRemaining(uploadsRemaining);
                    IUploadDetail ud = objectRepository.countUploadsByObjectAndUser(user.getId(), object.getId());
                    object.setTotalUploaded(ud.getTotalUploaded());
                    object.setTotalApproved(ud.getTotalApproved());
                    object.setTotalRejected(ud.getTotalRejected());
                    object.setTotalPending(ud.getTotalPending());
                }
            }*/
        }
        return ResponseEntity.ok().body(objects);
    }



    @GetMapping("/aidas-projects/{id}/aidas-objects/details")
    public ResponseEntity<List<ObjectDTO>> getAllAidasObjectsOfProjectForVendorUser(Pageable pageable, @PathVariable(value = "id", required = false) final Long projectId) {
        log.debug("REST request to get a page of AidasObjects");
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Page<ObjectDTO> page = null;
        Project project = projectRepository.getById(projectId);
        UserVendorMapping uvm = userVendorMappingRepository.findByVendorIdAndUserId(user.getVendor().getId(),user.getId());
        UserVendorMappingProjectMapping uvmpm = userVendorMappingProjectMappingRepository.findByUserVendorMappingIdProjectId(uvm.getId(),projectId);
        ObjectMapper mapper = new ObjectMapper();
        UvmomBatchMappingsDTO uvmomBatchMappingsDTO = new UvmomBatchMappingsDTO();
        UvmomBatchMappingDTO uvmomBatchMappingDTO = new UvmomBatchMappingDTO();
        Integer batchNumber =1;
        List<Object> objects1 =null;
        boolean unChanged = false;
        try {
            if (project.getAutoCreateObjects().equals(1)) {
                Integer objectsRemaining = objectRepository.getObjectNotAllocatedYet(projectId);
                if (uvmpm.getUvmomIds() != null) {
                    uvmomBatchMappingsDTO = mapper.readValue(uvmpm.getUvmomIds(), UvmomBatchMappingsDTO.class);
                }
                if (uvmomBatchMappingsDTO.getUvmoms() != null && uvmomBatchMappingsDTO.getUvmoms().size() > 0) {
                    uvmomBatchMappingsDTO.getUvmoms().sort(Comparator.comparing(UvmomBatchMappingDTO::getBatchNumber).reversed());
                    uvmomBatchMappingDTO = uvmomBatchMappingsDTO.getUvmoms().get(0);
                    batchNumber = uvmomBatchMappingDTO.getBatchNumber();
                }
                if(objectsRemaining>0) {
                    int i = 0;
                    if (uvmomBatchMappingDTO != null && uvmomBatchMappingDTO.getUserVendorMappingObjectMappingIds() != null && uvmomBatchMappingDTO.getUserVendorMappingObjectMappingIds().size() > 0) {
                        for (Long uvmomId : uvmomBatchMappingDTO.getUserVendorMappingObjectMappingIds()) {
                            UserVendorMappingObjectMapping uvmom = userVendorMappingObjectMappingRepository.getById(uvmomId);
                            if (uvmom.getTotalUploaded().equals(uvmom.getObject().getNumberOfUploadsRequired())) {
                                i++;
                            }
                        }
                        if (i == uvmomBatchMappingDTO.getUserVendorMappingObjectMappingIds().size()) {
                            System.out.println("All uploadsd required for the reserved objects are completed.  So lets fetch new set of data.");
                            PageRequest pages = PageRequest.of(pageable.getPageNumber(), project.getNumberOfObjectsCanBeAssignedToVendorUser(), pageable.getSort());
                            page = objectRepository.getAllObjectsByVendorUserProjectWithProjectId(pages, user.getId(), projectId);
                            if(page.getContent().size()==0){
                                System.out.println("No more uploads are available so let us always send the complete list which are completed and assigned....");
                                pages = PageRequest.of(pageable.getPageNumber(), project.getNumberOfObjectsCanBeAssignedToVendorUser(), pageable.getSort());
                                List<Long>  tempUvmomIds = new ArrayList<>();
                                for(UvmomBatchMappingDTO uvmbmdto:uvmomBatchMappingsDTO.getUvmoms()){
                                    tempUvmomIds.addAll(uvmbmdto.getUserVendorMappingObjectMappingIds());
                                }
                                page = objectRepository.getAllObjectsByVendorUserProjectWithProjectIdAndObjectAlreadyAssigned(pages, user.getId(), projectId, tempUvmomIds);
                                HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
                                return ResponseEntity.ok().headers(headers).body(page.getContent());
                            }else {
                                uvmomBatchMappingDTO = new UvmomBatchMappingDTO();
                                uvmomBatchMappingDTO.setBatchNumber(batchNumber + 1);
                                HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
                                return ResponseEntity.ok().headers(headers).body(page.getContent());
                            }
                        } else {
                            System.out.println("Not all object uploads are completed");
                            PageRequest pages = PageRequest.of(pageable.getPageNumber(), project.getNumberOfObjectsCanBeAssignedToVendorUser(), pageable.getSort());
                            page = objectRepository.getAllObjectsByVendorUserProjectWithProjectIdAndObjectAlreadyAssigned(pages, user.getId(), projectId, uvmomBatchMappingDTO.getUserVendorMappingObjectMappingIds());
                            unChanged = true;
                        }
                    } else {
                        PageRequest pages = PageRequest.of(pageable.getPageNumber(), project.getNumberOfObjectsCanBeAssignedToVendorUser(), pageable.getSort());
                        page = objectRepository.getAllObjectsByVendorUserProjectWithProjectId(pages, user.getId(), projectId);
                        uvmomBatchMappingDTO = new UvmomBatchMappingDTO();
                        uvmomBatchMappingDTO.setBatchNumber(batchNumber);
                        uvmomBatchMappingsDTO.getUvmoms().add(uvmomBatchMappingDTO);
                    }
                    for (ObjectDTO object : page.getContent()) {
                        Object o = objectRepository.getById(object.getId());
                        UserVendorMappingObjectMapping uvmom = userVendorMappingObjectMappingRepository.findByUserObject(user.getId(), object.getId());
                        o.setObjectAcquiredByUvmomId(uvmom.getId());
                        objectRepository.save(o);
                        if(!unChanged) {
                            uvmomBatchMappingDTO.getUserVendorMappingObjectMappingIds().add(uvmom.getId());
                        }
                        List<ObjectProperty> objectProperties = objectPropertyRepository.getAllObjectPropertyForObject(object.getId());
                        object.setObjectProperties(objectProperties);
                    }
                    if (unChanged) {
                        uvmomBatchMappingsDTO.getUvmoms().add(uvmomBatchMappingDTO);
                        List<Long> tempUvmomIds = new ArrayList<>();
                        for (UvmomBatchMappingDTO uvmbmdto : uvmomBatchMappingsDTO.getUvmoms()) {
                            tempUvmomIds.addAll(uvmomBatchMappingDTO.getUserVendorMappingObjectMappingIds());
                        }
                        PageRequest pages = PageRequest.of(pageable.getPageNumber(), project.getNumberOfObjectsCanBeAssignedToVendorUser(), pageable.getSort());
                        Page page1 = objectRepository.getAllObjectsByVendorUserProjectWithProjectIdAndObjectAlreadyAssigned(pages, user.getId(), projectId, tempUvmomIds);
                        page.getContent().addAll(page1.getContent());
                    }
                    uvmpm.setUvmomIds(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(uvmomBatchMappingsDTO));
                    userVendorMappingProjectMappingRepository.save(uvmpm);
                    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
                    return ResponseEntity.ok().headers(headers).body(page.getContent());
                }else{
                    System.out.println("No more uploads are available so let us always send the complete list which are completed and assigned....");
                    PageRequest pages = PageRequest.of(pageable.getPageNumber(), project.getNumberOfObjectsCanBeAssignedToVendorUser(), pageable.getSort());
                    List<Long>  tempUvmomIds = new ArrayList<>();
                    for(UvmomBatchMappingDTO uvmbmdto:uvmomBatchMappingsDTO.getUvmoms()){
                        tempUvmomIds.addAll(uvmbmdto.getUserVendorMappingObjectMappingIds());
                    }
                    page = objectRepository.getAllObjectsByVendorUserProjectWithProjectIdAndObjectAlreadyAssigned(pages, user.getId(), projectId, tempUvmomIds);
                    for (ObjectDTO object : page.getContent()) {
                        Object o = objectRepository.getById(object.getId());
                        UserVendorMappingObjectMapping uvmom = userVendorMappingObjectMappingRepository.findByUserObject(user.getId(), object.getId());
                        o.setObjectAcquiredByUvmomId(uvmom.getId());
                        objectRepository.save(o);
                        if(!unChanged) {
                            uvmomBatchMappingDTO.getUserVendorMappingObjectMappingIds().add(uvmom.getId());
                        }
                        List<ObjectProperty> objectProperties = objectPropertyRepository.getAllObjectPropertyForObject(object.getId());
                        object.setObjectProperties(objectProperties);
                    }

                    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
                    return ResponseEntity.ok().headers(headers).body(page.getContent());
                }
            } else {
                page = objectRepository.getAllObjectsByVendorUserProjectWithProjectId(pageable, user.getId(), projectId);
                for (ObjectDTO object : page.getContent()) {
                    Object o = objectRepository.getById(object.getId());
                    List<ObjectProperty> objectProperties = objectPropertyRepository.getAllObjectPropertyForObject(object.getId());
                    object.setObjectProperties(objectProperties);
                }
                HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
                return ResponseEntity.ok().headers(headers).body(page.getContent());
            }
        }catch(Exception e){
            throw new BadRequestAlertException("Error in fetch object details ", ENTITY_NAME, "object");
        }
    }

    /**
     * {@code GET  /aidas-objects/:id} : get the "id" object.
     *
     * @param id the id of the object to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the object, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-objects/{id}")
    public ResponseEntity<Object> getObject(@PathVariable Long id) {
        log.debug("REST request to get AidasObject : {}", id);
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Optional<Object> object = objectRepository.findById(id);
        if(user.getAuthority().getName().equals(AidasConstants.ADMIN) ){
            return ResponseUtil.wrapOrNotFound(object);
        }
        if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null ){
            if(object.isPresent()){
                if(object.get().getProject().getCustomer().getOrganisation().equals(user.getOrganisation())){
                    return ResponseUtil.wrapOrNotFound(object);
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) && user.getCustomer()!=null ){
            if(object.isPresent()){
                if(object.get().getProject().getCustomer().equals(user.getCustomer())){
                    return ResponseUtil.wrapOrNotFound(object);
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( user.getAuthority().getName().equals(AidasConstants.VENDOR_ADMIN)){
            Integer count = userVendorMappingObjectMappingRepository.getCountOfAidasObjectMappingForVendorAdmin(user.getVendor().getId(),id);
            if(count>0){
                if(object.isPresent()){
                    return ResponseUtil.wrapOrNotFound(object);
                }else{
                    throw new BadRequestAlertException("The object is not assigned to this vendor admin", ENTITY_NAME, "idexists");
                }
            }
            throw new BadRequestAlertException("The object is not assigned to this vendor admin", ENTITY_NAME, "idexists");
        }
        if( user.getAuthority().getName().equals(AidasConstants.VENDOR_USER)){
            UserVendorMappingObjectMapping auao =  userVendorMappingObjectMappingRepository.findByUserObject(user.getId(),id);
            if(auao!=null){
                if(object.isPresent()){
                        return ResponseUtil.wrapOrNotFound(object);
                }else{
                    throw new BadRequestAlertException("The object is not assigned to this vendor user", ENTITY_NAME, "idexists");
                }
            }
            throw new BadRequestAlertException("The object is not assigned to this vendor user", ENTITY_NAME, "idexists");
        }

        throw new BadRequestAlertException("The object is not assigned  user", ENTITY_NAME, "idexists");

    }


    /**
     * {@code GET  /aidas-objects/:id} : get the "id" object.
     *
     * @param objectId the id of the object to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the object, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-objects/assinged-users/{objectId}")
    public ResponseEntity<List<User>> getAssignedAidasUser(@PathVariable Long objectId) {
        log.debug("REST request to get assigned users for AidasObject : {}", objectId);
        List<User> assignedUsers = userRepository.getUsersByAssignedToObject(objectId);
        return ResponseEntity.ok().body(assignedUsers);
    }

    /**
     * {@code GET  /aidas-objects/:id} : get the "id" object.
     *
     * @param objectId the id of the object to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the object, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-objects/unassinged-users/{objectId}")
    public ResponseEntity<List<User>> getUnAssignedAidasUser(@PathVariable Long objectId) {
        log.debug("REST request to get unassigned users for AidasObject : {}", objectId);
        List<User> unAssignedUsers = userRepository.getUsersByNotAssignedToObject(objectId);
        return ResponseEntity.ok().body(unAssignedUsers);
    }


    /**
     * {@code DELETE  /aidas-objects/:id} : delete the "id" object.
     *
     * @param id the id of the object to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @DeleteMapping("/aidas-objects/{id}")
    public ResponseEntity<Void> deleteAidasObject(@PathVariable Long id) {
        log.debug("REST request to delete AidasObject : {}", id);
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Object object = objectRepository.getById(id);
        //aidasObjectRepository.deleteById(id);
        //aidasObjectSearchRepository.deleteById(id);
        if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null ){
            Optional<Customer> customer = customerRepository.findById(object.getProject().getCustomer().getId());
            if(customer.isPresent()){
                if(!object.getProject().getCustomer().equals(customer.get())){
                    throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "idexists");
                }
            }else{
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN)){
            if(user.getCustomer()!=null && !user.getCustomer().equals(object.getProject().getCustomer())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if(object !=null) {
            object.setStatus(0);
            objectRepository.save(object);
        }
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-objects?query=:query} : search for the object corresponding
     * to the query.
     *
     * @param query the query of the object search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/aidas-objects")
    public ResponseEntity<List<Object>> searchAidasObjects(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasObjects for query {}", query);
        Page<Object> page = aidasObjectSearchRepository.search(query, pageable);
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null) {
            Iterator<Object> it = page.getContent().iterator();
            while(it.hasNext()){
                Object object = it.next();
                if(!object.getProject().getCustomer().getOrganisation().equals(user.getOrganisation())){
                    it.remove();
                }
            }
        }
        if(user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) && user.getCustomer()!=null) {
            Iterator<Object> it = page.getContent().iterator();
            while(it.hasNext()){
                Object object = it.next();
                if(!object.getProject().getCustomer().equals(user.getCustomer())){
                    it.remove();
                }
            }
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }



    @Autowired
    DownloadUploadS3 downloadUploadS3;
    /**
     * {@code GET  /download/:id/:status} : download objects with the "id" object and provided status.  User "all" for download both.
     *
     * @param id the id of the object to retrieve.
     * @param status the id of the upload objects to retrieve and download.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the object, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/download/object/{id}/{status}")
    public void downloadUploadedObjectsOfObject(@PathVariable("id") Long id,@PathVariable("status") String status){
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        downloadUploadS3.setUser(user);
        Object object = objectRepository.getById(id);
        downloadUploadS3.setUp(object,status);
        taskExecutor.execute(downloadUploadS3);
    }
}
