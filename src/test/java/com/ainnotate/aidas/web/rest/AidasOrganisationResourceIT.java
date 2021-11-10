package com.ainnotate.aidas.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ainnotate.aidas.IntegrationTest;
import com.ainnotate.aidas.domain.AidasOrganisation;
import com.ainnotate.aidas.repository.AidasOrganisationRepository;
import com.ainnotate.aidas.repository.search.AidasOrganisationSearchRepository;
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
 * Integration tests for the {@link AidasOrganisationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AidasOrganisationResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/aidas-organisations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/aidas-organisations";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AidasOrganisationRepository aidasOrganisationRepository;

    /**
     * This repository is mocked in the com.ainnotate.aidas.repository.search test package.
     *
     * @see com.ainnotate.aidas.repository.search.AidasOrganisationSearchRepositoryMockConfiguration
     */
    @Autowired
    private AidasOrganisationSearchRepository mockAidasOrganisationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAidasOrganisationMockMvc;

    private AidasOrganisation aidasOrganisation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasOrganisation createEntity(EntityManager em) {
        AidasOrganisation aidasOrganisation = new AidasOrganisation().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        return aidasOrganisation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasOrganisation createUpdatedEntity(EntityManager em) {
        AidasOrganisation aidasOrganisation = new AidasOrganisation().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        return aidasOrganisation;
    }

    @BeforeEach
    public void initTest() {
        aidasOrganisation = createEntity(em);
    }

    @Test
    @Transactional
    void createAidasOrganisation() throws Exception {
        int databaseSizeBeforeCreate = aidasOrganisationRepository.findAll().size();
        // Create the AidasOrganisation
        restAidasOrganisationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasOrganisation))
            )
            .andExpect(status().isCreated());

        // Validate the AidasOrganisation in the database
        List<AidasOrganisation> aidasOrganisationList = aidasOrganisationRepository.findAll();
        assertThat(aidasOrganisationList).hasSize(databaseSizeBeforeCreate + 1);
        AidasOrganisation testAidasOrganisation = aidasOrganisationList.get(aidasOrganisationList.size() - 1);
        assertThat(testAidasOrganisation.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAidasOrganisation.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the AidasOrganisation in Elasticsearch
        verify(mockAidasOrganisationSearchRepository, times(1)).save(testAidasOrganisation);
    }

    @Test
    @Transactional
    void createAidasOrganisationWithExistingId() throws Exception {
        // Create the AidasOrganisation with an existing ID
        aidasOrganisation.setId(1L);

        int databaseSizeBeforeCreate = aidasOrganisationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAidasOrganisationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasOrganisation))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasOrganisation in the database
        List<AidasOrganisation> aidasOrganisationList = aidasOrganisationRepository.findAll();
        assertThat(aidasOrganisationList).hasSize(databaseSizeBeforeCreate);

        // Validate the AidasOrganisation in Elasticsearch
        verify(mockAidasOrganisationSearchRepository, times(0)).save(aidasOrganisation);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = aidasOrganisationRepository.findAll().size();
        // set the field null
        aidasOrganisation.setName(null);

        // Create the AidasOrganisation, which fails.

        restAidasOrganisationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasOrganisation))
            )
            .andExpect(status().isBadRequest());

        List<AidasOrganisation> aidasOrganisationList = aidasOrganisationRepository.findAll();
        assertThat(aidasOrganisationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAidasOrganisations() throws Exception {
        // Initialize the database
        aidasOrganisationRepository.saveAndFlush(aidasOrganisation);

        // Get all the aidasOrganisationList
        restAidasOrganisationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasOrganisation.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getAidasOrganisation() throws Exception {
        // Initialize the database
        aidasOrganisationRepository.saveAndFlush(aidasOrganisation);

        // Get the aidasOrganisation
        restAidasOrganisationMockMvc
            .perform(get(ENTITY_API_URL_ID, aidasOrganisation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(aidasOrganisation.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingAidasOrganisation() throws Exception {
        // Get the aidasOrganisation
        restAidasOrganisationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAidasOrganisation() throws Exception {
        // Initialize the database
        aidasOrganisationRepository.saveAndFlush(aidasOrganisation);

        int databaseSizeBeforeUpdate = aidasOrganisationRepository.findAll().size();

        // Update the aidasOrganisation
        AidasOrganisation updatedAidasOrganisation = aidasOrganisationRepository.findById(aidasOrganisation.getId()).get();
        // Disconnect from session so that the updates on updatedAidasOrganisation are not directly saved in db
        em.detach(updatedAidasOrganisation);
        updatedAidasOrganisation.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restAidasOrganisationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAidasOrganisation.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAidasOrganisation))
            )
            .andExpect(status().isOk());

        // Validate the AidasOrganisation in the database
        List<AidasOrganisation> aidasOrganisationList = aidasOrganisationRepository.findAll();
        assertThat(aidasOrganisationList).hasSize(databaseSizeBeforeUpdate);
        AidasOrganisation testAidasOrganisation = aidasOrganisationList.get(aidasOrganisationList.size() - 1);
        assertThat(testAidasOrganisation.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAidasOrganisation.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the AidasOrganisation in Elasticsearch
        verify(mockAidasOrganisationSearchRepository).save(testAidasOrganisation);
    }

    @Test
    @Transactional
    void putNonExistingAidasOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = aidasOrganisationRepository.findAll().size();
        aidasOrganisation.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasOrganisationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, aidasOrganisation.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasOrganisation))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasOrganisation in the database
        List<AidasOrganisation> aidasOrganisationList = aidasOrganisationRepository.findAll();
        assertThat(aidasOrganisationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasOrganisation in Elasticsearch
        verify(mockAidasOrganisationSearchRepository, times(0)).save(aidasOrganisation);
    }

    @Test
    @Transactional
    void putWithIdMismatchAidasOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = aidasOrganisationRepository.findAll().size();
        aidasOrganisation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasOrganisationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasOrganisation))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasOrganisation in the database
        List<AidasOrganisation> aidasOrganisationList = aidasOrganisationRepository.findAll();
        assertThat(aidasOrganisationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasOrganisation in Elasticsearch
        verify(mockAidasOrganisationSearchRepository, times(0)).save(aidasOrganisation);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAidasOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = aidasOrganisationRepository.findAll().size();
        aidasOrganisation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasOrganisationMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasOrganisation))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasOrganisation in the database
        List<AidasOrganisation> aidasOrganisationList = aidasOrganisationRepository.findAll();
        assertThat(aidasOrganisationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasOrganisation in Elasticsearch
        verify(mockAidasOrganisationSearchRepository, times(0)).save(aidasOrganisation);
    }

    @Test
    @Transactional
    void partialUpdateAidasOrganisationWithPatch() throws Exception {
        // Initialize the database
        aidasOrganisationRepository.saveAndFlush(aidasOrganisation);

        int databaseSizeBeforeUpdate = aidasOrganisationRepository.findAll().size();

        // Update the aidasOrganisation using partial update
        AidasOrganisation partialUpdatedAidasOrganisation = new AidasOrganisation();
        partialUpdatedAidasOrganisation.setId(aidasOrganisation.getId());

        partialUpdatedAidasOrganisation.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restAidasOrganisationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasOrganisation.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasOrganisation))
            )
            .andExpect(status().isOk());

        // Validate the AidasOrganisation in the database
        List<AidasOrganisation> aidasOrganisationList = aidasOrganisationRepository.findAll();
        assertThat(aidasOrganisationList).hasSize(databaseSizeBeforeUpdate);
        AidasOrganisation testAidasOrganisation = aidasOrganisationList.get(aidasOrganisationList.size() - 1);
        assertThat(testAidasOrganisation.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAidasOrganisation.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateAidasOrganisationWithPatch() throws Exception {
        // Initialize the database
        aidasOrganisationRepository.saveAndFlush(aidasOrganisation);

        int databaseSizeBeforeUpdate = aidasOrganisationRepository.findAll().size();

        // Update the aidasOrganisation using partial update
        AidasOrganisation partialUpdatedAidasOrganisation = new AidasOrganisation();
        partialUpdatedAidasOrganisation.setId(aidasOrganisation.getId());

        partialUpdatedAidasOrganisation.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restAidasOrganisationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasOrganisation.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasOrganisation))
            )
            .andExpect(status().isOk());

        // Validate the AidasOrganisation in the database
        List<AidasOrganisation> aidasOrganisationList = aidasOrganisationRepository.findAll();
        assertThat(aidasOrganisationList).hasSize(databaseSizeBeforeUpdate);
        AidasOrganisation testAidasOrganisation = aidasOrganisationList.get(aidasOrganisationList.size() - 1);
        assertThat(testAidasOrganisation.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAidasOrganisation.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingAidasOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = aidasOrganisationRepository.findAll().size();
        aidasOrganisation.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasOrganisationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, aidasOrganisation.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasOrganisation))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasOrganisation in the database
        List<AidasOrganisation> aidasOrganisationList = aidasOrganisationRepository.findAll();
        assertThat(aidasOrganisationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasOrganisation in Elasticsearch
        verify(mockAidasOrganisationSearchRepository, times(0)).save(aidasOrganisation);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAidasOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = aidasOrganisationRepository.findAll().size();
        aidasOrganisation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasOrganisationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasOrganisation))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasOrganisation in the database
        List<AidasOrganisation> aidasOrganisationList = aidasOrganisationRepository.findAll();
        assertThat(aidasOrganisationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasOrganisation in Elasticsearch
        verify(mockAidasOrganisationSearchRepository, times(0)).save(aidasOrganisation);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAidasOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = aidasOrganisationRepository.findAll().size();
        aidasOrganisation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasOrganisationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasOrganisation))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasOrganisation in the database
        List<AidasOrganisation> aidasOrganisationList = aidasOrganisationRepository.findAll();
        assertThat(aidasOrganisationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasOrganisation in Elasticsearch
        verify(mockAidasOrganisationSearchRepository, times(0)).save(aidasOrganisation);
    }

    @Test
    @Transactional
    void deleteAidasOrganisation() throws Exception {
        // Initialize the database
        aidasOrganisationRepository.saveAndFlush(aidasOrganisation);

        int databaseSizeBeforeDelete = aidasOrganisationRepository.findAll().size();

        // Delete the aidasOrganisation
        restAidasOrganisationMockMvc
            .perform(delete(ENTITY_API_URL_ID, aidasOrganisation.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AidasOrganisation> aidasOrganisationList = aidasOrganisationRepository.findAll();
        assertThat(aidasOrganisationList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AidasOrganisation in Elasticsearch
        verify(mockAidasOrganisationSearchRepository, times(1)).deleteById(aidasOrganisation.getId());
    }

    @Test
    @Transactional
    void searchAidasOrganisation() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        aidasOrganisationRepository.saveAndFlush(aidasOrganisation);
        when(mockAidasOrganisationSearchRepository.search("id:" + aidasOrganisation.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(aidasOrganisation), PageRequest.of(0, 1), 1));

        // Search the aidasOrganisation
        restAidasOrganisationMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + aidasOrganisation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasOrganisation.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}
