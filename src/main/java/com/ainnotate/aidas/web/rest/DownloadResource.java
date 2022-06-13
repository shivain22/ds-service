package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.Download;
import com.ainnotate.aidas.domain.Upload;
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
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link Upload}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class DownloadResource {

    private final Logger log = LoggerFactory.getLogger(DownloadResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasDownload";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;


    @Autowired
    private DownloadRepository downloadRepository;

    /**
     * {@code GET  /aidas-downloads} : get all the aidasUploads.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasDownloads in body.
     */
    @GetMapping("/aidas-downloads")
    public ResponseEntity<List<Download>> getAllAidasDownloads(Pageable pageable) {
        log.debug("REST request to get a page of AidasDownloads");
        Page<Download> page = downloadRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-downloads/:id} : get the "id" aidasDownload.
     *
     * @param id the id of the aidasDownload to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasDownload, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-downloads/{id}")
    public ResponseEntity<Download> getAidasDownload(@PathVariable Long id) {
        log.debug("REST request to get AidasUpload : {}", id);
        Optional<Download> upload = downloadRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(upload);
    }

}
