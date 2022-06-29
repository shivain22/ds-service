package com.ainnotate.aidas.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ainnotate.aidas.IntegrationTest;
import com.ainnotate.aidas.domain.Property;
import com.ainnotate.aidas.repository.PropertyRepository;
import com.ainnotate.aidas.repository.search.PropertySearchRepository;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
 * Integration tests for the {@link PropertyResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PropertyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final Integer DEFAULT_SYSTEM_PROPERTY = 1;
    private static final Integer UPDATED_SYSTEM_PROPERTY = 0;

    private static final Integer DEFAULT_OPTIONAL = 0;
    private static final Integer UPDATED_OPTIONAL = 1;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/aidas-property";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/aidas-property";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PropertyRepository propertyRepository;

    /**
     * This repository is mocked in the com.ainnotate.aidas.repository.search test package.
     *
     * @see com.ainnotate.aidas.repository.search.PropertySearchRepositoryMockConfiguration
     */
    @Autowired
    private PropertySearchRepository mockPropertySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAidasPropertiesMockMvc;

    private Property property;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Property createEntity(EntityManager em) {
        Property property = new Property()
            .name(DEFAULT_NAME)
            .value(DEFAULT_VALUE)
            .optional(DEFAULT_OPTIONAL)
            .description(DEFAULT_DESCRIPTION);
        return property;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Property createUpdatedEntity(EntityManager em) {
        Property property = new Property()
            .name(UPDATED_NAME)
            .value(UPDATED_VALUE)
            .optional(UPDATED_OPTIONAL)
            .description(UPDATED_DESCRIPTION);
        return property;
    }

    @BeforeEach
    public void initTest() {
        property = createEntity(em);
    }

    @Test
    @Transactional
    void createAidasProperties() throws Exception {
        int databaseSizeBeforeCreate = propertyRepository.findAll().size();
        // Create the AidasProperties
        restAidasPropertiesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(property))
            )
            .andExpect(status().isCreated());

        // Validate the AidasProperties in the database
        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeCreate + 1);
        Property testProperty = propertyList.get(propertyList.size() - 1);
        assertThat(testProperty.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProperty.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testProperty.getOptional()).isEqualTo(DEFAULT_OPTIONAL);
        assertThat(testProperty.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the AidasProperties in Elasticsearch
        verify(mockPropertySearchRepository, times(1)).save(testProperty);
    }

    @Test
    @Transactional
    void createAidasPropertiesWithExistingId() throws Exception {
        // Create the AidasProperties with an existing ID
        property.setId(1L);

        int databaseSizeBeforeCreate = propertyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAidasPropertiesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(property))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProperties in the database
        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeCreate);

        // Validate the AidasProperties in Elasticsearch
        verify(mockPropertySearchRepository, times(0)).save(property);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = propertyRepository.findAll().size();
        // set the field null
        property.setName(null);

        // Create the AidasProperties, which fails.

        restAidasPropertiesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(property))
            )
            .andExpect(status().isBadRequest());

        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = propertyRepository.findAll().size();
        // set the field null
        property.setValue(null);

        // Create the AidasProperties, which fails.

        restAidasPropertiesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(property))
            )
            .andExpect(status().isBadRequest());

        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSystemPropertyIsRequired() throws Exception {
        int databaseSizeBeforeTest = propertyRepository.findAll().size();
        // set the field null

        // Create the AidasProperties, which fails.

        restAidasPropertiesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(property))
            )
            .andExpect(status().isBadRequest());

        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOptionalIsRequired() throws Exception {
        int databaseSizeBeforeTest = propertyRepository.findAll().size();
        // set the field null
        property.setOptional(null);

        // Create the AidasProperties, which fails.

        restAidasPropertiesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(property))
            )
            .andExpect(status().isBadRequest());

        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAidasProperties() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        // Get all the aidasPropertiesList
        restAidasPropertiesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(property.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].systemProperty").value(hasItem(DEFAULT_SYSTEM_PROPERTY.intValue())))
            .andExpect(jsonPath("$.[*].optional").value(hasItem(DEFAULT_OPTIONAL.intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getAidasProperties() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        // Get the aidasProperties
        restAidasPropertiesMockMvc
            .perform(get(ENTITY_API_URL_ID, property.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(property.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE))
            .andExpect(jsonPath("$.systemProperty").value(DEFAULT_SYSTEM_PROPERTY.intValue()))
            .andExpect(jsonPath("$.optional").value(DEFAULT_OPTIONAL.intValue()))
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
        propertyRepository.saveAndFlush(property);

        int databaseSizeBeforeUpdate = propertyRepository.findAll().size();

        // Update the aidasProperties
        Property updatedProperty = propertyRepository.findById(property.getId()).get();
        // Disconnect from session so that the updates on updatedAidasProperties are not directly saved in db
        em.detach(updatedProperty);
        updatedProperty
            .name(UPDATED_NAME)
            .value(UPDATED_VALUE)
            .optional(UPDATED_OPTIONAL)
            .description(UPDATED_DESCRIPTION);

        restAidasPropertiesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProperty.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedProperty))
            )
            .andExpect(status().isOk());

        // Validate the AidasProperties in the database
        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeUpdate);
        Property testProperty = propertyList.get(propertyList.size() - 1);
        assertThat(testProperty.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProperty.getValue()).isEqualTo(UPDATED_VALUE);

        assertThat(testProperty.getOptional()).isEqualTo(UPDATED_OPTIONAL);
        assertThat(testProperty.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the AidasProperties in Elasticsearch
        verify(mockPropertySearchRepository).save(testProperty);
    }

    @Test
    @Transactional
    void putNonExistingAidasProperties() throws Exception {
        int databaseSizeBeforeUpdate = propertyRepository.findAll().size();
        property.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasPropertiesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, property.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(property))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProperties in the database
        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProperties in Elasticsearch
        verify(mockPropertySearchRepository, times(0)).save(property);
    }

    @Test
    @Transactional
    void putWithIdMismatchAidasProperties() throws Exception {
        int databaseSizeBeforeUpdate = propertyRepository.findAll().size();
        property.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasPropertiesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(property))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProperties in the database
        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProperties in Elasticsearch
        verify(mockPropertySearchRepository, times(0)).save(property);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAidasProperties() throws Exception {
        int databaseSizeBeforeUpdate = propertyRepository.findAll().size();
        property.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasPropertiesMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(property))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasProperties in the database
        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProperties in Elasticsearch
        verify(mockPropertySearchRepository, times(0)).save(property);
    }

    @Test
    @Transactional
    void partialUpdateAidasPropertiesWithPatch() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        int databaseSizeBeforeUpdate = propertyRepository.findAll().size();

        // Update the aidasProperties using partial update
        Property partialUpdatedProperty = new Property();
        partialUpdatedProperty.setId(property.getId());

        partialUpdatedProperty.name(UPDATED_NAME).optional(UPDATED_OPTIONAL).description(UPDATED_DESCRIPTION);

        restAidasPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProperty.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProperty))
            )
            .andExpect(status().isOk());

        // Validate the AidasProperties in the database
        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeUpdate);
        Property testProperty = propertyList.get(propertyList.size() - 1);
        assertThat(testProperty.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProperty.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testProperty.getOptional()).isEqualTo(UPDATED_OPTIONAL);
        assertThat(testProperty.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateAidasPropertiesWithPatch() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        int databaseSizeBeforeUpdate = propertyRepository.findAll().size();

        // Update the aidasProperties using partial update
        Property partialUpdatedProperty = new Property();
        partialUpdatedProperty.setId(property.getId());

        partialUpdatedProperty
            .name(UPDATED_NAME)
            .value(UPDATED_VALUE)

            .optional(UPDATED_OPTIONAL)
            .description(UPDATED_DESCRIPTION);

        restAidasPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProperty.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProperty))
            )
            .andExpect(status().isOk());

        // Validate the AidasProperties in the database
        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeUpdate);
        Property testProperty = propertyList.get(propertyList.size() - 1);
        assertThat(testProperty.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProperty.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testProperty.getOptional()).isEqualTo(UPDATED_OPTIONAL);
        assertThat(testProperty.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingAidasProperties() throws Exception {
        int databaseSizeBeforeUpdate = propertyRepository.findAll().size();
        property.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, property.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(property))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProperties in the database
        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProperties in Elasticsearch
        verify(mockPropertySearchRepository, times(0)).save(property);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAidasProperties() throws Exception {
        int databaseSizeBeforeUpdate = propertyRepository.findAll().size();
        property.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(property))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProperties in the database
        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProperties in Elasticsearch
        verify(mockPropertySearchRepository, times(0)).save(property);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAidasProperties() throws Exception {
        int databaseSizeBeforeUpdate = propertyRepository.findAll().size();
        property.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(property))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasProperties in the database
        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProperties in Elasticsearch
        verify(mockPropertySearchRepository, times(0)).save(property);
    }

    @Test
    @Transactional
    void deleteAidasProperties() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        int databaseSizeBeforeDelete = propertyRepository.findAll().size();

        // Delete the aidasProperties
        restAidasPropertiesMockMvc
            .perform(delete(ENTITY_API_URL_ID, property.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AidasProperties in Elasticsearch
        verify(mockPropertySearchRepository, times(1)).deleteById(property.getId());
    }

    @Test
    @Transactional
    void searchAidasProperties() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        propertyRepository.saveAndFlush(property);
        when(mockPropertySearchRepository.search("id:" + property.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(property), PageRequest.of(0, 1), 1));

        // Search the aidasProperties
        restAidasPropertiesMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + property.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(property.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].systemProperty").value(hasItem(DEFAULT_SYSTEM_PROPERTY.intValue())))
            .andExpect(jsonPath("$.[*].optional").value(hasItem(DEFAULT_OPTIONAL.intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}
