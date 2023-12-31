package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.AppProperty;
import com.ainnotate.aidas.domain.Customer;
import com.ainnotate.aidas.domain.CustomerOrganisationMapping;
import com.ainnotate.aidas.domain.Organisation;
import com.ainnotate.aidas.domain.Property;
import com.ainnotate.aidas.domain.User;
import com.ainnotate.aidas.domain.UserAuthorityMapping;
import com.ainnotate.aidas.dto.UserCustomerMappingDTO;
import com.ainnotate.aidas.repository.AppPropertyRepository;
import com.ainnotate.aidas.repository.CustomerOrganisationMappingRepository;
import com.ainnotate.aidas.repository.CustomerRepository;
import com.ainnotate.aidas.repository.OrganisationRepository;
import com.ainnotate.aidas.repository.PropertyRepository;
import com.ainnotate.aidas.repository.UserAuthorityMappingRepository;
import com.ainnotate.aidas.repository.UserRepository;
import com.ainnotate.aidas.repository.predicates.CustomerPredicatesBuilder;
import com.ainnotate.aidas.repository.predicates.ProjectPredicatesBuilder;
import com.ainnotate.aidas.repository.search.CustomerSearchRepository;
import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.security.SecurityUtils;
import com.ainnotate.aidas.service.AESCBCPKCS5Padding;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link Customer}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CustomerResource {

    private final Logger log = LoggerFactory.getLogger(CustomerResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasCustomer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CustomerRepository customerRepository;

    @Autowired
    private AppPropertyRepository appPropertyRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    private final CustomerSearchRepository aidasCustomerSearchRepository;

    @Autowired
	private UserAuthorityMappingRepository userAuthorityMappingRepository;

    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrganisationRepository organisationRepository;
    
    @Autowired
    private CustomerOrganisationMappingRepository customerOrganisationRepository;

    public CustomerResource(
        CustomerRepository customerRepository,
        CustomerSearchRepository aidasCustomerSearchRepository
    ) {
        this.customerRepository = customerRepository;
        this.aidasCustomerSearchRepository = aidasCustomerSearchRepository;
    }

    /**
     * {@code POST  /aidas-customers} : Create a new customer.
     *
     * @param customer the customer to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new customer, or with status {@code 400 (Bad Request)} if the customer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN})
    @PostMapping("/aidas-customers")
    public ResponseEntity<Customer> createAidasCustomer(@Valid @RequestBody Customer customer) throws URISyntaxException {
        User loggedInUser = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to save AidasCustomer : {}", customer);
        if (customer.getId() != null) {
            throw new BadRequestAlertException("A new customer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if( loggedInUser.getAuthority().getName().equals(AidasConstants.ADMIN) ||
            (loggedInUser.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && loggedInUser.getOrganisation()!=null && loggedInUser.getOrganisation().equals(customer.getOrganisation()))){
            try {
	        	Customer result = customerRepository.save(customer);
	        	CustomerOrganisationMapping com1 = new CustomerOrganisationMapping();
	        	Organisation org = organisationRepository.getById(-1l);
	        	com1.setOrganisation(org);
	        	com1.setCustomer(result);
	        	customerOrganisationRepository.save(com1);
	        	if(loggedInUser.getAuthority().getName().equals(AidasConstants.ADMIN) ) {
	        		com1 = new CustomerOrganisationMapping();
		        	com1.setOrganisation(result.getOrganisation());
		        	com1.setCustomer(result);
		        	customerOrganisationRepository.save(com1);
	        	}
	        	if(loggedInUser.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && loggedInUser.getOrganisation()!=null && loggedInUser.getOrganisation().equals(customer.getOrganisation())) {
	        		com1 = new CustomerOrganisationMapping();
		        	com1.setOrganisation(result.getOrganisation());
		        	com1.setCustomer(result);
		        	customerOrganisationRepository.save(com1);
	        	}
	            propertyRepository.addCustomerProperties(result.getId(),loggedInUser.getId());
	            /*
	            List<String> props = Arrays.asList("bucketName","region","accessKey","accessSecret");
	            List<Property> propsToEncrypt = propertyRepository.getAllCustomerPropertiesForEnc(customer.getId(),props);
	       			for(Property ap:propsToEncrypt) {
	       				ap.setValue( new String(AESCBCPKCS5Padding.encrypt(ap.getValue(), AidasConstants.KEY,AidasConstants.IV_STR)));
	       				propertyRepository.save(ap);
	       			}
	            appPropertyRepository.addCustomerAppProperties(result.getId(),loggedInUser.getId());
	            props = Arrays.asList("downloadBucketName","downloadRegion","downloadAccessKey","downloadAccessSecret",
	           		 "uploadBucketName","uploadRegion","uploadAccessKey","uploadAccessSecret");
	       			List<AppProperty> toBeEncProps = appPropertyRepository.getAppPropertyCust(result.getId(), props);
	       			for(AppProperty ap:toBeEncProps) {
	       				ap.setValue( new String(AESCBCPKCS5Padding.encrypt(ap.getValue(), AidasConstants.KEY,AidasConstants.IV_STR)));
	       				appPropertyRepository.save(ap);
	       			}
	       			*/
	            return ResponseEntity
	                .created(new URI("/api/aidas-customers/" + result.getId()))
	                .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
	                .body(result);
            }catch(Exception e) {
            	throw new BadRequestAlertException("Customer already exists.  Please change the name of Customer.", ENTITY_NAME, "idinvalid");
            }
        }else{
            throw new BadRequestAlertException("Not Authorised", ENTITY_NAME, "idinvalid");
        }
    }

    /**
     * {@code PUT  /aidas-customers/:id} : Updates an existing customer.
     *
     * @param id the id of the customer to save.
     * @param customer the customer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customer,
     * or with status {@code 400 (Bad Request)} if the customer is not valid,
     * or with status {@code 500 (Internal Server Error)} if the customer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @PutMapping("/aidas-customers/{id}")
    public ResponseEntity<Customer> updateAidasCustomer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Customer customer
    ) throws URISyntaxException {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to update AidasCustomer : {}, {}", id, customer);
        if (customer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!customerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        if( user.getAuthority().getName().equals(AidasConstants.ADMIN) ||
            !(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null && user.getOrganisation().equals(customer.getOrganisation()))||
            !(user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) && user.getCustomer().equals(customer))
        ){
            Customer result = customerRepository.save(customer);
            aidasCustomerSearchRepository.save(result);
            return ResponseEntity
                .ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, customer.getId().toString()))
                .body(result);
        }else{
            throw new BadRequestAlertException("Not Authorised", ENTITY_NAME, "idinvalid");
        }
    }


    /**
     * {@code GET  /aidas-customers} : get all the aidasCustomers.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasCustomers in body.
     */
    //@Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @GetMapping("/aidas-customers")
    public ResponseEntity<List<Customer>> getAllAidasCustomers(Pageable pageable) {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to get a page of AidasCustomers");
        Page<Customer> page = customerRepository.findNone(pageable);
        if( user.getAuthority().getName().equals(AidasConstants.ADMIN)){
            page = customerRepository.findAllByIdGreaterThan(pageable,-1l);
        }else if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null) {
            page = customerRepository.findAllByAidasOrganisation(pageable, user.getOrganisation());
        }else if(user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) && user.getCustomer()!=null) {
            page = customerRepository.findAllByIdEquals(pageable, user.getCustomer().getId());
        }
        if(page!=null ){
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        }else{
            throw new BadRequestAlertException("Not Authorised", ENTITY_NAME, "idinvalid");
        }
    }

    /**
     * {@code GET  /aidas-customers/dropdown} : get all the aidasCustomers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasCustomers in body.
     */
    //@Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @GetMapping("/aidas-customers/dropdown")
    public ResponseEntity<List<Customer>> getCustomersForDropDown() {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to get a page of AidasCustomers");
        List<Customer> customers =new ArrayList<>();
        if( user.getAuthority().getName().equals(AidasConstants.ADMIN)){
            customers = customerRepository.findAllByIdGreaterThanForDropDown(-1l);
        }else if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null) {
            customers = customerRepository.findAllByAidasOrganisationForDropDown(user.getOrganisation().getId());
        }else if(user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) && user.getCustomer()!=null) {
            customers = customerRepository.findAllByIdEqualsForDropDown(user.getCustomer().getId());
        }
        return ResponseEntity.ok().body(customers);
    }
    
    /**
     * {@code GET  /aidas-customers/dropdown} : get all the aidasCustomers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasCustomers in body.
     */
    //@Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @GetMapping("/aidas-customers/dropdown/{organisationId}")
    public ResponseEntity<List<Customer>> getCustomersOfOrganisationForDropDown(@PathVariable Long organisationId) {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to get a page of AidasCustomers");
        List<Customer> customers =null;
        if( user.getAuthority().getName().equals(AidasConstants.ADMIN)){
            customers = customerRepository.findAllByAidasOrganisationForDropDown(organisationId);
        }else if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null) {
            customers = customerRepository.findAllByAidasOrganisationForDropDown(user.getOrganisation().getId());
        }else if(user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) && user.getCustomer()!=null) {
            customers = customerRepository.findAllByIdEqualsForDropDown(user.getCustomer().getId());
        }
        if(customers!=null ){
            return ResponseEntity.ok().body(customers);
        }else{
            throw new BadRequestAlertException("Not Authorised", ENTITY_NAME, "idinvalid");
        }
    }
    
    /**
     * {@code GET  /aidas-customers/dropdown} : get all the aidasCustomers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasCustomers in body.
     */
    //@Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @GetMapping("/aidas-customers/dropdown/new")
    public ResponseEntity<List<UserCustomerMappingDTO>> getCustomersForDropDownNew() {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        UserAuthorityMapping uam = userAuthorityMappingRepository.findByAuthorityIdAndUserId(user.getAuthority().getId(), user.getId());
        log.debug("REST request to get a page of AidasCustomers");
        List<UserCustomerMappingDTO> customers =null;
        if( user.getAuthority().getName().equals(AidasConstants.ADMIN)){
            customers = customerRepository.getAllCustomersWithUamId(user.getId(),uam.getAuthority().getId());
        }else if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null) {
            customers = customerRepository.getAllCustomersWithUamIdAndOrgId(uam.getId(),user.getOrganisation().getId());
        }else if(user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) && user.getCustomer()!=null) {
            customers = customerRepository.getAllCustomersWithUamIdAndCustomerId(uam.getId(),user.getCustomer().getId());
        }
        if(customers!=null ){
            return ResponseEntity.ok().body(customers);
        }else{
            throw new BadRequestAlertException("Not Authorised", ENTITY_NAME, "idinvalid");
        }
    }

    /**
     * {@code GET  /aidas-customers/:id} : get the "id" customer.
     *
     * @param id the id of the customer to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the customer, or with status {@code 404 (Not Found)}.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @GetMapping("/aidas-customers/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable Long id) {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Optional<Customer> customer = customerRepository.findById(id);;
        log.debug("REST request to get AidasCustomer : {}", id);
        if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) ){
            customer = customerRepository.findById(id);
            if(customer.isPresent()){
                if(!customer.get().getOrganisation().equals(user.getOrganisation())){
                    throw new BadRequestAlertException("Not Authorised", ENTITY_NAME, "idinvalid");
                }
            }
        }
        return ResponseUtil.wrapOrNotFound(customer);
    }

    /**
     * {@code DELETE  /aidas-customers/:id} : delete the "id" customer.
     *
     * @param id the id of the customer to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN})
    @DeleteMapping("/aidas-customers/{id}")
    public ResponseEntity<Void> deleteAidasCustomer(@PathVariable Long id) {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to delete AidasCustomer : {}", id);
        Customer customer = customerRepository.getById(id);
        if( user.getAuthority().getName().equals(AidasConstants.ADMIN) ||
            !(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN) && user.getOrganisation()!=null && user.getOrganisation().equals(customer.getOrganisation()))||
            !(user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN) && user.getCustomer().equals(customer))
        ) {
            //aidasCustomerRepository.deleteById(id);
            //aidasCustomerSearchRepository.deleteById(id);
            if(customer !=null) {
                customer.setStatus(0);
                customerRepository.save(customer);
            }
            return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                .build();
        }else{
            throw new BadRequestAlertException("Not Authorised", ENTITY_NAME, "idinvalid");
        }
    }

    /**
     * {@code SEARCH  /_search/aidas-customers?query=:query} : search for the customer corresponding
     * to the query.
     *
     * @param query the query of the customer search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @Secured({AidasConstants.ADMIN, AidasConstants.ORG_ADMIN, AidasConstants.CUSTOMER_ADMIN})
    @GetMapping("/_search/aidas-customers")
    public ResponseEntity<List<Customer>> searchAidasCustomers(@RequestParam String query, Pageable pageable) {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to search for a page of AidasCustomers for query {}", query);
        Page<Customer> page = aidasCustomerSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
    
    @GetMapping(value = "/search/customers")
    @ResponseBody
    public ResponseEntity<List<Customer>> search(@RequestParam(value = "search") String search, Pageable pageable) {
        CustomerPredicatesBuilder builder = new CustomerPredicatesBuilder();

        if (search != null) {
            Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
            Matcher matcher = pattern.matcher(search + ",");
            while (matcher.find()) {
                builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
            }
        }
        builder.with("id", ">",0);
        BooleanExpression exp = builder.build();
        Page<Customer> page = customerRepository.findAll(exp,pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
