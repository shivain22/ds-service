package com.ainnotate.aidas.web.rest;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.Language;
import com.ainnotate.aidas.repository.LanguageRepository;
import com.ainnotate.aidas.repository.predicates.LanguagePredicatesBuilder;
import com.ainnotate.aidas.repository.predicates.UserPredicatesBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;

import tech.jhipster.web.util.PaginationUtil;

/**
 * REST controller for managing {@link Authority}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class LanguageResource {

    private final Logger log = LoggerFactory.getLogger(LanguageResource.class);
    private final LanguageRepository languageRepository;

    public LanguageResource(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }
    
	/*
	 * @GetMapping("/languages") public ResponseEntity<List<Language>>
	 * getAllAidasAuthorities() {
	 * log.debug("REST request to get a list of languages"); List<Language>
	 * languages = languageRepository.getLanguages(); return
	 * ResponseEntity.ok().body(languages); }
	 */
    
    @GetMapping(value = "/languages")
    @ResponseBody
    public ResponseEntity<List<Language>> search() {
    	List<Language> page = languageRepository.findAll();
        return ResponseEntity.ok().body(page);
}

}