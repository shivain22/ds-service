package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.dto.*;
import com.ainnotate.aidas.repository.*;
import com.ainnotate.aidas.repository.predicates.ObjectPredicatesBuilder;
import com.ainnotate.aidas.repository.predicates.ProjectPredicatesBuilder;
import com.ainnotate.aidas.repository.search.ObjectSearchRepository;
import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.security.SecurityUtils;
import com.ainnotate.aidas.service.DownloadUploadS3;
import com.ainnotate.aidas.service.ObjectAddingTask;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.dsl.BooleanExpression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

    @Autowired
    DownloadUploadS3 downloadUploadS3;
    
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
        try {
        object.setDummy(0);
        object.setStatus(1);
        Project project = null;
        if(object.getProject()!=null && object.getProject().getId()!=null){
            project = projectRepository.getById(object.getProject().getId());
        }
        if(object.getBufferPercent()==null){
            if(project!=null ) {
                if(project.getBufferPercent()!=null) {
                    object.setBufferPercent(project.getBufferPercent());
                }else{
                    object.setBufferPercent(10);
                }
            }else{
                object.setBufferPercent(10);
            }
        }
        object.setNumberOfBufferedUploadsRequired(object.getNumberOfUploadsRequired()+(object.getNumberOfUploadsRequired()*(object.getBufferPercent()/100)));
        object.setTotalRequired(object.getNumberOfBufferedUploadsRequired());
        Object result = objectRepository.save(object);
        objectRepository.addObjectProperties(project.getId(),project.getCategory().getId(),result.getId());
        project.setNumberOfObjects(project.getNumberOfObjects()+1);
        project.setTotalRequired(project.getTotalRequired()+result.getNumberOfUploadsRequired());
        project.setNumberOfUploadsRequired(project.getNumberOfUploadsRequired()+object.getNumberOfUploadsRequired());
        project.setNumberOfBufferedUploadsdRequired(project.getNumberOfBufferedUploadsdRequired()+object.getNumberOfBufferedUploadsRequired());
        return ResponseEntity
            .created(new URI("/api/aidas-objects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
        }catch(Exception e) {
        	throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "idexists");
        }
    }
    
    
    /**
     * {@code POST  /aidas-objects} : Create a new object.
     *
     * @param object the object to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new object, or with status {@code 400 (Bad Request)} if the object has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @PostMapping("/aidas-objects/new")
    public ResponseEntity<Object> createAidasObjectFromDTO(@Valid @RequestBody CreateObjectDTO createObjectDTO) throws URISyntaxException {
        log.debug("REST request to save AidasObject : {}", createObjectDTO);
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        if (createObjectDTO.getProjectId() == null) {
            throw new BadRequestAlertException("A new object cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Project project = projectRepository.getById(createObjectDTO.getProjectId());
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
        try {
        Object object = new Object();
        object.setDummy(0);
        object.setStatus(1);
        object.setProject(project);
        object.setName(createObjectDTO.getName());
        object.setDescription(createObjectDTO.getDescription());
        object.setNumberOfUploadsRequired(createObjectDTO.getNumberOfUploadsRequired());
        if(object.getBufferPercent()==null){
            if(project!=null ) {
                if(project.getBufferPercent()!=null) {
                    object.setBufferPercent(project.getBufferPercent());
                }else{
                    object.setBufferPercent(10);
                }
            }else{
                object.setBufferPercent(10);
            }
        }
        object.setNumberOfBufferedUploadsRequired(object.getNumberOfUploadsRequired()+(object.getNumberOfUploadsRequired()*(object.getBufferPercent()/100)));
        object.setTotalRequired(object.getNumberOfBufferedUploadsRequired());
        Object result = objectRepository.save(object);
        objectRepository.addObjectProperties(project.getId(),project.getCategory().getId(),result.getId());
        project.setNumberOfObjects(project.getNumberOfObjects()+1);
        project.setTotalRequired(project.getTotalRequired()+result.getNumberOfUploadsRequired());
        project.setNumberOfUploadsRequired(project.getNumberOfUploadsRequired()+object.getNumberOfUploadsRequired());
        project.setNumberOfBufferedUploadsdRequired(project.getNumberOfBufferedUploadsdRequired()+object.getNumberOfBufferedUploadsRequired());
        return ResponseEntity
            .created(new URI("/api/aidas-objects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
        }catch(Exception e) {
        	throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "idexists");
        }
    }

    
    /**
     * {@code POST  /aidas-objects} : Create a new object.
     *
     * @param object the object to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new object, or with status {@code 400 (Bad Request)} if the object has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @PutMapping("/aidas-objects/{id}")
    public ResponseEntity<Object> updateAidasObject(@PathVariable(value = "id", required = false) final Long id,@Valid @RequestBody Object object) throws URISyntaxException {
        log.debug("REST request to save AidasObject : {}", object);
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        
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
        try {
			/*
			 * object.setDummy(0); object.setStatus(1); Project project = null;
			 * if(object.getProject()!=null && object.getProject().getId()!=null){ project =
			 * projectRepository.getById(object.getProject().getId()); }
			 * if(object.getBufferPercent()==null){ if(project!=null ) {
			 * if(project.getBufferPercent()!=null) {
			 * object.setBufferPercent(project.getBufferPercent()); }else{
			 * object.setBufferPercent(10); } }else{ object.setBufferPercent(10); } }
			 * object.setNumberOfBufferedUploadsRequired(object.getNumberOfUploadsRequired()
			 * +(object.getNumberOfUploadsRequired()*(object.getBufferPercent()/100)));
			 * object.setTotalRequired(object.getNumberOfBufferedUploadsRequired()); Object
			 * result = objectRepository.save(object);
			 * objectRepository.addObjectProperties(project.getId(),project.getCategory().
			 * getId(),result.getId());
			 * project.setNumberOfObjects(project.getNumberOfObjects()+1);
			 * project.setTotalRequired(project.getTotalRequired()+result.
			 * getNumberOfUploadsRequired());
			 * project.setNumberOfUploadsRequired(project.getNumberOfUploadsRequired()+
			 * object.getNumberOfUploadsRequired());
			 * project.setNumberOfBufferedUploadsdRequired(project.
			 * getNumberOfBufferedUploadsdRequired()+object.
			 * getNumberOfBufferedUploadsRequired());
			 */
        	Object object1 = objectRepository.getObjectById(object.getId());
        	object1.setName(object.getName());
        	//System.out.println(object1.getName());
        	object1.setDescription(object.getDescription());
        	object1.setObjectDescriptionLink(object.getObjectDescriptionLink());
        	//object1.setTotalRequired((object.getNumberOfUploadsRequired()-object1.getNumberOfUploadsRequired())-object1.getTotalRequired());
        	Object result = objectRepository.save(object1);
        	
        return ResponseEntity
            .created(new URI("/api/aidas-objects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
        }catch(Exception e) {
        	throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "idexists");
        }
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
        return ResponseEntity
            .created(new URI("/api/aidas-objects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
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

    @GetMapping("/aidas-projects/{id}/aidas-objects/dropdownformetadata")
    public ResponseEntity<List<ObjectDTO>> getAllAidasObjectsOfProjectForDropdownForMetadata( @PathVariable(value = "id", required = false) final Long projectId) {
        log.debug("REST request to get a page of AidasObjects");
        List<ObjectDTO> objects = new ArrayList<>();
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        if(user.getAuthority().getName().equals(AidasConstants.ADMIN) ||
        		user.getAuthority().getName().equals(AidasConstants.VENDOR_ADMIN )||
        		user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) ||
        		user.getAuthority().getName().equals(AidasConstants.ADMIN_QC_USER) ||
        		user.getAuthority().getName().equals(AidasConstants.CUSTOMER_QC_USER) ||
        		user.getAuthority().getName().equals(AidasConstants.ORG_QC_USER) ||
        		user.getAuthority().getName().equals(AidasConstants.VENDOR_QC_USER )) {
        	
        	objects = objectRepository.getAllObjectDTOsOfProjectForMetadataForOtherThanVendorUser(projectId);
        	
        }else if(user.getAuthority().getName().equals(AidasConstants.VENDOR_USER)) {
	        List<Vendor> vendors = vendorRepository.getVendors(user.getId(), AidasConstants.VENDOR_USER_ID);
	        if(vendors!=null && vendors.size()>0) {
	        	user.setVendor(vendors.get(0));
		        if(user.getVendor()==null) {
		        	throw new BadRequestAlertException("Vendor is not mapped", ENTITY_NAME, "vendoridnotexists");
		        }
	        }
	        UserVendorMapping uvm = userVendorMappingRepository.findByVendorIdAndUserId(user.getVendor().getId(),user.getId());
	        objects =objectRepository.getAllObjectDTOsOfProjectForMetadata(projectId,uvm.getId());
        }
        
        return ResponseEntity.ok().body(objects);
    }
    
    @GetMapping("/aidas-projects/{id}/aidas-objects")
    public ResponseEntity<List<Object>> getAllAidasObjectsOfProject(Pageable pageable, @PathVariable(value = "id", required = false) final Long projectId) {
        log.debug("REST request to get a page of AidasObjects");
        Page<Object> page = objectRepository.getAllObjectsOfProjectForDisplay(pageable,projectId);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    
    @GetMapping("/aidas-projects/{id}/aidas-objects/details")
    public ResponseEntity<List<ObjectDTO>> getAllAidasObjectsOfProjectForVendorUser(Pageable pageable, @PathVariable(value = "id", required = false) final Long projectId) {
        log.debug("REST request to get a page of AidasObjects");
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        List<Vendor> vendors = vendorRepository.getVendors(user.getId(), AidasConstants.VENDOR_USER_ID);
        if(vendors!=null && vendors.size()>0) {
        	user.setVendor(vendors.get(0));
	        if(user.getVendor()==null) {
	        	throw new BadRequestAlertException("Vendor is not mapped", ENTITY_NAME, "vendoridnotexists");
	        }
        }
        if(user.getVendor()==null) {
        	throw new BadRequestAlertException("Vendor is not mapped", ENTITY_NAME, "vendoridnotexists");
        }
        Page<ObjectDTO> page = null;
        Project project = projectRepository.findById(projectId).get();
        UserVendorMapping uvm = userVendorMappingRepository.findByVendorIdAndUserId(user.getVendor().getId(),user.getId());
        List<ObjectDTO> objList = new ArrayList();
        Integer numOfObjectsAlreadyAssigned = 0;
        if(project.getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS)){
        	numOfObjectsAlreadyAssigned = objectRepository.getAllObjectsByVendorUserProjectWithProjectId(uvm.getId(),projectId,pageable.getPageSize(),0);
        	if(numOfObjectsAlreadyAssigned==0) {
        		objList = objectRepository.getNewObjectsDtoListForGrouped(projectId,pageable.getPageSize());
        		for(ObjectDTO o:objList){
                    UserVendorMappingObjectMapping uvmom = userVendorMappingObjectMappingRepository.findByUserVendorMappingObject(uvm.getId(),o.getId());
                    Object object = objectRepository.getById(o.getId());
                    if(uvmom==null){
                        uvmom  = new UserVendorMappingObjectMapping();
                        uvmom.setUserVendorMapping(uvm);
                        uvmom.setStatus(AidasConstants.STATUS_ENABLED);
                        uvmom.setObject(object);
                        userVendorMappingObjectMappingRepository.save(uvmom);
                    }
                    object.setUserVendorMappingObjectMappingId(uvmom.getId());
                    object.setObjectAcquiredByUvmomId(uvmom.getId());
    	            objectRepository.save(object);
                }
        		page = objectRepository.getExistingForGrouped(pageable,uvm.getId(),projectId);
        	}else {
        		page = objectRepository.getExistingForGrouped(pageable,uvm.getId(),projectId);
        	}
        }else {
        	numOfObjectsAlreadyAssigned = objectRepository.getAllObjectsByVendorUserProjectWithProjectId(uvm.getId(),projectId,pageable.getPageSize(),pageable.getPageNumber());
        	if(numOfObjectsAlreadyAssigned==0) {
        		objList = objectRepository.getNewObjectsDtoListForNonGrouped(projectId,pageable.getPageSize());
        		for(ObjectDTO o:objList){
                    UserVendorMappingObjectMapping uvmom = userVendorMappingObjectMappingRepository.findByUserVendorMappingObject(uvm.getId(),o.getId());
                    Object object = new Object();
                    object.setId(o.getId());
                    if(uvmom==null){
                        uvmom  = new UserVendorMappingObjectMapping();
                        uvmom.setUserVendorMapping(uvm);
                        uvmom.setStatus(AidasConstants.STATUS_ENABLED);
                        
                        uvmom.setObject(object);
                        userVendorMappingObjectMappingRepository.save(uvmom);
                    }
                    if(project.getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS)){
                    	object.setUserVendorMappingObjectMappingId(uvmom.getId());
                    	object.setObjectAcquiredByUvmomId(uvmom.getId());
    	                objectRepository.save(object);
                    }
                }
        		page = objectRepository.getExistingForNonGrouped(pageable,uvm.getId(),projectId);
        	}else {
        		page = objectRepository.getExistingForNonGrouped(pageable,uvm.getId(),projectId);
        	}
        }
        /*for(ObjectDTO o:page.getContent()){
            UserVendorMappingObjectMapping uvmom = userVendorMappingObjectMappingRepository.findByUserVendorMappingObject(uvm.getId(),o.getId());
            Object object = objectRepository.getById(o.getId());
            if(uvmom==null){
                uvmom  = new UserVendorMappingObjectMapping();
                uvmom.setUserVendorMapping(uvm);
                uvmom.setStatus(AidasConstants.AUTO_CREATE_OBJECT_ENABLE);
                uvmom.setObject(object);
                userVendorMappingObjectMappingRepository.save(uvmom);
            }
            if(project.getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS)){
            	object.setUserVendorMappingObjectMappingId(uvmom.getId());
            	object.setObjectAcquiredByUvmomId(uvmom.getId());
                objectRepository.save(object);
            }
        }*/
    	HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
    
    
    
    
    
    @GetMapping("/aidas-projects/{id}/aidas-objects/details/search")
    public ResponseEntity<List<ObjectDTO>> searchAllAidasObjectsOfProjectForVendorUser(Pageable pageable, @PathVariable(value = "id", required = false) final Long projectId,
    		@RequestParam(value = "search", required = false) final String search) {
        log.debug("REST request to get a page of AidasObjects");
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        List<Vendor> vendors = vendorRepository.getVendors(user.getId(), AidasConstants.VENDOR_USER_ID);
        if(vendors!=null && vendors.size()>0) {
        	user.setVendor(vendors.get(0));
	        if(user.getVendor()==null) {
	        	throw new BadRequestAlertException("Vendor is not mapped", ENTITY_NAME, "vendoridnotexists");
	        }
        }
        Page<ObjectDTO> page = null;
        Project project = projectRepository.getById(projectId);
        UserVendorMapping uvm = userVendorMappingRepository.findByVendorIdAndUserId(user.getVendor().getId(),user.getId());
        if(project.getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS)){
        		page = objectRepository.getExistingForGroupedSearch(pageable,uvm.getId(),projectId,search);
        		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
                return ResponseEntity.ok().headers(headers).body(page.getContent());
        	
        }else {
        		page = objectRepository.getExistingForNonGroupedSearch(pageable,uvm.getId(),projectId,search);
        	for(ObjectDTO o:page.getContent()){
                UserVendorMappingObjectMapping uvmom = userVendorMappingObjectMappingRepository.findByUserVendorMappingObject(uvm.getId(),o.getId());
                Object object = objectRepository.getById(o.getId());
                if(uvmom==null){
                    uvmom  = new UserVendorMappingObjectMapping();
                    uvmom.setUserVendorMapping(uvm);
                    uvmom.setStatus(AidasConstants.AUTO_CREATE_OBJECT_ENABLE);
                    uvmom.setObject(object);
                    userVendorMappingObjectMappingRepository.save(uvmom);
                }
                if(project.getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS)){
                	object.setUserVendorMappingObjectMappingId(uvmom.getId());
                	object.setObjectAcquiredByUvmomId(uvmom.getId());
	                objectRepository.save(object);
                }
            }
        	HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
    }
    
    
    
    private void setProps(List<ObjectDTO> objectDTOs) {
    	for(ObjectDTO o:objectDTOs) {
    		o.setObjectProperties(objectPropertyRepository.findAllByObjectId(o.getId()));
    	}
    }

    @GetMapping("/aidas-projects/{id}/aidas-objects/details/fresh-batch")
    public synchronized ResponseEntity<List<ObjectDTO>> getAllAidasObjectsOfProjectForVendorUserFreshBatch(Pageable pageable, @PathVariable(value = "id", required = false) final Long projectId) {
        log.debug("REST request to get a page of AidasObjects");
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Page<ObjectDTO> page = null;
        Project project = projectRepository.getById(projectId);
        UserVendorMapping uvm = userVendorMappingRepository.findByVendorIdAndUserId(user.getVendor().getId(),user.getId());
        List<UserVendorMappingObjectMapping> uvmoms = new LinkedList<>();
        List<Object> objects = new LinkedList<>();
            List<ObjectDTO> objs = objectRepository.getFreshObjects(projectId,pageable.getPageSize());
            PageRequest pageRequest = PageRequest.of(0, pageable.getPageSize());
        	page = new PageImpl<>(objs,pageRequest,objs.size());
            for(ObjectDTO o:page.getContent()){
                UserVendorMappingObjectMapping uvmom = userVendorMappingObjectMappingRepository.findByUserVendorMappingObject(uvm.getId(),o.getId());
                Object object = objectRepository.getById(o.getId());
                if(uvmom==null){
                    uvmom  = new UserVendorMappingObjectMapping();
                    uvmom.setUserVendorMapping(uvm);
                    uvmom.setObject(object);
                    uvmom.setStatus(AidasConstants.AUTO_CREATE_OBJECT_ENABLE);
                    uvmom = userVendorMappingObjectMappingRepository.save(uvmom);
                }
                object.setUserVendorMappingObjectMappingId(uvmom.getId());
                object.setObjectAcquiredByUvmomId(uvmom.getId());
                objects.add(object);
                uvmoms.add(uvmom);
            }
            objectRepository.saveAll(objects);
            userVendorMappingObjectMappingRepository.saveAll(uvmoms);
            
			/*
			 * if(project.getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS))
			 * { page =
			 * objectRepository.getExistingForGrouped(pageable,uvm.getId(),projectId); }
			 * else { page =
			 * objectRepository.getExistingForNonGrouped(pageable,uvm.getId(),projectId); }
			 */
            setProps(page.getContent());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
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
        Object object = objectRepository.getById(id);
        object.setObjectProperties(objectPropertyRepository.findAllByObjectId(object.getId()));
        return ResponseEntity.ok().body(object);
    }

    @GetMapping(value = "/search/objects/{projectId}")
    @ResponseBody
    public ResponseEntity<List<Object>> search(@RequestParam(value = "search") String search, Pageable pageable,@PathVariable(value = "projectId" ) Long projectId) {
        ObjectPredicatesBuilder builder = new ObjectPredicatesBuilder();

        if (search != null) {
            Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
            Matcher matcher = pattern.matcher(search + ",");
            while (matcher.find()) {
                builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
            }
        }
        builder.with("project.id", ":",projectId);
        builder.with("id", ">",0);
        builder.with("dummy", ":",0);
        BooleanExpression exp = builder.build();
        Page<Object> page = objectRepository.findAll(exp,pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
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
        Object object = objectRepository.getById(id);
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
     * {@code GET  /download/:id/:status} : download objects with the "id" object and provided status.  User "all" for download both.
     *
     * @param id the id of the object to retrieve.
     * @param status the id of the upload objects to retrieve and download.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the object, or with status {@code 404 (Not Found)}.
     */
	/*
	 * @GetMapping("/download/object/{id}/{status}") public void
	 * downloadUploadedObjectsOfObject(@PathVariable("id") Long
	 * id,@PathVariable("status") String status){ User user =
	 * userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
	 * downloadUploadS3.setUser(user); Object object = objectRepository.getById(id);
	 * downloadUploadS3.setUp(object,status);
	 * taskExecutor.execute(downloadUploadS3); }
	 */
}
