package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.*;
import com.ainnotate.aidas.repository.*;
import com.ainnotate.aidas.security.AidasAuthoritiesConstants;
import com.ainnotate.aidas.security.SecurityUtils;
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

import java.util.List;
import java.util.Optional;

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
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ADMIN)){
            ad.setOrganisationCount(aidasOrganisationRepository.count());
            ad.setCustomerCount(aidasCustomerRepository.count());
            ad.setVendorCount(aidasVendorRepository.count());
            ad.setProjectCount(aidasProjectRepository.count());
            ad.setObjectCount(aidasObjectRepository.count());
            ad.setUploadCount(aidasUploadRepository.count());
            ad.setApprovedUploadCount(aidasUploadRepository.countAidasUploadByStatusTrue());
            ad.setRejectedUploadCount(aidasUploadRepository.countAidasUploadByStatusFalse());
            ad.setPendingUploadCount(aidasUploadRepository.countAidasUploadByStatusIsNull());
            ad.setUserCount(aidasUserRepository.count());
        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.ORG_ADMIN)){
            ad.setOrganisationCount(1l);
            ad.setCustomerCount(aidasCustomerRepository.countAidasCustomerByAidasOrganisation(aidasUser.getAidasOrganisation()));
            ad.setVendorCount(aidasVendorRepository.count());
            ad.setProjectCount(aidasProjectRepository.countAidasProjectByAidasCustomer_AidasOrganisation(aidasUser.getAidasOrganisation()));
            ad.setObjectCount(aidasObjectRepository.countAidasObjectByAidasProject_AidasCustomer_AidasOrganisation(aidasUser.getAidasOrganisation()));
            ad.setUploadCount(aidasUploadRepository.countAidasUploadByAidasOrganisation(aidasUser.getAidasOrganisation().getId()));
            ad.setApprovedUploadCount(aidasUploadRepository.countAidasUploadByAidasOrganisationAndStatusTrue(aidasUser.getAidasOrganisation().getId()));
            ad.setRejectedUploadCount(aidasUploadRepository.countAidasUploadByAidasOrganisationAndStatusFalse(aidasUser.getAidasOrganisation().getId()));
            ad.setPendingUploadCount(aidasUploadRepository.countAidasUploadByAidasOrganisationAndStatusIsNull(aidasUser.getAidasOrganisation().getId()));
            ad.setOrgUsersCount(aidasUserRepository.countAllByAidasOrganisation(aidasUser.getAidasOrganisation()));
            ad.setCustomerUsersCount(aidasUserRepository.countAllByAidasCustomer_AidasOrganisation(aidasUser.getAidasOrganisation()));
            ad.setAllVendorUsersCount(aidasUserRepository.countAllVendorUsers());
            ad.setUserCount(ad.getOrgUsersCount()+ ad.getCustomerUsersCount());
        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.CUSTOMER_ADMIN)){
            ad.setOrganisationCount(1l);
            ad.setCustomerCount(1l);
            ad.setVendorCount(aidasVendorRepository.count());
            ad.setProjectCount(aidasProjectRepository.countAidasProjectByAidasCustomer(aidasUser.getAidasCustomer()));
            ad.setObjectCount(aidasObjectRepository.countAidasObjectByAidasProject_AidasCustomer(aidasUser.getAidasCustomer()));
            ad.setUploadCount(aidasUploadRepository.countAidasUploadByAidasCustomer(aidasUser.getAidasCustomer().getId()));
            ad.setApprovedUploadCount(aidasUploadRepository.countAidasUploadByAidasCustomerStatusTrue(aidasUser.getAidasCustomer().getId()));
            ad.setRejectedUploadCount(aidasUploadRepository.countAidasUploadByAidasCustomerStatusFalse(aidasUser.getAidasCustomer().getId()));
            ad.setPendingUploadCount(aidasUploadRepository.countAidasUploadByAidasCustomerStatusIsNull(aidasUser.getAidasCustomer().getId()));
            ad.setOrgUsersCount(1l);
            ad.setCustomerUsersCount(aidasUserRepository.countAllByAidasCustomer(aidasUser.getAidasCustomer()));
            ad.setAllVendorUsersCount(aidasUserRepository.countAllVendorUsers());
            ad.setUserCount(ad.getCustomerUsersCount());
        }
        if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_ADMIN)){
            ad.setOrganisationCount(1l);
            ad.setCustomerCount(aidasCustomerRepository.countAidasCustomerCountForVendorAdmin(aidasUser.getAidasVendor().getId()));
            ad.setVendorCount(1l);
            ad.setProjectCount(aidasProjectRepository.countAidasProjectByVendor(aidasUser.getAidasVendor().getId()));
            ad.setObjectCount(aidasObjectRepository.countAidasObjectByVendor(aidasUser.getAidasVendor().getId()));
            ad.setUploadCount(aidasUploadRepository.countAidasUploadByAidasVendor(aidasUser.getAidasCustomer().getId()));
            ad.setApprovedUploadCount(aidasUploadRepository.countAidasUploadByAidasVendorStatusTrue(aidasUser.getAidasVendor().getId()));
            ad.setRejectedUploadCount(aidasUploadRepository.countAidasUploadByAidasVendorStatusFalse(aidasUser.getAidasVendor().getId()));
            ad.setPendingUploadCount(aidasUploadRepository.countAidasUploadByAidasVendorStatusIsNull(aidasUser.getAidasVendor().getId()));
            ad.setVendorUsersCount(aidasUserRepository.countAllByAidasVendor(aidasUser.getAidasVendor()));
            ad.setUserCount(aidasUserRepository.countAllByAidasVendor(aidasUser.getAidasVendor()));
        }if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.VENDOR_USER)){
            ad.setOrganisationCount(1l);
            ad.setCustomerCount(1l);
            ad.setVendorCount(1l);
            ad.setProjectCount(aidasProjectRepository.countAidasProjectByVendorUser(aidasUser.getId()));
            ad.setObjectCount(aidasObjectRepository.countAidasProjectByVendorUser(aidasUser.getId()));
            ad.setUploadCount(aidasUploadRepository.countAidasUploadByAidasVendorUser(aidasUser.getAidasVendor().getId()));
            ad.setApprovedUploadCount(aidasUploadRepository.countAidasUploadByAidasVendorUserStatusTrue(aidasUser.getAidasVendor().getId()));
            ad.setRejectedUploadCount(aidasUploadRepository.countAidasUploadByAidasVendorUserStatusFalse(aidasUser.getAidasVendor().getId()));
            ad.setPendingUploadCount(aidasUploadRepository.countAidasUploadByAidasVendorUserStatusIsNull(aidasUser.getAidasVendor().getId()));
             ad.setUserCount(1l);
        }if(aidasAuthority.getName().equals(AidasAuthoritiesConstants.USER)){
            ad.setOrganisationCount(1l);
            ad.setCustomerCount(1l);
            ad.setVendorCount(1l);
            ad.setProjectCount(1l);
            ad.setObjectCount(1l);
            ad.setUploadCount(1l);
            ad.setApprovedUploadCount(1l);
            ad.setRejectedUploadCount(1l);
            ad.setPendingUploadCount(1l);
            ad.setUserCount(1l);
        }
        return ResponseEntity.ok().body(ad);
    }
}
