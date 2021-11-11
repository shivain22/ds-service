package com.ainnotate.aidas.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ainnotate.aidas.IntegrationTest;
import com.ainnotate.aidas.domain.AidasProperties;
import com.ainnotate.aidas.repository.AidasPropertiesRepository;
import com.ainnotate.aidas.repository.search.AidasPropertiesSearchRepository;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AidasPropertiesResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AidasPropertiesResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_SYSTEM_PROPERTY = false;
    private static final Boolean UPDATED_SYSTEM_PROPERTY = true;

    private static final Boolean DEFAULT_OPTIONAL = false;
    private static final Boolean UPDATED_OPTIONAL = true;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/aidas-properties";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/aidas-properties";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AidasPropertiesRepository aidasPropertiesRepository;

    /**
     * This repository is mocked in the com.ainnotate.aidas.repository.search test package.
     *
     * @see com.ainnotate.aidas.repository.search.AidasPropertiesSearchRepositoryMockConfiguration
     */
    @Autowired
    private AidasPropertiesSearchRepository mockAidasPropertiesSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAidasPropertiesMockMvc;

    private AidasProperties aidasProperties;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasProperties createEntity(EntityManager em) {
        AidasProperties aidasProperties = new AidasProperties()
            .name(DEFAULT_NAME)
            .value(DEFAULT_VALUE)
            .systemProperty(DEFAULT_SYSTEM_PROPERTY)
            .optional(DEFAULT_OPTIONAL)
            .description(DEFAULT_DESCRIPTION);
        return aidasProperties;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasProperties createUpdatedEntity(EntityManager em) {
        AidasProperties aidasProperties = new AidasProperties()
            .name(UPDATED_NAME)
            .value(UPDATED_VALUE)
            .systemProperty(UPDATED_SYSTEM_PROPERTY)
            .optional(UPDATED_OPTIONAL)
            .description(UPDATED_DESCRIPTION);
        return aidasProperties;
    }

    @BeforeEach
    public void initTest() {
        aidasProperties = createEntity(em);
    }

    @Test
    @Transactional
    void createAidasProperties() throws Exception {
        int databaseSizeBeforeCreate = aidasPropertiesRepository.findAll().size();
        // Create the AidasProperties
        restAidasPropertiesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasProperties))
            )
            .andExpect(status().isCreated());

        // Validate the AidasProperties in the database
        List<AidasProperties> aidasPropertiesList = aidasPropertiesRepository.findAll();
        assertThat(aidasPropertiesList).hasSize(databaseSizeBeforeCreate + 1);
        AidasProperties testAidasProperties = aidasPropertiesList.get(aidasPropertiesList.size() - 1);
        assertThat(testAidasProperties.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAidasProperties.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testAidasProperties.getSystemProperty()).isEqualTo(DEFAULT_SYSTEM_PROPERTY);
        assertThat(testAidasProperties.getOptional()).isEqualTo(DEFAULT_OPTIONAL);
        assertThat(testAidasProperties.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the AidasProperties in Elasticsearch
        verify(mockAidasPropertiesSearchRepository, times(1)).save(testAidasProperties);
    }

    @Test
    @Transactional
    void createAidasPropertiesWithExistingId() throws Exception {
        // Create the AidasProperties with an existing ID
        aidasProperties.setId(1L);

        int databaseSizeBeforeCreate = aidasPropertiesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAidasPropertiesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasProperties))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProperties in the database
        List<AidasProperties> aidasPropertiesList = aidasPropertiesRepository.findAll();
        assertThat(aidasPropertiesList).hasSize(databaseSizeBeforeCreate);

        // Validate the AidasProperties in Elasticsearch
        verify(mockAidasPropertiesSearchRepository, times(0)).save(aidasProperties);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = aidasPropertiesRepository.findAll().size();
        // set the field null
        aidasProperties.setName(null);

        // Create the AidasProperties, which fails.

        restAidasPropertiesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasProperties))
            )
            .andExpect(status().isBadRequest());

        List<AidasProperties> aidasPropertiesList = aidasPropertiesRepository.findAll();
        assertThat(aidasPropertiesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = aidasPropertiesRepository.findAll().size();
        // set the field null
        aidasProperties.setValue(null);

        // Create the AidasProperties, which fails.

        restAidasPropertiesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasProperties))
            )
            .andExpect(status().isBadRequest());

        List<AidasProperties> aidasPropertiesList = aidasPropertiesRepository.findAll();
        assertThat(aidasPropertiesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSystemPropertyIsRequired() throws Exception {
        int databaseSizeBeforeTest = aidasPropertiesRepository.findAll().size();
        // set the field null
        aidasProperties.setSystemProperty(null);

        // Create the AidasProperties, which fails.

        restAidasPropertiesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasProperties))
            )
            .andExpect(status().isBadRequest());

        List<AidasProperties> aidasPropertiesList = aidasPropertiesRepository.findAll();
        assertThat(aidasPropertiesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOptionalIsRequired() throws Exception {
        int databaseSizeBeforeTest = aidasPropertiesRepository.findAll().size();
        // set the field null
        aidasProperties.setOptional(null);

        // Create the AidasProperties, which fails.

        restAidasPropertiesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasProperties))
            )
            .andExpect(status().isBadRequest());

        List<AidasProperties> aidasPropertiesList = aidasPropertiesRepository.findAll();
        assertThat(aidasPropertiesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAidasProperties() throws Exception {
        // Initialize the database
        aidasPropertiesRepository.saveAndFlush(aidasProperties);

        // Get all the aidasPropertiesList
        restAidasPropertiesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasProperties.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].systemProperty").value(hasItem(DEFAULT_SYSTEM_PROPERTY.booleanValue())))
            .andExpect(jsonPath("$.[*].optional").value(hasItem(DEFAULT_OPTIONAL.booleanValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getAidasProperties() throws Exception {
        // Initialize the database
        aidasPropertiesRepository.saveAndFlush(aidasProperties);

        // Get the aidasProperties
        restAidasPropertiesMockMvc
            .perform(get(ENTITY_API_URL_ID, aidasProperties.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(aidasProperties.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE))
            .andExpect(jsonPath("$.systemProperty").value(DEFAULT_SYSTEM_PROPERTY.booleanValue()))
            .andExpect(jsonPath("$.optional").value(DEFAULT_OPTIONAL.booleanValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingAidasProperties() throws Exception {
        // Get the aidasProperties
        restAidasPropertiesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAidasProperties() throws Exception {
        // Initialize the database
        aidasPropertiesRepository.saveAndFlush(aidasProperties);

        int databaseSizeBeforeUpdate = aidasPropertiesRepository.findAll().size();

        // Update the aidasProperties
        AidasProperties updatedAidasProperties = aidasPropertiesRepository.findById(aidasProperties.getId()).get();
        // Disconnect from session so that the updates on updatedAidasProperties are not directly saved in db
        em.detach(updatedAidasProperties);
        updatedAidasProperties
            .name(UPDATED_NAME)
            .value(UPDATED_VALUE)
            .systemProperty(UPDATED_SYSTEM_PROPERTY)
            .optional(UPDATED_OPTIONAL)
            .description(UPDATED_DESCRIPTION);

        restAidasPropertiesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAidasProperties.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAidasProperties))
            )
            .andExpect(status().isOk());

        // Validate the AidasProperties in the database
        List<AidasProperties> aidasPropertiesList = aidasPropertiesRepository.findAll();
        assertThat(aidasPropertiesList).hasSize(databaseSizeBeforeUpdate);
        AidasProperties testAidasProperties = aidasPropertiesList.get(aidasPropertiesList.size() - 1);
        assertThat(testAidasProperties.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAidasProperties.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testAidasProperties.getSystemProperty()).isEqualTo(UPDATED_SYSTEM_PROPERTY);
        assertThat(testAidasProperties.getOptional()).isEqualTo(UPDATED_OPTIONAL);
        assertThat(testAidasProperties.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the AidasProperties in Elasticsearch
        verify(mockAidasPropertiesSearchRepository).save(testAidasProperties);
    }

    @Test
    @Transactional
    void putNonExistingAidasProperties() throws Exception {
        int databaseSizeBeforeUpdate = aidasPropertiesRepository.findAll().size();
        aidasProperties.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasPropertiesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, aidasProperties.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasProperties))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProperties in the database
        List<AidasProperties> aidasPropertiesList = aidasPropertiesRepository.findAll();
        assertThat(aidasPropertiesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProperties in Elasticsearch
        verify(mockAidasPropertiesSearchRepository, times(0)).save(aidasProperties);
    }

    @Test
    @Transactional
    void putWithIdMismatchAidasProperties() throws Exception {
        int databaseSizeBeforeUpdate = aidasPropertiesRepository.findAll().size();
        aidasProperties.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasPropertiesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasProperties))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProperties in the database
        List<AidasProperties> aidasPropertiesList = aidasPropertiesRepository.findAll();
        assertThat(aidasPropertiesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProperties in Elasticsearch
        verify(mockAidasPropertiesSearchRepository, times(0)).save(aidasProperties);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAidasProperties() throws Exception {
        int databaseSizeBeforeUpdate = aidasPropertiesRepository.findAll().size();
        aidasProperties.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasPropertiesMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasProperties))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasProperties in the database
        List<AidasProperties> aidasPropertiesList = aidasPropertiesRepository.findAll();
        assertThat(aidasPropertiesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProperties in Elasticsearch
        verify(mockAidasPropertiesSearchRepository, times(0)).save(aidasProperties);
    }

    @Test
    @Transactional
    void partialUpdateAidasPropertiesWithPatch() throws Exception {
        // Initialize the database
        aidasPropertiesRepository.saveAndFlush(aidasProperties);

        int databaseSizeBeforeUpdate = aidasPropertiesRepository.findAll().size();

        // Update the aidasProperties using partial update
        AidasProperties partialUpdatedAidasProperties = new AidasProperties();
        partialUpdatedAidasProperties.setId(aidasProperties.getId());

        partialUpdatedAidasProperties.name(UPDATED_NAME).optional(UPDATED_OPTIONAL).description(UPDATED_DESCRIPTION);

        restAidasPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasProperties.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasProperties))
            )
            .andExpect(status().isOk());

        // Validate the AidasProperties in the database
        List<AidasProperties> aidasPropertiesList = aidasPropertiesRepository.findAll();
        assertThat(aidasPropertiesList).hasSize(databaseSizeBeforeUpdate);
        AidasProperties testAidasProperties = aidasPropertiesList.get(aidasPropertiesList.size() - 1);
        assertThat(testAidasProperties.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAidasProperties.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testAidasProperties.getSystemProperty()).isEqualTo(DEFAULT_SYSTEM_PROPERTY);
        assertThat(testAidasProperties.getOptional()).isEqualTo(UPDATED_OPTIONAL);
        assertThat(testAidasProperties.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateAidasPropertiesWithPatch() throws Exception {
        // Initialize the database
        aidasPropertiesRepository.saveAndFlush(aidasProperties);

        int databaseSizeBeforeUpdate = aidasPropertiesRepository.findAll().size();

        // Update the aidasProperties using partial update
        AidasProperties partialUpdatedAidasProperties = new AidasProperties();
        partialUpdatedAidasProperties.setId(aidasProperties.getId());

        partialUpdatedAidasProperties
            .name(UPDATED_NAME)
            .value(UPDATED_VALUE)
            .systemProperty(UPDATED_SYSTEM_PROPERTY)
            .optional(UPDATED_OPTIONAL)
            .description(UPDATED_DESCRIPTION);

        restAidasPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasProperties.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasProperties))
            )
            .andExpect(status().isOk());

        // Validate the AidasProperties in the database
        List<AidasProperties> aidasPropertiesList = aidasPropertiesRepository.findAll();
        assertThat(aidasPropertiesList).hasSize(databaseSizeBeforeUpdate);
        AidasProperties testAidasProperties = aidasPropertiesList.get(aidasPropertiesList.size() - 1);
        assertThat(testAidasProperties.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAidasProperties.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testAidasProperties.getSystemProperty()).isEqualTo(UPDATED_SYSTEM_PROPERTY);
        assertThat(testAidasProperties.getOptional()).isEqualTo(UPDATED_OPTIONAL);
        assertThat(testAidasProperties.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingAidasProperties() throws Exception {
        int databaseSizeBeforeUpdate = aidasPropertiesRepository.findAll().size();
        aidasProperties.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, aidasProperties.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasProperties))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProperties in the database
        List<AidasProperties> aidasPropertiesList = aidasPropertiesRepository.findAll();
        assertThat(aidasPropertiesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProperties in Elasticsearch
        verify(mockAidasPropertiesSearchRepository, times(0)).save(aidasProperties);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAidasProperties() throws Exception {
        int databaseSizeBeforeUpdate = aidasPropertiesRepository.findAll().size();
        aidasProperties.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasProperties))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProperties in the database
        List<AidasProperties> aidasPropertiesList = aidasPropertiesRepository.findAll();
        assertThat(aidasPropertiesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProperties in Elasticsearch
        verify(mockAidasPropertiesSearchRepository, times(0)).save(aidasProperties);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAidasProperties() throws Exception {
        int databaseSizeBeforeUpdate = aidasPropertiesRepository.findAll().size();
        aidasProperties.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasProperties))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasProperties in the database
        List<AidasProperties> aidasPropertiesList = aidasPropertiesRepository.findAll();
        assertThat(aidasPropertiesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProperties in Elasticsearch
        verify(mockAidasPropertiesSearchRepository, times(0)).save(aidasProperties);
    }

    @Test
    @Transactional
    void deleteAidasProperties() throws Exception {
        // Initialize the database
        aidasPropertiesRepository.saveAndFlush(aidasProperties);

        int databaseSizeBeforeDelete = aidasPropertiesRepository.findAll().size();

        // Delete the aidasProperties
        restAidasPropertiesMockMvc
            .perform(delete(ENTITY_API_URL_ID, aidasProperties.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AidasProperties> aidasPropertiesList = aidasPropertiesRepository.findAll();
        assertThat(aidasPropertiesList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AidasProperties in Elasticsearch
        verify(mockAidasPropertiesSearchRepository, times(1)).deleteById(aidasProperties.getId());
    }

    @Test
    @Transactional
    void searchAidasProperties() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        aidasPropertiesRepository.saveAndFlush(aidasProperties);
        when(mockAidasPropertiesSearchRepository.search("id:" + aidasProperties.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(aidasProperties), PageRequest.of(0, 1), 1));

        // Search the aidasProperties
        restAidasPropertiesMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + aidasProperties.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasProperties.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].systemProperty").value(hasItem(DEFAULT_SYSTEM_PROPERTY.booleanValue())))
            .andExpect(jsonPath("$.[*].optional").value(hasItem(DEFAULT_OPTIONAL.booleanValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}
