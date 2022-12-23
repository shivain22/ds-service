package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.dto.*;
import com.ainnotate.aidas.repository.*;
import com.ainnotate.aidas.repository.search.ProjectSearchRepository;
import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.security.SecurityUtils;
import com.ainnotate.aidas.service.CSVHelper;
import com.ainnotate.aidas.service.DownloadUploadS3;
import com.ainnotate.aidas.service.ObjectAddingTask;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;
import org.springframework.core.io.Resource;

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
    private CustomerQcProjectMappingRepository customerQcProjectMappingRepository;

    @Autowired
    private UploadMetaDataRepository uploadMetaDataRepository;

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
    private UserVendorMappingProjectMappingRepository userVendorMappingProjectMappingRepository;
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
    private TaskExecutor taskExecutor;

    @Autowired
    private ObjectAddingTask objectAddingTask;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UploadCustomerQcProjectMappingBatchInfoRepository uploadCustomerQcProjectMappingBatchInfoRepository;

    @Autowired
    private ProjectQcLevelConfigurationsRepository projectQcLevelConfigurationsRepository;

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

        if(project.getObjectAvailabilityStatus()==null){
            project.setObjectAvailabilityStatus(1);
        }
        if(project.getNumberOfObjectsCanBeAssignedToVendorUser()==null){
            project.setNumberOfObjectsCanBeAssignedToVendorUser(5);
        }
            Category category = categoryRepository.getById(project.getCategory().getId());
            if(category!=null){
                project.setCategory(category);
                project.setProjectType(category.getName());
                List<Property> commonProperties = propertyRepository.findAllDefaultPropsOfCustomerAndCategory(project.getCustomer().getId(),1l);
                List<Property> categorySpecificProperties = propertyRepository.findAllDefaultPropsOfCustomerAndCategory(project.getCustomer().getId(),category.getId());
                commonProperties.addAll(categorySpecificProperties);
                for(Property p:commonProperties){
                    ProjectProperty pp = new ProjectProperty();
                    pp.setProject(project);
                    pp.setProperty(p);
                    pp.setValue(p.getValue());
                    pp.setOptional(p.getOptional());
                    pp.setAddToMetadata(p.getAddToMetadata());
                    pp.setPassedFromApp(0);
                    pp.setShowToVendorUser(p.getShowToVendorUser());
                    pp.setStatus(1);
                    pp.setProjectPropertyType(p.getPropertyType());
                    project.addAidasProjectProperty(pp);
                }
            }
            boolean isQcLevelConfigsAdded = false;
            if(project.getProjectQcLevelConfigurations()!=null && project.getProjectQcLevelConfigurations().size()>0){
                    for (ProjectQcLevelConfigurations pqlc : project.getProjectQcLevelConfigurations()) {
                        if(pqlc.getQcLevelAcceptancePercentage()!=null && pqlc.getQcLevelBatchSize()!=null){
                            isQcLevelConfigsAdded = true;
                        }
                        pqlc.setAllocationStrategy(AidasConstants.QC_LEVEL_FCFS);
                        pqlc.setProject(project);
                    }
            }
            if(isQcLevelConfigsAdded) {
                Project result = projectRepository.save(project);
                objectAddingTask.runQcUserAdd();
                {
                    Object obj = new Object();
                    obj.setName(result.getName() + " - Dummy Object");
                    obj.setNumberOfUploadsRequired(0);
                    obj.setDescription("Dummy object for project " + result.getName());
                    obj.setProject(result);
                    obj.setBufferPercent(0);
                    obj.setDummy(1);
                    obj.setStatus(0);
                    if (project.getCategory() != null) {
                        List<Property> commonProperties = propertyRepository.findAllDefaultPropsOfCustomerAndCategory(project.getCustomer().getId(), 1l);
                        List<Property> categorySpecificProperties = propertyRepository.findAllDefaultPropsOfCustomerAndCategory(project.getCustomer().getId(), project.getCategory().getId());
                        commonProperties.addAll(categorySpecificProperties);
                        for (Property p : commonProperties) {
                            ObjectProperty op = new ObjectProperty();
                            op.setObject(obj);
                            op.setProperty(p);
                            op.setValue(p.getValue());
                            op.setOptional(p.getOptional());
                            op.setAddToMetadata(0);
                            op.setShowToVendorUser(0);
                            op.setPassedFromApp(0);
                            op.setObjectPropertyType(p.getPropertyType());
                            op.setStatus(1);
                            obj.addAidasObjectProperty(op);
                        }
                    }
                    objectRepository.save(obj);
                   /* objectAddingTask.setDummy(true);
                    objectAddingTask.setObject(obj);
                    objectAddingTask.run();*/
                }
                List<Object> dynaObjects = new ArrayList<>();
                if (result.getAutoCreateObjects() != null && result.getAutoCreateObjects().equals(AidasConstants.AUTO_CREATE_OBJECTS)) {
                    for (int i = 0; i < result.getNumberOfObjects(); i++) {
                        Object obj = new Object();
                        String objName = "";
                        if (result.getObjectPrefix() != null && result.getObjectPrefix().trim().length()>0) {
                            objName += result.getObjectPrefix()+"_";
                        }
                        objName += String.valueOf(i);
                        if (result.getObjectSuffix() != null && result.getObjectSuffix().trim().length()>0) {
                            objName += "_"+result.getObjectSuffix();
                        }
                        obj.setName(objName);
                        obj.setNumberOfUploadsRequired(result.getNumberOfUploadsRequired());
                        obj.setDescription(objName);
                        obj.setProject(result);
                        obj.setBufferPercent(result.getBufferPercent());
                        obj.setDummy(0);
                        obj.setStatus(1);

                        int totalRequired = project.getNumberOfUploadsRequired();
                        Float bufferedRequired = obj.getBufferPercent().floatValue()/100f * totalRequired;
                        obj.setNumberOfBufferedUploadsRequired(totalRequired + bufferedRequired.intValue());
                        obj.setTotalRequired(totalRequired + bufferedRequired.intValue());
                        obj.setNumberOfUploadsRequired(totalRequired);
                        if (project.getCategory() != null) {
                            List<Property> commonProperties = propertyRepository.findAllDefaultPropsOfCustomerAndCategory(project.getCustomer().getId(), 1l);
                            List<Property> categorySpecificProperties = propertyRepository.findAllDefaultPropsOfCustomerAndCategory(project.getCustomer().getId(), project.getCategory().getId());
                            commonProperties.addAll(categorySpecificProperties);
                            for (Property p : commonProperties) {
                                ObjectProperty op = new ObjectProperty();
                                op.setObject(obj);
                                op.setProperty(p);
                                op.setValue(p.getValue());
                                op.setOptional(p.getOptional());
                                op.setAddToMetadata(0);
                                op.setPassedFromApp(0);
                                op.setStatus(1);
                                obj.addAidasObjectProperty(op);
                            }
                        }
                        Object resultObj = objectRepository.save(obj);
                        dynaObjects.add(resultObj);
                    }
                    /*objectAddingTask.setDummy(false);
                    objectAddingTask.setDynamicObjects(dynaObjects);
                    objectAddingTask.runBulkObjects();*/
                    //
                    int numOfUploadRequired = project.getNumberOfUploadsRequired();
                    int numOfObjects = project.getNumberOfObjects();
                    int totalRequired = numOfUploadRequired*numOfObjects;
                    Float bufferedRequired = project.getBufferPercent().floatValue()/100f * totalRequired;
                    project.setTotalRequired(totalRequired+bufferedRequired.intValue());
                    project.setNumberOfBufferedUploadsdRequired(totalRequired+bufferedRequired.intValue());
                    project.setNumberOfUploadsRequired(totalRequired);
                }

                return ResponseEntity
                    .created(new URI("/api/aidas-projects/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            }else{
                throw new BadRequestAlertException("Qc Level configurations are required ", ENTITY_NAME, "idexists");
            }
    }


    @GetMapping("/downloadFile/{projectId}/{approvalStatus}")
    public ResponseEntity<Resource> downloadFile(@PathVariable(value = "projectId", required = true) Long projectId,@PathVariable(value="approvalStatus",required = false) String approvalStatus ) throws MalformedURLException {
        int status=3;
        if(approvalStatus==null ){
            status=3;
        }else if(approvalStatus.equals("approved")){
            status =1;
        }else if(approvalStatus.equals("rejected")){
            status = 0;
        }else if(approvalStatus.equals("all")){
            status =3;
        }
        String filename = "metadata_"+projectId+".csv";
        Integer colCount = projectRepository.getTotalPropertyCountForExport(projectId);
        List<UploadMetadataDTO> uploadMetaDatas;
        if(status==3)
            uploadMetaDatas = uploadMetaDataRepository.getAllUploadMetaDataForProject(projectId);
        else
            uploadMetaDatas = uploadMetaDataRepository.getAllUploadMetaDataForProjectWithStatus(projectId,status);

        Project project = projectRepository.getById(projectId);
        List<List<String>> csvDatas = new LinkedList<>();
        List<String> csvData = new LinkedList<>();
        csvData.add("Sl No.");
        csvData.add("Project Name");
        csvData.add("Object Name");
        csvData.add("Upload Id");
        for(int q=1;q<=project.getQcLevels();q++){
            csvData.add("QC Level "+q+" Done by");
            csvData.add("QC Level "+q+" Status");
            csvData.add("QC Level "+q+" Reject Reasons");
        }
        List<String> cols = projectRepository.getTotalPropertyNamesForExport(projectId);
        csvData.addAll(cols);
        csvDatas.add(csvData);
        colCount = csvData.size();
        if(uploadMetaDatas.size()%cols.size()==0){
            int i=0;
            int slNo=1;
            csvData = null;
            for(UploadMetadataDTO umd:uploadMetaDatas){
                if(i%cols.size()==0){
                    if(csvData!=null) {
                        csvDatas.add(csvData);
                    }
                    csvData = new ArrayList<>();
                    csvData.add(String.valueOf(slNo));
                    csvData.add(umd.getProjectName());
                    csvData.add(umd.getObjectName());
                    csvData.add(String.valueOf(umd.getUploadId()));
                    Integer qcLevels = project.getQcLevels();
                    for(int j=1;j<=qcLevels;j++){
                        List<QcResultDTO> qcLevelStatus = uploadCustomerQcProjectMappingBatchInfoRepository.getQcLevelStatus(umd.getUploadId(),j);
                        if(qcLevelStatus==null || (qcLevelStatus!=null && qcLevelStatus.size()==0)){
                            csvData.add("QC Not Done Yet");
                            csvData.add("Pending");
                            csvData.add(" ");
                        }else {
                            for (QcResultDTO qcResultDTO : qcLevelStatus) {
                                csvData.add(qcResultDTO.getFirstName() + " " + qcResultDTO.getLastName());
                                if (qcResultDTO.getQcStatus().equals(0))
                                    csvData.add("Rejected");
                                if (qcResultDTO.getQcStatus().equals(1))
                                    csvData.add("Approved");
                                if (qcResultDTO.getQcStatus().equals(2))
                                    csvData.add("Pending");
                                if (qcResultDTO.getQcStatus() == 0) {
                                    Upload upload = uploadRepository.getById(umd.getUploadId());
                                    String rejectReasons ="";
                                    for (UploadRejectReasonMapping urrm : upload.getUploadRejectMappings()) {
                                        rejectReasons+=urrm.getUploadRejectReason().getReason() + ",";
                                    }
                                    csvData.add(rejectReasons.substring(0,rejectReasons.length()-1));
                                } else {
                                    csvData.add(" ");
                                }
                            }
                        }
                    }
                    slNo++;
                }
                csvData.add(umd.getValue());
                i++;
            }
            csvDatas.add(csvData);
        }
        try {
            String fileName = CSVHelper.uploadMetaDataToCsv(csvDatas, projectId);
            File file = new File(fileName);
            InputStreamResource file1 = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .contentLength(file.length())
                .body(file1);
        }catch(Exception e){
            e.printStackTrace();
            throw new BadRequestAlertException("Unable to generate file ", ENTITY_NAME, "idexists");
        }
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
        Project project = projectRepository.getById(projectQcDTO.getProjectId());
        for(UserDTO userDTO: projectQcDTO.getQcUsers()){
            //the uservendormappingid coming from the frontend is actually qpc.id -- check the method which fetch list of qc users for project.
            CustomerQcProjectMapping cqpm = customerQcProjectMappingRepository.getQcProjectMappingByProjectAndCustomerAndUserAndLevel(projectQcDTO.getProjectId(),userDTO.getUserCustomerMappingId(),userDTO.getQcLevel());
            if(cqpm==null){
                cqpm = new CustomerQcProjectMapping();
                cqpm.setProject(project);
                cqpm.setUserCustomerMapping(userCustomerMappingRepository.getById(userDTO.getUserCustomerMappingId()));
            }
            cqpm.setStatus(userDTO.getStatus());
            cqpm.setQcLevel(userDTO.getQcLevel());
            customerQcProjectMappingRepository.save(cqpm);
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
        List<UserVendorMappingProjectMapping> uvmpms = new ArrayList<>();
        List<UserVendorMappingObjectMapping> uvmoms = new ArrayList<>();
        for(VendorUserDTO vendorUserDTO:projectVendorMappingDTO.getVendors()){
            for(UsersOfVendor userDTO: vendorUserDTO.getUserDTOs()){
                UserVendorMappingProjectMapping uvmpm=null;
                if(userDTO.getUserVendorMappingProjectMappingId()!=null && userDTO.getUserVendorMappingProjectMappingId()!=-2){
                    uvmpm = userVendorMappingProjectMappingRepository.getById(userDTO.getUserVendorMappingProjectMappingId());
                }else{
                    uvmpm = new UserVendorMappingProjectMapping();
                    uvmpm.setUserVendorMapping(userVendorMappingRepository.getById(userDTO.getUserVendorMappingId()));
                    uvmpm.setProject(projectRepository.getById(projectVendorMappingDTO.getProjectId()));
                }
                uvmpm.setStatus(userDTO.getStatus());
                uvmpms.add(uvmpm);
                List<Object> objects = objectRepository.getAllObjectsOfProject(projectVendorMappingDTO.getProjectId());
                for(Object o:objects){
                    UserVendorMappingObjectMapping uvmom = userVendorMappingObjectMappingRepository.findByUserVendorMappingObject(userDTO.getUserVendorMappingId(),o.getId());
                    if(uvmom==null){
                        uvmom = new UserVendorMappingObjectMapping();
                        uvmom.setUserVendorMapping(userVendorMappingRepository.getById(userDTO.getUserVendorMappingId()));
                        uvmom.setObject(o);
                    }
                    uvmom.setStatus(userDTO.getStatus());
                    uvmoms.add(uvmom);
                }
            }
        }
        userVendorMappingProjectMappingRepository.saveAll(uvmpms);
        userVendorMappingObjectMappingRepository.saveAll(uvmoms);
        return ResponseEntity.ok().body("Successfully mapped vendors to project");
    }

    /**
     * {@code POST  /aidas-projects/vendormapping/add-remove/{fromObjectId}/{toObjectId}} : Create a new project.
     *
     * @param projectVendorMappingDTO the projectVendorMappings to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new project, or with status {@code 400 (Bad Request)} if the project has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @PostMapping("/aidas-projects/vendormapping/add-remove/{fromObjectId}/{toObjectId}")
    public ResponseEntity<String> addRemoveVendorUsersMappingBulk(@Valid @RequestBody ProjectVendorMappingDTO projectVendorMappingDTO,@PathVariable(value = "fromObjectId", required = false) final Integer fromObjectId,@PathVariable(value = "toObjectId", required = false) final Integer toObjectId) throws URISyntaxException {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to map AidasProject to AidasVendor: {}", projectVendorMappingDTO);
        if (projectVendorMappingDTO.getProjectId() == null) {
            throw new BadRequestAlertException("A new project cannot already have an ID", ENTITY_NAME, "idexists");
        }
        List<Long>userVendorMappingIds = new ArrayList<>();
        Map<Long,Integer>userVendorMappingStatusMap = new HashMap<>();
        for(VendorUserDTO vendorUserDTO:projectVendorMappingDTO.getVendors()){
            for(UsersOfVendor userDTO: vendorUserDTO.getUserDTOs()){
                userVendorMappingIds.add(userDTO.getUserVendorMappingId());
                userVendorMappingStatusMap.put(userDTO.getUserVendorMappingId(),userDTO.getStatus());
            }
        }
        Project project = projectRepository.getById(projectVendorMappingDTO.getProjectId());
        for(int i=fromObjectId;i<=toObjectId;i++){
            Object object = objectRepository.getObjectByName(project.getObjectPrefix()+"_"+i+"_"+project.getObjectSuffix());
            List<UserVendorMappingObjectMapping> uvmoms = userVendorMappingObjectMappingRepository.getAllUserVendorMappingObjectMappingsByObjectId(object.getId());
            for(UserVendorMappingObjectMapping uvmom:uvmoms){
                uvmom.setStatus(userVendorMappingStatusMap.get(uvmom.getUserVendorMapping().getId()));
            }
            userVendorMappingObjectMappingRepository.saveAll(uvmoms);
        }
        List<UserVendorMappingProjectMapping> uvmpms = userVendorMappingProjectMappingRepository.getAllUserVendorMappingProjectMappingByProjectId(projectVendorMappingDTO.getProjectId());
        for(UserVendorMappingProjectMapping uvmpm:uvmpms){
            uvmpm.setStatus(userVendorMappingStatusMap.get(uvmpm.getUserVendorMapping().getId()));
        }
        userVendorMappingProjectMappingRepository.saveAll(uvmpms);
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
        Project result = projectRepository.save(existingProject);
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
        try {
            if (user.getAuthority().getName().equals(AidasConstants.ADMIN)) {
                page = projectRepository.findAllByIdGreaterThan(0l, pageable);
            }
            if (user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation() != null) {
                page = projectRepository.findAllByAidasCustomer_AidasOrganisation(pageable, user.getOrganisation().getId());
            }
            if (user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) && user.getCustomer() != null) {
                page = projectRepository.findAllByAidasCustomer(pageable, user.getCustomer().getId());
            }
            if (user.getAuthority().getName().equals(AidasConstants.VENDOR_ADMIN)) {
                page = projectRepository.findAllProjectsByVendorAdmin(pageable, user.getVendor().getId());
            }
            if (user.getAuthority().getName().equals(AidasConstants.VENDOR_USER)) {
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
            if (page != null) {
                HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
                return ResponseEntity.ok().headers(headers).body(page.getContent());
            }else{
                return ResponseEntity.ok().body(null);
            }
        }catch(Exception e){
            e.printStackTrace();
                throw new BadRequestAlertException("Not Authorised", ENTITY_NAME, "idexists");
        }
    }

    /**
     * {@code GET  /aidas-projects} : get all the aidasProjects.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasProjects in body.
     */

    @GetMapping("/aidas-projects/dropdown")
    public ResponseEntity<List<ProjectDTO>> getAllAidasProjectsForDropDown() {
        log.debug("REST request to get a page of AidasProjects");
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        List<ProjectDTO> page = new ArrayList();
        if(user.getAuthority().getName().equals(AidasConstants.ADMIN)){
            page = projectRepository.findAllByIdGreaterThanForDropDown();
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
            page =  projectRepository.findProjectWithUploadCountByUserForDropDown(user.getId());
        }
        if(user.getAuthority().getName().equals(AidasConstants.QC_USER)){
            page = projectRepository.findProjectsForCustomerQC(user.getId());
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
        List<Long> enabledProjects = objectRepository.getObjectsEnabledForUser(user.getId());
        Page<ProjectDTO> page = null;
        if(user.getAuthority().getName().equals(AidasConstants.VENDOR_USER)){
            page = projectRepository.findProjectWithUploadCountByUser(pageable, user.getId());
            for(ProjectDTO p:page.getContent()){
                Integer availableObjectCount = projectRepository.findProjectWithAnyObjectEnabledForUser(user.getId(),p.getId());
                List<ProjectProperty> projectProperties = projectPropertyRepository.findAllProjectProperty(p.getId());
                p.setAidasProjectProperties(projectProperties);
            }
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
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
        Project project =projectRepository.findById(id).get();
        if(project!=null) {
            return ResponseEntity.ok().body(project);
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
        taskExecutor.execute(downloadUploadS3);
    }




}
