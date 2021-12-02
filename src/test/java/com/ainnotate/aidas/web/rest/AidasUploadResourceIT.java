package com.ainnotate.aidas.web.rest;

import static com.ainnotate.aidas.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ainnotate.aidas.IntegrationTest;
import com.ainnotate.aidas.domain.AidasUpload;
import com.ainnotate.aidas.domain.AidasUserAidasObjectMapping;
import com.ainnotate.aidas.repository.AidasUploadRepository;
import com.ainnotate.aidas.repository.search.AidasUploadSearchRepository;
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
 * Integration tests for the {@link AidasUploadResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AidasUploadResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_DATE_UPLOADED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE_UPLOADED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final ZonedDateTime DEFAULT_STATUS_MODIFIED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_STATUS_MODIFIED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_REJECT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REJECT_REASON = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/aidas-uploads";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/aidas-uploads";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AidasUploadRepository aidasUploadRepository;

    /**
     * This repository is mocked in the com.ainnotate.aidas.repository.search test package.
     *
     * @see com.ainnotate.aidas.repository.search.AidasUploadSearchRepositoryMockConfiguration
     */
    @Autowired
    private AidasUploadSearchRepository mockAidasUploadSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAidasUploadMockMvc;

    private AidasUpload aidasUpload;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasUpload createEntity(EntityManager em) {
        AidasUpload aidasUpload = new AidasUpload()
            .name(DEFAULT_NAME)
            .dateUploaded(DEFAULT_DATE_UPLOADED)
            .status(DEFAULT_STATUS)
            .statusModifiedDate(DEFAULT_STATUS_MODIFIED_DATE)
            .rejectReason(DEFAULT_REJECT_REASON);
        // Add required entity
        AidasUserAidasObjectMapping aidasUserAidasObjectMapping;
        if (TestUtil.findAll(em, AidasUserAidasObjectMapping.class).isEmpty()) {
            aidasUserAidasObjectMapping = AidasUserAidasObjectMappingResourceIT.createEntity(em);
            em.persist(aidasUserAidasObjectMapping);
            em.flush();
        } else {
            aidasUserAidasObjectMapping = TestUtil.findAll(em, AidasUserAidasObjectMapping.class).get(0);
        }
        aidasUpload.setAidasUserAidasObjectMapping(aidasUserAidasObjectMapping);
        return aidasUpload;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasUpload createUpdatedEntity(EntityManager em) {
        AidasUpload aidasUpload = new AidasUpload()
            .name(UPDATED_NAME)
            .dateUploaded(UPDATED_DATE_UPLOADED)
            .status(UPDATED_STATUS)
            .statusModifiedDate(UPDATED_STATUS_MODIFIED_DATE)
            .rejectReason(UPDATED_REJECT_REASON);
        // Add required entity
        AidasUserAidasObjectMapping aidasUserAidasObjectMapping;
        if (TestUtil.findAll(em, AidasUserAidasObjectMapping.class).isEmpty()) {
            aidasUserAidasObjectMapping = AidasUserAidasObjectMappingResourceIT.createUpdatedEntity(em);
            em.persist(aidasUserAidasObjectMapping);
            em.flush();
        } else {
            aidasUserAidasObjectMapping = TestUtil.findAll(em, AidasUserAidasObjectMapping.class).get(0);
        }
        aidasUpload.setAidasUserAidasObjectMapping(aidasUserAidasObjectMapping);
        return aidasUpload;
    }

    @BeforeEach
    public void initTest() {
        aidasUpload = createEntity(em);
    }

    @Test
    @Transactional
    void createAidasUpload() throws Exception {
        int databaseSizeBeforeCreate = aidasUploadRepository.findAll().size();
        // Create the AidasUpload
        restAidasUploadMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUpload))
            )
            .andExpect(status().isCreated());

        // Validate the AidasUpload in the database
        List<AidasUpload> aidasUploadList = aidasUploadRepository.findAll();
        assertThat(aidasUploadList).hasSize(databaseSizeBeforeCreate + 1);
        AidasUpload testAidasUpload = aidasUploadList.get(aidasUploadList.size() - 1);
        assertThat(testAidasUpload.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAidasUpload.getDateUploaded()).isEqualTo(DEFAULT_DATE_UPLOADED);
        assertThat(testAidasUpload.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testAidasUpload.getStatusModifiedDate()).isEqualTo(DEFAULT_STATUS_MODIFIED_DATE);
        assertThat(testAidasUpload.getRejectReason()).isEqualTo(DEFAULT_REJECT_REASON);

        // Validate the AidasUpload in Elasticsearch
        verify(mockAidasUploadSearchRepository, times(1)).save(testAidasUpload);
    }

    @Test
    @Transactional
    void createAidasUploadWithExistingId() throws Exception {
        // Create the AidasUpload with an existing ID
        aidasUpload.setId(1L);

        int databaseSizeBeforeCreate = aidasUploadRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAidasUploadMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUpload))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUpload in the database
        List<AidasUpload> aidasUploadList = aidasUploadRepository.findAll();
        assertThat(aidasUploadList).hasSize(databaseSizeBeforeCreate);

        // Validate the AidasUpload in Elasticsearch
        verify(mockAidasUploadSearchRepository, times(0)).save(aidasUpload);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = aidasUploadRepository.findAll().size();
        // set the field null
        aidasUpload.setName(null);

        // Create the AidasUpload, which fails.

        restAidasUploadMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUpload))
            )
            .andExpect(status().isBadRequest());

        List<AidasUpload> aidasUploadList = aidasUploadRepository.findAll();
        assertThat(aidasUploadList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAidasUploads() throws Exception {
        // Initialize the database
        aidasUploadRepository.saveAndFlush(aidasUpload);

        // Get all the aidasUploadList
        restAidasUploadMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasUpload.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].dateUploaded").value(hasItem(sameInstant(DEFAULT_DATE_UPLOADED))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())))
            .andExpect(jsonPath("$.[*].statusModifiedDate").value(hasItem(sameInstant(DEFAULT_STATUS_MODIFIED_DATE))))
            .andExpect(jsonPath("$.[*].rejectReason").value(hasItem(DEFAULT_REJECT_REASON)));
    }

    @Test
    @Transactional
    void getAidasUpload() throws Exception {
        // Initialize the database
        aidasUploadRepository.saveAndFlush(aidasUpload);

        // Get the aidasUpload
        restAidasUploadMockMvc
            .perform(get(ENTITY_API_URL_ID, aidasUpload.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(aidasUpload.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.dateUploaded").value(sameInstant(DEFAULT_DATE_UPLOADED)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.booleanValue()))
            .andExpect(jsonPath("$.statusModifiedDate").value(sameInstant(DEFAULT_STATUS_MODIFIED_DATE)))
            .andExpect(jsonPath("$.rejectReason").value(DEFAULT_REJECT_REASON));
    }

    @Test
    @Transactional
    void getNonExistingAidasUpload() throws Exception {
        // Get the aidasUpload
        restAidasUploadMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAidasUpload() throws Exception {
        // Initialize the database
        aidasUploadRepository.saveAndFlush(aidasUpload);

        int databaseSizeBeforeUpdate = aidasUploadRepository.findAll().size();

        // Update the aidasUpload
        AidasUpload updatedAidasUpload = aidasUploadRepository.findById(aidasUpload.getId()).get();
        // Disconnect from session so that the updates on updatedAidasUpload are not directly saved in db
        em.detach(updatedAidasUpload);
        updatedAidasUpload
            .name(UPDATED_NAME)
            .dateUploaded(UPDATED_DATE_UPLOADED)
            .status(UPDATED_STATUS)
            .statusModifiedDate(UPDATED_STATUS_MODIFIED_DATE)
            .rejectReason(UPDATED_REJECT_REASON);

        restAidasUploadMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAidasUpload.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAidasUpload))
            )
            .andExpect(status().isOk());

        // Validate the AidasUpload in the database
        List<AidasUpload> aidasUploadList = aidasUploadRepository.findAll();
        assertThat(aidasUploadList).hasSize(databaseSizeBeforeUpdate);
        AidasUpload testAidasUpload = aidasUploadList.get(aidasUploadList.size() - 1);
        assertThat(testAidasUpload.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAidasUpload.getDateUploaded()).isEqualTo(UPDATED_DATE_UPLOADED);
        assertThat(testAidasUpload.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testAidasUpload.getStatusModifiedDate()).isEqualTo(UPDATED_STATUS_MODIFIED_DATE);
        assertThat(testAidasUpload.getRejectReason()).isEqualTo(UPDATED_REJECT_REASON);

        // Validate the AidasUpload in Elasticsearch
        verify(mockAidasUploadSearchRepository).save(testAidasUpload);
    }

    @Test
    @Transactional
    void putNonExistingAidasUpload() throws Exception {
        int databaseSizeBeforeUpdate = aidasUploadRepository.findAll().size();
        aidasUpload.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasUploadMockMvc
            .perform(
                put(ENTITY_API_URL_ID, aidasUpload.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUpload))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUpload in the database
        List<AidasUpload> aidasUploadList = aidasUploadRepository.findAll();
        assertThat(aidasUploadList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUpload in Elasticsearch
        verify(mockAidasUploadSearchRepository, times(0)).save(aidasUpload);
    }

    @Test
    @Transactional
    void putWithIdMismatchAidasUpload() throws Exception {
        int databaseSizeBeforeUpdate = aidasUploadRepository.findAll().size();
        aidasUpload.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUploadMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUpload))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUpload in the database
        List<AidasUpload> aidasUploadList = aidasUploadRepository.findAll();
        assertThat(aidasUploadList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUpload in Elasticsearch
        verify(mockAidasUploadSearchRepository, times(0)).save(aidasUpload);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAidasUpload() throws Exception {
        int databaseSizeBeforeUpdate = aidasUploadRepository.findAll().size();
        aidasUpload.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUploadMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUpload))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasUpload in the database
        List<AidasUpload> aidasUploadList = aidasUploadRepository.findAll();
        assertThat(aidasUploadList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUpload in Elasticsearch
        verify(mockAidasUploadSearchRepository, times(0)).save(aidasUpload);
    }

    @Test
    @Transactional
    void partialUpdateAidasUploadWithPatch() throws Exception {
        // Initialize the database
        aidasUploadRepository.saveAndFlush(aidasUpload);

        int databaseSizeBeforeUpdate = aidasUploadRepository.findAll().size();

        // Update the aidasUpload using partial update
        AidasUpload partialUpdatedAidasUpload = new AidasUpload();
        partialUpdatedAidasUpload.setId(aidasUpload.getId());

        partialUpdatedAidasUpload
            .dateUploaded(UPDATED_DATE_UPLOADED)
            .status(UPDATED_STATUS)
            .statusModifiedDate(UPDATED_STATUS_MODIFIED_DATE);

        restAidasUploadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasUpload.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasUpload))
            )
            .andExpect(status().isOk());

        // Validate the AidasUpload in the database
        List<AidasUpload> aidasUploadList = aidasUploadRepository.findAll();
        assertThat(aidasUploadList).hasSize(databaseSizeBeforeUpdate);
        AidasUpload testAidasUpload = aidasUploadList.get(aidasUploadList.size() - 1);
        assertThat(testAidasUpload.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAidasUpload.getDateUploaded()).isEqualTo(UPDATED_DATE_UPLOADED);
        assertThat(testAidasUpload.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testAidasUpload.getStatusModifiedDate()).isEqualTo(UPDATED_STATUS_MODIFIED_DATE);
        assertThat(testAidasUpload.getRejectReason()).isEqualTo(DEFAULT_REJECT_REASON);
    }

    @Test
    @Transactional
    void fullUpdateAidasUploadWithPatch() throws Exception {
        // Initialize the database
        aidasUploadRepository.saveAndFlush(aidasUpload);

        int databaseSizeBeforeUpdate = aidasUploadRepository.findAll().size();

        // Update the aidasUpload using partial update
        AidasUpload partialUpdatedAidasUpload = new AidasUpload();
        partialUpdatedAidasUpload.setId(aidasUpload.getId());

        partialUpdatedAidasUpload
            .name(UPDATED_NAME)
            .dateUploaded(UPDATED_DATE_UPLOADED)
            .status(UPDATED_STATUS)
            .statusModifiedDate(UPDATED_STATUS_MODIFIED_DATE)
            .rejectReason(UPDATED_REJECT_REASON);

        restAidasUploadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasUpload.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasUpload))
            )
            .andExpect(status().isOk());

        // Validate the AidasUpload in the database
        List<AidasUpload> aidasUploadList = aidasUploadRepository.findAll();
        assertThat(aidasUploadList).hasSize(databaseSizeBeforeUpdate);
        AidasUpload testAidasUpload = aidasUploadList.get(aidasUploadList.size() - 1);
        assertThat(testAidasUpload.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAidasUpload.getDateUploaded()).isEqualTo(UPDATED_DATE_UPLOADED);
        assertThat(testAidasUpload.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testAidasUpload.getStatusModifiedDate()).isEqualTo(UPDATED_STATUS_MODIFIED_DATE);
        assertThat(testAidasUpload.getRejectReason()).isEqualTo(UPDATED_REJECT_REASON);
    }

    @Test
    @Transactional
    void patchNonExistingAidasUpload() throws Exception {
        int databaseSizeBeforeUpdate = aidasUploadRepository.findAll().size();
        aidasUpload.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasUploadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, aidasUpload.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasUpload))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUpload in the database
        List<AidasUpload> aidasUploadList = aidasUploadRepository.findAll();
        assertThat(aidasUploadList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUpload in Elasticsearch
        verify(mockAidasUploadSearchRepository, times(0)).save(aidasUpload);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAidasUpload() throws Exception {
        int databaseSizeBeforeUpdate = aidasUploadRepository.findAll().size();
        aidasUpload.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUploadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasUpload))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUpload in the database
        List<AidasUpload> aidasUploadList = aidasUploadRepository.findAll();
        assertThat(aidasUploadList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUpload in Elasticsearch
        verify(mockAidasUploadSearchRepository, times(0)).save(aidasUpload);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAidasUpload() throws Exception {
        int databaseSizeBeforeUpdate = aidasUploadRepository.findAll().size();
        aidasUpload.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUploadMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasUpload))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasUpload in the database
        List<AidasUpload> aidasUploadList = aidasUploadRepository.findAll();
        assertThat(aidasUploadList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUpload in Elasticsearch
        verify(mockAidasUploadSearchRepository, times(0)).save(aidasUpload);
    }

    @Test
    @Transactional
    void deleteAidasUpload() throws Exception {
        // Initialize the database
        aidasUploadRepository.saveAndFlush(aidasUpload);

        int databaseSizeBeforeDelete = aidasUploadRepository.findAll().size();

        // Delete the aidasUpload
        restAidasUploadMockMvc
            .perform(delete(ENTITY_API_URL_ID, aidasUpload.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AidasUpload> aidasUploadList = aidasUploadRepository.findAll();
        assertThat(aidasUploadList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AidasUpload in Elasticsearch
        verify(mockAidasUploadSearchRepository, times(1)).deleteById(aidasUpload.getId());
    }

    @Test
    @Transactional
    void searchAidasUpload() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        aidasUploadRepository.saveAndFlush(aidasUpload);
        when(mockAidasUploadSearchRepository.search("id:" + aidasUpload.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(aidasUpload), PageRequest.of(0, 1), 1));

        // Search the aidasUpload
        restAidasUploadMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + aidasUpload.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasUpload.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].dateUploaded").value(hasItem(sameInstant(DEFAULT_DATE_UPLOADED))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())))
            .andExpect(jsonPath("$.[*].statusModifiedDate").value(hasItem(sameInstant(DEFAULT_STATUS_MODIFIED_DATE))))
            .andExpect(jsonPath("$.[*].rejectReason").value(hasItem(DEFAULT_REJECT_REASON)));
    }
}
