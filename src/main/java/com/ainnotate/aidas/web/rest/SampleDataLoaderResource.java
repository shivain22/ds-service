package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.config.KeycloakConfig;
import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.repository.*;
import com.ainnotate.aidas.repository.search.*;
import com.ainnotate.aidas.security.SecurityUtils;
import com.ainnotate.aidas.service.CSVHelper;
import com.opencsv.exceptions.CsvException;
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
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link Authority}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SampleDataLoaderResource {

    private final Logger log = LoggerFactory.getLogger(SampleDataLoaderResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Autowired
    private OrganisationSearchRepository organisationSearchRepository;
    @Autowired
    private OrganisationRepository organisationRepository;
    @Autowired
    private CustomerSearchRepository customerSearchRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProjectSearchRepository projectSearchRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ObjectSearchRepository objectSearchRepository;
    @Autowired
    private ObjectRepository objectRepository;

    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private VendorSearchRepository vendorSearchRepository;

    @Autowired
    private UserSearchRepository userSearchRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private Keycloak keycloak;

    @Autowired
    private KeycloakConfig keycloakConfig;

    @Autowired
    private UserOrganisationMappingRepository userOrganisationMappingRepository;
    @Autowired
    private UserCustomerMappingRepository userCustomerMappingRepository;
    @Autowired
    private UserVendorMappingRepository userVendorMappingRepository;
    @Autowired
    private UserAuthorityMappingRepository userAuthorityMappingRepository;

    @Autowired
    private UserVendorMappingObjectMappingRepository userVendorMappingObjectMappingRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private ProjectPropertyRepository projectPropertyRepository;

    @Autowired
    private ObjectPropertyRepository objectPropertyRepository;


    @Autowired
    private UploadRepository uploadRepository;

    @Autowired
    ResourceLoader resourceLoader;
    /**
     * {@code GET  /aidas-authorities/:id} : get the "id" authority.
     *
     * @param dataFileName the id of the authority to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the authority, or with status {@code 404 (Not Found)}.
     */
   @GetMapping("/aidas-sample/{dataFileName}")
    public ResponseEntity<String> loadSampleData(@PathVariable String  dataFileName) {
        log.debug("REST request to get AidasAuthority : {}", dataFileName);
       try {
           File file = ResourceUtils.getFile("classpath:"+dataFileName+".csv");
           List<String[]> data = CSVHelper.getData(file);
           if(dataFileName.equals("dummy-org")){
               for(String[] d:data ){
                   Organisation o = new Organisation();
                   o.setId(Long.parseLong(d[0]));
                   o.setName(d[1]);
                   o.setDescription(d[2]);
                   o.setStatus(1);
                   o.setSampleData(1);
                   o = organisationRepository.save(o);
                   organisationSearchRepository.save(o);
               }
           }
           if(dataFileName.equals("dummy-cust")){
               for(String[] d:data ){
                   Customer c = new Customer();
                   c.setId(Long.parseLong(d[0]));
                   c.setName(d[1]);
                   c.setDescription(d[2]);
                   c.setStatus(1);
                   c.setSampleData(1);
                   c.setOrganisation(organisationRepository.getById(Long.parseLong(d[3])));
                   customerRepository.save(c);
               }
           }
           if(dataFileName.equals("dummy-vendor")){
               for(String[] d:data ){
                   Vendor v = new Vendor();
                   v.setId(Long.parseLong(d[0]));
                   v.setName(d[1]);
                   v.setDescription(d[2]);
                   v.setStatus(1);
                   v.setSampleData(1);
                   vendorRepository.save(v);
               }
           }
           if(dataFileName.equals("dummy-user")){
               int i=1;
               for(String[] d:data ){
                   User u = new User();
                   u.setId(Long.parseLong(d[0]));
                   u.setFirstName(d[1]);
                   u.setLastName(d[2]);
                   u.setEmail(d[3]);
                   u.setPassword(d[4]);
                   u.setLogin(d[5]);
                   if(d[6]!=null && d[6].trim().length()>0)
                       u.setOrganisation(organisationRepository.getById(Long.parseLong(d[6])));
                   if(d[7]!=null && d[7].trim().length()>0)
                       u.setCustomer(customerRepository.getById(Long.parseLong(d[7])));
                   if(d[8]!=null && d[8].trim().length()>0)
                       u.setVendor(vendorRepository.getById(Long.parseLong(d[8])));
                   if(d[9]!=null && d[9].trim().length()>0)
                       u.setAuthority(authorityRepository.getById(Long.parseLong(d[9])));
                   u.setDeleted(0);
                   u.setStatus(1);
                   u.setLocked(0);
                   u.setSampleData(1);
                   u.setCreatedBy(SecurityUtils.getCurrentUserLogin().get());
                   u.setCreatedDate(Instant.now());
                   u.setLastModifiedBy(SecurityUtils.getCurrentUserLogin().get());
                   u.setLastModifiedDate(Instant.now());
                   System.out.println(i+":"+u.getEmail());
                   u.setAltEmail("shivain22@gmail.com");
                   u.setCcEmails("admin@ainnotate.com");
                   addUserToKeyCloak(u);
                   userRepository.save(u);
                   updateUserToKeyCloak(u);
                   i++;
                   System.out.println(i+":"+u.getEmail()+":completed");
               }
           }
           if(dataFileName.equals("dummy-uom")){
               for(String[] d:data ){
                   UserOrganisationMapping uom = new UserOrganisationMapping();
                   uom.setId(Long.parseLong(d[0]));
                   uom.setUser(userRepository.getById(Long.parseLong(d[1])));
                   uom.setOrganisation(organisationRepository.getById(Long.parseLong(d[2])));
                   uom.setStatus(1);
                   uom.setSampleData(1);
                   userOrganisationMappingRepository.save(uom);
               }
           }
           if(dataFileName.equals("dummy-ucm")){
               for(String[] d:data ){
                   UserCustomerMapping ucm = new UserCustomerMapping();
                   ucm.setId(Long.parseLong(d[0]));
                   ucm.setUser(userRepository.getById(Long.parseLong(d[1])));
                   ucm.setCustomer(customerRepository.getById(Long.parseLong(d[2])));
                   ucm.setStatus(1);
                   ucm.setSampleData(1);
                   userCustomerMappingRepository.save(ucm);
               }
           }
           if(dataFileName.equals("dummy-uvm")){
               for(String[] d:data ){
                   UserVendorMapping uvm = new UserVendorMapping();
                   uvm.setId(Long.parseLong(d[0]));
                   uvm.setUser(userRepository.getById(Long.parseLong(d[1])));
                   uvm.setVendor(vendorRepository.getById(Long.parseLong(d[2])));
                   uvm.setStatus(1);
                   uvm.setSampleData(1);
                   userVendorMappingRepository.save(uvm);
               }
           }
           if(dataFileName.equals("dummy-uam")){
               for(String[] d:data ){
                   UserAuthorityMapping uam = new UserAuthorityMapping();
                   uam.setId(Long.parseLong(d[0]));
                   uam.setUser(userRepository.getById(Long.parseLong(d[1])));
                   uam.setAuthority(authorityRepository.getById(Long.parseLong(d[2])));
                   uam.setStatus(1);
                   uam.setSampleData(1);
                   userAuthorityMappingRepository.save(uam);
               }
           }
           if(dataFileName.equals("dummy-project")){
               for(String[] d:data ){
                   Project  p = new Project();
                   p.setId(Long.parseLong(d[0]));
                   p.setName(d[1]);
                   p.setDescription(d[2]);
                   p.setStatus(1);
                   p.setAutoCreateObjects(0);
                   p.setBufferPercent(20);
                   p.setExternalDatasetStatus(0);
                   p.setNumOfObjects(0);
                   p.setNumOfUploadsReqd(0);
                   p.setObjectPrefix("");
                   p.setObjectSuffix("");
                   p.setProjectType("image");
                   p.setQcLevels(1);
                   p.setReworkStatus(1);
                   p.setCustomer(customerRepository.getById(Long.parseLong(d[14])));
                   List<Property> props = propertyRepository.findAllDefaultProps();
                   props.forEach(item->{
                       ProjectProperty pp = new ProjectProperty();
                       pp.setProject(p);
                       pp.setProperty(item);
                       pp.setValue(item.getValue());
                       pp.setStatus(1);
                       p.getProjectProperties().add(pp);
                   });
                   p.setSampleData(1);
                   projectRepository.save(p);
               }
           }
           if(dataFileName.equals("dummy-object")){
               for(String[] d:data ){
                   com.ainnotate.aidas.domain.Object o = new Object();
                   o.setId(Long.parseLong(d[0]));
                   o.setName(d[1]);
                   o.setDescription(d[2]);
                   o.setStatus(1);
                   o.setBufferPercent(20);
                   o.setDummy(0);
                   o.setNumberOfUploadReqd(100);
                   List<Property> props = propertyRepository.findAllDefaultProps();
                   props.forEach(item->{
                       ObjectProperty op = new ObjectProperty();
                       op.setObject(o);
                       op.setProperty(item);
                       op.setValue(item.getValue());
                       op.setStatus(1);
                       o.getObjectProperties().add(op);
                   });
                   o.setSampleData(1);
                   o.setProject(projectRepository.getById(Long.parseLong(d[7])));
                   objectRepository.save(o);
               }
           }
           if(dataFileName.equals("dummy-uvmom")){
               for(String[] d:data ){
                   UserVendorMappingObjectMapping uvmom = new UserVendorMappingObjectMapping();
                   uvmom.setId(Long.parseLong(d[0]));
                   uvmom.setUserVendorMapping(userVendorMappingRepository.getById(Long.parseLong(d[1])));
                   uvmom.setObject(objectRepository.getById(Long.parseLong(d[2])));
                   uvmom.setStatus(1);
                   uvmom.setSampleData(1);
                   userVendorMappingObjectMappingRepository.save(uvmom);
               }
           }
           if(dataFileName.equals("dummy-upload")){
               for(String[] d:data ){
                   Upload upload = new Upload();
                   upload.setId(Long.parseLong(d[0]));
                   upload.setName(d[1]);
                   upload.setStatus(1);
                   upload.setApprovalStatus(2);
                   upload.setQcStatus(null);
                   upload.setUserVendorMappingObjectMapping(userVendorMappingObjectMappingRepository.getById(Long.parseLong(d[2])));
                   upload.setSampleData(1);
                   uploadRepository.save(upload);
               }
           }
           if(dataFileName.equals("cleanup-keycloak")){
               List<User> users = userRepository.findAll();
               for(User u:users){
                   if(u.getKeycloakId()!=null){
                       if(u.getSampleData().equals(1)){
                           deleteUserFromKeyCloak(u);
                       }
                   }
               }
           }
           if(dataFileName.equals("cleanup-keycloak-only")){
               cleanUpKeycloakOnly();
           }
       } catch (IOException e) {
           e.printStackTrace();
       } catch (CsvException e) {
           e.printStackTrace();
       } catch (URISyntaxException e) {
           e.printStackTrace();
       }
        return ResponseEntity.ok().body("Success");
    }


    public void addUserToKeyCloak(User myUser) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(myUser.getLogin());
        user.setFirstName(myUser.getFirstName());
        user.setLastName(myUser.getLastName());
        user.setEmail(myUser.getEmail());
        user.setEnabled(true);
        user.setEmailVerified(true);
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
        UserResource userResource = usersRessource.get(userId);
        userResource.resetPassword(passwordCred);
        List<RoleRepresentation> roleRepresentationList = realmResource.roles().list();
        for (RoleRepresentation roleRepresentation : roleRepresentationList)
        {
            for(Authority aa:myUser.getAuthorities()){
                if (roleRepresentation.getName().equals(aa.getName()))
                {
                    userResource.roles().realmLevel().add(Arrays.asList(roleRepresentation));
                    myUser.setAuthority(aa);
                }
            }
        }
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
        List<UserRepresentation> u = usersRessource.list();
        System.out.println(u.size());
        for(UserRepresentation ur:u){
            ur.getId();
        }
        usersRessource.delete(user.getKeycloakId());
    }

    private void cleanUpKeycloakOnly(){
        RealmResource realmResource = keycloak.realm(keycloakConfig.getClientRealm());
        UsersResource usersRessource = realmResource.users();
        List<UserRepresentation> u = usersRessource.list(0,3000);
        for(UserRepresentation ur:u){
            if(!ur.getEmail().equals("admin@ainnotate.com")){
                usersRessource.delete(ur.getId());
            }
        }
    }

}
