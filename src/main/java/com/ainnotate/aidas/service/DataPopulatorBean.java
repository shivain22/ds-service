package com.ainnotate.aidas.service;


import com.ainnotate.aidas.config.KeycloakConfig;
import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.repository.*;
import com.ainnotate.aidas.repository.search.*;
import com.ainnotate.aidas.security.SecurityUtils;
import com.opencsv.exceptions.CsvException;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.weaver.ast.Or;
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
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DataPopulatorBean implements Runnable {

    private TaskDefinition taskDefinition;

    private final Logger log = LoggerFactory.getLogger(DataPopulatorBean.class);

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
    private UploadMetaDataRepository uploadMetaDataRepository;


    @Autowired
    ResourceLoader resourceLoader;

    @SneakyThrows
    @Override
    public void run() {
        System.out.println("Running action: " + taskDefinition.getActionType());
            String dataFileName = taskDefinition.getActionType();
            log.debug("REST request to get AidasAuthority : {}", dataFileName);
            if(dataFileName.equals("load-all-sample-data")){
                try {
                    loadAllSampleData();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (CsvException e) {
                    e.printStackTrace();
                }
            }else if(dataFileName.equals("delete-all-sample-data")){
                try {
                    deleteAllSampleData();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (CsvException e) {
                    e.printStackTrace();
                }
            }
    }

    public TaskDefinition getTaskDefinition() {
        return taskDefinition;
    }

    public void setTaskDefinition(TaskDefinition taskDefinition) {
        this.taskDefinition = taskDefinition;
    }

    private void loadAllSampleData() throws IOException, URISyntaxException, CsvException {
        deleteAllSampleData();
        initialize();
        List<String> dummyOptions = new LinkedList<>();
        dummyOptions.add("dummy-org");
        dummyOptions.add("dummy-cust");
        dummyOptions.add("dummy-vendor");
        dummyOptions.add("dummy-user");
        dummyOptions.add("dummy-uam");
        dummyOptions.add("dummy-uom");
        dummyOptions.add("dummy-ucm");
        dummyOptions.add("dummy-uvm");
        dummyOptions.add("dummy-project");
        dummyOptions.add("dummy-object");
        dummyOptions.add("dummy-uvmom");
        dummyOptions.add("dummy-upload");
        System.out.println("Starting to load dummy data");
        Long totalTime = 0l;
        for(String dataFileName:dummyOptions){
            File file = ResourceUtils.getFile("classpath:"+dataFileName+".csv");
            if(file.exists()) {
                List<String[]> data = CSVHelper.getData(file);
                StopWatch watch = new StopWatch();
                if(dataFileName.equals("dummy-org")){
                    System.out.println("org started");
                    watch.start();
                    addSampleOrganisations(data);
                    watch.stop();
                    totalTime+=watch.getTime();
                    System.out.println("org-done in "+watch.getTime(TimeUnit.MINUTES));
                }
                if(dataFileName.equals("dummy-cust")){
                    System.out.println("cust started");
                    watch.start();
                    addSampleCustomers(data);
                    watch.stop();
                    totalTime+=watch.getTime();
                    System.out.println("cust done in "+watch.getTime(TimeUnit.MINUTES));
                }
                if(dataFileName.equals("dummy-vendor")){
                    System.out.println("vendor started");
                    watch.start();
                    addSampleVendors(data);
                    watch.stop();
                    totalTime+=watch.getTime();
                    System.out.println("vendor-done in "+watch.getTime(TimeUnit.MINUTES));
                }
                if(dataFileName.equals("dummy-user")){
                    System.out.println("user started");
                    watch.start();
                    addSampleUsers(data);
                    watch.stop();
                    totalTime+=watch.getTime();
                    System.out.println("user-done in "+watch.getTime(TimeUnit.MINUTES));
                }
                if(dataFileName.equals("dummy-uom")){
                    System.out.println("uom started");
                    watch.start();
                    addSampleUserOrganisationMappings(data);
                    watch.stop();
                    totalTime+=watch.getTime();
                    System.out.println("uom-done in "+watch.getTime(TimeUnit.MINUTES));
                }
                if(dataFileName.equals("dummy-uam")){
                    System.out.println("uam started");
                    watch.start();
                    addSampleUserAuthorityMappings(data);
                    watch.stop();
                    totalTime+=watch.getTime();
                    System.out.println("uam-done in "+watch.getTime(TimeUnit.MINUTES));
                }
                if(dataFileName.equals("dummy-project")){
                    System.out.println("project started");
                    watch.start();
                    addSampleProjects(data);
                    System.out.println("Project properties started");
                    addSampleProjectProperties();
                    System.out.println("Project properteis done");
                    watch.stop();
                    totalTime+=watch.getTime();
                    System.out.println("projects-done in "+watch.getTime(TimeUnit.MINUTES));
                }
                if(dataFileName.equals("dummy-object")){
                    System.out.println("object started");
                    watch.start();
                    addSampleObjects(data);
                    System.out.println("Object properties started");
                    addSampleObjectProperties();
                    System.out.println("Object properteis done");
                    watch.stop();
                    totalTime+=watch.getTime();
                    System.out.println("objects-done in "+watch.getTime(TimeUnit.MINUTES));
                }
                if(dataFileName.equals("dummy-uvmom")){
                    System.out.println("uvmom started");
                    watch.start();
                    addSampleUserVendorMappingObjectMappings(data);
                    watch.stop();
                    totalTime+=watch.getTime();
                    System.out.println("uvmom-done in "+watch.getTime(TimeUnit.MINUTES));
                }
                if(dataFileName.equals("dummy-upload")){
                    System.out.println("upload started");
                    watch.start();
                    addSampleUploads(data);
                    watch.stop();
                    totalTime+=watch.getTime();
                    System.out.println("upload-done in "+watch.getTime(TimeUnit.MINUTES));
                }
            }
        }
        System.out.println((totalTime/1000)/60 +" minutes");
        System.out.println("Completed loading dummy data");
    }



    private void deleteAllSampleData() throws IOException, URISyntaxException, CsvException {
        System.out.println("Cleaning up all sample data");
        deleteAllSampleMetaDataUploads();
        deleteAllSampleUploads();
        deleteAllSampleUserVendorMappingObjectMapping();
        deleteAllSampleUserVendorMapping();
        deleteAllSampleObjectProperties();
        deleteAllSampleObjects();
        deleteAllSampleProjectProperties();
        deleteAllSampleProjects();
        deleteAllSampleUserAuthorityMappings();
        deleteAllSampleUserCustomerMappings();
        deleteAllSampleUserOrganisationMappings();
        deleteAllSampleUserVendorMappings();
        deleteAllSampleUsers();
        deleteAllSampleCustomers();
        deleteAllSampleVendors();
        deleteAllSampleOrganisations();
        System.out.println("Cleaned up all sample data");
    }

    private void deleteAllSampleMetaDataUploads(){
        uploadMetaDataRepository.deleteAllSampleUploadMetadata();
    }
    private void deleteAllSampleUploads(){
        uploadRepository.deleteAllSampleUploads();
    }
    private void deleteAllSampleUserVendorMappingObjectMapping(){
        userVendorMappingObjectMappingRepository.deleteAllSampleUserVendorMappingObjectMappings();
    }
    private void deleteAllSampleUserVendorMapping(){
        userVendorMappingRepository.deleteAllSampleUserVendorMappings();
    }
    private void deleteAllSampleObjects(){
        objectRepository.deleteAllSampleObjects();
    }
    private void deleteAllSampleProjects(){
        projectRepository.deleteAllSampleProjects();
    }
    private void deleteAllSampleUserAuthorityMappings(){
        userAuthorityMappingRepository.deleteAllSampleUserAuthorityMappings();
    }
    private void deleteAllSampleUserOrganisationMappings(){
        userOrganisationMappingRepository.deleteAllSampleUserOrganisationMappings();
    }
    private void deleteAllSampleUserCustomerMappings(){
        userCustomerMappingRepository.deleteAllSampleUserCustomerMappings();
    }
    private void deleteAllSampleUserVendorMappings(){
        userVendorMappingRepository.deleteAllSampleUserVendorMappings();
    }
    private void deleteAllSampleUsers(){
        userRepository.deleteAllSampleUsers();
        cleanUpKeyCloakAndDBUsers();
        cleanUpKeycloakOnly();
    }
    private void deleteAllSampleCustomers(){
        customerRepository.deleteAllSampleCustomers();
    }
    private void deleteAllSampleOrganisations(){
        organisationRepository.deleteAllSampleOrganisations();
    }
    private void deleteAllSampleProjectProperties(){
        projectPropertyRepository.deleteAllSampleProjectProperty();
    }
    private void deleteAllSampleVendors(){
        vendorRepository.deleteAllSampleVendors();
    }
    private void deleteAllSampleObjectProperties(){
        objectPropertyRepository.deleteAllSampleObjectProperty();
    }
    List<Organisation> organisations=new LinkedList<>();
    List<Customer> customers = new LinkedList<>();
    List<Vendor> vendors = new ArrayList<>();
    List<User> users = new ArrayList<>();
    List<Project> projects = new ArrayList<>();
    List<Object> objects = new ArrayList<>();
    List<ProjectProperty> projectProperties = new LinkedList<>();
    List<ObjectProperty> objectProperties = new LinkedList<>();
    List<UserAuthorityMapping> userAuthorityMappings = new LinkedList<>();
    List<UserOrganisationMapping> userOrganisationMappings = new LinkedList<>();
    List<UserCustomerMapping> userCustomerMappings = new LinkedList<>();
    List<UserVendorMapping> userVendorMappings = new LinkedList<>();
    List<UserVendorMappingObjectMapping> userVendorMappingObjectMappings = new LinkedList<>();
    List<Property> properties = new LinkedList<>();
    public void initialize(){
         organisations=new LinkedList<>();
         customers = new LinkedList<>();
         vendors = new LinkedList<>();
         users = new LinkedList<>();
         projects = new LinkedList<>();
         objects = new LinkedList<>();
         userAuthorityMappings = new LinkedList<>();
         userOrganisationMappings = new LinkedList<>();
         userCustomerMappings = new LinkedList<>();
         userVendorMappings = new LinkedList<>();
         userVendorMappingObjectMappings = new LinkedList<>();
        properties = new LinkedList<>();
    }
    public void addSampleOrganisations(List<String[]> data){
        for(String[] d:data ){
            Organisation o = new Organisation();
            o.setName(d[0]);
            o.setDescription(d[1]);
            o.setStatus(1);
            o.setSampleData(1);
            organisations.add(o);
        }
        organisationRepository.saveAll(organisations);
        organisationSearchRepository.saveAll(organisations);
        organisations = organisationRepository.getAllSampleOrganisations();
    }
    private void addSampleCustomers(List<String[]>data){
        int i=0;
        int oid=0;
        for(String[] d:data ){
            if(i%10==0){
                if(i>0)
                    oid++;
            }
            Customer c = new Customer();
            c.setName(d[0]);
            c.setDescription(d[1]);
            c.setStatus(1);
            c.setSampleData(1);
            c.setOrganisation(organisations.get(oid));
            customers.add(c);
            i++;
        }
        customerRepository.saveAll(customers);
        customerSearchRepository.saveAll(customers);
        customers = customerRepository.getAllSampleCustomers();
    }
    private void addSampleVendors(List<String[]> data){
        for(String[] d:data ){
            Vendor v = new Vendor();
            v.setName(d[0]);
            v.setDescription(d[1]);
            v.setStatus(1);
            v.setSampleData(1);
            vendors.add(v);
        }
        vendorRepository.saveAll(vendors);
        vendorSearchRepository.saveAll(vendors);
        vendors = vendorRepository.getAllSampleVendors();
    }
    private void addSampleUsers(List<String[]> data){
        System.out.println("start batch insert users");
        int i=0;
        int j=0;
        int k=0;
        int l=0;
        int m=0;
        int n=0;
        for(String[] d:data ){
            User u = new User();
            u.setFirstName(d[0]);
            u.setLastName(d[1]);
            u.setEmail(d[2]);
            u.setPassword(d[3]);
            u.setLogin(d[4]);
            if(i<20)
                u.setOrganisation(organisations.get(i));
                u.setAuthority(authorityRepository.findByName(AidasConstants.ORG_ADMIN));
            if(i>20 && i<221)
                u.setCustomer(customers.get(i-21));
                u.setAuthority(authorityRepository.findByName(AidasConstants.CUSTOMER_ADMIN));
            if(i>220 && i<271)
                u.setVendor(vendors.get(i-221));
                u.setAuthority(authorityRepository.findByName(AidasConstants.VENDOR_ADMIN));
            if(i>270 && i<1271){
                if(i%10==0){{
                    n++;
                    if(n==2){
                        if(i>290)
                            j++;
                    }
                    if(n>1){
                        n=0;
                    }
                }
                }
                u.setVendor(vendors.get(j));
                u.setAuthority(authorityRepository.findByName(AidasConstants.VENDOR_USER));
            }
            if(i>1270){
                if(i%5==0){
                    if(i>1271)
                        k++;
                }
                u.setCustomer(customers.get(k));
                u.setAuthority(authorityRepository.findByName(AidasConstants.QC_USER));
            }
            u.setDeleted(0);
            u.setStatus(1);
            u.setLocked(0);
            u.setSampleData(1);
            u.setAltEmail("shivain22@gmail.com");
            u.setCcEmails("admin@ainnotate.com");
            users.add(u);
            i++;
        }
        userRepository.saveAll(users);
        userSearchRepository.saveAll(users);
        users = userRepository.getAllSampleUsers();

        System.out.println("Sync with keycloak");
        users = userRepository.findAll();
        for(User u:users){
            if(u.getId()>0){
                try {
                    addUserToKeyCloak(u);
                    userRepository.save(u);
                }catch(Exception e){
                    System.out.println(u.getEmail()+e.getMessage());
                }
            }
        }
        System.out.println("Sync with keycloak completed");

    }
    private void addSampleProjects(List<String[]> data){
        int i=0;
        int j=0;
        for(String[] d:data ){
            Project  p = new Project();
            p.setName(d[0]);
            p.setDescription(d[1]);
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
            if(i%5==0){
                if(i>0){
                    j++;
                }
            }
            p.setCustomer(customers.get(j));
            p.setSampleData(1);
            projects.add(p);
        }
        projectRepository.saveAll(projects);
        projects = projectRepository.getAllSampleProjects();
        properties = propertyRepository.findAllDefaultProps();
    }
    private void addSampleObjects(List<String[]> data){
        int i=0;
        int j=0;
        for(String[] d:data ){
            com.ainnotate.aidas.domain.Object o = new Object();
            o.setName(d[0]);
            o.setDescription(d[1]);
            o.setStatus(1);
            o.setBufferPercent(20);
            o.setDummy(0);
            o.setNumberOfUploadReqd(100);
            o.setSampleData(1);
            if(i%5==0){
                if(i>0){
                    j++;
                }
            }
            o.setProject(projects.get(j));
            objects.add(o);
        }
        objectRepository.saveAll(objects);
        objects = objectRepository.getAllSampleObjects();
    }

    private void addSampleProjectProperties(){
        for(Project p:projects){
            properties.forEach(item->{
                ProjectProperty pp = new ProjectProperty();
                pp.setProject(p);
                pp.setProperty(item);
                pp.setValue(item.getValue());
                pp.setStatus(1);
                pp.setSampleData(1);
                projectProperties.add(pp);
            });
        }
        projectPropertyRepository.saveAll(projectProperties);
    }
    private void addSampleObjectProperties(){
        for(Object o:objects){
            properties.forEach(item->{
                ObjectProperty op = new ObjectProperty();
                op.setObject(o);
                op.setProperty(item);
                op.setValue(item.getValue());
                op.setStatus(1);
                op.setSampleData(1);
                objectProperties.add(op);
            });
        }
        objectPropertyRepository.saveAll(objectProperties);
    }

    private void addSampleUserAuthorityMappings(List<String[]> data){
        for(User u:users ){
            UserAuthorityMapping uam = new UserAuthorityMapping();
            uam.setUser(u);
            uam.setAuthority(u.getAuthority());
            uam.setStatus(1);
            uam.setSampleData(1);
            userAuthorityMappings.add(uam);
        }
        userAuthorityMappingRepository.saveAll(userAuthorityMappings);
    }
    private void addSampleUserOrganisationMappings(List<String[]> data){
        for(User u:users){
            UserOrganisationMapping uom = new UserOrganisationMapping();
            UserCustomerMapping ucm = new UserCustomerMapping();
            UserVendorMapping uvm = new UserVendorMapping();
            if(u.getOrganisation()!=null) {
                uom.setUser(u);
                uom.setOrganisation(u.getOrganisation());
                uom.setStatus(1);
                uom.setSampleData(1);
                userOrganisationMappings.add(uom);
            }
            if(u.getCustomer()!=null) {
                ucm.setUser(u);
                ucm.setCustomer(u.getCustomer());
                ucm.setStatus(1);
                ucm.setSampleData(1);
                userCustomerMappings.add(ucm);
            }
            if(u.getVendor()!=null) {
                uvm.setUser(u);
                uvm.setVendor(u.getVendor());
                uvm.setStatus(1);
                uvm.setSampleData(1);
                userVendorMappings.add(uvm);
            }

        }
        userOrganisationMappingRepository.saveAll(userOrganisationMappings);
        userCustomerMappingRepository.saveAll(userCustomerMappings);
        userVendorMappingRepository.saveAll(userVendorMappings);
    }

    private void addSampleUserVendorMappingObjectMappings(List<String[]> data){
            List<Object> objects = objectRepository.getAllSampleObjects();
            List<UserVendorMapping> userVendorMappings = userVendorMappingRepository.getAllSampleUserVendorMappings();
            for(Object o: objects){
                for(UserVendorMapping UserVendorMapping: userVendorMappings){
                        UserVendorMappingObjectMapping uvmom = new UserVendorMappingObjectMapping();
                        uvmom.setUserVendorMapping(UserVendorMapping);
                        uvmom.setObject(o);
                        uvmom.setStatus(1);
                        uvmom.setSampleData(1);
                        userVendorMappingObjectMappings.add(uvmom);
                }
                userVendorMappingObjectMappingRepository.saveAll(userVendorMappingObjectMappings);
                userVendorMappingObjectMappings = new LinkedList<>();
            }
            System.out.println("completed storing user vendor mapping object mappings");
            userVendorMappingObjectMappings = userVendorMappingObjectMappingRepository.getAllSampleUserVendorMappingObjectMappings();
    }
    private void addSampleUploads(List<String[]> data){

           /* Upload upload = new Upload();
            upload.setId(Long.parseLong(d[0]));
            upload.setName(d[1]);
            upload.setStatus(1);
            upload.setApprovalStatus(2);
            upload.setQcStatus(null);
            upload.setUserVendorMappingObjectMapping(userVendorMappingObjectMappingRepository.getById(Long.parseLong(d[2])));
            upload.setSampleData(1);
            uploadRepository.save(upload);*/
    }
    private void cleanUpKeyCloakAndDBUsers(){
        System.out.println("Clean up all user data from keycloak");
        List<User> users = userRepository.findAll();
        for(User u:users){
            if(u.getKeycloakId()!=null){
                if(u.getSampleData()!=null && u.getSampleData().equals(1)){
                    deleteUserFromKeyCloak(u);
                }
            }
        }
        System.out.println("Cleaned up all user data from keycloak");
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

        Map<String,List<String>> userAttrs = new HashMap<>();
        List<String> userAttrsVals = new ArrayList<>();
        userAttrsVals.add(String.valueOf(myUser.getId()));
        userAttrs.put("aidas_id",userAttrsVals);
        userAttrsVals = new ArrayList<>();
        userAttrsVals.add(String.valueOf(myUser.getAuthority().getName()));
        userAttrs.put("current_role",userAttrsVals);
        userAttrsVals = new ArrayList<>();
        userAttrsVals.add("1");
        userAttrs.put("sample_user",userAttrsVals);
        user.setAttributes(userAttrs);

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
        List<String> defaultAdmins = new ArrayList<>();
        defaultAdmins.add("admin@ainnotate.com");
        defaultAdmins.add("admin@localhost.com");
        defaultAdmins.add("admin@localhost");
        for(UserRepresentation ur:u){
            if(!defaultAdmins.contains(ur.getEmail())){
                usersRessource.delete(ur.getId());
            }
        }
    }
}
