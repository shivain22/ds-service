package com.ainnotate.aidas.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ainnotate.aidas.IntegrationTest;
import com.ainnotate.aidas.domain.AidasUploadMetaData;
import com.ainnotate.aidas.repository.AidasUploadMetaDataRepository;
import com.ainnotate.aidas.repository.search.AidasUploadMetaDataSearchRepository;
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
 * Integration tests for the {@link AidasUploadMetaDataResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AidasUploadMetaDataResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/aidas-upload-meta-data";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/aidas-upload-meta-data";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AidasUploadMetaDataRepository aidasUploadMetaDataRepository;

    /**
     * This repository is mocked in the com.ainnotate.aidas.repository.search test package.
     *
     * @see com.ainnotate.aidas.repository.search.AidasUploadMetaDataSearchRepositoryMockConfiguration
     */
    @Autowired
    private AidasUploadMetaDataSearchRepository mockAidasUploadMetaDataSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAidasUploadMetaDataMockMvc;

    private AidasUploadMetaData aidasUploadMetaData;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasUploadMetaData createEntity(EntityManager em) {
        AidasUploadMetaData aidasUploadMetaData = new AidasUploadMetaData().name(DEFAULT_NAME).value(DEFAULT_VALUE);
        return aidasUploadMetaData;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasUploadMetaData createUpdatedEntity(EntityManager em) {
        AidasUploadMetaData aidasUploadMetaData = new AidasUploadMetaData().name(UPDATED_NAME).value(UPDATED_VALUE);
        return aidasUploadMetaData;
    }

    @BeforeEach
    public void initTest() {
        aidasUploadMetaData = createEntity(em);
    }

    @Test
    @Transactional
    void createAidasUploadMetaData() throws Exception {
        int databaseSizeBeforeCreate = aidasUploadMetaDataRepository.findAll().size();
        // Create the AidasUploadMetaData
        restAidasUploadMetaDataMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUploadMetaData))
            )
            .andExpect(status().isCreated());

        // Validate the AidasUploadMetaData in the database
        List<AidasUploadMetaData> aidasUploadMetaDataList = aidasUploadMetaDataRepository.findAll();
        assertThat(aidasUploadMetaDataList).hasSize(databaseSizeBeforeCreate + 1);
        AidasUploadMetaData testAidasUploadMetaData = aidasUploadMetaDataList.get(aidasUploadMetaDataList.size() - 1);
        assertThat(testAidasUploadMetaData.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAidasUploadMetaData.getValue()).isEqualTo(DEFAULT_VALUE);

        // Validate the AidasUploadMetaData in Elasticsearch
        verify(mockAidasUploadMetaDataSearchRepository, times(1)).save(testAidasUploadMetaData);
    }

    @Test
    @Transactional
    void createAidasUploadMetaDataWithExistingId() throws Exception {
        // Create the AidasUploadMetaData with an existing ID
        aidasUploadMetaData.setId(1L);

        int databaseSizeBeforeCreate = aidasUploadMetaDataRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAidasUploadMetaDataMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUploadMetaData))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUploadMetaData in the database
        List<AidasUploadMetaData> aidasUploadMetaDataList = aidasUploadMetaDataRepository.findAll();
        assertThat(aidasUploadMetaDataList).hasSize(databaseSizeBeforeCreate);

        // Validate the AidasUploadMetaData in Elasticsearch
        verify(mockAidasUploadMetaDataSearchRepository, times(0)).save(aidasUploadMetaData);
    }

    @Test
    @Transactional
    void getAllAidasUploadMetaData() throws Exception {
        // Initialize the database
        aidasUploadMetaDataRepository.saveAndFlush(aidasUploadMetaData);

        // Get all the aidasUploadMetaDataList
        restAidasUploadMetaDataMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasUploadMetaData.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }

    @Test
    @Transactional
    void getAidasUploadMetaData() throws Exception {
        // Initialize the database
        aidasUploadMetaDataRepository.saveAndFlush(aidasUploadMetaData);

        // Get the aidasUploadMetaData
        restAidasUploadMetaDataMockMvc
            .perform(get(ENTITY_API_URL_ID, aidasUploadMetaData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(aidasUploadMetaData.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE));
    }

    @Test
    @Transactional
    void getNonExistingAidasUploadMetaData() throws Exception {
        // Get the aidasUploadMetaData
        restAidasUploadMetaDataMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAidasUploadMetaData() throws Exception {
        // Initialize the database
        aidasUploadMetaDataRepository.saveAndFlush(aidasUploadMetaData);

        int databaseSizeBeforeUpdate = aidasUploadMetaDataRepository.findAll().size();

        // Update the aidasUploadMetaData
        AidasUploadMetaData updatedAidasUploadMetaData = aidasUploadMetaDataRepository.findById(aidasUploadMetaData.getId()).get();
        // Disconnect from session so that the updates on updatedAidasUploadMetaData are not directly saved in db
        em.detach(updatedAidasUploadMetaData);
        updatedAidasUploadMetaData.name(UPDATED_NAME).value(UPDATED_VALUE);

        restAidasUploadMetaDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAidasUploadMetaData.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAidasUploadMetaData))
            )
            .andExpect(status().isOk());

        // Validate the AidasUploadMetaData in the database
        List<AidasUploadMetaData> aidasUploadMetaDataList = aidasUploadMetaDataRepository.findAll();
        assertThat(aidasUploadMetaDataList).hasSize(databaseSizeBeforeUpdate);
        AidasUploadMetaData testAidasUploadMetaData = aidasUploadMetaDataList.get(aidasUploadMetaDataList.size() - 1);
        assertThat(testAidasUploadMetaData.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAidasUploadMetaData.getValue()).isEqualTo(UPDATED_VALUE);

        // Validate the AidasUploadMetaData in Elasticsearch
        verify(mockAidasUploadMetaDataSearchRepository).save(testAidasUploadMetaData);
    }

    @Test
    @Transactional
    void putNonExistingAidasUploadMetaData() throws Exception {
        int databaseSizeBeforeUpdate = aidasUploadMetaDataRepository.findAll().size();
        aidasUploadMetaData.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasUploadMetaDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, aidasUploadMetaData.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUploadMetaData))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUploadMetaData in the database
        List<AidasUploadMetaData> aidasUploadMetaDataList = aidasUploadMetaDataRepository.findAll();
        assertThat(aidasUploadMetaDataList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUploadMetaData in Elasticsearch
        verify(mockAidasUploadMetaDataSearchRepository, times(0)).save(aidasUploadMetaData);
    }

    @Test
    @Transactional
    void putWithIdMismatchAidasUploadMetaData() throws Exception {
        int databaseSizeBeforeUpdate = aidasUploadMetaDataRepository.findAll().size();
        aidasUploadMetaData.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUploadMetaDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUploadMetaData))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUploadMetaData in the database
        List<AidasUploadMetaData> aidasUploadMetaDataList = aidasUploadMetaDataRepository.findAll();
        assertThat(aidasUploadMetaDataList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUploadMetaData in Elasticsearch
        verify(mockAidasUploadMetaDataSearchRepository, times(0)).save(aidasUploadMetaData);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAidasUploadMetaData() throws Exception {
        int databaseSizeBeforeUpdate = aidasUploadMetaDataRepository.findAll().size();
        aidasUploadMetaData.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUploadMetaDataMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUploadMetaData))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasUploadMetaData in the database
        List<AidasUploadMetaData> aidasUploadMetaDataList = aidasUploadMetaDataRepository.findAll();
        assertThat(aidasUploadMetaDataList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUploadMetaData in Elasticsearch
        verify(mockAidasUploadMetaDataSearchRepository, times(0)).save(aidasUploadMetaData);
    }

    @Test
    @Transactional
    void partialUpdateAidasUploadMetaDataWithPatch() throws Exception {
        // Initialize the database
        aidasUploadMetaDataRepository.saveAndFlush(aidasUploadMetaData);

        int databaseSizeBeforeUpdate = aidasUploadMetaDataRepository.findAll().size();

        // Update the aidasUploadMetaData using partial update
        AidasUploadMetaData partialUpdatedAidasUploadMetaData = new AidasUploadMetaData();
        partialUpdatedAidasUploadMetaData.setId(aidasUploadMetaData.getId());

        partialUpdatedAidasUploadMetaData.name(UPDATED_NAME);

        restAidasUploadMetaDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasUploadMetaData.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasUploadMetaData))
            )
            .andExpect(status().isOk());

        // Validate the AidasUploadMetaData in the database
        List<AidasUploadMetaData> aidasUploadMetaDataList = aidasUploadMetaDataRepository.findAll();
        assertThat(aidasUploadMetaDataList).hasSize(databaseSizeBeforeUpdate);
        AidasUploadMetaData testAidasUploadMetaData = aidasUploadMetaDataList.get(aidasUploadMetaDataList.size() - 1);
        assertThat(testAidasUploadMetaData.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAidasUploadMetaData.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    void fullUpdateAidasUploadMetaDataWithPatch() throws Exception {
        // Initialize the database
        aidasUploadMetaDataRepository.saveAndFlush(aidasUploadMetaData);

        int databaseSizeBeforeUpdate = aidasUploadMetaDataRepository.findAll().size();

        // Update the aidasUploadMetaData using partial update
        AidasUploadMetaData partialUpdatedAidasUploadMetaData = new AidasUploadMetaData();
        partialUpdatedAidasUploadMetaData.setId(aidasUploadMetaData.getId());

        partialUpdatedAidasUploadMetaData.name(UPDATED_NAME).value(UPDATED_VALUE);

        restAidasUploadMetaDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasUploadMetaData.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasUploadMetaData))
            )
            .andExpect(status().isOk());

        // Validate the AidasUploadMetaData in the database
        List<AidasUploadMetaData> aidasUploadMetaDataList = aidasUploadMetaDataRepository.findAll();
        assertThat(aidasUploadMetaDataList).hasSize(databaseSizeBeforeUpdate);
        AidasUploadMetaData testAidasUploadMetaData = aidasUploadMetaDataList.get(aidasUploadMetaDataList.size() - 1);
        assertThat(testAidasUploadMetaData.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAidasUploadMetaData.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    void patchNonExistingAidasUploadMetaData() throws Exception {
        int databaseSizeBeforeUpdate = aidasUploadMetaDataRepository.findAll().size();
        aidasUploadMetaData.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasUploadMetaDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, aidasUploadMetaData.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasUploadMetaData))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUploadMetaData in the database
        List<AidasUploadMetaData> aidasUploadMetaDataList = aidasUploadMetaDataRepository.findAll();
        assertThat(aidasUploadMetaDataList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUploadMetaData in Elasticsearch
        verify(mockAidasUploadMetaDataSearchRepository, times(0)).save(aidasUploadMetaData);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAidasUploadMetaData() throws Exception {
        int databaseSizeBeforeUpdate = aidasUploadMetaDataRepository.findAll().size();
        aidasUploadMetaData.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUploadMetaDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasUploadMetaData))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUploadMetaData in the database
        List<AidasUploadMetaData> aidasUploadMetaDataList = aidasUploadMetaDataRepository.findAll();
        assertThat(aidasUploadMetaDataList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUploadMetaData in Elasticsearch
        verify(mockAidasUploadMetaDataSearchRepository, times(0)).save(aidasUploadMetaData);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAidasUploadMetaData() throws Exception {
        int databaseSizeBeforeUpdate = aidasUploadMetaDataRepository.findAll().size();
        aidasUploadMetaData.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUploadMetaDataMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasUploadMetaData))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasUploadMetaData in the database
        List<AidasUploadMetaData> aidasUploadMetaDataList = aidasUploadMetaDataRepository.findAll();
        assertThat(aidasUploadMetaDataList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUploadMetaData in Elasticsearch
        verify(mockAidasUploadMetaDataSearchRepository, times(0)).save(aidasUploadMetaData);
    }

    @Test
    @Transactional
    void deleteAidasUploadMetaData() throws Exception {
        // Initialize the database
        aidasUploadMetaDataRepository.saveAndFlush(aidasUploadMetaData);

        int databaseSizeBeforeDelete = aidasUploadMetaDataRepository.findAll().size();

        // Delete the aidasUploadMetaData
        restAidasUploadMetaDataMockMvc
            .perform(delete(ENTITY_API_URL_ID, aidasUploadMetaData.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AidasUploadMetaData> aidasUploadMetaDataList = aidasUploadMetaDataRepository.findAll();
        assertThat(aidasUploadMetaDataList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AidasUploadMetaData in Elasticsearch
        verify(mockAidasUploadMetaDataSearchRepository, times(1)).deleteById(aidasUploadMetaData.getId());
    }

    @Test
    @Transactional
    void searchAidasUploadMetaData() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        aidasUploadMetaDataRepository.saveAndFlush(aidasUploadMetaData);
        when(mockAidasUploadMetaDataSearchRepository.search("id:" + aidasUploadMetaData.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(aidasUploadMetaData), PageRequest.of(0, 1), 1));

        // Search the aidasUploadMetaData
        restAidasUploadMetaDataMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + aidasUploadMetaData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasUploadMetaData.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }
}
