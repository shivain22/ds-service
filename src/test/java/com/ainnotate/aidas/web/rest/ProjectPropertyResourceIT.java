package com.ainnotate.aidas.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ainnotate.aidas.IntegrationTest;
import com.ainnotate.aidas.domain.Project;
import com.ainnotate.aidas.domain.ProjectProperty;
import com.ainnotate.aidas.repository.ProjectPropertyRepository;
import com.ainnotate.aidas.repository.search.AidasProjectPropertySearchRepository;
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
 * Integration tests for the {@link ProjectPropertyResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProjectPropertyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/aidas-project-property";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/aidas-project-property";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProjectPropertyRepository projectPropertyRepository;

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

    private ProjectProperty projectProperty;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectProperty createEntity(EntityManager em) {
        ProjectProperty projectProperty = new ProjectProperty()
            .value(DEFAULT_VALUE);
        // Add required entity
        Project project;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            project = ProjectResourceIT.createEntity(em);
            em.persist(project);
            em.flush();
        } else {
            project = TestUtil.findAll(em, Project.class).get(0);
        }
        projectProperty.setProject(project);
        return projectProperty;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectProperty createUpdatedEntity(EntityManager em) {
        ProjectProperty projectProperty = new ProjectProperty()
            .value(UPDATED_VALUE);
        // Add required entity
        Project project;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            project = ProjectResourceIT.createUpdatedEntity(em);
            em.persist(project);
            em.flush();
        } else {
            project = TestUtil.findAll(em, Project.class).get(0);
        }
        projectProperty.setProject(project);
        return projectProperty;
    }

    @BeforeEach
    public void initTest() {
        projectProperty = createEntity(em);
    }

    @Test
    @Transactional
    void createAidasProjectProperty() throws Exception {
        int databaseSizeBeforeCreate = projectPropertyRepository.findAll().size();
        // Create the AidasProjectProperty
        restAidasProjectPropertyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectProperty))
            )
            .andExpect(status().isCreated());

        // Validate the AidasProjectProperty in the database
        List<ProjectProperty> projectPropertyList = projectPropertyRepository.findAll();
        assertThat(projectPropertyList).hasSize(databaseSizeBeforeCreate + 1);
        ProjectProperty testProjectProperty = projectPropertyList.get(projectPropertyList.size() - 1);
        assertThat(testProjectProperty.getValue()).isEqualTo(DEFAULT_VALUE);

        // Validate the AidasProjectProperty in Elasticsearch
        verify(mockAidasProjectPropertySearchRepository, times(1)).save(testProjectProperty);
    }

    @Test
    @Transactional
    void createAidasProjectPropertyWithExistingId() throws Exception {
        // Create the AidasProjectProperty with an existing ID
        projectProperty.setId(1L);

        int databaseSizeBeforeCreate = projectPropertyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAidasProjectPropertyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectProperty))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProjectProperty in the database
        List<ProjectProperty> projectPropertyList = projectPropertyRepository.findAll();
        assertThat(projectPropertyList).hasSize(databaseSizeBeforeCreate);

        // Validate the AidasProjectProperty in Elasticsearch
        verify(mockAidasProjectPropertySearchRepository, times(0)).save(projectProperty);
    }

    @Test
    @Transactional
    void getAllAidasProjectProperties() throws Exception {
        // Initialize the database
        projectPropertyRepository.saveAndFlush(projectProperty);

        // Get all the aidasProjectPropertyList
        restAidasProjectPropertyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectProperty.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }

    @Test
    @Transactional
    void getProjectProperty() throws Exception {
        // Initialize the database
        projectPropertyRepository.saveAndFlush(projectProperty);

        // Get the projectProperty
        restAidasProjectPropertyMockMvc
            .perform(get(ENTITY_API_URL_ID, projectProperty.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(projectProperty.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE));
    }

    @Test
    @Transactional
    void getNonExistingAidasProjectProperty() throws Exception {
        // Get the projectProperty
        restAidasProjectPropertyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAidasProjectProperty() throws Exception {
        // Initialize the database
        projectPropertyRepository.saveAndFlush(projectProperty);

        int databaseSizeBeforeUpdate = projectPropertyRepository.findAll().size();

        // Update the projectProperty
        ProjectProperty updatedProjectProperty = projectPropertyRepository.findById(projectProperty.getId()).get();
        // Disconnect from session so that the updates on updatedAidasProjectProperty are not directly saved in db
        em.detach(updatedProjectProperty);


        restAidasProjectPropertyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProjectProperty.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedProjectProperty))
            )
            .andExpect(status().isOk());

        // Validate the AidasProjectProperty in the database
        List<ProjectProperty> projectPropertyList = projectPropertyRepository.findAll();
        assertThat(projectPropertyList).hasSize(databaseSizeBeforeUpdate);
        ProjectProperty testProjectProperty = projectPropertyList.get(projectPropertyList.size() - 1);

        assertThat(testProjectProperty.getValue()).isEqualTo(UPDATED_VALUE);

        // Validate the AidasProjectProperty in Elasticsearch
        verify(mockAidasProjectPropertySearchRepository).save(testProjectProperty);
    }

    @Test
    @Transactional
    void putNonExistingAidasProjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = projectPropertyRepository.findAll().size();
        projectProperty.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasProjectPropertyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectProperty.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectProperty))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProjectProperty in the database
        List<ProjectProperty> projectPropertyList = projectPropertyRepository.findAll();
        assertThat(projectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProjectProperty in Elasticsearch
        verify(mockAidasProjectPropertySearchRepository, times(0)).save(projectProperty);
    }

    @Test
    @Transactional
    void putWithIdMismatchAidasProjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = projectPropertyRepository.findAll().size();
        projectProperty.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasProjectPropertyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectProperty))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProjectProperty in the database
        List<ProjectProperty> projectPropertyList = projectPropertyRepository.findAll();
        assertThat(projectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProjectProperty in Elasticsearch
        verify(mockAidasProjectPropertySearchRepository, times(0)).save(projectProperty);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAidasProjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = projectPropertyRepository.findAll().size();
        projectProperty.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasProjectPropertyMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectProperty))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasProjectProperty in the database
        List<ProjectProperty> projectPropertyList = projectPropertyRepository.findAll();
        assertThat(projectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProjectProperty in Elasticsearch
        verify(mockAidasProjectPropertySearchRepository, times(0)).save(projectProperty);
    }

    @Test
    @Transactional
    void partialUpdateAidasProjectPropertyWithPatch() throws Exception {
        // Initialize the database
        projectPropertyRepository.saveAndFlush(projectProperty);

        int databaseSizeBeforeUpdate = projectPropertyRepository.findAll().size();

        // Update the projectProperty using partial update
        ProjectProperty partialUpdatedProjectProperty = new ProjectProperty();
        partialUpdatedProjectProperty.setId(projectProperty.getId());



        restAidasProjectPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectProperty.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectProperty))
            )
            .andExpect(status().isOk());

        // Validate the AidasProjectProperty in the database
        List<ProjectProperty> projectPropertyList = projectPropertyRepository.findAll();
        assertThat(projectPropertyList).hasSize(databaseSizeBeforeUpdate);
        ProjectProperty testProjectProperty = projectPropertyList.get(projectPropertyList.size() - 1);

        assertThat(testProjectProperty.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    void fullUpdateAidasProjectPropertyWithPatch() throws Exception {
        // Initialize the database
        projectPropertyRepository.saveAndFlush(projectProperty);

        int databaseSizeBeforeUpdate = projectPropertyRepository.findAll().size();

        // Update the projectProperty using partial update
        ProjectProperty partialUpdatedProjectProperty = new ProjectProperty();
        partialUpdatedProjectProperty.setId(projectProperty.getId());



        restAidasProjectPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectProperty.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectProperty))
            )
            .andExpect(status().isOk());

        // Validate the AidasProjectProperty in the database
        List<ProjectProperty> projectPropertyList = projectPropertyRepository.findAll();
        assertThat(projectPropertyList).hasSize(databaseSizeBeforeUpdate);
        ProjectProperty testProjectProperty = projectPropertyList.get(projectPropertyList.size() - 1);

        assertThat(testProjectProperty.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    void patchNonExistingAidasProjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = projectPropertyRepository.findAll().size();
        projectProperty.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasProjectPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, projectProperty.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projectProperty))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProjectProperty in the database
        List<ProjectProperty> projectPropertyList = projectPropertyRepository.findAll();
        assertThat(projectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProjectProperty in Elasticsearch
        verify(mockAidasProjectPropertySearchRepository, times(0)).save(projectProperty);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAidasProjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = projectPropertyRepository.findAll().size();
        projectProperty.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasProjectPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projectProperty))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasProjectProperty in the database
        List<ProjectProperty> projectPropertyList = projectPropertyRepository.findAll();
        assertThat(projectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProjectProperty in Elasticsearch
        verify(mockAidasProjectPropertySearchRepository, times(0)).save(projectProperty);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAidasProjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = projectPropertyRepository.findAll().size();
        projectProperty.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasProjectPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projectProperty))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasProjectProperty in the database
        List<ProjectProperty> projectPropertyList = projectPropertyRepository.findAll();
        assertThat(projectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasProjectProperty in Elasticsearch
        verify(mockAidasProjectPropertySearchRepository, times(0)).save(projectProperty);
    }

    @Test
    @Transactional
    void deleteAidasProjectProperty() throws Exception {
        // Initialize the database
        projectPropertyRepository.saveAndFlush(projectProperty);

        int databaseSizeBeforeDelete = projectPropertyRepository.findAll().size();

        // Delete the projectProperty
        restAidasProjectPropertyMockMvc
            .perform(delete(ENTITY_API_URL_ID, projectProperty.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProjectProperty> projectPropertyList = projectPropertyRepository.findAll();
        assertThat(projectPropertyList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AidasProjectProperty in Elasticsearch
        verify(mockAidasProjectPropertySearchRepository, times(1)).deleteById(projectProperty.getId());
    }

    @Test
    @Transactional
    void searchAidasProjectProperty() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        projectPropertyRepository.saveAndFlush(projectProperty);
        when(mockAidasProjectPropertySearchRepository.search("id:" + projectProperty.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(projectProperty), PageRequest.of(0, 1), 1));

        // Search the projectProperty
        restAidasProjectPropertyMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + projectProperty.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectProperty.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }
}
