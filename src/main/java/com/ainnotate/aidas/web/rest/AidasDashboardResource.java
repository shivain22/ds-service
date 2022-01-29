package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.AidasDownload;
import com.ainnotate.aidas.domain.AidasUpload;
import com.ainnotate.aidas.repository.*;
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
    private AidasProjectRepository aidasProjectRepository;

    @Autowired
    private AidasObjectRepository aidasObjectRepository;

    @Autowired
    private  AidasUserRepository aidasUserRepository;

    @Autowired
    private AidasUploadRepository aidasUploadRepository;

    /**
     * {@code GET  /aidas-downloads} : get all the aidasUploads.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasDownloads in body.
     */
    @GetMapping("/aidas-dashboard")
    public ResponseEntity<List<AidasDownload>> getAllAidasUploads(Pageable pageable) {
        log.debug("REST request to get a page of AidasDownloads");
        Page<AidasDownload> page = aidasDownloadRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }


}
