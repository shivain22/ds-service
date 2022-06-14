package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.User;
import com.ainnotate.aidas.repository.AuthorityRepository;
import com.ainnotate.aidas.repository.UserRepository;
import com.ainnotate.aidas.repository.search.AidasAuthoritySearchRepository;
import com.ainnotate.aidas.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link Authority}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AuthorityResource {

    private final Logger log = LoggerFactory.getLogger(AuthorityResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasAuthority";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Autowired
    private UserRepository userRepository;

    private final AuthorityRepository authorityRepository;

    private final AidasAuthoritySearchRepository aidasAuthoritySearchRepository;

    public AuthorityResource(
        AuthorityRepository authorityRepository,
        AidasAuthoritySearchRepository aidasAuthoritySearchRepository
    ) {
        this.authorityRepository = authorityRepository;
        this.aidasAuthoritySearchRepository = aidasAuthoritySearchRepository;
    }
    /**
     * {@code GET  /aidas-authorities} : get all the aidasAuthoritys.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasAuthoritys in body.
     */
    @Operation(summary = "Retrieve all AidasAuthorities (getAllAidasAuthorities())")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Retrieved list of AidasAuthories",content = { @Content(mediaType = "application/json",schema = @Schema(implementation = Authority.class)) }),
        @ApiResponse(responseCode = "500", description = "Unable to retrieve the list of AidasAuthorities.  Contact the administrator",content = @Content) })
    @GetMapping("/aidas-authorities")
    public ResponseEntity<List<Authority>> getAllAidasAuthorities() {
        log.debug("REST request to get a list of AidasAuthorities");
        List<Authority> aidasAuthorities = authorityRepository.findAll();
        return ResponseEntity.ok().body(aidasAuthorities);
    }

    @Operation(summary = "Retrieve all AidasAuthorities (getMyAidasAuthorities())")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Retrieved list of AidasAuthories of logged in user",content = { @Content(mediaType = "application/json",schema = @Schema(implementation = Authority.class)) }),
        @ApiResponse(responseCode = "500", description = "Unable to retrieve the list of AidasAuthorities of logged in user.  Contact the administrator",content = @Content) })
    @GetMapping("/my-authorities")
    public ResponseEntity<List<Authority>> getMyAidasAuthorityies() {
        User user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        log.debug("REST request to get a list of AidasAuthorities of logged in user"+ user.getId());
        List<Authority> myAuthorities   = user.getAuthorities().stream().collect(Collectors.toList());
        return ResponseEntity.ok().body(myAuthorities);
    }

    /**
     * {@code GET  /aidas-authorities/:id} : get the "id" authority.
     *
     * @param id the id of the authority to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the authority, or with status {@code 404 (Not Found)}.
     */
    @Operation(summary = "Retrieve all AidasAuthorities (getAuthority())")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Retrieved details of AidasAuthority",content = { @Content(mediaType = "application/json",schema = @Schema(implementation = Authority.class)) }),
        @ApiResponse(responseCode = "500", description = "Unable to details of AidasAuthority.  Contact the administrator",content = @Content) })
    @GetMapping("/aidas-authorities/{id}")
    public ResponseEntity<Authority> getAuthority(@PathVariable Long id) {
        log.debug("REST request to get AidasAuthority : {}", id);
        Optional<Authority> authority = authorityRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(authority);
    }

    /**
     * {@code SEARCH  /_search/aidas-authorities?query=:query} : search for the authority corresponding
     * to the query.
     *
     * @param query the query of the authority search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @Operation(summary = "Retrieve all AidasAuthorities based on search params (searchAidasAuthoritys())")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Retrieved list of AidasAuthories based on search",content = { @Content(mediaType = "application/json",schema = @Schema(implementation = Authority.class)) }),
        @ApiResponse(responseCode = "500", description = "Unable to retrieve the list of AidasAuthorities based on search.  Contact the administrator",content = @Content) })
    @GetMapping("/_search/aidas-authorities")
    public ResponseEntity<List<Authority>> searchAidasAuthoritys(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasAuthoritys for query {}", query);
        Page<Authority> page = aidasAuthoritySearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
