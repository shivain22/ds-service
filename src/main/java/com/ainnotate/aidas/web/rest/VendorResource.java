package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.AppProperty;
import com.ainnotate.aidas.domain.User;
import com.ainnotate.aidas.domain.Vendor;
import com.ainnotate.aidas.dto.IUserDTO;
import com.ainnotate.aidas.dto.UserDTO;
import com.ainnotate.aidas.dto.VendorUserDTO;
import com.ainnotate.aidas.repository.AppPropertyRepository;
import com.ainnotate.aidas.repository.UserRepository;
import com.ainnotate.aidas.repository.VendorRepository;
import com.ainnotate.aidas.repository.search.VendorSearchRepository;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import liquibase.pro.packaged.V;
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

    private final VendorRepository vendorRepository;

    @Autowired
    private UserRepository userRepository;

    private final VendorSearchRepository aidasVendorSearchRepository;

    public VendorResource(VendorRepository vendorRepository, VendorSearchRepository aidasVendorSearchRepository) {
        this.vendorRepository = vendorRepository;
        this.aidasVendorSearchRepository = aidasVendorSearchRepository;
    }

    /**
     * {@code POST  /aidas-vendors} : Create a new vendor.
     *
     * @param vendor the vendor to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new vendor, or with status {@code 400 (Bad Request)} if the vendor has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-vendors")
    public ResponseEntity<Vendor> createAidasVendor(@Valid @RequestBody Vendor vendor) throws URISyntaxException {
        log.debug("REST request to save AidasVendor : {}", vendor);
        if (vendor.getId() != null) {
            throw new BadRequestAlertException("A new vendor cannot already have an ID", ENTITY_NAME, "idexists");
        }

        Vendor result = vendorRepository.save(vendor);
        Set<AppProperty> appProperties = appPropertyRepository.getAppPropertyOfVendor(-1l);
        result.setAppProperties(appProperties);
        return ResponseEntity
            .created(new URI("/api/aidas-vendors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /aidas-vendors/:id} : Updates an existing vendor.
     *
     * @param id the id of the vendor to save.
     * @param vendor the vendor to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vendor,
     * or with status {@code 400 (Bad Request)} if the vendor is not valid,
     * or with status {@code 500 (Internal Server Error)} if the vendor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-vendors/{id}")
    public ResponseEntity<Vendor> updateAidasVendor(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Vendor vendor
    ) throws URISyntaxException {
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

        Vendor result = vendorRepository.save(vendor);
        aidasVendorSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, vendor.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /aidas-vendors/:id} : Partial updates given fields of an existing vendor, field will ignore if it is null
     *
     * @param id the id of the vendor to save.
     * @param vendor the vendor to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vendor,
     * or with status {@code 400 (Bad Request)} if the vendor is not valid,
     * or with status {@code 404 (Not Found)} if the vendor is not found,
     * or with status {@code 500 (Internal Server Error)} if the vendor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-vendors/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Vendor> partialUpdateAidasVendor(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Vendor vendor
    ) throws URISyntaxException {
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

        Optional<Vendor> result = vendorRepository
            .findById(vendor.getId())
            .map(existingAidasVendor -> {
                if (vendor.getName() != null) {
                    existingAidasVendor.setName(vendor.getName());
                }
                if (vendor.getDescription() != null) {
                    existingAidasVendor.setDescription(vendor.getDescription());
                }

                return existingAidasVendor;
            })
            .map(vendorRepository::save)
            .map(savedAidasVendor -> {
                aidasVendorSearchRepository.save(savedAidasVendor);

                return savedAidasVendor;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, vendor.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-vendors} : get all the aidasVendors.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasVendors in body.
     */
    @GetMapping("/aidas-vendors/vendors-with-users")
    public ResponseEntity<List<VendorUserDTO>> getAllVendorsWithUsers() {
        log.debug("REST request to get a page of AidasVendors");
        List<VendorUserDTO> vendorUserDtos = new ArrayList<>();
        List<Vendor> vendors = vendorRepository.getAllVendors();
        for(Vendor v:vendors){
            VendorUserDTO vendorUserDto = new VendorUserDTO();
            vendorUserDto.setVendorId(v.getId());
            vendorUserDto.setName(v.getName());

            List<IUserDTO> vendorUsers = userRepository.findAllUsersOfVendor(v.getId());
            for(IUserDTO iu:vendorUsers){
                UserDTO u = new UserDTO();
                u.setLastName(iu.getLastName());
                u.setFirstName(iu.getFirstName());
                u.setUserVendorMappingId(iu.getUserVendorMappingId());
                u.setUserId(iu.getUserId());
                //to be modified later for getting the actual state of the user against any object
                u.setStatus(0);
                vendorUserDto.getUserDTOs().add(u);
            }
            vendorUserDtos.add(vendorUserDto);
        }
        return ResponseEntity.ok().body(vendorUserDtos);
    }

    /**
     * {@code GET  /aidas-vendors} : get all the aidasVendors.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasVendors in body.
     */
    @GetMapping("/aidas-vendors/vendors-with-users/{projectId}")
    public ResponseEntity<List<VendorUserDTO>> getAllVendorsWithUsers(@PathVariable(value = "projectId", required = false) final Long projectId) {
        log.debug("REST request to get a page of AidasVendors");
        List<VendorUserDTO> vendorUserDtos = new ArrayList<>();
        List<UserDTO> vendorUsers = userRepository.findAllUsersOfVendorWithProject(projectId);
        Map<VendorUserDTO, List<UserDTO>> userPerVendor = vendorUsers.stream().collect(Collectors.groupingBy(item->{return new VendorUserDTO(item.getVendorId(),item.getVendorName());}));
        userPerVendor.forEach((k, v) -> {k.setUserDTOs(v); vendorUserDtos.add(k);});
        return ResponseEntity.ok().body(vendorUserDtos);
    }

    /**
     * {@code GET  /aidas-vendors} : get all the aidasVendors.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasVendors in body.
     */
    @GetMapping("/aidas-vendors")
    public ResponseEntity<List<Vendor>> getAllVendors(Pageable pageable) {
        log.debug("REST request to get a page of AidasVendors");
        Page<Vendor> page = vendorRepository.findAllByIdGreaterThan(0l,pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-vendors} : get all the aidasVendors.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasVendors in body.
     */
    @GetMapping("/aidas-vendors/dropdown")
    public ResponseEntity<List<Vendor>> getAllVendorsForDropDown() {
        log.debug("REST request to get a page of AidasVendors");
        List<Vendor> vendors = vendorRepository.findAllByIdGreaterThanForDropDown(0l);
        return ResponseEntity.ok().body(vendors);
    }

    /**
     * {@code GET  /aidas-vendors/:id} : get the "id" vendor.
     *
     * @param id the id of the vendor to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vendor, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-vendors/{id}")
    public ResponseEntity<Vendor> getVendor(@PathVariable Long id) {
        log.debug("REST request to get AidasVendor : {}", id);
        Optional<Vendor> vendor = vendorRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(vendor);
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
        //aidasVendorRepository.deleteById(id);
        //aidasVendorSearchRepository.deleteById(id);
        Vendor vendor = vendorRepository.getById(id);
        if(vendor !=null){
            vendor.setStatus(0);
            vendorRepository.save(vendor);
        }
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-vendors?query=:query} : search for the vendor corresponding
     * to the query.
     *
     * @param query the query of the vendor search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/aidas-vendors")
    public ResponseEntity<List<Vendor>> searchAidasVendors(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasVendors for query {}", query);
        Page<Vendor> page = aidasVendorSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
