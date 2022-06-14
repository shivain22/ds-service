package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.config.KeycloakConfig;
import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.repository.*;
import com.ainnotate.aidas.repository.search.AidasUserSearchRepository;
import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.security.SecurityUtils;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;

import com.ainnotate.aidas.web.rest.vm.ManagedUserVM;
/*import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;*/
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
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
 * REST controller for managing {@link User}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserRepository userRepository;

    private Keycloak keycloak;

    @Autowired
    private KeycloakConfig keycloakConfig;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private ObjectRepository objectRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private UserVendorMappingRepository userVendorMappingRepository;

    @Autowired
    private UserVendorMappingObjectMappingRepository userVendorMappingObjectMappingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    private final AidasUserSearchRepository aidasUserSearchRepository;

    public UserResource(UserRepository userRepository, AidasUserSearchRepository aidasUserSearchRepository, Keycloak keycloak) {
        this.userRepository = userRepository;
        this.aidasUserSearchRepository = aidasUserSearchRepository;
        this.keycloak =  keycloak;
    }

    /**
     * {@code POST  /aidas-users} : Create a new user.
     *
     * @param user the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the user has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN, AidasConstants.VENDOR_ADMIN})
    @PostMapping("/aidas-users")
    public ResponseEntity<User> createAidasUser(@Valid @RequestBody User user) throws URISyntaxException {
        log.debug("REST request to save AidasUser : {}", user);
        User loggedInUser = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        User tmp = userRepository.getAidasUserByLogin(user.getEmail());
        if(tmp!=null){
            throw new BadRequestAlertException("User with login is already available in the system.  Please contact admin to reset your account.", ENTITY_NAME, "idexists");
        }
        if (user.getId() != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if(loggedInUser.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && loggedInUser.getOrganisation()!=null ){
            if(user.getOrganisation()!=null){
                Organisation organisation = organisationRepository.getById(user.getOrganisation().getId());
                if(!loggedInUser.getOrganisation().equals(organisation)){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
            if(user.getCustomer()!=null){
                Customer customer = customerRepository.getById(user.getCustomer().getId());
                if(!loggedInUser.getOrganisation().equals(customer.getOrganisation())){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
        }
        if( loggedInUser.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN)){
            if(user.getCustomer()!=null){
                if(!loggedInUser.getCustomer().equals(user.getOrganisation())){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
        }
        if( loggedInUser.getAuthority().getName().equals(AidasConstants.VENDOR_ADMIN)){
            if(user.getVendor()!=null){
                if(!loggedInUser.getVendor().equals(user.getVendor())){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
        }
        if( loggedInUser.getAuthority().getName().equals(AidasConstants.VENDOR_USER)){
            if(user.getVendor()!=null){
                if(!loggedInUser.getVendor().equals(user.getVendor())){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
        }
        if( loggedInUser.getAuthority().getName().equals(AidasConstants.QC_USER)){
            if(user.getCustomer()!=null){
                if(!loggedInUser.getCustomer().equals(user.getCustomer())){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
        }


        for(Authority aa: user.getAuthorities()){
            if(aa!=null && aa.getId()!=null){
                UserAuthorityMapping auaam = new UserAuthorityMapping();
                auaam.setUser(user);
                auaam.getAuthority(aa);
                user.getUserAuthorityMappings().add(auaam);
                user.setAuthority(aa);
            }
        }
        for(Organisation ao: user.getOrganisations()){
            if(ao!=null && ao.getId()!=null){
                UserOrganisationMapping auaom = new UserOrganisationMapping();
                auaom.setUser(user);
                auaom.setOrganisation(ao);
                user.getUserOrganisationMappings().add(auaom);
                user.setOrganisation(ao);
            }
        }
        for(Customer ac: user.getCustomers()){
            if(ac!=null && ac.getId()!=null){
                UserCustomerMapping auacm = new UserCustomerMapping();
                auacm.setUser(user);
                auacm.setCustomer(ac);
                user.getUserCustomerMappings().add(auacm);
                user.setCustomer(ac);
            }
        }
        for(Vendor av: user.getVednors()){
            if(av!=null && av.getId()!=null){
                UserVendorMapping auavm = new UserVendorMapping();
                auavm.setUser(user);
                auavm.setVendor(av);
                user.getUserVendorMappings().add(auavm);
                user.setVendor(av);
            }
        }
        addUserToKeyCloak(user);
        user.setDeleted(0);
        if(user.getOrganisation()!=null){
            UserOrganisationMapping uom = new UserOrganisationMapping();
            uom.setUser(user);
            uom.setOrganisation(user.getOrganisation());
            user.getUserOrganisationMappings().add(uom);
        }
        if(user.getCustomer()!=null){
            UserCustomerMapping ucm = new UserCustomerMapping();
            ucm.setUser(user);
            ucm.setCustomer(user.getCustomer());
            user.getUserCustomerMappings().add(ucm);
        }
        if(user.getVendor()!=null){
            UserVendorMapping uvm = new UserVendorMapping();
            uvm.setUser(user);
            uvm.setVendor(user.getVendor());
            user.getUserVendorMappings().add(uvm);
        }
        User result = userRepository.save(user);
        //aidasUserSearchRepository.save(result);
        if(result.getAuthorities()!=null && result.getAuthorities().size()>0){
            for(Authority a:result.getAuthorities()){
                result.getAuthority(a);
            }
        }
        updateUserToKeyCloak(result);

        /*String bucketName = "objects-upload";
        AWSCredentials credentials = new BasicAWSCredentials(
            "AKIART6S2XPD6CH4Y23I\n",
            "PjnrxkZXV1HX4wL+ycOJrnUE7LlBQwKsuRdc3OC4\n"
        );
        AmazonS3 s3client = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.AP_SOUTH_1)
            .build();

        s3client.putObject(
            bucketName,
            "reqs.txt",
            new File("C:\\Users\\shiva\\Downloads\\reqs.txt")
        );*/


        return ResponseEntity
            .created(new URI("/api/aidas-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    private static ByteBuffer getRandomByteBuffer(int size) throws IOException {
        byte[] b = new byte[size];
        new Random().nextBytes(b);
        return ByteBuffer.wrap(b);
    }
    /**
     * {@code POST  /aidas-users} : Create a new user.
     *
     * @param newUser the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the user has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerAidasUser(@Valid @RequestBody ManagedUserVM newUser) throws URISyntaxException {
        log.debug("REST request to save AidasUser : {}", newUser);
        User user = new User();
        user.setEmail(newUser.getEmail());
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        user.setLocked(0);
        user.setPassword(newUser.getPassword());
        if (user.getId() != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID", ENTITY_NAME, "idexists");
        }

        registerNewUser(user);
        user.setDeleted(0);
        User result = userRepository.save(user);

        Object defaultObject = objectRepository.getById(-1l);

        UserVendorMappingObjectMapping auao = new UserVendorMappingObjectMapping();
        Vendor defaultVendor = vendorRepository.getById(-1l);
        UserVendorMapping auavm = new UserVendorMapping();
        auavm.setUser(result);
        auavm.setVendor(defaultVendor);
        auavm = userVendorMappingRepository.save(auavm);

        auao.setUserVendorMapping(auavm);
        auao.setObject(defaultObject);
        userVendorMappingObjectMappingRepository.save(auao);
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
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the user has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-users/{role}")
    public ResponseEntity<User> updateCurrentRole(@Valid @PathVariable String role) throws URISyntaxException {
        log.debug("REST request to update current role of AidasUser : {}", SecurityUtils.getCurrentUserLogin().get());
        User loggedInUser = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        updateCurrentRole(user,role);
        Authority currentAuthority =  authorityRepository.findByName(role.trim());
        user.setAuthority(currentAuthority);
        userRepository.save(user);
        return ResponseEntity
            .created(new URI("/api/aidas-users/" + user.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, user.getId().toString()))
            .body(user);
    }


    /**
     * {@code PUT  /aidas-users/:id} : Updates an existing user.
     *
     * @param id the id of the user to save.
     * @param user the user to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated user,
     * or with status {@code 400 (Bad Request)} if the user is not valid,
     * or with status {@code 500 (Internal Server Error)} if the user couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-users/{id}")
    public ResponseEntity<User> updateAidasUser(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody User user
    ) throws URISyntaxException {
        log.debug("REST request to update AidasUser : {}, {}", id, user);
        User loggedInUser = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        if (user.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, user.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        if(loggedInUser.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && loggedInUser.getOrganisation()!=null ){
            if(user.getOrganisation()!=null){
                Organisation organisation = organisationRepository.getById(user.getOrganisation().getId());
                if(!loggedInUser.getOrganisation().equals(organisation)){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
            if(user.getCustomer()!=null){
                Customer customer = customerRepository.getById(user.getCustomer().getId());
                if(!loggedInUser.getOrganisation().equals(customer.getOrganisation())){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
        }
        if( loggedInUser.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN)){
            if(user.getCustomer()!=null){
                if(!loggedInUser.getCustomer().equals(user.getOrganisation())){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
        }
        if( loggedInUser.getAuthority().getName().equals(AidasConstants.VENDOR_ADMIN)){
            if(user.getVendor()!=null){
                if(!loggedInUser.getVendor().equals(user.getVendor())){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
        }
        User result = userRepository.save(user);
        aidasUserSearchRepository.save(result);
        updateUserToKeyCloak(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, user.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /aidas-users/:id} : Partial updates given fields of an existing user, field will ignore if it is null
     *
     * @param id the id of the user to save.
     * @param user the user to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated user,
     * or with status {@code 400 (Bad Request)} if the user is not valid,
     * or with status {@code 404 (Not Found)} if the user is not found,
     * or with status {@code 500 (Internal Server Error)} if the user couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-users/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<User> partialUpdateAidasUser(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody User user
    ) throws URISyntaxException {
        log.debug("REST request to partial update AidasUser partially : {}, {}", id, user);
        User loggedInUser = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        if (user.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, user.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        if(loggedInUser.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && loggedInUser.getOrganisation()!=null ){
            if(user.getOrganisation()!=null){
                Organisation organisation = organisationRepository.getById(user.getOrganisation().getId());
                if(!loggedInUser.getOrganisation().equals(organisation)){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
            if(user.getCustomer()!=null){
                Customer customer = customerRepository.getById(user.getCustomer().getId());
                if(!loggedInUser.getOrganisation().equals(customer.getOrganisation())){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
        }
        if( user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN)){
            if(user.getCustomer()!=null){
                if(!loggedInUser.getCustomer().equals(user.getOrganisation())){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
        }
        if( user.getAuthority().getName().equals(AidasConstants.VENDOR_ADMIN)){
            if(user.getVendor()!=null){
                if(!loggedInUser.getVendor().equals(user.getVendor())){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
        }

        Optional<User> result = userRepository
            .findById(user.getId())
            .map(existingAidasUser -> {
                if (user.getFirstName() != null) {
                    existingAidasUser.setFirstName(user.getFirstName());
                }
                if (user.getLastName() != null) {
                    existingAidasUser.setLastName(user.getLastName());
                }
                if (user.getEmail() != null) {
                    existingAidasUser.setEmail(user.getEmail());
                }
                if (user.getLocked() != null) {
                    existingAidasUser.setLocked(user.getLocked());
                }
                if (user.getPassword() != null) {
                    existingAidasUser.setPassword(user.getPassword());
                }

                return existingAidasUser;
            })
            .map(userRepository::save)
            .map(savedAidasUser -> {
                aidasUserSearchRepository.save(savedAidasUser);

                return savedAidasUser;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, user.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-users} : get all the aidasUsers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasUsers in body.
     */
    @GetMapping("/aidas-users")
    public ResponseEntity<List<User>> getAllAidasUsers(Pageable pageable) {
        log.debug("REST request to get a page of AidasUsers");
        User loggedInUser = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Page<User> page = null;//aidasUserRepository.findAll(pageable);
        if(loggedInUser.getAuthority().getName().equals(AidasConstants.ADMIN)){
            page = userRepository.findAllByIdGreaterThanAndDeletedIsFalse(0l,pageable);
        }
        if(loggedInUser.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && loggedInUser.getOrganisation()!=null ){
            page = userRepository.findAllByDeletedIsFalseAndAidasOrganisation_OrAidasCustomer_AidasOrganisation(pageable,loggedInUser.getOrganisation(),loggedInUser.getOrganisation());
        }
        if( loggedInUser.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) && loggedInUser.getCustomer()!=null ){
            page = userRepository.findAllByDeletedIsFalseAndAidasCustomer(pageable,loggedInUser.getCustomer().getId());
        }
        if(loggedInUser.getAuthority().getName().equals(AidasConstants.VENDOR_ADMIN) || loggedInUser.getAuthority().getName().equals(AidasConstants.VENDOR_USER)){
            page = userRepository.findAllByDeletedIsFalseAndAidasVendor(pageable,loggedInUser.getVendor());
        }
        if(loggedInUser.getAuthority().getName().equals(AidasConstants.VENDOR_USER)){

        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-users} : get all the aidasUsers.
     *
     *  @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasUsers in body.
     */
    @GetMapping("/aidas-qc-users")
    public ResponseEntity<List<User>> getAllAidasQcUsers() {
        log.debug("REST request to get a page of AidasQCUsers");
        User loggedInUser = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Authority authority = authorityRepository.findByName(AidasConstants.QC_USER);
        List<User> users = userRepository.findAllByAidasAuthoritiesEquals(authority.getId());
        return ResponseEntity.ok().body(users);
    }

    /**
     * {@code GET  /aidas-users} : get all the aidasUsers.
     *
     *  @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasUsers in body.
     */
    @GetMapping("/aidas-users/freelance-users")
    public ResponseEntity<List<User>> getAllFreeLanceUsers() {
        log.debug("REST request to get a page of AidasUsers");
        User loggedInUser = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        List<User> users = userRepository.findAllFreelanceUsers();
        return ResponseEntity.ok().body(users);
    }

    /**
     * {@code GET  /aidas-users/:id} : get the "id" user.
     *
     * @param id the id of the user to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the user, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        log.debug("REST request to get AidasUser : {}", id);
        User loggedInUser = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        User user = userRepository.getById(id);
         return ResponseEntity.ok().body(user);
    }



    /**
     * {@code DELETE  /aidas-users/:id} : delete the "id" user.
     *
     * @param id the id of the user to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aidas-users/{id}")
    public ResponseEntity<Void> deleteAidasUser(@PathVariable Long id) {
        log.debug("REST request to delete AidasUser : {}", id);
        User loggedInUser = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        User user = userRepository.getById(id);
        if(loggedInUser.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null ){
            if(!loggedInUser.getOrganisation().equals(user.getOrganisation())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( loggedInUser.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) && user.getCustomer()!=null ){
            if(!loggedInUser.getCustomer().equals(user.getCustomer())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if(loggedInUser.getAuthority().getName().equals(AidasConstants.VENDOR_ADMIN) ){
            if(!loggedInUser.getVendor().equals(user.getVendor())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if(loggedInUser.getAuthority().getName().equals(AidasConstants.VENDOR_USER) ){
            if(!loggedInUser.getVendor().equals(user.getVendor())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        //deleteUserFromKeyCloak(user);

        if(user !=null){
            user.setDeleted(1);
            user.setStatus(0);
            userRepository.save(user);
        }

        //aidasUserRepository.deleteById(id);
        //aidasUserSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-users?query=:query} : search for the user corresponding
     * to the query.
     *
     * @param query the query of the user search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/aidas-users")
    public ResponseEntity<List<User>> searchAidasUsers(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasUsers for query {}", query);
        User loggedInUser = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Page<User> page = aidasUserSearchRepository.search(query, pageable);
        if(loggedInUser.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && loggedInUser.getOrganisation()!=null) {
            Iterator<User> it = page.getContent().iterator();
            while(it.hasNext()){
                User user = it.next();
                if(!user.getOrganisation().equals(loggedInUser.getOrganisation())){
                    it.remove();
                }
            }
        }
        if(loggedInUser.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) && loggedInUser.getCustomer()!=null) {
            Iterator<User> it = page.getContent().iterator();
            while(it.hasNext()){
                User user = it.next();
                if(!user.getCustomer().equals(loggedInUser.getCustomer())){
                    it.remove();
                }
            }
        }
        if(loggedInUser.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && loggedInUser.getCustomer()!=null) {
            Iterator<User> it = page.getContent().iterator();
            while(it.hasNext()){
                User user = it.next();
                if(!user.getVendor().equals(loggedInUser.getVendor())){
                    it.remove();
                }
            }
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }



    public void addUserToKeyCloak(User myUser) {

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(myUser.getLogin());
        user.setFirstName(myUser.getFirstName());
        user.setLastName(myUser.getLastName());
        user.setEmail(myUser.getEmail());
        myUser.setCreatedBy(SecurityUtils.getCurrentUserLogin().get());
        myUser.setCreatedDate(Instant.now());
        myUser.setLastModifiedBy(SecurityUtils.getCurrentUserLogin().get());
        myUser.setLastModifiedDate(Instant.now());
        List<String> groups = new ArrayList<>();
        groups.add("Users");
        user.setGroups(groups);
        RealmResource realmResource = keycloak.realm(keycloakConfig.getClientRealm());
        UsersResource usersRessource = realmResource.users();
        user.setEnabled(true);
        user.setEmailVerified(true);
        Response response = usersRessource.create(user);
        String userId = CreatedResponseUtil.getCreatedId(response);
        myUser.setKeycloakId(userId);
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(myUser.getPassword());
        org.keycloak.admin.client.resource.UserResource userResource = usersRessource.get(userId);
        userResource.resetPassword(passwordCred);
        //userResource.sendVerifyEmail();
        List<RoleRepresentation> roleRepresentationList = realmResource.roles().list();
        for (RoleRepresentation roleRepresentation : roleRepresentationList)
        {
            for(Authority aa:myUser.getAuthorities()){
                System.out.println(aa.getName()+""+roleRepresentation.getName());
                if (roleRepresentation.getName().equals(aa.getName()))
                {
                    userResource.roles().realmLevel().add(Arrays.asList(roleRepresentation));
                    myUser.setAuthority(aa);
                }
            }
        }
    }

    public void registerNewUser(User myUser) {

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(user.getEmail());
        user.setEmail(user.getEmail());
        myUser.setLogin(user.getEmail());
        if(SecurityUtils.getCurrentUserLogin().get()!=null) {
            myUser.setCreatedBy(SecurityUtils.getCurrentUserLogin().get());
            myUser.setLastModifiedBy(SecurityUtils.getCurrentUserLogin().get());
        }else{
            myUser.setCreatedBy("self_registered");
            myUser.setLastModifiedBy("self_registered");
        }
        myUser.setCreatedDate(Instant.now());
        myUser.setLastModifiedDate(Instant.now());
        List<String> groups = new ArrayList<>();
        groups.add("Users");
        user.setGroups(groups);
        RealmResource realmResource = keycloak.realm(keycloakConfig.getClientRealm());
        UsersResource usersRessource = realmResource.users();
        user.setEnabled(true);
        user.setEmailVerified(true);
        Response response = usersRessource.create(user);
        String userId = CreatedResponseUtil.getCreatedId(response);
        myUser.setKeycloakId(userId);
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(myUser.getPassword());
        org.keycloak.admin.client.resource.UserResource userResource = usersRessource.get(userId);
        userResource.resetPassword(passwordCred);
        //userResource.sendVerifyEmail();
        RoleRepresentation userRealmRole = realmResource.roles().get(AidasConstants.VENDOR_USER).toRepresentation();
        userResource.roles().realmLevel().add(Arrays.asList(userRealmRole));
        Authority currentAuthority = authorityRepository.findByName("ROLE_VENDOR_USER");
        myUser.setAuthority(currentAuthority);
    }

    public void updateUserToKeyCloak(User myUser) {
        RealmResource realmResource = keycloak.realm(keycloakConfig.getClientRealm());
        UsersResource usersRessource = realmResource.users();
        org.keycloak.admin.client.resource.UserResource userResource = usersRessource.get(myUser.getKeycloakId());
        UserRepresentation user = userResource.toRepresentation();
        Map<String,List<String>> userAttrs = new HashMap<>();
        List<String> userAttrsVals = new ArrayList<>();
        userAttrsVals.add(String.valueOf(myUser.getId()));
        userAttrs.put("aidas_id",userAttrsVals);
        userAttrsVals = new ArrayList<>();
        userAttrsVals.add(String.valueOf(myUser.getAuthority().getName()));
        userAttrs.put("current_role",userAttrsVals);
        user.setAttributes(userAttrs);
        user.setEnabled(true);
        user.setUsername(user.getEmail());
        user.setFirstName(user.getFirstName());
        user.setLastName(user.getLastName());
        user.setEmail(user.getEmail());
        userResource.update(user);
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(myUser.getPassword());
        userResource.resetPassword(passwordCred);
        userResource.roles().realmLevel().add(myUser.getAuthorities().stream().map(authority -> {return realmResource.roles().get(authority.getName()).toRepresentation();}).collect(Collectors.toList()));
    }

    private void deleteUserFromKeyCloak(User user){
        RealmResource realmResource = keycloak.realm(keycloakConfig.getClientRealm());
        UsersResource usersRessource = realmResource.users();
        usersRessource.delete(user.getKeycloakId());
    }

    private void updateCurrentRole(User myUser, String selectedRole){
        RealmResource realmResource = keycloak.realm(keycloakConfig.getClientRealm());
        UsersResource usersRessource = realmResource.users();
        org.keycloak.admin.client.resource.UserResource userResource = usersRessource.get(myUser.getKeycloakId());
        UserRepresentation user = userResource.toRepresentation();
        List<String> userAttrsVals = new ArrayList<>();
        userAttrsVals.add(selectedRole);
        if(user.getAttributes()!=null) {
            user.getAttributes().put("current_role", userAttrsVals);
        }
        else{
            Map<String,List<String>> userAttrs = new HashMap<>();
            userAttrsVals.add(String.valueOf(user.getId()));
            userAttrs.put("current_role",userAttrsVals);
            user.setAttributes(userAttrs);
        }
        userResource.update(user);
    }
}
