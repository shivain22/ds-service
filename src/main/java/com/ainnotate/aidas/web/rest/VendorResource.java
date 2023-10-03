package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.dto.UserVendorMappingDTO;
import com.ainnotate.aidas.dto.UsersOfVendorDTO;
import com.ainnotate.aidas.dto.VendorCustomerMappingDTO;
import com.ainnotate.aidas.dto.VendorOrganisationMappingDTO;
import com.ainnotate.aidas.dto.VendorUserDTO;
import com.ainnotate.aidas.repository.AppPropertyRepository;
import com.ainnotate.aidas.repository.CustomerRepository;
import com.ainnotate.aidas.repository.OrganisationRepository;
import com.ainnotate.aidas.repository.UserAuthorityMappingRepository;
import com.ainnotate.aidas.repository.UsersOfVendorRepository;
import com.ainnotate.aidas.repository.VendorCustomerMappingRepository;
import com.ainnotate.aidas.repository.VendorOrganisationMappingRepository;
import com.ainnotate.aidas.repository.UserRepository;
import com.ainnotate.aidas.repository.VendorRepository;
import com.ainnotate.aidas.repository.predicates.ProjectPredicatesBuilder;
import com.ainnotate.aidas.repository.predicates.VendorPredicatesBuilder;
import com.ainnotate.aidas.repository.search.VendorSearchRepository;
import com.ainnotate.aidas.security.SecurityUtils;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * REST controller for managing {@link Vendor}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class VendorResource {

	private final Logger log = LoggerFactory.getLogger(VendorResource.class);

	private static final String ENTITY_NAME = "ainnotateserviceAidasVendor";

	@Value("${jhipster.clientApp.name}")
	private String applicationName;

	@Autowired
	private AppPropertyRepository appPropertyRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private OrganisationRepository organisationRepository;
	
	@Autowired
	private VendorCustomerMappingRepository vendorCustomerMappingRepository;
	
	@Autowired
	private VendorOrganisationMappingRepository vendorOrganisationMappingRepository;
	
	@Autowired
	private UserAuthorityMappingRepository userAuthorityMappingRepository;

	private final VendorRepository vendorRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UsersOfVendorRepository userOfVendorRepository;

	private final VendorSearchRepository aidasVendorSearchRepository;

	public VendorResource(VendorRepository vendorRepository, VendorSearchRepository aidasVendorSearchRepository) {
		this.vendorRepository = vendorRepository;
		this.aidasVendorSearchRepository = aidasVendorSearchRepository;
	}

	/**
	 * {@code POST  /aidas-vendors} : Create a new vendor.
	 *
	 * @param vendor the vendor to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new vendor, or with status {@code 400 (Bad Request)} if the
	 *         vendor has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping("/aidas-vendors")
	public ResponseEntity<Vendor> createAidasVendor(@RequestBody Vendor vendor) throws URISyntaxException {
		log.debug("REST request to save AidasVendor : {}", vendor);
		if (vendor.getId() != null) {
			throw new BadRequestAlertException("A new vendor cannot already have an ID", ENTITY_NAME, "idexists");
		}
		try {
			Vendor result = vendorRepository.save(vendor);
			Set<AppProperty> appProperties = appPropertyRepository.getAppPropertyOfVendor(-1l);
			for (AppProperty ap : appProperties) {
				AppProperty p = new AppProperty();
				p.setName(ap.getName());
				p.setValue(ap.getValue());
				p.setVendor(result);
				appPropertyRepository.save(p);
			}
			if((vendor.getCustomerDtos()==null || (vendor.getCustomerDtos()!=null && vendor.getCustomerDtos().size()==0))
					&& (vendor.getOrganisationDtos()==null || (vendor.getOrganisationDtos()!=null && vendor.getOrganisationDtos().size()==0))) {
				VendorOrganisationMapping vom1 = vendorOrganisationMappingRepository.getByOrgIdAndVendorId(-1l, -1l);
				if(vom1==null) {
					vom1= new VendorOrganisationMapping();
					vom1.setVendor(vendorRepository.getById(-1l));
					vom1.setOrganisation(organisationRepository.getById(-1l));
					vendorOrganisationMappingRepository.save(vom1);
				}
				VendorCustomerMapping vcm1 = vendorCustomerMappingRepository.getByCustomerAndVendor(-1l, -1l);
				if(vcm1==null) {
					vcm1 = new VendorCustomerMapping();
					vcm1.setVendor(vendor);
					vcm1.setCustomer(customerRepository.getById(-1l));
					vendorCustomerMappingRepository.save(vcm1);
				}
			}
			
			VendorOrganisationMapping vom = new VendorOrganisationMapping();
			/*vom.setOrganisation(organisationRepository.getById(-1l));
			vom.setVendor(result);
			vendorOrganisationMappingRepository.save(vom);*/
			
			
			
			VendorCustomerMapping vcm = new VendorCustomerMapping();
			/*vcm.setVendor(vendor);
			vcm.setCustomer(customerRepository.getById(-1l));
			vendorCustomerMappingRepository.save(vcm);*/
			
			if(vendor.getCustomerDtos()!=null) {
				for(VendorCustomerMappingDTO vdto:vendor.getCustomerDtos()) {
					Customer customer = customerRepository.getById(vdto.getCustomerId());
					vcm = new VendorCustomerMapping();
					vcm.setVendor(vendor);
					vcm.setCustomer(customer);
					vendorCustomerMappingRepository.save(vcm);
				}
			}
			
			if(vendor.getOrganisationDtos()!=null) {
				for(VendorOrganisationMappingDTO vdto:vendor.getOrganisationDtos()) {
					Organisation organisation = organisationRepository.getById(vdto.getOrganisationId());
					vom = new VendorOrganisationMapping();
					vom.setVendor(vendor);
					vom.setOrganisation(organisation);
					vendorOrganisationMappingRepository.save(vom);
				}
			}
			
			return ResponseEntity
					.created(new URI("/api/aidas-vendors/" + result.getId())).headers(HeaderUtil
							.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
					.body(result);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "idexists");
		}
	}

	/**
	 * {@code PUT  /aidas-vendors/:id} : Updates an existing vendor.
	 *
	 * @param id     the id of the vendor to save.
	 * @param vendor the vendor to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated vendor, or with status {@code 400 (Bad Request)} if the
	 *         vendor is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the vendor couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PutMapping("/aidas-vendors/{id}")
	public ResponseEntity<Vendor> updateAidasVendor(@PathVariable(value = "id", required = false) final Long id,
			@Valid @RequestBody Vendor vendor) throws URISyntaxException {
		log.debug("REST request to update AidasVendor : {}, {}", id, vendor);
		if (vendor.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		if (!Objects.equals(id, vendor.getId())) {
			throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
		}

		if (!vendorRepository.existsById(id)) {
			throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
		}
		
		VendorCustomerMapping vcm1 = vendorCustomerMappingRepository.getByCustomerAndVendor(-1l, vendor.getId());
		if(vcm1==null) {
			vcm1 = new VendorCustomerMapping();
			vcm1.setVendor(vendor);
			vcm1.setCustomer(customerRepository.getById(-1l));
			vcm1.setStatus(AidasConstants.STATUS_ENABLED);
		}
		
		VendorOrganisationMapping vom1 = vendorOrganisationMappingRepository.getByOrgIdAndVendorId(-1l, vendor.getId());
		if(vom1==null) {
			vom1 = new VendorOrganisationMapping();
			vom1.setVendor(vendor);
			vom1.setOrganisation(organisationRepository.getById(-1l));
			vom1.setStatus(AidasConstants.STATUS_ENABLED);
		}

		Vendor result = vendorRepository.save(vendor);
		if(vendor.getCustomerDtos()!=null) {
			for(VendorCustomerMappingDTO vdto:vendor.getCustomerDtos()) {
				Customer customer = customerRepository.getById(vdto.getCustomerId());
				VendorCustomerMapping vcm = vendorCustomerMappingRepository.getByCustomerAndVendor(customer.getId(), vendor.getId());
				if(vcm==null) {
					vcm = new VendorCustomerMapping();
					vcm.setVendor(vendor);
					vcm.setCustomer(customer);
					vcm.setStatus(vdto.getStatus());
				}else {
					vcm.setStatus(vdto.getStatus());
				}
				vendorCustomerMappingRepository.save(vcm);
			}
		}
		
		if(vendor.getOrganisationDtos()!=null) {
			for(VendorOrganisationMappingDTO vdto:vendor.getOrganisationDtos()) {
				Organisation organisation = organisationRepository.getById(vdto.getOrganisationId());
				VendorOrganisationMapping vom = vendorOrganisationMappingRepository.getByOrgIdAndVendorId(organisation.getId(),vendor.getId());
				if(vom==null) {
					vom = new VendorOrganisationMapping();
					vom.setVendor(vendor);
					vom.setOrganisation(organisation);
					vom.setStatus(vdto.getStatus());
				}else {
					vom.setStatus(vdto.getStatus());
				}
				vendorOrganisationMappingRepository.save(vom);
			}
		}
		aidasVendorSearchRepository.save(result);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, vendor.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PATCH  /aidas-vendors/:id} : Partial updates given fields of an
	 * existing vendor, field will ignore if it is null
	 *
	 * @param id     the id of the vendor to save.
	 * @param vendor the vendor to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated vendor, or with status {@code 400 (Bad Request)} if the
	 *         vendor is not valid, or with status {@code 404 (Not Found)} if the
	 *         vendor is not found, or with status
	 *         {@code 500 (Internal Server Error)} if the vendor couldn't be
	 *         updated.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PatchMapping(value = "/aidas-vendors/{id}", consumes = { "application/json", "application/merge-patch+json" })
	public ResponseEntity<Vendor> partialUpdateAidasVendor(@PathVariable(value = "id", required = false) final Long id,
			@NotNull @RequestBody Vendor vendor) throws URISyntaxException {
		log.debug("REST request to partial update AidasVendor partially : {}, {}", id, vendor);
		if (vendor.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		if (!Objects.equals(id, vendor.getId())) {
			throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
		}

		if (!vendorRepository.existsById(id)) {
			throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
		}

		Optional<Vendor> result = vendorRepository.findById(vendor.getId()).map(existingAidasVendor -> {
			if (vendor.getName() != null) {
				existingAidasVendor.setName(vendor.getName());
			}
			if (vendor.getDescription() != null) {
				existingAidasVendor.setDescription(vendor.getDescription());
			}

			return existingAidasVendor;
		}).map(vendorRepository::save).map(savedAidasVendor -> {
			aidasVendorSearchRepository.save(savedAidasVendor);

			return savedAidasVendor;
		});

		return ResponseUtil.wrapOrNotFound(result,
				HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, vendor.getId().toString()));
	}


	/**
	 * {@code GET  /aidas-vendors} : get all the aidasVendors.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of aidasVendors in body.
	 */
	@GetMapping("/aidas-vendors/vendors-with-users/{projectId}")
	public ResponseEntity<List<VendorUserDTO>> getAllVendorsWithUsersProject(
			@PathVariable(value = "projectId", required = false) final Long projectId) {
		log.debug("REST request to get a page of AidasVendors");
		User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
		List<VendorUserDTO> vendorUserDtos = new ArrayList<>();
		List<UsersOfVendorDTO> vendorUsers = null;// userOfVendorRepository.getUserOfVendor(projectId);
		if(user.getAuthority().getName().equals(AidasConstants.ADMIN)) {
			vendorUsers = vendorRepository.getUsersOfVendorForAdmin(projectId);
		}else if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN)) {
			vendorUsers = vendorRepository.getUsersOfVendorForOrganisastion(projectId,user.getOrganisation().getId());
		}else if(user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN)) {
			vendorUsers = vendorRepository.getUsersOfVendorForCustomer(projectId, user.getCustomer().getId());
		}else if(user.getAuthority().getName().equals(AidasConstants.VENDOR_ADMIN)) {
			vendorUsers = vendorRepository.getUsersOfVendorForVendor(projectId, user.getVendor().getId());
		}
		Map<VendorUserDTO, List<UsersOfVendorDTO>> userPerVendor = vendorUsers.stream()
				.collect(Collectors.groupingBy(item -> {
					return new VendorUserDTO(item.getVendorId(), item.getVendorName());
				}));
		userPerVendor.forEach((k, v) -> {
			k.setUserDTOs(v);
			vendorUserDtos.add(k);
		});
		return ResponseEntity.ok().body(vendorUserDtos);
	}

	/**
	 * {@code GET  /aidas-vendors} : get all the aidasVendors.
	 *
	 * @param pageable the pagination information.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of aidasVendors in body.
	 */
	@GetMapping("/aidas-vendors")
	public ResponseEntity<List<Vendor>> getAllVendors(Pageable pageable) {
		log.debug("REST request to get a page of AidasVendors");
		User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
		
		Page<Vendor> page = null;// vendorRepository.findAllByIdGreaterThan(0l, pageable);
		if(user.getAuthority().getName().equals(AidasConstants.ADMIN)) {
			/*Organisation o = organisationRepository.getById(-1l);
			Customer c = customerRepository.getById(-1l);
			List<Vendor> vendorList = vendorRepository.findAllByIdGreaterThanForDropDown(-1l);
			for(Vendor v: vendorList) {
				
				VendorOrganisationMapping vom = vendorOrganisationMappingRepository.getByOrgIdAndVendorId(-1l,v.getId());
				if(vom==null) {
					vom = new VendorOrganisationMapping();
					vom.setVendor(v);
					vom.setOrganisation(o);
					vendorOrganisationMappingRepository.save(vom);
				}
				VendorCustomerMapping vcm = vendorCustomerMappingRepository.getByCustomerAndVendor(-1l, v.getId());
				if(vcm==null) {
					vcm = new VendorCustomerMapping();
					vcm.setVendor(v);
					vcm.setCustomer(c);
					vendorCustomerMappingRepository.save(vcm);
				}
			}*/
			page = vendorRepository.findAllByIdGreaterThan(0l, pageable);
		}else if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN)) {
			page = vendorRepository.findAllByOrganisation(user.getOrganisation().getId(), pageable);
		}else if(user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN)) {
			page = vendorRepository.findAllByCustomer(user.getCustomer().getId(), pageable);
		}else if(user.getAuthority().getName().equals(AidasConstants.VENDOR_ADMIN)) {
			page = vendorRepository.findAllByVendor(user.getVendor().getId(), pageable);
		}
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers).body(page.getContent());
	}

	/**
	 * {@code GET  /aidas-vendors} : get all the aidasVendors.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of aidasVendors in body.
	 */
	@GetMapping("/aidas-vendors/dropdown")
	public ResponseEntity<List<Vendor>> getAllVendorsForDropDown() {
		log.debug("REST request to get a page of AidasVendors");
		User loggedInUser = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
		List<Vendor> vendors =new ArrayList();
        if(loggedInUser.getAuthority().getName().equals(AidasConstants.ADMIN)) {
        	vendors = vendorRepository.findAll();
        }else {
        	vendors = vendorRepository.findAllByIdGreaterThanForDropDown(0l);
        }
		
		return ResponseEntity.ok().body(vendors);
	}
	
	/**
	 * {@code GET  /aidas-vendors} : get all the aidasVendors.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of aidasVendors in body.
	 */
	@GetMapping("/aidas-vendors/dropdown/new")
	public ResponseEntity<List<UserVendorMappingDTO>> getAllVendorsForDropDownNew() {
		log.debug("REST request to get a page of AidasVendors");
		User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
		List<UserVendorMappingDTO> vendors =new ArrayList();
        UserAuthorityMapping uam = userAuthorityMappingRepository.findByAuthorityIdAndUserId(user.getAuthority().getId(), user.getId());
        if(user.getAuthority().getName().equals(AidasConstants.ADMIN)) {
        	vendors = vendorRepository.getAllVendorsWithUamId(user.getId(),uam.getAuthority().getId());
		}else if(user.getAuthority().getName().equals(AidasConstants.ORG_ADMIN)) {
			vendors = vendorRepository.getAllVendorsOfOrganisation(user.getOrganisation().getId());
		}else if(user.getAuthority().getName().equals(AidasConstants.CUSTOMER_ADMIN)) {
			vendors = vendorRepository.getAllVendorsWithUamId(user.getId(),uam.getAuthority().getId());
		}else if(user.getAuthority().getName().equals(AidasConstants.VENDOR_ADMIN)) {
			vendors = vendorRepository.getAllVendorsWithUamId(user.getId(),uam.getAuthority().getId());
		}
		
		return ResponseEntity.ok().body(vendors);
	}

	/**
	 * {@code GET  /aidas-vendors/:id} : get the "id" vendor.
	 *
	 * @param id the id of the vendor to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the vendor, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("/aidas-vendors/{id}")
	public ResponseEntity<Vendor> getVendor(@PathVariable Long id) {
		log.debug("REST request to get AidasVendor : {}", id);
		Vendor vendor = vendorRepository.getById(id);
		vendor.setCustomerDtos(vendorRepository.getAllCustomers(vendor.getId()));
		vendor.setOrganisationDtos(vendorRepository.getAllOrganisations(id));
		return ResponseEntity.ok().body(vendor);
	}

	/**
	 * {@code DELETE  /aidas-vendors/:id} : delete the "id" vendor.
	 *
	 * @param id the id of the vendor to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@DeleteMapping("/aidas-vendors/{id}")
	public ResponseEntity<Void> deleteAidasVendor(@PathVariable Long id) {
		log.debug("REST request to delete AidasVendor : {}", id);
		Vendor vendor = vendorRepository.getById(id);
		if (vendor != null) {
			vendor.setStatus(0);
			vendorRepository.save(vendor);
		}
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
				.build();
	}

	/**
	 * {@code SEARCH  /_search/aidas-vendors?query=:query} : search for the vendor
	 * corresponding to the query.
	 *
	 * @param query    the query of the vendor search.
	 * @param pageable the pagination information.
	 * @return the result of the search.
	 */
	@GetMapping("/_search/aidas-vendors")
	public ResponseEntity<List<Vendor>> searchAidasVendors(@RequestParam String query, Pageable pageable) {
		log.debug("REST request to search for a page of AidasVendors for query {}", query);
		Page<Vendor> page = aidasVendorSearchRepository.search(query, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers).body(page.getContent());
	}

	@GetMapping(value = "/search/vendors")
	@ResponseBody
	public ResponseEntity<List<Vendor>> search(@RequestParam(value = "search") String search, Pageable pageable) {
		VendorPredicatesBuilder builder = new VendorPredicatesBuilder();

		if (search != null) {
			Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
			Matcher matcher = pattern.matcher(search + ",");
			while (matcher.find()) {
				builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
			}
		}
		builder.with("id", ">",0);
		BooleanExpression exp = builder.build();
		Page<Vendor> page = vendorRepository.findAll(exp, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return ResponseEntity.ok().headers(headers).body(page.getContent());
	}
}
