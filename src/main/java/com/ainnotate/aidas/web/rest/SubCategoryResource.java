package com.ainnotate.aidas.web.rest;

import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.Category;
import com.ainnotate.aidas.domain.SubCategory;
import com.ainnotate.aidas.repository.CategoryRepository;
import com.ainnotate.aidas.repository.SubCategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.web.util.ResponseUtil;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link Authority}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SubCategoryResource {

    private final Logger log = LoggerFactory.getLogger(SubCategoryResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceCategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Autowired
    SubCategoryRepository subCategoryRepository;


    /**
     * {@code GET  /aidas-authorities} : get all the aidasAuthoritys.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasAuthoritys in body.
     */
    @GetMapping("/aidas-subcategory")
    public ResponseEntity<List<SubCategory>> getAllSubCategories() {
        log.debug("REST request to get a list of AidasAuthorities");
        List<SubCategory> categories = subCategoryRepository.findAll();
        return ResponseEntity.ok().body(categories);
    }

    /**
     * {@code GET  /aidas-authorities} : get all the aidasAuthoritys.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasAuthoritys in body.
     */
    @GetMapping("/aidas-subcategory/dropdown")
    public ResponseEntity<List<SubCategory>> getAllSubCategoriesDropdown() {
        log.debug("REST request to get a list of AidasAuthorities");
        List<SubCategory> categories = subCategoryRepository.findAll();
        return ResponseEntity.ok().body(categories);
    }

    /**
     * {@code GET  /aidas-authorities} : get all the aidasAuthoritys.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasAuthoritys in body.
     */
    @GetMapping("/aidas-subcategory/{categoryId}/dropdown")
    public ResponseEntity<List<SubCategory>> getAllSubCategoriesDropdown(@PathVariable(value = "categoryId", required = false) final Long categoryId) {
        log.debug("REST request to get a list of AidasAuthorities");
        List<SubCategory> categories = subCategoryRepository.findAll();
        return ResponseEntity.ok().body(categories);
    }



    /**
     * {@code GET  /aidas-authorities/:id} : get the "id" authority.
     *
     * @param id the id of the authority to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the authority, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-subcategory/{id}")
    public ResponseEntity<SubCategory> getSubCategory(@PathVariable Long id) {
        log.debug("REST request to get AidasAuthority : {}", id);
        Optional<SubCategory> category = subCategoryRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(category);
    }


}
