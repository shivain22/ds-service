package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.Category;
import com.ainnotate.aidas.domain.User;
import com.ainnotate.aidas.repository.AuthorityRepository;
import com.ainnotate.aidas.repository.CategoryRepository;
import com.ainnotate.aidas.repository.UserRepository;
import com.ainnotate.aidas.repository.search.AuthoritySearchRepository;
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
public class CategoryResource {

    private final Logger log = LoggerFactory.getLogger(CategoryResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceCategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Autowired
    CategoryRepository categoryRepository;


    /**
     * {@code GET  /aidas-authorities} : get all the aidasAuthoritys.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasAuthoritys in body.
     */
    @GetMapping("/aidas-category")
    public ResponseEntity<List<Category>> getAllAidasCategories() {
        log.debug("REST request to get a list of AidasAuthorities");
        List<Category> categories = categoryRepository.findAll();
        return ResponseEntity.ok().body(categories);
    }

    /**
     * {@code GET  /aidas-authorities} : get all the aidasAuthoritys.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasAuthoritys in body.
     */
    @GetMapping("/aidas-category/dropdown")
    public ResponseEntity<List<Category>> getAllAidasCategoriesDropdown() {
        log.debug("REST request to get a list of AidasAuthorities");
        List<Category> categories = categoryRepository.findAll();
        return ResponseEntity.ok().body(categories);
    }



    /**
     * {@code GET  /aidas-authorities/:id} : get the "id" authority.
     *
     * @param id the id of the authority to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the authority, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-category/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        log.debug("REST request to get AidasAuthority : {}", id);
        Optional<Category> category = categoryRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(category);
    }


}
