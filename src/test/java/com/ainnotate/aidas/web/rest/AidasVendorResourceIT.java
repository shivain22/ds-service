package com.ainnotate.aidas.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ainnotate.aidas.IntegrationTest;
import com.ainnotate.aidas.domain.AidasVendor;
import com.ainnotate.aidas.repository.AidasVendorRepository;
import com.ainnotate.aidas.repository.search.AidasVendorSearchRepository;
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
 * Integration tests for the {@link AidasVendorResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AidasVendorResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/aidas-vendors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/aidas-vendors";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AidasVendorRepository aidasVendorRepository;

    /**
     * This repository is mocked in the com.ainnotate.aidas.repository.search test package.
     *
     * @see com.ainnotate.aidas.repository.search.AidasVendorSearchRepositoryMockConfiguration
     */
    @Autowired
    private AidasVendorSearchRepository mockAidasVendorSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAidasVendorMockMvc;

    private AidasVendor aidasVendor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasVendor createEntity(EntityManager em) {
        AidasVendor aidasVendor = new AidasVendor().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        return aidasVendor;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasVendor createUpdatedEntity(EntityManager em) {
        AidasVendor aidasVendor = new AidasVendor().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        return aidasVendor;
    }

    @BeforeEach
    public void initTest() {
        aidasVendor = createEntity(em);
    }

    @Test
    @Transactional
    void createAidasVendor() throws Exception {
        int databaseSizeBeforeCreate = aidasVendorRepository.findAll().size();
        // Create the AidasVendor
        restAidasVendorMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasVendor))
            )
            .andExpect(status().isCreated());

        // Validate the AidasVendor in the database
        List<AidasVendor> aidasVendorList = aidasVendorRepository.findAll();
        assertThat(aidasVendorList).hasSize(databaseSizeBeforeCreate + 1);
        AidasVendor testAidasVendor = aidasVendorList.get(aidasVendorList.size() - 1);
        assertThat(testAidasVendor.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAidasVendor.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the AidasVendor in Elasticsearch
        verify(mockAidasVendorSearchRepository, times(1)).save(testAidasVendor);
    }

    @Test
    @Transactional
    void createAidasVendorWithExistingId() throws Exception {
        // Create the AidasVendor with an existing ID
        aidasVendor.setId(1L);

        int databaseSizeBeforeCreate = aidasVendorRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAidasVendorMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasVendor))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasVendor in the database
        List<AidasVendor> aidasVendorList = aidasVendorRepository.findAll();
        assertThat(aidasVendorList).hasSize(databaseSizeBeforeCreate);

        // Validate the AidasVendor in Elasticsearch
        verify(mockAidasVendorSearchRepository, times(0)).save(aidasVendor);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = aidasVendorRepository.findAll().size();
        // set the field null
        aidasVendor.setName(null);

        // Create the AidasVendor, which fails.

        restAidasVendorMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasVendor))
            )
            .andExpect(status().isBadRequest());

        List<AidasVendor> aidasVendorList = aidasVendorRepository.findAll();
        assertThat(aidasVendorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAidasVendors() throws Exception {
        // Initialize the database
        aidasVendorRepository.saveAndFlush(aidasVendor);

        // Get all the aidasVendorList
        restAidasVendorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasVendor.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getAidasVendor() throws Exception {
        // Initialize the database
        aidasVendorRepository.saveAndFlush(aidasVendor);

        // Get the aidasVendor
        restAidasVendorMockMvc
            .perform(get(ENTITY_API_URL_ID, aidasVendor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(aidasVendor.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingAidasVendor() throws Exception {
        // Get the aidasVendor
        restAidasVendorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAidasVendor() throws Exception {
        // Initialize the database
        aidasVendorRepository.saveAndFlush(aidasVendor);

        int databaseSizeBeforeUpdate = aidasVendorRepository.findAll().size();

        // Update the aidasVendor
        AidasVendor updatedAidasVendor = aidasVendorRepository.findById(aidasVendor.getId()).get();
        // Disconnect from session so that the updates on updatedAidasVendor are not directly saved in db
        em.detach(updatedAidasVendor);
        updatedAidasVendor.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restAidasVendorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAidasVendor.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAidasVendor))
            )
            .andExpect(status().isOk());

        // Validate the AidasVendor in the database
        List<AidasVendor> aidasVendorList = aidasVendorRepository.findAll();
        assertThat(aidasVendorList).hasSize(databaseSizeBeforeUpdate);
        AidasVendor testAidasVendor = aidasVendorList.get(aidasVendorList.size() - 1);
        assertThat(testAidasVendor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAidasVendor.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the AidasVendor in Elasticsearch
        verify(mockAidasVendorSearchRepository).save(testAidasVendor);
    }

    @Test
    @Transactional
    void putNonExistingAidasVendor() throws Exception {
        int databaseSizeBeforeUpdate = aidasVendorRepository.findAll().size();
        aidasVendor.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasVendorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, aidasVendor.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasVendor))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasVendor in the database
        List<AidasVendor> aidasVendorList = aidasVendorRepository.findAll();
        assertThat(aidasVendorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasVendor in Elasticsearch
        verify(mockAidasVendorSearchRepository, times(0)).save(aidasVendor);
    }

    @Test
    @Transactional
    void putWithIdMismatchAidasVendor() throws Exception {
        int databaseSizeBeforeUpdate = aidasVendorRepository.findAll().size();
        aidasVendor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasVendorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasVendor))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasVendor in the database
        List<AidasVendor> aidasVendorList = aidasVendorRepository.findAll();
        assertThat(aidasVendorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasVendor in Elasticsearch
        verify(mockAidasVendorSearchRepository, times(0)).save(aidasVendor);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAidasVendor() throws Exception {
        int databaseSizeBeforeUpdate = aidasVendorRepository.findAll().size();
        aidasVendor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasVendorMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasVendor))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasVendor in the database
        List<AidasVendor> aidasVendorList = aidasVendorRepository.findAll();
        assertThat(aidasVendorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasVendor in Elasticsearch
        verify(mockAidasVendorSearchRepository, times(0)).save(aidasVendor);
    }

    @Test
    @Transactional
    void partialUpdateAidasVendorWithPatch() throws Exception {
        // Initialize the database
        aidasVendorRepository.saveAndFlush(aidasVendor);

        int databaseSizeBeforeUpdate = aidasVendorRepository.findAll().size();

        // Update the aidasVendor using partial update
        AidasVendor partialUpdatedAidasVendor = new AidasVendor();
        partialUpdatedAidasVendor.setId(aidasVendor.getId());

        partialUpdatedAidasVendor.description(UPDATED_DESCRIPTION);

        restAidasVendorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasVendor.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasVendor))
            )
            .andExpect(status().isOk());

        // Validate the AidasVendor in the database
        List<AidasVendor> aidasVendorList = aidasVendorRepository.findAll();
        assertThat(aidasVendorList).hasSize(databaseSizeBeforeUpdate);
        AidasVendor testAidasVendor = aidasVendorList.get(aidasVendorList.size() - 1);
        assertThat(testAidasVendor.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAidasVendor.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateAidasVendorWithPatch() throws Exception {
        // Initialize the database
        aidasVendorRepository.saveAndFlush(aidasVendor);

        int databaseSizeBeforeUpdate = aidasVendorRepository.findAll().size();

        // Update the aidasVendor using partial update
        AidasVendor partialUpdatedAidasVendor = new AidasVendor();
        partialUpdatedAidasVendor.setId(aidasVendor.getId());

        partialUpdatedAidasVendor.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restAidasVendorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasVendor.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasVendor))
            )
            .andExpect(status().isOk());

        // Validate the AidasVendor in the database
        List<AidasVendor> aidasVendorList = aidasVendorRepository.findAll();
        assertThat(aidasVendorList).hasSize(databaseSizeBeforeUpdate);
        AidasVendor testAidasVendor = aidasVendorList.get(aidasVendorList.size() - 1);
        assertThat(testAidasVendor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAidasVendor.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingAidasVendor() throws Exception {
        int databaseSizeBeforeUpdate = aidasVendorRepository.findAll().size();
        aidasVendor.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasVendorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, aidasVendor.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasVendor))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasVendor in the database
        List<AidasVendor> aidasVendorList = aidasVendorRepository.findAll();
        assertThat(aidasVendorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasVendor in Elasticsearch
        verify(mockAidasVendorSearchRepository, times(0)).save(aidasVendor);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAidasVendor() throws Exception {
        int databaseSizeBeforeUpdate = aidasVendorRepository.findAll().size();
        aidasVendor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasVendorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasVendor))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasVendor in the database
        List<AidasVendor> aidasVendorList = aidasVendorRepository.findAll();
        assertThat(aidasVendorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasVendor in Elasticsearch
        verify(mockAidasVendorSearchRepository, times(0)).save(aidasVendor);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAidasVendor() throws Exception {
        int databaseSizeBeforeUpdate = aidasVendorRepository.findAll().size();
        aidasVendor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasVendorMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasVendor))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasVendor in the database
        List<AidasVendor> aidasVendorList = aidasVendorRepository.findAll();
        assertThat(aidasVendorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasVendor in Elasticsearch
        verify(mockAidasVendorSearchRepository, times(0)).save(aidasVendor);
    }

    @Test
    @Transactional
    void deleteAidasVendor() throws Exception {
        // Initialize the database
        aidasVendorRepository.saveAndFlush(aidasVendor);

        int databaseSizeBeforeDelete = aidasVendorRepository.findAll().size();

        // Delete the aidasVendor
        restAidasVendorMockMvc
            .perform(delete(ENTITY_API_URL_ID, aidasVendor.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AidasVendor> aidasVendorList = aidasVendorRepository.findAll();
        assertThat(aidasVendorList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AidasVendor in Elasticsearch
        verify(mockAidasVendorSearchRepository, times(1)).deleteById(aidasVendor.getId());
    }

    @Test
    @Transactional
    void searchAidasVendor() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        aidasVendorRepository.saveAndFlush(aidasVendor);
        when(mockAidasVendorSearchRepository.search("id:" + aidasVendor.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(aidasVendor), PageRequest.of(0, 1), 1));

        // Search the aidasVendor
        restAidasVendorMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + aidasVendor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasVendor.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}
