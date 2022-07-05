package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.config.KeycloakConfig;
import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.dto.*;
import com.ainnotate.aidas.repository.*;
import com.ainnotate.aidas.repository.search.UserSearchRepository;
import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.security.SecurityUtils;
import com.ainnotate.aidas.service.UserAddingTask;
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
    private ProjectRepository projectRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private ObjectRepository objectRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private UserVendorMappingRepository userVendorMappingRepository;

    @Autowired
    private UserCustomerMappingRepository userCustomerMappingRepository;

    @Autowired
    private UserOrganisationMappingRepository userOrganisationMappingRepository;

    @Autowired
    private UserAuthorityMappingRepository userAuthorityMappingRepository;

    @Autowired
    private UserVendorMappingObjectMappingRepository userVendorMappingObjectMappingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    TaskExecutor userVendorMappingObjectMappingTaskExecutor;

    @Autowired
    UserAddingTask userVendorMappingObjectMappingTask;

    @Autowired
    private AppPropertyRepository appPropertyRepository;

    private final UserSearchRepository aidasUserSearchRepository;

    public UserResource(UserRepository userRepository, UserSearchRepository aidasUserSearchRepository, Keycloak keycloak) {
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

        if(user.getOrganisationIds()!=null && user.getOrganisationIds().size()>0){
            Organisation o =null;
            for(UserOrganisationMappingDTO oid: user.getOrganisationIds()){
                o = organisationRepository.getById(oid.getOrganisationId());
                UserOrganisationMapping uom = new UserOrganisationMapping();
                uom.setOrganisation(o);
                uom.setUser(user);
                user.getUserOrganisationMappings().add(uom);
            }
            if(o!=null){
                user.setOrganisation(o);
            }
        }
        UserCustomerMapping userCustomerMapping = null;
        if(user.getCustomerIds()!=null && user.getCustomerIds().size()>0){
            Customer c =null;
            for(UserCustomerMappingDTO cid: user.getCustomerIds()){
                c = customerRepository.getById(cid.getCustomerId());
                UserCustomerMapping ucm = new UserCustomerMapping();
                ucm.setCustomer(c);
                ucm.setUser(user);
                user.getUserCustomerMappings().add(ucm);
                userCustomerMapping = ucm;
            }
            if(c!=null){
                user.setCustomer(c);
            }
        }
        UserVendorMapping userVendorMapping = null;
        if(user.getVendorIds()!=null && user.getVendorIds().size()>0){
            Vendor v = null;
            for(UserVendorMappingDTO vid: user.getVendorIds()){
                v = vendorRepository.getById(vid.getVendorId());
                UserVendorMapping uvm = new UserVendorMapping();
                uvm.setVendor(v);
                uvm.setUser(user);
                user.getUserVendorMappings().add(uvm);
                userVendorMapping = uvm;
            }
            if(v!=null){
                user.setVendor(v);
            }
        }

        if(user.getAuthorityIds()!=null && user.getAuthorityIds().size()>0){
            Authority a =null;
            for(UserAuthorityMappingDTO aid: user.getAuthorityIds()){
                a = authorityRepository.getById(aid.getAuthorityId());
                UserAuthorityMapping uam = new UserAuthorityMapping();
                uam.setAuthority(a);
                uam.setUser(user);
                user.getUserAuthorityMappings().add(uam);
            }
            if(a!=null){
                user.setAuthority(a);
            }
        }

        addUserToKeyCloak(user);
        user.setDeleted(0);



        User result = userRepository.save(user);
        updateUserToKeyCloak(result);
        if(user!=null) {
            userVendorMappingObjectMappingTask.setUser(user);
            userVendorMappingObjectMappingTaskExecutor.execute(userVendorMappingObjectMappingTask);
        }
        if(user.getAuthority().getName().equals(AidasConstants.VENDOR_USER)) {
            if (userVendorMapping != null) {
                userVendorMappingObjectMappingTask.setUserVendorMapping(userVendorMapping);
                userVendorMappingObjectMappingTaskExecutor.execute(userVendorMappingObjectMappingTask);
            }
        }

        if(user.getAuthority().getName().equals(AidasConstants.QC_USER)) {
            if (userCustomerMapping != null) {
                userVendorMappingObjectMappingTask.setUserCustomerMapping(userCustomerMapping);
                userVendorMappingObjectMappingTaskExecutor.execute(userVendorMappingObjectMappingTask);
            }
        }
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
     * {@code POST  /aidas-users/organisation/:organisationId} : Update/change current role of the user.
     *
     * @param organisationId the role to switch.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the user has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-users/organisation/{organisationId}")
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN})
    public ResponseEntity<User> updateCurrentOrganisation(@Valid @PathVariable Long organisationId) throws URISyntaxException {
        log.debug("REST request to update current role of AidasUser : {}", SecurityUtils.getCurrentUserLogin().get());
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Organisation currentOrganisation = organisationRepository.getById(organisationId);
        user.setOrganisation(currentOrganisation);
        userRepository.save(user);
        return ResponseEntity
            .created(new URI("/api/aidas-users/" + user.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, user.getId().toString()))
            .body(user);
    }

    /**
     * {@code POST  /aidas-users/customer/:customerId} : Update/change current role of the user.
     *
     * @param customerId the role to switch.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the user has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN,AidasConstants.CUSTOMER_ADMIN,AidasConstants.QC_USER})
    @PostMapping("/aidas-users/customer/{customerId}")
    public ResponseEntity<User> updateCurrentCustomer(@Valid @PathVariable Long customerId) throws URISyntaxException {
        log.debug("REST request to update current role of AidasUser : {}", SecurityUtils.getCurrentUserLogin().get());
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Customer currentCustomer = customerRepository.getById(customerId);
        user.setCustomer(currentCustomer);
        userRepository.save(user);
        return ResponseEntity
            .created(new URI("/api/aidas-users/" + user.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, user.getId().toString()))
            .body(user);
    }

    /**
     * {@code POST  /aidas-users/vendor/:vendorId} : Update/change current role of the user.
     *
     * @param vendorId the role to switch.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the user has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN,AidasConstants.CUSTOMER_ADMIN,AidasConstants.VENDOR_ADMIN,AidasConstants.VENDOR_USER,AidasConstants.QC_USER})
    @PostMapping("/aidas-users/vendor/{vendorId}")
    public ResponseEntity<User> updateCurrentVendor(@Valid @PathVariable Long vendorId) throws URISyntaxException {
        log.debug("REST request to update current role of AidasUser : {}", SecurityUtils.getCurrentUserLogin().get());
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Vendor currentVendor = vendorRepository.getById(vendorId);
        user.setVendor(currentVendor);
        userRepository.save(user);
        return ResponseEntity
            .created(new URI("/api/aidas-users/" + user.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, user.getId().toString()))
            .body(user);
    }


    /**
     * {@code POST  /aidas-users/add/organisation/:organisationId/:userId} : Update/change current role of the user.
     *
     * @param organisationId the role to switch.
     * @param userId the role to switch.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the user has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-users/add/organisation/{organisationId}/{userId}")
    public ResponseEntity<User> addOrganisation( @Valid @PathVariable Long organisationId,@Valid @PathVariable Long userId) throws URISyntaxException {
        log.debug("REST request to update current role of AidasUser : {}", SecurityUtils.getCurrentUserLogin().get());
        User user = userRepository.getById(userId);
        Organisation organisation = organisationRepository.getById(organisationId);
        UserOrganisationMapping userOrganisationMapping = new UserOrganisationMapping();
        userOrganisationMapping.setOrganisation(organisation);
        userOrganisationMapping.setUser(user);
        user.getUserOrganisationMappings().add(userOrganisationMapping);
        userRepository.save(user);
        return ResponseEntity
            .created(new URI("/api/aidas-users/" + user.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, user.getId().toString()))
            .body(user);
    }

    /**
     * {@code POST  /aidas-users/add/customer/:customerId/:userId} : Update/change current role of the user.
     *
     * @param customerId the role to switch.
     * @param userId the role to switch.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the user has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-users/add/customer/{customerId}/{userId}")
    public ResponseEntity<User> addCustomer( @Valid @PathVariable Long customerId,@Valid @PathVariable Long userId) throws URISyntaxException {
        log.debug("REST request to update current role of AidasUser : {}", SecurityUtils.getCurrentUserLogin().get());
        User user = userRepository.getById(userId);
        Customer customer = customerRepository.getById(customerId);
        UserCustomerMapping userCustomerMapping = new UserCustomerMapping();
        userCustomerMapping.setCustomer(customer);
        userCustomerMapping.setUser(user);
        user.getUserCustomerMappings().add(userCustomerMapping);
        userRepository.save(user);
        return ResponseEntity
            .created(new URI("/api/aidas-users/" + user.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, user.getId().toString()))
            .body(user);
    }

    /**
     * {@code POST  /aidas-users/add/vendor/:userVendorMappingId} : Update/change current role of the user.
     *
     * @param vendorId the role to switch.
     * @param userId the role to switch.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the user has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-users/add/vendor/{vendorId}/{userId}")
    public ResponseEntity<User> addVendor(@Valid @PathVariable Long vendorId,@Valid @PathVariable Long userId) throws URISyntaxException {
        log.debug("REST request to update current role of AidasUser : {}", SecurityUtils.getCurrentUserLogin().get());
        User user = userRepository.getById(userId);
        Vendor vendor = vendorRepository.getById(vendorId);
        UserVendorMapping userVendorMapping = new UserVendorMapping();
        userVendorMapping.setVendor(vendor);
        userVendorMapping.setUser(user);
        user.getUserVendorMappings().add(userVendorMapping);
        userRepository.save(user);
        return ResponseEntity
            .created(new URI("/api/aidas-users/" + user.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, user.getId().toString()))
            .body(user);
    }

    /**
     * {@code POST  /aidas-users/add/authority/:userVendorMappingId} : Update/change current role of the user.
     *
     * @param authorityId the role to switch.
     * @param userId the role to switch.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the user has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-users/add/authority/{authorityId}/{userId}")
    public ResponseEntity<User> addAuthority(@Valid @PathVariable Long authorityId,@Valid @PathVariable Long userId) throws URISyntaxException {
        log.debug("REST request to update current role of AidasUser : {}", SecurityUtils.getCurrentUserLogin().get());
        User user = userRepository.getById(userId);
        Authority authority = authorityRepository.getById(authorityId);
        UserAuthorityMapping userAuthorityMapping = new UserAuthorityMapping();
        userAuthorityMapping.setAuthority(authority);
        userAuthorityMapping.setUser(user);
        user.getUserAuthorityMappings().add(userAuthorityMapping);
        userRepository.save(user);
        return ResponseEntity
            .created(new URI("/api/aidas-users/" + user.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, user.getId().toString()))
            .body(user);
    }


    /**
     * {@code POST  /aidas-users/remove/organisation/:userOrganisationMappingId} : Update/change current role of the user.
     *
     * @param userOrganisationMappingId the role to switch.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the user has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-users/remove/organisation/{userOrganisationMappingId}")
    public ResponseEntity<User> removeOrganisation(@Valid @PathVariable Long userOrganisationMappingId) throws URISyntaxException {
        log.debug("REST request to update current role of AidasUser : {}", SecurityUtils.getCurrentUserLogin().get());
        UserOrganisationMapping userOrganisationMapping = userOrganisationMappingRepository.getById(userOrganisationMappingId);
        User user = userOrganisationMapping.getUser();
        if(userOrganisationMapping!=null) {
            if (!user.getOrganisation().getId().equals(userOrganisationMapping.getOrganisation().getId())) {
                if (user.getUserOrganisationMappings().contains(userOrganisationMapping)) {
                    user.getUserOrganisationMappings().remove(userOrganisationMapping);
                }
            }
        }
        return ResponseEntity
            .created(new URI("/api/aidas-users/" + user.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, user.getId().toString()))
            .body(user);
    }

    /**
     * {@code POST  /aidas-users/remove/customer/:userCustomerMappingId} : Update/change current role of the user.
     *
     * @param userCustomerMappingId the role to switch.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the user has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-users/remove/customer/{userCustomerMappingId}")
    public ResponseEntity<User> removeCustomer(@Valid @PathVariable Long userCustomerMappingId) throws URISyntaxException {
        log.debug("REST request to update current role of AidasUser : {}", SecurityUtils.getCurrentUserLogin().get());
        UserCustomerMapping userCustomerMapping = userCustomerMappingRepository.getById(userCustomerMappingId);
        User user = userCustomerMapping.getUser();
        if(userCustomerMapping!=null) {
            if (!user.getCustomer().getId().equals(userCustomerMapping.getCustomer().getId())) {
                if (user.getUserCustomerMappings().contains(userCustomerMapping)) {
                    user.getUserCustomerMappings().remove(userCustomerMapping);
                }
            }
        }
        userRepository.save(user);
        return ResponseEntity
            .created(new URI("/api/aidas-users/" + user.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, user.getId().toString()))
            .body(user);
    }

    /**
     * {@code POST  /aidas-users/remove/vendor/:userVendorMappingId} : Update/change current role of the user.
     *
     * @param userVendorMappingId the role to switch.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the user has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-users/remove/vendor/{userVendorMappingId}")
    public ResponseEntity<User> removeVendor(@Valid @PathVariable Long userVendorMappingId) throws URISyntaxException {
        log.debug("REST request to update current role of AidasUser : {}", SecurityUtils.getCurrentUserLogin().get());
        UserVendorMapping userVendorMapping= userVendorMappingRepository.getById(userVendorMappingId);
        User user = userVendorMapping.getUser();
        if(userVendorMapping!=null) {
            if (!user.getVendor().getId().equals(userVendorMapping.getVendor().getId())) {
                if (user.getUserVendorMappings().contains(userVendorMapping)) {
                    user.getUserVendorMappings().remove(userVendorMapping);
                }
            }
        }
        userRepository.save(user);
        return ResponseEntity
            .created(new URI("/api/aidas-users/" + user.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, user.getId().toString()))
            .body(user);
    }

    /**
     * {@code POST  /aidas-users/remove/authority/:userAuthorityMappingId} : Update/change current role of the user.
     *
     * @param userAuthorityMappingId the role to switch.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the user has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-users/remove/authority/{userAuthorityMappingId}")
    public ResponseEntity<User> removeAuthority(@Valid @PathVariable Long userAuthorityMappingId) throws URISyntaxException {
        log.debug("REST request to update current role of AidasUser : {}", SecurityUtils.getCurrentUserLogin().get());
        UserAuthorityMapping userAuthorityMapping= userAuthorityMappingRepository.getById(userAuthorityMappingId);
        User user = userAuthorityMapping.getUser();
        if(userAuthorityMapping!=null) {
            if (!user.getVendor().getId().equals(userAuthorityMapping.getAuthority().getId())) {
                if (user.getUserAuthorityMappings().contains(userAuthorityMapping)) {
                    user.getUserAuthorityMappings().remove(userAuthorityMapping);
                }
            }
        }
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
        User existingUser= userRepository.getById(id);

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());

        if(user.getOrganisationIds()!=null && user.getOrganisationIds().size()>0){
            Organisation o =null;
            for(UserOrganisationMappingDTO oid: user.getOrganisationIds()){
                UserOrganisationMapping uom = userOrganisationMappingRepository.findByOrganisationIdAndUserId(oid.getOrganisationId(),user.getId());
                if(uom==null) {
                    uom=new UserOrganisationMapping();
                    o = organisationRepository.getById(oid.getOrganisationId());
                    uom.setOrganisation(o);
                    uom.setUser(user);
                    uom.setStatus(oid.getStatus());
                    existingUser.getUserOrganisationMappings().add(uom);
                }else{
                    UserOrganisationMapping finalUom = uom;
                    existingUser.getUserOrganisationMappings().stream().filter(item->item.equals(finalUom)).findFirst().get().setStatus(oid.getStatus());
                }
            }
        }
        if(user.getCustomerIds()!=null && user.getCustomerIds().size()>0){
            Customer c =null;
            for(UserCustomerMappingDTO cid: user.getCustomerIds()){
                UserCustomerMapping ucm = userCustomerMappingRepository.findByCustomerIdAndUserId(cid.getCustomerId(),user.getId());
                if(ucm==null) {
                    ucm = new UserCustomerMapping();
                    c = customerRepository.getById(cid.getCustomerId());
                    ucm.setCustomer(c);
                    ucm.setUser(user);
                    existingUser.getUserCustomerMappings().add(ucm);
                }else{
                    UserCustomerMapping finalUcm = ucm;
                    existingUser.getUserCustomerMappings().stream().filter(item->item.equals(finalUcm)).findFirst().get().setStatus(cid.getStatus());
                }
            }
        }
        if(user.getVendorIds()!=null && user.getVendorIds().size()>0){
            Vendor v = null;
            for(UserVendorMappingDTO vid: user.getVendorIds()){
                UserVendorMapping uvm = userVendorMappingRepository.findByVendorIdAndUserId(vid.getVendorId(),user.getId());
                if(uvm==null) {
                    uvm = new UserVendorMapping();
                    v = vendorRepository.getById(vid.getVendorId());
                    uvm.setVendor(v);
                    uvm.setUser(user);
                    existingUser.getUserVendorMappings().add(uvm);
                }else{
                    UserVendorMapping finalUvm = uvm;
                    existingUser.getUserVendorMappings().stream().filter(item->item.equals(finalUvm)).findFirst().get().setStatus(vid.getStatus());
                }
            }
        }
        if(user.getAuthorityIds()!=null && user.getAuthorityIds().size()>0){
            Authority a =null;
            for(UserAuthorityMappingDTO aid: user.getAuthorityIds()){
                UserAuthorityMapping uam = userAuthorityMappingRepository.findByAuthorityIdAndUserId(aid.getAuthorityId(),user.getId());
                if(uam==null) {
                    uam = new UserAuthorityMapping();
                    a = authorityRepository.getById(aid.getAuthorityId());
                    uam.setAuthority(a);
                    uam.setUser(user);
                    existingUser.getUserAuthorityMappings().add(uam);
                }else{
                    UserAuthorityMapping finalUam = uam;
                    existingUser.getUserAuthorityMappings().stream().filter(item->item.equals(finalUam)).findFirst().get().setStatus(aid.getStatus());
                }
            }
        }

        User result = userRepository.save(user);
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
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasUsers in body.
     */
    @GetMapping("/aidas-users/dropdown")
    public ResponseEntity<List<User>> getAllUsersForDropDown() {
        log.debug("REST request to get a page of AidasUsers");
        User loggedInUser = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        List<User> users = null;//aidasUserRepository.findAll(pageable);
        if(loggedInUser.getAuthority().getName().equals(AidasConstants.ADMIN)){
            users = userRepository.findAllByIdGreaterThanAndDeletedIsFalse(0l);
        }
        if(loggedInUser.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && loggedInUser.getOrganisation()!=null ){
            users = userRepository.findAllByDeletedIsFalseAndAidasOrganisation_OrAidasCustomer_AidasOrganisation(loggedInUser.getOrganisation(),loggedInUser.getOrganisation());
        }
        if( loggedInUser.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) && loggedInUser.getCustomer()!=null ){
            users = userRepository.findAllByDeletedIsFalseAndAidasCustomer(loggedInUser.getCustomer().getId());
        }
        if(loggedInUser.getAuthority().getName().equals(AidasConstants.VENDOR_ADMIN) || loggedInUser.getAuthority().getName().equals(AidasConstants.VENDOR_USER)){
            users = userRepository.findAllByDeletedIsFalseAndAidasVendor(loggedInUser.getVendor());
        }
        if(loggedInUser.getAuthority().getName().equals(AidasConstants.VENDOR_USER)){

        }
        return ResponseEntity.ok().body(users);
    }

    /**
     * {@code GET  /aidas-users} : get all the aidasUsers.
     *
     *  @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasUsers in body.
     */
    @GetMapping("/aidas-qc-users/{projectId}")
    public ResponseEntity<ProjectQcDTO> getAllAidasQcUsers(@PathVariable(value = "projectId", required = false) final Long projectId) {
        log.debug("REST request to get a page of AidasQCUsers");
        User loggedInUser = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Authority authority = authorityRepository.findByName(AidasConstants.QC_USER);
        ProjectQcDTO projectQcDTO = new ProjectQcDTO();
        Project project= projectRepository.getById(projectId);
        projectQcDTO.setProjectId(projectId);
        projectQcDTO.setCustomerId(project.getCustomer().getId());
        projectQcDTO.setName(project.getCustomer().getName());
        projectQcDTO.setQcUsers1(new ArrayList<>());
        List<IUserDTO> userDTOs = userRepository.findAllByQcUsersByCustomerAndProject(projectId);
        for (int j = 0; j < userDTOs.size(); j++) {
            IUserDTO u = userDTOs.get(j);
            UserDTO udto = new UserDTO();
            udto.setQcLevel(u.getQcLevel());
            udto.setUserCustomerMappingId(u.getUserCustomerMappingId());
            udto.setUserId(u.getUserId());
            udto.setFirstName(u.getFirstName());
            udto.setLastName(u.getLastName());
            udto.setLogin(u.getLogin());
            udto.setStatus(u.getStatus());
            projectQcDTO.getQcUsers1().add(udto);
        }
        return ResponseEntity.ok().body(projectQcDTO);
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
