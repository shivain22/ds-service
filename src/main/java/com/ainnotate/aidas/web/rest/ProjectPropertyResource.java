package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.dto.ProjectPropertyDTO;
import com.ainnotate.aidas.dto.ProperyProjectPropertyDTO;
import com.ainnotate.aidas.repository.*;
import com.ainnotate.aidas.repository.search.ProjectPropertySearchRepository;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
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
 * REST controller for managing {@link ProjectProperty}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ProjectPropertyResource {

    private final Logger log = LoggerFactory.getLogger(ProjectPropertyResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasProjectProperty";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectPropertyRepository projectPropertyRepository;

    @Autowired
    private  ObjectPropertyRepository objectPropertyRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ObjectRepository objectRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private final ProjectPropertySearchRepository aidasProjectPropertySearchRepository;

    public ProjectPropertyResource(
        ProjectPropertyRepository projectPropertyRepository,
        ProjectPropertySearchRepository aidasProjectPropertySearchRepository
    ) {
        this.projectPropertyRepository = projectPropertyRepository;
        this.aidasProjectPropertySearchRepository = aidasProjectPropertySearchRepository;
    }

    /**
     * {@code POST  /aidas-project-property} : Create a new projectProperty.
     *
     * @param projectProperty the projectProperty to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectProperty, or with status {@code 400 (Bad Request)} if the projectProperty has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-project-property")
    public ResponseEntity<ProjectProperty> createAidasProjectProperty(@Valid @RequestBody ProjectProperty projectProperty)
        throws URISyntaxException {
        log.debug("REST request to save AidasProjectProperty : {}", projectProperty);
        if (projectProperty.getId() != null) {
            throw new BadRequestAlertException("A new projectProperty cannot already have an ID", ENTITY_NAME, "idexists");
        }
        try {
            ProjectProperty result = projectPropertyRepository.save(projectProperty);
            aidasProjectPropertySearchRepository.save(result);
            return ResponseEntity
                .created(new URI("/api/aidas-project-property/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                .body(result);
        }catch(DataIntegrityViolationException dive){
            throw new BadRequestAlertException("Selected property is already added to the project.", ENTITY_NAME, "idexists");
        }
    }


    /**
     * {@code POST  /aidas-project-property/dto} : Create a new projectProperty.
     *
     * @param projectPropertyDTO the projectPropertyDto to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectProperty, or with status {@code 400 (Bad Request)} if the projectProperty has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-project-property/dto")
    public ResponseEntity<ProjectProperty> createAidasProjectProperty(@Valid @RequestBody ProjectPropertyDTO projectPropertyDTO)
        throws URISyntaxException {
        log.debug("REST request to save AidasProjectProperty : {}", projectPropertyDTO);
        Project project = projectRepository.getById(projectPropertyDTO.getAidasProjectId());
        Property property = propertyRepository.getById(projectPropertyDTO.getAidasPropertyId());
        try {
            if (project != null && property != null) {
                ProjectProperty projectProperty = new ProjectProperty();
                projectProperty.setProject(project);
                projectProperty.setProperty(property);
                projectProperty.setValue(projectPropertyDTO.getValue());
                ProjectProperty result =  projectPropertyRepository.save(projectProperty);
                return ResponseEntity
                    .created(new URI("/api/aidas-project-property/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            }else{
                throw new BadRequestAlertException("Error occured when trying to map aidas property to project", ENTITY_NAME, "idexists");
            }
        }
        catch(Exception e){
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "idexists");
        }
    }



    /**
     * {@code POST  /aidas-project-property/dtos} : Create a new projectProperty.
     *
     * @param projectPropertyDTOS the projectPropertyDto to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectProperty, or with status {@code 400 (Bad Request)} if the projectProperty has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-project-property/dtos")
    public ResponseEntity<String> createAidasProjectProperties(@Valid @RequestBody List<ProjectPropertyDTO> projectPropertyDTOS)
        throws URISyntaxException {
        log.debug("REST request to save AidasProjectProperty : {}", projectPropertyDTOS);
        int i=0;
        try {
            for(ProjectPropertyDTO projectPropertyDTO : projectPropertyDTOS) {
                Project project = projectRepository.getById(projectPropertyDTO.getAidasProjectId());
                Property property = propertyRepository.getById(projectPropertyDTO.getAidasPropertyId());
                ProjectProperty projectProperty = projectPropertyRepository.findByProjectAndProperty(project.getId(),property.getId());
                if (projectProperty!=null) {
                    projectProperty.setValue(projectPropertyDTO.getValue());
                    i++;
                } else {
                    ProjectProperty projectProperty1 = new ProjectProperty();
                    projectProperty1.setProject(project);
                    projectProperty1.setProperty(property);
                    projectProperty1.setValue(projectPropertyDTO.getValue());
                    ProjectProperty result = projectPropertyRepository.save(projectProperty1);
                    i++;
                }
            }
        }
        catch(Exception e){
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "idexists");
        }
        if(i== projectPropertyDTOS.size()){
            return ResponseEntity.ok().body("All project property created");
        }else{
            return ResponseEntity.ok().body("Some project property not created");
        }
    }

    /**
     * {@code POST  /aidas-property-aidas-project-property/dtos} : Create a new projectProperty with creating aidasProperties entryt.
     *
     * @param properyProjectPropertyDTOS the projectPropertyDto to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectProperty, or with status {@code 400 (Bad Request)} if the projectProperty has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-property-aidas-project-property/dto")
    public ResponseEntity<String> createAidasPropertiesAidasProjectProperties(@Valid @RequestBody List<ProperyProjectPropertyDTO> properyProjectPropertyDTOS)
        throws URISyntaxException {
        log.debug("REST request to save AidasProjectProperty : {}", properyProjectPropertyDTOS);
        int i=0;
        try {
            for(ProperyProjectPropertyDTO properyProjectPropertyDTO : properyProjectPropertyDTOS) {
                Project project = projectRepository.getById(properyProjectPropertyDTO.getAidasProjectId());
                Property property = new Property();
                if (project != null && property != null) {
                    property.setOptional(properyProjectPropertyDTO.getOptional());
                    property.setName(properyProjectPropertyDTO.getName());
                    property.setDefaultProp(properyProjectPropertyDTO.getDefaultProp());
                    property.setPropertyType(AidasConstants.AIDAS_METADATA_PROPERTY);
                    property.setDescription(properyProjectPropertyDTO.getDescription());
                    property.setValue(properyProjectPropertyDTO.getValue());
                    property.setAddToMetadata(1);
                    property.setValue("Not yet filled");
                    property.setCustomer(project.getCustomer());
                    property.setShowToVendorUser(1);
                    Category category = categoryRepository.getById(6l);
                    property.setCategory(category);
                    property.setPropertyType(2);
                    property = propertyRepository.save(property);
                    ProjectProperty projectProperty = new ProjectProperty();
                    projectProperty.setProject(project);
                    projectProperty.setProperty(property);
                    projectProperty.setOptional(property.getOptional());
                    projectProperty.setAddToMetadata(1);
                    projectProperty.setDefaultProp(property.getDefaultProp());
                    projectProperty.setValue(properyProjectPropertyDTO.getAidasProjectPropertyValue());
                    projectProperty.setShowToVendorUser(1);
                    projectProperty.setProjectPropertyType(AidasConstants.AIDAS_METADATA_PROPERTY);
                    ProjectProperty result = projectPropertyRepository.save(projectProperty);
                    i++;
                }
            }
        }
        catch(Exception e){
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "idexists");
        }
        if(i== properyProjectPropertyDTOS.size()){
            return ResponseEntity.ok().body("All project property created");
        }else{
            return ResponseEntity.ok().body("Some project property not created");
        }
    }

    /**
     * {@code POST  /aidas-project-property/dto/update} : Create a new projectProperty.
     *
     * @param projectPropertyDTO the projectPropertyDto to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectProperty, or with status {@code 400 (Bad Request)} if the projectProperty has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-project-property/dto/update")
    public ResponseEntity<ProjectProperty> updateAidasProjectProperty(@Valid @RequestBody ProjectPropertyDTO projectPropertyDTO)
        throws URISyntaxException {
        ProjectProperty projectProperty = projectPropertyRepository.findByProjectAndProperty(projectPropertyDTO.getAidasProjectId(), projectPropertyDTO.getAidasPropertyId());
        try {
            if (projectProperty != null) {
                projectProperty.setValue(projectPropertyDTO.getValue());
                ProjectProperty result =  projectPropertyRepository.save(projectProperty);
                return ResponseEntity
                    .created(new URI("/api/aidas-project-property/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            }else{
                throw new BadRequestAlertException("Error occured when trying to map aidas property to project", ENTITY_NAME, "idexists");
            }
        }
        catch(Exception e){
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "idexists");
        }

    }

    /**
     * {@code POST  /aidas-project-property/dtos/update} : Create a new projectProperty.
     *
     * @param projectPropertys the projectPropertyDto to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectProperty, or with status {@code 400 (Bad Request)} if the projectProperty has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-project-property/dtos/update")
    public ResponseEntity<String> updateAidasProjectProperties(@Valid @RequestBody List<ProjectProperty> projectPropertys)
        throws URISyntaxException {
        int i=0;
        try {
            for(ProjectProperty projectProperty : projectPropertys) {
                ProjectProperty projectProperty1 = projectPropertyRepository.getById(projectProperty.getId());
                projectProperty1.setAddToMetadata(projectProperty.getAddToMetadata());
                projectPropertyRepository.save(projectProperty1);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "idexists");
        }
        return ResponseEntity.ok().body("All project property created");
    }

    /**
     * {@code PUT  /aidas-project-property/:id} : Updates an existing projectProperty.
     *
     * @param id the id of the projectProperty to save.
     * @param projectProperty the projectProperty to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectProperty,
     * or with status {@code 400 (Bad Request)} if the projectProperty is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectProperty couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-project-property/{id}")
    public ResponseEntity<ProjectProperty> updateAidasProjectProperty(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProjectProperty projectProperty
    ) throws URISyntaxException {
        log.debug("REST request to update AidasProjectProperty : {}, {}", id, projectProperty);
        if (projectProperty.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectProperty.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectPropertyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ProjectProperty result = projectPropertyRepository.save(projectProperty);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, projectProperty.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /aidas-project-property/:id} : Partial updates given fields of an existing projectProperty, field will ignore if it is null
     *
     * @param id the id of the projectProperty to save.
     * @param projectProperty the projectProperty to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectProperty,
     * or with status {@code 400 (Bad Request)} if the projectProperty is not valid,
     * or with status {@code 404 (Not Found)} if the projectProperty is not found,
     * or with status {@code 500 (Internal Server Error)} if the projectProperty couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-project-property/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProjectProperty> partialUpdateAidasProjectProperty(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProjectProperty projectProperty
    ) throws URISyntaxException {
        log.debug("REST request to partial update AidasProjectProperty partially : {}, {}", id, projectProperty);
        if (projectProperty.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectProperty.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectPropertyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProjectProperty> result = projectPropertyRepository
            .findById(projectProperty.getId())
            .map(existingAidasProjectProperty -> {

                if (projectProperty.getValue() != null) {
                    existingAidasProjectProperty.setValue(projectProperty.getValue());
                }

                return existingAidasProjectProperty;
            })
            .map(projectPropertyRepository::save)
            .map(savedAidasProjectProperty -> {
                aidasProjectPropertySearchRepository.save(savedAidasProjectProperty);

                return savedAidasProjectProperty;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, projectProperty.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-project-property} : get all the projectProperties.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projectProperties in body.
     */
    @GetMapping("/aidas-project-property")
    public ResponseEntity<List<ProjectProperty>> getAllAidasProjectProperties(Pageable pageable) {
        log.debug("REST request to get a page of AidasProjectProperties");
        Page<ProjectProperty> page = projectPropertyRepository.findAllByAidasProjectIdGreaterThan(pageable,-1l);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-project-property/dropdown} : get all the projectProperties.
     *
     * @param projectId the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projectProperties in body.
     */
    @GetMapping("/aidas-project-property/dropdown")
    public ResponseEntity<List<ProjectProperty>> getAllAidasProjectProperties(@PathVariable(value = "projectId", required = false) final Long projectId) {
        log.debug("REST request to get a page of AidasProjectProperties");
        List<ProjectProperty> page = projectPropertyRepository.findAllByAidasProjectIdGreaterThanForDropDown(-1l);
        return ResponseEntity.ok().body(page);
    }

    /**
     * {@code GET  /aidas-project-property/:id} : get the "id" projectProperty.
     *
     * @param id the id of the projectProperty to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectProperty, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-project-property/{id}")
    public ResponseEntity<ProjectProperty> getProjectProperty(@PathVariable Long id) {
        log.debug("REST request to get AidasProjectProperty : {}", id);
        Optional<ProjectProperty> projectProperty = projectPropertyRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(projectProperty);
    }

    /**
     * {@code DELETE  /aidas-project-property/:id} : delete the "id" projectProperty.
     *
     * @param id the id of the projectProperty to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aidas-project-property/{id}")
    public ResponseEntity<Void> deleteAidasProjectProperty(@PathVariable Long id) {
        log.debug("REST request to delete AidasProjectProperty : {}", id);
        ProjectProperty projectProperty = projectPropertyRepository.getById(id);
        //AidasProject project = projectProperty.getProject();
        //project.removeAidasProjectProperty(projectProperty);
        //aidasProjectRepository.save(project);
        //aidasProjectPropertyRepository.deleteById(id);
        if(projectProperty !=null){
            projectProperty.setStatus(0);
            projectPropertyRepository.save(projectProperty);
        }
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-project-property?query=:query} : search for the projectProperty corresponding
     * to the query.
     *
     * @param query the query of the projectProperty search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/aidas-project-property")
    public ResponseEntity<List<ProjectProperty>> searchAidasProjectProperties(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasProjectProperties for query {}", query);
        Page<ProjectProperty> page = aidasProjectPropertySearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
