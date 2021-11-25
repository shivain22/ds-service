package com.ainnotate.aidas.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ainnotate.aidas.domain.AidasAuthority;
import com.ainnotate.aidas.domain.AidasUser;
import com.ainnotate.aidas.repository.AidasUserRepository;
import com.ainnotate.aidas.repository.search.AidasUserSearchRepository;
import com.ainnotate.aidas.security.SecurityUtils;
import com.ainnotate.aidas.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ainnotate.aidas.domain.AidasUser}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AidasUserResource {

    private final Logger log = LoggerFactory.getLogger(AidasUserResource.class);

    private static final String ENTITY_NAME = "ainnotateserviceAidasUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AidasUserRepository aidasUserRepository;

    private final AidasUserSearchRepository aidasUserSearchRepository;

    public AidasUserResource(AidasUserRepository aidasUserRepository, AidasUserSearchRepository aidasUserSearchRepository) {
        this.aidasUserRepository = aidasUserRepository;
        this.aidasUserSearchRepository = aidasUserSearchRepository;
    }

    /**
     * {@code POST  /aidas-users} : Create a new aidasUser.
     *
     * @param aidasUser the aidasUser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aidasUser, or with status {@code 400 (Bad Request)} if the aidasUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aidas-users")
    public ResponseEntity<AidasUser> createAidasUser(@Valid @RequestBody AidasUser aidasUser) throws URISyntaxException {
        log.debug("REST request to save AidasUser : {}", aidasUser);
        if (aidasUser.getId() != null) {
            throw new BadRequestAlertException("A new aidasUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        addUserToKeyCloak(aidasUser);
        AidasUser result = aidasUserRepository.save(aidasUser);
        aidasUserSearchRepository.save(result);
        updateUserToKeyCloak(result);
        return ResponseEntity
            .created(new URI("/api/aidas-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /aidas-users/:id} : Updates an existing aidasUser.
     *
     * @param id the id of the aidasUser to save.
     * @param aidasUser the aidasUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasUser,
     * or with status {@code 400 (Bad Request)} if the aidasUser is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aidasUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aidas-users/{id}")
    public ResponseEntity<AidasUser> updateAidasUser(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AidasUser aidasUser
    ) throws URISyntaxException {
        log.debug("REST request to update AidasUser : {}, {}", id, aidasUser);
        if (aidasUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AidasUser result = aidasUserRepository.save(aidasUser);
        aidasUserSearchRepository.save(result);
        updateUserToKeyCloak(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasUser.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /aidas-users/:id} : Partial updates given fields of an existing aidasUser, field will ignore if it is null
     *
     * @param id the id of the aidasUser to save.
     * @param aidasUser the aidasUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aidasUser,
     * or with status {@code 400 (Bad Request)} if the aidasUser is not valid,
     * or with status {@code 404 (Not Found)} if the aidasUser is not found,
     * or with status {@code 500 (Internal Server Error)} if the aidasUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/aidas-users/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AidasUser> partialUpdateAidasUser(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AidasUser aidasUser
    ) throws URISyntaxException {
        log.debug("REST request to partial update AidasUser partially : {}, {}", id, aidasUser);
        if (aidasUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aidasUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aidasUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AidasUser> result = aidasUserRepository
            .findById(aidasUser.getId())
            .map(existingAidasUser -> {
                if (aidasUser.getFirstName() != null) {
                    existingAidasUser.setFirstName(aidasUser.getFirstName());
                }
                if (aidasUser.getLastName() != null) {
                    existingAidasUser.setLastName(aidasUser.getLastName());
                }
                if (aidasUser.getEmail() != null) {
                    existingAidasUser.setEmail(aidasUser.getEmail());
                }
                if (aidasUser.getLocked() != null) {
                    existingAidasUser.setLocked(aidasUser.getLocked());
                }
                if (aidasUser.getPassword() != null) {
                    existingAidasUser.setPassword(aidasUser.getPassword());
                }

                return existingAidasUser;
            })
            .map(aidasUserRepository::save)
            .map(savedAidasUser -> {
                aidasUserSearchRepository.save(savedAidasUser);

                return savedAidasUser;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aidasUser.getId().toString())
        );
    }

    /**
     * {@code GET  /aidas-users} : get all the aidasUsers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aidasUsers in body.
     */
    @GetMapping("/aidas-users")
    public ResponseEntity<List<AidasUser>> getAllAidasUsers(Pageable pageable) {
        log.debug("REST request to get a page of AidasUsers");
        Page<AidasUser> page = aidasUserRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aidas-users/:id} : get the "id" aidasUser.
     *
     * @param id the id of the aidasUser to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aidasUser, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aidas-users/{id}")
    public ResponseEntity<AidasUser> getAidasUser(@PathVariable Long id) {
        log.debug("REST request to get AidasUser : {}", id);
        Optional<AidasUser> aidasUser = aidasUserRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(aidasUser);
    }

    /**
     * {@code DELETE  /aidas-users/:id} : delete the "id" aidasUser.
     *
     * @param id the id of the aidasUser to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aidas-users/{id}")
    public ResponseEntity<Void> deleteAidasUser(@PathVariable Long id) {
        log.debug("REST request to delete AidasUser : {}", id);
        AidasUser  aidasUser = aidasUserRepository.getById(id);
        deleteUserFromKeyCloak(aidasUser);
        aidasUserRepository.deleteById(id);
        aidasUserSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/aidas-users?query=:query} : search for the aidasUser corresponding
     * to the query.
     *
     * @param query the query of the aidasUser search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/aidas-users")
    public ResponseEntity<List<AidasUser>> searchAidasUsers(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AidasUsers for query {}", query);
        Page<AidasUser> page = aidasUserSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    public void addUserToKeyCloak(AidasUser aidasUser) {
        String serverUrl = "https://auth.ainnotate.com/auth";
        String realm = "master";
        String clientId = "admin-cli";
        Keycloak keycloak = KeycloakBuilder.builder()
            .serverUrl(serverUrl)
            .realm(realm) //
            .grantType(OAuth2Constants.PASSWORD)
            .clientId(clientId)
            .username("admin")
            .password("admin")
            .build();
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(aidasUser.getEmail());
        user.setFirstName(aidasUser.getFirstName());
        user.setLastName(aidasUser.getLastName());
        user.setEmail(aidasUser.getEmail());
        aidasUser.setLogin(aidasUser.getEmail());
        aidasUser.setCreatedBy(SecurityUtils.getCurrentUserLogin().get());
        aidasUser.setCreatedDate(Instant.now());
        aidasUser.setLastModifiedBy(SecurityUtils.getCurrentUserLogin().get());
        aidasUser.setLastModifiedDate(Instant.now());

        List<String> groups = new ArrayList<>();
        groups.add("Users");
        user.setGroups(groups);

        RealmResource realmResource = keycloak.realm("jhipster");
        UsersResource usersRessource = realmResource.users();
        user.setEnabled(true);
        user.setEmailVerified(true);

        Response response = usersRessource.create(user);

        String userId = CreatedResponseUtil.getCreatedId(response);
        aidasUser.setKeycloakId(userId);
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(aidasUser.getPassword());
        UserResource userResource = usersRessource.get(userId);
        userResource.resetPassword(passwordCred);
        userResource.sendVerifyEmail();
        RoleRepresentation userRealmRole = realmResource.roles().get("ROLE_USER").toRepresentation();
        if(aidasUser.getAidasOrganisation()!=null){
            userRealmRole = realmResource.roles().get("ROLE_ORG_ADMIN").toRepresentation();
        }else if (aidasUser.getAidasCustomer()!=null){
            userRealmRole = realmResource.roles().get("ROLE_CUSTOMER_ADMIN").toRepresentation();
        }else if(aidasUser.getAidasVendor()!=null){
            userRealmRole = realmResource.roles().get("ROLE_VENDOR_ADMIN").toRepresentation();
        }
        userResource.roles().realmLevel().add(Arrays.asList(userRealmRole));
    }

    public void updateUserToKeyCloak(AidasUser aidasUser) {
        String serverUrl = "https://auth.ainnotate.com/auth";
        String realm = "master";
        String clientId = "admin-cli";
        Keycloak keycloak = KeycloakBuilder.builder()
            .serverUrl(serverUrl)
            .realm(realm) //
            .grantType(OAuth2Constants.PASSWORD)
            .clientId(clientId)
            .username("admin")
            .password("admin")
            .build();
        RealmResource realmResource = keycloak.realm("jhipster");
        UsersResource usersRessource = realmResource.users();
        UserResource userResource = usersRessource.get(aidasUser.getKeycloakId());
        UserRepresentation user = userResource.toRepresentation();

        Map<String,List<String>> userAttrs = new HashMap<>();
        List<String> userAttrsVals = new ArrayList<>();
        userAttrsVals.add(String.valueOf(aidasUser.getId()));
        userAttrs.put("aidas_id",userAttrsVals);
        user.setAttributes(userAttrs);

        user.setEnabled(true);
        user.setUsername(aidasUser.getEmail());
        user.setFirstName(aidasUser.getFirstName());
        user.setLastName(aidasUser.getLastName());
        user.setEmail(aidasUser.getEmail());
        userResource.update(user);
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(aidasUser.getPassword());
        userResource.resetPassword(passwordCred);
        userResource.roles().realmLevel().add(aidasUser.getAuthorities().stream().map(authority -> {return realmResource.roles().get(authority.getName()).toRepresentation();}).collect(Collectors.toList()));
    }

    private void deleteUserFromKeyCloak(AidasUser aidasUser){
        String serverUrl = "https://auth.ainnotate.com/auth";
        String realm = "master";
        String clientId = "admin-cli";
        Keycloak keycloak = KeycloakBuilder.builder()
            .serverUrl(serverUrl)
            .realm(realm) //
            .grantType(OAuth2Constants.PASSWORD)
            .clientId(clientId)
            .username("admin")
            .password("admin")
            .build();
        RealmResource realmResource = keycloak.realm("jhipster");
        UsersResource usersRessource = realmResource.users();
        usersRessource.delete(aidasUser.getKeycloakId());
    }
}
