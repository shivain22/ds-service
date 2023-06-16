package com.ainnotate.aidas.web.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.Language;
import com.ainnotate.aidas.repository.LanguageRepository;

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
    
    @GetMapping("/languages")
    public ResponseEntity<List<Language>> getAllAidasAuthorities() {
        log.debug("REST request to get a list of languages");
        List<Language> languages = languageRepository.getLanguages();
        return ResponseEntity.ok().body(languages);
    }
}
