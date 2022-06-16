package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.dto.ObjectPropertyDTO;
import com.ainnotate.aidas.repository.ObjectPropertyRepository;
import com.ainnotate.aidas.repository.ObjectRepository;
import com.ainnotate.aidas.repository.PropertyRepository;
import com.ainnotate.aidas.repository.UserRepository;
import com.ainnotate.aidas.repository.search.ObjectPropertySearchRepository;
import com.ainnotate.aidas.security.SecurityUtils;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;

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
 * REST controller for managing {@link ObjectProperty}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ObjectPropertyResource {

    private final Logger log = LoggerFactory.getLogger(ObjectPropertyResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasObjectProperty";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ObjectPropertyRepository objectPropertyRepository;

    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private ObjectRepository objectRepository;

    @Autowired
    private UserRepository userRepository;

    private final ObjectPropertySearchRepository aidasObjectPropertySearchRepository;

    public ObjectPropertyResource(
        ObjectPropertyRepository objectPropertyRepository,
        ObjectPropertySearchRepository aidasObjectPropertySearchRepository
    ) {
        this.objectPropertyRepository = objectPropertyRepository;
        this.aidasObjectPropertySearchRepository = aidasObjectPropertySearchRepository;
    }

    /**
     * {@code POST  /aidas-object-property} : Create a new objectProperty.
     *
     * @param objectProperty the objectProperty to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new objectProperty, or with status {@code 400 (Bad Request)} if the objectProperty has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-object-property")
    public ResponseEntity<ObjectProperty> createAidasObjectProperty(@Valid @RequestBody ObjectProperty objectProperty)
        throws URISyntaxException {
        log.debug("REST request to save AidasObjectProperty : {}", objectProperty);
        if (objectProperty.getId() != null) {
            throw new BadRequestAlertException("A new objectProperty cannot already have an ID", ENTITY_NAME, "idexists");
        }
        try {
            ObjectProperty result = objectPropertyRepository.save(objectProperty);
            aidasObjectPropertySearchRepository.save(result);
            return ResponseEntity
                .created(new URI("/api/aidas-object-property/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                .body(result);
        }catch(DataIntegrityViolationException dive){
            throw new BadRequestAlertException("The property is already added to the object.", ENTITY_NAME, "idexists");
        }
    }

    /**
     * {@code POST  /aidas-object-property/dto} : Create a new objectProperty.
     *
     * @param objectPropertyDTO the aidasObjectPropertyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new objectProperty, or with status {@code 400 (Bad Request)} if the projectProperty has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-object-property/dto")
    public ResponseEntity<ObjectProperty> createAidasObjectProperty(@Valid @RequestBody ObjectPropertyDTO objectPropertyDTO)
        throws URISyntaxException {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to save AidasProjectProperty : {}", objectPropertyDTO);
        Object object = objectRepository.getById(objectPropertyDTO.getAidasObjectId());
        Property property = propertyRepository.getById(objectPropertyDTO.getAidasPropertyId());
        ObjectProperty objectProperty = new ObjectProperty();
        try {
            if (object != null && property != null) {
                objectProperty = new ObjectProperty();
                objectProperty.setObject(object);
                objectProperty.setProperty(property);
                objectProperty.setValue(objectPropertyDTO.getValue());
                ObjectProperty result =  objectPropertyRepository.save(objectProperty);
                return ResponseEntity
                    .created(new URI("/api/aidas-project-property/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            }else if(object !=null && property ==null){
                property = new Property();
                property.setName(objectPropertyDTO.getName());
                property.setDefaultProp(objectPropertyDTO.getDefaultProp());
                property.setPropertyType(objectPropertyDTO.getPropertyType());
                property.setValue(objectPropertyDTO.getValue());
                property.setOptional(objectPropertyDTO.getIsOptional());
                property.setUser(user);
                property = propertyRepository.save(property);
                objectProperty = new ObjectProperty();
                objectProperty.setObject(object);
                objectProperty.setProperty(property);
                objectPropertyRepository.save(objectProperty);
            }
            return ResponseEntity.ok().body(objectProperty);
        }
        catch(Exception e){
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "idexists");
        }
    }

    /**
     * {@code POST  /aidas-object-property/dtos} : Create a new objectProperty.
     *
     * @param objectPropertyDTOS the aidasObjectPropertyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new objectProperty, or with status {@code 400 (Bad Request)} if the projectProperty has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-object-property/dtos")
    public ResponseEntity<String> createAidasObjectProperties(@Valid @RequestBody List<ObjectPropertyDTO> objectPropertyDTOS)
        throws URISyntaxException {
        log.debug("REST request to save AidasProjectProperty : {}", objectPropertyDTOS);
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        int i=0;
        try {
            for(ObjectPropertyDTO objectPropertyDTO : objectPropertyDTOS){
                Object object = objectRepository.getById(objectPropertyDTO.getAidasObjectId());
                Property property = propertyRepository.getById(objectPropertyDTO.getAidasPropertyId());
                if (object != null && property != null) {
                    ObjectProperty objectProperty = new ObjectProperty();
                    objectProperty.setObject(object);
                    objectProperty.setProperty(property);
                    objectProperty.setValue(objectPropertyDTO.getValue());
                    ObjectProperty result =  objectPropertyRepository.save(objectProperty);
                    i++;
                }else if(object !=null && property ==null){
                        property = new Property();
                        property.setName(objectPropertyDTO.getName());
                        property.setDefaultProp(objectPropertyDTO.getDefaultProp());
                        property.setPropertyType(objectPropertyDTO.getPropertyType());
                        property.setValue(objectPropertyDTO.getValue());
                        property.setOptional(objectPropertyDTO.getIsOptional());
                        property.setUser(user);
                        property = propertyRepository.save(property);
                        ObjectProperty objectProperty = new ObjectProperty();
                        objectProperty.setObject(object);
                        objectProperty.setProperty(property);
                        objectPropertyRepository.save(objectProperty);
                }
            }
            if(i== objectPropertyDTOS.size()){
                return ResponseEntity.ok().body("All object property created");
            }else{
                return ResponseEntity.ok().body("Some object property not created");
            }
        }
        catch(Exception e){
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "idexists");
        }
    }

    /**
     * {@code POST  /aidas-object-property/dto/update} : Create a new objectProperty.
     *
     * @param objectPropertyDTO the aidasObjectPropertyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new objectProperty, or with status {@code 400 (Bad Request)} if the projectProperty has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-object-property/dto/update")
    public ResponseEntity<ObjectProperty> updateAidasObjectProperty(@Valid @RequestBody ObjectPropertyDTO objectPropertyDTO)
        throws URISyntaxException {
        log.debug("REST request to save AidasProjectProperty : {}", objectPropertyDTO);
        ObjectProperty objectProperty =  objectPropertyRepository.findByAidasObject_IdAndAidasProperty_Id(objectPropertyDTO.getAidasObjectId(), objectPropertyDTO.getAidasPropertyId());

        try {
            if (objectProperty != null) {
                objectProperty.setValue(objectPropertyDTO.getValue());
                ObjectProperty result =  objectPropertyRepository.save(objectProperty);
                return ResponseEntity
                    .created(new URI("/api/aidas-object-property/" + result.getId()))
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
     * {@code POST  /aidas-object-property/dtos/update} : Create a new objectProperty.
     *
     * @param objectPropertyDTOS the aidasObjectPropertyDTOs to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new objectProperty, or with status {@code 400 (Bad Request)} if the projectProperty has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-object-property/dtos/update")
    public ResponseEntity<String> updateAidasObjectProperties(@Valid @RequestBody List<ObjectPropertyDTO> objectPropertyDTOS)
        throws URISyntaxException {
        log.debug("REST request to save AidasProjectProperty : {}", objectPropertyDTOS);
        try {
            int i=0;
            for(ObjectPropertyDTO objectPropertyDTO : objectPropertyDTOS){
                ObjectProperty objectProperty =  objectPropertyRepository.findByAidasObject_IdAndAidasProperty_Id(objectPropertyDTO.getAidasObjectId(), objectPropertyDTO.getAidasPropertyId());
                if (objectProperty != null) {
                    objectProperty.setValue(objectPropertyDTO.getValue());
                    ObjectProperty result =  objectPropertyRepository.save(objectProperty);
                    i++;
                }else{
                    //throw new BadRequestAlertException("Error occured when trying to map aidas property to project", ENTITY_NAME, "idexists");
                }
            }
            if(i== objectPropertyDTOS.size()){
                return ResponseEntity.ok().body("All object property updated");
            }else{
                return ResponseEntity.ok().body("Some object property not updated");
            }
        }
        catch(Exception e){
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "idexists");
        }
    }
    /**
     * {@code PUT  /aidas-object-property/:id} : Updates an existing objectProperty.
     *
     * @param id the id of the objectProperty to save.
     * @param objectProperty the objectProperty to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated objectProperty,
     * or with status {@code 400 (Bad Request)} if the objectProperty is not valid,
     * or with status {@code 500 (Internal Server Error)} if the objectProperty couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-object-property/{id}")
    public ResponseEntity<ObjectProperty> updateAidasObjectProperty(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ObjectProperty objectProperty
    ) throws URISyntaxException {
        log.debug("REST request to update AidasObjectProperty : {}, {}", id, objectProperty);
        if (objectProperty.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, objectProperty.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!objectPropertyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ObjectProperty result = objectPropertyRepository.save(objectProperty);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, objectProperty.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /aidas-object-property} : get all the objectProperties.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of objectProperties in body.
     */
    @GetMapping("/aidas-object-property")
    public ResponseEntity<List<ObjectProperty>> getAllAidasObjectProperties(Pageable pageable) {
        log.debug("REST request to get a page of AidasObjectProperties");
        Page<ObjectProperty> page = objectPropertyRepository.findAllByAidasObjectIdGreaterThan(pageable,-1l);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-object-property/:id} : get the "id" objectProperty.
     *
     * @param id the id of the objectProperty to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the objectProperty, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-object-property/{id}")
    public ResponseEntity<ObjectProperty> getObjectProperty(@PathVariable Long id) {
        log.debug("REST request to get AidasObjectProperty : {}", id);
        Optional<ObjectProperty> objectProperty = objectPropertyRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(objectProperty);
    }

    /**
     * {@code DELETE  /aidas-object-property/:id} : delete the "id" objectProperty.
     *
     * @param id the id of the objectProperty to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aidas-object-property/{id}")
    public ResponseEntity<Void> deleteAidasObjectProperty(@PathVariable Long id) {
        log.debug("REST request to delete AidasObjectProperty : {}", id);
        ObjectProperty objectProperty = objectPropertyRepository.getById(id);
        if(objectProperty !=null) {
            objectProperty.setStatus(0);
            objectPropertyRepository.save(objectProperty);
        }
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-object-property?query=:query} : search for the objectProperty corresponding
     * to the query.
     *
     * @param query the query of the objectProperty search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/aidas-object-property")
    public ResponseEntity<List<ObjectProperty>> searchAidasObjectProperties(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasObjectProperties for query {}", query);
        Page<ObjectProperty> page = aidasObjectPropertySearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
