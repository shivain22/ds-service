package com.ainnotate.aidas.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ainnotate.aidas.IntegrationTest;
import com.ainnotate.aidas.domain.AidasCustomer;
import com.ainnotate.aidas.domain.AidasProject;
import com.ainnotate.aidas.repository.AidasProjectRepository;
import com.ainnotate.aidas.repository.search.AidasProjectSearchRepository;
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
 * Integration tests for the {@link AidasProjectResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AidasProjectResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_PROJECT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_PROJECT_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/aidas-projects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/aidas-projects";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AidasProjectRepository aidasProjectRepository;

    /**
     * This repository is mocked in the com.ainnotate.aidas.repository.search test package.
     *
     * @see com.ainnotate.aidas.repository.search.AidasProjectSearchRepositoryMockConfiguration
     */
    @Autowired
    private AidasProjectSearchRepository mockAidasProjectSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAidasProjectMockMvc;

    private AidasProject aidasProject;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasProject createEntity(EntityManager em) {
        AidasProject aidasProject = new AidasProject()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .projectType(DEFAULT_PROJECT_TYPE);
        // Add required entity
        AidasCustomer aidasCustomer;
        if (TestUtil.findAll(em, AidasCustomer.class).isEmpty()) {
            aidasCustomer = AidasCustomerResourceIT.createEntity(em);
            em.persist(aidasCustomer);
            em.flush();
        } else {
            aidasCustomer = TestUtil.findAll(em, AidasCustomer.class).get(0);
        }
        aidasProject.setAidasCustomer(aidasCustomer);
        return aidasProject;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasProject createUpdatedEntity(EntityManager em) {
        AidasProject aidasProject = new AidasProject()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .projectType(UPDATED_PROJECT_TYPE);
        // Add required entity
        AidasCustomer aidasCustomer;
        if (TestUtil.findAll(em, AidasCustomer.class).isEmpty()) {
            aidasCustomer = AidasCustomerResourceIT.createUpdatedEntity(em);
            em.persist(aidasCustomer);
            em.flush();
        } else {
            aidasCustomer = TestUtil.findAll(em, AidasCustomer.class).get(0);
        }
        aidasProject.setAidasCustomer(aidasCustomer);
        return aidasProject;
    }

    @BeforeEach
    public void initTest() {
        aidasProject = createEntity(em);
    }

    @Test
    @Transactional
    void createAidasProject() throws Exception {
        int databaseSizeBeforeCreate = aidasProjectRepository.findAll().size();
        // Create the AidasProject
        restAidasProjectMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasProject))
            )
            .andExpect(status().isCreated());

        // Validate the AidasProject in the database
        List<AidasProject> aidasProjectList = aidasProjectRepository.findAll();
        assertThat(aidasProjectList).hasSize(databaseSizeBeforeCreate + 1);
        AidasProject testAidasProject = aidasProjectList.get(aidasProjectList.size() - 1);
        assertThat(testAidasProject.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAidasProject.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAidasProject.getProjectType()).isEqualTo(DEFAULT_PROJECT_TYPE);

        // Validate the AidasProject in Elasticsearch
        verify(mockAidasProjectSearchRepository, times(1)).save(testAidasProject);
    }

    @Test
    @Transactional
    void createAidasProjectWithExistingId() throws Exception {
        // Create the AidasProject with an existing ID
        aidasProject.setId(1L);

        int databaseSizeBeforeCreate = aidasProjectRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAidasProjectMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasProject))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProject in the database
        List<AidasProject> aidasProjectList = aidasProjectRepository.findAll();
        assertThat(aidasProjectList).hasSize(databaseSizeBeforeCreate);

        // Validate the AidasProject in Elasticsearch
        verify(mockAidasProjectSearchRepository, times(0)).save(aidasProject);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = aidasProjectRepository.findAll().size();
        // set the field null
        aidasProject.setName(null);

        // Create the AidasProject, which fails.

        restAidasProjectMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasProject))
            )
            .andExpect(status().isBadRequest());

        List<AidasProject> aidasProjectList = aidasProjectRepository.findAll();
        assertThat(aidasProjectList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAidasProjects() throws Exception {
        // Initialize the database
        aidasProjectRepository.saveAndFlush(aidasProject);

        // Get all the aidasProjectList
        restAidasProjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasProject.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].projectType").value(hasItem(DEFAULT_PROJECT_TYPE)));
    }

    @Test
    @Transactional
    void getAidasProject() throws Exception {
        // Initialize the database
        aidasProjectRepository.saveAndFlush(aidasProject);

        // Get the aidasProject
        restAidasProjectMockMvc
            .perform(get(ENTITY_API_URL_ID, aidasProject.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(aidasProject.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.projectType").value(DEFAULT_PROJECT_TYPE));
    }

    @Test
    @Transactional
    void getNonExistingAidasProject() throws Exception {
        // Get the aidasProject
        restAidasProjectMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAidasProject() throws Exception {
        // Initialize the database
        aidasProjectRepository.saveAndFlush(aidasProject);

        int databaseSizeBeforeUpdate = aidasProjectRepository.findAll().size();

        // Update the aidasProject
        AidasProject updatedAidasProject = aidasProjectRepository.findById(aidasProject.getId()).get();
        // Disconnect from session so that the updates on updatedAidasProject are not directly saved in db
        em.detach(updatedAidasProject);
        updatedAidasProject.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).projectType(UPDATED_PROJECT_TYPE);

        restAidasProjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAidasProject.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAidasProject))
            )
            .andExpect(status().isOk());

        // Validate the AidasProject in the database
        List<AidasProject> aidasProjectList = aidasProjectRepository.findAll();
        assertThat(aidasProjectList).hasSize(databaseSizeBeforeUpdate);
        AidasProject testAidasProject = aidasProjectList.get(aidasProjectList.size() - 1);
        assertThat(testAidasProject.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAidasProject.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAidasProject.getProjectType()).isEqualTo(UPDATED_PROJECT_TYPE);

        // Validate the AidasProject in Elasticsearch
        verify(mockAidasProjectSearchRepository).save(testAidasProject);
    }

    @Test
    @Transactional
    void putNonExistingAidasProject() throws Exception {
        int databaseSizeBeforeUpdate = aidasProjectRepository.findAll().size();
        aidasProject.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasProjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, aidasProject.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasProject))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProject in the database
        List<AidasProject> aidasProjectList = aidasProjectRepository.findAll();
        assertThat(aidasProjectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProject in Elasticsearch
        verify(mockAidasProjectSearchRepository, times(0)).save(aidasProject);
    }

    @Test
    @Transactional
    void putWithIdMismatchAidasProject() throws Exception {
        int databaseSizeBeforeUpdate = aidasProjectRepository.findAll().size();
        aidasProject.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasProjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasProject))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProject in the database
        List<AidasProject> aidasProjectList = aidasProjectRepository.findAll();
        assertThat(aidasProjectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProject in Elasticsearch
        verify(mockAidasProjectSearchRepository, times(0)).save(aidasProject);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAidasProject() throws Exception {
        int databaseSizeBeforeUpdate = aidasProjectRepository.findAll().size();
        aidasProject.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasProjectMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasProject))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasProject in the database
        List<AidasProject> aidasProjectList = aidasProjectRepository.findAll();
        assertThat(aidasProjectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProject in Elasticsearch
        verify(mockAidasProjectSearchRepository, times(0)).save(aidasProject);
    }

    @Test
    @Transactional
    void partialUpdateAidasProjectWithPatch() throws Exception {
        // Initialize the database
        aidasProjectRepository.saveAndFlush(aidasProject);

        int databaseSizeBeforeUpdate = aidasProjectRepository.findAll().size();

        // Update the aidasProject using partial update
        AidasProject partialUpdatedAidasProject = new AidasProject();
        partialUpdatedAidasProject.setId(aidasProject.getId());

        partialUpdatedAidasProject.name(UPDATED_NAME);

        restAidasProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasProject.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasProject))
            )
            .andExpect(status().isOk());

        // Validate the AidasProject in the database
        List<AidasProject> aidasProjectList = aidasProjectRepository.findAll();
        assertThat(aidasProjectList).hasSize(databaseSizeBeforeUpdate);
        AidasProject testAidasProject = aidasProjectList.get(aidasProjectList.size() - 1);
        assertThat(testAidasProject.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAidasProject.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAidasProject.getProjectType()).isEqualTo(DEFAULT_PROJECT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateAidasProjectWithPatch() throws Exception {
        // Initialize the database
        aidasProjectRepository.saveAndFlush(aidasProject);

        int databaseSizeBeforeUpdate = aidasProjectRepository.findAll().size();

        // Update the aidasProject using partial update
        AidasProject partialUpdatedAidasProject = new AidasProject();
        partialUpdatedAidasProject.setId(aidasProject.getId());

        partialUpdatedAidasProject.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).projectType(UPDATED_PROJECT_TYPE);

        restAidasProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasProject.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasProject))
            )
            .andExpect(status().isOk());

        // Validate the AidasProject in the database
        List<AidasProject> aidasProjectList = aidasProjectRepository.findAll();
        assertThat(aidasProjectList).hasSize(databaseSizeBeforeUpdate);
        AidasProject testAidasProject = aidasProjectList.get(aidasProjectList.size() - 1);
        assertThat(testAidasProject.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAidasProject.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAidasProject.getProjectType()).isEqualTo(UPDATED_PROJECT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingAidasProject() throws Exception {
        int databaseSizeBeforeUpdate = aidasProjectRepository.findAll().size();
        aidasProject.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, aidasProject.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasProject))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProject in the database
        List<AidasProject> aidasProjectList = aidasProjectRepository.findAll();
        assertThat(aidasProjectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProject in Elasticsearch
        verify(mockAidasProjectSearchRepository, times(0)).save(aidasProject);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAidasProject() throws Exception {
        int databaseSizeBeforeUpdate = aidasProjectRepository.findAll().size();
        aidasProject.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasProject))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProject in the database
        List<AidasProject> aidasProjectList = aidasProjectRepository.findAll();
        assertThat(aidasProjectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProject in Elasticsearch
        verify(mockAidasProjectSearchRepository, times(0)).save(aidasProject);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAidasProject() throws Exception {
        int databaseSizeBeforeUpdate = aidasProjectRepository.findAll().size();
        aidasProject.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasProjectMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasProject))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasProject in the database
        List<AidasProject> aidasProjectList = aidasProjectRepository.findAll();
        assertThat(aidasProjectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProject in Elasticsearch
        verify(mockAidasProjectSearchRepository, times(0)).save(aidasProject);
    }

    @Test
    @Transactional
    void deleteAidasProject() throws Exception {
        // Initialize the database
        aidasProjectRepository.saveAndFlush(aidasProject);

        int databaseSizeBeforeDelete = aidasProjectRepository.findAll().size();

        // Delete the aidasProject
        restAidasProjectMockMvc
            .perform(delete(ENTITY_API_URL_ID, aidasProject.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AidasProject> aidasProjectList = aidasProjectRepository.findAll();
        assertThat(aidasProjectList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AidasProject in Elasticsearch
        verify(mockAidasProjectSearchRepository, times(1)).deleteById(aidasProject.getId());
    }

    @Test
    @Transactional
    void searchAidasProject() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        aidasProjectRepository.saveAndFlush(aidasProject);
        when(mockAidasProjectSearchRepository.search("id:" + aidasProject.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(aidasProject), PageRequest.of(0, 1), 1));

        // Search the aidasProject
        restAidasProjectMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + aidasProject.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasProject.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].projectType").value(hasItem(DEFAULT_PROJECT_TYPE)));
    }
}
