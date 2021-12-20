package com.ainnotate.aidas.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ainnotate.aidas.IntegrationTest;
import com.ainnotate.aidas.domain.AidasObject;
import com.ainnotate.aidas.domain.AidasObjectProperty;
import com.ainnotate.aidas.domain.AidasProperties;
import com.ainnotate.aidas.repository.AidasObjectPropertyRepository;
import com.ainnotate.aidas.repository.search.AidasObjectPropertySearchRepository;
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
 * Integration tests for the {@link AidasObjectPropertyResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AidasObjectPropertyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/aidas-object-properties";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/aidas-object-properties";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AidasObjectPropertyRepository aidasObjectPropertyRepository;

    /**
     * This repository is mocked in the com.ainnotate.aidas.repository.search test package.
     *
     * @see com.ainnotate.aidas.repository.search.AidasObjectPropertySearchRepositoryMockConfiguration
     */
    @Autowired
    private AidasObjectPropertySearchRepository mockAidasObjectPropertySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAidasObjectPropertyMockMvc;

    private AidasObjectProperty aidasObjectProperty;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasObjectProperty createEntity(EntityManager em) {
        AidasObjectProperty aidasObjectProperty = new AidasObjectProperty()
            .value(DEFAULT_VALUE);
        // Add required entity
        AidasObject aidasObject;
        if (TestUtil.findAll(em, AidasObject.class).isEmpty()) {
            aidasObject = AidasObjectResourceIT.createEntity(em);
            em.persist(aidasObject);
            em.flush();
        } else {
            aidasObject = TestUtil.findAll(em, AidasObject.class).get(0);
        }
        aidasObjectProperty.setAidasObject(aidasObject);
        // Add required entity
        AidasProperties aidasProperties;
        if (TestUtil.findAll(em, AidasProperties.class).isEmpty()) {
            aidasProperties = AidasPropertiesResourceIT.createEntity(em);
            em.persist(aidasProperties);
            em.flush();
        } else {
            aidasProperties = TestUtil.findAll(em, AidasProperties.class).get(0);
        }
        aidasObjectProperty.setAidasProperties(aidasProperties);
        return aidasObjectProperty;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasObjectProperty createUpdatedEntity(EntityManager em) {
        AidasObjectProperty aidasObjectProperty = new AidasObjectProperty()
            .value(UPDATED_VALUE);
        // Add required entity
        AidasObject aidasObject;
        if (TestUtil.findAll(em, AidasObject.class).isEmpty()) {
            aidasObject = AidasObjectResourceIT.createUpdatedEntity(em);
            em.persist(aidasObject);
            em.flush();
        } else {
            aidasObject = TestUtil.findAll(em, AidasObject.class).get(0);
        }
        aidasObjectProperty.setAidasObject(aidasObject);
        // Add required entity
        AidasProperties aidasProperties;
        if (TestUtil.findAll(em, AidasProperties.class).isEmpty()) {
            aidasProperties = AidasPropertiesResourceIT.createUpdatedEntity(em);
            em.persist(aidasProperties);
            em.flush();
        } else {
            aidasProperties = TestUtil.findAll(em, AidasProperties.class).get(0);
        }
        aidasObjectProperty.setAidasProperties(aidasProperties);
        return aidasObjectProperty;
    }

    @BeforeEach
    public void initTest() {
        aidasObjectProperty = createEntity(em);
    }

    @Test
    @Transactional
    void createAidasObjectProperty() throws Exception {
        int databaseSizeBeforeCreate = aidasObjectPropertyRepository.findAll().size();
        // Create the AidasObjectProperty
        restAidasObjectPropertyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasObjectProperty))
            )
            .andExpect(status().isCreated());

        // Validate the AidasObjectProperty in the database
        List<AidasObjectProperty> aidasObjectPropertyList = aidasObjectPropertyRepository.findAll();
        assertThat(aidasObjectPropertyList).hasSize(databaseSizeBeforeCreate + 1);
        AidasObjectProperty testAidasObjectProperty = aidasObjectPropertyList.get(aidasObjectPropertyList.size() - 1);
        assertThat(testAidasObjectProperty.getValue()).isEqualTo(DEFAULT_VALUE);

        // Validate the AidasObjectProperty in Elasticsearch
        verify(mockAidasObjectPropertySearchRepository, times(1)).save(testAidasObjectProperty);
    }

    @Test
    @Transactional
    void createAidasObjectPropertyWithExistingId() throws Exception {
        // Create the AidasObjectProperty with an existing ID
        aidasObjectProperty.setId(1L);

        int databaseSizeBeforeCreate = aidasObjectPropertyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAidasObjectPropertyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasObjectProperty))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasObjectProperty in the database
        List<AidasObjectProperty> aidasObjectPropertyList = aidasObjectPropertyRepository.findAll();
        assertThat(aidasObjectPropertyList).hasSize(databaseSizeBeforeCreate);

        // Validate the AidasObjectProperty in Elasticsearch
        verify(mockAidasObjectPropertySearchRepository, times(0)).save(aidasObjectProperty);
    }

    @Test
    @Transactional
    void getAllAidasObjectProperties() throws Exception {
        // Initialize the database
        aidasObjectPropertyRepository.saveAndFlush(aidasObjectProperty);

        // Get all the aidasObjectPropertyList
        restAidasObjectPropertyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasObjectProperty.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }

    @Test
    @Transactional
    void getAidasObjectProperty() throws Exception {
        // Initialize the database
        aidasObjectPropertyRepository.saveAndFlush(aidasObjectProperty);

        // Get the aidasObjectProperty
        restAidasObjectPropertyMockMvc
            .perform(get(ENTITY_API_URL_ID, aidasObjectProperty.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(aidasObjectProperty.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE));
    }

    @Test
    @Transactional
    void getNonExistingAidasObjectProperty() throws Exception {
        // Get the aidasObjectProperty
        restAidasObjectPropertyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAidasObjectProperty() throws Exception {
        // Initialize the database
        aidasObjectPropertyRepository.saveAndFlush(aidasObjectProperty);

        int databaseSizeBeforeUpdate = aidasObjectPropertyRepository.findAll().size();

        // Update the aidasObjectProperty
        AidasObjectProperty updatedAidasObjectProperty = aidasObjectPropertyRepository.findById(aidasObjectProperty.getId()).get();
        // Disconnect from session so that the updates on updatedAidasObjectProperty are not directly saved in db
        em.detach(updatedAidasObjectProperty);

        restAidasObjectPropertyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAidasObjectProperty.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAidasObjectProperty))
            )
            .andExpect(status().isOk());

        // Validate the AidasObjectProperty in the database
        List<AidasObjectProperty> aidasObjectPropertyList = aidasObjectPropertyRepository.findAll();
        assertThat(aidasObjectPropertyList).hasSize(databaseSizeBeforeUpdate);
        AidasObjectProperty testAidasObjectProperty = aidasObjectPropertyList.get(aidasObjectPropertyList.size() - 1);
        assertThat(testAidasObjectProperty.getValue()).isEqualTo(UPDATED_VALUE);

        // Validate the AidasObjectProperty in Elasticsearch
        verify(mockAidasObjectPropertySearchRepository).save(testAidasObjectProperty);
    }

    @Test
    @Transactional
    void putNonExistingAidasObjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = aidasObjectPropertyRepository.findAll().size();
        aidasObjectProperty.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasObjectPropertyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, aidasObjectProperty.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasObjectProperty))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasObjectProperty in the database
        List<AidasObjectProperty> aidasObjectPropertyList = aidasObjectPropertyRepository.findAll();
        assertThat(aidasObjectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObjectProperty in Elasticsearch
        verify(mockAidasObjectPropertySearchRepository, times(0)).save(aidasObjectProperty);
    }

    @Test
    @Transactional
    void putWithIdMismatchAidasObjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = aidasObjectPropertyRepository.findAll().size();
        aidasObjectProperty.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasObjectPropertyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasObjectProperty))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasObjectProperty in the database
        List<AidasObjectProperty> aidasObjectPropertyList = aidasObjectPropertyRepository.findAll();
        assertThat(aidasObjectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObjectProperty in Elasticsearch
        verify(mockAidasObjectPropertySearchRepository, times(0)).save(aidasObjectProperty);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAidasObjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = aidasObjectPropertyRepository.findAll().size();
        aidasObjectProperty.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasObjectPropertyMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasObjectProperty))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasObjectProperty in the database
        List<AidasObjectProperty> aidasObjectPropertyList = aidasObjectPropertyRepository.findAll();
        assertThat(aidasObjectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObjectProperty in Elasticsearch
        verify(mockAidasObjectPropertySearchRepository, times(0)).save(aidasObjectProperty);
    }

    @Test
    @Transactional
    void partialUpdateAidasObjectPropertyWithPatch() throws Exception {
        // Initialize the database
        aidasObjectPropertyRepository.saveAndFlush(aidasObjectProperty);

        int databaseSizeBeforeUpdate = aidasObjectPropertyRepository.findAll().size();

        // Update the aidasObjectProperty using partial update
        AidasObjectProperty partialUpdatedAidasObjectProperty = new AidasObjectProperty();
        partialUpdatedAidasObjectProperty.setId(aidasObjectProperty.getId());

        partialUpdatedAidasObjectProperty.value(UPDATED_VALUE);

        restAidasObjectPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasObjectProperty.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasObjectProperty))
            )
            .andExpect(status().isOk());

        // Validate the AidasObjectProperty in the database
        List<AidasObjectProperty> aidasObjectPropertyList = aidasObjectPropertyRepository.findAll();
        assertThat(aidasObjectPropertyList).hasSize(databaseSizeBeforeUpdate);
        AidasObjectProperty testAidasObjectProperty = aidasObjectPropertyList.get(aidasObjectPropertyList.size() - 1);
        assertThat(testAidasObjectProperty.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    void fullUpdateAidasObjectPropertyWithPatch() throws Exception {
        // Initialize the database
        aidasObjectPropertyRepository.saveAndFlush(aidasObjectProperty);

        int databaseSizeBeforeUpdate = aidasObjectPropertyRepository.findAll().size();

        // Update the aidasObjectProperty using partial update
        AidasObjectProperty partialUpdatedAidasObjectProperty = new AidasObjectProperty();
        partialUpdatedAidasObjectProperty.setId(aidasObjectProperty.getId());


        restAidasObjectPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasObjectProperty.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasObjectProperty))
            )
            .andExpect(status().isOk());

        // Validate the AidasObjectProperty in the database
        List<AidasObjectProperty> aidasObjectPropertyList = aidasObjectPropertyRepository.findAll();
        assertThat(aidasObjectPropertyList).hasSize(databaseSizeBeforeUpdate);
        AidasObjectProperty testAidasObjectProperty = aidasObjectPropertyList.get(aidasObjectPropertyList.size() - 1);

        assertThat(testAidasObjectProperty.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    void patchNonExistingAidasObjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = aidasObjectPropertyRepository.findAll().size();
        aidasObjectProperty.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasObjectPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, aidasObjectProperty.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasObjectProperty))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasObjectProperty in the database
        List<AidasObjectProperty> aidasObjectPropertyList = aidasObjectPropertyRepository.findAll();
        assertThat(aidasObjectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObjectProperty in Elasticsearch
        verify(mockAidasObjectPropertySearchRepository, times(0)).save(aidasObjectProperty);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAidasObjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = aidasObjectPropertyRepository.findAll().size();
        aidasObjectProperty.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasObjectPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasObjectProperty))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasObjectProperty in the database
        List<AidasObjectProperty> aidasObjectPropertyList = aidasObjectPropertyRepository.findAll();
        assertThat(aidasObjectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObjectProperty in Elasticsearch
        verify(mockAidasObjectPropertySearchRepository, times(0)).save(aidasObjectProperty);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAidasObjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = aidasObjectPropertyRepository.findAll().size();
        aidasObjectProperty.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasObjectPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasObjectProperty))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasObjectProperty in the database
        List<AidasObjectProperty> aidasObjectPropertyList = aidasObjectPropertyRepository.findAll();
        assertThat(aidasObjectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObjectProperty in Elasticsearch
        verify(mockAidasObjectPropertySearchRepository, times(0)).save(aidasObjectProperty);
    }

    @Test
    @Transactional
    void deleteAidasObjectProperty() throws Exception {
        // Initialize the database
        aidasObjectPropertyRepository.saveAndFlush(aidasObjectProperty);

        int databaseSizeBeforeDelete = aidasObjectPropertyRepository.findAll().size();

        // Delete the aidasObjectProperty
        restAidasObjectPropertyMockMvc
            .perform(delete(ENTITY_API_URL_ID, aidasObjectProperty.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AidasObjectProperty> aidasObjectPropertyList = aidasObjectPropertyRepository.findAll();
        assertThat(aidasObjectPropertyList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AidasObjectProperty in Elasticsearch
        verify(mockAidasObjectPropertySearchRepository, times(1)).deleteById(aidasObjectProperty.getId());
    }

    @Test
    @Transactional
    void searchAidasObjectProperty() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        aidasObjectPropertyRepository.saveAndFlush(aidasObjectProperty);
        when(mockAidasObjectPropertySearchRepository.search("id:" + aidasObjectProperty.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(aidasObjectProperty), PageRequest.of(0, 1), 1));

        // Search the aidasObjectProperty
        restAidasObjectPropertyMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + aidasObjectProperty.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasObjectProperty.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }
}
