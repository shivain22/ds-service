package com.ainnotate.aidas.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ainnotate.aidas.IntegrationTest;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.domain.Project;
import com.ainnotate.aidas.repository.ObjectRepository;
import com.ainnotate.aidas.repository.search.ObjectSearchRepository;
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
 * Integration tests for the {@link ObjectResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ObjectResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_NUMBER_OF_UPLOAD_REQD = 1;
    private static final Integer UPDATED_NUMBER_OF_UPLOAD_REQD = 2;

    private static final String ENTITY_API_URL = "/api/aidas-objects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/aidas-objects";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectRepository objectRepository;

    /**
     * This repository is mocked in the com.ainnotate.aidas.repository.search test package.
     *
     * @see com.ainnotate.aidas.repository.search.ObjectSearchRepositoryMockConfiguration
     */
    @Autowired
    private ObjectSearchRepository mockObjectSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAidasObjectMockMvc;

    private Object object;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Object createEntity(EntityManager em) {
        Object object = new Object()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .numberOfUploadReqd(DEFAULT_NUMBER_OF_UPLOAD_REQD);
        // Add required entity
        Project project;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            project = ProjectResourceIT.createEntity(em);
            em.persist(project);
            em.flush();
        } else {
            project = TestUtil.findAll(em, Project.class).get(0);
        }
        object.setProject(project);
        return object;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Object createUpdatedEntity(EntityManager em) {
        Object object = new Object()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .numberOfUploadReqd(UPDATED_NUMBER_OF_UPLOAD_REQD);
        // Add required entity
        Project project;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            project = ProjectResourceIT.createUpdatedEntity(em);
            em.persist(project);
            em.flush();
        } else {
            project = TestUtil.findAll(em, Project.class).get(0);
        }
        object.setProject(project);
        return object;
    }

    @BeforeEach
    public void initTest() {
        object = createEntity(em);
    }

    @Test
    @Transactional
    void createAidasObject() throws Exception {
        int databaseSizeBeforeCreate = objectRepository.findAll().size();
        // Create the AidasObject
        restAidasObjectMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(object))
            )
            .andExpect(status().isCreated());

        // Validate the AidasObject in the database
        List<Object> objectList = objectRepository.findAll();
        assertThat(objectList).hasSize(databaseSizeBeforeCreate + 1);
        Object testObject = objectList.get(objectList.size() - 1);
        assertThat(testObject.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testObject.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testObject.getNumberOfUploadReqd()).isEqualTo(DEFAULT_NUMBER_OF_UPLOAD_REQD);

        // Validate the AidasObject in Elasticsearch
        verify(mockObjectSearchRepository, times(1)).save(testObject);
    }

    @Test
    @Transactional
    void createAidasObjectWithExistingId() throws Exception {
        // Create the AidasObject with an existing ID
        object.setId(1L);

        int databaseSizeBeforeCreate = objectRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAidasObjectMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(object))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasObject in the database
        List<Object> objectList = objectRepository.findAll();
        assertThat(objectList).hasSize(databaseSizeBeforeCreate);

        // Validate the AidasObject in Elasticsearch
        verify(mockObjectSearchRepository, times(0)).save(object);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = objectRepository.findAll().size();
        // set the field null
        object.setName(null);

        // Create the AidasObject, which fails.

        restAidasObjectMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(object))
            )
            .andExpect(status().isBadRequest());

        List<Object> objectList = objectRepository.findAll();
        assertThat(objectList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNumberOfUploadReqdIsRequired() throws Exception {
        int databaseSizeBeforeTest = objectRepository.findAll().size();
        // set the field null
        object.setNumberOfUploadReqd(null);

        // Create the AidasObject, which fails.

        restAidasObjectMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(object))
            )
            .andExpect(status().isBadRequest());

        List<Object> objectList = objectRepository.findAll();
        assertThat(objectList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAidasObjects() throws Exception {
        // Initialize the database
        objectRepository.saveAndFlush(object);

        // Get all the aidasObjectList
        restAidasObjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(object.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].numberOfUploadReqd").value(hasItem(DEFAULT_NUMBER_OF_UPLOAD_REQD)));
    }

    @Test
    @Transactional
    void getObject() throws Exception {
        // Initialize the database
        objectRepository.saveAndFlush(object);

        // Get the object
        restAidasObjectMockMvc
            .perform(get(ENTITY_API_URL_ID, object.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(object.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.numberOfUploadReqd").value(DEFAULT_NUMBER_OF_UPLOAD_REQD));
    }

    @Test
    @Transactional
    void getNonExistingAidasObject() throws Exception {
        // Get the object
        restAidasObjectMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAidasObject() throws Exception {
        // Initialize the database
        objectRepository.saveAndFlush(object);

        int databaseSizeBeforeUpdate = objectRepository.findAll().size();

        // Update the object
        Object updatedObject = objectRepository.findById(object.getId()).get();
        // Disconnect from session so that the updates on updatedAidasObject are not directly saved in db
        em.detach(updatedObject);
        updatedObject.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).numberOfUploadReqd(UPDATED_NUMBER_OF_UPLOAD_REQD);

        restAidasObjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedObject.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedObject))
            )
            .andExpect(status().isOk());

        // Validate the AidasObject in the database
        List<Object> objectList = objectRepository.findAll();
        assertThat(objectList).hasSize(databaseSizeBeforeUpdate);
        Object testObject = objectList.get(objectList.size() - 1);
        assertThat(testObject.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testObject.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testObject.getNumberOfUploadReqd()).isEqualTo(UPDATED_NUMBER_OF_UPLOAD_REQD);

        // Validate the AidasObject in Elasticsearch
        verify(mockObjectSearchRepository).save(testObject);
    }

    @Test
    @Transactional
    void putNonExistingAidasObject() throws Exception {
        int databaseSizeBeforeUpdate = objectRepository.findAll().size();
        object.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasObjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, object.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(object))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasObject in the database
        List<Object> objectList = objectRepository.findAll();
        assertThat(objectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObject in Elasticsearch
        verify(mockObjectSearchRepository, times(0)).save(object);
    }

    @Test
    @Transactional
    void putWithIdMismatchAidasObject() throws Exception {
        int databaseSizeBeforeUpdate = objectRepository.findAll().size();
        object.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasObjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(object))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasObject in the database
        List<Object> objectList = objectRepository.findAll();
        assertThat(objectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObject in Elasticsearch
        verify(mockObjectSearchRepository, times(0)).save(object);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAidasObject() throws Exception {
        int databaseSizeBeforeUpdate = objectRepository.findAll().size();
        object.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasObjectMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(object))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasObject in the database
        List<Object> objectList = objectRepository.findAll();
        assertThat(objectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObject in Elasticsearch
        verify(mockObjectSearchRepository, times(0)).save(object);
    }

    @Test
    @Transactional
    void partialUpdateAidasObjectWithPatch() throws Exception {
        // Initialize the database
        objectRepository.saveAndFlush(object);

        int databaseSizeBeforeUpdate = objectRepository.findAll().size();

        // Update the object using partial update
        Object partialUpdatedObject = new Object();
        partialUpdatedObject.setId(object.getId());

        partialUpdatedObject.description(UPDATED_DESCRIPTION);

        restAidasObjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedObject.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedObject))
            )
            .andExpect(status().isOk());

        // Validate the AidasObject in the database
        List<Object> objectList = objectRepository.findAll();
        assertThat(objectList).hasSize(databaseSizeBeforeUpdate);
        Object testObject = objectList.get(objectList.size() - 1);
        assertThat(testObject.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testObject.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testObject.getNumberOfUploadReqd()).isEqualTo(DEFAULT_NUMBER_OF_UPLOAD_REQD);
    }

    @Test
    @Transactional
    void fullUpdateAidasObjectWithPatch() throws Exception {
        // Initialize the database
        objectRepository.saveAndFlush(object);

        int databaseSizeBeforeUpdate = objectRepository.findAll().size();

        // Update the object using partial update
        Object partialUpdatedObject = new Object();
        partialUpdatedObject.setId(object.getId());

        partialUpdatedObject.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).numberOfUploadReqd(UPDATED_NUMBER_OF_UPLOAD_REQD);

        restAidasObjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedObject.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedObject))
            )
            .andExpect(status().isOk());

        // Validate the AidasObject in the database
        List<Object> objectList = objectRepository.findAll();
        assertThat(objectList).hasSize(databaseSizeBeforeUpdate);
        Object testObject = objectList.get(objectList.size() - 1);
        assertThat(testObject.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testObject.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testObject.getNumberOfUploadReqd()).isEqualTo(UPDATED_NUMBER_OF_UPLOAD_REQD);
    }

    @Test
    @Transactional
    void patchNonExistingAidasObject() throws Exception {
        int databaseSizeBeforeUpdate = objectRepository.findAll().size();
        object.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasObjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, object.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(object))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasObject in the database
        List<Object> objectList = objectRepository.findAll();
        assertThat(objectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObject in Elasticsearch
        verify(mockObjectSearchRepository, times(0)).save(object);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAidasObject() throws Exception {
        int databaseSizeBeforeUpdate = objectRepository.findAll().size();
        object.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasObjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(object))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasObject in the database
        List<Object> objectList = objectRepository.findAll();
        assertThat(objectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObject in Elasticsearch
        verify(mockObjectSearchRepository, times(0)).save(object);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAidasObject() throws Exception {
        int databaseSizeBeforeUpdate = objectRepository.findAll().size();
        object.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasObjectMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(object))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasObject in the database
        List<Object> objectList = objectRepository.findAll();
        assertThat(objectList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObject in Elasticsearch
        verify(mockObjectSearchRepository, times(0)).save(object);
    }

    @Test
    @Transactional
    void deleteAidasObject() throws Exception {
        // Initialize the database
        objectRepository.saveAndFlush(object);

        int databaseSizeBeforeDelete = objectRepository.findAll().size();

        // Delete the object
        restAidasObjectMockMvc
            .perform(delete(ENTITY_API_URL_ID, object.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Object> objectList = objectRepository.findAll();
        assertThat(objectList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AidasObject in Elasticsearch
        verify(mockObjectSearchRepository, times(1)).deleteById(object.getId());
    }

    @Test
    @Transactional
    void searchAidasObject() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        objectRepository.saveAndFlush(object);
        when(mockObjectSearchRepository.search("id:" + object.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(object), PageRequest.of(0, 1), 1));

        // Search the object
        restAidasObjectMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + object.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(object.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].numberOfUploadReqd").value(hasItem(DEFAULT_NUMBER_OF_UPLOAD_REQD)));
    }
}
