package com.ainnotate.aidas.web.rest;

import static com.ainnotate.aidas.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ainnotate.aidas.IntegrationTest;
import com.ainnotate.aidas.domain.Upload;
import com.ainnotate.aidas.domain.UserVendorMappingObjectMapping;
import com.ainnotate.aidas.repository.UploadRepository;
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
 * Integration tests for the {@link UploadResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UploadResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_UPLOADED = Instant.now();
    private static final Instant UPDATED_DATE_UPLOADED = Instant.now();

    private static final Integer DEFAULT_STATUS = 2;
    private static final Integer UPDATED_STATUS = 1;

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
    private UploadRepository uploadRepository;

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

    private Upload upload;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Upload createEntity(EntityManager em) {
        Upload upload = new Upload()
            .name(DEFAULT_NAME)
            .dateUploaded(DEFAULT_DATE_UPLOADED)
            .status(DEFAULT_STATUS)
            .statusModifiedDate(DEFAULT_STATUS_MODIFIED_DATE)
            ;
        // Add required entity
        UserVendorMappingObjectMapping userVendorMappingObjectMapping;
        if (TestUtil.findAll(em, UserVendorMappingObjectMapping.class).isEmpty()) {
            userVendorMappingObjectMapping = UserVendorMappingObjectMappingResourceIT.createEntity(em);
            em.persist(userVendorMappingObjectMapping);
            em.flush();
        } else {
            userVendorMappingObjectMapping = TestUtil.findAll(em, UserVendorMappingObjectMapping.class).get(0);
        }
        upload.setAidasUserAidasObjectMapping(userVendorMappingObjectMapping);
        return upload;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Upload createUpdatedEntity(EntityManager em) {
        Upload upload = new Upload()
            .name(UPDATED_NAME)
            .dateUploaded(UPDATED_DATE_UPLOADED)
            .status(UPDATED_STATUS)
            .statusModifiedDate(UPDATED_STATUS_MODIFIED_DATE)
            ;
        // Add required entity
        UserVendorMappingObjectMapping userVendorMappingObjectMapping;
        if (TestUtil.findAll(em, UserVendorMappingObjectMapping.class).isEmpty()) {
            userVendorMappingObjectMapping = UserVendorMappingObjectMappingResourceIT.createUpdatedEntity(em);
            em.persist(userVendorMappingObjectMapping);
            em.flush();
        } else {
            userVendorMappingObjectMapping = TestUtil.findAll(em, UserVendorMappingObjectMapping.class).get(0);
        }
        upload.setAidasUserAidasObjectMapping(userVendorMappingObjectMapping);
        return upload;
    }

    @BeforeEach
    public void initTest() {
        upload = createEntity(em);
    }

    @Test
    @Transactional
    void createAidasUpload() throws Exception {
        int databaseSizeBeforeCreate = uploadRepository.findAll().size();
        // Create the AidasUpload
        restAidasUploadMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(upload))
            )
            .andExpect(status().isCreated());

        // Validate the AidasUpload in the database
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeCreate + 1);
        Upload testUpload = uploadList.get(uploadList.size() - 1);
        assertThat(testUpload.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUpload.getDateUploaded()).isEqualTo(DEFAULT_DATE_UPLOADED);
        assertThat(testUpload.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testUpload.getStatusModifiedDate()).isEqualTo(DEFAULT_STATUS_MODIFIED_DATE);


        // Validate the AidasUpload in Elasticsearch
        verify(mockAidasUploadSearchRepository, times(1)).save(testUpload);
    }

    @Test
    @Transactional
    void createAidasUploadWithExistingId() throws Exception {
        // Create the AidasUpload with an existing ID
        upload.setId(1L);

        int databaseSizeBeforeCreate = uploadRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAidasUploadMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(upload))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUpload in the database
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeCreate);

        // Validate the AidasUpload in Elasticsearch
        verify(mockAidasUploadSearchRepository, times(0)).save(upload);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = uploadRepository.findAll().size();
        // set the field null
        upload.setName(null);

        // Create the AidasUpload, which fails.

        restAidasUploadMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(upload))
            )
            .andExpect(status().isBadRequest());

        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAidasUploads() throws Exception {
        // Initialize the database
        uploadRepository.saveAndFlush(upload);

        // Get all the aidasUploadList
        restAidasUploadMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(upload.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            //.andExpect(jsonPath("$.[*].dateUploaded").value(hasItem(sameInstant(DEFAULT_DATE_UPLOADED))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].statusModifiedDate").value(hasItem(sameInstant(DEFAULT_STATUS_MODIFIED_DATE))))
            .andExpect(jsonPath("$.[*].rejectReason").value(hasItem(DEFAULT_REJECT_REASON)));
    }

    @Test
    @Transactional
    void getUpload() throws Exception {
        // Initialize the database
        uploadRepository.saveAndFlush(upload);

        // Get the upload
        restAidasUploadMockMvc
            .perform(get(ENTITY_API_URL_ID, upload.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(upload.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            //.andExpect(jsonPath("$.dateUploaded").value(sameInstant(DEFAULT_DATE_UPLOADED)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.statusModifiedDate").value(sameInstant(DEFAULT_STATUS_MODIFIED_DATE)))
            .andExpect(jsonPath("$.rejectReason").value(DEFAULT_REJECT_REASON));
    }

    @Test
    @Transactional
    void getNonExistingAidasUpload() throws Exception {
        // Get the upload
        restAidasUploadMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAidasUpload() throws Exception {
        // Initialize the database
        uploadRepository.saveAndFlush(upload);

        int databaseSizeBeforeUpdate = uploadRepository.findAll().size();

        // Update the upload
        Upload updatedUpload = uploadRepository.findById(upload.getId()).get();
        // Disconnect from session so that the updates on updatedAidasUpload are not directly saved in db
        em.detach(updatedUpload);
        updatedUpload
            .name(UPDATED_NAME)
            .dateUploaded(UPDATED_DATE_UPLOADED)
            .status(UPDATED_STATUS)
            .statusModifiedDate(UPDATED_STATUS_MODIFIED_DATE);


        restAidasUploadMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUpload.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUpload))
            )
            .andExpect(status().isOk());

        // Validate the AidasUpload in the database
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeUpdate);
        Upload testUpload = uploadList.get(uploadList.size() - 1);
        assertThat(testUpload.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUpload.getDateUploaded()).isEqualTo(UPDATED_DATE_UPLOADED);
        assertThat(testUpload.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testUpload.getStatusModifiedDate()).isEqualTo(UPDATED_STATUS_MODIFIED_DATE);

        // Validate the AidasUpload in Elasticsearch
        verify(mockAidasUploadSearchRepository).save(testUpload);
    }

    @Test
    @Transactional
    void putNonExistingAidasUpload() throws Exception {
        int databaseSizeBeforeUpdate = uploadRepository.findAll().size();
        upload.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasUploadMockMvc
            .perform(
                put(ENTITY_API_URL_ID, upload.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(upload))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUpload in the database
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUpload in Elasticsearch
        verify(mockAidasUploadSearchRepository, times(0)).save(upload);
    }

    @Test
    @Transactional
    void putWithIdMismatchAidasUpload() throws Exception {
        int databaseSizeBeforeUpdate = uploadRepository.findAll().size();
        upload.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUploadMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(upload))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUpload in the database
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUpload in Elasticsearch
        verify(mockAidasUploadSearchRepository, times(0)).save(upload);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAidasUpload() throws Exception {
        int databaseSizeBeforeUpdate = uploadRepository.findAll().size();
        upload.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUploadMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(upload))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasUpload in the database
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUpload in Elasticsearch
        verify(mockAidasUploadSearchRepository, times(0)).save(upload);
    }

    @Test
    @Transactional
    void partialUpdateAidasUploadWithPatch() throws Exception {
        // Initialize the database
        uploadRepository.saveAndFlush(upload);

        int databaseSizeBeforeUpdate = uploadRepository.findAll().size();

        // Update the upload using partial update
        Upload partialUpdatedUpload = new Upload();
        partialUpdatedUpload.setId(upload.getId());

        partialUpdatedUpload
            .dateUploaded(UPDATED_DATE_UPLOADED)
            .status(UPDATED_STATUS)
            .statusModifiedDate(UPDATED_STATUS_MODIFIED_DATE);

        restAidasUploadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUpload.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUpload))
            )
            .andExpect(status().isOk());

        // Validate the AidasUpload in the database
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeUpdate);
        Upload testUpload = uploadList.get(uploadList.size() - 1);
        assertThat(testUpload.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUpload.getDateUploaded()).isEqualTo(UPDATED_DATE_UPLOADED);
        assertThat(testUpload.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testUpload.getStatusModifiedDate()).isEqualTo(UPDATED_STATUS_MODIFIED_DATE);

    }

    @Test
    @Transactional
    void fullUpdateAidasUploadWithPatch() throws Exception {
        // Initialize the database
        uploadRepository.saveAndFlush(upload);

        int databaseSizeBeforeUpdate = uploadRepository.findAll().size();

        // Update the upload using partial update
        Upload partialUpdatedUpload = new Upload();
        partialUpdatedUpload.setId(upload.getId());

        partialUpdatedUpload
            .name(UPDATED_NAME)
            .dateUploaded(UPDATED_DATE_UPLOADED)
            .status(UPDATED_STATUS)
            .statusModifiedDate(UPDATED_STATUS_MODIFIED_DATE)
           ;

        restAidasUploadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUpload.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUpload))
            )
            .andExpect(status().isOk());

        // Validate the AidasUpload in the database
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeUpdate);
        Upload testUpload = uploadList.get(uploadList.size() - 1);
        assertThat(testUpload.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUpload.getDateUploaded()).isEqualTo(UPDATED_DATE_UPLOADED);
        assertThat(testUpload.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testUpload.getStatusModifiedDate()).isEqualTo(UPDATED_STATUS_MODIFIED_DATE);

    }

    @Test
    @Transactional
    void patchNonExistingAidasUpload() throws Exception {
        int databaseSizeBeforeUpdate = uploadRepository.findAll().size();
        upload.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasUploadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, upload.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(upload))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUpload in the database
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUpload in Elasticsearch
        verify(mockAidasUploadSearchRepository, times(0)).save(upload);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAidasUpload() throws Exception {
        int databaseSizeBeforeUpdate = uploadRepository.findAll().size();
        upload.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUploadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(upload))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUpload in the database
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUpload in Elasticsearch
        verify(mockAidasUploadSearchRepository, times(0)).save(upload);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAidasUpload() throws Exception {
        int databaseSizeBeforeUpdate = uploadRepository.findAll().size();
        upload.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUploadMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(upload))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasUpload in the database
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUpload in Elasticsearch
        verify(mockAidasUploadSearchRepository, times(0)).save(upload);
    }

    @Test
    @Transactional
    void deleteAidasUpload() throws Exception {
        // Initialize the database
        uploadRepository.saveAndFlush(upload);

        int databaseSizeBeforeDelete = uploadRepository.findAll().size();

        // Delete the upload
        restAidasUploadMockMvc
            .perform(delete(ENTITY_API_URL_ID, upload.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AidasUpload in Elasticsearch
        verify(mockAidasUploadSearchRepository, times(1)).deleteById(upload.getId());
    }

    @Test
    @Transactional
    void searchAidasUpload() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        uploadRepository.saveAndFlush(upload);
        when(mockAidasUploadSearchRepository.search("id:" + upload.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(upload), PageRequest.of(0, 1), 1));

        // Search the upload
        restAidasUploadMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + upload.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(upload.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            //.andExpect(jsonPath("$.[*].dateUploaded").value(hasItem(sameInstant(DEFAULT_DATE_UPLOADED))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].statusModifiedDate").value(hasItem(sameInstant(DEFAULT_STATUS_MODIFIED_DATE))))
            .andExpect(jsonPath("$.[*].rejectReason").value(hasItem(DEFAULT_REJECT_REASON)));
    }
}
