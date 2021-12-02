package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.config.KeycloakConfig;
import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.repository.AidasAuthorityRepository;
import com.ainnotate.aidas.repository.AidasCustomerRepository;
import com.ainnotate.aidas.repository.AidasOrganisationRepository;
import com.ainnotate.aidas.repository.AidasUserRepository;
import com.ainnotate.aidas.repository.search.AidasUserSearchRepository;
import com.ainnotate.aidas.security.AidasAuthoritiesConstants;
import com.ainnotate.aidas.security.SecurityUtils;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;

import com.ainnotate.aidas.web.rest.vm.ManagedUserVM;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
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
 * REST controller for managing {@link com.ainnotate.aidas.domain.AidasUser}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AidasUserResource {

    private final Logger log = LoggerFactory.getLogger(AidasUserResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AidasUserRepository aidasUserRepository;

    private Keycloak keycloak;

    @Autowired
    private KeycloakConfig keycloakConfig;

    @Autowired
    private AidasAuthorityRepository aidasAuthorityRepository;

    @Autowired
    private AidasCustomerRepository aidasCustomerRepository;

    @Autowired
    private  AidasOrganisationRepository aidasOrganisationRepository;

    private final AidasUserSearchRepository aidasUserSearchRepository;

    public AidasUserResource(AidasUserRepository aidasUserRepository, AidasUserSearchRepository aidasUserSearchRepository, Keycloak keycloak) {
        this.aidasUserRepository = aidasUserRepository;
        this.aidasUserSearchRepository = aidasUserSearchRepository;
        this.keycloak =  keycloak;
    }

    /**
     * {@code POST  /aidas-users} : Create a new aidasUser.
     *
     * @param aidasUser the aidasUser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasUser, or with status {@code 400 (Bad Request)} if the aidasUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasAuthoritiesConstants.ADMIN,AidasAuthoritiesConstants.ORG_ADMIN,AidasAuthoritiesConstants.CUSTOMER_ADMIN,AidasAuthoritiesConstants.VENDOR_ADMIN})
    @PostMapping("/aidas-users")
    public ResponseEntity<AidasUser> createAidasUser(@Valid @RequestBody AidasUser aidasUser) throws URISyntaxException {
        log.debug("REST request to save AidasUser : {}", aidasUser);
        AidasUser loggedInUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        if (aidasUser.getId() != null) {
            throw new BadRequestAlertException("A new aidasUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if(loggedInUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && loggedInUser.getAidasOrganisation()!=null ){
            if(aidasUser.getAidasOrganisation()!=null){
                AidasOrganisation aidasOrganisation = aidasOrganisationRepository.getById(aidasUser.getAidasOrganisation().getId());
                if(!loggedInUser.getAidasOrganisation().equals(aidasOrganisation)){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
            if(aidasUser.getAidasCustomer()!=null){
                AidasCustomer aidasCustomer = aidasCustomerRepository.getById(aidasUser.getAidasCustomer().getId());
                if(!loggedInUser.getAidasOrganisation().equals(aidasCustomer.getAidasOrganisation())){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){
            if(aidasUser.getAidasCustomer()!=null){
                if(!loggedInUser.getAidasCustomer().equals(aidasUser.getAidasOrganisation())){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN)){
            if(aidasUser.getAidasVendor()!=null){
                if(!loggedInUser.getAidasVendor().equals(aidasUser.getAidasVendor())){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
        }
        addUserToKeyCloak(aidasUser);
        AidasUser result = aidasUserRepository.save(aidasUser);
        aidasUserSearchRepository.save(result);
        updateUserToKeyCloak(result);
        return ResponseEntity
            .created(new URI("/api/aidas-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code POST  /aidas-users} : Create a new aidasUser.
     *
     * @param newUser the aidasUser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasUser, or with status {@code 400 (Bad Request)} if the aidasUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/register")
    public ResponseEntity<AidasUser> registerAidasUser(@Valid @RequestBody ManagedUserVM newUser) throws URISyntaxException {
        log.debug("REST request to save AidasUser : {}", newUser);
        AidasUser aidasUser = new AidasUser();
        aidasUser.setEmail(newUser.getEmail());
        aidasUser.setFirstName(newUser.getFirstName());
        aidasUser.setLastName(newUser.getLastName());
        aidasUser.setLocked(false);
        aidasUser.setPassword(newUser.getPassword());
        if (aidasUser.getId() != null) {
            throw new BadRequestAlertException("A new aidasUser cannot already have an ID", ENTITY_NAME, "idexists");
        }

        registerNewUser(aidasUser);
        AidasUser result = aidasUserRepository.save(aidasUser);
        aidasUserSearchRepository.save(result);
        updateUserToKeyCloak(result);
        return ResponseEntity
            .created(new URI("/api/aidas-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code POST  /aidas-users/:role} : Update/change current role of the user.
     *
     * @param role the role to switch.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasUser, or with status {@code 400 (Bad Request)} if the aidasUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-users/{role}")
    public ResponseEntity<AidasUser> updateCurrentRole(@Valid @PathVariable String role) throws URISyntaxException {
        log.debug("REST request to update current role of AidasUser : {}", SecurityUtils.getCurrentUserLogin().get());
        AidasUser loggedInUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        System.out.println(SecurityUtils.getCurrentUserLogin().get());
        AidasUser aidasUser  = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        updateCurrentRole(aidasUser,role);
        AidasAuthority currentAidasAuthority =  aidasAuthorityRepository.findByName(role.trim());
        aidasUser.setCurrentAidasAuthority(currentAidasAuthority);
        aidasUserRepository.save(aidasUser);
        return ResponseEntity
            .created(new URI("/api/aidas-users/" + aidasUser.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, aidasUser.getId().toString()))
            .body(aidasUser);
    }


    /**
     * {@code PUT  /aidas-users/:id} : Updates an existing aidasUser.
     *
     * @param id the id of the aidasUser to save.
     * @param aidasUser the aidasUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasUser,
     * or with status {@code 400 (Bad Request)} if the aidasUser is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aidasUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-users/{id}")
    public ResponseEntity<AidasUser> updateAidasUser(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AidasUser aidasUser
    ) throws URISyntaxException {
        log.debug("REST request to update AidasUser : {}, {}", id, aidasUser);
        AidasUser loggedInUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        if (aidasUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        if(loggedInUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && loggedInUser.getAidasOrganisation()!=null ){
            if(aidasUser.getAidasOrganisation()!=null){
                AidasOrganisation aidasOrganisation = aidasOrganisationRepository.getById(aidasUser.getAidasOrganisation().getId());
                if(!loggedInUser.getAidasOrganisation().equals(aidasOrganisation)){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
            if(aidasUser.getAidasCustomer()!=null){
                AidasCustomer aidasCustomer = aidasCustomerRepository.getById(aidasUser.getAidasCustomer().getId());
                if(!loggedInUser.getAidasOrganisation().equals(aidasCustomer.getAidasOrganisation())){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){
            if(aidasUser.getAidasCustomer()!=null){
                if(!loggedInUser.getAidasCustomer().equals(aidasUser.getAidasOrganisation())){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN)){
            if(aidasUser.getAidasVendor()!=null){
                if(!loggedInUser.getAidasVendor().equals(aidasUser.getAidasVendor())){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
        }
        AidasUser result = aidasUserRepository.save(aidasUser);
        aidasUserSearchRepository.save(result);
        updateUserToKeyCloak(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasUser.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /aidas-users/:id} : Partial updates given fields of an existing aidasUser, field will ignore if it is null
     *
     * @param id the id of the aidasUser to save.
     * @param aidasUser the aidasUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasUser,
     * or with status {@code 400 (Bad Request)} if the aidasUser is not valid,
     * or with status {@code 404 (Not Found)} if the aidasUser is not found,
     * or with status {@code 500 (Internal Server Error)} if the aidasUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-users/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AidasUser> partialUpdateAidasUser(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AidasUser aidasUser
    ) throws URISyntaxException {
        log.debug("REST request to partial update AidasUser partially : {}, {}", id, aidasUser);
        AidasUser loggedInUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        if (aidasUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        if(loggedInUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && loggedInUser.getAidasOrganisation()!=null ){
            if(aidasUser.getAidasOrganisation()!=null){
                AidasOrganisation aidasOrganisation = aidasOrganisationRepository.getById(aidasUser.getAidasOrganisation().getId());
                if(!loggedInUser.getAidasOrganisation().equals(aidasOrganisation)){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
            if(aidasUser.getAidasCustomer()!=null){
                AidasCustomer aidasCustomer = aidasCustomerRepository.getById(aidasUser.getAidasCustomer().getId());
                if(!loggedInUser.getAidasOrganisation().equals(aidasCustomer.getAidasOrganisation())){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){
            if(aidasUser.getAidasCustomer()!=null){
                if(!loggedInUser.getAidasCustomer().equals(aidasUser.getAidasOrganisation())){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
        }
        if( aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN)){
            if(aidasUser.getAidasVendor()!=null){
                if(!loggedInUser.getAidasVendor().equals(aidasUser.getAidasVendor())){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
        }

        Optional<AidasUser> result = aidasUserRepository
            .findById(aidasUser.getId())
            .map(existingAidasUser -> {
                if (aidasUser.getFirstName() != null) {
                    existingAidasUser.setFirstName(aidasUser.getFirstName());
                }
                if (aidasUser.getLastName() != null) {
                    existingAidasUser.setLastName(aidasUser.getLastName());
                }
                if (aidasUser.getEmail() != null) {
                    existingAidasUser.setEmail(aidasUser.getEmail());
                }
                if (aidasUser.getLocked() != null) {
                    existingAidasUser.setLocked(aidasUser.getLocked());
                }
                if (aidasUser.getPassword() != null) {
                    existingAidasUser.setPassword(aidasUser.getPassword());
                }

                return existingAidasUser;
            })
            .map(aidasUserRepository::save)
            .map(savedAidasUser -> {
                aidasUserSearchRepository.save(savedAidasUser);

                return savedAidasUser;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasUser.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-users} : get all the aidasUsers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasUsers in body.
     */
    @GetMapping("/aidas-users")
    public ResponseEntity<List<AidasUser>> getAllAidasUsers(Pageable pageable) {
        log.debug("REST request to get a page of AidasUsers");
        AidasUser loggedInUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Page<AidasUser> page = null;//aidasUserRepository.findAll(pageable);
        if(loggedInUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ADMIN)){
            page = aidasUserRepository.findAll(pageable);
        }
        if(loggedInUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && loggedInUser.getAidasOrganisation()!=null ){
            page = aidasUserRepository.findAllByAidasCustomer_AidasOrganisation(pageable,loggedInUser.getAidasOrganisation());
        }
        if( loggedInUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN) && loggedInUser.getAidasCustomer()!=null ){
            page = aidasUserRepository.findAllByAidasCustomer(pageable,loggedInUser.getAidasCustomer());
        }
        if(loggedInUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN) || loggedInUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){
            page = aidasUserRepository.findAllByAidasVendor(pageable,loggedInUser.getAidasVendor());
        }
        if(loggedInUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.USER)){

        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-users/:id} : get the "id" aidasUser.
     *
     * @param id the id of the aidasUser to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasUser, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-users/{id}")
    public ResponseEntity<AidasUser> getAidasUser(@PathVariable Long id) {
        log.debug("REST request to get AidasUser : {}", id);
        AidasUser loggedInUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        AidasUser aidasUser = aidasUserRepository.getById(id);
        Optional<AidasUser> aidasUser1 = aidasUserRepository.findById(id);
        if(loggedInUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            if(!loggedInUser.getAidasOrganisation().equals(aidasUser.getAidasOrganisation())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( loggedInUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN) && aidasUser.getAidasCustomer()!=null ){
            if(!loggedInUser.getAidasCustomer().equals(aidasUser.getAidasCustomer())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if(loggedInUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN) || aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){
            if(!loggedInUser.getAidasVendor().equals(aidasUser.getAidasVendor())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
         return ResponseUtil.wrapOrNotFound(aidasUser1);
    }



    /**
     * {@code DELETE  /aidas-users/:id} : delete the "id" aidasUser.
     *
     * @param id the id of the aidasUser to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aidas-users/{id}")
    public ResponseEntity<Void> deleteAidasUser(@PathVariable Long id) {
        log.debug("REST request to delete AidasUser : {}", id);
        AidasUser loggedInUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        AidasUser  aidasUser = aidasUserRepository.getById(id);
        if(loggedInUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && aidasUser.getAidasOrganisation()!=null ){
            if(!loggedInUser.getAidasOrganisation().equals(aidasUser.getAidasOrganisation())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( loggedInUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN) && aidasUser.getAidasCustomer()!=null ){
            if(!loggedInUser.getAidasCustomer().equals(aidasUser.getAidasCustomer())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if(loggedInUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN) || aidasUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){
            if(!loggedInUser.getAidasVendor().equals(aidasUser.getAidasVendor())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        deleteUserFromKeyCloak(aidasUser);
        aidasUserRepository.deleteById(id);
        aidasUserSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-users?query=:query} : search for the aidasUser corresponding
     * to the query.
     *
     * @param query the query of the aidasUser search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/aidas-users")
    public ResponseEntity<List<AidasUser>> searchAidasUsers(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasUsers for query {}", query);
        AidasUser loggedInUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Page<AidasUser> page = aidasUserSearchRepository.search(query, pageable);
        if(loggedInUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && loggedInUser.getAidasOrganisation()!=null) {
            Iterator<AidasUser> it = page.getContent().iterator();
            while(it.hasNext()){
                AidasUser aidasUser = it.next();
                if(!aidasUser.getAidasOrganisation().equals(loggedInUser.getAidasOrganisation())){
                    it.remove();
                }
            }
        }
        if(loggedInUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN) && loggedInUser.getAidasCustomer()!=null) {
            Iterator<AidasUser> it = page.getContent().iterator();
            while(it.hasNext()){
                AidasUser aidasUser = it.next();
                if(!aidasUser.getAidasCustomer().equals(loggedInUser.getAidasCustomer())){
                    it.remove();
                }
            }
        }
        if(loggedInUser.getCurrentAidasAuthority().getName().equals(AidasAuthoritiesConstants.ORG_ADMIN) && loggedInUser.getAidasCustomer()!=null) {
            Iterator<AidasUser> it = page.getContent().iterator();
            while(it.hasNext()){
                AidasUser aidasUser = it.next();
                if(!aidasUser.getAidasVendor().equals(loggedInUser.getAidasVendor())){
                    it.remove();
                }
            }
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }



    public void addUserToKeyCloak(AidasUser aidasUser) {

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(aidasUser.getEmail());
        user.setFirstName(aidasUser.getFirstName());
        user.setLastName(aidasUser.getLastName());
        user.setEmail(aidasUser.getEmail());
        aidasUser.setLogin(aidasUser.getEmail());
        aidasUser.setCreatedBy(SecurityUtils.getCurrentUserLogin().get());
        aidasUser.setCreatedDate(Instant.now());
        aidasUser.setLastModifiedBy(SecurityUtils.getCurrentUserLogin().get());
        aidasUser.setLastModifiedDate(Instant.now());
        List<String> groups = new ArrayList<>();
        groups.add("Users");
        user.setGroups(groups);
        RealmResource realmResource = keycloak.realm(keycloakConfig.getClientRealm());
        UsersResource usersRessource = realmResource.users();
        user.setEnabled(true);
        user.setEmailVerified(true);
        Response response = usersRessource.create(user);
        String userId = CreatedResponseUtil.getCreatedId(response);
        aidasUser.setKeycloakId(userId);
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(aidasUser.getPassword());
        UserResource userResource = usersRessource.get(userId);
        userResource.resetPassword(passwordCred);
        //userResource.sendVerifyEmail();
        List<RoleRepresentation> roleRepresentationList = realmResource.roles().list();
        for (RoleRepresentation roleRepresentation : roleRepresentationList)
        {
            for(AidasAuthority aa:aidasUser.getAidasAuthorities()){
                if (roleRepresentation.getName().equals(aa.getName()))
                {
                    userResource.roles().realmLevel().add(Arrays.asList(roleRepresentation));
                    aidasUser.setCurrentAidasAuthority(aa);
                }
            }
        }
    }

    public void registerNewUser(AidasUser aidasUser) {

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(aidasUser.getEmail());
        user.setEmail(aidasUser.getEmail());
        aidasUser.setLogin(aidasUser.getEmail());
        if(SecurityUtils.getCurrentUserLogin().get()!=null) {
            aidasUser.setCreatedBy(SecurityUtils.getCurrentUserLogin().get());
            aidasUser.setLastModifiedBy(SecurityUtils.getCurrentUserLogin().get());
        }else{
            aidasUser.setCreatedBy("self_registered");
            aidasUser.setLastModifiedBy("self_registered");
        }
        aidasUser.setCreatedDate(Instant.now());
        aidasUser.setLastModifiedDate(Instant.now());
        List<String> groups = new ArrayList<>();
        groups.add("Users");
        user.setGroups(groups);
        RealmResource realmResource = keycloak.realm(keycloakConfig.getClientRealm());
        UsersResource usersRessource = realmResource.users();
        user.setEnabled(true);
        user.setEmailVerified(true);
        Response response = usersRessource.create(user);
        String userId = CreatedResponseUtil.getCreatedId(response);
        aidasUser.setKeycloakId(userId);
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(aidasUser.getPassword());
        UserResource userResource = usersRessource.get(userId);
        userResource.resetPassword(passwordCred);
        //userResource.sendVerifyEmail();
        RoleRepresentation userRealmRole = realmResource.roles().get(AidasAuthoritiesConstants.USER).toRepresentation();
        userResource.roles().realmLevel().add(Arrays.asList(userRealmRole));
        AidasAuthority currentAidasAuthority = aidasAuthorityRepository.findByName("ROLE_USER");
        aidasUser.setCurrentAidasAuthority(currentAidasAuthority);
    }

    public void updateUserToKeyCloak(AidasUser aidasUser) {
        RealmResource realmResource = keycloak.realm(keycloakConfig.getClientRealm());
        UsersResource usersRessource = realmResource.users();
        UserResource userResource = usersRessource.get(aidasUser.getKeycloakId());
        UserRepresentation user = userResource.toRepresentation();
        Map<String,List<String>> userAttrs = new HashMap<>();
        List<String> userAttrsVals = new ArrayList<>();
        userAttrsVals.add(String.valueOf(aidasUser.getId()));
        userAttrs.put("aidas_id",userAttrsVals);
        user.setAttributes(userAttrs);
        user.setEnabled(true);
        user.setUsername(aidasUser.getEmail());
        user.setFirstName(aidasUser.getFirstName());
        user.setLastName(aidasUser.getLastName());
        user.setEmail(aidasUser.getEmail());
        userResource.update(user);
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(aidasUser.getPassword());
        userResource.resetPassword(passwordCred);
        userResource.roles().realmLevel().add(aidasUser.getAidasAuthorities().stream().map(authority -> {return realmResource.roles().get(authority.getName()).toRepresentation();}).collect(Collectors.toList()));
    }

    private void deleteUserFromKeyCloak(AidasUser aidasUser){
        RealmResource realmResource = keycloak.realm(keycloakConfig.getClientRealm());
        UsersResource usersRessource = realmResource.users();
        usersRessource.delete(aidasUser.getKeycloakId());
    }

    private void updateCurrentRole(AidasUser aidasUser,String selectedRole){
        RealmResource realmResource = keycloak.realm(keycloakConfig.getClientRealm());
        UsersResource usersRessource = realmResource.users();
        UserResource userResource = usersRessource.get(aidasUser.getKeycloakId());
        UserRepresentation user = userResource.toRepresentation();
        List<String> userAttrsVals = new ArrayList<>();
        userAttrsVals.add(selectedRole);
        if(user.getAttributes()!=null) {
            user.getAttributes().put("current_role", userAttrsVals);
        }
        else{
            Map<String,List<String>> userAttrs = new HashMap<>();
            userAttrsVals.add(String.valueOf(aidasUser.getId()));
            userAttrs.put("current_role",userAttrsVals);
            user.setAttributes(userAttrs);
        }
        userResource.update(user);
    }
}
