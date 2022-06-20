package com.ainnotate.aidas.service;


import com.ainnotate.aidas.config.KeycloakConfig;
import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.repository.*;
import com.ainnotate.aidas.repository.search.*;
import com.ainnotate.aidas.security.SecurityUtils;
import com.opencsv.exceptions.CsvException;
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

    @Override
    public void run() {
        System.out.println("Running action: " + taskDefinition.getActionType());
            String dataFileName = taskDefinition.getActionType();
            log.debug("REST request to get AidasAuthority : {}", dataFileName);
            if(dataFileName.equals("load-all")){
                try {
                    loadAllDummys();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (CsvException e) {
                    e.printStackTrace();
                }
            }else if(dataFileName.equals("cleanup-keycloak")){
                cleanUpKeyCloakAndDBUsers();
            }
            else if(dataFileName.equals("cleanup-keycloak-only")){
                cleanUpKeycloakOnly();
            }
    }

    public TaskDefinition getTaskDefinition() {
        return taskDefinition;
    }

    public void setTaskDefinition(TaskDefinition taskDefinition) {
        this.taskDefinition = taskDefinition;
    }

    private void loadAllDummys() throws IOException, URISyntaxException, CsvException {
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
                    addDummyOrganisations(data);
                    watch.stop();
                    totalTime+=watch.getTime();
                    System.out.println("org-done in "+watch.getTime(TimeUnit.MINUTES));
                }
                if(dataFileName.equals("dummy-cust")){
                    System.out.println("cust started");
                    watch.start();
                    addDummyCustomers(data);
                    watch.stop();
                    totalTime+=watch.getTime();
                    System.out.println("cust done in "+watch.getTime(TimeUnit.MINUTES));
                }
                if(dataFileName.equals("dummy-vendor")){
                    System.out.println("vendor started");
                    watch.start();
                    addDummyVendors(data);
                    watch.stop();
                    totalTime+=watch.getTime();
                    System.out.println("vendor-done in "+watch.getTime(TimeUnit.MINUTES));
                }
                if(dataFileName.equals("dummy-user")){
                    System.out.println("user started");
                    watch.start();
                    addDummyUsers(data);
                    watch.stop();
                    totalTime+=watch.getTime();
                    System.out.println("user-done in "+watch.getTime(TimeUnit.MINUTES));
                }
                if(dataFileName.equals("dummy-uom")){
                    System.out.println("uom started");
                    watch.start();
                    addDummyUserOrganisationMappings(data);
                    watch.stop();
                    totalTime+=watch.getTime();
                    System.out.println("uom-done in "+watch.getTime(TimeUnit.MINUTES));
                }
                if(dataFileName.equals("dummy-ucm")){
                    System.out.println("ucm started");
                    watch.start();
                    addDummyUserCustomerMappings(data);
                    watch.stop();
                    totalTime+=watch.getTime();
                    System.out.println("ucm-done in "+watch.getTime(TimeUnit.MINUTES));
                }
                if(dataFileName.equals("dummy-uvm")){
                    System.out.println("uvm started");
                    watch.start();
                    addDummyUserVendorMappings(data);
                    watch.stop();
                    totalTime+=watch.getTime();
                    System.out.println("uvm-done in "+watch.getTime(TimeUnit.MINUTES));
                }
                if(dataFileName.equals("dummy-uam")){
                    System.out.println("uam started");
                    watch.start();
                    addDummyUserAuthorityMappings(data);
                    watch.stop();
                    totalTime+=watch.getTime();
                    System.out.println("uam-done in "+watch.getTime(TimeUnit.MINUTES));
                }
                if(dataFileName.equals("dummy-project")){
                    System.out.println("project started");
                    watch.start();
                    addDummyProjects(data);
                    watch.stop();
                    totalTime+=watch.getTime();
                    System.out.println("projects-done in "+watch.getTime(TimeUnit.MINUTES));
                }
                if(dataFileName.equals("dummy-object")){
                    System.out.println("object started");
                    watch.start();
                    addDummyObjects(data);
                    watch.stop();
                    totalTime+=watch.getTime();
                    System.out.println("objects-done in "+watch.getTime(TimeUnit.MINUTES));
                }
                if(dataFileName.equals("dummy-uvmom")){
                    System.out.println("uvmom started");
                    watch.start();
                    addDummyUserVendorMappingObjectMappings(data);
                    watch.stop();
                    totalTime+=watch.getTime();
                    System.out.println("uvmom-done in "+watch.getTime(TimeUnit.MINUTES));
                }
                if(dataFileName.equals("dummy-upload")){
                    System.out.println("upload started");
                    watch.start();
                    addDummyUploads(data);
                    watch.stop();
                    totalTime+=watch.getTime();
                    System.out.println("upload-done in "+watch.getTime(TimeUnit.MINUTES));
                }
            }
        }
        System.out.println((totalTime/1000)/60 +" minutes");
        System.out.println("Completed loading dummy data");
    }



    private void deleteAllSamples() throws IOException, URISyntaxException, CsvException {
        deleteAllDummyMetaDataUploads();
        deleteAllDummyUploads();
        deleteAllDummyUserVendorMappingObjectMapping();
        deleteAllDummyUserVendorMapping();
        deleteAllDummyObjects();
        deleteAllDummyProjects();
        deleteAllDummyUserAuthorityMappings();
        deleteAllDummyUserOrganisationMappings();
        deleteAllDummyUserCustomerMappings();
        deleteAllDummyUserVendorMappings();
        deleteAllDummyUsers();
        deleteAllDummyCustomers();
        deleteAllDummyOrganisations();
    }

    private void deleteAllDummyMetaDataUploads(){
        uploadMetaDataRepository.deleteAllSampleUploadMetadata();
    }
    private void deleteAllDummyUploads(){
        uploadRepository.deleteAllSampleUploads();
    }
    private void deleteAllDummyUserVendorMappingObjectMapping(){
        userVendorMappingObjectMappingRepository.deleteAllSampleUserVendorMappingObjectMappings();
    }
    private void deleteAllDummyUserVendorMapping(){
        userVendorMappingRepository.deleteAllSampleUserVendorMappings();
    }
    private void deleteAllDummyObjects(){
        objectRepository.deleteAllSampleObjects();
    }
    private void deleteAllDummyProjects(){
        projectRepository.deleteAllSampleProjects();
    }
    private void deleteAllDummyUserAuthorityMappings(){

    }
    private void deleteAllDummyUserOrganisationMappings(){
        userOrganisationMappingRepository.deleteAllSampleUserOrganisationMappings();
    }
    private void deleteAllDummyUserCustomerMappings(){
        userCustomerMappingRepository.deleteAllSampleUserCustomerMappings();
    }
    private void deleteAllDummyUserVendorMappings(){
        userVendorMappingRepository.deleteAllSampleUserVendorMappings();
    }
    private void deleteAllDummyUsers(){
        userRepository.deleteAllSampleUsers();
        cleanUpKeyCloakAndDBUsers();
        cleanUpKeycloakOnly();
    }
    private void deleteAllDummyCustomers(){
        customerRepository.deleteAllSampleCustomers();
    }
    private void deleteAllDummyOrganisations(){
        organisationRepository.deleteAllSampleOrganisations();
    }

    /*public void addOrgTest(String fileName) throws IOException, URISyntaxException, CsvException {
        var wrapper = new Object(){ String value = ""; };
        File file = ResourceUtils.getFile("classpath:"+fileName);
        List<String[]> data = CSVHelper.getData(file);
        *//*try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()))) {
            stream.forEach(s->wrapper.value += s);
        } catch (IOException e) {
            e.printStackTrace();
        }*//*
        List<Organisation> organisations= new ArrayList<>();
        for(String[] d:data ){
            Organisation o = new Organisation();
            o.setId(Long.parseLong(d[0]));
            o.setName(d[1]);
            o.setDescription(d[2]);
            o.setStatus(1);
            o.setSampleData(1);
            organisations.add(o);
        }
        organisationRepository.saveAll(organisations);
    }*/
    public void addDummyOrganisations(List<String[]> data){
        List<Organisation> organisations= new ArrayList<>();
        for(String[] d:data ){
            Organisation o = new Organisation();
            o.setId(Long.parseLong(d[0]));
            o.setName(d[1]);
            o.setDescription(d[2]);
            o.setStatus(1);
            o.setSampleData(1);
            organisations.add(o);
        }
        organisationRepository.saveAll(organisations);
        organisationSearchRepository.saveAll(organisations);
    }
    private void addDummyCustomers(List<String[]>data){
        List<Customer>customers = new ArrayList<>();
        List<Organisation> organisations = organisationRepository.getAllSampleOrganisations();
        Map<Long,Organisation> orgMap = new HashMap<>();
        for(Organisation o:organisations){
            orgMap.put(o.getId(),o);
        }
        for(String[] d:data ){
            Customer c = new Customer();
            c.setId(Long.parseLong(d[0]));
            c.setName(d[1]);
            c.setDescription(d[2]);
            c.setStatus(1);
            c.setSampleData(1);
            c.setOrganisation(orgMap.get(Long.parseLong(d[3])));
            customers.add(c);
            //customerRepository.save(c);
        }
        customerRepository.saveAll(customers);
        customerSearchRepository.saveAll(customers);
    }
    private void addDummyVendors(List<String[]> data){
        List<Vendor> vendors = new ArrayList<>();
        for(String[] d:data ){
            Vendor v = new Vendor();
            v.setId(Long.parseLong(d[0]));
            v.setName(d[1]);
            v.setDescription(d[2]);
            v.setStatus(1);
            v.setSampleData(1);
            vendors.add(v);
            //vendorRepository.save(v);
        }
        vendorRepository.saveAll(vendors);
        vendorSearchRepository.saveAll(vendors);
    }
    private void addDummyUsers(List<String[]> data){
        List<User> users = new ArrayList<>();
        StopWatch watch = new StopWatch();
        System.out.println("start batch insert users");
        watch.start();
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
            u.setAltEmail("shivain22@gmail.com");
            u.setCcEmails("admin@ainnotate.com");
            //addUserToKeyCloak(u);
            //u=userRepository.save(u);
            //updateUserToKeyCloak(u);
            users.add(u);
        }
        userRepository.saveAll(users);
        userSearchRepository.saveAll(users);
        watch.stop();
        watch = new StopWatch();
        System.out.println("Insert to db done in "+watch.getTime(TimeUnit.MINUTES));
        watch.start();
        System.out.println("Sync with keycloak");
        users = userRepository.findAll();
        for(User u:users){
            if(u.getId()>0){
                addUserToKeyCloak(u);
                userRepository.save(u);
            }
        }
        System.out.println("Sync with keycloak completed in "+watch.getTime(TimeUnit.MINUTES));
        watch.stop();
    }
    private void addDummyProjects(List<String[]> data){
        List<Project> projects = new ArrayList<>();
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
            projects.add(p);
            //projectRepository.save(p);
        }
        projectRepository.saveAll(projects);
        projectSearchRepository.saveAll(projects);
    }
    private void addDummyObjects(List<String[]> data){
        List<Object> objects = new ArrayList<>();
        for(String[] d:data ){
            com.ainnotate.aidas.domain.Object o = new Object();
            System.out.println(d[0]);
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
            objects.add(o);
            //objectRepository.save(o);
        }
        objectRepository.saveAll(objects);
        objectSearchRepository.saveAll(objects);
    }
   /* private void addDummyProperties(){
        for(int i=1;i<201;i++){
            propertyRepository.addNewProperty(Long.valueOf(i));
        }
    }*/
    private void addDummyUserAuthorityMappings(List<String[]> data){
        List<UserAuthorityMapping> userAuthorityMappings = new ArrayList<>();
        for(String[] d:data ){
            UserAuthorityMapping uam = new UserAuthorityMapping();
            uam.setId(Long.parseLong(d[0]));
            uam.setUser(userRepository.getById(Long.parseLong(d[1])));
            uam.setAuthority(authorityRepository.getById(Long.parseLong(d[2])));
            uam.setStatus(1);
            uam.setSampleData(1);
            userAuthorityMappings.add(uam);
            //userAuthorityMappingRepository.save(uam);
        }
        userAuthorityMappingRepository.saveAll(userAuthorityMappings);
    }
    private void addDummyUserOrganisationMappings(List<String[]> data){
        List<UserOrganisationMapping> userOrganisationMappings = new ArrayList<>();
        for(String[] d:data ){
            UserOrganisationMapping uom = new UserOrganisationMapping();
            uom.setId(Long.parseLong(d[0]));
            uom.setUser(userRepository.getById(Long.parseLong(d[1])));
            uom.setOrganisation(organisationRepository.getById(Long.parseLong(d[2])));
            uom.setStatus(1);
            uom.setSampleData(1);
            userOrganisationMappings.add(uom);
            //userOrganisationMappingRepository.save(uom);
        }
        userOrganisationMappingRepository.saveAll(userOrganisationMappings);
    }
    private void addDummyUserCustomerMappings(List<String[]> data){
        List<UserCustomerMapping>userCustomerMappings =new ArrayList<>();
        for(String[] d:data ){
            UserCustomerMapping ucm = new UserCustomerMapping();
            ucm.setId(Long.parseLong(d[0]));
            ucm.setUser(userRepository.getById(Long.parseLong(d[1])));
            ucm.setCustomer(customerRepository.getById(Long.parseLong(d[2])));
            ucm.setStatus(1);
            ucm.setSampleData(1);
            userCustomerMappings.add(ucm);
            //userCustomerMappingRepository.save(ucm);
        }
        userCustomerMappingRepository.saveAll(userCustomerMappings);
    }
    private void addDummyUserVendorMappings(List<String[]> data){
        List<UserVendorMapping> userVendorMappings = new ArrayList<>();
        for(String[] d:data ){
            UserVendorMapping uvm = new UserVendorMapping();
            uvm.setId(Long.parseLong(d[0]));
            uvm.setUser(userRepository.getById(Long.parseLong(d[1])));
            uvm.setVendor(vendorRepository.getById(Long.parseLong(d[2])));
            uvm.setStatus(1);
            uvm.setSampleData(1);
            userVendorMappings.add(uvm);
            //userVendorMappingRepository.save(uvm);
        }
        userVendorMappingRepository.saveAll(userVendorMappings);
    }
    private void addDummyUserVendorMappingObjectMappings(List<String[]> data){
        for(String[] d:data ){
            List<Object> objects = objectRepository.findAll();
            List<Vendor> vendors = vendorRepository.findAll();
            for(Object o: objects){
                for(Vendor vendor:vendors){
                    List<UserVendorMapping> uvms = userVendorMappingRepository.findAllVendorUserMappings(vendor.getId());
                    for(UserVendorMapping uvm:uvms){
                        UserVendorMappingObjectMapping uvmom = new UserVendorMappingObjectMapping();
                        uvmom.setUserVendorMapping(uvm);
                        uvmom.setObject(o);
                        uvmom.setStatus(1);
                        uvmom.setSampleData(1);
                        userVendorMappingObjectMappingRepository.save(uvmom);
                    }
                }
            }
            UserVendorMappingObjectMapping uvmom = new UserVendorMappingObjectMapping();
            uvmom.setId(Long.parseLong(d[0]));
            uvmom.setUserVendorMapping(userVendorMappingRepository.getById(Long.parseLong(d[1])));
            uvmom.setObject(objectRepository.getById(Long.parseLong(d[2])));
            uvmom.setStatus(1);
            uvmom.setSampleData(1);
            userVendorMappingObjectMappingRepository.save(uvmom);
        }
    }
    private void addDummyUploads(List<String[]> data){
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
    private void cleanUpKeyCloakAndDBUsers(){
        List<User> users = userRepository.findAll();
        for(User u:users){
            if(u.getKeycloakId()!=null){
                if(u.getSampleData().equals(1)){
                    deleteUserFromKeyCloak(u);
                }
            }
        }
    }
    private void cleanUpDummyData(){

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
        for(UserRepresentation ur:u){
            if(!ur.getEmail().equals("admin@ainnotate.com")){
                usersRessource.delete(ur.getId());
            }
        }
    }
}
