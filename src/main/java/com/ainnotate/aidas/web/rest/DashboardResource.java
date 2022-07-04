package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.repository.*;
import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.security.SecurityUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * REST controller for managing {@link Upload}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class DashboardResource {

    private final Logger log = LoggerFactory.getLogger(DashboardResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasDownload";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Autowired
    private DownloadRepository downloadRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ObjectRepository objectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UploadRepository uploadRepository;

    /**
     * {@code GET  /aidas-dashboard} : get all the dashboard metrics.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasDashboard in body.
     */
    @GetMapping("/aidas-dashboard")
    public ResponseEntity<Dashboard> getDashboardDetails() {
        log.debug("REST request to get a page of AidasDownloads");
        Dashboard ad = new Dashboard();
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Authority authority = user.getAuthority();
        if(authority.getName().equals(AidasConstants.ADMIN)){
            ad.setOrganisationCount(organisationRepository.countAllOrgsForSuperAdmin());
            ad.setCustomerCount(customerRepository.countAidasCustomersForSuperAdmin());
            ad.setVendorCount(vendorRepository.countAllVendorsForSuperAdmin());
            ad.setProjectCount(projectRepository.countAllProjectsForSuperAdmin());
            ad.setObjectCount(objectRepository.countAllObjectsForSuperAdmin());
            //ad.setUploadCount(uploadRepository.countAllUploadsForSuperAdmin());
            ad.setUserCount(userRepository.countAllForSuperAdmin());
        }
        if(authority.getName().equals(AidasConstants.ORG_ADMIN)){
            ad.setOrganisationCount(1l);
            ad.setCustomerCount(customerRepository.countAidasCustomerByAidasOrganisation(user.getOrganisation()));
            ad.setVendorCount(vendorRepository.countAllVendorsForOrgAdmin());
            ad.setProjectCount(projectRepository.countAidasProjectByAidasCustomer_AidasOrganisation(user.getOrganisation().getId()));
            ad.setObjectCount(objectRepository.countAidasObjectByAidasProject_AidasCustomer_AidasOrganisation(user.getOrganisation().getId()));
            //ad.setUploadCount(uploadRepository.countAidasUploadByAidasOrganisation(user.getOrganisation().getId()));
            ad.setOrgUsersCount(userRepository.countAllByOrganisation(user.getOrganisation().getId()));
            ad.setCustomerUsersCount(userRepository.countAllByCustomer_Organisation(user.getOrganisation().getId()));
            ad.setAllVendorUsersCount(userRepository.countAllVendorUsers());
            ad.setUserCount(ad.getOrgUsersCount()+ ad.getCustomerUsersCount());
        }
        if(authority.getName().equals(AidasConstants.CUSTOMER_ADMIN)){
            ad.setOrganisationCount(1l);
            ad.setCustomerCount(1l);
            ad.setVendorCount(vendorRepository.countAllVendorsForCustomerAdmin());
            ad.setProjectCount(projectRepository.countAidasProjectByAidasCustomer(user.getCustomer().getId()));
            ad.setObjectCount(objectRepository.countAidasObjectByAidasProject_AidasCustomer(user.getCustomer().getId()));
            //ad.setUploadCount(uploadRepository.countAidasUploadByAidasCustomer(user.getCustomer().getId()));
            ad.setOrgUsersCount(1l);
            ad.setCustomerUsersCount(userRepository.countAllByCustomer(user.getCustomer().getId()));
            ad.setAllVendorUsersCount(userRepository.countAllVendorUsers());
            ad.setUserCount(ad.getCustomerUsersCount());
        }
        if(authority.getName().equals(AidasConstants.VENDOR_ADMIN)){
            ad.setOrganisationCount(1l);
            ad.setCustomerCount(customerRepository.countAidasCustomerCountForVendorAdmin(user.getVendor().getId()));
            ad.setVendorCount(1l);
            ad.setProjectCount(projectRepository.countAidasProjectByVendor(user.getVendor().getId()));
            ad.setObjectCount(objectRepository.countAidasObjectByVendor(user.getVendor().getId()));
            //ad.setUploadCount(uploadRepository.countAidasUploadByAidasVendor(user.getCustomer().getId()));
            ad.setVendorUsersCount(userRepository.countAllByVendor(user.getVendor().getId()));
            ad.setUserCount(userRepository.countAllByVendor(user.getVendor().getId()));
        }if(authority.getName().equals(AidasConstants.VENDOR_USER)){
            ad.setOrganisationCount(1l);
            ad.setCustomerCount(1l);
            ad.setVendorCount(1l);
            ad.setProjectCount(projectRepository.countAidasProjectByVendorUser(user.getId()));
            ad.setObjectCount(objectRepository.countAidasProjectByVendorUser(user.getId()));
            ad.setUploadCount(uploadRepository.countAidasUploadByAidasVendorUser(user.getId()));
            ad.setApprovedUploadCount(uploadRepository.countAidasUploadByAidasVendorUser(user.getVendor().getId(),AidasConstants.AIDAS_UPLOAD_APPROVED));
            ad.setRejectedUploadCount(uploadRepository.countAidasUploadByAidasVendorUser(user.getVendor().getId(),AidasConstants.AIDAS_UPLOAD_REJECTED));
            ad.setPendingUploadCount(uploadRepository.countAidasUploadByAidasVendorUser(user.getVendor().getId(),AidasConstants.AIDAS_UPLOAD_PENDING));
             ad.setUserCount(1l);
        }
        return ResponseEntity.ok().body(ad);
    }
}
