package com.ainnotate.aidas.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ainnotate.aidas.IntegrationTest;
import com.ainnotate.aidas.domain.AidasProject;
import com.ainnotate.aidas.domain.AidasProjectProperty;
import com.ainnotate.aidas.repository.AidasProjectPropertyRepository;
import com.ainnotate.aidas.repository.search.AidasProjectPropertySearchRepository;
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
 * Integration tests for the {@link AidasProjectPropertyResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AidasProjectPropertyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/aidas-project-properties";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/aidas-project-properties";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AidasProjectPropertyRepository aidasProjectPropertyRepository;

    /**
     * This repository is mocked in the com.ainnotate.aidas.repository.search test package.
     *
     * @see com.ainnotate.aidas.repository.search.AidasProjectPropertySearchRepositoryMockConfiguration
     */
    @Autowired
    private AidasProjectPropertySearchRepository mockAidasProjectPropertySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAidasProjectPropertyMockMvc;

    private AidasProjectProperty aidasProjectProperty;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasProjectProperty createEntity(EntityManager em) {
        AidasProjectProperty aidasProjectProperty = new AidasProjectProperty()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .value(DEFAULT_VALUE);
        // Add required entity
        AidasProject aidasProject;
        if (TestUtil.findAll(em, AidasProject.class).isEmpty()) {
            aidasProject = AidasProjectResourceIT.createEntity(em);
            em.persist(aidasProject);
            em.flush();
        } else {
            aidasProject = TestUtil.findAll(em, AidasProject.class).get(0);
        }
        aidasProjectProperty.setAidasProject(aidasProject);
        return aidasProjectProperty;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasProjectProperty createUpdatedEntity(EntityManager em) {
        AidasProjectProperty aidasProjectProperty = new AidasProjectProperty()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .value(UPDATED_VALUE);
        // Add required entity
        AidasProject aidasProject;
        if (TestUtil.findAll(em, AidasProject.class).isEmpty()) {
            aidasProject = AidasProjectResourceIT.createUpdatedEntity(em);
            em.persist(aidasProject);
            em.flush();
        } else {
            aidasProject = TestUtil.findAll(em, AidasProject.class).get(0);
        }
        aidasProjectProperty.setAidasProject(aidasProject);
        return aidasProjectProperty;
    }

    @BeforeEach
    public void initTest() {
        aidasProjectProperty = createEntity(em);
    }

    @Test
    @Transactional
    void createAidasProjectProperty() throws Exception {
        int databaseSizeBeforeCreate = aidasProjectPropertyRepository.findAll().size();
        // Create the AidasProjectProperty
        restAidasProjectPropertyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasProjectProperty))
            )
            .andExpect(status().isCreated());

        // Validate the AidasProjectProperty in the database
        List<AidasProjectProperty> aidasProjectPropertyList = aidasProjectPropertyRepository.findAll();
        assertThat(aidasProjectPropertyList).hasSize(databaseSizeBeforeCreate + 1);
        AidasProjectProperty testAidasProjectProperty = aidasProjectPropertyList.get(aidasProjectPropertyList.size() - 1);
        assertThat(testAidasProjectProperty.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAidasProjectProperty.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAidasProjectProperty.getValue()).isEqualTo(DEFAULT_VALUE);

        // Validate the AidasProjectProperty in Elasticsearch
        verify(mockAidasProjectPropertySearchRepository, times(1)).save(testAidasProjectProperty);
    }

    @Test
    @Transactional
    void createAidasProjectPropertyWithExistingId() throws Exception {
        // Create the AidasProjectProperty with an existing ID
        aidasProjectProperty.setId(1L);

        int databaseSizeBeforeCreate = aidasProjectPropertyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAidasProjectPropertyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasProjectProperty))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProjectProperty in the database
        List<AidasProjectProperty> aidasProjectPropertyList = aidasProjectPropertyRepository.findAll();
        assertThat(aidasProjectPropertyList).hasSize(databaseSizeBeforeCreate);

        // Validate the AidasProjectProperty in Elasticsearch
        verify(mockAidasProjectPropertySearchRepository, times(0)).save(aidasProjectProperty);
    }

    @Test
    @Transactional
    void getAllAidasProjectProperties() throws Exception {
        // Initialize the database
        aidasProjectPropertyRepository.saveAndFlush(aidasProjectProperty);

        // Get all the aidasProjectPropertyList
        restAidasProjectPropertyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasProjectProperty.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }

    @Test
    @Transactional
    void getAidasProjectProperty() throws Exception {
        // Initialize the database
        aidasProjectPropertyRepository.saveAndFlush(aidasProjectProperty);

        // Get the aidasProjectProperty
        restAidasProjectPropertyMockMvc
            .perform(get(ENTITY_API_URL_ID, aidasProjectProperty.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(aidasProjectProperty.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE));
    }

    @Test
    @Transactional
    void getNonExistingAidasProjectProperty() throws Exception {
        // Get the aidasProjectProperty
        restAidasProjectPropertyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAidasProjectProperty() throws Exception {
        // Initialize the database
        aidasProjectPropertyRepository.saveAndFlush(aidasProjectProperty);

        int databaseSizeBeforeUpdate = aidasProjectPropertyRepository.findAll().size();

        // Update the aidasProjectProperty
        AidasProjectProperty updatedAidasProjectProperty = aidasProjectPropertyRepository.findById(aidasProjectProperty.getId()).get();
        // Disconnect from session so that the updates on updatedAidasProjectProperty are not directly saved in db
        em.detach(updatedAidasProjectProperty);
        updatedAidasProjectProperty.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).value(UPDATED_VALUE);

        restAidasProjectPropertyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAidasProjectProperty.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAidasProjectProperty))
            )
            .andExpect(status().isOk());

        // Validate the AidasProjectProperty in the database
        List<AidasProjectProperty> aidasProjectPropertyList = aidasProjectPropertyRepository.findAll();
        assertThat(aidasProjectPropertyList).hasSize(databaseSizeBeforeUpdate);
        AidasProjectProperty testAidasProjectProperty = aidasProjectPropertyList.get(aidasProjectPropertyList.size() - 1);
        assertThat(testAidasProjectProperty.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAidasProjectProperty.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAidasProjectProperty.getValue()).isEqualTo(UPDATED_VALUE);

        // Validate the AidasProjectProperty in Elasticsearch
        verify(mockAidasProjectPropertySearchRepository).save(testAidasProjectProperty);
    }

    @Test
    @Transactional
    void putNonExistingAidasProjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = aidasProjectPropertyRepository.findAll().size();
        aidasProjectProperty.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasProjectPropertyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, aidasProjectProperty.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasProjectProperty))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProjectProperty in the database
        List<AidasProjectProperty> aidasProjectPropertyList = aidasProjectPropertyRepository.findAll();
        assertThat(aidasProjectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProjectProperty in Elasticsearch
        verify(mockAidasProjectPropertySearchRepository, times(0)).save(aidasProjectProperty);
    }

    @Test
    @Transactional
    void putWithIdMismatchAidasProjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = aidasProjectPropertyRepository.findAll().size();
        aidasProjectProperty.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasProjectPropertyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasProjectProperty))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProjectProperty in the database
        List<AidasProjectProperty> aidasProjectPropertyList = aidasProjectPropertyRepository.findAll();
        assertThat(aidasProjectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProjectProperty in Elasticsearch
        verify(mockAidasProjectPropertySearchRepository, times(0)).save(aidasProjectProperty);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAidasProjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = aidasProjectPropertyRepository.findAll().size();
        aidasProjectProperty.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasProjectPropertyMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasProjectProperty))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasProjectProperty in the database
        List<AidasProjectProperty> aidasProjectPropertyList = aidasProjectPropertyRepository.findAll();
        assertThat(aidasProjectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProjectProperty in Elasticsearch
        verify(mockAidasProjectPropertySearchRepository, times(0)).save(aidasProjectProperty);
    }

    @Test
    @Transactional
    void partialUpdateAidasProjectPropertyWithPatch() throws Exception {
        // Initialize the database
        aidasProjectPropertyRepository.saveAndFlush(aidasProjectProperty);

        int databaseSizeBeforeUpdate = aidasProjectPropertyRepository.findAll().size();

        // Update the aidasProjectProperty using partial update
        AidasProjectProperty partialUpdatedAidasProjectProperty = new AidasProjectProperty();
        partialUpdatedAidasProjectProperty.setId(aidasProjectProperty.getId());

        partialUpdatedAidasProjectProperty.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restAidasProjectPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasProjectProperty.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasProjectProperty))
            )
            .andExpect(status().isOk());

        // Validate the AidasProjectProperty in the database
        List<AidasProjectProperty> aidasProjectPropertyList = aidasProjectPropertyRepository.findAll();
        assertThat(aidasProjectPropertyList).hasSize(databaseSizeBeforeUpdate);
        AidasProjectProperty testAidasProjectProperty = aidasProjectPropertyList.get(aidasProjectPropertyList.size() - 1);
        assertThat(testAidasProjectProperty.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAidasProjectProperty.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAidasProjectProperty.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    void fullUpdateAidasProjectPropertyWithPatch() throws Exception {
        // Initialize the database
        aidasProjectPropertyRepository.saveAndFlush(aidasProjectProperty);

        int databaseSizeBeforeUpdate = aidasProjectPropertyRepository.findAll().size();

        // Update the aidasProjectProperty using partial update
        AidasProjectProperty partialUpdatedAidasProjectProperty = new AidasProjectProperty();
        partialUpdatedAidasProjectProperty.setId(aidasProjectProperty.getId());

        partialUpdatedAidasProjectProperty.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).value(UPDATED_VALUE);

        restAidasProjectPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasProjectProperty.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasProjectProperty))
            )
            .andExpect(status().isOk());

        // Validate the AidasProjectProperty in the database
        List<AidasProjectProperty> aidasProjectPropertyList = aidasProjectPropertyRepository.findAll();
        assertThat(aidasProjectPropertyList).hasSize(databaseSizeBeforeUpdate);
        AidasProjectProperty testAidasProjectProperty = aidasProjectPropertyList.get(aidasProjectPropertyList.size() - 1);
        assertThat(testAidasProjectProperty.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAidasProjectProperty.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAidasProjectProperty.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    void patchNonExistingAidasProjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = aidasProjectPropertyRepository.findAll().size();
        aidasProjectProperty.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasProjectPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, aidasProjectProperty.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasProjectProperty))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProjectProperty in the database
        List<AidasProjectProperty> aidasProjectPropertyList = aidasProjectPropertyRepository.findAll();
        assertThat(aidasProjectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProjectProperty in Elasticsearch
        verify(mockAidasProjectPropertySearchRepository, times(0)).save(aidasProjectProperty);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAidasProjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = aidasProjectPropertyRepository.findAll().size();
        aidasProjectProperty.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasProjectPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasProjectProperty))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProjectProperty in the database
        List<AidasProjectProperty> aidasProjectPropertyList = aidasProjectPropertyRepository.findAll();
        assertThat(aidasProjectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProjectProperty in Elasticsearch
        verify(mockAidasProjectPropertySearchRepository, times(0)).save(aidasProjectProperty);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAidasProjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = aidasProjectPropertyRepository.findAll().size();
        aidasProjectProperty.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasProjectPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasProjectProperty))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasProjectProperty in the database
        List<AidasProjectProperty> aidasProjectPropertyList = aidasProjectPropertyRepository.findAll();
        assertThat(aidasProjectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProjectProperty in Elasticsearch
        verify(mockAidasProjectPropertySearchRepository, times(0)).save(aidasProjectProperty);
    }

    @Test
    @Transactional
    void deleteAidasProjectProperty() throws Exception {
        // Initialize the database
        aidasProjectPropertyRepository.saveAndFlush(aidasProjectProperty);

        int databaseSizeBeforeDelete = aidasProjectPropertyRepository.findAll().size();

        // Delete the aidasProjectProperty
        restAidasProjectPropertyMockMvc
            .perform(delete(ENTITY_API_URL_ID, aidasProjectProperty.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AidasProjectProperty> aidasProjectPropertyList = aidasProjectPropertyRepository.findAll();
        assertThat(aidasProjectPropertyList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AidasProjectProperty in Elasticsearch
        verify(mockAidasProjectPropertySearchRepository, times(1)).deleteById(aidasProjectProperty.getId());
    }

    @Test
    @Transactional
    void searchAidasProjectProperty() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        aidasProjectPropertyRepository.saveAndFlush(aidasProjectProperty);
        when(mockAidasProjectPropertySearchRepository.search("id:" + aidasProjectProperty.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(aidasProjectProperty), PageRequest.of(0, 1), 1));

        // Search the aidasProjectProperty
        restAidasProjectPropertyMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + aidasProjectProperty.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasProjectProperty.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }
}
