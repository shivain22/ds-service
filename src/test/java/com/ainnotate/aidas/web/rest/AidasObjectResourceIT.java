package com.ainnotate.aidas.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ainnotate.aidas.IntegrationTest;
import com.ainnotate.aidas.domain.AidasObject;
import com.ainnotate.aidas.domain.AidasProject;
import com.ainnotate.aidas.repository.AidasObjectRepository;
import com.ainnotate.aidas.repository.search.AidasObjectSearchRepository;
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
 * Integration tests for the {@link AidasObjectResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AidasObjectResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/aidas-objects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/aidas-objects";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AidasObjectRepository aidasObjectRepository;

    /**
     * This repository is mocked in the com.ainnotate.aidas.repository.search test package.
     *
     * @see com.ainnotate.aidas.repository.search.AidasObjectSearchRepositoryMockConfiguration
     */
    @Autowired
    private AidasObjectSearchRepository mockAidasObjectSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAidasObjectMockMvc;

    private AidasObject aidasObject;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasObject createEntity(EntityManager em) {
        AidasObject aidasObject = new AidasObject().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        // Add required entity
        AidasProject aidasProject;
        if (TestUtil.findAll(em, AidasProject.class).isEmpty()) {
            aidasProject = AidasProjectResourceIT.createEntity(em);
            em.persist(aidasProject);
            em.flush();
        } else {
            aidasProject = TestUtil.findAll(em, AidasProject.class).get(0);
        }
        aidasObject.setAidasProject(aidasProject);
        return aidasObject;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasObject createUpdatedEntity(EntityManager em) {
        AidasObject aidasObject = new AidasObject().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        // Add required entity
        AidasProject aidasProject;
        if (TestUtil.findAll(em, AidasProject.class).isEmpty()) {
            aidasProject = AidasProjectResourceIT.createUpdatedEntity(em);
            em.persist(aidasProject);
            em.flush();
        } else {
            aidasProject = TestUtil.findAll(em, AidasProject.class).get(0);
        }
        aidasObject.setAidasProject(aidasProject);
        return aidasObject;
    }

    @BeforeEach
    public void initTest() {
        aidasObject = createEntity(em);
    }

    @Test
    @Transactional
    void createAidasObject() throws Exception {
        int databaseSizeBeforeCreate = aidasObjectRepository.findAll().size();
        // Create the AidasObject
        restAidasObjectMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasObject))
            )
            .andExpect(status().isCreated());

        // Validate the AidasObject in the database
        List<AidasObject> aidasObjectList = aidasObjectRepository.findAll();
        assertThat(aidasObjectList).hasSize(databaseSizeBeforeCreate + 1);
        AidasObject testAidasObject = aidasObjectList.get(aidasObjectList.size() - 1);
        assertThat(testAidasObject.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAidasObject.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the AidasObject in Elasticsearch
        verify(mockAidasObjectSearchRepository, times(1)).save(testAidasObject);
    }

    @Test
    @Transactional
    void createAidasObjectWithExistingId() throws Exception {
        // Create the AidasObject with an existing ID
        aidasObject.setId(1L);

        int databaseSizeBeforeCreate = aidasObjectRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAidasObjectMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasObject))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasObject in the database
        List<AidasObject> aidasObjectList = aidasObjectRepository.findAll();
        assertThat(aidasObjectList).hasSize(databaseSizeBeforeCreate);

        // Validate the AidasObject in Elasticsearch
        verify(mockAidasObjectSearchRepository, times(0)).save(aidasObject);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = aidasObjectRepository.findAll().size();
        // set the field null
        aidasObject.setName(null);

        // Create the AidasObject, which fails.

        restAidasObjectMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasObject))
            )
            .andExpect(status().isBadRequest());

        List<AidasObject> aidasObjectList = aidasObjectRepository.findAll();
        assertThat(aidasObjectList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAidasObjects() throws Exception {
        // Initialize the database
        aidasObjectRepository.saveAndFlush(aidasObject);

        // Get all the aidasObjectList
        restAidasObjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasObject.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getAidasObject() throws Exception {
        // Initialize the database
        aidasObjectRepository.saveAndFlush(aidasObject);

        // Get the aidasObject
        restAidasObjectMockMvc
            .perform(get(ENTITY_API_URL_ID, aidasObject.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(aidasObject.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingAidasObject() throws Exception {
        // Get the aidasObject
        restAidasObjectMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAidasObject() throws Exception {
        // Initialize the database
        aidasObjectRepository.saveAndFlush(aidasObject);

        int databaseSizeBeforeUpdate = aidasObjectRepository.findAll().size();

        // Update the aidasObject
        AidasObject updatedAidasObject = aidasObjectRepository.findById(aidasObject.getId()).get();
        // Disconnect from session so that the updates on updatedAidasObject are not directly saved in db
        em.detach(updatedAidasObject);
        updatedAidasObject.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restAidasObjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAidasObject.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAidasObject))
            )
            .andExpect(status().isOk());

        // Validate the AidasObject in the database
        List<AidasObject> aidasObjectList = aidasObjectRepository.findAll();
        assertThat(aidasObjectList).hasSize(databaseSizeBeforeUpdate);
        AidasObject testAidasObject = aidasObjectList.get(aidasObjectList.size() - 1);
        assertThat(testAidasObject.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAidasObject.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the AidasObject in Elasticsearch
        verify(mockAidasObjectSearchRepository).save(testAidasObject);
    }

    @Test
    @Transactional
    void putNonExistingAidasObject() throws Exception {
        int databaseSizeBeforeUpdate = aidasObjectRepository.findAll().size();
        aidasObject.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasObjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, aidasObject.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasObject))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasObject in the database
        List<AidasObject> aidasObjectList = aidasObjectRepository.findAll();
        assertThat(aidasObjectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObject in Elasticsearch
        verify(mockAidasObjectSearchRepository, times(0)).save(aidasObject);
    }

    @Test
    @Transactional
    void putWithIdMismatchAidasObject() throws Exception {
        int databaseSizeBeforeUpdate = aidasObjectRepository.findAll().size();
        aidasObject.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasObjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasObject))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasObject in the database
        List<AidasObject> aidasObjectList = aidasObjectRepository.findAll();
        assertThat(aidasObjectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObject in Elasticsearch
        verify(mockAidasObjectSearchRepository, times(0)).save(aidasObject);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAidasObject() throws Exception {
        int databaseSizeBeforeUpdate = aidasObjectRepository.findAll().size();
        aidasObject.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasObjectMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasObject))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasObject in the database
        List<AidasObject> aidasObjectList = aidasObjectRepository.findAll();
        assertThat(aidasObjectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObject in Elasticsearch
        verify(mockAidasObjectSearchRepository, times(0)).save(aidasObject);
    }

    @Test
    @Transactional
    void partialUpdateAidasObjectWithPatch() throws Exception {
        // Initialize the database
        aidasObjectRepository.saveAndFlush(aidasObject);

        int databaseSizeBeforeUpdate = aidasObjectRepository.findAll().size();

        // Update the aidasObject using partial update
        AidasObject partialUpdatedAidasObject = new AidasObject();
        partialUpdatedAidasObject.setId(aidasObject.getId());

        partialUpdatedAidasObject.description(UPDATED_DESCRIPTION);

        restAidasObjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasObject.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasObject))
            )
            .andExpect(status().isOk());

        // Validate the AidasObject in the database
        List<AidasObject> aidasObjectList = aidasObjectRepository.findAll();
        assertThat(aidasObjectList).hasSize(databaseSizeBeforeUpdate);
        AidasObject testAidasObject = aidasObjectList.get(aidasObjectList.size() - 1);
        assertThat(testAidasObject.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAidasObject.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateAidasObjectWithPatch() throws Exception {
        // Initialize the database
        aidasObjectRepository.saveAndFlush(aidasObject);

        int databaseSizeBeforeUpdate = aidasObjectRepository.findAll().size();

        // Update the aidasObject using partial update
        AidasObject partialUpdatedAidasObject = new AidasObject();
        partialUpdatedAidasObject.setId(aidasObject.getId());

        partialUpdatedAidasObject.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restAidasObjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasObject.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasObject))
            )
            .andExpect(status().isOk());

        // Validate the AidasObject in the database
        List<AidasObject> aidasObjectList = aidasObjectRepository.findAll();
        assertThat(aidasObjectList).hasSize(databaseSizeBeforeUpdate);
        AidasObject testAidasObject = aidasObjectList.get(aidasObjectList.size() - 1);
        assertThat(testAidasObject.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAidasObject.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingAidasObject() throws Exception {
        int databaseSizeBeforeUpdate = aidasObjectRepository.findAll().size();
        aidasObject.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasObjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, aidasObject.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasObject))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasObject in the database
        List<AidasObject> aidasObjectList = aidasObjectRepository.findAll();
        assertThat(aidasObjectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObject in Elasticsearch
        verify(mockAidasObjectSearchRepository, times(0)).save(aidasObject);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAidasObject() throws Exception {
        int databaseSizeBeforeUpdate = aidasObjectRepository.findAll().size();
        aidasObject.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasObjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasObject))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasObject in the database
        List<AidasObject> aidasObjectList = aidasObjectRepository.findAll();
        assertThat(aidasObjectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObject in Elasticsearch
        verify(mockAidasObjectSearchRepository, times(0)).save(aidasObject);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAidasObject() throws Exception {
        int databaseSizeBeforeUpdate = aidasObjectRepository.findAll().size();
        aidasObject.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasObjectMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasObject))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasObject in the database
        List<AidasObject> aidasObjectList = aidasObjectRepository.findAll();
        assertThat(aidasObjectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObject in Elasticsearch
        verify(mockAidasObjectSearchRepository, times(0)).save(aidasObject);
    }

    @Test
    @Transactional
    void deleteAidasObject() throws Exception {
        // Initialize the database
        aidasObjectRepository.saveAndFlush(aidasObject);

        int databaseSizeBeforeDelete = aidasObjectRepository.findAll().size();

        // Delete the aidasObject
        restAidasObjectMockMvc
            .perform(delete(ENTITY_API_URL_ID, aidasObject.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AidasObject> aidasObjectList = aidasObjectRepository.findAll();
        assertThat(aidasObjectList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AidasObject in Elasticsearch
        verify(mockAidasObjectSearchRepository, times(1)).deleteById(aidasObject.getId());
    }

    @Test
    @Transactional
    void searchAidasObject() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        aidasObjectRepository.saveAndFlush(aidasObject);
        when(mockAidasObjectSearchRepository.search("id:" + aidasObject.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(aidasObject), PageRequest.of(0, 1), 1));

        // Search the aidasObject
        restAidasObjectMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + aidasObject.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasObject.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}
