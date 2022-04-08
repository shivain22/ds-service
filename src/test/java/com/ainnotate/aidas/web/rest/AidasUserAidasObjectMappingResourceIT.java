package com.ainnotate.aidas.web.rest;

import static com.ainnotate.aidas.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ainnotate.aidas.IntegrationTest;
import com.ainnotate.aidas.domain.AidasObject;
import com.ainnotate.aidas.domain.AidasUser;
import com.ainnotate.aidas.domain.AidasUserAidasObjectMapping;
import com.ainnotate.aidas.repository.AidasUserAidasObjectMappingRepository;
import com.ainnotate.aidas.repository.search.AidasUserAidasObjectMappingSearchRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Integration tests for the {@link AidasUserAidasObjectMappingResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AidasUserAidasObjectMappingResourceIT {

    private static final ZonedDateTime DEFAULT_DATE_ASSIGNED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE_ASSIGNED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Integer DEFAULT_STATUS = 1;
    private static final Integer UPDATED_STATUS = 2;

    private static final String ENTITY_API_URL = "/api/aidas-user-aidas-object-mappings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/aidas-user-aidas-object-mappings";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AidasUserAidasObjectMappingRepository aidasUserAidasObjectMappingRepository;

    /**
     * This repository is mocked in the com.ainnotate.aidas.repository.search test package.
     *
     * @see com.ainnotate.aidas.repository.search.AidasUserAidasObjectMappingSearchRepositoryMockConfiguration
     */
    @Autowired
    private AidasUserAidasObjectMappingSearchRepository mockAidasUserAidasObjectMappingSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAidasUserAidasObjectMappingMockMvc;

    private AidasUserAidasObjectMapping aidasUserAidasObjectMapping;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasUserAidasObjectMapping createEntity(EntityManager em) {
        AidasUserAidasObjectMapping aidasUserAidasObjectMapping = new AidasUserAidasObjectMapping()
            .dateAssigned(DEFAULT_DATE_ASSIGNED)
            .status(DEFAULT_STATUS);
        // Add required entity
        AidasUser aidasUser;
        if (TestUtil.findAll(em, AidasUser.class).isEmpty()) {
            aidasUser = AidasUserResourceIT.createEntity(em);
            em.persist(aidasUser);
            em.flush();
        } else {
            aidasUser = TestUtil.findAll(em, AidasUser.class).get(0);
        }
        aidasUserAidasObjectMapping.setAidasUser(aidasUser);
        // Add required entity
        AidasObject aidasObject;
        if (TestUtil.findAll(em, AidasObject.class).isEmpty()) {
            aidasObject = AidasObjectResourceIT.createEntity(em);
            em.persist(aidasObject);
            em.flush();
        } else {
            aidasObject = TestUtil.findAll(em, AidasObject.class).get(0);
        }
        aidasUserAidasObjectMapping.setAidasObject(aidasObject);
        return aidasUserAidasObjectMapping;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasUserAidasObjectMapping createUpdatedEntity(EntityManager em) {
        AidasUserAidasObjectMapping aidasUserAidasObjectMapping = new AidasUserAidasObjectMapping()
            .dateAssigned(UPDATED_DATE_ASSIGNED)
            .status(UPDATED_STATUS);
        // Add required entity
        AidasUser aidasUser;
        if (TestUtil.findAll(em, AidasUser.class).isEmpty()) {
            aidasUser = AidasUserResourceIT.createUpdatedEntity(em);
            em.persist(aidasUser);
            em.flush();
        } else {
            aidasUser = TestUtil.findAll(em, AidasUser.class).get(0);
        }
        aidasUserAidasObjectMapping.setAidasUser(aidasUser);
        // Add required entity
        AidasObject aidasObject;
        if (TestUtil.findAll(em, AidasObject.class).isEmpty()) {
            aidasObject = AidasObjectResourceIT.createUpdatedEntity(em);
            em.persist(aidasObject);
            em.flush();
        } else {
            aidasObject = TestUtil.findAll(em, AidasObject.class).get(0);
        }
        aidasUserAidasObjectMapping.setAidasObject(aidasObject);
        return aidasUserAidasObjectMapping;
    }

    @BeforeEach
    public void initTest() {
        aidasUserAidasObjectMapping = createEntity(em);
    }

    @Test
    @Transactional
    void createAidasUserAidasObjectMapping() throws Exception {
        int databaseSizeBeforeCreate = aidasUserAidasObjectMappingRepository.findAll().size();
        // Create the AidasUserAidasObjectMapping
        restAidasUserAidasObjectMappingMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUserAidasObjectMapping))
            )
            .andExpect(status().isCreated());

        // Validate the AidasUserAidasObjectMapping in the database
        List<AidasUserAidasObjectMapping> aidasUserAidasObjectMappingList = aidasUserAidasObjectMappingRepository.findAll();
        assertThat(aidasUserAidasObjectMappingList).hasSize(databaseSizeBeforeCreate + 1);
        AidasUserAidasObjectMapping testAidasUserAidasObjectMapping = aidasUserAidasObjectMappingList.get(
            aidasUserAidasObjectMappingList.size() - 1
        );
        assertThat(testAidasUserAidasObjectMapping.getDateAssigned()).isEqualTo(DEFAULT_DATE_ASSIGNED);
        assertThat(testAidasUserAidasObjectMapping.getStatus()).isEqualTo(DEFAULT_STATUS);

        // Validate the AidasUserAidasObjectMapping in Elasticsearch
        verify(mockAidasUserAidasObjectMappingSearchRepository, times(1)).save(testAidasUserAidasObjectMapping);
    }

    @Test
    @Transactional
    void createAidasUserAidasObjectMappingWithExistingId() throws Exception {
        // Create the AidasUserAidasObjectMapping with an existing ID
        aidasUserAidasObjectMapping.setId(1L);

        int databaseSizeBeforeCreate = aidasUserAidasObjectMappingRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAidasUserAidasObjectMappingMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUserAidasObjectMapping))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUserAidasObjectMapping in the database
        List<AidasUserAidasObjectMapping> aidasUserAidasObjectMappingList = aidasUserAidasObjectMappingRepository.findAll();
        assertThat(aidasUserAidasObjectMappingList).hasSize(databaseSizeBeforeCreate);

        // Validate the AidasUserAidasObjectMapping in Elasticsearch
        verify(mockAidasUserAidasObjectMappingSearchRepository, times(0)).save(aidasUserAidasObjectMapping);
    }

    @Test
    @Transactional
    void getAllAidasUserAidasObjectMappings() throws Exception {
        // Initialize the database
        aidasUserAidasObjectMappingRepository.saveAndFlush(aidasUserAidasObjectMapping);

        // Get all the aidasUserAidasObjectMappingList
        restAidasUserAidasObjectMappingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasUserAidasObjectMapping.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateAssigned").value(hasItem(sameInstant(DEFAULT_DATE_ASSIGNED))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @Test
    @Transactional
    void getAidasUserAidasObjectMapping() throws Exception {
        // Initialize the database
        aidasUserAidasObjectMappingRepository.saveAndFlush(aidasUserAidasObjectMapping);

        // Get the aidasUserAidasObjectMapping
        restAidasUserAidasObjectMappingMockMvc
            .perform(get(ENTITY_API_URL_ID, aidasUserAidasObjectMapping.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(aidasUserAidasObjectMapping.getId().intValue()))
            .andExpect(jsonPath("$.dateAssigned").value(sameInstant(DEFAULT_DATE_ASSIGNED)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    void getNonExistingAidasUserAidasObjectMapping() throws Exception {
        // Get the aidasUserAidasObjectMapping
        restAidasUserAidasObjectMappingMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAidasUserAidasObjectMapping() throws Exception {
        // Initialize the database
        aidasUserAidasObjectMappingRepository.saveAndFlush(aidasUserAidasObjectMapping);

        int databaseSizeBeforeUpdate = aidasUserAidasObjectMappingRepository.findAll().size();

        // Update the aidasUserAidasObjectMapping
        AidasUserAidasObjectMapping updatedAidasUserAidasObjectMapping = aidasUserAidasObjectMappingRepository
            .findById(aidasUserAidasObjectMapping.getId())
            .get();
        // Disconnect from session so that the updates on updatedAidasUserAidasObjectMapping are not directly saved in db
        em.detach(updatedAidasUserAidasObjectMapping);
        updatedAidasUserAidasObjectMapping.dateAssigned(UPDATED_DATE_ASSIGNED).status(UPDATED_STATUS);

        restAidasUserAidasObjectMappingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAidasUserAidasObjectMapping.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAidasUserAidasObjectMapping))
            )
            .andExpect(status().isOk());

        // Validate the AidasUserAidasObjectMapping in the database
        List<AidasUserAidasObjectMapping> aidasUserAidasObjectMappingList = aidasUserAidasObjectMappingRepository.findAll();
        assertThat(aidasUserAidasObjectMappingList).hasSize(databaseSizeBeforeUpdate);
        AidasUserAidasObjectMapping testAidasUserAidasObjectMapping = aidasUserAidasObjectMappingList.get(
            aidasUserAidasObjectMappingList.size() - 1
        );
        assertThat(testAidasUserAidasObjectMapping.getDateAssigned()).isEqualTo(UPDATED_DATE_ASSIGNED);
        assertThat(testAidasUserAidasObjectMapping.getStatus()).isEqualTo(UPDATED_STATUS);

        // Validate the AidasUserAidasObjectMapping in Elasticsearch
        verify(mockAidasUserAidasObjectMappingSearchRepository).save(testAidasUserAidasObjectMapping);
    }

    @Test
    @Transactional
    void putNonExistingAidasUserAidasObjectMapping() throws Exception {
        int databaseSizeBeforeUpdate = aidasUserAidasObjectMappingRepository.findAll().size();
        aidasUserAidasObjectMapping.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasUserAidasObjectMappingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, aidasUserAidasObjectMapping.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUserAidasObjectMapping))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUserAidasObjectMapping in the database
        List<AidasUserAidasObjectMapping> aidasUserAidasObjectMappingList = aidasUserAidasObjectMappingRepository.findAll();
        assertThat(aidasUserAidasObjectMappingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUserAidasObjectMapping in Elasticsearch
        verify(mockAidasUserAidasObjectMappingSearchRepository, times(0)).save(aidasUserAidasObjectMapping);
    }

    @Test
    @Transactional
    void putWithIdMismatchAidasUserAidasObjectMapping() throws Exception {
        int databaseSizeBeforeUpdate = aidasUserAidasObjectMappingRepository.findAll().size();
        aidasUserAidasObjectMapping.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUserAidasObjectMappingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUserAidasObjectMapping))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUserAidasObjectMapping in the database
        List<AidasUserAidasObjectMapping> aidasUserAidasObjectMappingList = aidasUserAidasObjectMappingRepository.findAll();
        assertThat(aidasUserAidasObjectMappingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUserAidasObjectMapping in Elasticsearch
        verify(mockAidasUserAidasObjectMappingSearchRepository, times(0)).save(aidasUserAidasObjectMapping);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAidasUserAidasObjectMapping() throws Exception {
        int databaseSizeBeforeUpdate = aidasUserAidasObjectMappingRepository.findAll().size();
        aidasUserAidasObjectMapping.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUserAidasObjectMappingMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUserAidasObjectMapping))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasUserAidasObjectMapping in the database
        List<AidasUserAidasObjectMapping> aidasUserAidasObjectMappingList = aidasUserAidasObjectMappingRepository.findAll();
        assertThat(aidasUserAidasObjectMappingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUserAidasObjectMapping in Elasticsearch
        verify(mockAidasUserAidasObjectMappingSearchRepository, times(0)).save(aidasUserAidasObjectMapping);
    }

    @Test
    @Transactional
    void partialUpdateAidasUserAidasObjectMappingWithPatch() throws Exception {
        // Initialize the database
        aidasUserAidasObjectMappingRepository.saveAndFlush(aidasUserAidasObjectMapping);

        int databaseSizeBeforeUpdate = aidasUserAidasObjectMappingRepository.findAll().size();

        // Update the aidasUserAidasObjectMapping using partial update
        AidasUserAidasObjectMapping partialUpdatedAidasUserAidasObjectMapping = new AidasUserAidasObjectMapping();
        partialUpdatedAidasUserAidasObjectMapping.setId(aidasUserAidasObjectMapping.getId());

        partialUpdatedAidasUserAidasObjectMapping.dateAssigned(UPDATED_DATE_ASSIGNED);

        restAidasUserAidasObjectMappingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasUserAidasObjectMapping.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasUserAidasObjectMapping))
            )
            .andExpect(status().isOk());

        // Validate the AidasUserAidasObjectMapping in the database
        List<AidasUserAidasObjectMapping> aidasUserAidasObjectMappingList = aidasUserAidasObjectMappingRepository.findAll();
        assertThat(aidasUserAidasObjectMappingList).hasSize(databaseSizeBeforeUpdate);
        AidasUserAidasObjectMapping testAidasUserAidasObjectMapping = aidasUserAidasObjectMappingList.get(
            aidasUserAidasObjectMappingList.size() - 1
        );
        assertThat(testAidasUserAidasObjectMapping.getDateAssigned()).isEqualTo(UPDATED_DATE_ASSIGNED);
        assertThat(testAidasUserAidasObjectMapping.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateAidasUserAidasObjectMappingWithPatch() throws Exception {
        // Initialize the database
        aidasUserAidasObjectMappingRepository.saveAndFlush(aidasUserAidasObjectMapping);

        int databaseSizeBeforeUpdate = aidasUserAidasObjectMappingRepository.findAll().size();

        // Update the aidasUserAidasObjectMapping using partial update
        AidasUserAidasObjectMapping partialUpdatedAidasUserAidasObjectMapping = new AidasUserAidasObjectMapping();
        partialUpdatedAidasUserAidasObjectMapping.setId(aidasUserAidasObjectMapping.getId());

        partialUpdatedAidasUserAidasObjectMapping.dateAssigned(UPDATED_DATE_ASSIGNED).status(UPDATED_STATUS);

        restAidasUserAidasObjectMappingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasUserAidasObjectMapping.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasUserAidasObjectMapping))
            )
            .andExpect(status().isOk());

        // Validate the AidasUserAidasObjectMapping in the database
        List<AidasUserAidasObjectMapping> aidasUserAidasObjectMappingList = aidasUserAidasObjectMappingRepository.findAll();
        assertThat(aidasUserAidasObjectMappingList).hasSize(databaseSizeBeforeUpdate);
        AidasUserAidasObjectMapping testAidasUserAidasObjectMapping = aidasUserAidasObjectMappingList.get(
            aidasUserAidasObjectMappingList.size() - 1
        );
        assertThat(testAidasUserAidasObjectMapping.getDateAssigned()).isEqualTo(UPDATED_DATE_ASSIGNED);
        assertThat(testAidasUserAidasObjectMapping.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingAidasUserAidasObjectMapping() throws Exception {
        int databaseSizeBeforeUpdate = aidasUserAidasObjectMappingRepository.findAll().size();
        aidasUserAidasObjectMapping.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasUserAidasObjectMappingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, aidasUserAidasObjectMapping.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasUserAidasObjectMapping))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUserAidasObjectMapping in the database
        List<AidasUserAidasObjectMapping> aidasUserAidasObjectMappingList = aidasUserAidasObjectMappingRepository.findAll();
        assertThat(aidasUserAidasObjectMappingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUserAidasObjectMapping in Elasticsearch
        verify(mockAidasUserAidasObjectMappingSearchRepository, times(0)).save(aidasUserAidasObjectMapping);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAidasUserAidasObjectMapping() throws Exception {
        int databaseSizeBeforeUpdate = aidasUserAidasObjectMappingRepository.findAll().size();
        aidasUserAidasObjectMapping.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUserAidasObjectMappingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasUserAidasObjectMapping))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUserAidasObjectMapping in the database
        List<AidasUserAidasObjectMapping> aidasUserAidasObjectMappingList = aidasUserAidasObjectMappingRepository.findAll();
        assertThat(aidasUserAidasObjectMappingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUserAidasObjectMapping in Elasticsearch
        verify(mockAidasUserAidasObjectMappingSearchRepository, times(0)).save(aidasUserAidasObjectMapping);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAidasUserAidasObjectMapping() throws Exception {
        int databaseSizeBeforeUpdate = aidasUserAidasObjectMappingRepository.findAll().size();
        aidasUserAidasObjectMapping.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUserAidasObjectMappingMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasUserAidasObjectMapping))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasUserAidasObjectMapping in the database
        List<AidasUserAidasObjectMapping> aidasUserAidasObjectMappingList = aidasUserAidasObjectMappingRepository.findAll();
        assertThat(aidasUserAidasObjectMappingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUserAidasObjectMapping in Elasticsearch
        verify(mockAidasUserAidasObjectMappingSearchRepository, times(0)).save(aidasUserAidasObjectMapping);
    }

    @Test
    @Transactional
    void deleteAidasUserAidasObjectMapping() throws Exception {
        // Initialize the database
        aidasUserAidasObjectMappingRepository.saveAndFlush(aidasUserAidasObjectMapping);

        int databaseSizeBeforeDelete = aidasUserAidasObjectMappingRepository.findAll().size();

        // Delete the aidasUserAidasObjectMapping
        restAidasUserAidasObjectMappingMockMvc
            .perform(delete(ENTITY_API_URL_ID, aidasUserAidasObjectMapping.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AidasUserAidasObjectMapping> aidasUserAidasObjectMappingList = aidasUserAidasObjectMappingRepository.findAll();
        assertThat(aidasUserAidasObjectMappingList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AidasUserAidasObjectMapping in Elasticsearch
        verify(mockAidasUserAidasObjectMappingSearchRepository, times(1)).deleteById(aidasUserAidasObjectMapping.getId());
    }

    @Test
    @Transactional
    void searchAidasUserAidasObjectMapping() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        aidasUserAidasObjectMappingRepository.saveAndFlush(aidasUserAidasObjectMapping);
        when(mockAidasUserAidasObjectMappingSearchRepository.search("id:" + aidasUserAidasObjectMapping.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(aidasUserAidasObjectMapping), PageRequest.of(0, 1), 1));

        // Search the aidasUserAidasObjectMapping
        restAidasUserAidasObjectMappingMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + aidasUserAidasObjectMapping.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasUserAidasObjectMapping.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateAssigned").value(hasItem(sameInstant(DEFAULT_DATE_ASSIGNED))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }
}
