package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.AidasDownload;
import com.ainnotate.aidas.domain.AidasUpload;
import com.ainnotate.aidas.domain.AidasUserAidasObjectMapping;
import com.ainnotate.aidas.dto.UploadDto;
import com.ainnotate.aidas.repository.*;
import com.ainnotate.aidas.repository.search.AidasUploadSearchRepository;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link AidasUpload}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AidasDownloadResource {

    private final Logger log = LoggerFactory.getLogger(AidasDownloadResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasDownload";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;


    @Autowired
    private AidasDownloadRepository aidasDownloadRepository;

    /**
     * {@code GET  /aidas-downloads} : get all the aidasUploads.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasDownloads in body.
     */
    @GetMapping("/aidas-downloads")
    public ResponseEntity<List<AidasDownload>> getAllAidasUploads(Pageable pageable) {
        log.debug("REST request to get a page of AidasDownloads");
        Page<AidasDownload> page = aidasDownloadRepository.findAll(pageable);
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
    public ResponseEntity<AidasDownload> getAidasDownload(@PathVariable Long id) {
        log.debug("REST request to get AidasUpload : {}", id);
        Optional<AidasDownload> aidasUpload = aidasDownloadRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(aidasUpload);
    }

    /**
     * {@code DELETE  /aidas-uploads/:id} : delete the "id" aidasUpload.
     *
     * @param id the id of the aidasUpload to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aidas-downloads/{id}")
    public ResponseEntity<Void> deleteAidasDownload(@PathVariable Long id) {
        log.debug("REST request to delete AidasDownload : {}", id);
        aidasDownloadRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }


}
