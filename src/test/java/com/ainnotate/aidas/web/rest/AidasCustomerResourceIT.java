package com.ainnotate.aidas.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ainnotate.aidas.IntegrationTest;
import com.ainnotate.aidas.domain.AidasCustomer;
import com.ainnotate.aidas.domain.AidasOrganisation;
import com.ainnotate.aidas.repository.AidasCustomerRepository;
import com.ainnotate.aidas.repository.search.AidasCustomerSearchRepository;
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
 * Integration tests for the {@link AidasCustomerResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AidasCustomerResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/aidas-customers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/aidas-customers";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AidasCustomerRepository aidasCustomerRepository;

    /**
     * This repository is mocked in the com.ainnotate.aidas.repository.search test package.
     *
     * @see com.ainnotate.aidas.repository.search.AidasCustomerSearchRepositoryMockConfiguration
     */
    @Autowired
    private AidasCustomerSearchRepository mockAidasCustomerSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAidasCustomerMockMvc;

    private AidasCustomer aidasCustomer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasCustomer createEntity(EntityManager em) {
        AidasCustomer aidasCustomer = new AidasCustomer().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        // Add required entity
        AidasOrganisation aidasOrganisation;
        if (TestUtil.findAll(em, AidasOrganisation.class).isEmpty()) {
            aidasOrganisation = AidasOrganisationResourceIT.createEntity(em);
            em.persist(aidasOrganisation);
            em.flush();
        } else {
            aidasOrganisation = TestUtil.findAll(em, AidasOrganisation.class).get(0);
        }
        aidasCustomer.setAidasOrganisation(aidasOrganisation);
        return aidasCustomer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasCustomer createUpdatedEntity(EntityManager em) {
        AidasCustomer aidasCustomer = new AidasCustomer().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        // Add required entity
        AidasOrganisation aidasOrganisation;
        if (TestUtil.findAll(em, AidasOrganisation.class).isEmpty()) {
            aidasOrganisation = AidasOrganisationResourceIT.createUpdatedEntity(em);
            em.persist(aidasOrganisation);
            em.flush();
        } else {
            aidasOrganisation = TestUtil.findAll(em, AidasOrganisation.class).get(0);
        }
        aidasCustomer.setAidasOrganisation(aidasOrganisation);
        return aidasCustomer;
    }

    @BeforeEach
    public void initTest() {
        aidasCustomer = createEntity(em);
    }

    @Test
    @Transactional
    void createAidasCustomer() throws Exception {
        int databaseSizeBeforeCreate = aidasCustomerRepository.findAll().size();
        // Create the AidasCustomer
        restAidasCustomerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasCustomer))
            )
            .andExpect(status().isCreated());

        // Validate the AidasCustomer in the database
        List<AidasCustomer> aidasCustomerList = aidasCustomerRepository.findAll();
        assertThat(aidasCustomerList).hasSize(databaseSizeBeforeCreate + 1);
        AidasCustomer testAidasCustomer = aidasCustomerList.get(aidasCustomerList.size() - 1);
        assertThat(testAidasCustomer.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAidasCustomer.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the AidasCustomer in Elasticsearch
        verify(mockAidasCustomerSearchRepository, times(1)).save(testAidasCustomer);
    }

    @Test
    @Transactional
    void createAidasCustomerWithExistingId() throws Exception {
        // Create the AidasCustomer with an existing ID
        aidasCustomer.setId(1L);

        int databaseSizeBeforeCreate = aidasCustomerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAidasCustomerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasCustomer))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasCustomer in the database
        List<AidasCustomer> aidasCustomerList = aidasCustomerRepository.findAll();
        assertThat(aidasCustomerList).hasSize(databaseSizeBeforeCreate);

        // Validate the AidasCustomer in Elasticsearch
        verify(mockAidasCustomerSearchRepository, times(0)).save(aidasCustomer);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = aidasCustomerRepository.findAll().size();
        // set the field null
        aidasCustomer.setName(null);

        // Create the AidasCustomer, which fails.

        restAidasCustomerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasCustomer))
            )
            .andExpect(status().isBadRequest());

        List<AidasCustomer> aidasCustomerList = aidasCustomerRepository.findAll();
        assertThat(aidasCustomerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAidasCustomers() throws Exception {
        // Initialize the database
        aidasCustomerRepository.saveAndFlush(aidasCustomer);

        // Get all the aidasCustomerList
        restAidasCustomerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasCustomer.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getAidasCustomer() throws Exception {
        // Initialize the database
        aidasCustomerRepository.saveAndFlush(aidasCustomer);

        // Get the aidasCustomer
        restAidasCustomerMockMvc
            .perform(get(ENTITY_API_URL_ID, aidasCustomer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(aidasCustomer.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingAidasCustomer() throws Exception {
        // Get the aidasCustomer
        restAidasCustomerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAidasCustomer() throws Exception {
        // Initialize the database
        aidasCustomerRepository.saveAndFlush(aidasCustomer);

        int databaseSizeBeforeUpdate = aidasCustomerRepository.findAll().size();

        // Update the aidasCustomer
        AidasCustomer updatedAidasCustomer = aidasCustomerRepository.findById(aidasCustomer.getId()).get();
        // Disconnect from session so that the updates on updatedAidasCustomer are not directly saved in db
        em.detach(updatedAidasCustomer);
        updatedAidasCustomer.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restAidasCustomerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAidasCustomer.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAidasCustomer))
            )
            .andExpect(status().isOk());

        // Validate the AidasCustomer in the database
        List<AidasCustomer> aidasCustomerList = aidasCustomerRepository.findAll();
        assertThat(aidasCustomerList).hasSize(databaseSizeBeforeUpdate);
        AidasCustomer testAidasCustomer = aidasCustomerList.get(aidasCustomerList.size() - 1);
        assertThat(testAidasCustomer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAidasCustomer.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the AidasCustomer in Elasticsearch
        verify(mockAidasCustomerSearchRepository).save(testAidasCustomer);
    }

    @Test
    @Transactional
    void putNonExistingAidasCustomer() throws Exception {
        int databaseSizeBeforeUpdate = aidasCustomerRepository.findAll().size();
        aidasCustomer.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasCustomerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, aidasCustomer.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasCustomer))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasCustomer in the database
        List<AidasCustomer> aidasCustomerList = aidasCustomerRepository.findAll();
        assertThat(aidasCustomerList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasCustomer in Elasticsearch
        verify(mockAidasCustomerSearchRepository, times(0)).save(aidasCustomer);
    }

    @Test
    @Transactional
    void putWithIdMismatchAidasCustomer() throws Exception {
        int databaseSizeBeforeUpdate = aidasCustomerRepository.findAll().size();
        aidasCustomer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasCustomerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasCustomer))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasCustomer in the database
        List<AidasCustomer> aidasCustomerList = aidasCustomerRepository.findAll();
        assertThat(aidasCustomerList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasCustomer in Elasticsearch
        verify(mockAidasCustomerSearchRepository, times(0)).save(aidasCustomer);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAidasCustomer() throws Exception {
        int databaseSizeBeforeUpdate = aidasCustomerRepository.findAll().size();
        aidasCustomer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasCustomerMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasCustomer))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasCustomer in the database
        List<AidasCustomer> aidasCustomerList = aidasCustomerRepository.findAll();
        assertThat(aidasCustomerList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasCustomer in Elasticsearch
        verify(mockAidasCustomerSearchRepository, times(0)).save(aidasCustomer);
    }

    @Test
    @Transactional
    void partialUpdateAidasCustomerWithPatch() throws Exception {
        // Initialize the database
        aidasCustomerRepository.saveAndFlush(aidasCustomer);

        int databaseSizeBeforeUpdate = aidasCustomerRepository.findAll().size();

        // Update the aidasCustomer using partial update
        AidasCustomer partialUpdatedAidasCustomer = new AidasCustomer();
        partialUpdatedAidasCustomer.setId(aidasCustomer.getId());

        partialUpdatedAidasCustomer.description(UPDATED_DESCRIPTION);

        restAidasCustomerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasCustomer.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasCustomer))
            )
            .andExpect(status().isOk());

        // Validate the AidasCustomer in the database
        List<AidasCustomer> aidasCustomerList = aidasCustomerRepository.findAll();
        assertThat(aidasCustomerList).hasSize(databaseSizeBeforeUpdate);
        AidasCustomer testAidasCustomer = aidasCustomerList.get(aidasCustomerList.size() - 1);
        assertThat(testAidasCustomer.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAidasCustomer.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateAidasCustomerWithPatch() throws Exception {
        // Initialize the database
        aidasCustomerRepository.saveAndFlush(aidasCustomer);

        int databaseSizeBeforeUpdate = aidasCustomerRepository.findAll().size();

        // Update the aidasCustomer using partial update
        AidasCustomer partialUpdatedAidasCustomer = new AidasCustomer();
        partialUpdatedAidasCustomer.setId(aidasCustomer.getId());

        partialUpdatedAidasCustomer.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restAidasCustomerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasCustomer.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasCustomer))
            )
            .andExpect(status().isOk());

        // Validate the AidasCustomer in the database
        List<AidasCustomer> aidasCustomerList = aidasCustomerRepository.findAll();
        assertThat(aidasCustomerList).hasSize(databaseSizeBeforeUpdate);
        AidasCustomer testAidasCustomer = aidasCustomerList.get(aidasCustomerList.size() - 1);
        assertThat(testAidasCustomer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAidasCustomer.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingAidasCustomer() throws Exception {
        int databaseSizeBeforeUpdate = aidasCustomerRepository.findAll().size();
        aidasCustomer.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasCustomerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, aidasCustomer.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasCustomer))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasCustomer in the database
        List<AidasCustomer> aidasCustomerList = aidasCustomerRepository.findAll();
        assertThat(aidasCustomerList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasCustomer in Elasticsearch
        verify(mockAidasCustomerSearchRepository, times(0)).save(aidasCustomer);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAidasCustomer() throws Exception {
        int databaseSizeBeforeUpdate = aidasCustomerRepository.findAll().size();
        aidasCustomer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasCustomerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasCustomer))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasCustomer in the database
        List<AidasCustomer> aidasCustomerList = aidasCustomerRepository.findAll();
        assertThat(aidasCustomerList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasCustomer in Elasticsearch
        verify(mockAidasCustomerSearchRepository, times(0)).save(aidasCustomer);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAidasCustomer() throws Exception {
        int databaseSizeBeforeUpdate = aidasCustomerRepository.findAll().size();
        aidasCustomer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasCustomerMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasCustomer))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasCustomer in the database
        List<AidasCustomer> aidasCustomerList = aidasCustomerRepository.findAll();
        assertThat(aidasCustomerList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasCustomer in Elasticsearch
        verify(mockAidasCustomerSearchRepository, times(0)).save(aidasCustomer);
    }

    @Test
    @Transactional
    void deleteAidasCustomer() throws Exception {
        // Initialize the database
        aidasCustomerRepository.saveAndFlush(aidasCustomer);

        int databaseSizeBeforeDelete = aidasCustomerRepository.findAll().size();

        // Delete the aidasCustomer
        restAidasCustomerMockMvc
            .perform(delete(ENTITY_API_URL_ID, aidasCustomer.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AidasCustomer> aidasCustomerList = aidasCustomerRepository.findAll();
        assertThat(aidasCustomerList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AidasCustomer in Elasticsearch
        verify(mockAidasCustomerSearchRepository, times(1)).deleteById(aidasCustomer.getId());
    }

    @Test
    @Transactional
    void searchAidasCustomer() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        aidasCustomerRepository.saveAndFlush(aidasCustomer);
        when(mockAidasCustomerSearchRepository.search("id:" + aidasCustomer.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(aidasCustomer), PageRequest.of(0, 1), 1));

        // Search the aidasCustomer
        restAidasCustomerMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + aidasCustomer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasCustomer.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}
