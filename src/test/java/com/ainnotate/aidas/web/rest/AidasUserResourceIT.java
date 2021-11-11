package com.ainnotate.aidas.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ainnotate.aidas.IntegrationTest;
import com.ainnotate.aidas.domain.AidasUser;
import com.ainnotate.aidas.repository.AidasUserRepository;
import com.ainnotate.aidas.repository.search.AidasUserSearchRepository;
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
 * Integration tests for the {@link AidasUserResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AidasUserResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_LOCKED = false;
    private static final Boolean UPDATED_LOCKED = true;

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/aidas-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/aidas-users";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AidasUserRepository aidasUserRepository;

    /**
     * This repository is mocked in the com.ainnotate.aidas.repository.search test package.
     *
     * @see com.ainnotate.aidas.repository.search.AidasUserSearchRepositoryMockConfiguration
     */
    @Autowired
    private AidasUserSearchRepository mockAidasUserSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAidasUserMockMvc;

    private AidasUser aidasUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasUser createEntity(EntityManager em) {
        AidasUser aidasUser = new AidasUser()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .locked(DEFAULT_LOCKED)
            .password(DEFAULT_PASSWORD);
        return aidasUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AidasUser createUpdatedEntity(EntityManager em) {
        AidasUser aidasUser = new AidasUser()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .locked(UPDATED_LOCKED)
            .password(UPDATED_PASSWORD);
        return aidasUser;
    }

    @BeforeEach
    public void initTest() {
        aidasUser = createEntity(em);
    }

    @Test
    @Transactional
    void createAidasUser() throws Exception {
        int databaseSizeBeforeCreate = aidasUserRepository.findAll().size();
        // Create the AidasUser
        restAidasUserMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUser))
            )
            .andExpect(status().isCreated());

        // Validate the AidasUser in the database
        List<AidasUser> aidasUserList = aidasUserRepository.findAll();
        assertThat(aidasUserList).hasSize(databaseSizeBeforeCreate + 1);
        AidasUser testAidasUser = aidasUserList.get(aidasUserList.size() - 1);
        assertThat(testAidasUser.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testAidasUser.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testAidasUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testAidasUser.getLocked()).isEqualTo(DEFAULT_LOCKED);
        assertThat(testAidasUser.getPassword()).isEqualTo(DEFAULT_PASSWORD);

        // Validate the AidasUser in Elasticsearch
        verify(mockAidasUserSearchRepository, times(1)).save(testAidasUser);
    }

    @Test
    @Transactional
    void createAidasUserWithExistingId() throws Exception {
        // Create the AidasUser with an existing ID
        aidasUser.setId(1L);

        int databaseSizeBeforeCreate = aidasUserRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAidasUserMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUser in the database
        List<AidasUser> aidasUserList = aidasUserRepository.findAll();
        assertThat(aidasUserList).hasSize(databaseSizeBeforeCreate);

        // Validate the AidasUser in Elasticsearch
        verify(mockAidasUserSearchRepository, times(0)).save(aidasUser);
    }

    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = aidasUserRepository.findAll().size();
        // set the field null
        aidasUser.setFirstName(null);

        // Create the AidasUser, which fails.

        restAidasUserMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUser))
            )
            .andExpect(status().isBadRequest());

        List<AidasUser> aidasUserList = aidasUserRepository.findAll();
        assertThat(aidasUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = aidasUserRepository.findAll().size();
        // set the field null
        aidasUser.setEmail(null);

        // Create the AidasUser, which fails.

        restAidasUserMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUser))
            )
            .andExpect(status().isBadRequest());

        List<AidasUser> aidasUserList = aidasUserRepository.findAll();
        assertThat(aidasUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLockedIsRequired() throws Exception {
        int databaseSizeBeforeTest = aidasUserRepository.findAll().size();
        // set the field null
        aidasUser.setLocked(null);

        // Create the AidasUser, which fails.

        restAidasUserMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUser))
            )
            .andExpect(status().isBadRequest());

        List<AidasUser> aidasUserList = aidasUserRepository.findAll();
        assertThat(aidasUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPasswordIsRequired() throws Exception {
        int databaseSizeBeforeTest = aidasUserRepository.findAll().size();
        // set the field null
        aidasUser.setPassword(null);

        // Create the AidasUser, which fails.

        restAidasUserMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUser))
            )
            .andExpect(status().isBadRequest());

        List<AidasUser> aidasUserList = aidasUserRepository.findAll();
        assertThat(aidasUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAidasUsers() throws Exception {
        // Initialize the database
        aidasUserRepository.saveAndFlush(aidasUser);

        // Get all the aidasUserList
        restAidasUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].locked").value(hasItem(DEFAULT_LOCKED.booleanValue())))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)));
    }

    @Test
    @Transactional
    void getAidasUser() throws Exception {
        // Initialize the database
        aidasUserRepository.saveAndFlush(aidasUser);

        // Get the aidasUser
        restAidasUserMockMvc
            .perform(get(ENTITY_API_URL_ID, aidasUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(aidasUser.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.locked").value(DEFAULT_LOCKED.booleanValue()))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD));
    }

    @Test
    @Transactional
    void getNonExistingAidasUser() throws Exception {
        // Get the aidasUser
        restAidasUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAidasUser() throws Exception {
        // Initialize the database
        aidasUserRepository.saveAndFlush(aidasUser);

        int databaseSizeBeforeUpdate = aidasUserRepository.findAll().size();

        // Update the aidasUser
        AidasUser updatedAidasUser = aidasUserRepository.findById(aidasUser.getId()).get();
        // Disconnect from session so that the updates on updatedAidasUser are not directly saved in db
        em.detach(updatedAidasUser);
        updatedAidasUser
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .locked(UPDATED_LOCKED)
            .password(UPDATED_PASSWORD);

        restAidasUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAidasUser.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAidasUser))
            )
            .andExpect(status().isOk());

        // Validate the AidasUser in the database
        List<AidasUser> aidasUserList = aidasUserRepository.findAll();
        assertThat(aidasUserList).hasSize(databaseSizeBeforeUpdate);
        AidasUser testAidasUser = aidasUserList.get(aidasUserList.size() - 1);
        assertThat(testAidasUser.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testAidasUser.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testAidasUser.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testAidasUser.getLocked()).isEqualTo(UPDATED_LOCKED);
        assertThat(testAidasUser.getPassword()).isEqualTo(UPDATED_PASSWORD);

        // Validate the AidasUser in Elasticsearch
        verify(mockAidasUserSearchRepository).save(testAidasUser);
    }

    @Test
    @Transactional
    void putNonExistingAidasUser() throws Exception {
        int databaseSizeBeforeUpdate = aidasUserRepository.findAll().size();
        aidasUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, aidasUser.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUser in the database
        List<AidasUser> aidasUserList = aidasUserRepository.findAll();
        assertThat(aidasUserList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUser in Elasticsearch
        verify(mockAidasUserSearchRepository, times(0)).save(aidasUser);
    }

    @Test
    @Transactional
    void putWithIdMismatchAidasUser() throws Exception {
        int databaseSizeBeforeUpdate = aidasUserRepository.findAll().size();
        aidasUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUser in the database
        List<AidasUser> aidasUserList = aidasUserRepository.findAll();
        assertThat(aidasUserList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUser in Elasticsearch
        verify(mockAidasUserSearchRepository, times(0)).save(aidasUser);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAidasUser() throws Exception {
        int databaseSizeBeforeUpdate = aidasUserRepository.findAll().size();
        aidasUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUserMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(aidasUser))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasUser in the database
        List<AidasUser> aidasUserList = aidasUserRepository.findAll();
        assertThat(aidasUserList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUser in Elasticsearch
        verify(mockAidasUserSearchRepository, times(0)).save(aidasUser);
    }

    @Test
    @Transactional
    void partialUpdateAidasUserWithPatch() throws Exception {
        // Initialize the database
        aidasUserRepository.saveAndFlush(aidasUser);

        int databaseSizeBeforeUpdate = aidasUserRepository.findAll().size();

        // Update the aidasUser using partial update
        AidasUser partialUpdatedAidasUser = new AidasUser();
        partialUpdatedAidasUser.setId(aidasUser.getId());

        partialUpdatedAidasUser.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).email(UPDATED_EMAIL);

        restAidasUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasUser.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasUser))
            )
            .andExpect(status().isOk());

        // Validate the AidasUser in the database
        List<AidasUser> aidasUserList = aidasUserRepository.findAll();
        assertThat(aidasUserList).hasSize(databaseSizeBeforeUpdate);
        AidasUser testAidasUser = aidasUserList.get(aidasUserList.size() - 1);
        assertThat(testAidasUser.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testAidasUser.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testAidasUser.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testAidasUser.getLocked()).isEqualTo(DEFAULT_LOCKED);
        assertThat(testAidasUser.getPassword()).isEqualTo(DEFAULT_PASSWORD);
    }

    @Test
    @Transactional
    void fullUpdateAidasUserWithPatch() throws Exception {
        // Initialize the database
        aidasUserRepository.saveAndFlush(aidasUser);

        int databaseSizeBeforeUpdate = aidasUserRepository.findAll().size();

        // Update the aidasUser using partial update
        AidasUser partialUpdatedAidasUser = new AidasUser();
        partialUpdatedAidasUser.setId(aidasUser.getId());

        partialUpdatedAidasUser
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .locked(UPDATED_LOCKED)
            .password(UPDATED_PASSWORD);

        restAidasUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAidasUser.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAidasUser))
            )
            .andExpect(status().isOk());

        // Validate the AidasUser in the database
        List<AidasUser> aidasUserList = aidasUserRepository.findAll();
        assertThat(aidasUserList).hasSize(databaseSizeBeforeUpdate);
        AidasUser testAidasUser = aidasUserList.get(aidasUserList.size() - 1);
        assertThat(testAidasUser.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testAidasUser.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testAidasUser.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testAidasUser.getLocked()).isEqualTo(UPDATED_LOCKED);
        assertThat(testAidasUser.getPassword()).isEqualTo(UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    void patchNonExistingAidasUser() throws Exception {
        int databaseSizeBeforeUpdate = aidasUserRepository.findAll().size();
        aidasUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, aidasUser.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUser in the database
        List<AidasUser> aidasUserList = aidasUserRepository.findAll();
        assertThat(aidasUserList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUser in Elasticsearch
        verify(mockAidasUserSearchRepository, times(0)).save(aidasUser);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAidasUser() throws Exception {
        int databaseSizeBeforeUpdate = aidasUserRepository.findAll().size();
        aidasUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUser in the database
        List<AidasUser> aidasUserList = aidasUserRepository.findAll();
        assertThat(aidasUserList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUser in Elasticsearch
        verify(mockAidasUserSearchRepository, times(0)).save(aidasUser);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAidasUser() throws Exception {
        int databaseSizeBeforeUpdate = aidasUserRepository.findAll().size();
        aidasUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUserMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(aidasUser))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasUser in the database
        List<AidasUser> aidasUserList = aidasUserRepository.findAll();
        assertThat(aidasUserList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUser in Elasticsearch
        verify(mockAidasUserSearchRepository, times(0)).save(aidasUser);
    }

    @Test
    @Transactional
    void deleteAidasUser() throws Exception {
        // Initialize the database
        aidasUserRepository.saveAndFlush(aidasUser);

        int databaseSizeBeforeDelete = aidasUserRepository.findAll().size();

        // Delete the aidasUser
        restAidasUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, aidasUser.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AidasUser> aidasUserList = aidasUserRepository.findAll();
        assertThat(aidasUserList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AidasUser in Elasticsearch
        verify(mockAidasUserSearchRepository, times(1)).deleteById(aidasUser.getId());
    }

    @Test
    @Transactional
    void searchAidasUser() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        aidasUserRepository.saveAndFlush(aidasUser);
        when(mockAidasUserSearchRepository.search("id:" + aidasUser.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(aidasUser), PageRequest.of(0, 1), 1));

        // Search the aidasUser
        restAidasUserMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + aidasUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aidasUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].locked").value(hasItem(DEFAULT_LOCKED.booleanValue())))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)));
    }
}
