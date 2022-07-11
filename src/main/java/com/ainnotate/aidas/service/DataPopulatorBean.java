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
import java.sql.*;
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
    private CategoryRepository categoryRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;
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
    OrganisationQcProjectMappingRepository organisationQcProjectMappingRepository;

    @Autowired
    CustomerQcProjectMappingRepository customerQcProjectMappingRepository;

    @Autowired
    private UserCustomerMappingRepository userCustomerMappingRepository;
    @Autowired
    private UserVendorMappingRepository userVendorMappingRepository;
    @Autowired
    private UserAuthorityMappingRepository userAuthorityMappingRepository;

    @Autowired
    private UserVendorMappingObjectMappingRepository userVendorMappingObjectMappingRepository;

    @Autowired
    private UserVendorMappingProjectMappingRepository userVendorMappingProjectMappingRepository;
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
        System.out.println("Cleaning up");
        deleteAllSampleData();
        System.out.println("Done cleaning up");

        System.out.println("Inserting sample data");
        initialize();

        File file = ResourceUtils.getFile("classpath:dummy-org.csv");
        List<String[]> data = CSVHelper.getData(file);
        addSampleOrganisations(data);

        file = ResourceUtils.getFile("classpath:dummy-cust.csv");
        data = CSVHelper.getData(file);
        addSampleCustomers(data);

        file = ResourceUtils.getFile("classpath:dummy-vendor.csv");
        data = CSVHelper.getData(file);
        addSampleVendors(data);

        file = ResourceUtils.getFile("classpath:dummy-user.csv");
        data = CSVHelper.getData(file);
        addSampleUsers(data);

        file = ResourceUtils.getFile("classpath:dummy-project.csv");
        data = CSVHelper.getData(file);
        addSampleProjects(data);
        addSampleProjectProperties();

        addSampleObjects();
        addSampleObjectProperties();
        addSampleUAMUOMUCMUVM();
        addSampleUserVendorMappingObjectMappings();
        addSampleOrgnisationQcMappings();
        addSampleCustomerQcMappings();

        addSampleUploads();

        System.out.println("compleded sample data inserts");

    }



    private void deleteAllSampleData() throws IOException, URISyntaxException, CsvException {
        deleteAllSampleMetaDataUploads();
        deleteAllSampleUploads();
        deleteAllSampleUserVendorMappingObjectMapping();
        deleteAllSampleUserVendorMapping();
        deleteAllSampleCustomerProperties();
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
    private void deleteAllSampleCustomerProperties(){
        propertyRepository.deleteAllSampleProperties();
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
        String query="insert into organisation(name,description,status,is_sample_data) values ";
        String values = "";
        for(String[] d:data ){
            values+="('"+d[0]+"','"+d[1]+"',1,1),";
        }
        batchInsert(query+values.substring(0,values.length()-1));
        organisations = organisationRepository.getAllSampleOrganisations();
    }
    private void addSampleCustomers(List<String[]>data){
        String query="insert into customer(name,description,organisation_id,status,is_sample_data) values ";
        String values = "";
        for(String[] d:data ){
            Organisation o = organisationRepository.findSampleOrganisationByName(d[2]);
            values+="('"+d[0]+"','"+d[1]+"',"+o.getId()+",1,1),";
        }
        batchInsert(query+values.substring(0,values.length()-1));
        customers = customerRepository.getAllSampleCustomers();
        properties = propertyRepository.findAllDefaultProps();
        addSamplePropertiesForCustomer();
    }
    private void addSampleVendors(List<String[]> data){
        String query="insert into vendor(name,description,status,is_sample_data) values ";
        String values = "";
        for(String[] d:data ){
            Vendor v = new Vendor();
            v.setName(d[0]);
            v.setDescription(d[1]);
            v.setStatus(1);
            v.setSampleData(1);
            vendors.add(v);
            values+="('"+d[0]+"','"+d[1]+"',1,1),";
        }
        batchInsert(query+values.substring(0,values.length()-1));
        vendors = vendorRepository.getAllSampleVendors();
    }
    private void addSampleUsers(List<String[]> data){
        String query="insert into user(first_name,last_name,email,login,password,organisation_id,customer_id,vendor_id,authority_id,status,is_sample_data,deleted,locked,alt_email,cc_email) values \n";
        String values = "";
        for(String[] d:data ){
            values+="('"+d[0]+"','"+d[1]+"','"+d[2]+"','"+d[4]+"','"+d[3]+"',";
            if(d[5].equals("oa")){
                Organisation o = organisationRepository.findSampleOrganisationByName(d[6]);
                values+=o.getId()+",null,null,2,1,1,0,0,'shivain22@gmail.com','shivain22@gmail.com'),";
            }else if(d[5].equals("ca")){
                Customer c = customerRepository.findSampleCustomerByNameAndOrgName(d[6],d[7]);
                values+="null,"+c.getId()+",null,3,1,1,0,0,'shivain22@gmail.com','shivain22@gmail.com'),";
            }else if(d[5].equals("va")){
                Vendor v = vendorRepository.findSampleVendorByName(d[6]);
                values+="null,null,"+v.getId()+",4,1,1,0,0,'shivain22@gmail.com','shivain22@gmail.com'),";
            }else if(d[5].equals("vu")){
                Vendor v = vendorRepository.findSampleVendorByName(d[6]);
                values+="null,null,"+v.getId()+",5,1,1,0,0,'shivain22@gmail.com','shivain22@gmail.com'),";
            }else if(d[5].equals("cq")){
                Customer c = customerRepository.findSampleCustomerByNameAndOrgName(d[6],d[7]);
                values+="null,"+c.getId()+",null,6,1,1,0,0,'shivain22@gmail.com','shivain22@gmail.com'),";
            }else if(d[5].equals("oq")){
                Organisation o = organisationRepository.findSampleOrganisationByName(d[6]);
                values+=o.getId()+",null,null,6,1,1,0,0,'shivain22@gmail.com','shivain22@gmail.com'),";
            }
        }
        batchInsert(query+values.substring(0,values.length()-1));
        users = userRepository.getAllSampleUsers();
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
        users = userRepository.getAllSampleUsers();
    }

    private void addSampleProjects(List<String[]> data){
        String query="insert into project(name,description,status,is_sample_data,customer_id,project_type,image_type,video_type,audio_type,category_id,sub_category_id,qc_levels,rework_status,number_of_uploads_required,number_of_buffered_uploads_required,user_added_status) values ";
        String values = "";
        for(String[] d:data ){
            Customer c = customerRepository.findSampleCustomerByNameAndOrgName(d[2],d[3]);
            Category cc = categoryRepository.findByName(d[4]);
            SubCategory sc = subCategoryRepository.findByValue(d[5]);
            if(d[4].equals("image")){
                values += "('"+d[0]+"','"+d[1]+"',1,1,"+c.getId()+",'"+d[4]+"','"+d[5]+"',null,null,"+cc.getId()+","+sc.getId()+",1,0,0,0,1),";
            }
            else if(d[4].equals("video")){
                values += "('"+d[0]+"','"+d[1]+"',1,1,"+c.getId()+",'"+d[4]+"',null,"+d[5]+",null,"+cc.getId()+","+sc.getId()+",1,0,0,0,1),";
            }
            else if(d[4].equals("both")){
                values += "('"+d[0]+"','"+d[1]+"',1,1,"+c.getId()+",'"+d[4]+"',"+d[5]+","+d[6]+",null,"+cc.getId()+","+sc.getId()+",1,0,0,0,1),";
            }
            else if(d[4].equals("audio")){
                values += "('"+d[0]+"','"+d[1]+"',1,1,"+c.getId()+",'"+d[4]+"',null,null,"+d[5]+","+cc.getId()+","+sc.getId()+",1,0,0,0,1),";
            }
        }
        batchInsert(query+values.substring(0,values.length()-1));
        projects = projectRepository.getAllSampleProjects();
    }

    private void addSamplePropertiesForCustomer(){
        String query="insert into property(name,value,status,is_sample_data,customer_id,optional,property_type) values ";
        String values = "";
        for(Customer c: customers){
            values="";
            for(Property p:properties){
                values+="('"+p.getName()+"','"+p.getValue()+"',1,1,"+c.getId()+",1,1),";
            }
            batchInsert(query+values.substring(0,values.length()-1));
        }
    }

    private void addSampleObjects(){
        String query="insert into object(name,description,status,is_sample_data,project_id,number_of_uploads_required,buffer_percent,is_dummy) values ";
        String values = "";
        for(Project p:projects){
            values="";
            for(int i=0;i<5;i++) {
                values += "('Obj-"+i+"-" + p.getName() + "','Obj-"+i+"-" + p.getDescription() + "',1,1,"+p.getId()+",100,20,0),";
            }
            batchInsert(query+values.substring(0,values.length()-1));
        }
        objects = objectRepository.getAllSampleObjects();
    }

    private void addSampleProjectProperties(){
        String query="insert into project_property(value,status,is_sample_data,property_id,project_id) values ";
        String values = "";
        for(Project p:projects){
            values="";
            properties = propertyRepository.getAllSampleProperties(p.getCustomer().getId());
            for(Property pr:properties){
                values+= "('"+pr.getValue()+"',1,1,"+pr.getId()+","+p.getId()+"),";
            }
            batchInsert(query+values.substring(0,values.length()-1));
        }
    }
    private void addSampleObjectProperties(){
        String query="insert into object_property(value,status,is_sample_data,property_id,object_id) values ";
        String values = "";
        for(Object o:objects){
            values="";
            properties = propertyRepository.getAllSampleProperties(o.getProject().getCustomer().getId());
            for(Property pr:properties){
                values+= "('"+pr.getValue()+"',1,1,"+pr.getId()+","+o.getId()+"),";
            }
            batchInsert(query+values.substring(0,values.length()-1));
        }
    }

    private void addSampleUAMUOMUCMUVM(){
        for(User u:users){
            if(!u.getId().equals(-1l)) {
                UserOrganisationMapping uom = new UserOrganisationMapping();
                UserCustomerMapping ucm = new UserCustomerMapping();
                UserVendorMapping uvm = new UserVendorMapping();
                UserAuthorityMapping uam = new UserAuthorityMapping();
                uam.setUser(u);
                uam.setAuthority(u.getAuthority());
                uam.setStatus(1);
                uam.setSampleData(1);
                userAuthorityMappingRepository.save(uam);
                if (u.getOrganisation() != null) {
                    uom.setUser(u);
                    uom.setOrganisation(u.getOrganisation());
                    uom.setStatus(1);
                    uom.setSampleData(1);
                    userOrganisationMappingRepository.save(uom);
                }
                if (u.getCustomer() != null) {
                    ucm.setUser(u);
                    ucm.setCustomer(u.getCustomer());
                    ucm.setStatus(1);
                    ucm.setSampleData(1);
                    userCustomerMappingRepository.save(ucm);
                }
                if (u.getVendor() != null) {
                    uvm.setUser(u);
                    uvm.setVendor(u.getVendor());
                    uvm.setStatus(1);
                    uvm.setSampleData(1);
                    userVendorMappingRepository.save(uvm);
                }
            }
        }
    }

    private void addSampleUserVendorMappingObjectMappings(){
            List<Object> objects = objectRepository.getAllSampleObjects();
            List<UserVendorMapping> uvms = userVendorMappingRepository.getAllSampleUserVendorMappingsOfVendorUsers();
            List<UserVendorMappingObjectMapping> uvmoms = new ArrayList<>();
            List<UserVendorMappingProjectMapping> uvmpms = new ArrayList<>();
            for(Object o: objects){
                for(UserVendorMapping uvm: uvms){
                    UserVendorMappingObjectMapping uvmom = new UserVendorMappingObjectMapping();
                    uvmom.setUserVendorMapping(uvm);
                    uvmom.setObject(o);
                    uvmom.setSampleData(1);
                    uvmoms.add(uvmom);
                }
            }
            for(Project p: projects){
                for(UserVendorMapping uvm: uvms){
                    UserVendorMappingProjectMapping uvmpm = new UserVendorMappingProjectMapping();
                    uvmpm.setUserVendorMapping(uvm);
                    uvmpm.setProject(p);
                    uvmpm.setSampleData(1);
                    uvmpms.add(uvmpm);
                }
            }
            userVendorMappingObjectMappingRepository.saveAll(uvmoms);
            userVendorMappingProjectMappingRepository.saveAll(uvmpms);
    }

    private void addSampleUploads(){
        List<Long> uvmomids = userVendorMappingObjectMappingRepository.getAllSampleUserVendorMappingObjectMappingsIds();
        int batchSize=0;
        String values="";
        for(Long uvmomid:uvmomids) {
            for(int i=0;i<100;i++){
                values +="(1,1,2,'"+i+"-"+uvmomid+".png',"+uvmomid+",'"+i+"-"+uvmomid+".png'),";
                batchSize++;
            }
            if(batchSize%100==0){
                batchInsert("insert into upload (status,is_sample_data,approval_status,object_key,user_vendor_mapping_object_mapping_id,name) values  "+values.substring(0,values.length()-1));
                batchSize=0;
                values="";
            }
        }
    }

    private void addSampleOrgnisationQcMappings(){
        List<UserCustomerMapping> ucms = userCustomerMappingRepository.getAllSampleUserCustomerMappingsForQc();
        List<CustomerQcProjectMapping> oqpms = new ArrayList<>();
        for(Project p: projects){
            for(UserCustomerMapping ucm: ucms) {
                CustomerQcProjectMapping cqpm = new CustomerQcProjectMapping();
                cqpm.setUserCustomerMapping(ucm);
                cqpm.setProject(p);
                oqpms.add(cqpm);
            }
        }
        customerQcProjectMappingRepository.saveAll(oqpms);
    }
    private void addSampleCustomerQcMappings(){
        List<UserOrganisationMapping> uoms = userOrganisationMappingRepository.getAllSampleUserOrganisationMappingsForQc();
        List<OrganisationQcProjectMapping> oqpms = new ArrayList<>();
        for(Project p: projects){
            for(UserOrganisationMapping uom: uoms) {
                OrganisationQcProjectMapping oqpm = new OrganisationQcProjectMapping();
                oqpm.setUserOrganisationMapping(uom);
                oqpm.setProject(p);
                oqpms.add(oqpm);
            }
        }
        organisationQcProjectMappingRepository.saveAll(oqpms);
    }

    private void batchInsert(String query){
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://ainnotateservice-mysql:3306/ainnotateservice", "root", "")) {
            if (conn != null) {
                PreparedStatement ps = conn.prepareStatement(query);
                ps.executeUpdate();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cleanUpKeyCloakAndDBUsers(){
        List<User> users = userRepository.findAll();
        for(User u:users){
            if(u.getKeycloakId()!=null){
                if(u.getSampleData()!=null && u.getSampleData().equals(1)){
                    deleteUserFromKeyCloak(u);
                }
            }
        }
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
        UserResource userResource = usersRessource.get(myUser.getKeycloakId());
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
