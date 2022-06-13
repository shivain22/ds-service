package com.ainnotate.aidas.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ainnotate.aidas.IntegrationTest;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.domain.ObjectProperty;
import com.ainnotate.aidas.domain.Property;
import com.ainnotate.aidas.repository.ObjectPropertyRepository;
import com.ainnotate.aidas.repository.search.AidasObjectPropertySearchRepository;
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
 * Integration tests for the {@link ObjectPropertyResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ObjectPropertyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/aidas-object-property";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/aidas-object-property";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectPropertyRepository objectPropertyRepository;

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

    private ObjectProperty objectProperty;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ObjectProperty createEntity(EntityManager em) {
        ObjectProperty objectProperty = new ObjectProperty()
            .value(DEFAULT_VALUE);
        // Add required entity
        Object object;
        if (TestUtil.findAll(em, Object.class).isEmpty()) {
            object = ObjectResourceIT.createEntity(em);
            em.persist(object);
            em.flush();
        } else {
            object = TestUtil.findAll(em, Object.class).get(0);
        }
        objectProperty.setObject(object);
        // Add required entity
        Property property;
        if (TestUtil.findAll(em, Property.class).isEmpty()) {
            property = PropertyResourceIT.createEntity(em);
            em.persist(property);
            em.flush();
        } else {
            property = TestUtil.findAll(em, Property.class).get(0);
        }
        objectProperty.setProperty(property);
        return objectProperty;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ObjectProperty createUpdatedEntity(EntityManager em) {
        ObjectProperty objectProperty = new ObjectProperty()
            .value(UPDATED_VALUE);
        // Add required entity
        Object object;
        if (TestUtil.findAll(em, Object.class).isEmpty()) {
            object = ObjectResourceIT.createUpdatedEntity(em);
            em.persist(object);
            em.flush();
        } else {
            object = TestUtil.findAll(em, Object.class).get(0);
        }
        objectProperty.setObject(object);
        // Add required entity
        Property property;
        if (TestUtil.findAll(em, Property.class).isEmpty()) {
            property = PropertyResourceIT.createUpdatedEntity(em);
            em.persist(property);
            em.flush();
        } else {
            property = TestUtil.findAll(em, Property.class).get(0);
        }
        objectProperty.setProperty(property);
        return objectProperty;
    }

    @BeforeEach
    public void initTest() {
        objectProperty = createEntity(em);
    }

    @Test
    @Transactional
    void createAidasObjectProperty() throws Exception {
        int databaseSizeBeforeCreate = objectPropertyRepository.findAll().size();
        // Create the AidasObjectProperty
        restAidasObjectPropertyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(objectProperty))
            )
            .andExpect(status().isCreated());

        // Validate the AidasObjectProperty in the database
        List<ObjectProperty> objectPropertyList = objectPropertyRepository.findAll();
        assertThat(objectPropertyList).hasSize(databaseSizeBeforeCreate + 1);
        ObjectProperty testObjectProperty = objectPropertyList.get(objectPropertyList.size() - 1);
        assertThat(testObjectProperty.getValue()).isEqualTo(DEFAULT_VALUE);

        // Validate the AidasObjectProperty in Elasticsearch
        verify(mockAidasObjectPropertySearchRepository, times(1)).save(testObjectProperty);
    }

    @Test
    @Transactional
    void createAidasObjectPropertyWithExistingId() throws Exception {
        // Create the AidasObjectProperty with an existing ID
        objectProperty.setId(1L);

        int databaseSizeBeforeCreate = objectPropertyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAidasObjectPropertyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(objectProperty))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasObjectProperty in the database
        List<ObjectProperty> objectPropertyList = objectPropertyRepository.findAll();
        assertThat(objectPropertyList).hasSize(databaseSizeBeforeCreate);

        // Validate the AidasObjectProperty in Elasticsearch
        verify(mockAidasObjectPropertySearchRepository, times(0)).save(objectProperty);
    }

    @Test
    @Transactional
    void getAllAidasObjectProperties() throws Exception {
        // Initialize the database
        objectPropertyRepository.saveAndFlush(objectProperty);

        // Get all the aidasObjectPropertyList
        restAidasObjectPropertyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(objectProperty.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }

    @Test
    @Transactional
    void getObjectProperty() throws Exception {
        // Initialize the database
        objectPropertyRepository.saveAndFlush(objectProperty);

        // Get the objectProperty
        restAidasObjectPropertyMockMvc
            .perform(get(ENTITY_API_URL_ID, objectProperty.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(objectProperty.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE));
    }

    @Test
    @Transactional
    void getNonExistingAidasObjectProperty() throws Exception {
        // Get the objectProperty
        restAidasObjectPropertyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAidasObjectProperty() throws Exception {
        // Initialize the database
        objectPropertyRepository.saveAndFlush(objectProperty);

        int databaseSizeBeforeUpdate = objectPropertyRepository.findAll().size();

        // Update the objectProperty
        ObjectProperty updatedObjectProperty = objectPropertyRepository.findById(objectProperty.getId()).get();
        // Disconnect from session so that the updates on updatedAidasObjectProperty are not directly saved in db
        em.detach(updatedObjectProperty);

        restAidasObjectPropertyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedObjectProperty.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedObjectProperty))
            )
            .andExpect(status().isOk());

        // Validate the AidasObjectProperty in the database
        List<ObjectProperty> objectPropertyList = objectPropertyRepository.findAll();
        assertThat(objectPropertyList).hasSize(databaseSizeBeforeUpdate);
        ObjectProperty testObjectProperty = objectPropertyList.get(objectPropertyList.size() - 1);
        assertThat(testObjectProperty.getValue()).isEqualTo(UPDATED_VALUE);

        // Validate the AidasObjectProperty in Elasticsearch
        verify(mockAidasObjectPropertySearchRepository).save(testObjectProperty);
    }

    @Test
    @Transactional
    void putNonExistingAidasObjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = objectPropertyRepository.findAll().size();
        objectProperty.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasObjectPropertyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, objectProperty.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(objectProperty))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasObjectProperty in the database
        List<ObjectProperty> objectPropertyList = objectPropertyRepository.findAll();
        assertThat(objectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObjectProperty in Elasticsearch
        verify(mockAidasObjectPropertySearchRepository, times(0)).save(objectProperty);
    }

    @Test
    @Transactional
    void putWithIdMismatchAidasObjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = objectPropertyRepository.findAll().size();
        objectProperty.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasObjectPropertyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(objectProperty))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasObjectProperty in the database
        List<ObjectProperty> objectPropertyList = objectPropertyRepository.findAll();
        assertThat(objectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObjectProperty in Elasticsearch
        verify(mockAidasObjectPropertySearchRepository, times(0)).save(objectProperty);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAidasObjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = objectPropertyRepository.findAll().size();
        objectProperty.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasObjectPropertyMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(objectProperty))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasObjectProperty in the database
        List<ObjectProperty> objectPropertyList = objectPropertyRepository.findAll();
        assertThat(objectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObjectProperty in Elasticsearch
        verify(mockAidasObjectPropertySearchRepository, times(0)).save(objectProperty);
    }

    @Test
    @Transactional
    void partialUpdateAidasObjectPropertyWithPatch() throws Exception {
        // Initialize the database
        objectPropertyRepository.saveAndFlush(objectProperty);

        int databaseSizeBeforeUpdate = objectPropertyRepository.findAll().size();

        // Update the objectProperty using partial update
        ObjectProperty partialUpdatedObjectProperty = new ObjectProperty();
        partialUpdatedObjectProperty.setId(objectProperty.getId());

        partialUpdatedObjectProperty.value(UPDATED_VALUE);

        restAidasObjectPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedObjectProperty.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedObjectProperty))
            )
            .andExpect(status().isOk());

        // Validate the AidasObjectProperty in the database
        List<ObjectProperty> objectPropertyList = objectPropertyRepository.findAll();
        assertThat(objectPropertyList).hasSize(databaseSizeBeforeUpdate);
        ObjectProperty testObjectProperty = objectPropertyList.get(objectPropertyList.size() - 1);
        assertThat(testObjectProperty.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    void fullUpdateAidasObjectPropertyWithPatch() throws Exception {
        // Initialize the database
        objectPropertyRepository.saveAndFlush(objectProperty);

        int databaseSizeBeforeUpdate = objectPropertyRepository.findAll().size();

        // Update the objectProperty using partial update
        ObjectProperty partialUpdatedObjectProperty = new ObjectProperty();
        partialUpdatedObjectProperty.setId(objectProperty.getId());


        restAidasObjectPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedObjectProperty.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedObjectProperty))
            )
            .andExpect(status().isOk());

        // Validate the AidasObjectProperty in the database
        List<ObjectProperty> objectPropertyList = objectPropertyRepository.findAll();
        assertThat(objectPropertyList).hasSize(databaseSizeBeforeUpdate);
        ObjectProperty testObjectProperty = objectPropertyList.get(objectPropertyList.size() - 1);

        assertThat(testObjectProperty.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    void patchNonExistingAidasObjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = objectPropertyRepository.findAll().size();
        objectProperty.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasObjectPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, objectProperty.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(objectProperty))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasObjectProperty in the database
        List<ObjectProperty> objectPropertyList = objectPropertyRepository.findAll();
        assertThat(objectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObjectProperty in Elasticsearch
        verify(mockAidasObjectPropertySearchRepository, times(0)).save(objectProperty);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAidasObjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = objectPropertyRepository.findAll().size();
        objectProperty.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasObjectPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(objectProperty))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasObjectProperty in the database
        List<ObjectProperty> objectPropertyList = objectPropertyRepository.findAll();
        assertThat(objectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObjectProperty in Elasticsearch
        verify(mockAidasObjectPropertySearchRepository, times(0)).save(objectProperty);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAidasObjectProperty() throws Exception {
        int databaseSizeBeforeUpdate = objectPropertyRepository.findAll().size();
        objectProperty.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasObjectPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(objectProperty))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasObjectProperty in the database
        List<ObjectProperty> objectPropertyList = objectPropertyRepository.findAll();
        assertThat(objectPropertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasObjectProperty in Elasticsearch
        verify(mockAidasObjectPropertySearchRepository, times(0)).save(objectProperty);
    }

    @Test
    @Transactional
    void deleteAidasObjectProperty() throws Exception {
        // Initialize the database
        objectPropertyRepository.saveAndFlush(objectProperty);

        int databaseSizeBeforeDelete = objectPropertyRepository.findAll().size();

        // Delete the objectProperty
        restAidasObjectPropertyMockMvc
            .perform(delete(ENTITY_API_URL_ID, objectProperty.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ObjectProperty> objectPropertyList = objectPropertyRepository.findAll();
        assertThat(objectPropertyList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AidasObjectProperty in Elasticsearch
        verify(mockAidasObjectPropertySearchRepository, times(1)).deleteById(objectProperty.getId());
    }

    @Test
    @Transactional
    void searchAidasObjectProperty() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        objectPropertyRepository.saveAndFlush(objectProperty);
        when(mockAidasObjectPropertySearchRepository.search("id:" + objectProperty.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(objectProperty), PageRequest.of(0, 1), 1));

        // Search the objectProperty
        restAidasObjectPropertyMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + objectProperty.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(objectProperty.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }
}
