package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.config.KeycloakConfig;
import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.dto.*;
import com.ainnotate.aidas.repository.*;
import com.ainnotate.aidas.repository.predicates.ProjectPredicatesBuilder;
import com.ainnotate.aidas.repository.predicates.UserPredicatesBuilder;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;

import com.ainnotate.aidas.web.rest.vm.ChangePasswordUserVM;
import com.ainnotate.aidas.web.rest.vm.ManagedUserVM;
import com.querydsl.core.types.dsl.BooleanExpression;

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
    private QcUsersOfCustomerRepository qcUsersOfCustomerRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AuthorityRepository authorityRepository;
    
    @Autowired
    private LanguageRepository languageRepository;
    
    @Autowired
    private UserLanguageMappingRepository userLanguageMappingRepository;

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
    private UserAuthorityMappingUserOrganisationMappingRepository userAuthorityUserOrganisationMappingRepository;
    
    @Autowired
    private UserAuthorityMappingUserCustomerMappingRepository userAuthorityUserCustomerMappingRepository;
    
    @Autowired
    private UserAuthorityMappingUserVendorMappingRepository userAuthorityUserVendorMappingRepository;

    @Autowired
    private UserAuthorityMappingRepository userAuthorityMappingRepository;

    @Autowired
    private UserVendorMappingObjectMappingRepository userVendorMappingObjectMappingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    TaskExecutor taskExecutor;

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
    @PostMapping("/aidas-users")
    public ResponseEntity<User> createAidasUser(@Valid @RequestBody User user) throws URISyntaxException {
        log.debug("REST request to save AidasUser : {}", user);
        User loggedInUser = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        User tmp = userRepository.getUserByEmail(user.getEmail());
        try {
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
        if( loggedInUser.getAuthority().getName().equals(AidasConstants.ORG_QC_USER)){
            if(user.getCustomer()!=null){
                if(!loggedInUser.getCustomer().equals(user.getCustomer())){
                    throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
                }
            }
        }
        try {
            try {
                addUserToKeyCloak(user);
                user.setDeleted(0);
                if(loggedInUser.getAuthority().getName().equals(AidasConstants.ADMIN)) {
                	user.setParentOrganisation(organisationRepository.getById(-1l));
                	user.setParentCustomer(customerRepository.getById(-1l));
                	user.setParentVendor(vendorRepository.getById(-1l));
                }else if(loggedInUser.getAuthority().getName().equals(AidasConstants.ORG_ADMIN)) {
                	user.setParentOrganisation(loggedInUser.getOrganisation());
                	user.setParentCustomer(null);
                	user.setParentVendor(null);
                } else if(loggedInUser.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN)) {
                	user.setParentOrganisation(null);
                	user.setParentCustomer(loggedInUser.getCustomer());
                	user.setParentVendor(null);
                }else if(loggedInUser.getAuthority().getName().equals(AidasConstants.VENDOR_ADMIN)) {
                	user.setParentOrganisation(null);
                	user.setParentCustomer(null);
                	user.setParentVendor(loggedInUser.getVendor());
                }
                
                User result = userRepository.save(user);
                if(user.getAuthorityDtos()!=null && user.getAuthorityDtos().size()>0){
                    Authority a =null;
                    for(UserAuthorityMappingDTO aid: user.getAuthorityDtos()){
                    	if(aid.getStatus().equals(AidasConstants.STATUS_ENABLED)){
	                        a = authorityRepository.getById(aid.getAuthorityId());
	                        UserAuthorityMapping uam = new UserAuthorityMapping();
	                        uam.setAuthority(a);
	                        uam.setUser(user);
	                        uam.setStatus(aid.getStatus());
	                        userAuthorityMappingRepository.save(uam);
	                        if(a.getName().equals(AidasConstants.ORG_ADMIN)) {
	                            if(user.getAdminOrgDtos()!=null && user.getAdminOrgDtos().size()>0){
	                                Organisation o =null;
	                                
	                                for(UserOrganisationMappingDTO oid: user.getAdminOrgDtos()){
	                                	UserOrganisationMapping uom = userOrganisationMappingRepository.findByOrganisationIdAndUserId(oid.getOrganisationId(), user.getId());
	                                	if(uom==null) {
	        	                            o = organisationRepository.getById(oid.getOrganisationId());
	        	                            uom = new UserOrganisationMapping();
	        	                            uom.setOrganisation(o);
	        	                            uom.setUser(user);
	        	                            uom.setStatus(oid.getStatus());
	        	                            userOrganisationMappingRepository.save(uom);
	        	                            UserAuthorityMappingUserOrganisationMapping uamuom = new UserAuthorityMappingUserOrganisationMapping();
	        	                            uamuom.setUserOrganisationMapping(uom);
	        	                            uamuom.setUserAuthorityMapping(uam);
	        	                            uamuom.setStatus(oid.getStatus());
	        	                            userAuthorityUserOrganisationMappingRepository.save(uamuom);
	                                	}
	                                }
	                                if(o!=null){
	                                    user.setOrganisation(o);
	                                }
	                            }
	                        }
	                        if(a.getName().equals(AidasConstants.CUSTOMER_ADMIN)) {
	                        	if(user.getAdminCustomerDtos()!=null && user.getAdminCustomerDtos().size()>0){
	                                Customer c =null;
	                                for(UserCustomerMappingDTO cid: user.getAdminCustomerDtos()){
	                                	UserCustomerMapping ucm = userCustomerMappingRepository.findByCustomerIdAndUserId(cid.getCustomerId(),user.getId());
	                                	if(ucm==null) {
	        	                            c = customerRepository.getById(cid.getCustomerId());
	        	                            ucm = new UserCustomerMapping();
	        	                            ucm.setCustomer(c);
	        	                            ucm.setUser(user);
	        	                            ucm.setStatus(cid.getStatus());
	        	                            userCustomerMappingRepository.save(ucm);
	        	                            UserAuthorityMappingUserCustomerMapping uamucm = new UserAuthorityMappingUserCustomerMapping();
	        	                            uamucm.setUserCustomerMapping(ucm);
	        	                            uamucm.setUserAuthorityMapping(uam);
	        	                            uamucm.setStatus(cid.getStatus());
	        	                            userAuthorityUserCustomerMappingRepository.save(uamucm);
	                                	}
	                                }
	                                if(c!=null){
	                                    user.setCustomer(c);
	                                }
	                            }
	                        }
	                        if(a.getName().equals(AidasConstants.VENDOR_ADMIN)) {
	                        	 if(user.getAdminVendorDtos()!=null && user.getAdminVendorDtos().size()>0){
	                                 Vendor v = null;
	                                 for(UserVendorMappingDTO vid: user.getAdminVendorDtos()){
	                                	 UserVendorMapping uvm = userVendorMappingRepository.findByUserAndVendor(vid.getVendorId(), user.getId());
	                                     if(uvm==null) {
	        	                             v = vendorRepository.getById(vid.getVendorId());
	        	                             uvm = new UserVendorMapping();
	        	                             uvm.setVendor(v);
	        	                             uvm.setUser(user);
	        	                             uvm.setStatus(vid.getStatus());
	        	                             userVendorMappingRepository.save(uvm);
	        	                             UserAuthorityMappingUserVendorMapping uamuvm = new UserAuthorityMappingUserVendorMapping();
	        	                             uamuvm.setUserVendorMapping(uvm);
	        	                             uamuvm.setUserAuthorityMapping(uam);
	        	                             uamuvm.setStatus(vid.getStatus());
	        	                             userAuthorityUserVendorMappingRepository.save(uamuvm);
	                                     }
	                                 }
	                                 if(v!=null){
	                                     user.setVendor(v);
	                                 }
	                             }
	                        }
	                        if(a.getName().equals(AidasConstants.VENDOR_USER)) {
	                        	 if(user.getUserVendorDtos()!=null && user.getUserVendorDtos().size()>0){
	                                 Vendor v = null;
	                                 for(UserVendorMappingDTO vid: user.getUserVendorDtos()){
	                                	 UserVendorMapping uvm = userVendorMappingRepository.findByUserAndVendor(vid.getVendorId(), user.getId());
	                                     if(uvm==null) {
	        	                             v = vendorRepository.getById(vid.getVendorId());
	        	                             uvm = new UserVendorMapping();
	        	                             uvm.setVendor(v);
	        	                             uvm.setUser(user);
	        	                             uvm.setStatus(vid.getStatus());
	        	                             userVendorMappingRepository.save(uvm);
	        	                             UserAuthorityMappingUserVendorMapping uamuvm = new UserAuthorityMappingUserVendorMapping();
	        	                             uamuvm.setUserVendorMapping(uvm);
	        	                             uamuvm.setUserAuthorityMapping(uam);
	        	                             uamuvm.setStatus(vid.getStatus());
	        	                             userAuthorityUserVendorMappingRepository.save(uamuvm);
	                                     }
	                                 }
	                                 if(v!=null){
	                                     user.setVendor(v);
	                                 }
	                             }
	                        }
	                        if(a.getName().equals(AidasConstants.ADMIN_QC_USER)) {
	                        	if(user.getQcAdminDtos()!=null && user.getQcAdminDtos().size()>0){
	                                Organisation o = organisationRepository.getById(-1l);;
	                               	UserOrganisationMapping uom = userOrganisationMappingRepository.findByOrganisationIdAndUserId(-1l, user.getId());
	                                    if(uom==null) {
	        	                            uom = new UserOrganisationMapping();
	        	                            uom.setOrganisation(o);
	        	                            uom.setUser(user);
	        	                            userOrganisationMappingRepository.save(uom);
	        	                            UserAuthorityMappingUserOrganisationMapping uamuom = new UserAuthorityMappingUserOrganisationMapping();
	        	                            uamuom.setUserOrganisationMapping(uom);
	        	                            uamuom.setUserAuthorityMapping(uam);
	        	                            uamuom.setStatus(AidasConstants.STATUS_ENABLED);
	        	                            userAuthorityUserOrganisationMappingRepository.save(uamuom);
	                                    }else {
	                                    	
	                                    }
	                              }
	                                
	                        }
	        				if(a.getName().equals(AidasConstants.ORG_QC_USER)) {
	        					if(user.getQcOrgDtos()!=null && user.getQcOrgDtos().size()>0){
	                                Organisation o =null;
	                                for(UserOrganisationMappingDTO oid: user.getQcOrgDtos()){
	                                	UserOrganisationMapping uom = userOrganisationMappingRepository.findByOrganisationIdAndUserId(oid.getOrganisationId(), user.getId());
	                                	if(uom==null) {
	        	                            o = organisationRepository.getById(oid.getOrganisationId());
	        	                            uom = new UserOrganisationMapping();
	        	                            uom.setOrganisation(o);
	        	                            uom.setUser(user);
	        	                            uom.setStatus(oid.getStatus());
	        	                            userOrganisationMappingRepository.save(uom);
	        	                            UserAuthorityMappingUserOrganisationMapping uamuom = new UserAuthorityMappingUserOrganisationMapping();
	        	                            uamuom.setUserOrganisationMapping(uom);
	        	                            uamuom.setUserAuthorityMapping(uam);
	        	                            uamuom.setStatus(oid.getStatus());
	        	                            userAuthorityUserOrganisationMappingRepository.save(uamuom);
	                                	}
	                                }
	                                if(o!=null){
	                                    user.setOrganisation(o);
	                                }
	                            }      	
	        				}
	        				if(a.getName().equals(AidasConstants.CUSTOMER_QC_USER)) {
	
	        					if(user.getQcCustomerDtos()!=null && user.getQcCustomerDtos().size()>0){
	                                Customer c =null;
	                                for(UserCustomerMappingDTO cid: user.getQcCustomerDtos()){
	                                	UserCustomerMapping ucm = userCustomerMappingRepository.findByCustomerIdAndUserId(cid.getCustomerId(),user.getId());
	                                	if(ucm==null) {
	        	                            c = customerRepository.getById(cid.getCustomerId());
	        	                            ucm = new UserCustomerMapping();
	        	                            ucm.setCustomer(c);
	        	                            ucm.setUser(user);
	        	                            ucm.setStatus(cid.getStatus());
	        	                            userCustomerMappingRepository.save(ucm);
	        	                            UserAuthorityMappingUserCustomerMapping uamucm = new UserAuthorityMappingUserCustomerMapping();
	        	                            uamucm.setUserCustomerMapping(ucm);
	        	                            uamucm.setUserAuthorityMapping(uam);
	        	                            uamucm.setStatus(cid.getStatus());
	        	                            userAuthorityUserCustomerMappingRepository.save(uamucm);
	        	                            
	                                	}
	                                }
	                                if(c!=null){
	                                    user.setCustomer(c);
	                                }
	                            }
	        				}
	        				if(a.getName().equals(AidasConstants.VENDOR_QC_USER)) {
	        					if(user.getQcVendorDtos()!=null && user.getAdminVendorDtos().size()>0){
	                                Vendor v = null;
	                                for(UserVendorMappingDTO vid: user.getAdminVendorDtos()){
	                                	UserVendorMapping uvm = userVendorMappingRepository.findByUserAndVendor(vid.getVendorId(), user.getId());
	                                    if(uvm==null) {
	        	                        	v = vendorRepository.getById(vid.getVendorId());
	        	                            uvm = new UserVendorMapping();
	        	                            uvm.setVendor(v);
	        	                            uvm.setUser(user);
	        	                            uvm.setStatus(vid.getStatus());
	        	                            userVendorMappingRepository.save(uvm);
	        	                            UserAuthorityMappingUserVendorMapping uamuvm = new UserAuthorityMappingUserVendorMapping();
	        	                            uamuvm.setUserVendorMapping(uvm);
	        	                            uamuvm.setUserAuthorityMapping(uam);
	        	                            uamuvm.setStatus(vid.getStatus());
	        	                            userAuthorityUserVendorMappingRepository.save(uamuvm);
	                                    }
	                                }
	                                if(v!=null){
	                                    user.setVendor(v);
	                                }
	                            }
	        				}
	                    }
                    }
                    if(a!=null){
                        user.setAuthority(a);
                    }
                    
                }
                if(user.getLanguageIds()!=null && user.getLanguageIds().size()>0) {
                	Language language = null;
                	for(UserLanguageMappingDTO ulmdto: user.getLanguageIds()) {
                		language = languageRepository.getById(ulmdto.getLanguageId());
                		UserLanguageMapping ulm = new UserLanguageMapping();
                		ulm.setLanguage(language);
                		ulm.setUser(user);
                	}
                }
                
                updateUserToKeyCloak(result);
                return ResponseEntity
                    .created(new URI("/api/aidas-users/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            }catch (Exception e){
                if(e.getMessage().equals("Create method returned status Conflict (Code: 409); expected status: Created (201)")){
                    throw new BadRequestAlertException("User already registered....", ENTITY_NAME, "useralreadyregistered");
                }else{
                    throw new BadRequestAlertException("Server Error....", ENTITY_NAME, "servererror"+e.getMessage());
                }
            }
        }catch(Exception e){
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "idexists");
        }
        }catch(Exception e) {
        	e.printStackTrace();
        	throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "idexists");
        }
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
    public ResponseEntity<User> registerAidasUser(@RequestBody ManagedUserVM newUser) throws URISyntaxException {
        log.debug("REST request to save AidasUser : {}", newUser);
        User user = new User();
        user.setEmail(newUser.getEmail());
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        user.setCountry(newUser.getCountry());
        user.setState(newUser.getState());
        user.setMobileNumber(newUser.getMobileNumber());
        user.setLocked(0);
        user.setPassword(newUser.getPassword());
        
        if (user.getId() != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID", ENTITY_NAME, "idexists");
        }
        registerNewUser(user);
        user.setDeleted(0);
        if(newUser.getLanguages()!=null) {
        	for(LanguageDTO lang :newUser.getLanguages()) {
        		UserLanguageMapping ulm = new UserLanguageMapping();
        		ulm.setUser(user);
        		ulm.setLanguage(languageRepository.getById(lang.getLangId()));
        		ulm.setProficiency(lang.getProficiency());
        		userLanguageMappingRepository.save(ulm);
        	}
        }
        Vendor defaultVendor = vendorRepository.getById(-1l);
        user.setVendor(defaultVendor);
        User result = userRepository.save(user);
        Object defaultObject = objectRepository.getById(-1l);
        UserVendorMappingObjectMapping auao = new UserVendorMappingObjectMapping();
        UserVendorMapping auavm = new UserVendorMapping();
        auavm.setUser(result);
        auavm.setVendor(defaultVendor);
        auavm = userVendorMappingRepository.save(auavm);
        auao.setUserVendorMapping(auavm);
        auao.setObject(defaultObject);
        
        UserAuthorityMapping uam = new UserAuthorityMapping();
        uam.setUser(result);
        Authority authority = authorityRepository.getById(5l);
        uam.setAuthority(authority);
        userAuthorityMappingRepository.save(uam);
        
        userVendorMappingObjectMappingRepository.save(auao);
        updateUserToKeyCloak(result);
        return ResponseEntity
            .created(new URI("/api/aidas-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code POST  /aidas-users} : Create a new user.
     *
     * @param cpwd the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the user has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/change-secret")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordUserVM cpwd) throws URISyntaxException {
        log.debug("REST request to save AidasUser : {}", cpwd);
        User user =null;
        if(cpwd.getEmail()!=null){
            user = userRepository.getByEmail(cpwd.getEmail());
        }
        boolean changePassword=false;
        if(user!=null)
        	changePassword = changePassword(user,cpwd.getPassword());
        if(changePassword){
        	return ResponseEntity.ok().body("Password reset successfully.");
        }else{
        	return ResponseEntity.ok().body("Unable to reset password.");
        }
    }
    
    /**
     * {@code POST  /aidas-users} : Create a new user.
     *
     * @param cpwd the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the user has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/forgot-secret")
    public ResponseEntity<String> forgotPassword(@RequestBody ChangePasswordUserVM cpwd) throws URISyntaxException {
        log.debug("REST request to save AidasUser : {}", cpwd);
        User user =null;
        if(cpwd.getEmail()!=null){
            user = userRepository.getByEmail(cpwd.getEmail());
        }
        boolean changePassword=false;
        if(user!=null) {
         changePassword = changePassword(user,cpwd.getPassword());
        }
        if(changePassword){
        	return ResponseEntity.ok().body("Please check your email for reset password.");
        }else{
        	return ResponseEntity.ok().body("Unable to update password.  Please enter correct email.");
        }
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
    @GetMapping("/aidas-users/changeEntity/{entityType}/{entityId}")
     public ResponseEntity<String> updateCurrentEntity(@Valid @PathVariable String entityType,@Valid @PathVariable Long entityId) throws URISyntaxException {
        log.debug("REST request to update current role of AidasUser : {}", SecurityUtils.getCurrentUserLogin().get());
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        if(entityType.equals("ROLE_ORG_ADMIN")) {
        	Organisation currentOrganisation = organisationRepository.getById(entityId);
        	user.setOrganisation(currentOrganisation);
        }else if(entityType.equals("ROLE_CUSTOMER_ADMIN")) {
        	Customer currentCustomer = customerRepository.getById(entityId);
        	user.setCustomer(currentCustomer);
        }else if(entityType.equals("ROLE_VENDOR_ADMIN")) {
        	Vendor currentVendor  = vendorRepository.getById(entityId);
        	user.setVendor(currentVendor);
        }else if(entityType.equals("ROLE_VENDOR_USER")) {
        	Vendor currentVendor  = vendorRepository.getById(entityId);
        	user.setVendor(currentVendor);
        }else if(entityType.equals("ROLE_ORG_QC")) {
        	Organisation currentOrganisation = organisationRepository.getById(entityId);
        	user.setOrganisation(currentOrganisation);
        }else if(entityType.equals("ROLE_CUSTOMER_QC")) {
        	Customer currentCustomer = customerRepository.getById(entityId);
        	user.setCustomer(currentCustomer);
        }else if(entityType.equals("ROLE_VENDOR_QC")) {
        	Vendor currentVendor  = vendorRepository.getById(entityId);
        	user.setVendor(currentVendor);
        }
        userRepository.save(user);
        return ResponseEntity
            .created(new URI("/api/aidas-users/" + user.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, user.getId().toString()))
            .body("Successfully changed");
    }

    /**
     * {@code POST  /aidas-users/customer/:customerId} : Update/change current role of the user.
     *
     * @param customerId the role to switch.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the user has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
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

    
    
    @GetMapping("/aidas-users/changeEntity/{entityType}")
    public ResponseEntity<List<NameValueHolderDto>> getOrgCustVendList(@Valid @PathVariable String entityType) throws URISyntaxException {
        log.debug("REST request to update current role of AidasUser : {}", SecurityUtils.getCurrentUserLogin().get());
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        List<NameValueHolderDto> list =  new ArrayList<>();
        user.setOrganisation(null);
        user.setVendor(null);
        user.setCustomer(null);
        Authority a = authorityRepository.findByName(entityType);
        user.setAuthority(a);
        if(entityType.equals("ROLE_ORG_ADMIN")) {
        	List<Organisation> orgs = organisationRepository.getOrganisations(user.getId(),user.getAuthority().getId());
        	for(Organisation o:orgs) {
        		NameValueHolderDto n = new NameValueHolderDto();
        		if(user.getOrganisation()!=null && user.getOrganisation().getId().equals(o.getId())) {
        			n.setLastLoggedInRole(true);
        		}
        		n.setId(o.getId());
        		n.setName(o.getName());
        		list.add(n);
        	}
        }else if(entityType.equals("ROLE_CUSTOMER_ADMIN")) {
        	List<Customer> custs = customerRepository.getCustomers(user.getId(),user.getAuthority().getId());
        	for(Customer c:custs) {
        		NameValueHolderDto n = new NameValueHolderDto();
        		if(user.getCustomer()!=null && user.getCustomer().getId().equals(c.getId())) {
        			n.setLastLoggedInRole(true);
        		}
        		n.setId(c.getId());
        		n.setName(c.getName());
        		list.add(n);
        	}
        }else if(entityType.equals("ROLE_VENDOR_ADMIN")) {
        	Authority auth = authorityRepository.findByName(entityType);
        	List<Vendor> vendors = vendorRepository.getVendors(user.getId(),auth.getId());
        	for(Vendor v:vendors) {
        		NameValueHolderDto n = new NameValueHolderDto();
        		if(user.getVendor()!=null && user.getVendor().getId().equals(v.getId())) {
        			n.setLastLoggedInRole(true);
        		}
        		n.setId(v.getId());
        		n.setName(v.getName());
        		list.add(n);
        	}
        }else if(entityType.equals("ROLE_VENDOR_USER")) {
        	Authority auth = authorityRepository.findByName(entityType);
        	List<Vendor> vendors = vendorRepository.getVendors(user.getId(),auth.getId());
        	for(Vendor v:vendors) {
        		NameValueHolderDto n = new NameValueHolderDto();
        		if(user.getVendor()!=null && user.getVendor().getId().equals(v.getId())) {
        			n.setLastLoggedInRole(true);
        		}
        		n.setId(v.getId());
        		n.setName(v.getName());
        		list.add(n);
        	}
        }else if(entityType.equals("ROLE_ORG_QC")) {
        	List<Organisation> orgs = organisationRepository.getOrganisations(user.getId(),user.getAuthority().getId());
        	for(Organisation o:orgs) {
        		NameValueHolderDto n = new NameValueHolderDto();
        		if(user.getOrganisation()!=null && user.getOrganisation().getId().equals(o.getId())) {
        			n.setLastLoggedInRole(true);
        		}
        		n.setId(o.getId());
        		n.setName(o.getName());
        		list.add(n);
        	}
        }
        else if(entityType.equals("ROLE_CUSTOMER_QC")) {
        	List<Customer> custs = customerRepository.getCustomers(user.getId(),user.getAuthority().getId());
        	for(Customer c:custs) {
        		NameValueHolderDto n = new NameValueHolderDto();
        		if(user.getCustomer()!=null && user.getCustomer().getId().equals(c.getId())) {
        			n.setLastLoggedInRole(true);
        		}
        		n.setId(c.getId());
        		n.setName(c.getName());
        		list.add(n);
        	}
        }else if(entityType.equals("ROLE_VENDOR_QC")) {
        	Authority auth = authorityRepository.findByName(entityType);
        	List<Vendor> vendors = vendorRepository.getVendors(user.getId(),auth.getId());
        	for(Vendor v:vendors) {
        		NameValueHolderDto n = new NameValueHolderDto();
        		if(user.getVendor()!=null && user.getVendor().getId().equals(v.getId())) {
        			n.setLastLoggedInRole(true);
        		}
        		n.setId(v.getId());
        		n.setName(v.getName());
        		list.add(n);
        	}
        }
        userRepository.save(user);
        return ResponseEntity
            .created(new URI("/api/aidas-users/" + user.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, user.getId().toString()))
            .body(list);
    }
    
    
    @GetMapping("/aidas-users/signout")
    public ResponseEntity<User> signOut() throws URISyntaxException {
        log.debug("REST request to update current role of AidasUser : {}", SecurityUtils.getCurrentUserLogin().get());
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        user.setVendor(null);
        user.setCustomer(null);
        user.setOrganisation(null);
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
        
        
        
        if(user.getAuthorityDtos()!=null && user.getAuthorityDtos().size()>0){
            Authority a =null;
            for(UserAuthorityMappingDTO aid: user.getAuthorityDtos()){
	            	if(aid.getStatus().equals(AidasConstants.STATUS_ENABLED)){
	            		UserAuthorityMapping uam = null;//
	            		if(aid.getAuthorityId()>0) {
	            			a = authorityRepository.getByUamId(aid.getAuthorityId());
	            			uam = userAuthorityMappingRepository.getById(aid.getAuthorityId());
	            		}else {
	            			a = authorityRepository.findByName(aid.getName().trim());
	            		}
		            	
		                
		                if(uam!=null) {
		                	uam.setStatus(aid.getStatus());
		                }else {
			                uam = new UserAuthorityMapping();
		                	uam.setAuthority(a);
			                uam.setUser(user);
			                uam.setStatus(aid.getStatus());
		                }
		                userAuthorityMappingRepository.save(uam);
		                if(a.getName().equals(AidasConstants.ORG_ADMIN)) {
		                    if(user.getAdminOrgDtos()!=null && user.getAdminOrgDtos().size()>0){
		                        Organisation o =null;
		                        for(UserOrganisationMappingDTO oid: user.getAdminOrgDtos()){
		                        	UserOrganisationMapping uom = userOrganisationMappingRepository.findByOrganisationIdAndUserId(oid.getOrganisationId(), user.getId());
		                        	if(uom==null && oid.getStatus().equals(AidasConstants.STATUS_ENABLED)) {
			                            o = organisationRepository.getById(oid.getOrganisationId());
			                            uom = new UserOrganisationMapping();
			                            uom.setOrganisation(o);
			                            uom.setUser(user);
			                            userOrganisationMappingRepository.save(uom);
			                            UserAuthorityMappingUserOrganisationMapping uamuom = new UserAuthorityMappingUserOrganisationMapping();
			                            uamuom.setUserOrganisationMapping(uom);
			                            uamuom.setUserAuthorityMapping(uam);
			                            uamuom.setStatus(AidasConstants.STATUS_ENABLED);
			                            userAuthorityUserOrganisationMappingRepository.save(uamuom);
		                        	}else {
		                        		UserAuthorityMappingUserOrganisationMapping uamuom = userAuthorityUserOrganisationMappingRepository.getByUomIdAndUamId(uom.getId(), uam.getId());
			                            uom.setStatus(oid.getStatus());
		                        		if(uamuom!=null) {
			                            	uamuom.setStatus(oid.getStatus());
			                            }else {
			                            	uamuom = new UserAuthorityMappingUserOrganisationMapping();
			                        		uamuom.setUserOrganisationMapping(uom);
				                            uamuom.setUserAuthorityMapping(uam);
				                            uamuom.setStatus(oid.getStatus());
			                            }
			                            userAuthorityUserOrganisationMappingRepository.save(uamuom);
		                        	}
		                        }
		                    }
		                }
		                if(a.getName().equals(AidasConstants.CUSTOMER_ADMIN)) {
		                	if(user.getAdminCustomerDtos()!=null && user.getAdminCustomerDtos().size()>0){
		                        Customer c =null;
		                        for(UserCustomerMappingDTO cid: user.getAdminCustomerDtos()){
		                        	UserCustomerMapping ucm = userCustomerMappingRepository.findByCustomerIdAndUserId(cid.getCustomerId(),user.getId());
		                        	if(ucm==null && cid.getStatus().equals(AidasConstants.STATUS_ENABLED)) {
			                            c = customerRepository.getById(cid.getCustomerId());
			                            ucm = new UserCustomerMapping();
			                            ucm.setCustomer(c);
			                            ucm.setUser(user);
			                            userCustomerMappingRepository.save(ucm);
			                            UserAuthorityMappingUserCustomerMapping uamucm = new UserAuthorityMappingUserCustomerMapping();
			                            uamucm.setUserCustomerMapping(ucm);
			                            uamucm.setUserAuthorityMapping(uam);
			                            uamucm.setStatus(AidasConstants.STATUS_ENABLED);
			                            userAuthorityUserCustomerMappingRepository.save(uamucm);
		                        	}else {
		                        		UserAuthorityMappingUserCustomerMapping uamucm = userAuthorityUserCustomerMappingRepository.getByUcmIdAndUamId(ucm.getId(), uam.getId());
			                            ucm.setStatus(cid.getStatus());
		                        		if(uamucm!=null) {
			                            	uamucm.setStatus(cid.getStatus());
			                            }else {
			                            	uamucm = new UserAuthorityMappingUserCustomerMapping();
				                            uamucm.setUserCustomerMapping(ucm);
				                            uamucm.setUserAuthorityMapping(uam);
				                            uamucm.setStatus(cid.getStatus());
			                            }
			                            userAuthorityUserCustomerMappingRepository.save(uamucm);
		                        	}
		                        }
		                    }
		                }
		                if(a.getName().equals(AidasConstants.VENDOR_ADMIN)) {
		                	 if(user.getAdminVendorDtos()!=null && user.getAdminVendorDtos().size()>0){
		                         Vendor v = null;
		                         for(UserVendorMappingDTO vid: user.getAdminVendorDtos()){
		                        	 UserVendorMapping uvm = userVendorMappingRepository.findByUserIdAndVendorId(vid.getVendorId(), user.getId(),a.getId());
		                             if(uvm==null && vid.getStatus().equals(AidasConstants.STATUS_ENABLED)) {
			                             v = vendorRepository.getById(vid.getVendorId());
			                             uvm = new UserVendorMapping();
			                             uvm.setVendor(v);
			                             uvm.setUser(user);
			                             userVendorMappingRepository.save(uvm);
			                             UserAuthorityMappingUserVendorMapping uamuvm = new UserAuthorityMappingUserVendorMapping();
			                             uamuvm.setUserVendorMapping(uvm);
			                             uamuvm.setUserAuthorityMapping(uam);
			                             uamuvm.setStatus(AidasConstants.STATUS_ENABLED);
			                             userAuthorityUserVendorMappingRepository.save(uamuvm);
		                             }else {
		                        		UserAuthorityMappingUserVendorMapping uamuvm = userAuthorityUserVendorMappingRepository.getByUvmIdAndUamId(uvm.getId(), uam.getId());
		                        		uvm.setStatus(vid.getStatus());
			                            if(uamuvm!=null) {
			                            	uamuvm.setStatus(vid.getStatus());
			                            }else {
			                            	uamuvm = new UserAuthorityMappingUserVendorMapping();
			                            	uamuvm.setUserVendorMapping(uvm);
			                            	uamuvm.setUserAuthorityMapping(uam);
			                            	uamuvm.setStatus(vid.getStatus());
			                            }
			                            userAuthorityUserVendorMappingRepository.save(uamuvm);
		                        	}
		                         }
		                     }
		                }
		                if(a.getName().equals(AidasConstants.VENDOR_USER)) {
		                	 if(user.getUserVendorDtos()!=null && user.getUserVendorDtos().size()>0){
		                         Vendor v = null;
		                         for(UserVendorMappingDTO vid: user.getUserVendorDtos()){
		                        	 UserVendorMapping uvm = userVendorMappingRepository.findByUserIdAndVendorId(vid.getVendorId(), user.getId(),a.getId());
		                             if(uvm==null && vid.getStatus().equals(AidasConstants.STATUS_ENABLED)) {
			                             v = vendorRepository.getById(vid.getVendorId());
			                             uvm = new UserVendorMapping();
			                             uvm.setVendor(v);
			                             uvm.setUser(user);
			                             userVendorMappingRepository.save(uvm);
			                             UserAuthorityMappingUserVendorMapping uamuvm = new UserAuthorityMappingUserVendorMapping();
			                             uamuvm.setUserVendorMapping(uvm);
			                             uamuvm.setUserAuthorityMapping(uam);
			                             uamuvm.setStatus(AidasConstants.STATUS_ENABLED);
			                             userAuthorityUserVendorMappingRepository.save(uamuvm);
		                             }else {
		                        		UserAuthorityMappingUserVendorMapping uamuvm = userAuthorityUserVendorMappingRepository.getByUvmIdAndUamId(uvm.getId(), uam.getId());
			                            uvm.setStatus(vid.getStatus());
		                        		if(uamuvm!=null) {
			                            	uamuvm.setStatus(vid.getStatus());
			                            }else {
			                            	uamuvm = new UserAuthorityMappingUserVendorMapping();
			                            	uamuvm.setUserVendorMapping(uvm);
			                            	uamuvm.setUserAuthorityMapping(uam);
			                            	uamuvm.setStatus(vid.getStatus());
			                            }
			                            userAuthorityUserVendorMappingRepository.save(uamuvm);
		                        	}
		                         }
		                     }
		                }
		                if(a.getName().equals(AidasConstants.ADMIN_QC_USER)) {
		                	if(user.getQcAdminDtos()!=null && user.getQcAdminDtos().size()>0){
		                        Organisation o =null;
		                        for(UserOrganisationMappingDTO oid: user.getQcAdminDtos()){
		                        	UserOrganisationMapping uom = userOrganisationMappingRepository.findByOrganisationIdAndUserId(-1l, user.getId());
		                            if(uom==null && oid.getStatus().equals(AidasConstants.STATUS_ENABLED)) {
			                        	o = organisationRepository.getById(-1l);
			                            uom = new UserOrganisationMapping();
			                            uom.setOrganisation(o);
			                            uom.setUser(user);
			                            userOrganisationMappingRepository.save(uom);
			                            UserAuthorityMappingUserOrganisationMapping uamuom = new UserAuthorityMappingUserOrganisationMapping();
			                            uamuom.setUserOrganisationMapping(uom);
			                            uamuom.setUserAuthorityMapping(uam);
			                            uamuom.setStatus(AidasConstants.STATUS_ENABLED);
			                            userAuthorityUserOrganisationMappingRepository.save(uamuom);
		                            }else {
		                        		UserAuthorityMappingUserOrganisationMapping uamuom = userAuthorityUserOrganisationMappingRepository.getByUomIdAndUamId(uom.getId(), uam.getId());
			                            uom.setStatus(oid.getStatus());
		                        		if(uamuom!=null) {
			                            	uamuom.setStatus(oid.getStatus());
			                            }else {
			                            	uamuom = new UserAuthorityMappingUserOrganisationMapping();
			                        		uamuom.setUserOrganisationMapping(uom);
				                            uamuom.setUserAuthorityMapping(uam);
				                            uamuom.setStatus(oid.getStatus());
			                            }
			                            userAuthorityUserOrganisationMappingRepository.save(uamuom);
		                        	}
		                        }
		                    }   
		                }
						if(a.getName().equals(AidasConstants.ORG_QC_USER)) {
							if(user.getQcOrgDtos()!=null && user.getQcOrgDtos().size()>0){
		                        Organisation o =null;
		                        for(UserOrganisationMappingDTO oid: user.getQcOrgDtos()){
		                        	UserOrganisationMapping uom = userOrganisationMappingRepository.findByOrganisationIdAndUserId(oid.getOrganisationId(), user.getId());
		                        	if(uom==null && oid.getStatus().equals(AidasConstants.STATUS_ENABLED)) {
			                            o = organisationRepository.getById(oid.getOrganisationId());
			                            uom = new UserOrganisationMapping();
			                            uom.setOrganisation(o);
			                            uom.setUser(user);
			                            userOrganisationMappingRepository.save(uom);
			                            UserAuthorityMappingUserOrganisationMapping uamuom = new UserAuthorityMappingUserOrganisationMapping();
			                            uamuom.setUserOrganisationMapping(uom);
			                            uamuom.setUserAuthorityMapping(uam);
			                            uamuom.setStatus(AidasConstants.STATUS_ENABLED);
			                            userAuthorityUserOrganisationMappingRepository.save(uamuom);
		                        	}else{
		                        		UserAuthorityMappingUserOrganisationMapping uamuom = userAuthorityUserOrganisationMappingRepository.getByUomIdAndUamId(uom.getId(), uam.getId());
			                            uom.setStatus(oid.getStatus());
		                        		if(uamuom!=null) {
			                            	uamuom.setStatus(oid.getStatus());
			                            }else {
			                            	uamuom = new UserAuthorityMappingUserOrganisationMapping();
			                        		uamuom.setUserOrganisationMapping(uom);
				                            uamuom.setUserAuthorityMapping(uam);
				                            uamuom.setStatus(oid.getStatus());
			                            }
			                            userAuthorityUserOrganisationMappingRepository.save(uamuom);
		                        	}
		                        }
		                    }      	
						}
						if(a.getName().equals(AidasConstants.CUSTOMER_QC_USER)) {
		
							if(user.getQcCustomerDtos()!=null && user.getQcCustomerDtos().size()>0){
		                        Customer c =null;
		                        for(UserCustomerMappingDTO cid: user.getQcCustomerDtos()){
		                        	UserCustomerMapping ucm = userCustomerMappingRepository.findByCustomerIdAndUserId(cid.getCustomerId(),user.getId());
		                        	if(ucm==null && cid.getStatus().equals(AidasConstants.STATUS_ENABLED)) {
			                            c = customerRepository.getById(cid.getCustomerId());
			                            ucm = new UserCustomerMapping();
			                            ucm.setCustomer(c);
			                            ucm.setUser(user);
			                            userCustomerMappingRepository.save(ucm);
			                            UserAuthorityMappingUserCustomerMapping uamucm = new UserAuthorityMappingUserCustomerMapping();
			                            uamucm.setUserCustomerMapping(ucm);
			                            uamucm.setUserAuthorityMapping(uam);
			                            uamucm.setStatus(AidasConstants.STATUS_ENABLED);
			                            userAuthorityUserCustomerMappingRepository.save(uamucm);
			                            
		                        	}else {
		                        		UserAuthorityMappingUserCustomerMapping uamucm = userAuthorityUserCustomerMappingRepository.getByUcmIdAndUamId(ucm.getId(), uam.getId());
			                            ucm.setStatus(cid.getStatus());
		                        		if(uamucm!=null) {
			                            	uamucm.setStatus(cid.getStatus());
			                            }else {
			                            	uamucm = new UserAuthorityMappingUserCustomerMapping();
				                            uamucm.setUserCustomerMapping(ucm);
				                            uamucm.setUserAuthorityMapping(uam);
				                            uamucm.setStatus(cid.getStatus());
			                            }
			                            userAuthorityUserCustomerMappingRepository.save(uamucm);
		                        	}
		                        }
		                    }
						}
						if(a.getName().equals(AidasConstants.VENDOR_QC_USER)) {
							if(user.getQcVendorDtos()!=null && user.getAdminVendorDtos().size()>0){
		                        Vendor v = null;
		                        for(UserVendorMappingDTO vid: user.getAdminVendorDtos()){
		                        	UserVendorMapping uvm = userVendorMappingRepository.findByUserAndVendor(vid.getVendorId(), user.getId());
		                            if(uvm==null && vid.getStatus().equals(AidasConstants.STATUS_ENABLED)) {
			                        	v = vendorRepository.getById(vid.getVendorId());
			                            uvm = new UserVendorMapping();
			                            uvm.setVendor(v);
			                            uvm.setUser(user);
			                            userVendorMappingRepository.save(uvm);
			                            UserAuthorityMappingUserVendorMapping uamuvm = new UserAuthorityMappingUserVendorMapping();
			                            uamuvm.setUserVendorMapping(uvm);
			                            uamuvm.setUserAuthorityMapping(uam);
			                            uamuvm.setStatus(AidasConstants.STATUS_ENABLED);
			                            userAuthorityUserVendorMappingRepository.save(uamuvm);
		                            }else {
		                        		UserAuthorityMappingUserVendorMapping uamuvm = userAuthorityUserVendorMappingRepository.getByUvmIdAndUamId(uvm.getId(), uam.getId());
			                            uvm.setStatus(vid.getStatus());
		                        		if(uamuvm!=null) {
			                            	uamuvm.setStatus(vid.getStatus());
			                            }else {
			                            	uamuvm = new UserAuthorityMappingUserVendorMapping();
			                            	uamuvm.setUserVendorMapping(uvm);
			                            	uamuvm.setUserAuthorityMapping(uam);
			                            	uamuvm.setStatus(vid.getStatus());
			                            }
			                            userAuthorityUserVendorMappingRepository.save(uamuvm);
		                        	}
		                        }
		                    }
						}
	            }
            }
        }
        User result = userRepository.save(existingUser);
        result.setAuthorityDtos(user.getAuthorityDtos());
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
        Page<User> page = userRepository.findAll(pageable);
        if(loggedInUser.getAuthority().getName().equals(AidasConstants.ADMIN)){
            page = userRepository.findAllByIdGreaterThanAndDeletedIsFalse(0l,pageable);
        }
        if(loggedInUser.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && loggedInUser.getOrganisation()!=null ){
            page = userRepository.findAllByParentOrganisationId(pageable,loggedInUser.getOrganisation().getId());
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
        //Authority authority = authorityRepository.findByName(AidasConstants.QC_USER);
        List<Authority> authorities = authorityRepository.getAllAuthorities(Arrays.asList(new String[]{AidasConstants.ADMIN_QC_USER,AidasConstants.ORG_QC_USER,AidasConstants.CUSTOMER_QC_USER,AidasConstants.VENDOR_QC_USER }));
        ProjectQcDTO projectQcDTO = new ProjectQcDTO();
        Project project= projectRepository.getById(projectId);
        projectQcDTO.setProjectId(projectId);
        projectQcDTO.setCustomerId(project.getCustomer().getId());
        projectQcDTO.setName(project.getCustomer().getName());
        projectQcDTO.setQcUsers1(new ArrayList<>());
        List<QcUser> userDTOs = qcUsersOfCustomerRepository.getQcUserOfCustomer(projectId,project.getCustomer().getId());
        if(loggedInUser.getAuthority().getName().equals(AidasConstants.ADMIN)){
            userDTOs = qcUsersOfCustomerRepository.getQcUserOfAdmin(projectId,project.getCustomer().getId());
        }
        else if(loggedInUser.getAuthority().getName().equals(AidasConstants.ORG_ADMIN)){
            userDTOs = qcUsersOfCustomerRepository.getQcUserOfOrg(projectId,project.getCustomer().getId());
        }
        else if(loggedInUser.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN)){
            userDTOs = qcUsersOfCustomerRepository.getQcUserOfCustomer(projectId,project.getCustomer().getId());
        }
        else if(loggedInUser.getAuthority().getName().equals(AidasConstants.VENDOR_ADMIN)){
            userDTOs = qcUsersOfCustomerRepository.getQcUserOfVendor(projectId,project.getCustomer().getId());
        }

        projectQcDTO.setQcUsers1(userDTOs);
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
        User user = userRepository.getById(id);
        List<UserAuthorityMappingDTO> uams = userAuthorityMappingRepository.getAllAuthoritiesOfUser(user.getId());
        user.setAuthorityDtos(uams);
       
        Long tmp = -1l;
        for(UserAuthorityMappingDTO uamdto:uams) {
        	UserAuthorityMapping uam =null;
        	if(uamdto.getAuthorityId()>0)
        	 uam = userAuthorityMappingRepository.getById(uamdto.getAuthorityId());
        	 if(uam!=null && (uam.getAuthority().getName().equals(AidasConstants.ORG_ADMIN))) {
	        	user.setAdminOrgDtos(organisationRepository.getAllOrganisationsWithUamId(user.getId(),uam.getAuthority().getId()));
        	 }else if(uamdto.getName().equals(AidasConstants.ORG_ADMIN)) {
        		user.setAdminOrgDtos(organisationRepository.getAllOrganisationsWithoutUamId(user.getId()));
        	 }
        	 if(uam!=null && (uam.getAuthority().getName().equals(AidasConstants.ORG_QC_USER))) {
 	        	user.setQcOrgDtos(organisationRepository.getAllOrganisationsWithUamId(user.getId(),uam.getAuthority().getId()));
         	 }else if(uamdto.getName().equals(AidasConstants.ORG_QC_USER)){
         		user.setQcOrgDtos(organisationRepository.getAllOrganisationsWithoutUamId(user.getId()));
         	 }
        	 
        	 
        	 if(uam!=null && (uam.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN))) {
	            user.setAdminCustomerDtos(customerRepository.getAllCustomersWithUamId(user.getId(),uam.getAuthority().getId()));
        	 }else if(uamdto.getName().equals(AidasConstants.CUSTOMER_ADMIN)) {
        		 user.setAdminCustomerDtos(customerRepository.getAllCustomersWithoutUamId(user.getId()));
        	 }
        	 if(uam!=null && (uam.getAuthority().getName().equals(AidasConstants.CUSTOMER_QC_USER))) {
 	            user.setQcCustomerDtos(customerRepository.getAllCustomersWithUamId(user.getId(),uam.getAuthority().getId()));
         	 }else if(uamdto.getName().equals(AidasConstants.CUSTOMER_QC_USER)){
         		 user.setQcCustomerDtos(customerRepository.getAllCustomersWithoutUamId(user.getId()));
         	 }
        	 
        	 if(uam!=null && (uam.getAuthority().getName().equals(AidasConstants.VENDOR_ADMIN))) {
 	            user.setAdminVendorDtos(vendorRepository.getAllVendorsWithUamId(user.getId(),uam.getAuthority().getId()));
         	 }else if(uamdto.getName().equals(AidasConstants.VENDOR_ADMIN)) {
         		 user.setAdminVendorDtos(vendorRepository.getAllVendorsWithoutUamId(user.getId()));
         	 }
         	 if(uam!=null && (uam.getAuthority().getName().equals(AidasConstants.VENDOR_QC_USER))) {
  	            user.setQcVendorDtos(vendorRepository.getAllVendorsWithUamId(user.getId(),uam.getAuthority().getId()));
          	 }else if(uamdto.getName().equals(AidasConstants.VENDOR_QC_USER)){
          		 user.setQcVendorDtos(vendorRepository.getAllVendorsWithoutUamId(user.getId()));
          	 }
         	 if(uam!=null && (uam.getAuthority().getName().equals(AidasConstants.VENDOR_USER))) {
  	            user.setUserVendorDtos(vendorRepository.getAllVendorsWithUamId(user.getId(),uam.getAuthority().getId()));
          	 }else if(uamdto.getName().equals(AidasConstants.VENDOR_USER)){
          		 user.setUserVendorDtos(vendorRepository.getAllVendorsWithoutUamId(user.getId()));
          	 }
	    		/*user.setAdminVendorDtos(vendorRepository.getAllVendorsWithUamId(uam.getId()));
	    		user.setUserVendorDtos(vendorRepository.getAllVendorsWithUamId(uam.getId()));
	    		user.setQcAdminDtos(organisationRepository.getAllOrganisationsWithUamId(uam.getId()));
	    		user.setQcOrgDtos(organisationRepository.getAllOrganisationsWithUamId(uam.getId()));
	    		user.setQcCustomerDtos(customerRepository.getAllCustomersWithUamId(uam.getId()));		
	    		user.setQcVendorDtos(vendorRepository.getAllVendorsWithUamId(uam.getId()));*/
        	
			if(uamdto.getAuthorityId().equals(-2l)) {
				uamdto.setAuthorityId(tmp--);
			}
        }
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
            if(loggedInUser.getOrganisation().equals(user.getOrganisation())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if( loggedInUser.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) && user.getCustomer()!=null ){
            if(loggedInUser.getCustomer().equals(user.getCustomer())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if(loggedInUser.getAuthority().getName().equals(AidasConstants.VENDOR_ADMIN) ){
            if(loggedInUser.getVendor().equals(user.getVendor())){
                throw new BadRequestAlertException("Not Customer", ENTITY_NAME, "idexists");
            }
        }
        if(loggedInUser.getAuthority().getName().equals(AidasConstants.VENDOR_USER) ){
            if(loggedInUser.getVendor().equals(user.getVendor())){
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
        
            for(UserAuthorityMappingDTO aa:myUser.getAuthorityDtos()){
                RoleRepresentation rr = realmResource.roles().get(aa.getName()).toRepresentation();
                userResource.roles().realmLevel().add(Arrays.asList(rr));
            }
        
        
    }

    public void registerNewUser(User myUser) {

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(myUser.getEmail());
        user.setEmail(myUser.getEmail());
        user.setFirstName(myUser.getFirstName());
        user.setLastName(myUser.getLastName());
        myUser.setLogin(myUser.getEmail());
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
        //user.setEnabled(true);
        
        user.setEmailVerified(false);
        Response response = usersRessource.create(user);
        String userId = CreatedResponseUtil.getCreatedId(response);
        myUser.setKeycloakId(userId);
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(myUser.getPassword());
        
        org.keycloak.admin.client.resource.UserResource userResource = usersRessource.get(userId);
        userResource.resetPassword(passwordCred);
        userResource.sendVerifyEmail();
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
        userResource.roles().realmLevel().add(myUser.getAuthorityDtos().stream().map(authority -> {return realmResource.roles().get(authority.getName()).toRepresentation();}).collect(Collectors.toList()));
    }

    private boolean changePassword(User myUser,String password){
        RealmResource realmResource = keycloak.realm(keycloakConfig.getClientRealm());
        UsersResource usersRessource = realmResource.users();
        org.keycloak.admin.client.resource.UserResource userResource = usersRessource.get(myUser.getKeycloakId());
        UserRepresentation user = userResource.toRepresentation();
        //CredentialRepresentation passwordCred = new CredentialRepresentation();
        //passwordCred.setTemporary(false);
        //user.setEnabled(false);
       // passwordCred.setType(CredentialRepresentation.PASSWORD);
        //passwordCred.setValue(myUser.getPassword());
        //userResource.resetPassword(passwordCred);
        //userResource.update(user);
        //userResource.sendVerifyEmail();
        userResource.executeActionsEmail(List.of("UPDATE_PASSWORD"));
     
        return true;
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
    
    @GetMapping(value = "/search/users")
    @ResponseBody
    public ResponseEntity<List<User>> search(@RequestParam(value = "search") String search, Pageable pageable) {
        UserPredicatesBuilder builder = new UserPredicatesBuilder();

        if (search != null) {
            Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
            Matcher matcher = pattern.matcher(search + ",");
            while (matcher.find()) {
                builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
            }
        }
        builder.with("id", ">",-1);
        BooleanExpression exp = builder.build();
        Page<User> page = userRepository.findAll(exp,pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
