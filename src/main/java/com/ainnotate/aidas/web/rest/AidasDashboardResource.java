package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.repository.*;
import com.ainnotate.aidas.constants.AidasConstants;
import com.ainnotate.aidas.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing {@link AidasUpload}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AidasDashboardResource {

    private final Logger log = LoggerFactory.getLogger(AidasDashboardResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasDownload";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Autowired
    private AidasDownloadRepository aidasDownloadRepository;

    @Autowired
    private AidasOrganisationRepository aidasOrganisationRepository;

    @Autowired
    private AidasCustomerRepository aidasCustomerRepository;

    @Autowired
    private AidasVendorRepository aidasVendorRepository;

    @Autowired
    private AidasProjectRepository aidasProjectRepository;

    @Autowired
    private AidasObjectRepository aidasObjectRepository;

    @Autowired
    private  AidasUserRepository aidasUserRepository;

    @Autowired
    private AidasUploadRepository aidasUploadRepository;

    /**
     * {@code GET  /aidas-dashboard} : get all the dashboard metrics.
     *
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasDashboard in body.
     */
    @GetMapping("/aidas-dashboard")
    public ResponseEntity<AidasDashboard> getDashboardDetails() {
        log.debug("REST request to get a page of AidasDownloads");
        AidasDashboard ad = new AidasDashboard();
        AidasUser aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        AidasAuthority aidasAuthority = aidasUser.getCurrentAidasAuthority();
        if(aidasAuthority.getName().equals(AidasConstants.ADMIN)){
            ad.setOrganisationCount(aidasOrganisationRepository.countAllOrgsForSuperAdmin());
            ad.setCustomerCount(aidasCustomerRepository.countAidasCustomersForSuperAdmin());
            ad.setVendorCount(aidasVendorRepository.countAllVendorsForSuperAdmin());
            ad.setProjectCount(aidasProjectRepository.countAllProjectsForSuperAdmin());
            ad.setObjectCount(aidasObjectRepository.countAllObjectsForSuperAdmin());
            ad.setUploadCount(aidasUploadRepository.countAllUploadsForSuperAdmin());
            ad.setApprovedUploadCount(aidasUploadRepository.countAllUploadsForSuperAdmin(AidasConstants.AIDAS_UPLOAD_APPROVED));
            ad.setRejectedUploadCount(aidasUploadRepository.countAllUploadsForSuperAdmin(AidasConstants.AIDAS_UPLOAD_REJECTED));
            ad.setPendingUploadCount(aidasUploadRepository.countAllUploadsForSuperAdmin(AidasConstants.AIDAS_UPLOAD_PENDING));
            ad.setUserCount(aidasUserRepository.countAllForSuperAdmin());
        }
        if(aidasAuthority.getName().equals(AidasConstants.ORG_ADMIN)){
            ad.setOrganisationCount(1l);
            ad.setCustomerCount(aidasCustomerRepository.countAidasCustomerByAidasOrganisation(aidasUser.getAidasOrganisation()));
            ad.setVendorCount(aidasVendorRepository.countAllVendorsForOrgAdmin());
            ad.setProjectCount(aidasProjectRepository.countAidasProjectByAidasCustomer_AidasOrganisation(aidasUser.getAidasOrganisation()));
            ad.setObjectCount(aidasObjectRepository.countAidasObjectByAidasProject_AidasCustomer_AidasOrganisation(aidasUser.getAidasOrganisation()));
            ad.setUploadCount(aidasUploadRepository.countAidasUploadByAidasOrganisation(aidasUser.getAidasOrganisation().getId()));
            ad.setApprovedUploadCount(aidasUploadRepository.countAidasUploadByAidasOrganisation(aidasUser.getAidasOrganisation().getId(),AidasConstants.AIDAS_UPLOAD_APPROVED));
            ad.setRejectedUploadCount(aidasUploadRepository.countAidasUploadByAidasOrganisation(aidasUser.getAidasOrganisation().getId(),AidasConstants.AIDAS_UPLOAD_REJECTED));
            ad.setPendingUploadCount(aidasUploadRepository.countAidasUploadByAidasOrganisation(aidasUser.getAidasOrganisation().getId(),AidasConstants.AIDAS_UPLOAD_PENDING));
            ad.setOrgUsersCount(aidasUserRepository.countAllByAidasOrganisation(aidasUser.getAidasOrganisation()));
            ad.setCustomerUsersCount(aidasUserRepository.countAllByAidasCustomer_AidasOrganisation(aidasUser.getAidasOrganisation()));
            ad.setAllVendorUsersCount(aidasUserRepository.countAllVendorUsers());
            ad.setUserCount(ad.getOrgUsersCount()+ ad.getCustomerUsersCount());
        }
        if(aidasAuthority.getName().equals(AidasConstants.CUSTOMER_ADMIN)){
            ad.setOrganisationCount(1l);
            ad.setCustomerCount(1l);
            ad.setVendorCount(aidasVendorRepository.countAllVendorsForCustomerAdmin());
            ad.setProjectCount(aidasProjectRepository.countAidasProjectByAidasCustomer(aidasUser.getAidasCustomer()));
            ad.setObjectCount(aidasObjectRepository.countAidasObjectByAidasProject_AidasCustomer(aidasUser.getAidasCustomer()));
            ad.setUploadCount(aidasUploadRepository.countAidasUploadByAidasCustomer(aidasUser.getAidasCustomer().getId()));
            ad.setApprovedUploadCount(aidasUploadRepository.countAidasUploadByAidasCustomer(aidasUser.getAidasCustomer().getId(),AidasConstants.AIDAS_UPLOAD_APPROVED));
            ad.setRejectedUploadCount(aidasUploadRepository.countAidasUploadByAidasCustomer(aidasUser.getAidasCustomer().getId(),AidasConstants.AIDAS_UPLOAD_REJECTED));
            ad.setPendingUploadCount(aidasUploadRepository.countAidasUploadByAidasCustomer(aidasUser.getAidasCustomer().getId(),AidasConstants.AIDAS_UPLOAD_PENDING));
            ad.setOrgUsersCount(1l);
            ad.setCustomerUsersCount(aidasUserRepository.countAllByAidasCustomer(aidasUser.getAidasCustomer()));
            ad.setAllVendorUsersCount(aidasUserRepository.countAllVendorUsers());
            ad.setUserCount(ad.getCustomerUsersCount());
        }
        if(aidasAuthority.getName().equals(AidasConstants.VENDOR_ADMIN)){
            ad.setOrganisationCount(1l);
            ad.setCustomerCount(aidasCustomerRepository.countAidasCustomerCountForVendorAdmin(aidasUser.getAidasVendor().getId()));
            ad.setVendorCount(1l);
            ad.setProjectCount(aidasProjectRepository.countAidasProjectByVendor(aidasUser.getAidasVendor().getId()));
            ad.setObjectCount(aidasObjectRepository.countAidasObjectByVendor(aidasUser.getAidasVendor().getId()));
            ad.setUploadCount(aidasUploadRepository.countAidasUploadByAidasVendor(aidasUser.getAidasCustomer().getId()));
            ad.setApprovedUploadCount(aidasUploadRepository.countAidasUploadByAidasVendor(aidasUser.getAidasVendor().getId(),AidasConstants.AIDAS_UPLOAD_APPROVED));
            ad.setRejectedUploadCount(aidasUploadRepository.countAidasUploadByAidasVendor(aidasUser.getAidasVendor().getId(),AidasConstants.AIDAS_UPLOAD_REJECTED));
            ad.setPendingUploadCount(aidasUploadRepository.countAidasUploadByAidasVendor(aidasUser.getAidasVendor().getId(),AidasConstants.AIDAS_UPLOAD_PENDING));
            ad.setVendorUsersCount(aidasUserRepository.countAllByAidasVendor(aidasUser.getAidasVendor()));
            ad.setUserCount(aidasUserRepository.countAllByAidasVendor(aidasUser.getAidasVendor()));
        }if(aidasAuthority.getName().equals(AidasConstants.VENDOR_USER)){
            ad.setOrganisationCount(1l);
            ad.setCustomerCount(1l);
            ad.setVendorCount(1l);
            ad.setProjectCount(aidasProjectRepository.countAidasProjectByVendorUser(aidasUser.getId()));
            ad.setObjectCount(aidasObjectRepository.countAidasProjectByVendorUser(aidasUser.getId()));
            ad.setUploadCount(aidasUploadRepository.countAidasUploadByAidasVendorUser(aidasUser.getId()));
            ad.setApprovedUploadCount(aidasUploadRepository.countAidasUploadByAidasVendorUser(aidasUser.getAidasVendor().getId(),AidasConstants.AIDAS_UPLOAD_APPROVED));
            ad.setRejectedUploadCount(aidasUploadRepository.countAidasUploadByAidasVendorUser(aidasUser.getAidasVendor().getId(),AidasConstants.AIDAS_UPLOAD_REJECTED));
            ad.setPendingUploadCount(aidasUploadRepository.countAidasUploadByAidasVendorUser(aidasUser.getAidasVendor().getId(),AidasConstants.AIDAS_UPLOAD_PENDING));
             ad.setUserCount(1l);
        }
        return ResponseEntity.ok().body(ad);
    }
}
