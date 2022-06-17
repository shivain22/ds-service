package com.ainnotate.aidas.web.rest;

import static com.ainnotate.aidas.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ainnotate.aidas.IntegrationTest;
import com.ainnotate.aidas.domain.Object;
import com.ainnotate.aidas.domain.User;
import com.ainnotate.aidas.domain.UserVendorMappingObjectMapping;
import com.ainnotate.aidas.repository.UserVendorMappingObjectMappingRepository;
import com.ainnotate.aidas.repository.search.UserObjectMappingSearchRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;

import com.ainnotate.aidas.repository.search.UserObjectMappingSearchRepositoryMockConfiguration;
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
 * Integration tests for the {@link UserVendorMappingObjectMappingResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UserVendorMappingObjectMappingResourceIT {

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
    private UserVendorMappingObjectMappingRepository userVendorMappingObjectMappingRepository;

    /**
     * This repository is mocked in the com.ainnotate.aidas.repository.search test package.
     *
     * @see UserObjectMappingSearchRepositoryMockConfiguration
     */
    @Autowired
    private UserObjectMappingSearchRepository mockUserObjectMappingSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAidasUserAidasObjectMappingMockMvc;

    private UserVendorMappingObjectMapping userVendorMappingObjectMapping;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserVendorMappingObjectMapping createEntity(EntityManager em) {
        UserVendorMappingObjectMapping userVendorMappingObjectMapping = new UserVendorMappingObjectMapping()
            .dateAssigned(DEFAULT_DATE_ASSIGNED)
            .status(DEFAULT_STATUS);
        // Add required entity
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            user = UserResourceIT.createEntity(em);
            em.persist(user);
            em.flush();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        //aidasUserAidasVendorMappingAidasObjectMapping.setUserVendorMapping(user);
        // Add required entity
        Object object;
        if (TestUtil.findAll(em, Object.class).isEmpty()) {
            object = ObjectResourceIT.createEntity(em);
            em.persist(object);
            em.flush();
        } else {
            object = TestUtil.findAll(em, Object.class).get(0);
        }
        userVendorMappingObjectMapping.setObject(object);
        return userVendorMappingObjectMapping;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserVendorMappingObjectMapping createUpdatedEntity(EntityManager em) {
        UserVendorMappingObjectMapping userVendorMappingObjectMapping = new UserVendorMappingObjectMapping()
            .dateAssigned(UPDATED_DATE_ASSIGNED)
            .status(UPDATED_STATUS);
        // Add required entity
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            user = UserResourceIT.createUpdatedEntity(em);
            em.persist(user);
            em.flush();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        //aidasUserAidasVendorMappingAidasObjectMapping.setUser(user);
        // Add required entity
        Object object;
        if (TestUtil.findAll(em, Object.class).isEmpty()) {
            object = ObjectResourceIT.createUpdatedEntity(em);
            em.persist(object);
            em.flush();
        } else {
            object = TestUtil.findAll(em, Object.class).get(0);
        }
        userVendorMappingObjectMapping.setObject(object);
        return userVendorMappingObjectMapping;
    }

    @BeforeEach
    public void initTest() {
        userVendorMappingObjectMapping = createEntity(em);
    }

    @Test
    @Transactional
    void createAidasUserAidasObjectMapping() throws Exception {
        int databaseSizeBeforeCreate = userVendorMappingObjectMappingRepository.findAll().size();
        // Create the AidasUserAidasObjectMapping
        restAidasUserAidasObjectMappingMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userVendorMappingObjectMapping))
            )
            .andExpect(status().isCreated());

        // Validate the AidasUserAidasObjectMapping in the database
        List<UserVendorMappingObjectMapping> userVendorMappingObjectMappingList = userVendorMappingObjectMappingRepository.findAll();
        assertThat(userVendorMappingObjectMappingList).hasSize(databaseSizeBeforeCreate + 1);
        UserVendorMappingObjectMapping testUserVendorMappingObjectMapping = userVendorMappingObjectMappingList.get(
            userVendorMappingObjectMappingList.size() - 1
        );
        assertThat(testUserVendorMappingObjectMapping.getDateAssigned()).isEqualTo(DEFAULT_DATE_ASSIGNED);
        assertThat(testUserVendorMappingObjectMapping.getStatus()).isEqualTo(DEFAULT_STATUS);

        // Validate the AidasUserAidasObjectMapping in Elasticsearch
        verify(mockUserObjectMappingSearchRepository, times(1)).save(testUserVendorMappingObjectMapping);
    }

    @Test
    @Transactional
    void createAidasUserAidasObjectMappingWithExistingId() throws Exception {
        // Create the AidasUserAidasObjectMapping with an existing ID
        userVendorMappingObjectMapping.setId(1L);

        int databaseSizeBeforeCreate = userVendorMappingObjectMappingRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAidasUserAidasObjectMappingMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userVendorMappingObjectMapping))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUserAidasObjectMapping in the database
        List<UserVendorMappingObjectMapping> userVendorMappingObjectMappingList = userVendorMappingObjectMappingRepository.findAll();
        assertThat(userVendorMappingObjectMappingList).hasSize(databaseSizeBeforeCreate);

        // Validate the AidasUserAidasObjectMapping in Elasticsearch
        verify(mockUserObjectMappingSearchRepository, times(0)).save(userVendorMappingObjectMapping);
    }

    @Test
    @Transactional
    void getAllAidasUserAidasObjectMappings() throws Exception {
        // Initialize the database
        userVendorMappingObjectMappingRepository.saveAndFlush(userVendorMappingObjectMapping);

        // Get all the aidasUserAidasObjectMappingList
        restAidasUserAidasObjectMappingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userVendorMappingObjectMapping.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateAssigned").value(hasItem(sameInstant(DEFAULT_DATE_ASSIGNED))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @Test
    @Transactional
    void getUserVendorMappingObjectMapping() throws Exception {
        // Initialize the database
        userVendorMappingObjectMappingRepository.saveAndFlush(userVendorMappingObjectMapping);

        // Get the aidasUserAidasObjectMapping
        restAidasUserAidasObjectMappingMockMvc
            .perform(get(ENTITY_API_URL_ID, userVendorMappingObjectMapping.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userVendorMappingObjectMapping.getId().intValue()))
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
        userVendorMappingObjectMappingRepository.saveAndFlush(userVendorMappingObjectMapping);

        int databaseSizeBeforeUpdate = userVendorMappingObjectMappingRepository.findAll().size();

        // Update the aidasUserAidasObjectMapping
        UserVendorMappingObjectMapping updatedUserVendorMappingObjectMapping = userVendorMappingObjectMappingRepository
            .findById(userVendorMappingObjectMapping.getId())
            .get();
        // Disconnect from session so that the updates on updatedAidasUserAidasObjectMapping are not directly saved in db
        em.detach(updatedUserVendorMappingObjectMapping);
        updatedUserVendorMappingObjectMapping.dateAssigned(UPDATED_DATE_ASSIGNED).status(UPDATED_STATUS);

        restAidasUserAidasObjectMappingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserVendorMappingObjectMapping.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUserVendorMappingObjectMapping))
            )
            .andExpect(status().isOk());

        // Validate the AidasUserAidasObjectMapping in the database
        List<UserVendorMappingObjectMapping> userVendorMappingObjectMappingList = userVendorMappingObjectMappingRepository.findAll();
        assertThat(userVendorMappingObjectMappingList).hasSize(databaseSizeBeforeUpdate);
        UserVendorMappingObjectMapping testUserVendorMappingObjectMapping = userVendorMappingObjectMappingList.get(
            userVendorMappingObjectMappingList.size() - 1
        );
        assertThat(testUserVendorMappingObjectMapping.getDateAssigned()).isEqualTo(UPDATED_DATE_ASSIGNED);
        assertThat(testUserVendorMappingObjectMapping.getStatus()).isEqualTo(UPDATED_STATUS);

        // Validate the AidasUserAidasObjectMapping in Elasticsearch
        verify(mockUserObjectMappingSearchRepository).save(testUserVendorMappingObjectMapping);
    }

    @Test
    @Transactional
    void putNonExistingAidasUserAidasObjectMapping() throws Exception {
        int databaseSizeBeforeUpdate = userVendorMappingObjectMappingRepository.findAll().size();
        userVendorMappingObjectMapping.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasUserAidasObjectMappingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userVendorMappingObjectMapping.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userVendorMappingObjectMapping))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUserAidasObjectMapping in the database
        List<UserVendorMappingObjectMapping> userVendorMappingObjectMappingList = userVendorMappingObjectMappingRepository.findAll();
        assertThat(userVendorMappingObjectMappingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUserAidasObjectMapping in Elasticsearch
        verify(mockUserObjectMappingSearchRepository, times(0)).save(userVendorMappingObjectMapping);
    }

    @Test
    @Transactional
    void putWithIdMismatchAidasUserAidasObjectMapping() throws Exception {
        int databaseSizeBeforeUpdate = userVendorMappingObjectMappingRepository.findAll().size();
        userVendorMappingObjectMapping.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUserAidasObjectMappingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userVendorMappingObjectMapping))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUserAidasObjectMapping in the database
        List<UserVendorMappingObjectMapping> userVendorMappingObjectMappingList = userVendorMappingObjectMappingRepository.findAll();
        assertThat(userVendorMappingObjectMappingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUserAidasObjectMapping in Elasticsearch
        verify(mockUserObjectMappingSearchRepository, times(0)).save(userVendorMappingObjectMapping);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAidasUserAidasObjectMapping() throws Exception {
        int databaseSizeBeforeUpdate = userVendorMappingObjectMappingRepository.findAll().size();
        userVendorMappingObjectMapping.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUserAidasObjectMappingMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userVendorMappingObjectMapping))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasUserAidasObjectMapping in the database
        List<UserVendorMappingObjectMapping> userVendorMappingObjectMappingList = userVendorMappingObjectMappingRepository.findAll();
        assertThat(userVendorMappingObjectMappingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUserAidasObjectMapping in Elasticsearch
        verify(mockUserObjectMappingSearchRepository, times(0)).save(userVendorMappingObjectMapping);
    }

    @Test
    @Transactional
    void partialUpdateAidasUserAidasObjectMappingWithPatch() throws Exception {
        // Initialize the database
        userVendorMappingObjectMappingRepository.saveAndFlush(userVendorMappingObjectMapping);

        int databaseSizeBeforeUpdate = userVendorMappingObjectMappingRepository.findAll().size();

        // Update the aidasUserAidasObjectMapping using partial update
        UserVendorMappingObjectMapping partialUpdatedUserVendorMappingObjectMapping = new UserVendorMappingObjectMapping();
        partialUpdatedUserVendorMappingObjectMapping.setId(userVendorMappingObjectMapping.getId());

        partialUpdatedUserVendorMappingObjectMapping.dateAssigned(UPDATED_DATE_ASSIGNED);

        restAidasUserAidasObjectMappingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserVendorMappingObjectMapping.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserVendorMappingObjectMapping))
            )
            .andExpect(status().isOk());

        // Validate the AidasUserAidasObjectMapping in the database
        List<UserVendorMappingObjectMapping> userVendorMappingObjectMappingList = userVendorMappingObjectMappingRepository.findAll();
        assertThat(userVendorMappingObjectMappingList).hasSize(databaseSizeBeforeUpdate);
        UserVendorMappingObjectMapping testUserVendorMappingObjectMapping = userVendorMappingObjectMappingList.get(
            userVendorMappingObjectMappingList.size() - 1
        );
        assertThat(testUserVendorMappingObjectMapping.getDateAssigned()).isEqualTo(UPDATED_DATE_ASSIGNED);
        assertThat(testUserVendorMappingObjectMapping.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateAidasUserAidasObjectMappingWithPatch() throws Exception {
        // Initialize the database
        userVendorMappingObjectMappingRepository.saveAndFlush(userVendorMappingObjectMapping);

        int databaseSizeBeforeUpdate = userVendorMappingObjectMappingRepository.findAll().size();

        // Update the aidasUserAidasObjectMapping using partial update
        UserVendorMappingObjectMapping partialUpdatedUserVendorMappingObjectMapping = new UserVendorMappingObjectMapping();
        partialUpdatedUserVendorMappingObjectMapping.setId(userVendorMappingObjectMapping.getId());

        partialUpdatedUserVendorMappingObjectMapping.dateAssigned(UPDATED_DATE_ASSIGNED).status(UPDATED_STATUS);

        restAidasUserAidasObjectMappingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserVendorMappingObjectMapping.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserVendorMappingObjectMapping))
            )
            .andExpect(status().isOk());

        // Validate the AidasUserAidasObjectMapping in the database
        List<UserVendorMappingObjectMapping> userVendorMappingObjectMappingList = userVendorMappingObjectMappingRepository.findAll();
        assertThat(userVendorMappingObjectMappingList).hasSize(databaseSizeBeforeUpdate);
        UserVendorMappingObjectMapping testUserVendorMappingObjectMapping = userVendorMappingObjectMappingList.get(
            userVendorMappingObjectMappingList.size() - 1
        );
        assertThat(testUserVendorMappingObjectMapping.getDateAssigned()).isEqualTo(UPDATED_DATE_ASSIGNED);
        assertThat(testUserVendorMappingObjectMapping.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingAidasUserAidasObjectMapping() throws Exception {
        int databaseSizeBeforeUpdate = userVendorMappingObjectMappingRepository.findAll().size();
        userVendorMappingObjectMapping.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAidasUserAidasObjectMappingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userVendorMappingObjectMapping.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userVendorMappingObjectMapping))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUserAidasObjectMapping in the database
        List<UserVendorMappingObjectMapping> userVendorMappingObjectMappingList = userVendorMappingObjectMappingRepository.findAll();
        assertThat(userVendorMappingObjectMappingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUserAidasObjectMapping in Elasticsearch
        verify(mockUserObjectMappingSearchRepository, times(0)).save(userVendorMappingObjectMapping);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAidasUserAidasObjectMapping() throws Exception {
        int databaseSizeBeforeUpdate = userVendorMappingObjectMappingRepository.findAll().size();
        userVendorMappingObjectMapping.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUserAidasObjectMappingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userVendorMappingObjectMapping))
            )
            .andExpect(status().isBadRequest());

        // Validate the AidasUserAidasObjectMapping in the database
        List<UserVendorMappingObjectMapping> userVendorMappingObjectMappingList = userVendorMappingObjectMappingRepository.findAll();
        assertThat(userVendorMappingObjectMappingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUserAidasObjectMapping in Elasticsearch
        verify(mockUserObjectMappingSearchRepository, times(0)).save(userVendorMappingObjectMapping);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAidasUserAidasObjectMapping() throws Exception {
        int databaseSizeBeforeUpdate = userVendorMappingObjectMappingRepository.findAll().size();
        userVendorMappingObjectMapping.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAidasUserAidasObjectMappingMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userVendorMappingObjectMapping))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AidasUserAidasObjectMapping in the database
        List<UserVendorMappingObjectMapping> userVendorMappingObjectMappingList = userVendorMappingObjectMappingRepository.findAll();
        assertThat(userVendorMappingObjectMappingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AidasUserAidasObjectMapping in Elasticsearch
        verify(mockUserObjectMappingSearchRepository, times(0)).save(userVendorMappingObjectMapping);
    }

    @Test
    @Transactional
    void deleteAidasUserAidasObjectMapping() throws Exception {
        // Initialize the database
        userVendorMappingObjectMappingRepository.saveAndFlush(userVendorMappingObjectMapping);

        int databaseSizeBeforeDelete = userVendorMappingObjectMappingRepository.findAll().size();

        // Delete the aidasUserAidasObjectMapping
        restAidasUserAidasObjectMappingMockMvc
            .perform(delete(ENTITY_API_URL_ID, userVendorMappingObjectMapping.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserVendorMappingObjectMapping> userVendorMappingObjectMappingList = userVendorMappingObjectMappingRepository.findAll();
        assertThat(userVendorMappingObjectMappingList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AidasUserAidasObjectMapping in Elasticsearch
        verify(mockUserObjectMappingSearchRepository, times(1)).deleteById(userVendorMappingObjectMapping.getId());
    }

    @Test
    @Transactional
    void searchAidasUserAidasObjectMapping() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        userVendorMappingObjectMappingRepository.saveAndFlush(userVendorMappingObjectMapping);
        when(mockUserObjectMappingSearchRepository.search("id:" + userVendorMappingObjectMapping.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(userVendorMappingObjectMapping), PageRequest.of(0, 1), 1));

        // Search the aidasUserAidasObjectMapping
        restAidasUserAidasObjectMappingMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + userVendorMappingObjectMapping.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userVendorMappingObjectMapping.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateAssigned").value(hasItem(sameInstant(DEFAULT_DATE_ASSIGNED))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }
}
